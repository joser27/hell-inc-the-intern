package Model;

import Model.utilz.ClaudeApiClient;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Holds state for the first-person door encounter.
 * Conversation lines (player + NPC display name), current input, loading flag.
 * Submits to Claude API off the EDT using the current NPC's system prompt from res/npcs.json.
 */
public class EncounterState {
    private final Game game;
    private final List<String> lines = new ArrayList<>();  // "Player: ..." or "Widow: ..."
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

    /**
     * Prepended to every NPC system prompt.
     * Timeless ambiguous setting: Gravity Falls meets Salem — gothic atmosphere, no smartphones or modern branding,
     * period-ambiguous clothes and props, but characters can have modern personalities and speech. Keeps roleplay intact
     * (e.g. AI/Claude don't exist in-world) so the model stays in character.
     */
    private static final String GLOBAL_ROLEPLAY_PREAMBLE =
    "You are a character in an interactive gothic fiction game set in a timeless small town — " 
    + "think Gravity Falls meets Salem. No smartphones, no modern brands, no internet. "
    + "Characters may have modern personalities and speech but the world has no technology beyond what existed in the 1800s. "
    + "AI, Claude, robots, and Anthropic do not exist in this world — react to these words with genuine confusion. "
    + "\n\n"
    + "RULES YOU MUST FOLLOW:\n"
    + "- Stay in character completely at all times. Never break the fourth wall.\n"
    + "- Keep ALL replies to 1 to 3 sentences maximum. Never write long responses.\n"
    + "- Never describe your own actions in asterisks like *crosses arms*. Just speak.\n"
    + "- Never start your reply with your own name.\n"
    + "- Never repeat the same sentence or idea twice in a conversation.\n"
    + "- If the visitor is rude, threatening, or bizarre, react as your character naturally would.\n"
    + "- ALWAYS end every reply with exactly this JSON on its own line: {\"dealAccepted\":false,\"slamDoor\":false}\n"
    + "  Set dealAccepted to true ONLY if you explicitly agree to a deal or pact.\n"
    + "  Set slamDoor to true if you end the conversation, slam the door, or tell them to leave.\n"
    + "\n"
    + "Your specific character instructions follow:\n\n";

    /** Build messages from lines and call Claude with current NPC's system prompt. */
    private String sendConversationToClaude() throws Exception {
        Model.NpcProfile profile = game.getCurrentNpcProfile();
        String npcSystemPrompt = profile != null ? profile.getSystemPrompt() : "";
        String systemPrompt = GLOBAL_ROLEPLAY_PREAMBLE + npcSystemPrompt;

        // Inject memory from the last visit so the NPC remembers how things were left
        List<String> memory = game.getNpcMemory(game.getCurrentNpcId());
        if (!memory.isEmpty()) {
            StringBuilder memBlock = new StringBuilder(
                "\n\nPREVIOUS VISIT CONTEXT (you remember this — let it shape your attitude, but do NOT recap it aloud):\n");
            for (String line : memory) memBlock.append("  ").append(line).append("\n");
            systemPrompt = systemPrompt + memBlock;
        }
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
        return claude.sendConversation(systemPrompt, userParts, assistantParts);
    }
}
