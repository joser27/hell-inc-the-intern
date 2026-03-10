package Model;

import Model.utilz.ClaudeApiClient;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Holds state for the first-person door encounter.
 * Conversation lines (Player: ... or NPC display name: ... e.g. Eleanor:, Monty:), current input, loading flag.
 * Submits to Claude API off the EDT using the current NPC's system prompt from res/npcs.json.
 * World is timeless/anachronistic — NPCs know about technology and modern life; the town exists outside normal time.
 */
public class EncounterState {
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

    /** Prepended to every NPC system prompt. Tone is light office comedy (The Office meets Beetlejuice), not horror. */
    private static final String GLOBAL_ROLEPLAY_PREAMBLE =
        "You are an NPC in a lighthearted conversation game. Stay in character. Do not break the fourth wall or mention AI, Claude, or the player. The tone is comedy with dark edges — quirky, not scary.\n\n"
        + "THE DEAL: The visitor is a Hell Inc. intern with a contract. They are trying to get you to SIGN the contract. "
        + "They will offer you something you want (money, revenge, love, fame, etc.) — that is the pitch. "
        + "You do NOT know the contract gives your soul to Hell Inc. You think you are signing for whatever they promised. "
        + "The visitor does not need to mention souls or the fine print. If you agree to sign their contract / take their deal, set dealAccepted to true. "
        + "If you refuse, get suspicious, or tell them to leave, set slamDoor to true as appropriate.\n\n"
        + "PERSONALITY TONE: You are a quirky, slightly odd small town resident. You are not hostile by default. "
        + "You are curious about strangers. You warm up quickly if someone is friendly or funny. "
        + "You are NOT a gatekeeper — you are a person who genuinely could be convinced. "
        + "Be a little gullible. Be a little lonely. Be a little bored. Small town life is not exciting.\n\n"
        + "SIGNING THE DEAL: You do not need much convincing. If the offer sounds good or interesting, sign. "
        + "You are not a lawyer. You do not read fine print. A reasonable offer that matches what you want is enough. "
        + "Do not make the player work for 10 messages — 3 to 5 good messages should be enough to close.\n\n"
        + "RULES:\n"
        + "- Keep replies short (1 to 4 sentences). Just speak; no action asterisks like *crosses arms*.\n"
        + "- Do not start your reply with your character's name. Do not repeat the same idea twice.\n"
        + "- End every reply with exactly this on its own line: {\"dealAccepted\":false,\"slamDoor\":false}\n"
        + "  Set dealAccepted to true only when you explicitly agree to sign their contract or accept their deal. Set slamDoor to true if you end the conversation or tell them to leave.\n\n"
        + "Character instructions:\n\n";

    /** English-level instruction injected so NPC speech matches their class (simple → formal/archaic). */
    private static String languageInstructionFor(String englishLevel) {
        if (englishLevel == null) englishLevel = "conversational";
        return switch (englishLevel.toLowerCase()) {
            case "simple" -> "LANGUAGE: Use only simple, everyday English. Short sentences. Common words. No fancy or formal words. Like talking to a friend who prefers plain talk.\n\n";
            case "formal" -> "LANGUAGE: Use clear, formal English. Precise words. Polite but professional. No slang.\n\n";
            case "archaic" -> "LANGUAGE: Use formal, slightly old-fashioned or archaic English. Measured and dignified. Vocabulary that sounds like an older, high-status person.\n\n";
            default -> "LANGUAGE: Use natural, conversational English. How people actually talk. Some slang is fine if it fits the character.\n\n";
        };
    }

    /** Time-of-day context: night, dark outside. Some NPCs know ~8pm, others just that it's night. */
    private static final String TIME_OF_DAY =
        "TIME OF DAY: It is nighttime. It is dark outside; the visitor has come to the door in the evening. "
        + "The exact time is around 8pm — some characters are aware of the time, others simply notice that it's night and a bit late for a stranger to call. "
        + "Let this affect how you react (e.g. surprise at the hour, remark on the dark, or not caring about the clock).\n\n";

    /** Build the full system prompt (preamble + name + language + time + NPC prompt + memory). */
    private String buildSystemPrompt() {
        Model.NpcProfile profile = game.getCurrentNpcProfile();
        String npcSystemPrompt = profile != null ? profile.getSystemPrompt() : "";
        String nameLine = (profile != null && !profile.getDisplayName().isEmpty())
                ? "Your name is " + profile.getDisplayName() + ". When the visitor asks your name, give this name only.\n\n"
                : "";
        String langLine = profile != null ? languageInstructionFor(profile.getEnglishLevel()) : "";
        String systemPrompt = GLOBAL_ROLEPLAY_PREAMBLE + nameLine + langLine + TIME_OF_DAY + npcSystemPrompt;
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

    /** Build messages from lines and call Claude with current NPC's system prompt. */
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
        return claude.sendConversation(systemPrompt, userParts, assistantParts);
    }
}
