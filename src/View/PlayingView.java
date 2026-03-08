package View;

import Controller.GameController;
import Model.EncounterState;
import Model.Game;
import Model.NpcProfile;
import Model.gamestates.Playing;
import Model.gamestates.PlayingUI;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renders the Playing state: full-screen game world and Player1 ability UI.
 * When an NPC encounter is active, draws that NPC's first-person frame (from res/npcs.json) over the overworld.
 */
public class PlayingView {
    private final GameView gameView = new GameView();
    private final Map<String, BufferedImage> encounterFrameCache = new HashMap<>();

    public void render(Graphics g, Game game, Playing playing) {
        gameView.render(g, game, playing.getXLvlOffset(), playing.getYLvlOffset());
        drawPlayingUI(g, playing.getPlayingUI());
        if (!game.isShowWidowFrame()) {
            drawSoulsCounter(g, game);
            if (game.isLastEncounterMessageVisible())
                drawEncounterOutcomeMessage(g, game);
        } else {
            drawWidowFrame(g, game);
        }
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

        // Panel background
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRoundRect(panelX, panelY, panelW, panelH, 12, 12);
        g.setColor(new Color(80, 60, 100, 100));
        g.drawRoundRect(panelX, panelY, panelW, panelH, 12, 12);

        int cx = panelX + HUD_INSET;
        int y = panelY + HUD_INSET;

        // Souls
        String soulsText = "Souls: " + game.getSouls() + " / " + game.getSoulQuota();
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
        if (s < 25f)      { label = "Suspicion — Dormant";       barColor = new Color(120, 80,  160, 200); }
        else if (s < 50f) { label = "Suspicion — Patrol";        barColor = new Color(160, 90,  120, 220); }
        else if (s < 75f) { label = "Suspicion — Interviewing"; barColor = new Color(200, 90,  90,  230); }
        else              { label = "Suspicion — Hunting";       barColor = new Color(220, 60,  60,  240); }

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
        int x = (GameController.GAME_WIDTH - boxW) / 2;
        int y = GameController.GAME_HEIGHT - 140;
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(x, y, boxW, boxH, 12, 12);
        g.setColor(Color.WHITE);
        g.drawString(msg, x + pad, y + (boxH + fm.getAscent()) / 2 - 2);
    }

    /** First-person encounter view; frame image comes from current NPC profile (res/npcs.json). */
    private void drawWidowFrame(Graphics g, Game game) {
        NpcProfile npc = game.getCurrentNpcProfile();
        BufferedImage frameImage = null;
        if (npc != null) {
            frameImage = encounterFrameCache.get(npc.getId());
            if (frameImage == null) {
                try {
                    frameImage = LoadSave.GetSpriteAtlas(npc.getFrameImage());
                    encounterFrameCache.put(npc.getId(), frameImage);
                } catch (Exception ignored) { }
            }
        }
        if (frameImage == null) {
            g.setColor(new Color(40, 20, 30));
            g.fillRect(0, 0, GameController.GAME_WIDTH, GameController.GAME_HEIGHT);
            g.setColor(Color.WHITE);
            g.drawString(npc != null ? "Encounter (" + npc.getFrameImage() + " not found)" : "Encounter (no NPC)", 50, 100);
            drawEscapeHint(g, game);
            return;
        }
        int w = GameController.GAME_WIDTH;
        int h = GameController.GAME_HEIGHT;
        g.drawImage(frameImage, 0, 0, w, h, null);
        drawEncounterDialogue(g, game, w, h);
        drawEscapeHint(g, game);
    }

    private static final int DIALOGUE_PAD = 32;
    private static final int DIALOGUE_INSET = 16;
    private static final int LINE_HEIGHT = 26;
    /** Dialogue box height; leaves room for hint below. */
    private static final int DIALOGUE_BOX_HEIGHT = 340;
    private static final int DIALOGUE_BOTTOM_MARGIN = 56;
    /** Space reserved at bottom of box for input line(s) and hint (so dialogue doesn't overlap). */
    private static final int DIALOGUE_BOTTOM_RESERVE = 72;

    private void drawEncounterDialogue(Graphics g, Game game, int w, int h) {
        EncounterState enc = game.getEncounterState();
        boolean pendingClose = game.hasPendingEncounterOutcome();
        int boxHeight = Math.min(DIALOGUE_BOX_HEIGHT, h - DIALOGUE_BOTTOM_MARGIN);
        int boxY = h - boxHeight - DIALOGUE_BOTTOM_MARGIN;
        int boxWidth = w - 2 * DIALOGUE_PAD;
        int maxTextWidth = boxWidth - 2 * DIALOGUE_INSET;

        g.setColor(new Color(0, 0, 0, 220));
        g.fillRoundRect(DIALOGUE_PAD, boxY, boxWidth, boxHeight, 12, 12);
        g.setColor(new Color(255, 255, 255, 230));
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));

        // "Returning visitor" badge — shown only when the NPC has memory of a prior visit
        boolean hasMemory = game.getCurrentNpcId() != null
                && !game.getNpcMemory(game.getCurrentNpcId()).isEmpty();
        int topPad = hasMemory ? 38 : 28;
        if (hasMemory) {
            g.setColor(new Color(180, 150, 255, 160));
            g.setFont(new Font("SansSerif", Font.ITALIC, 12));
            g.drawString("~ they remember you ~", DIALOGUE_PAD + DIALOGUE_INSET, boxY + 18);
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
        g.drawString(hint, 24, GameController.GAME_HEIGHT - 24);
    }

    private void drawPlayingUI(Graphics g, PlayingUI ui) {
        // if (ui == null) return;
        // int barY = GameController.GAME_HEIGHT - 50 * GameController.SCALE;
        // g.setColor(new Color(255, 255, 255, 100));
        // g.fillRect(0, barY - 10 * GameController.SCALE, GameController.GAME_WIDTH, 60 * GameController.SCALE);
        // int startX = GameController.GAME_WIDTH / 2 - (5 * 20 * GameController.SCALE) / 2;
        // g.drawImage(ui.getPlayer1_P(), startX, barY, null);
        // g.drawImage(ui.getPlayer1_Q(), startX + 20 * GameController.SCALE, barY, null);
        // g.drawImage(ui.getPlayer1_W(), startX + 40 * GameController.SCALE, barY, null);
        // g.drawImage(ui.getPlayer1_E(), startX + 60 * GameController.SCALE, barY, null);
        // g.drawImage(ui.getPlayer1_R(), startX + 80 * GameController.SCALE, barY, null);
    }

}
