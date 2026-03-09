package Model.utilz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads KEY=value pairs from env/.env (relative to user.dir).
 * Used for API keys; env/ is gitignored.
 */
public class EnvLoader {
    private static final String ENV_DIR = "env";
    private static final String ENV_FILE = ".env";
    private static final String KEY_ANTHROPIC = "ANTHROPIC_API_KEY";
    private static final String KEY_CLAUDE_URL = "CLAUDE_API_URL";

    private static Map<String, String> cache;

    public static String getAnthropicApiKey() {
        return getEnv().get(KEY_ANTHROPIC);
    }

    /** Returns CLAUDE_API_URL if set (Lambda proxy), otherwise null. */
    public static String getClaudeApiUrl() {
        return getEnv().get(KEY_CLAUDE_URL);
    }

    public static Map<String, String> getEnv() {
        if (cache != null) return cache;
        cache = new HashMap<>();
        Path envPath = Path.of(System.getProperty("user.dir"), ENV_DIR, ENV_FILE);
        if (!Files.isRegularFile(envPath)) return cache;
        try {
            for (String line : Files.readAllLines(envPath)) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq <= 0) continue;
                String key = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();
                if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2)
                    value = value.substring(1, value.length() - 1);
                cache.put(key, value);
            }
        } catch (IOException ignored) { }
        return cache;
    }
}
