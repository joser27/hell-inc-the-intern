package Model.utilz;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Minimal client for Anthropic Messages API (Claude).
 * Uses a Lambda proxy URL for production (no API key shipped with the game).
 * Falls back to direct Anthropic API with ANTHROPIC_API_KEY from env/.env for local dev.
 */
public class ClaudeApiClient {
    private static final String DIRECT_API_URL = "https://api.anthropic.com/v1/messages";

    private static final String PROXY_URL = "https://rx25ulq5xcupw3cpzr3qerxtou0rkvsg.lambda-url.us-east-1.on.aws/";

    /** Cheapest: claude-haiku-4-5-20251001. Better quality: claude-sonnet-4-20250514 */
    private static final String MODEL = "claude-haiku-4-5-20251001";
    private static final String ANTHROPIC_VERSION = "2023-06-01";
    /** Cap reply length so NPCs stay concise (roughly 2–4 short sentences). */
    private static final int MAX_RESPONSE_TOKENS = 256;

    private final String apiUrl;
    private final String apiKey;
    private final boolean useProxy;
    private final HttpClient http;

    public ClaudeApiClient() {
        if (!PROXY_URL.isBlank()) {
            this.apiUrl = PROXY_URL;
            this.apiKey = null;
            this.useProxy = true;
        } else {
            this.apiUrl = DIRECT_API_URL;
            this.apiKey = EnvLoader.getAnthropicApiKey();
            this.useProxy = false;
        }
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
        if (!useProxy && (apiKey == null || apiKey.isBlank()))
            throw new IllegalStateException("ANTHROPIC_API_KEY not set in env/.env and no CLAUDE_API_URL proxy configured");
        String body = buildConversationBody(systemPrompt, userMessages, assistantMessages);
        System.err.println("[ClaudeAPI] POST " + apiUrl + " (proxy=" + useProxy + ", bodyLen=" + body.length() + ", messages=" + userMessages.size() + "u/" + assistantMessages.size() + "a)");
        HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("content-type", "application/json")
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (!useProxy) {
            reqBuilder.header("x-api-key", apiKey);
            reqBuilder.header("anthropic-version", ANTHROPIC_VERSION);
        }
        HttpResponse<String> response = http.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("[ClaudeAPI] ERROR " + response.statusCode() + ": " + response.body());
            System.err.println("[ClaudeAPI] Request body (first 500 chars): " + body.substring(0, Math.min(500, body.length())));
            throw new RuntimeException("Claude API error " + response.statusCode() + ": " + response.body());
        }
        return extractTextFromResponse(response.body());
    }

    /** Builds the JSON body. Proxy mode uses {systemPrompt, messages} to match the Lambda. Direct mode uses {system, model, max_tokens, messages} for Anthropic. */
    private String buildConversationBody(String systemPrompt, List<String> userMessages, List<String> assistantMessages) {
        StringBuilder messages = new StringBuilder();
        messages.append("[");
        boolean first = true;
        int n = Math.min(userMessages.size(), assistantMessages.size());
        for (int i = 0; i < n; i++) {
            if (!first) messages.append(",");
            messages.append("{\"role\":\"user\",\"content\":\"").append(escapeJson(userMessages.get(i))).append("\"},");
            messages.append("{\"role\":\"assistant\",\"content\":\"").append(escapeJson(assistantMessages.get(i))).append("\"}");
            first = false;
        }
        if (userMessages.size() > assistantMessages.size()) {
            if (!first) messages.append(",");
            messages.append("{\"role\":\"user\",\"content\":\"").append(escapeJson(userMessages.get(userMessages.size() - 1))).append("\"}");
        }
        messages.append("]");

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (useProxy) {
            if (systemPrompt != null && !systemPrompt.isBlank())
                sb.append("\"system\":\"").append(escapeJson(systemPrompt)).append("\",");
            sb.append("\"max_tokens\":").append(MAX_RESPONSE_TOKENS).append(",");
            sb.append("\"messages\":").append(messages);
        } else {
            if (systemPrompt != null && !systemPrompt.isBlank())
                sb.append("\"system\":\"").append(escapeJson(systemPrompt)).append("\",");
            sb.append("\"model\":\"").append(MODEL).append("\",");
            sb.append("\"max_tokens\":").append(MAX_RESPONSE_TOKENS).append(",");
            sb.append("\"messages\":").append(messages);
        }
        sb.append("}");
        return sb.toString();
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
