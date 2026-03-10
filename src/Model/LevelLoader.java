package Model;

import Controller.GameController;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * Loads level from Tiled TMX map (res/tiled/mapTMX.tmx).
 * Uses "collision" layer for collision (non-zero GID = solid). Exposes tile layers for rendering.
 * Tiles are 16x16 in the tileset; returned tile images are scaled to TILE_SIZE (48) for the game.
 * Supports multiple tilesets (e.g. Pixel Kingdom + Grasslands Tileset & Objects); each has firstGid, image, columns.
 */
public class LevelLoader {
    private static final String TMX_PATH = "res/tiled/mapTMX.tmx";
    private static final String TILESET_DIR = "res/tiled/";
    private static final int TILESET_TILE_SIZE = 16;

    /** One tileset: first GID in the map, image strip, and column count (tiles per row). */
    private static final class TilesetInfo {
        final int firstGid;
        final BufferedImage image;
        final int columns;

        TilesetInfo(int firstGid, BufferedImage image, int columns) {
            this.firstGid = firstGid;
            this.image = image;
            this.columns = columns;
        }
    }

    /** Collision map: 0 = walkable, 1 = solid. Built from Tiled "collision" layer. */
    public static int[][] world;

    private int mapWidth;
    private int mapHeight;
    private int tileWidth;
    private int tileHeight;
    /** All tilesets in TMX order (firstGid ascending). Used to resolve GID to tile image. */
    private final List<TilesetInfo> tilesets = new ArrayList<>();
    private Map<String, int[][]> layersByName = new LinkedHashMap<>();
    private List<String> drawLayerOrder = new ArrayList<>();
    private final List<Trigger> triggers = new ArrayList<>();
    private final List<Light> lights = new ArrayList<>();
    /** Collision rectangles from objectgroup "collision" (game pixels). Merged into world[][] with tile-layer collision. */
    private final List<Rectangle> collisionObjects = new ArrayList<>();
    /** VFX spawn points from objectgroup "vfx" with effect=firefly (x,y in game pixels). */
    private final List<float[]> fireflySpawns = new ArrayList<>();
    /** Ground decorations (plants, mushrooms, bushes) from objectgroup "plants": row/col pick tile from Tileset & Objects (e.g. row 8, col 0–39). */
    private final List<Decoration> decorations = new ArrayList<>();
    /** Readable zones from objectgroup "readables": property "text" (required), optional "title". */
    private final List<Readable> readables = new ArrayList<>();
    /** Scaled tile cache: gid -> 48x48 image (or null for empty). */
    private final Map<Integer, Image> tileImageCache = new HashMap<>();

    /** One placed decoration: position in game pixels; bottom tile (row 8) and optional top tile (row 7) for tall plants. */
    public static final class Decoration {
        public final int x;
        public final int y;
        /** GID for bottom tile (row 8). */
        public final int gid;
        /** GID for top tile (row 7), or 0 to draw only the bottom tile. */
        public final int gidTop;

        public Decoration(int x, int y, int gid, int gidTop) {
            this.x = x;
            this.y = y;
            this.gid = gid;
            this.gidTop = gidTop;
        }
    }

    public LevelLoader() {
        try {
            loadTmx();
            buildCollisionFromLayer();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Tiled level: " + TMX_PATH, e);
        }
    }

    /** Tries classpath first, then file from working dir. In JAR, res/ is at classpath root (no "res/" prefix). */
    private static InputStream openResource(String path) {
        InputStream is = LevelLoader.class.getResourceAsStream("/" + path);
        if (is != null) return is;
        is = LevelLoader.class.getClassLoader().getResourceAsStream(path);
        if (is != null) return is;
        // Maven puts res/ contents at classpath root, so try without "res/" prefix (for JAR / jpackage exe)
        if (path.startsWith("res/")) {
            String classpathPath = path.substring(4);
            is = LevelLoader.class.getResourceAsStream("/" + classpathPath);
            if (is != null) return is;
            is = LevelLoader.class.getClassLoader().getResourceAsStream(classpathPath);
            if (is != null) return is;
        }
        File f = new File(System.getProperty("user.dir"), path);
        if (f.isFile()) {
            try {
                return new FileInputStream(f);
            } catch (IOException ignored) { }
        }
        // Windows path variant
        String winPath = path.replace('/', File.separatorChar);
        if (!winPath.equals(path)) {
            f = new File(System.getProperty("user.dir"), winPath);
            if (f.isFile()) {
                try {
                    return new FileInputStream(f);
                } catch (IOException ignored) { }
            }
        }
        return null;
    }

