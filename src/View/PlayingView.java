package View;

import Controller.GameController;
import Model.EncounterState;
import Model.Game;
import Model.LevelLoader;
import Model.Light;
import Model.NpcProfile;
import Model.gamestates.Playing;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renders the Playing state: full-screen game world and Player1 ability UI.
 * When an NPC encounter is active, draws that NPC's first-person frame (from res/npcs.json) over the overworld.
 * Night is a separate bluish texture with radial light gradients from Tiled "lights" layer, layered with multiply blend.
 */
public class PlayingView {
    private final GameView gameView = new GameView();
    /** GTA-style encounter: door loaded once; portraits per NPC (key = image path), fallback widow. */
    private BufferedImage doorFrameImage;
    private BufferedImage fallbackPortraitImage;
    private final Map<String, BufferedImage> portraitCache = new HashMap<>();

    /** Shown above the player when they can knock on a door. */
    private BufferedImage exclamationMarkImage;
    /** Night overlay texture in world space; built at downscaled resolution for performance. */
    private BufferedImage nightOverlayTexture;
    private int nightOverlayLevelW = -1;
    private int nightOverlayLevelH = -1;
    /** Build overlay at 1/DOWNSCALE resolution; drawn scaled up with bilinear filtering. */
    private static final int NIGHT_OVERLAY_DOWNSCALE = 4;

    /** Tint colour for dark areas (RGB only; alpha is computed per-pixel from light distance). */
    private static final int NIGHT_TINT_R = 5, NIGHT_TINT_G = 8, NIGHT_TINT_B = 55;
    /** Max alpha in darkest areas (0-255). Higher = darker night. */
    private static final int NIGHT_MAX_ALPHA = 190;
    /** Min alpha at light centers; lights never fully bright (0 = current behaviour). */
    private static final int NIGHT_MIN_ALPHA = 55;
    /** Falloff extends this far past nominal radius (1.5 = 50% softer edge). */
    private static final float NIGHT_LIGHT_FALLOFF_EXTEND = 1.5f;

    /** Actual display size passed in from the controller so encounter covers the full panel (including fullscreen). */
    private int displayW = GameController.GAME_WIDTH;
    private int displayH = GameController.GAME_HEIGHT;

    public void render(Graphics g, Game game, Playing playing) {
        render(g, game, playing, GameController.GAME_WIDTH, GameController.GAME_HEIGHT);
    }

    public void render(Graphics g, Game game, Playing playing, int displayWidth, int displayHeight) {
        this.displayW = displayWidth;
        this.displayH = displayHeight;
        gameView.render(g, game, playing.getXLvlOffset(), playing.getYLvlOffset(), displayW, displayH);
        LevelLoader levelLoader = game.getLevelLoader();
        ensureNightOverlay(levelLoader);
        if (nightOverlayTexture != null) {
            Graphics2D g2 = (Graphics2D) g;
            AffineTransform saveTransform = g2.getTransform();
            Object saveInterpolation = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
            g2.translate(-playing.getXLvlOffset(), -playing.getYLvlOffset());
            g2.scale(GameController.CAMERA_ZOOM, GameController.CAMERA_ZOOM);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(nightOverlayTexture, 0, 0, nightOverlayLevelW, nightOverlayLevelH, null);
            g2.setTransform(saveTransform);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    saveInterpolation != null ? saveInterpolation : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        }
        if (!game.isShowWidowFrame()) {
            drawSoulsCounter(g, game);
            if (game.isLastEncounterMessageVisible())
                drawEncounterOutcomeMessage(g, game);
            if (game.getDoorTriggerNpcId() != null)
                drawKnockHint(g, game, playing);
        } else {
            drawWidowFrame(g, game);
        }
    }

