package Model;

/**
 * Result of one NPC turn: display text plus whether the deal was accepted or the door was slammed.
 * Claude is instructed to append a JSON line to each reply: {"dealAccepted":bool,"slamDoor":bool}.
 * Parsing strips that line and extracts the two flags so the game can update soul count and close the encounter.
 * Long replies are truncated for display so NPCs don't dominate the dialogue.
 */
public class EncounterOutcome {
    /** Max characters to show for one NPC reply; rest is truncated with "...". */
    private static final int MAX_DISPLAY_CHARS = 380;
    /** The reply text to show (with any trailing JSON line stripped). */
    public final String replyText;
    /** True if the NPC agreed to the deal (soul sold). */
    public final boolean dealAccepted;
    /** True if the NPC ended the conversation (closed door, threw player out). */
    public final boolean slamDoor;

    public EncounterOutcome(String replyText, boolean dealAccepted, boolean slamDoor) {
        this.replyText = replyText != null ? replyText.trim() : "";
        this.dealAccepted = dealAccepted;
        this.slamDoor = slamDoor;
    }

    /**
     * Parse Claude's reply: looks for a line {@code {"dealAccepted":bool,"slamDoor":bool}} in the last few lines.
     * Returns outcome with replyText (that line stripped) and the two flags (default false if not found).
     */
    public static EncounterOutcome parse(String rawReply) {
        if (rawReply == null) return new EncounterOutcome("", false, false);
        String text = rawReply.trim();
        boolean dealAccepted = false;
        boolean slamDoor = false;

        String[] lines = text.split("\n");
        int jsonLineIndex = -1;
        for (int i = lines.length - 1; i >= 0 && i >= lines.length - 3; i--) {
            String line = lines[i].trim();
            if (line.startsWith("{") && line.contains("dealAccepted") && line.contains("slamDoor")) {
                dealAccepted = line.contains("\"dealAccepted\":true");
                slamDoor = line.contains("\"slamDoor\":true");
                jsonLineIndex = i;
                break;
            }
        }
        if (jsonLineIndex >= 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < jsonLineIndex; i++) {
                if (i > 0) sb.append('\n');
                sb.append(lines[i].trim());
            }
            text = sb.toString().trim();
        }
        text = cleanReplyText(text);
        return new EncounterOutcome(text, dealAccepted, slamDoor);
    }

    /** Remove stray junk and truncate long replies for display. */
    private static String cleanReplyText(String text) {
        if (text == null) return "";
        text = text.trim();
        // Strip trailing whitespace + digits (e.g. "...mind. 56" or "... 56")
        text = text.replaceAll("\\s+\\d+\\s*$", "").trim();
        // Remove any lines that are only digits or non-printable junk
        String[] lines = text.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            String t = line.trim();
            if (t.isEmpty()) continue;
            if (t.matches("^\\d+$")) continue;  // skip line that is only numbers
            if (sb.length() > 0) sb.append('\n');
            sb.append(t);
        }
        text = sb.toString().trim();
        // Truncate very long replies so NPCs don't talk too much
        if (text.length() > MAX_DISPLAY_CHARS) {
            int cut = MAX_DISPLAY_CHARS;
            while (cut > 0 && cut < text.length() && !Character.isWhitespace(text.charAt(cut - 1))) {
                cut--;
            }
            if (cut < MAX_DISPLAY_CHARS / 2) cut = MAX_DISPLAY_CHARS;
            text = text.substring(0, cut).trim() + "...";
        }
        return text;
    }
}
