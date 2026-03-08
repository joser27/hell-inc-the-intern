package Model.utilz;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Minimal client for Anthropic Messages API (Claude).
 * Reads API key from env/.env (ANTHROPIC_API_KEY).
 */
public class ClaudeApiClient {
    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    /** Cheapest: claude-haiku-4-5-20251001. Better quality: claude-sonnet-4-20250514 */
    private static final String MODEL = "claude-haiku-4-5-20251001";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private final String apiKey;
    private final HttpClient http;

    public ClaudeApiClient() {
        this(EnvLoader.getAnthropicApiKey());
    }

    public ClaudeApiClient(String apiKey) {
        this.apiKey = apiKey;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * Send a single user message and return the assistant's text reply.
     */
    public String sendMessage(String userMessage) throws Exception {
        return sendConversation(null, List.of(userMessage), List.of());
    }

    /**
     * Send a full conversation (system prompt + alternating user/assistant messages) and return the next assistant reply.
     * userMessages and assistantMessages are paired; if there is one more user message, it is the current turn.
     */
    public String sendConversation(String systemPrompt, List<String> userMessages, List<String> assistantMessages) throws Exception {
        if (apiKey == null || apiKey.isBlank())
            throw new IllegalStateException("ANTHROPIC_API_KEY not set in env/.env");
        String body = buildConversationBody(systemPrompt, userMessages, assistantMessages);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("x-api-key", apiKey)
                .header("anthropic-version", ANTHROPIC_VERSION)
                .header("content-type", "application/json")
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200)
            throw new RuntimeException("Claude API error " + response.statusCode() + ": " + response.body());
        return extractTextFromResponse(response.body());
    }

    private static String buildConversationBody(String systemPrompt, List<String> userMessages, List<String> assistantMessages) {
        StringBuilder msgJson = new StringBuilder();
        int n = Math.min(userMessages.size(), assistantMessages.size());
        for (int i = 0; i < n; i++) {
            if (msgJson.length() > 0) msgJson.append(",\n");
            msgJson.append("    { \"role\": \"user\", \"content\": \"").append(escapeJson(userMessages.get(i))).append("\" },\n");
            msgJson.append("    { \"role\": \"assistant\", \"content\": \"").append(escapeJson(assistantMessages.get(i))).append("\" }");
        }
        if (userMessages.size() > assistantMessages.size()) {
            if (msgJson.length() > 0) msgJson.append(",\n");
            msgJson.append("    { \"role\": \"user\", \"content\": \"").append(escapeJson(userMessages.get(userMessages.size() - 1))).append("\" }");
        }
        String systemPart = (systemPrompt != null && !systemPrompt.isBlank())
                ? "  \"system\": \"" + escapeJson(systemPrompt) + "\",\n"
                : "";
        return "{\n"
                + systemPart
                + "  \"model\": \"" + MODEL + "\",\n"
                + "  \"max_tokens\": 1024,\n"
                + "  \"messages\": [\n"
                + msgJson + "\n  ]\n"
                + "}";
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"':  sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:   sb.append(c); break;
            }
        }
        return sb.toString();
    }

    /** Parse response JSON and return content[0].text (first text block). */
    private static String extractTextFromResponse(String json) {
        // Look for "text":"... with possible escaped quotes inside
        int idx = json.indexOf("\"text\"");
        if (idx == -1) return "";
        idx = json.indexOf(':', idx);
        if (idx == -1) return "";
        idx = json.indexOf('"', idx + 1);
        if (idx == -1) return "";
        int start = idx + 1;
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                if (next == '"') { sb.append('"'); i++; continue; }
                if (next == '\\') { sb.append('\\'); i++; continue; }
                if (next == 'n') { sb.append('\n'); i++; continue; }
                if (next == 'r') { sb.append('\r'); i++; continue; }
                if (next == 't') { sb.append('\t'); i++; continue; }
            }
            if (c == '"') break;
            sb.append(c);
        }
        return sb.toString();
    }
}
