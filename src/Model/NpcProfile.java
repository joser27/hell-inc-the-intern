package Model;

/**
 * Data for one NPC: Tiled npc_id, frame image, portrait image (GTA-style), display name, system prompt, and tattle chance (0–100).
 * When the door is slammed, there is a tattleChance% chance they report to the Investigator (+extra suspicion).
 */
public class NpcProfile {
    private final String id;
    private final String frameImage;
    /** Portrait image for GTA-style encounter (9:16); if blank, falls back to widowPortrait.png. */
    private final String portraitImage;
    private final String displayName;
    private final String systemPrompt;
    /** Chance 0–100 that this NPC reports to the Investigator after a failed/slammed encounter. */
    private final int tattleChance;

    public NpcProfile(String id, String frameImage, String displayName, String systemPrompt) {
        this(id, frameImage, null, displayName, systemPrompt, 30);
    }

    public NpcProfile(String id, String frameImage, String displayName, String systemPrompt, int tattleChance) {
        this(id, frameImage, null, displayName, systemPrompt, tattleChance);
    }

    public NpcProfile(String id, String frameImage, String portraitImage, String displayName, String systemPrompt, int tattleChance) {
        this.id = id;
        this.frameImage = frameImage != null ? frameImage : "";
        this.portraitImage = portraitImage != null && !portraitImage.isEmpty() ? portraitImage : "";
        this.displayName = displayName != null ? displayName : id;
        this.systemPrompt = systemPrompt != null ? systemPrompt : "";
        this.tattleChance = Math.max(0, Math.min(100, tattleChance));
    }

    public String getId() { return id; }
    public String getFrameImage() { return frameImage; }
    public String getPortraitImage() { return portraitImage; }
    public String getDisplayName() { return displayName; }
    public String getSystemPrompt() { return systemPrompt; }
    public int getTattleChance() { return tattleChance; }
}