    private void loadTmx() throws Exception {
        InputStream is = openResource(TMX_PATH);
        if (is == null) throw new RuntimeException("TMX not found: " + TMX_PATH + " (tried classpath and res/tiled/ from working dir)");
        try {
            var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            doc.getDocumentElement().normalize();

        var map = doc.getDocumentElement();
        mapWidth = Integer.parseInt(map.getAttribute("width"));
        mapHeight = Integer.parseInt(map.getAttribute("height"));
        tileWidth = Integer.parseInt(map.getAttribute("tilewidth"));
        tileHeight = Integer.parseInt(map.getAttribute("tileheight"));

        var tilesetNodes = map.getElementsByTagName("tileset");
        for (int i = 0; i < tilesetNodes.getLength(); i++) {
            var tileset = (org.w3c.dom.Element) tilesetNodes.item(i);
            int firstGid = Integer.parseInt(tileset.getAttribute("firstgid"));
            String tsxSource = tileset.getAttribute("source");
            TilesetInfo info = loadTilesetFromTsx(TILESET_DIR + tsxSource, firstGid);
            tilesets.add(info);
        }

        var layerNodes = map.getElementsByTagName("layer");
        for (int i = 0; i < layerNodes.getLength(); i++) {
            var layerEl = (org.w3c.dom.Element) layerNodes.item(i);
            String name = layerEl.getAttribute("name");
            int w = Integer.parseInt(layerEl.getAttribute("width"));
            int h = Integer.parseInt(layerEl.getAttribute("height"));
            var dataEl = (org.w3c.dom.Element) layerEl.getElementsByTagName("data").item(0);
            String encoding = dataEl.getAttribute("encoding");
            if (!"csv".equalsIgnoreCase(encoding)) continue;
            String csv = dataEl.getTextContent().trim();
            int[][] gids = parseCsvData(csv, w, h);
            layersByName.put(name, gids);
            if (!"collision".equals(name))  // collision layer is for logic only, not drawn
                drawLayerOrder.add(name);
        }
        loadTriggers(map);
        loadLights(map);
        loadCollisionObjects(map);
        loadVfxSpawns(map);
        loadPlants(map);
        loadReadables(map);
        } finally {
            try { if (is != null) is.close(); } catch (IOException ignored) { }
        }
    }

