package Model;

import Controller.GameController;
import Model.entities.*;
import Model.utilz.NpcLoader;
import Model.utilz.SoundPlayer;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

    public enum GameMode { CAMPAIGN, ENDLESS }
    private GameMode gameMode = GameMode.CAMPAIGN;
    public GameMode getGameMode() { return gameMode; }
    public void setGameMode(GameMode mode) { this.gameMode = mode; }

    private Player1 player1;
    private Wall[] walls;
    private Enemy[] enemy;
    private Player[] players;
    private Entity[] entities;
    private boolean gameOver = false;
    private CollisionChecker collisionChecker;
    /** Which trigger (npc_id) the player was in last update; null if none. Used to open encounter only on first step in. */
    /** NPCs whose soul has been collected; their door triggers no longer open the encounter. */
    private final Set<String> soulsCollected = new HashSet<>();
    /** When true, overworld is paused and view shows first-person encounter frame (ESC to close). */
    private boolean showWidowFrame = false;
    /** Which NPC the current encounter is with (npc_id from Tiled). */
    private String currentNpcId = null;
    private EncounterState encounterState;
    /** Souls collected this assignment (resets each assignment in Endless). */
    private int souls = 0;
    /** Total souls collected across all assignments (for Endless scoring). */
    private int totalSouls = 0;

    /** Campaign: 7 souls to win. */
    private static final int ASSIGNMENT_QUOTA = 7;
    /** Endless: first assignment quota, then doubles each time (1 → 2 → 4 → 8...). */
    private static final int ENDLESS_START_QUOTA = 1;
    /** Current soul quota for this assignment. */
    private int currentQuota = ASSIGNMENT_QUOTA;
    /**
     * Town-wide suspicion 0–100. At 100 the residents report you and you're pulled from the assignment.
     * No timer — pressure comes from your choices and rejection rate.
     */
    private float suspicion = 0f;
    /** +15 % per deal accepted: every soul costs you, even a win draws attention. */
    private static final float SUSPICION_PER_DEAL = 15f;
    /** +14 % when a door is slammed: the NPC felt something was wrong. */
    private static final float SUSPICION_PER_SLAM = 14f;
    /** Extra +10 % when the NPC reports you to the Investigator (rolled per their tattleChance). */
    private static final float SUSPICION_PER_TATTLE = 10f;
    /** Passive creep: +1 % every 2 minutes (120 s) of overworld time. At 120 UPS = 1/(120*120) per tick. */
    private static final float SUSPICION_CREEP_PER_TICK = 1f / (120f * 120f);

    /** Brief message shown on overworld after encounter ends (e.g. "Soul collected!"). Cleared after display. */
    private String lastEncounterMessage = null;
    private long lastEncounterMessageUntil = 0;
    /** When 1 = deal accepted, 2 = door slammed. Encounter stays open until player presses ENTER/ESC. */
    private int pendingEncounterOutcome = 0;
    /** Last few dialogue lines per NPC, kept across encounters so Claude remembers context on revisit. */
    private static final int MEMORY_LINES = 6;
    private final Map<String, List<String>> npcMemory = new HashMap<>();
    private int time = 600;
    private int timer = 0;
    private int playerWinner = 1;

    ArrayList<Medkit> activeMedkits = new ArrayList<>();
    private boolean placedMedkit = false;
    private int[][] world;
    private LevelLoader levelLoader;
    private int yLvlOffset = -200;
    private int lvlMovingTick;
    Medkit medkit;

    /** Firefly (and future VFX) particles. Updated when in overworld; drawn by GameView. */
    private final List<Particle> particles = new ArrayList<>();
    /** One tick counter per Tiled vfx spawn point — each point spawns on its own schedule. */
    private int[] fireflySpawnTickPerPoint;

    /** When the player walks over a plant decoration, it rustles. Key = "x,y", value = rustle end time (ms). */
    private final Map<String, Long> decorationRustleEndTimes = new HashMap<>();
    /** Decorations the player was overlapping last frame — rustle only when stepping onto one, not while standing. */
    private final Set<String> playerOverlappingDecorationKeys = new HashSet<>();
    private static final long DECORATION_RUSTLE_DURATION_MS = 450;

    /** Firefly lifetime in seconds — tweak these to change how long they last before fading out. */
    private static final float FIREFLY_LIFETIME_MIN_SEC = 4f;
    private static final float FIREFLY_LIFETIME_MAX_SEC = 7f;
    /** How many fireflies to create per vfx spawn point at init. Lower = less dense. */
    private static final int FIREFLY_INITIAL_PER_SPAWN = 1;
    /** Ticks between adding one new firefly at each vfx spawn point (per point, not global). Higher = less dense. */
    private static final int FIREFLY_SPAWN_INTERVAL_TICKS = 120;
    /** If true, interval is multiplied by number of points so total spawn rate stays similar to a single point. */
    private static final boolean FIREFLY_SPAWN_INTERVAL_SCALE_WITH_POINT_COUNT = true;
    /** Max random offset (world px) from spawn center when placing a firefly. Higher = more spread. */
    private static final float FIREFLY_SPREAD_PX = 120f;
    /** Chance (0–1) to use small firefly (row 0). Rest use large firefly (row 1). Higher = more small ones. */
    private static final float FIREFLY_SMALL_CHANCE = 0.85f;

    /** Spawn tile (col, row) when player leaves the map; teleported back here with a Gary message. */
    private static final int SPAWN_TILE_X = 41;
    private static final int SPAWN_TILE_Y = 43;
    /** How far past the level edge (px) the player can go before being teleported back. Gives a bit of leeway. */
    private static final int LEVEL_BOUNDS_MARGIN = 200;

    public Game() {
        levelLoader = new LevelLoader();
        world = levelLoader.getWorld();
        player1 = new Player1(SPAWN_TILE_X * GameController.TILE_SIZE, SPAWN_TILE_Y * GameController.TILE_SIZE, 6 * GameController.SCALE, 8 * GameController.SCALE, this);
        players = new Player[]{player1};
        walls = new Wall[0];  // Level collision from Tiled solid layer (tile map), not Wall entities
        enemy = new Enemy[0];

        int sizeOfEntities = enemy.length + walls.length + players.length;
        entities = new Entity[sizeOfEntities];
        Entity[] allEntities = new Entity[sizeOfEntities];
        System.arraycopy(walls, 0, allEntities, 0, walls.length);
        System.arraycopy(enemy, 0, allEntities, walls.length, enemy.length);
        System.arraycopy(players, 0, allEntities, walls.length + enemy.length, players.length);
        System.arraycopy(allEntities, 0, entities, 0, sizeOfEntities);

        collisionChecker = new CollisionChecker();
        encounterState = new EncounterState(this);
        initFireflyParticles();
    }

    private Particle spawnFirefly(float cx, float cy) {
        Particle p = new Particle();
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        float ox = cx + (rng.nextFloat() - 0.5f) * FIREFLY_SPREAD_PX * 2f;
        float oy = cy + (rng.nextFloat() - 0.5f) * FIREFLY_SPREAD_PX * 2f;
        p.origX = ox;
        p.origY = oy;
        p.spawnX = ox;
        p.spawnY = oy;
        p.x = ox;
        p.y = oy;
        p.phase = rng.nextFloat() * 6.2832f;
        p.glowPhase = rng.nextFloat() * 6.2832f;
        p.aniOffset = (long) (rng.nextFloat() * 600f);
        // Slow upward drift with random sideways lean
        p.driftVy = -(FIREFLY_RISE_SPEED_MIN + rng.nextFloat() * (FIREFLY_RISE_SPEED_MAX - FIREFLY_RISE_SPEED_MIN));
        p.driftVx = (rng.nextFloat() - 0.5f) * FIREFLY_SIDEWAYS_RANGE;
        p.maxLife = FIREFLY_LIFETIME_MIN_SEC + rng.nextFloat() * (FIREFLY_LIFETIME_MAX_SEC - FIREFLY_LIFETIME_MIN_SEC);
        p.life = p.maxLife;
        p.row = rng.nextFloat() < FIREFLY_SMALL_CHANCE ? 0 : 1;
        p.dead = false;
        return p;
    }

    /** Re-randomise a particle's drift direction, keeping it at its current anchor position. */
    private void resetDrift(Particle p) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        p.spawnX = p.origX;
        p.spawnY = p.origY;
        p.driftVy = -(FIREFLY_RISE_SPEED_MIN + rng.nextFloat() * (FIREFLY_RISE_SPEED_MAX - FIREFLY_RISE_SPEED_MIN));
        p.driftVx = (rng.nextFloat() - 0.5f) * FIREFLY_SIDEWAYS_RANGE;
        p.phase = rng.nextFloat() * 6.2832f;
    }

    private void initFireflyParticles() {
        particles.clear();
        List<float[]> spawns = levelLoader.getFireflySpawns();
        for (float[] pt : spawns) {
            for (int i = 0; i < FIREFLY_INITIAL_PER_SPAWN; i++) {
                particles.add(spawnFirefly(pt[0], pt[1]));
            }
        }
        initFireflySpawnCounters(spawns);
    }

    /** (Re)build per-point spawn counters; stagger so all points don't spawn the same frame. */
    private void initFireflySpawnCounters(List<float[]> spawns) {
        int n = spawns.size();
        if (n == 0) {
            fireflySpawnTickPerPoint = null;
            return;
        }
        fireflySpawnTickPerPoint = new int[n];
        int interval = effectiveFireflySpawnIntervalTicks(n);
        for (int i = 0; i < n; i++)
            fireflySpawnTickPerPoint[i] = (i * interval / Math.max(1, n)) % Math.max(1, interval);
    }

    private int effectiveFireflySpawnIntervalTicks(int numPoints) {
        if (numPoints <= 0) return FIREFLY_SPAWN_INTERVAL_TICKS;
        if (FIREFLY_SPAWN_INTERVAL_SCALE_WITH_POINT_COUNT)
            return FIREFLY_SPAWN_INTERVAL_TICKS * numPoints;
        return FIREFLY_SPAWN_INTERVAL_TICKS;
    }

    /** Sine-wave wobble radius (world px) layered on top of the upward drift. */
    private static final float FIREFLY_DRIFT_RADIUS = 14f;
    /** How fast fireflies float upward, in world pixels per second. */
    private static final float FIREFLY_RISE_SPEED_MIN = 8f;
    private static final float FIREFLY_RISE_SPEED_MAX = 18f;
    /** Max random sideways drift speed (world px/sec, applied symmetrically left/right). */
    private static final float FIREFLY_SIDEWAYS_RANGE = 10f;
    /** After rising this many world pixels, the firefly loops back to its origin. */
    private static final float FIREFLY_MAX_RISE = 80f;

    /** Call each tick when in overworld. Updates particles and occasionally spawns one at a vfx spawn. */
    void updateParticles() {
        float dt = 1f / 120f;
        float t = System.currentTimeMillis() / 1000f;
        for (Particle p : particles) {
            // Slowly move the anchor upward (and slightly sideways)
            p.spawnX += p.driftVx * dt;
            p.spawnY += p.driftVy * dt;
            // Sine-wave wobble layered on top of the drift
            p.x = p.spawnX + (float) Math.sin(t * 0.8f + p.phase) * FIREFLY_DRIFT_RADIUS;
            p.y = p.spawnY + (float) Math.cos(t * 0.5f + p.phase * 1.3f) * FIREFLY_DRIFT_RADIUS * 0.6f;
            // Once it has risen far enough, loop back to origin with a fresh drift angle
            float risen = p.origY - p.spawnY;
            if (risen > FIREFLY_MAX_RISE) resetDrift(p);
            p.life -= dt;
            if (p.life <= 0) p.dead = true;
        }
        particles.removeIf(p -> p.dead);
        List<float[]> spawns = levelLoader.getFireflySpawns();
        if (spawns.isEmpty()) return;
        if (fireflySpawnTickPerPoint == null || fireflySpawnTickPerPoint.length != spawns.size())
            initFireflySpawnCounters(spawns);
        int n = spawns.size();
        int interval = effectiveFireflySpawnIntervalTicks(n);
        for (int i = 0; i < n; i++) {
            fireflySpawnTickPerPoint[i]++;
            if (fireflySpawnTickPerPoint[i] >= interval) {
                fireflySpawnTickPerPoint[i] = 0;
                float[] pt = spawns.get(i);
                particles.add(spawnFirefly(pt[0], pt[1]));
            }
        }
    }

    /** Consecutive door slams (reset when a deal is accepted). Used for Gary messages. */
    private int consecutiveSlams = 0;

    /** Called when the NPC accepts the deal (soul sold). */
    public void onDealAccepted() {
        consecutiveSlams = 0;
        souls++;
        totalSouls++;
        if (currentNpcId != null) soulsCollected.add(currentNpcId);
        addSuspicion(SUSPICION_PER_DEAL);
        showEncounterMessage("Deal signed!");
        if (souls == 1) showGaryMessage("Great start! See? Not so hard. I always believed in you. — Gary");
        else if (souls == (currentQuota + 1) / 2) showGaryMessage("Nice momentum. Keep this up and I will put in a good word with the Director. Of Darkness. — Gary");
    }

    /** Called when the NPC slams the door / ends the conversation. May add tattle suspicion by personality. */
    public void onEncounterSlammed() {
        consecutiveSlams++;
        addSuspicion(SUSPICION_PER_SLAM);
        NpcProfile npc = currentNpcId != null ? NpcLoader.getById(currentNpcId) : null;
        boolean tattled = npc != null && npc.getTattleChance() > 0
            && (int)(Math.random() * 100) < npc.getTattleChance();
        if (tattled) {
            addSuspicion(SUSPICION_PER_TATTLE);
            showEncounterMessage("They slammed the door. They reported you to the Investigator.");
        } else {
            showEncounterMessage("They slammed the door.");
        }
        if (consecutiveSlams == 1) showGaryMessage("Hey so I saw a rejection on your activity log. Totally normal. Happens to everyone. Once. — Gary");
        else if (consecutiveSlams == 2) showGaryMessage("Two rejections in a row is a little concerning. Maybe try leading with the wellness package? — Gary");
        else if (consecutiveSlams == 3) showGaryMessage("Okay I am going to need you to really dig deep here. What is your pitch? Can I hear your pitch? — Gary");
        else if (consecutiveSlams >= 5) showGaryMessage("I hate to do this but HR is asking questions about your close rate. Hell Resources. — Gary");
    }

    /** Add to town suspicion (capped at 100). Blocked in god mode. */
    public void addSuspicion(float amount) {
        if (godMode) return;
        suspicion = Math.min(100f, suspicion + amount);
    }

    public float getSuspicion() { return suspicion; }
    public int getSoulQuota() { return currentQuota; }
    public int getTotalSouls() { return totalSouls; }

    private void showEncounterMessage(String msg) {
        lastEncounterMessage = msg;
        lastEncounterMessageUntil = System.currentTimeMillis() + 3500;
    }

    public int getSouls() { return souls; }

    // ---- Dev cheats (F7 menu in KeyboardInputs) ----

    private boolean godMode = false;
    public boolean isGodMode() { return godMode; }

    /** Toggle god mode: suspicion freezes, can't lose. */
    public void cheatToggleGodMode() {
        godMode = !godMode;
        showEncounterMessage(godMode ? "[CHEAT] God mode ON" : "[CHEAT] God mode OFF");
    }

    /** +1 soul toward quota (no NPC needed). */
    public void cheatAddSoul() {
        souls++;
        totalSouls++;
        showEncounterMessage("[CHEAT] Soul added (" + souls + "/" + currentQuota + ")");
    }

    /** Suspicion back to 0. */
    public void cheatClearSuspicion() {
        suspicion = 0f;
        showEncounterMessage("[CHEAT] Suspicion cleared");
    }

    /** Fill current quota — Campaign: win. Endless: Assignment Complete transition. */
    public void cheatInstantWin() {
        souls = currentQuota;
        showEncounterMessage("[CHEAT] Quota filled!");
    }

    /** Reset all NPC memory so every encounter is fresh. */
    public void cheatClearNpcMemory() {
        npcMemory.clear();
        soulsCollected.clear();
        showEncounterMessage("[CHEAT] NPC memory + souls reset");
    }

    /** Halve current suspicion. */
    public void cheatHalveSuspicion() {
        suspicion = Math.max(0f, suspicion / 2f);
        showEncounterMessage("[CHEAT] Suspicion halved → " + (int) suspicion + "%");
    }

    public String getLastEncounterMessage() { return lastEncounterMessage; }
    public boolean isLastEncounterMessageVisible() { return lastEncounterMessage != null && System.currentTimeMillis() < lastEncounterMessageUntil; }
    public void clearLastEncounterMessage() { lastEncounterMessage = null; }

    /** Gary phone popup: message text and when to auto-hide (ms). Dismissible with E/ENTER. */
    private String garyMessage = null;
    private long garyMessageUntil = 0;
    private static final long GARY_MESSAGE_DURATION_MS = 8000;
    /** Last message from Gary (or default); shown when player opens the phone. Persists so phone always has something to read. */
    private String lastGaryMessage = "Your Q3 soul acquisition numbers are disappointing. We expect better. — Gary";
    /** True when the player has opened the phone to view messages (e.g. pressed P). */
    private boolean phoneOpen = false;

    private void showGaryMessage(String msg) {
        garyMessage = msg;
        lastGaryMessage = msg;
        garyMessageUntil = System.currentTimeMillis() + GARY_MESSAGE_DURATION_MS;
    }

    public String getGaryMessage() { return garyMessage; }
    public boolean isGaryMessageVisible() { return garyMessage != null && System.currentTimeMillis() < garyMessageUntil; }
    public void dismissGaryMessage() { garyMessage = null; }
    /** Message to show on the phone: current popup if active, otherwise last message (or default). */
    public String getMessageToShowOnPhone() { return garyMessage != null ? garyMessage : lastGaryMessage; }
    /** Phone is visible because Gary just sent a message (popup) or because the player opened it. */
    public boolean isPhoneVisible() { return phoneOpen || isGaryMessageVisible(); }
    public void openPhone() { phoneOpen = true; }
    public void closePhone() { phoneOpen = false; }
    public boolean isPhoneOpen() { return phoneOpen; }

    /**
     * Saves the last MEMORY_LINES dialogue lines for the current NPC so they can be
     * injected as context on the next visit.  Safe to call even if lines is empty.
     */
    public void saveCurrentNpcMemory() {
        if (currentNpcId == null) return;
        List<String> all = encounterState.getLines();
        if (all.isEmpty()) return;
        int from = Math.max(0, all.size() - MEMORY_LINES);
        npcMemory.put(currentNpcId, new ArrayList<>(all.subList(from, all.size())));
    }

    /** Returns saved dialogue lines for the given NPC, or an empty list if none. */
    public List<String> getNpcMemory(String npcId) {
        return npcMemory.getOrDefault(npcId, List.of());
    }

    public boolean hasPendingEncounterOutcome() { return pendingEncounterOutcome != 0; }
    public void setPendingEncounterOutcome(boolean dealAccepted, boolean slamDoor) {
        if (dealAccepted) pendingEncounterOutcome = 1;
        else if (slamDoor) pendingEncounterOutcome = 2;
    }
    /** Called when player presses ENTER/ESC after seeing the final reply. Applies outcome and closes encounter. */
    public void confirmEncounterClose() {
        saveCurrentNpcMemory();
        if (pendingEncounterOutcome == 1) onDealAccepted();
        else if (pendingEncounterOutcome == 2) onEncounterSlammed();
        pendingEncounterOutcome = 0;
        setShowWidowFrame(false);
        currentNpcId = null;
    }

    public void update() {
        if (gameOver) return;
        if (lastEncounterMessage != null && System.currentTimeMillis() >= lastEncounterMessageUntil)
            lastEncounterMessage = null;
        if (garyMessage != null && System.currentTimeMillis() >= garyMessageUntil)
            garyMessage = null;
        checkTriggers();
        if (pendingKnockNpcId != null && System.currentTimeMillis() >= pendingKnockUntil) {
            saveCurrentNpcMemory();
            currentNpcId = pendingKnockNpcId;
            pendingKnockNpcId = null;
            showWidowFrame = true;
            encounterState.clearConversation();
            encounterState.requestOpeningLine();
            SoundPlayer.stopNightAmbience();
            SoundPlayer.startEncounterMusic();
        }
        if (!showWidowFrame) {
            entitiesUpdates();
            checkPlayerBoundsAndTeleport();
            checkReadables();
            if (showingReadable != null && System.currentTimeMillis() >= showingReadableUntil)
                closeReadable();
            checkDecorationRustle();
            updateParticles();
            // Passive suspicion creep — only ticks while in the overworld, not during encounters
            addSuspicion(SUSPICION_CREEP_PER_TICK);
            checkWinLose();
        }
    }

    /** If the player has left the level bounds (including margin), teleport back to spawn (tile 41,43) and show a Gary message. */
    private void checkPlayerBoundsAndTeleport() {
        int levelW = levelLoader.getLevelWidthPixels();
        int levelH = levelLoader.getLevelHeightPixels();
        int m = LEVEL_BOUNDS_MARGIN;
        Rectangle2D.Float h = player1.getHitBox();
        float x = h.x;
        float y = h.y;
        float w = h.width;
        float hH = h.height;
        if (x < -m || y < -m || x + w > levelW + m || y + hH > levelH + m) {
            int spawnX = SPAWN_TILE_X * GameController.TILE_SIZE;
            int spawnY = SPAWN_TILE_Y * GameController.TILE_SIZE;
            player1.setXHitBox(spawnX);
            player1.setYHitBox(spawnY);
            showGaryMessage("Stay in the assignment area. We do not reimburse for unauthorized travel. — Gary");
        }
    }

    /** When the player steps onto a plant (not already standing on it), mark it rustling. Trigger area is only the base tile (footprint), not the full 2-tile sprite. */
    private void checkDecorationRustle() {
        Rectangle playerBox = player1.getHitBox().getBounds();
        int tileSize = GameController.TILE_SIZE;
        Set<String> currentlyOverlapping = new HashSet<>();
        for (LevelLoader.Decoration dec : levelLoader.getDecorations()) {
            Rectangle plantBase = new Rectangle(dec.x, dec.y, tileSize, tileSize);
            if (playerBox.intersects(plantBase)) {
                String key = dec.x + "," + dec.y;
                currentlyOverlapping.add(key);
                if (!playerOverlappingDecorationKeys.contains(key)) {
                    decorationRustleEndTimes.put(key, System.currentTimeMillis() + DECORATION_RUSTLE_DURATION_MS);
                    SoundPlayer.playRustle();
                }
            }
        }
        playerOverlappingDecorationKeys.clear();
        playerOverlappingDecorationKeys.addAll(currentlyOverlapping);
        decorationRustleEndTimes.entrySet().removeIf(e -> System.currentTimeMillis() >= e.getValue());
    }

    /** Horizontal pixel offset for drawing a rustling plant (wobble). Returns 0 if not rustling. */
    public int getDecorationRustleOffsetX(int decX, int decY) {
        String key = decX + "," + decY;
        Long end = decorationRustleEndTimes.get(key);
        if (end == null || System.currentTimeMillis() >= end) return 0;
        return (int) (4 * Math.sin(System.currentTimeMillis() / 55.0));
    }

    /** True when quota was just met — controller reads this to transition to DAY_SUMMARY. */
    private boolean quotaJustMet = false;
    public boolean isQuotaJustMet() { return quotaJustMet; }
    public void clearQuotaJustMet() { quotaJustMet = false; }

    private void checkWinLose() {
        if (suspicion >= 100f) {
            gameOver = true;
            playerWinner = 0;
            return;
        }
        if (souls >= currentQuota && !quotaJustMet) {
            if (gameMode == GameMode.CAMPAIGN) {
                gameOver = true;
                playerWinner = 1;
            } else {
                quotaJustMet = true;  // Endless: show Assignment Complete, then next assignment
            }
        }
    }

    /** Called from DaySummary when the player presses ENTER (Endless only). Resets for next assignment. */
    public void advanceAfterQuota() {
        quotaJustMet = false;
        souls = 0;
        soulsCollected.clear();
        currentQuota *= 2;
        suspicion = Math.max(0f, suspicion - 10f);
    }

    /** Next assignment's quota (Endless), for summary screen. */
    public int getNextAssignmentQuota() {
        return currentQuota * 2;
    }

    public boolean isShowWidowFrame() { return showWidowFrame; }
    public void setShowWidowFrame(boolean show) {
        this.showWidowFrame = show;
        if (!show) {
            SoundPlayer.stopEncounterMusic();
            SoundPlayer.startNightAmbience();
        }
    }
    public EncounterState getEncounterState() { return encounterState; }
    public String getCurrentNpcId() { return currentNpcId; }
    /** Profile for the NPC we're currently in encounter with (from res/npcs.json). */
    public NpcProfile getCurrentNpcProfile() { return currentNpcId != null ? NpcLoader.getById(currentNpcId) : null; }

    /** Which door trigger (npc_id) the player is currently standing in; null if none. Updated each tick. */
    private String playerInTriggerNpcId = null;
    /** After knock key: npc_id to open when delay elapses; null if no pending knock. */
    private String pendingKnockNpcId = null;
    private long pendingKnockUntil = 0;
    /** Delay in ms between knock sound and opening the encounter. */
    private static final long KNOCK_DELAY_MS = 450;

    /** Updates which door trigger the player is in. Does not open the encounter — use tryKnockOnDoor() when player presses E. */
    private void checkTriggers() {
        var hitBox = player1.getHitBox();
        String triggerNow = null;
        for (Trigger t : levelLoader.getTriggers()) {
            if (t.intersects(hitBox)) {
                String npcId = t.getNpcId();
                if (npcId != null && !soulsCollected.contains(npcId) && NpcLoader.getById(npcId) != null) {
                    triggerNow = npcId;
                    break;
                }
            }
        }
        playerInTriggerNpcId = triggerNow;
    }

    /** Call when the player presses the knock key (e.g. E). Plays knock sound and opens the encounter after a short delay. */
    public void tryKnockOnDoor() {
        if (showWidowFrame) return;
        if (playerInTriggerNpcId == null) return;
        if (pendingKnockNpcId != null) return; // already knocking
        SoundPlayer.playKnock();
        pendingKnockNpcId = playerInTriggerNpcId;
        pendingKnockUntil = System.currentTimeMillis() + KNOCK_DELAY_MS;
    }

    /** The npc_id of the door the player is currently standing at (for "Press E to knock" hint); null if not at a door or already knocking. */
    public String getDoorTriggerNpcId() { return pendingKnockNpcId != null ? null : playerInTriggerNpcId; }

    /** Readable zone the player is currently in; null if none. Updated each tick. */
    private Readable playerInReadableZone = null;
    /** Readable currently being shown (E opened it); null when dialog is closed. */
    private Readable showingReadable = null;
    /** When to auto-close the readable (ms). */
    private long showingReadableUntil = 0;
    /** How long readable text stays on screen (ms) before auto-dismissing. */
    private static final long READABLE_DURATION_MS = 4000;

    private void checkReadables() {
        var hitBox = player1.getHitBox();
        Readable in = null;
        for (Readable r : levelLoader.getReadables()) {
            if (r.intersects(hitBox)) {
                in = r;
                break;
            }
        }
        playerInReadableZone = in;
    }

    /** The readable zone the player is in (for "Press E to read" hint). Null if not in one or a read dialog is already open. */
    public Readable getCurrentReadableZone() { return showingReadable != null ? null : playerInReadableZone; }
    /** The readable whose text is currently shown; null if none. */
    public Readable getShowingReadable() { return showingReadable; }
    /** Open the read dialog for the zone the player is in. No-op if not in a zone. Auto-closes after READABLE_DURATION_MS. */
    public void tryRead() {
        if (showWidowFrame) return;
        if (playerInReadableZone != null) {
            showingReadable = playerInReadableZone;
            showingReadableUntil = System.currentTimeMillis() + READABLE_DURATION_MS;
        }
    }
    /** Close the read dialog. */
    public void closeReadable() { showingReadable = null; }

    private void entitiesUpdates() {
        player1.update();
        for (int i = 0; i < enemy.length; i++)
            enemy[i].update();
        for (int i = 0; i < walls.length; i++)
            walls[i].update();
    }


    public LevelLoader getLevelLoader() { return levelLoader; }
    public List<Particle> getParticles() { return particles; }
    public Wall[] getWalls() { return walls; }

    public Entity[] getEntities() {
        return entities;
    }

    public Player1 getPlayer1() {
        return player1;
    }
    public CollisionChecker getCollisionChecker() {
        return collisionChecker;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time -= time;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    /** Reset contract state for a new run (souls, suspicion, time, NPC memory). Call when leaving GAMEOVER to play again. */
    public void resetForNewGame() {
        gameOver = false;
        playerWinner = 1;
        souls = 0;
        totalSouls = 0;
        soulsCollected.clear();
        suspicion = 0f;
        npcMemory.clear();
        encounterState.clearConversation();
        lastEncounterMessage = null;
        garyMessage = null;
        phoneOpen = false;
        lastGaryMessage = "Your Q3 soul acquisition numbers are disappointing. We expect better. — Gary";
        pendingEncounterOutcome = 0;
        currentNpcId = null;
        playerInTriggerNpcId = null;
        pendingKnockNpcId = null;
        currentQuota = (gameMode == GameMode.ENDLESS) ? ENDLESS_START_QUOTA : ASSIGNMENT_QUOTA;
        quotaJustMet = false;
        setShowWidowFrame(false);
        initFireflyParticles();
    }

    public int getPlayerWinner() {
        return playerWinner;
    }

}
