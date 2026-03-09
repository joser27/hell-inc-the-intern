package Model;

import java.util.List;

/**
 * Data for one NPC: Tiled npc_id, frame/portrait images, display name, system prompt, tattle chance,
 * difficulty (1–4), English speaking level, hidden desire, and persuasion hints for intel.
 */
public class NpcProfile {
    private final String id;
    private final String frameImage;
    private final String portraitImage;
    private final String displayName;
    private final String systemPrompt;
    private final int tattleChance;
    /** How hard to close a deal: 1=Easy, 2=Medium, 3=Hard, 4=Very Hard. */
    private final int difficulty;
    /** How this NPC speaks: simple, conversational, formal, archaic. */
    private final String englishLevel;
    /** One-line summary of what they want (for intel / reveal after visit). */
    private final String hiddenDesire;
    /** Hints for persuading them (shown after first visit). */
    private final List<String> persuasionHints;

    public NpcProfile(String id, String frameImage, String displayName, String systemPrompt) {
        this(id, frameImage, null, displayName, systemPrompt, 30, 2, "conversational", "", List.of());
    }

    public NpcProfile(String id, String frameImage, String displayName, String systemPrompt, int tattleChance) {
        this(id, frameImage, null, displayName, systemPrompt, tattleChance, 2, "conversational", "", List.of());
    }

    public NpcProfile(String id, String frameImage, String portraitImage, String displayName, String systemPrompt, int tattleChance) {
        this(id, frameImage, portraitImage, displayName, systemPrompt, tattleChance, 2, "conversational", "", List.of());
    }

    public NpcProfile(String id, String frameImage, String portraitImage, String displayName, String systemPrompt, int tattleChance,
                      int difficulty, String englishLevel, String hiddenDesire, List<String> persuasionHints) {
        this.id = id;
        this.frameImage = frameImage != null ? frameImage : "";
        this.portraitImage = portraitImage != null && !portraitImage.isEmpty() ? portraitImage : "";
        this.displayName = displayName != null ? displayName : id;
        this.systemPrompt = systemPrompt != null ? systemPrompt : "";
        this.tattleChance = Math.max(0, Math.min(100, tattleChance));
        this.difficulty = Math.max(1, Math.min(4, difficulty));
        this.englishLevel = englishLevel != null && !englishLevel.isEmpty() ? englishLevel : "conversational";
        this.hiddenDesire = hiddenDesire != null ? hiddenDesire : "";
        this.persuasionHints = persuasionHints != null ? List.copyOf(persuasionHints) : List.of();
    }

    public String getId() { return id; }
    public String getFrameImage() { return frameImage; }
    public String getPortraitImage() { return portraitImage; }
    public String getDisplayName() { return displayName; }
    public String getSystemPrompt() { return systemPrompt; }
    public int getTattleChance() { return tattleChance; }
    public int getDifficulty() { return difficulty; }
    public String getEnglishLevel() { return englishLevel; }
    public String getHiddenDesire() { return hiddenDesire; }
    public List<String> getPersuasionHints() { return persuasionHints; }

    /** Human-readable difficulty label. */
    public String getDifficultyLabel() {
        return switch (difficulty) {
            case 1 -> "Easy";
            case 2 -> "Medium";
            case 3 -> "Hard";
            default -> "Very Hard";
        };
    }
}
