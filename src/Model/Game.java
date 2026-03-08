package Model;

import Controller.GameController;
import Model.entities.*;
import Model.utilz.NpcLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Game {
    private Player1 player1;
    private Wall[] walls;
    private Enemy[] enemy;
    private Player[] players;
    private Entity[] entities;
    private boolean gameOver = false;
    private CollisionChecker collisionChecker;
    /** Which trigger (npc_id) the player was in last update; null if none. Used to open encounter only on first step in. */
    private String wasInTriggerNpcId = null;
    /** NPCs whose soul has been collected; their door triggers no longer open the encounter. */
    private final Set<String> soulsCollected = new HashSet<>();
    /** When true, overworld is paused and view shows first-person encounter frame (ESC to close). */
    private boolean showWidowFrame = false;
    /** Which NPC the current encounter is with (npc_id from Tiled). */
    private String currentNpcId = null;
    private EncounterState encounterState;
    /** Souls collected from deals. */
    private int souls = 0;
    /** Soul quota — reach this to win. */
    private static final int SOUL_QUOTA = 3;
    /**
     * Town-wide suspicion 0–100.  At 100 Father Creed performs the banishment ritual — game over.
     * There is no timer; suspicion is the only pressure.
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

    public Game() {
        levelLoader = new LevelLoader();
        world = levelLoader.getWorld();
        player1 = new Player1(50 * GameController.TILE_SIZE, 40 * GameController.TILE_SIZE, 6 * GameController.SCALE, 8 * GameController.SCALE, .16f * GameController.SCALE, this);
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
    }

    /** Called when the NPC accepts the deal (soul sold). */
    public void onDealAccepted() {
        souls++;
        if (currentNpcId != null) soulsCollected.add(currentNpcId);
        addSuspicion(SUSPICION_PER_DEAL);
        showEncounterMessage("Soul collected!");
    }

    /** Called when the NPC slams the door / ends the conversation. May add tattle suspicion by personality. */
    public void onEncounterSlammed() {
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
    }

    /** Add to town suspicion (capped at 100). */
    public void addSuspicion(float amount) {
        suspicion = Math.min(100f, suspicion + amount);
    }

    public float getSuspicion() { return suspicion; }
    public int getSoulQuota() { return SOUL_QUOTA; }

    private void showEncounterMessage(String msg) {
        lastEncounterMessage = msg;
        lastEncounterMessageUntil = System.currentTimeMillis() + 3500;
    }

    public int getSouls() { return souls; }
    public String getLastEncounterMessage() { return lastEncounterMessage; }
    public boolean isLastEncounterMessageVisible() { return lastEncounterMessage != null && System.currentTimeMillis() < lastEncounterMessageUntil; }
    public void clearLastEncounterMessage() { lastEncounterMessage = null; }

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
        checkTriggers();
        if (!showWidowFrame) {
            entitiesUpdates();
            // Passive suspicion creep — only ticks while in the overworld, not during encounters
            addSuspicion(SUSPICION_CREEP_PER_TICK);
            checkWinLose();
        }
    }

    private void checkWinLose() {
        if (souls >= SOUL_QUOTA) {
            gameOver = true;
            playerWinner = 1;
            return;
        }
        if (suspicion >= 100f) {
            gameOver = true;
            playerWinner = 0;
        }
    }

    public boolean isShowWidowFrame() { return showWidowFrame; }
    public void setShowWidowFrame(boolean show) { this.showWidowFrame = show; }
    public EncounterState getEncounterState() { return encounterState; }
    public String getCurrentNpcId() { return currentNpcId; }
    /** Profile for the NPC we're currently in encounter with (from res/npcs.json). */
    public NpcProfile getCurrentNpcProfile() { return currentNpcId != null ? NpcLoader.getById(currentNpcId) : null; }

    /** When the player enters a door trigger (npc_id in Tiled), open that NPC's encounter if we don't have their soul yet. */
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
        if (triggerNow != null && wasInTriggerNpcId == null) {
            saveCurrentNpcMemory();   // capture lines from any previous encounter before clearing
            currentNpcId = triggerNow;
            showWidowFrame = true;
            encounterState.clearConversation();
        }
        wasInTriggerNpcId = triggerNow;
    }

    private void entitiesUpdates() {
        player1.update();
        for (int i = 0; i < enemy.length; i++)
            enemy[i].update();
        for (int i = 0; i < walls.length; i++)
            walls[i].update();
    }


    public LevelLoader getLevelLoader() { return levelLoader; }
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
        soulsCollected.clear();
        suspicion = 0f;
        npcMemory.clear();
        encounterState.clearConversation();
        lastEncounterMessage = null;
        pendingEncounterOutcome = 0;
        currentNpcId = null;
        wasInTriggerNpcId = null;
        setShowWidowFrame(false);
    }

    public int getPlayerWinner() {
        return playerWinner;
    }

}