    private static float parseFloatAttr(org.w3c.dom.Element el, String attr, float defaultValue) {
        String s = el.getAttribute(attr);
        if (s == null || s.isEmpty()) return defaultValue;
        try {
            return Float.parseFloat(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** Parse objectgroup "triggers": each object's x,y,w,h (Tiled pixels) scaled to game pixels; property npc_id. */
    private void loadTriggers(org.w3c.dom.Element map) {
        float scale = (float) GameController.TILE_SIZE / tileWidth;
        var objectGroups = map.getElementsByTagName("objectgroup");
        for (int g = 0; g < objectGroups.getLength(); g++) {
            var og = (org.w3c.dom.Element) objectGroups.item(g);
            if (!"triggers".equals(og.getAttribute("name"))) continue;
            var objects = og.getElementsByTagName("object");
            for (int i = 0; i < objects.getLength(); i++) {
                var obj = (org.w3c.dom.Element) objects.item(i);
                float x = parseFloatAttr(obj, "x", 0) * scale;
                float y = parseFloatAttr(obj, "y", 0) * scale;
                float w = parseFloatAttr(obj, "width", 0) * scale;
                float h = parseFloatAttr(obj, "height", 0) * scale;
                if (w <= 0 || h <= 0) continue;
                String npcId = null;
                var propList = obj.getElementsByTagName("property");
                for (int p = 0; p < propList.getLength(); p++) {
                    var prop = (org.w3c.dom.Element) propList.item(p);
                    if ("npc_id".equals(prop.getAttribute("name"))) {
                        npcId = prop.getAttribute("value");
                        break;
                    }
                }
                triggers.add(new Trigger(x, y, w, h, npcId));
            }
            break;
        }
    }

    /** Parse objectgroup "lights": each object's center (x+width/2, y+height/2) and radius in game pixels. Optional property "radius" (Tiled pixels). */
    private void loadLights(org.w3c.dom.Element map) {
        float scale = (float) GameController.TILE_SIZE / tileWidth;
        var objectGroups = map.getElementsByTagName("objectgroup");
        for (int g = 0; g < objectGroups.getLength(); g++) {
            var og = (org.w3c.dom.Element) objectGroups.item(g);
            if (!"lights".equals(og.getAttribute("name"))) continue;
            var objects = og.getElementsByTagName("object");
            for (int i = 0; i < objects.getLength(); i++) {
                var obj = (org.w3c.dom.Element) objects.item(i);
                float tx = Float.parseFloat(obj.getAttribute("x"));
                float ty = Float.parseFloat(obj.getAttribute("y"));
                float tw = obj.hasAttribute("width") ? Float.parseFloat(obj.getAttribute("width")) : 0;
                float th = obj.hasAttribute("height") ? Float.parseFloat(obj.getAttribute("height")) : 0;
                float centerX = (tx + tw * 0.5f) * scale;
                float centerY = (ty + th * 0.5f) * scale;
                Float radiusTiled = null;
                var propList = obj.getElementsByTagName("property");
                for (int p = 0; p < propList.getLength(); p++) {
                    var prop = (org.w3c.dom.Element) propList.item(p);
                    if ("radius".equals(prop.getAttribute("name"))) {
                        try {
                            radiusTiled = Float.parseFloat(prop.getAttribute("value"));
                        } catch (NumberFormatException ignored) { }
                        break;
                    }
                }
                float radius = radiusTiled != null
                        ? radiusTiled * scale
                        : Math.max(GameController.TILE_SIZE * 5f, Math.max(tw, th) * 0.5f * scale);
                if (radius <= 0) radius = GameController.TILE_SIZE * 5f;
                lights.add(new Light(centerX, centerY, radius));
            }
            break;
        }
    }

    /** Parse objectgroup "vfx": objects with property effect=firefly; store center (x,y) in game pixels as spawn points. */
    private void loadVfxSpawns(org.w3c.dom.Element map) {
        float scale = (float) GameController.TILE_SIZE / tileWidth;
        var objectGroups = map.getElementsByTagName("objectgroup");
        for (int g = 0; g < objectGroups.getLength(); g++) {
            var og = (org.w3c.dom.Element) objectGroups.item(g);
            if (!"vfx".equals(og.getAttribute("name"))) continue;
            var objects = og.getElementsByTagName("object");
            for (int i = 0; i < objects.getLength(); i++) {
                var obj = (org.w3c.dom.Element) objects.item(i);
                String effect = null;
                var propList = obj.getElementsByTagName("property");
                for (int p = 0; p < propList.getLength(); p++) {
                    var prop = (org.w3c.dom.Element) propList.item(p);
                    if ("effect".equals(prop.getAttribute("name"))) {
                        effect = prop.getAttribute("value");
                        break;
                    }
                }
                if (!"firefly".equals(effect)) continue;
                float tx = Float.parseFloat(obj.getAttribute("x"));
                float ty = Float.parseFloat(obj.getAttribute("y"));
                float tw = obj.hasAttribute("width") ? Float.parseFloat(obj.getAttribute("width")) : 0;
                float th = obj.hasAttribute("height") ? Float.parseFloat(obj.getAttribute("height")) : 0;
                float x = (tx + tw * 0.5f) * scale;
                float y = (ty + th * 0.5f) * scale;
                fireflySpawns.add(new float[]{x, y});
            }
            break;
        }
    }

    /** Parse objectgroup "plants": each object has x,y (Tiled pixels) and optional properties "row" (default 8), "col" (default 0).
     * Row/col index into the second tileset (Tileset & Objects.png), e.g. row 8, col 0–39 for grass/mushrooms/bushes. */
    private void loadPlants(org.w3c.dom.Element map) {
        if (tilesets.size() < 2) return;
        TilesetInfo objectsTileset = tilesets.get(1);
        float scale = (float) GameController.TILE_SIZE / tileWidth;
        var objectGroups = map.getElementsByTagName("objectgroup");
        for (int g = 0; g < objectGroups.getLength(); g++) {
            var og = (org.w3c.dom.Element) objectGroups.item(g);
            if (!"plants".equals(og.getAttribute("name"))) continue;
            var objects = og.getElementsByTagName("object");
            for (int i = 0; i < objects.getLength(); i++) {
                var obj = (org.w3c.dom.Element) objects.item(i);
                int row = 8;
                int col = 0;
                var propList = obj.getElementsByTagName("property");
                for (int p = 0; p < propList.getLength(); p++) {
                    var prop = (org.w3c.dom.Element) propList.item(p);
                    String name = prop.getAttribute("name");
                    String value = prop.getAttribute("value");
                    if ("row".equals(name)) row = parseInt(value, 8);
                    else if ("col".equals(name)) col = parseInt(value, 0);
                }
                float tx = Float.parseFloat(obj.getAttribute("x"));
                float ty = Float.parseFloat(obj.getAttribute("y"));
                int gameX = (int) (tx * scale);
                int gameY = (int) (ty * scale);
                int gidBottom = objectsTileset.firstGid + 8 * objectsTileset.columns + col;
                int gidTop = objectsTileset.firstGid + 7 * objectsTileset.columns + col;
                decorations.add(new Decoration(gameX, gameY, gidBottom, gidTop));
            }
            break;
        }
    }

    private static int parseInt(String s, int defaultValue) {
        if (s == null || s.isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** Parse objectgroup "readables": each object has x,y,width,height (Tiled pixels) and properties "text" (required), optional "title". Objects with no text are skipped. */
    private void loadReadables(org.w3c.dom.Element map) {
        float scale = (float) GameController.TILE_SIZE / tileWidth;
        var objectGroups = map.getElementsByTagName("objectgroup");
        for (int g = 0; g < objectGroups.getLength(); g++) {
            var og = (org.w3c.dom.Element) objectGroups.item(g);
            if (!"readables".equals(og.getAttribute("name"))) continue;
            var objects = og.getElementsByTagName("object");
            for (int i = 0; i < objects.getLength(); i++) {
                var obj = (org.w3c.dom.Element) objects.item(i);
                float x = parseFloatAttr(obj, "x", 0) * scale;
                float y = parseFloatAttr(obj, "y", 0) * scale;
                float w = parseFloatAttr(obj, "width", 0) * scale;
                float h = parseFloatAttr(obj, "height", 0) * scale;
                if (w <= 0 || h <= 0) continue;
                String text = null;
                String title = null;
                var propList = obj.getElementsByTagName("property");
                for (int p = 0; p < propList.getLength(); p++) {
                    var prop = (org.w3c.dom.Element) propList.item(p);
                    String name = prop.getAttribute("name");
                    String value = prop.getAttribute("value");
                    if ("text".equals(name)) text = value != null ? value : "";
                    else if ("title".equals(name)) title = value;
                }
                if (text == null || text.isEmpty()) continue;
                readables.add(new Readable(x, y, w, h, text, title));
            }
            break;
        }
    }

    /** Parse objectgroup "collision": each object's x,y,width,height (Tiled pixels) scaled to game pixels. Used to mark tiles solid in buildCollisionFromLayer. */
    private void loadCollisionObjects(org.w3c.dom.Element map) {
        float scale = (float) GameController.TILE_SIZE / tileWidth;
        var objectGroups = map.getElementsByTagName("objectgroup");
        for (int g = 0; g < objectGroups.getLength(); g++) {
            var og = (org.w3c.dom.Element) objectGroups.item(g);
            if (!"collision".equals(og.getAttribute("name"))) continue;
            var objects = og.getElementsByTagName("object");
            for (int i = 0; i < objects.getLength(); i++) {
                var obj = (org.w3c.dom.Element) objects.item(i);
                float x = Float.parseFloat(obj.getAttribute("x")) * scale;
                float y = Float.parseFloat(obj.getAttribute("y")) * scale;
                float w = (obj.hasAttribute("width") ? Float.parseFloat(obj.getAttribute("width")) : 0) * scale;
                float h = (obj.hasAttribute("height") ? Float.parseFloat(obj.getAttribute("height")) : 0) * scale;
                if (w > 0 && h > 0)
                    collisionObjects.add(new Rectangle((int) x, (int) y, (int) w, (int) h));
            }
            break;
        }
    }

    private static int[][] parseCsvData(String csv, int width, int height) {
        String[] rows = csv.split("\\s*\\n\\s*");
        int[][] out = new int[height][width];
        for (int row = 0; row < height && row < rows.length; row++) {
            String[] cells = rows[row].trim().split("\\s*,\\s*");
            for (int col = 0; col < width && col < cells.length; col++) {
                try {
                    out[row][col] = Integer.parseInt(cells[col].trim());
                } catch (NumberFormatException e) {
                    out[row][col] = 0;
                }
            }
        }
        return out;
    }

    private TilesetInfo loadTilesetFromTsx(String tsxPath, int firstGid) throws Exception {
        InputStream is = openResource(tsxPath);
        if (is == null) throw new RuntimeException("TSX not found: " + tsxPath);
        try {
            var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            doc.getDocumentElement().normalize();
            var tileset = doc.getDocumentElement();
            int columns = Integer.parseInt(tileset.getAttribute("columns"));
            var imageEl = (org.w3c.dom.Element) tileset.getElementsByTagName("image").item(0);
            String imageSource = imageEl.getAttribute("source");
            String imagePath = resolveImagePath(tsxPath, imageSource);
            BufferedImage img = loadTilesetImage(imagePath);
            return new TilesetInfo(firstGid, img, columns);
        } finally {
            try { if (is != null) is.close(); } catch (IOException ignored) { }
        }
    }

    private static String resolveImagePath(String tsxPath, String imageSource) {
        // tsxPath like "res/tiled/Pixel Kingdom.tsx", imageSource like "../paid/Pixel Kingdom - Town Tale/Overview.png"
        int lastSlash = tsxPath.lastIndexOf('/');
        String baseDir = lastSlash >= 0 ? tsxPath.substring(0, lastSlash + 1) : "";
        Deque<String> baseParts = new ArrayDeque<>(Arrays.asList(baseDir.split("/")));
        baseParts.removeIf(s -> s.isEmpty());
        for (String part : imageSource.split("/")) {
            if ("..".equals(part)) {
                if (!baseParts.isEmpty()) baseParts.removeLast();
            } else if (!part.isEmpty() && !".".equals(part)) {
                baseParts.add(part);
            }
        }
        return String.join("/", baseParts);
    }

    private BufferedImage loadTilesetImage(String imagePath) throws Exception {
        InputStream is = openResource(imagePath);
        if (is == null && !imagePath.startsWith("res/")) {
            is = openResource("res/" + imagePath);
        }
        if (is == null) {
            is = openResource("res/tiled/Overview.png");
        }
        if (is == null)
            throw new RuntimeException("Tileset image not found: " + imagePath + " (add tileset image as in TSX, e.g. res/paid/...)");
        try {
            return ImageIO.read(is);
        } finally {
            try { if (is != null) is.close(); } catch (IOException ignored) { }
        }
    }

    /** Builds collision map from Tiled tile layer "collision" (non-zero GID = solid). Object-layer collision is handled separately via getCollisionRects(). */
    private void buildCollisionFromLayer() {
        world = new int[mapHeight][mapWidth];
        int[][] collisionLayer = layersByName.get("collision");
        if (collisionLayer != null) {
            for (int i = 0; i < mapHeight; i++) {
                for (int j = 0; j < mapWidth; j++) {
                    int gid = collisionLayer[i][j] & 0x1FFFFFFF;
                    world[i][j] = (gid != 0) ? 1 : 0;
                }
            }
        }
    }

    /** Pixel-precise collision rectangles from the objectgroup "collision" layer (game pixels). */
    public List<Rectangle> getCollisionRects() {
        return Collections.unmodifiableList(collisionObjects);
    }

    /** Returns tile image for the given GID, scaled to TILE_SIZE (48). Returns null for empty (0 or invalid). */
    public Image getTileImage(int gid) {
        int raw = gid & 0x1FFFFFFF;
        if (raw == 0) return null;
        TilesetInfo info = null;
        for (TilesetInfo t : tilesets) {
            if (t.firstGid <= raw) info = t;
            else break;
        }
        if (info == null) return null;
        int localId = raw - info.firstGid;
        if (localId < 0) return null;
        final TilesetInfo tilesetInfo = info;
        final int localTileId = localId;
        return tileImageCache.computeIfAbsent(gid, k -> {
            int col = localTileId % tilesetInfo.columns;
            int row = localTileId / tilesetInfo.columns;
            int x = col * TILESET_TILE_SIZE;
            int y = row * TILESET_TILE_SIZE;
            if (x + TILESET_TILE_SIZE > tilesetInfo.image.getWidth() || y + TILESET_TILE_SIZE > tilesetInfo.image.getHeight())
                return null;
            BufferedImage sub = tilesetInfo.image.getSubimage(x, y, TILESET_TILE_SIZE, TILESET_TILE_SIZE);
            int size = GameController.TILE_SIZE;
            BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = scaled.createGraphics();
            g.drawImage(sub, 0, 0, size, size, null);
            g.dispose();
            return scaled;
        });
    }

    public int[][] getWorld() {
        return world;
    }

    public int getColumns() {
        return mapWidth;
    }

    public int getRows() {
        return mapHeight;
    }

    /** Layer names in draw order (as in TMX). */
    public List<String> getDrawLayerNames() {
        return Collections.unmodifiableList(drawLayerOrder);
    }

    /** GID grid for a layer (row, col). Returns null if layer missing. */
    public int[][] getLayerGids(String layerName) {
        return layersByName.get(layerName);
    }

    /** Level width in pixels (tiles * TILE_SIZE). */
    public int getLevelWidthPixels() {
        return mapWidth * GameController.TILE_SIZE;
    }

    /** Level height in pixels (tiles * TILE_SIZE). */
    public int getLevelHeightPixels() {
        return mapHeight * GameController.TILE_SIZE;
    }

    /** Triggers from Tiled objectgroup "triggers" (bounds in game pixels). */
    public List<Trigger> getTriggers() {
        return Collections.unmodifiableList(triggers);
    }

    /** Lights from Tiled objectgroup "lights" (center and radius in game pixels). */
    public List<Light> getLights() {
        return Collections.unmodifiableList(lights);
    }

    /** Firefly spawn points from Tiled objectgroup "vfx" with effect=firefly (x,y in game pixels). */
    public List<float[]> getFireflySpawns() {
        return Collections.unmodifiableList(fireflySpawns);
    }

    /** Decorations from objectgroup "plants" (row/col from Tileset & Objects), for Y-sorted drawing. */
    public List<Decoration> getDecorations() {
        return Collections.unmodifiableList(decorations);
    }

    /** Readable zones from objectgroup "readables" (Press E to read). */
    public List<Readable> getReadables() {
        return Collections.unmodifiableList(readables);
    }
}
