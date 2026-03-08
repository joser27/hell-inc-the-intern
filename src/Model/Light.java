package Model;

/**
 * A light source from Tiled objectgroup "lights". Center and radius in game pixels (world space).
 */
public class Light {
    private final float centerX;
    private final float centerY;
    private final float radius;

    public Light(float centerX, float centerY, float radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    public float getCenterX() { return centerX; }
    public float getCenterY() { return centerY; }
    public float getRadius() { return radius; }
}
