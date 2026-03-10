package Model;

import java.awt.geom.Rectangle2D;

/** A readable zone from Tiled objectgroup "readables". Bounds in game pixels; property "text" (required), optional "title". */
public class Readable {
    private final float x, y, width, height;
    private final String text;
    private final String title;

    public Readable(float x, float y, float width, float height, String text, String title) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text != null ? text : "";
        this.title = title != null && !title.isEmpty() ? title : null;
    }

    public boolean intersects(Rectangle2D.Float box) {
        return box.intersects(x, y, width, height);
    }

    public String getText() { return text; }
    public String getTitle() { return title; }
}