    /**
     * Builds night overlay once at downscaled resolution. Uses alpha to represent darkness:
     * high alpha = dark, low/zero alpha = lit (transparent). Drawn with default SrcOver (hw-accelerated).
     */
    private void ensureNightOverlay(LevelLoader levelLoader) {
        if (levelLoader == null) return;
        int levelW = levelLoader.getLevelWidthPixels();
        int levelH = levelLoader.getLevelHeightPixels();
        if (levelW <= 0 || levelH <= 0) return;
        if (nightOverlayTexture != null && nightOverlayLevelW == levelW && nightOverlayLevelH == levelH)
            return;
        nightOverlayLevelW = levelW;
        nightOverlayLevelH = levelH;
        int w = Math.max(1, levelW / NIGHT_OVERLAY_DOWNSCALE);
        int h = Math.max(1, levelH / NIGHT_OVERLAY_DOWNSCALE);
        nightOverlayTexture = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int tintRGB = (NIGHT_TINT_R << 16) | (NIGHT_TINT_G << 8) | NIGHT_TINT_B;
        List<Light> lights = levelLoader.getLights();
        float invScale = 1f / NIGHT_OVERLAY_DOWNSCALE;
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float darkness = 1f;
                for (Light light : lights) {
                    float dx = x - light.getCenterX() * invScale;
                    float dy = y - light.getCenterY() * invScale;
                    float dist = dx * dx + dy * dy;
                    float radiusScaled = light.getRadius() * invScale * NIGHT_LIGHT_FALLOFF_EXTEND;
                    float r2 = radiusScaled * radiusScaled;
                    float contribution = 1f;
                    if (dist < r2) {
                        float t = (float) Math.sqrt(dist) / radiusScaled;
                        float t2 = t * t;
                        float soft = t * t2 * (10f - 15f * t + 6f * t2);
                        contribution = soft;
                    }
                    darkness *= contribution;
                }
                int alpha = (int) (darkness * (NIGHT_MAX_ALPHA - NIGHT_MIN_ALPHA)) + NIGHT_MIN_ALPHA;
                pixels[y * w + x] = (Math.min(255, alpha) << 24) | tintRGB;
            }
        }
        nightOverlayTexture.setRGB(0, 0, w, h, pixels, 0, w);
    }

    // HUD layout: one panel top-left, scaled for readability
    private static final int HUD_PAD = 24;
    private static final int HUD_INSET = 20;
    private static final int SOULS_FONT_SIZE = 26;
    private static final int SOULS_LINE_HEIGHT = 34;
    private static final int HUD_ROW_GAP = 32;
    private static final int SUSPICION_LABEL_FONT_SIZE = 14;
    private static final int SUSPICION_LABEL_TO_BAR = 8;
    private static final int SUSPICION_BAR_W = 280;
    private static final int SUSPICION_BAR_H = 22;
    private static final int SUSPICION_LABEL_HEIGHT = 18;
    /** Height of the suspicion block: label line + gap + bar. */
    private static final int SUSPICION_BLOCK_H = SUSPICION_LABEL_HEIGHT + SUSPICION_LABEL_TO_BAR + SUSPICION_BAR_H;

    private void drawSoulsCounter(Graphics g, Game game) {
        int panelX = HUD_PAD;
        int panelY = HUD_PAD;
        int panelW = SUSPICION_BAR_W + HUD_INSET * 2;
        int panelH = HUD_INSET + SOULS_LINE_HEIGHT + HUD_ROW_GAP + SUSPICION_BLOCK_H + HUD_INSET;

        g.setColor(new Color(0, 0, 0, 140));
        g.fillRoundRect(panelX, panelY, panelW, panelH, 12, 12);
        g.setColor(new Color(80, 60, 100, 100));
        g.drawRoundRect(panelX, panelY, panelW, panelH, 12, 12);

        int cx = panelX + HUD_INSET;
        int y = panelY + HUD_INSET;

        // Souls (and total in Endless)
        String soulsText = game.getGameMode() == Game.GameMode.ENDLESS
                ? "Quota: " + game.getSouls() + " / " + game.getSoulQuota() + "  (Total: " + game.getTotalSouls() + ")"
                : "Quota: " + game.getSouls() + " / " + game.getSoulQuota();
        g.setFont(new Font("SansSerif", Font.BOLD, SOULS_FONT_SIZE));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(0, 0, 0, 200));
        g.drawString(soulsText, cx + 2, y + 2 + fm.getAscent());
        g.setColor(new Color(230, 200, 255));
        g.drawString(soulsText, cx, y + fm.getAscent());

        y += SOULS_LINE_HEIGHT + HUD_ROW_GAP;
        drawSuspicionMeter(g, game, cx, y);
    }

    private void drawSuspicionMeter(Graphics g, Game game, int x, int y) {
        float s = game.getSuspicion();

        // Label changes with escalation thresholds
        String label;
        Color barColor;
        if (s < 25f)      { label = "Town buzz — Low";           barColor = new Color(120, 80,  160, 200); }
        else if (s < 50f) { label = "Town buzz — Rising";        barColor = new Color(160, 90,  120, 220); }
        else if (s < 75f) { label = "Town buzz — Under review";  barColor = new Color(200, 90,  90,  230); }
        else              { label = "Town buzz — Assignment over"; barColor = new Color(220, 60,  60,  240); }

        g.setFont(new Font("SansSerif", Font.PLAIN, SUSPICION_LABEL_FONT_SIZE));
        FontMetrics smFm = g.getFontMetrics();
        g.setColor(new Color(230, 220, 245));
        g.drawString(label, x, y + smFm.getAscent());

        int barY = y + SUSPICION_LABEL_HEIGHT + SUSPICION_LABEL_TO_BAR;
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(x + 2, barY + 2, SUSPICION_BAR_W, SUSPICION_BAR_H, 6, 6);
        g.setColor(new Color(50, 40, 70));
        g.fillRoundRect(x, barY, SUSPICION_BAR_W, SUSPICION_BAR_H, 6, 6);
        int fill = (int) (SUSPICION_BAR_W * Math.min(1f, s / 100f));
        if (fill > 0) {
            g.setColor(barColor);
            g.fillRoundRect(x, barY, fill, SUSPICION_BAR_H, 6, 6);
        }
    }

    /** Toast-style message after encounter ends (soul collected / door slammed). */
    private void drawEncounterOutcomeMessage(Graphics g, Game game) {
        String msg = game.getLastEncounterMessage();
        if (msg == null) return;
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(msg);
        int pad = 32;
        int boxW = tw + pad * 2;
        int boxH = 56;
        int x = (displayW - boxW) / 2;
        int y = displayH - 140;
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(x, y, boxW, boxH, 12, 12);
        g.setColor(Color.WHITE);
        g.drawString(msg, x + pad, y + (boxH + fm.getAscent()) / 2 - 2);
    }

    /** Icon size (screen pixels) when drawn above the player. */
    private static final int KNOCK_ICON_SIZE = 80;
    /** Vertical offset (world pixels) above player hitbox top for the knock icon. */
    private static final int KNOCK_ICON_OFFSET_ABOVE = 32;

    /** Shown when the player is standing at a door (overworld only): exclamation above player, hint text at bottom. */
    private void drawKnockHint(Graphics g, Game game, Playing playing) {
        float zoom = GameController.CAMERA_ZOOM;
        Model.entities.Player1 player = game.getPlayer1();
        java.awt.geom.Rectangle2D.Float hitbox = player.getHitBox();
        // Player top-center in world pixels -> screen position
        float worldX = (float) (hitbox.x + hitbox.width / 2);
        float worldY = (float) hitbox.y;
        int screenX = (int) (worldX * zoom) - playing.getXLvlOffset();
        int screenY = (int) (worldY * zoom) - playing.getYLvlOffset();
        int iconX = screenX - KNOCK_ICON_SIZE / 2;
        int iconY = screenY - KNOCK_ICON_OFFSET_ABOVE - KNOCK_ICON_SIZE;

        if (exclamationMarkImage == null) {
            try {
                exclamationMarkImage = LoadSave.GetSpriteAtlas(LoadSave.EXCLAMATION_MARK);
            } catch (Exception ignored) { }
        }
        if (exclamationMarkImage != null) {
            g.drawImage(exclamationMarkImage, iconX, iconY, KNOCK_ICON_SIZE, KNOCK_ICON_SIZE, null);
        }

        String hint = "Press E to knock";
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(hint);
        int x = (displayW - tw) / 2;
        int y = displayH - 80;
        g.setColor(new Color(255, 255, 255, 220));
        g.drawString(hint, x, y);
    }

    /** GTA-style encounter: black background, generic door (16:9), current NPC portrait (9:16) in same spot for all. */
    private void drawWidowFrame(Graphics g, Game game) {
        int w = displayW;
        int h = displayH;
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);

        if (doorFrameImage == null) {
            try {
                doorFrameImage = LoadSave.GetSpriteAtlas(LoadSave.GENERIC_DOOR_FRAME);
            } catch (Exception ignored) { }
        }
        if (fallbackPortraitImage == null) {
            try {
                fallbackPortraitImage = LoadSave.GetSpriteAtlas(LoadSave.WIDOW_PORTRAIT);
            } catch (Exception ignored) { }
        }

        if (doorFrameImage == null) {
            g.setColor(Color.WHITE);
            g.drawString("Encounter (genericDoorFrame.png missing in res/)", 50, 100);
            drawEncounterDialogue(g, game, w, h);
            drawEscapeHint(g, game);
            return;
        }

        float doorWidth = w * 0.52f;
        float doorHeight = doorWidth * (9f / 16f);
        int doorX = (int) ((w - doorWidth) / 2);
        int doorY = (int) ((h - doorHeight) / 2);
        g.drawImage(doorFrameImage, doorX, doorY, (int) doorWidth, (int) doorHeight, null);

        BufferedImage portrait = getPortraitForNpc(game.getCurrentNpcProfile());
        if (portrait != null) {
            float portraitHeight = h * 0.88f;
            float portraitWidth = portraitHeight * (9f / 16f);
            int portraitX = (int) ((w - portraitWidth) / 2 + w * 0.14f);
            int portraitY = (int) (h - portraitHeight);
            g.drawImage(portrait, portraitX, portraitY, (int) portraitWidth, (int) portraitHeight, null);
        }

        drawEncounterDialogue(g, game, w, h);
        drawEscapeHint(g, game);
    }

    private BufferedImage getPortraitForNpc(NpcProfile npc) {
        String path = npc != null && !npc.getPortraitImage().isEmpty() ? npc.getPortraitImage() : LoadSave.WIDOW_PORTRAIT;
        BufferedImage img = portraitCache.get(path);
        if (img == null) {
            img = loadPortrait(path);
            if (img != null) portraitCache.put(path, img);
        }
        return img != null ? img : fallbackPortraitImage;
    }

    /** Tries path as-is, then "res/" + path, so portraits are found whether classpath root is res/ or project root. */
    private BufferedImage loadPortrait(String path) {
        try {
            return LoadSave.GetSpriteAtlas(path);
        } catch (Exception ignored) { }
        if (!path.startsWith("res/")) {
            try {
                return LoadSave.GetSpriteAtlas("res/" + path);
            } catch (Exception ignored) { }
        }
        return null;
    }

    private static final int DIALOGUE_PAD = 32;
    private static final int DIALOGUE_INSET = 16;
    private static final int LINE_HEIGHT = 26;
    /** Dialogue box height; leaves room for hint below. */
    private static final int DIALOGUE_BOX_HEIGHT = 340;
    private static final int DIALOGUE_BOTTOM_MARGIN = 56;
    /** Space reserved at bottom of box for input line(s) and hint (so dialogue doesn't overlap). */
    private static final int DIALOGUE_BOTTOM_RESERVE = 72;

    private static final int NAME_ABOVE_BOX_GAP = 6;
    private static final int NAME_FONT_SIZE = 22;

    private void drawEncounterDialogue(Graphics g, Game game, int w, int h) {
        EncounterState enc = game.getEncounterState();
        boolean pendingClose = game.hasPendingEncounterOutcome();
        int boxHeight = Math.min(DIALOGUE_BOX_HEIGHT, h - DIALOGUE_BOTTOM_MARGIN);
        int boxY = h - boxHeight - DIALOGUE_BOTTOM_MARGIN;
        int boxWidth = w - 2 * DIALOGUE_PAD;
        int maxTextWidth = boxWidth - 2 * DIALOGUE_INSET;

        NpcProfile profile = game.getCurrentNpcProfile();
        String npcName = profile != null ? profile.getDisplayName() : "";
        if (!npcName.isEmpty()) {
            g.setFont(new Font("SansSerif", Font.BOLD, NAME_FONT_SIZE));
            g.setColor(new Color(255, 255, 255, 240));
            FontMetrics nameFm = g.getFontMetrics();
            int nameY = boxY - NAME_ABOVE_BOX_GAP;
            g.drawString(npcName, DIALOGUE_PAD, nameY - nameFm.getDescent());
            if (profile != null) {
                g.setFont(new Font("SansSerif", Font.PLAIN, 14));
                g.setColor(new Color(200, 180, 255, 220));
                String diffLabel = "Difficulty: " + profile.getDifficultyLabel();
                g.drawString(diffLabel, DIALOGUE_PAD + boxWidth - g.getFontMetrics().stringWidth(diffLabel), nameY - nameFm.getDescent());
                g.setFont(new Font("SansSerif", Font.PLAIN, 18));
                g.setColor(new Color(255, 255, 255, 230));
            }
        }

        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(DIALOGUE_PAD, boxY, boxWidth, boxHeight, 12, 12);
        g.setColor(new Color(255, 255, 255, 200));
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(DIALOGUE_PAD, boxY, boxWidth, boxHeight, 12, 12);
        } else {
            g.drawRoundRect(DIALOGUE_PAD, boxY, boxWidth, boxHeight, 12, 12);
        }
        g.setColor(new Color(255, 255, 255, 230));
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));

        // "Returning visitor" badge and intel (desire + hint) when the player has visited before
        boolean hasMemory = game.getCurrentNpcId() != null
                && !game.getNpcMemory(game.getCurrentNpcId()).isEmpty();
        int topPad = 28;
        if (hasMemory) {
            g.setColor(new Color(180, 150, 255, 160));
            g.setFont(new Font("SansSerif", Font.ITALIC, 12));
            g.drawString("~ they remember you ~", DIALOGUE_PAD + DIALOGUE_INSET, boxY + 18);
            topPad = 38;
            if (profile != null && (!profile.getHiddenDesire().isEmpty() || !profile.getPersuasionHints().isEmpty())) {
                g.setFont(new Font("SansSerif", Font.PLAIN, 13));
                g.setColor(new Color(220, 210, 255, 230));
                int intelY = boxY + topPad;
                if (!profile.getHiddenDesire().isEmpty()) {
                    String desireLine = "Desire: " + profile.getHiddenDesire();
                    List<String> desireWrapped = wrapText(g.getFontMetrics(), desireLine, maxTextWidth);
                    for (String line : desireWrapped) {
                        g.drawString(line, DIALOGUE_PAD + DIALOGUE_INSET, intelY);
                        intelY += LINE_HEIGHT - 2;
                    }
                    intelY += 4;
                }
                if (!profile.getPersuasionHints().isEmpty()) {
                    String hintLine = "Hint: " + profile.getPersuasionHints().get(0);
                    List<String> hintWrapped = wrapText(g.getFontMetrics(), hintLine, maxTextWidth);
                    for (String line : hintWrapped) {
                        g.drawString(line, DIALOGUE_PAD + DIALOGUE_INSET, intelY);
                        intelY += LINE_HEIGHT - 2;
                    }
                    intelY += 6;
                }
                topPad = intelY - boxY;
            }
            g.setColor(new Color(255, 255, 255, 230));
            g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        }

        FontMetrics fm = g.getFontMetrics();
        List<String> logicalLines = enc.getLines();
        List<String> wrappedLines = new ArrayList<>();
        for (String line : logicalLines) {
            wrappedLines.addAll(wrapText(fm, line, maxTextWidth));
        }

        int contentHeight = boxHeight - topPad - DIALOGUE_BOTTOM_RESERVE;
        int maxVisibleLines = Math.max(1, contentHeight / LINE_HEIGHT);
        int totalWrapped = wrappedLines.size();

        // Clamp scroll: can't go further up than there are lines above the first visible line
        int maxScroll = Math.max(0, totalWrapped - maxVisibleLines);
        int scrollOffset = Math.min(enc.getScrollOffset(), maxScroll);
        // Keep EncounterState in sync so it doesn't drift above the real top
        if (enc.getScrollOffset() > maxScroll) enc.scroll(maxScroll - enc.getScrollOffset());

        // end is the exclusive upper bound; scrollOffset 0 = most recent lines
        int end = totalWrapped - scrollOffset;
        int start = Math.max(0, end - maxVisibleLines);

        int y = boxY + topPad;
        for (int i = start; i < end; i++) {
            g.drawString(wrappedLines.get(i), DIALOGUE_PAD + DIALOGUE_INSET, y);
            y += LINE_HEIGHT;
        }

        // "more above" indicator — sits just above the first drawn line, below any badge
        if (start > 0) {
            g.setColor(new Color(200, 200, 200, 180));
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString("▲ scroll up for more", DIALOGUE_PAD + DIALOGUE_INSET, boxY + topPad - 4);
            g.setColor(new Color(255, 255, 255, 230));
            g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        }

        int inputY = boxY + boxHeight - DIALOGUE_BOTTOM_RESERVE + 6;
        if (!pendingClose) {
            String input = enc.getInputLine();
            if (enc.isLoading()) {
                g.setColor(new Color(200, 200, 255));
                String name = game.getCurrentNpcProfile() != null ? game.getCurrentNpcProfile().getDisplayName() : "They";
                g.drawString(name + (name.equals("They") ? " are" : " is") + " thinking...", DIALOGUE_PAD + DIALOGUE_INSET, inputY);
            } else {
                String inputLine = "You: " + input + "|";
                List<String> inputWrapped = wrapText(fm, inputLine, maxTextWidth);
                for (int i = 0; i < inputWrapped.size(); i++) {
                    g.drawString(inputWrapped.get(i), DIALOGUE_PAD + DIALOGUE_INSET, inputY + i * LINE_HEIGHT);
                }
            }
        }
        g.setColor(new Color(180, 180, 180));
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        String hint = pendingClose ? "Press ENTER or ESC to return to overworld" : "Enter — send message  |  scroll to read history";
        g.drawString(hint, DIALOGUE_PAD + DIALOGUE_INSET, boxY + boxHeight - 12);
    }

    /** Breaks text into lines that fit within maxWidth pixels (word wrap). */
    private static List<String> wrapText(FontMetrics fm, String text, int maxWidth) {
        List<String> out = new ArrayList<>();
        if (text == null || text.isEmpty()) return out;
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            String trial = current.length() == 0 ? word : current + " " + word;
            if (fm.stringWidth(trial) <= maxWidth) {
                current.setLength(0);
                current.append(trial);
            } else {
                if (current.length() > 0) {
                    out.add(current.toString());
                    current.setLength(0);
                }
                if (fm.stringWidth(word) <= maxWidth) {
                    current.append(word);
                } else {
                    for (int i = 0; i < word.length(); i++) {
                        char c = word.charAt(i);
                        String t = current.length() == 0 ? String.valueOf(c) : current.toString() + c;
                        if (fm.stringWidth(t) <= maxWidth) {
                            current.setLength(0);
                            current.append(t);
                        } else {
                            if (current.length() > 0) {
                                out.add(current.toString());
                                current.setLength(0);
                            }
                            current.append(c);
                        }
                    }
                }
            }
        }
        if (current.length() > 0) out.add(current.toString());
        return out;
    }

    private void drawEscapeHint(Graphics g, Game game) {
        g.setColor(new Color(255, 255, 255, 200));
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        String hint = game.hasPendingEncounterOutcome() ? "Press ENTER or ESC to return to overworld" : "ESC — Back to overworld";
        g.drawString(hint, 24, displayH - 24);
    }

}
