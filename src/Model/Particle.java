package Model;

/** Plain data for a particle (e.g. firefly). No methods — logic is inline where particles are updated and drawn. */
public class Particle {
    public float x, y, life, maxLife;
    /** Phase offset (radians) for sine-wave position drift and alpha pulse. */
    public float phase;
    /** Separate phase offset for the alpha glow pulse (randomised so fireflies blink at different times). */
    public float glowPhase;
    /** Spawn anchor; particle oscillates around this point. Slowly drifts upward. */
    public float spawnX, spawnY;
    /** Original spawn position — used to detect when the firefly has drifted far enough to respawn. */
    public float origX, origY;
    /** Slow drift velocity (world pixels/sec). Carries the anchor upward with a slight angle. */
    public float driftVx, driftVy;
    /** Per-particle animation frame offset (ms) so fireflies don't all flash in sync. */
    public long aniOffset;
    /** Row in spritesheet: 0 = simple firefly, 1 = larger. */
    public int row;
    public boolean dead;
}
