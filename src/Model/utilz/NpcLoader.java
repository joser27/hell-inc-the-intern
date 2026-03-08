package Model.utilz;

import Model.NpcProfile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads NPC profiles from res/npcs.json. Uses simple parsing (no external JSON lib).
 * Tiled trigger npc_id should match the "id" field (e.g. widow, nosy_neighbor).
 */
public class NpcLoader {
    private static final String NPC_JSON_PATH = "npcs.json";
    private static List<NpcProfile> cache;

    public static List<NpcProfile> load() {
        if (cache != null) return cache;
        String json = readResource();
        if (json == null || json.isBlank()) {
            cache = List.of();
            return cache;
        }
        cache = parseNpcs(json);
        return cache;
    }

    public static NpcProfile getById(String npcId) {
        if (npcId == null) return null;
        for (NpcProfile n : load()) {
            if (npcId.equals(n.getId())) return n;
        }
        return null;
    }

    private static String readResource() {
        // res/ is java-resource folder: file is at classpath root as npcs.json
        InputStream stream = NpcLoader.class.getResourceAsStream("/" + NPC_JSON_PATH);
        if (stream == null) stream = NpcLoader.class.getClassLoader().getResourceAsStream(NPC_JSON_PATH);
        if (stream != null) {
            try {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                return null;
            }
        }
        Path p = Path.of(System.getProperty("user.dir"), "res", NPC_JSON_PATH);
        if (Files.isRegularFile(p)) {
            try {
                return Files.readString(p);
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private static List<NpcProfile> parseNpcs(String json) {
        List<NpcProfile> out = new ArrayList<>();
        // Find each object in "npcs": [ { ... }, { ... } ]
        int arrayStart = json.indexOf("\"npcs\"");
        if (arrayStart < 0) return out;
        int bracket = json.indexOf('[', arrayStart);
        if (bracket < 0) return out;
        int i = bracket + 1;
        while (i < json.length()) {
            int objStart = json.indexOf('{', i);
            if (objStart < 0) break;
            int objEnd = matchingBrace(json, objStart);
            if (objEnd < 0) break;
            String block = json.substring(objStart, objEnd + 1);
            NpcProfile p = parseOne(block);
            if (p != null) out.add(p);
            i = objEnd + 1;
        }
        return out;
    }

    private static int matchingBrace(String s, int openIdx) {
        int depth = 1;
        boolean inString = false;
        char stringChar = 0;
        boolean escape = false;
        for (int i = openIdx + 1; i < s.length(); i++) {
            char c = s.charAt(i);
            if (escape) { escape = false; continue; }
            if (c == '\\' && inString) { escape = true; continue; }
            if (!inString) {
                if (c == '"' || c == '\'') { inString = true; stringChar = c; continue; }
                if (c == '{') depth++;
                else if (c == '}') { depth--; if (depth == 0) return i; }
                continue;
            }
            if (c == stringChar) inString = false;
        }
        return -1;
    }

    private static NpcProfile parseOne(String block) {
        String id = extractString(block, "id");
        if (id == null) return null;
        String frameImage = extractString(block, "frameImage");
        String displayName = extractString(block, "displayName");
        if (displayName == null) displayName = id;
        String systemPrompt = extractStringLong(block, "systemPrompt");
        if (systemPrompt == null) systemPrompt = "";
        int tattleChance = extractInt(block, "tattleChance", 30);
        return new NpcProfile(id, frameImage, displayName, systemPrompt, tattleChance);
    }

    private static int extractInt(String block, String key, int defaultValue) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(-?\\d+)");
        Matcher m = p.matcher(block);
        return m.find() ? Integer.parseInt(m.group(1).trim()) : defaultValue;
    }

    private static String extractString(String block, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher m = p.matcher(block);
        return m.find() ? unescape(m.group(1)) : null;
    }

    private static String extractStringLong(String block, String key) {
        String prefix = "\"" + key + "\"\\s*:\\s*\"";
        int idx = block.indexOf(prefix);
        if (idx < 0) return null;
        int start = idx + prefix.length();
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < block.length(); i++) {
            char c = block.charAt(i);
            if (c == '\\' && i + 1 < block.length()) {
                char next = block.charAt(i + 1);
                if (next == '"') { sb.append('"'); i++; continue; }
                if (next == '\\') { sb.append('\\'); i++; continue; }
                if (next == 'n') { sb.append('\n'); i++; continue; }
            }
            if (c == '"') break;
            sb.append(c);
        }
        return sb.toString();
    }

    private static String unescape(String s) {
        if (s == null) return null;
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
