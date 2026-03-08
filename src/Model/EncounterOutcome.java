package Model;

/**
 * Result of one NPC turn: display text plus whether the deal was accepted or the door was slammed.
 * Claude is instructed to append a JSON line to each reply so we can parse this.
 */
public class EncounterOutcome {
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
     * Parse Claude's reply: expects optional trailing line {@code {"dealAccepted":bool,"slamDoor":bool}}.
     * Returns outcome with replyText (without that line) and the two flags (default false if not found).
     */
    public static EncounterOutcome parse(String rawReply) {
        if (rawReply == null) return new EncounterOutcome("", false, false);
        String text = rawReply.trim();
        boolean dealAccepted = false;
        boolean slamDoor = false;

        // Find last line that looks like {"dealAccepted":true/false,"slamDoor":true/false}
        int lastNewline = text.lastIndexOf('\n');
        if (lastNewline >= 0) {
            String lastLine = text.substring(lastNewline + 1).trim();
            if (lastLine.startsWith("{") && lastLine.contains("dealAccepted") && lastLine.contains("slamDoor")) {
                dealAccepted = lastLine.contains("\"dealAccepted\":true");
                slamDoor = lastLine.contains("\"slamDoor\":true");
                text = text.substring(0, lastNewline).trim();
            }
        }
        text = cleanReplyText(text);
        return new EncounterOutcome(text, dealAccepted, slamDoor);
    }

    /** Remove stray ASCII/numbers that sometimes appear in model output (e.g. token counts, "56"). */
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
        return sb.toString().trim();
    }
}
