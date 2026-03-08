package Model;

/**
 * Data for one NPC: Tiled npc_id, encounter frame image, display name in dialogue, and Claude system prompt.
 */
public class NpcProfile {
    private final String id;
    private final String frameImage;
    private final String displayName;
    private final String systemPrompt;

    public NpcProfile(String id, String frameImage, String displayName, String systemPrompt) {
        this.id = id;
        this.frameImage = frameImage != null ? frameImage : "";
        this.displayName = displayName != null ? displayName : id;
        this.systemPrompt = systemPrompt != null ? systemPrompt : "";
    }

    public String getId() { return id; }
    public String getFrameImage() { return frameImage; }
    public String getDisplayName() { return displayName; }
    public String getSystemPrompt() { return systemPrompt; }
}
