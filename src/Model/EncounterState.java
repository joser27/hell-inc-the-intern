package Model;

import Model.utilz.ClaudeApiClient;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Holds state for the first-person door encounter.
 * Conversation lines (Player: ... or NPC display name: ... e.g. Eleanor:, Casey Ohs:), current input, loading flag.
 * Submits to Claude API off the EDT using the current NPC's system prompt from res/npcs.json.
 * World is timeless/anachronistic — NPCs know about technology and modern life; the town exists outside normal time.
 * Conversation history is capped to avoid OOM from unbounded growth (see MAX_LINES and MAX_MESSAGES_FOR_API).
 */
public class EncounterState {
    /** Max lines kept in memory (and shown in UI). Prevents OOM from very long single encounters. */
    private static final int MAX_LINES = 200;
    /** Max user+assistant message pairs sent to the API per request (keeps request body and token usage bounded). */
    private static final int MAX_MESSAGES_FOR_API = 30;

    private final Game game;
    private final List<String> lines = new ArrayList<>();  // "Player: ..." or "NPC name: ..."
    private final StringBuilder inputLine = new StringBuilder();
    private boolean loading = false;
    private String lastError = null;
    /** Lines scrolled up from the bottom (0 = showing most recent). */
    private int scrollOffset = 0;
    private final ClaudeApiClient claude = new ClaudeApiClient();
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "claude-encounter");
        t.setDaemon(true);
        return t;
    });

    public EncounterState(Game game) {
        this.game = game;
    }

    public List<String> getLines() { return new ArrayList<>(lines); }
    public String getInputLine() { return inputLine.toString(); }
    public boolean isLoading() { return loading; }
    public String getLastError() { return lastError; }
    public int getScrollOffset() { return scrollOffset; }

    /**
     * Scroll the dialogue history. Positive delta = scroll up (see older lines).
     * maxOffset is enforced by the view when it knows the total wrapped line count.
     */
    public void scroll(int delta) { scrollOffset = Math.max(0, scrollOffset + delta); }

    /** Snap back to the latest messages (called automatically when a new line arrives). */
    public void resetScroll() { scrollOffset = 0; }

    /** Clear dialogue history and input (e.g. when starting an encounter with a different NPC). */
    public void clearConversation() {
        lines.clear();
        inputLine.setLength(0);
        scrollOffset = 0;
        lastError = null;
    }

    public void appendInputChar(char c) {
        if (c >= 32 && c < 127) inputLine.append(c);
    }

    public void backspaceInput() {
        if (inputLine.length() > 0) inputLine.setLength(inputLine.length() - 1);
    }

    public void clearInput() { inputLine.setLength(0); }

    /** Call when user presses Enter. Adds player line and requests NPC reply (async). */
    public void submitInput() {
        String text = inputLine.toString().trim();
        if (text.isEmpty() || loading) return;
        clearInput();
        lastError = null;
        lines.add("Player: " + text);
        trimLinesToMax();
        resetScroll();

        loading = true;
        executor.submit(() -> {
            try {
                String rawReply = sendConversationToClaude();
                EncounterOutcome outcome = rawReply != null ? EncounterOutcome.parse(rawReply) : new EncounterOutcome("", false, false);
                boolean deal = outcome.dealAccepted;
                boolean slam = outcome.slamDoor;
                SwingUtilities.invokeLater(() -> {
                    String displayName = getNpcDisplayName();
                    if (!outcome.replyText.isEmpty()) {
                        lines.add(displayName + ": " + outcome.replyText);
                        trimLinesToMax();
                        resetScroll();
                    }
                    loading = false;
                    if (deal || slam) {
                        game.setPendingEncounterOutcome(deal, slam);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    lastError = e.getMessage();
                    lines.add("(Error: " + e.getMessage() + ")");
                    loading = false;
                });
            }
        });
    }

    private String getNpcDisplayName() {
        Model.NpcProfile p = game.getCurrentNpcProfile();
        return p != null ? p.getDisplayName() : "NPC";
    }

    /** Character identity block — placed FIRST so the model anchors on personality before reading rules. */
    private static final String CHARACTER_HEADER =
        "WHO YOU ARE (this is your identity — stay in character at all times, matching this personality and tone exactly):\n\n";

    /** Game rules — placed AFTER the character so personality is already established. */
    private static final String GAME_RULES =
        "\n\n--- GAME RULES (follow these, but never break character to do so) ---\n\n"
        + "You are in a conversation game. Do not break the fourth wall or mention AI.\n\n"
        + "THE TOWN: You live in a small town. You know of the other residents and neighbors (e.g. Eleanor, Gerald, Casey Ohs, Aldous, Marta, Lord Vane, Father Creed, Brad Kessler, Dr. Ashford, Harlan Webb, Maddie Walsh). If the visitor asks about someone in town, you know of them — react in character. Never claim you don't know a fellow resident.\n\n"
        + "THE DEAL: The visitor is a Hell Inc. intern with a contract. They want you to SIGN it. "
        + "They will offer you something you want — that is the pitch. "
        + "You do NOT know the contract gives your soul to Hell Inc. You think you are signing for whatever they promised. "
        + "If you agree to sign, set dealAccepted to true. If you refuse or tell them to leave, set slamDoor to true.\n\n"
        + "SIGNING: You do not need much convincing. If the offer sounds good, sign. "
        + "You are not a lawyer. A reasonable offer that matches what you want is enough. "
        + "3 to 5 good messages should be enough to close.\n\n"
        + "CONVERSATION: Be interesting but brief. React to what the visitor said, then add one short thought of your own. Keep the conversation moving.\n\n"
        + "It is nighttime; the visitor has come in the evening. Do not fixate on or complain about the time.\n\n"
        + "FORMAT:\n"
        + "- Spoken dialogue only. No stage directions or asterisk actions unless your character uses them (e.g. sound effects).\n"
        + "- Keep replies SHORT: 1 to 3 sentences only. No long paragraphs or monologues. Do not start with your name.\n"
        + "- End every reply with exactly this on its own line: {\"dealAccepted\":false,\"slamDoor\":false}\n";

    /** English-level instruction injected so NPC speech matches their class. */
    private static String languageInstructionFor(String englishLevel) {
        if (englishLevel == null) englishLevel = "conversational";
        return switch (englishLevel.toLowerCase()) {
            case "simple" -> "\nLANGUAGE: Simple everyday English. Short sentences. Common words.\n";
            case "formal" -> "\nLANGUAGE: Clear, formal English. Precise words. No slang.\n";
            case "archaic" -> "\nLANGUAGE: Formal, slightly old-fashioned English. Measured and dignified.\n";
            default -> "";
        };
    }

    /** Build the full system prompt: character FIRST, then game rules, then language + memory. */
    private String buildSystemPrompt() {
        Model.NpcProfile profile = game.getCurrentNpcProfile();
        String npcSystemPrompt = profile != null ? profile.getSystemPrompt() : "";
        String nameLine = (profile != null && !profile.getDisplayName().isEmpty())
                ? "Your name is " + profile.getDisplayName() + ".\n"
                : "";
        String langLine = profile != null ? languageInstructionFor(profile.getEnglishLevel()) : "";
        String systemPrompt = CHARACTER_HEADER + nameLine + npcSystemPrompt + GAME_RULES + langLine;
        List<String> memory = game.getNpcMemory(game.getCurrentNpcId());
        if (!memory.isEmpty()) {
            StringBuilder memBlock = new StringBuilder(
                "\n\nPREVIOUS VISIT CONTEXT (you remember this — let it shape your attitude, but do NOT recap it aloud):\n");
            for (String line : memory) memBlock.append("  ").append(line).append("\n");
            systemPrompt = systemPrompt + memBlock;
        }
        return systemPrompt;
    }

    private static final String KNOCK_PROMPT = "*knocks on the door*";

    /** Request the NPC's opening line when the visitor has just knocked. Call once when the encounter opens. */
    public void requestOpeningLine() {
        if (loading) return;
        lines.add("Player: " + KNOCK_PROMPT);
        loading = true;
        executor.submit(() -> {
            try {
                String systemPrompt = buildSystemPrompt();
                String rawReply = claude.sendConversation(systemPrompt, List.of(KNOCK_PROMPT), List.of());
                EncounterOutcome outcome = rawReply != null ? EncounterOutcome.parse(rawReply) : new EncounterOutcome("", false, false);
                String replyText = outcome.replyText != null ? outcome.replyText.trim() : "";
                SwingUtilities.invokeLater(() -> {
                    if (!replyText.isEmpty()) {
                        lines.add(getNpcDisplayName() + ": " + replyText);
                        trimLinesToMax();
                        resetScroll();
                    }
                    loading = false;
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    lastError = e.getMessage();
                    lines.add("(Error: " + e.getMessage() + ")");
                    loading = false;
                });
            }
        });
    }

    /** Keep only the most recent MAX_LINES to avoid unbounded memory growth. */
    private void trimLinesToMax() {
        while (lines.size() > MAX_LINES) {
            lines.remove(0);
        }
    }

    /** Build messages from lines and call Claude with current NPC's system prompt. Only the last MAX_MESSAGES_FOR_API pairs are sent to limit request size and tokens. */
    private String sendConversationToClaude() throws Exception {
        String systemPrompt = buildSystemPrompt();
        String npcPrefix = getNpcDisplayName() + ": ";
        List<String> userParts = new ArrayList<>();
        List<String> assistantParts = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("Player: ")) {
                userParts.add(line.substring("Player: ".length()).trim());
            } else if (line.startsWith(npcPrefix)) {
                assistantParts.add(line.substring(npcPrefix.length()).trim());
            }
        }
        // Send only the last N message pairs to avoid huge request bodies and OOM
        int pairs = Math.min(userParts.size(), assistantParts.size());
        if (pairs > MAX_MESSAGES_FOR_API) {
            int from = pairs - MAX_MESSAGES_FOR_API;
            userParts = new ArrayList<>(userParts.subList(from, userParts.size()));
            assistantParts = new ArrayList<>(assistantParts.subList(from, assistantParts.size()));
        }
        return claude.sendConversation(systemPrompt, userParts, assistantParts);
    }
}
