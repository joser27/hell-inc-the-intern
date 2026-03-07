package Model;

import java.awt.geom.Rectangle2D;

/** A trigger zone from Tiled objectgroup "triggers". Bounds in game pixels. */
public class Trigger {
    private final float x, y, width, height;
    private final String npcId;

    public Trigger(float x, float y, float width, float height, String npcId) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.npcId = npcId != null ? npcId : "";
    }

    public boolean intersects(Rectangle2D.Float box) {
        return box.intersects(x, y, width, height);
    }

    public String getNpcId() {
        return npcId;
    }
}
