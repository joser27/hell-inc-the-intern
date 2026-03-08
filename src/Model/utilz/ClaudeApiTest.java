package Model.utilz;

/**
 * Quick test for Claude API. Run this class (main) to verify env/.env and API call.
 * Ensure ANTHROPIC_API_KEY is set in env/.env and run from project root (demo2DGame).
 */
public class ClaudeApiTest {
    public static void main(String[] args) {
        String key = EnvLoader.getAnthropicApiKey();
        if (key == null || key.isBlank()) {
            System.err.println("ANTHROPIC_API_KEY not found in env/.env. Add: ANTHROPIC_API_KEY=your_key");
            return;
        }
        System.out.println("Calling Claude API (model: claude-sonnet-4-20250514)...");
        try {
            ClaudeApiClient client = new ClaudeApiClient();
            String reply = client.sendMessage("Hello, world");
            System.out.println("Reply: " + reply);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
