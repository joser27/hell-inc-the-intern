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
 */
public class LevelLoader {
    private static final String TMX_PATH = "res/tiled/mapTMX.tmx";
    private static final String TILESET_DIR = "res/tiled/";
    private static final int TILESET_TILE_SIZE = 16;
    private static final int FIRST_GID = 1;

    /** Collision map: 0 = walkable, 1 = solid. Built from Tiled "collision" layer. */
    public static int[][] world;

    private int mapWidth;
    private int mapHeight;
    private int tileWidth;
    private int tileHeight;
    private BufferedImage tilesetImage;
    private int tilesetColumns;
    private Map<String, int[][]> layersByName = new LinkedHashMap<>();
    private List<String> drawLayerOrder = new ArrayList<>();
    private final List<Trigger> triggers = new ArrayList<>();
    /** Scaled tile cache: gid -> 48x48 image (or null for empty). */
    private final Map<Integer, Image> tileImageCache = new HashMap<>();

    public LevelLoader() {
        try {
            loadTmx();
            loadTileset();
            buildCollisionFromLayer();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Tiled level: " + TMX_PATH, e);
        }
    }

    /** Tries classpath first, then file from working dir (project root when run from IDE/terminal). */
    private static InputStream openResource(String path) {
        InputStream is = LevelLoader.class.getResourceAsStream("/" + path);
        if (is != null) return is;
        is = LevelLoader.class.getClassLoader().getResourceAsStream(path);
        if (is != null) return is;
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

        var tileset = (org.w3c.dom.Element) map.getElementsByTagName("tileset").item(0);
        String tsxSource = tileset.getAttribute("source");
        loadTilesetFromTsx(TILESET_DIR + tsxSource);

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
        } finally {
            try { if (is != null) is.close(); } catch (IOException ignored) { }
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
                float x = Float.parseFloat(obj.getAttribute("x")) * scale;
                float y = Float.parseFloat(obj.getAttribute("y")) * scale;
                float w = Float.parseFloat(obj.getAttribute("width")) * scale;
                float h = Float.parseFloat(obj.getAttribute("height")) * scale;
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

    private void loadTilesetFromTsx(String tsxPath) throws Exception {
        InputStream is = openResource(tsxPath);
        if (is == null) throw new RuntimeException("TSX not found: " + tsxPath);
        try {
            var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            doc.getDocumentElement().normalize();
            var tileset = doc.getDocumentElement();
            tilesetColumns = Integer.parseInt(tileset.getAttribute("columns"));
            var imageEl = (org.w3c.dom.Element) tileset.getElementsByTagName("image").item(0);
            String imageSource = imageEl.getAttribute("source");
            String imagePath = resolveImagePath(tsxPath, imageSource);
            loadTilesetImage(imagePath);
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

    private void loadTilesetImage(String imagePath) throws Exception {
        InputStream is = openResource(imagePath);
        if (is == null) {
            is = openResource("res/tiled/Overview.png");
        }
        if (is == null)
            throw new RuntimeException("Tileset image not found: " + imagePath + " or res/tiled/Overview.png (add Pixel Kingdom tileset image as in TSX)");
        try {
            tilesetImage = ImageIO.read(is);
        } finally {
            try { if (is != null) is.close(); } catch (IOException ignored) { }
        }
    }

    private void loadTileset() {
        if (tilesetImage == null) return;
        // Already loaded in loadTilesetFromTsx
    }

    /** Builds collision map from Tiled "collision" layer (non-zero GID = solid). */
    private void buildCollisionFromLayer() {
        int[][] collisionLayer = layersByName.get("collision");
        if (collisionLayer == null) {
            world = new int[mapHeight][mapWidth];
            return;
        }
        world = new int[mapHeight][mapWidth];
        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                int gid = collisionLayer[i][j] & 0x1FFFFFFF;
                world[i][j] = (gid != 0) ? 1 : 0;
            }
        }
    }

    /** Returns tile image for the given GID, scaled to TILE_SIZE (48). Returns null for empty (0 or invalid). */
    public Image getTileImage(int gid) {
        int raw = gid & 0x1FFFFFFF;
        if (raw == 0) return null;
        int localId = raw - FIRST_GID;
        if (localId < 0 || tilesetImage == null) return null;
        return tileImageCache.computeIfAbsent(gid, k -> {
            int col = localId % tilesetColumns;
            int row = localId / tilesetColumns;
            int x = col * TILESET_TILE_SIZE;
            int y = row * TILESET_TILE_SIZE;
            if (x + TILESET_TILE_SIZE > tilesetImage.getWidth() || y + TILESET_TILE_SIZE > tilesetImage.getHeight())
                return null;
            BufferedImage sub = tilesetImage.getSubimage(x, y, TILESET_TILE_SIZE, TILESET_TILE_SIZE);
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
}
