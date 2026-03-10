package Model.entities;

import Controller.GameController;
import Model.Game;
import Model.utilz.LoadSave;
import Model.utilz.SoundPlayer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import static Model.utilz.Constants.PlayerConstants.*;

/** Player character (Hell Inc. intern) — idle (idle.png 4x3) and run (run.png 8x3). */
public class Player1 extends Player {

    /** Ticks per animation frame. Lower = faster (Entity default is 20). */
    private static final int ANI_SPEED = 14;
    /** Movement speed per tick (pixels). Set in Player1 only. */
    private static final float MOVEMENT_SPEED = 0.24f * GameController.SCALE;

    /** Drawn size of the run sprite (so view can align to hitbox). */
    private static final int SPRITE_DRAW_W = 80 * GameController.SCALE;
    private static final int SPRITE_DRAW_H = 80 * GameController.SCALE;

    /** run.png: row 0 = right, row 1 = down, row 2 = up. Left = right flipped. 8 frames per row. */
    private BufferedImage[][] runAnimations;
    private static final int RUN_COLS = 8;
    private static final int RUN_ROWS = 3;
    /** run.png frame size: 632/8 = 79, 237/3 = 79. idle.png same frame size 316/4 = 79. */
    private static final int RUN_FRAME_W = 79;
    private static final int RUN_FRAME_H = 79;
    /** idle.png: 4 cols x 3 rows. Row 0=right, 1=down, 2=up; 4 frames per row. */
    private static final int IDLE_COLS = 4;

    /** Idle overlay (hair etc.): same 4x3 layout, indexed like idle [0]=down,[1]=up,[2]=left,[3]=right. */
    private BufferedImage[][] idleOverlayFrames;
    /** Run overlay (hair etc.): same 8x3 layout, indices 4=down,5=up,6=left,7=right; 8 frames each. */
    private BufferedImage[][] runOverlayFrames;

    /** Run animation has 8 frames; play step when foot hits (frames 0 and 4). */
    private static final int[] STEP_ANIMATION_FRAMES = { 0, 4 };
    private int lastStepAniIndex = -1;
    /** Idle has 4 frames vs run 8; use 2x tick duration so one full idle cycle = one full run cycle. */
    private static final int IDLE_SPEED_MULTIPLIER = 2;

    @Override
    protected int getEffectiveAniSpeed() {
        return IDLE.equals(playerAction) ? aniSpeed * IDLE_SPEED_MULTIPLIER : aniSpeed;
    }

    public static int getSpriteDrawWidth() { return SPRITE_DRAW_W; }
    public static int getSpriteDrawHeight() { return SPRITE_DRAW_H; }

    public Player1(int xPos, int yPos, int width, int height, Game game) {
        super(xPos, yPos, width, height, MOVEMENT_SPEED, game);
        aniSpeed = ANI_SPEED;
        loadRunSprites();
    }

    private void loadRunSprites() {
        BufferedImage runAtlas = LoadSave.GetSpriteAtlas(LoadSave.RUN_ATLAS);
        BufferedImage idleAtlas = LoadSave.GetSpriteAtlas(LoadSave.IDLE_ATLAS);
        int drawW = SPRITE_DRAW_W;
        int drawH = SPRITE_DRAW_H;
        runAnimations = new BufferedImage[8][];

        // Idle: 4 frames per direction from idle.png (row 0=right, 1=down, 2=up; left = right flipped)
        runAnimations[0] = extractIdleRow(idleAtlas, drawW, drawH, 1); // idle down R1C0-R1C3
        runAnimations[1] = extractIdleRow(idleAtlas, drawW, drawH, 2); // idle up R2C0-R2C3
        runAnimations[2] = extractIdleRowFlipped(idleAtlas, drawW, drawH, 0); // idle left = right flipped
        runAnimations[3] = extractIdleRow(idleAtlas, drawW, drawH, 0); // idle right R0C0-R0C3
        // Run: 8 frames each from run.png
        runAnimations[4] = extractRow(runAtlas, drawW, drawH, 1, 8); // run down r1c0-c7
        runAnimations[5] = extractRow(runAtlas, drawW, drawH, 2, 8); // run up r2c0-c7
        runAnimations[6] = extractRowFlipped(runAtlas, drawW, drawH, 0, 8); // run left = run right flipped
        runAnimations[7] = extractRow(runAtlas, drawW, drawH, 0, 8); // run right r0c0-c7

        // Idle overlay (hair): same 4x3 layout as idle, drawn on top during idle only
        try {
            BufferedImage hairAtlas = LoadSave.GetSpriteAtlas(LoadSave.IDLE_HAIR_OVERLAY);
            idleOverlayFrames = new BufferedImage[4][];
            idleOverlayFrames[0] = extractIdleRow(hairAtlas, drawW, drawH, 1); // down
            idleOverlayFrames[1] = extractIdleRow(hairAtlas, drawW, drawH, 2); // up
            idleOverlayFrames[2] = extractIdleRowFlipped(hairAtlas, drawW, drawH, 0); // left
            idleOverlayFrames[3] = extractIdleRow(hairAtlas, drawW, drawH, 0); // right
        } catch (Exception ignored) {
            idleOverlayFrames = null;
        }

        // Run overlay (hair): same 8x3 layout as run, drawn on top during run only
        try {
            BufferedImage runHairAtlas = LoadSave.GetSpriteAtlas(LoadSave.RUN_HAIR_OVERLAY);
            runOverlayFrames = new BufferedImage[8][];
            runOverlayFrames[4] = extractRow(runHairAtlas, drawW, drawH, 1, 8); // run down
            runOverlayFrames[5] = extractRow(runHairAtlas, drawW, drawH, 2, 8); // run up
            runOverlayFrames[6] = extractRowFlipped(runHairAtlas, drawW, drawH, 0, 8); // run left
            runOverlayFrames[7] = extractRow(runHairAtlas, drawW, drawH, 0, 8); // run right
        } catch (Exception ignored) {
            runOverlayFrames = null;
        }
    }

    /** Idle sheet is 4 cols; frame size 79x79 (same as run). */
    private BufferedImage[] extractIdleRow(BufferedImage atlas, int drawW, int drawH, int row) {
        return extractRow(atlas, drawW, drawH, row, IDLE_COLS);
    }

    private BufferedImage[] extractIdleRowFlipped(BufferedImage atlas, int drawW, int drawH, int row) {
        return extractRowFlipped(atlas, drawW, drawH, row, IDLE_COLS);
    }

    private BufferedImage[] scaleFrame(BufferedImage atlas, int drawW, int drawH, int row, int col) {
        BufferedImage one = atlas.getSubimage(col * RUN_FRAME_W, row * RUN_FRAME_H, RUN_FRAME_W, RUN_FRAME_H);
        Image scaled = one.getScaledInstance(drawW, drawH, Image.SCALE_DEFAULT);
        BufferedImage out = new BufferedImage(drawW, drawH, BufferedImage.TYPE_INT_ARGB);
        Graphics g = out.getGraphics();
        g.drawImage(scaled, 0, 0, null);
        g.dispose();
        return new BufferedImage[]{out};
    }

    private BufferedImage[] scaleFrameFlipped(BufferedImage atlas, int drawW, int drawH, int row, int col) {
        BufferedImage one = atlas.getSubimage(col * RUN_FRAME_W, row * RUN_FRAME_H, RUN_FRAME_W, RUN_FRAME_H);
        return new BufferedImage[]{flipAndScale(one, drawW, drawH)};
    }

    private BufferedImage[] extractRow(BufferedImage atlas, int drawW, int drawH, int row, int count) {
        BufferedImage[] out = new BufferedImage[count];
        for (int c = 0; c < count; c++) {
            BufferedImage sub = atlas.getSubimage(c * RUN_FRAME_W, row * RUN_FRAME_H, RUN_FRAME_W, RUN_FRAME_H);
            Image scaled = sub.getScaledInstance(drawW, drawH, Image.SCALE_DEFAULT);
            BufferedImage b = new BufferedImage(drawW, drawH, BufferedImage.TYPE_INT_ARGB);
            Graphics g = b.getGraphics();
            g.drawImage(scaled, 0, 0, null);
            g.dispose();
            out[c] = b;
        }
        return out;
    }

    private BufferedImage[] extractRowFlipped(BufferedImage atlas, int drawW, int drawH, int row, int count) {
        BufferedImage[] out = new BufferedImage[count];
        for (int c = 0; c < count; c++) {
            BufferedImage sub = atlas.getSubimage(c * RUN_FRAME_W, row * RUN_FRAME_H, RUN_FRAME_W, RUN_FRAME_H);
            out[c] = flipAndScale(sub, drawW, drawH);
        }
        return out;
    }

    private BufferedImage flipAndScale(BufferedImage src, int drawW, int drawH) {
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage flipped = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = flipped.createGraphics();
        g2.drawImage(src, w, 0, 0, h, 0, 0, w, h, null);
        g2.dispose();
        Image scaled = flipped.getScaledInstance(drawW, drawH, Image.SCALE_DEFAULT);
        BufferedImage out = new BufferedImage(drawW, drawH, BufferedImage.TYPE_INT_ARGB);
        Graphics g = out.getGraphics();
        g.drawImage(scaled, 0, 0, null);
        g.dispose();
        return out;
    }

    public void respawn() {
        setXHitBox(13 * GameController.TILE_SIZE);
        setYHitBox(8 * GameController.TILE_SIZE);
        setHealth(100);
    }

    public void updatePos() {
        float xSpeed = 0;
        float ySpeed = 0;
        isMoving = false;
        if (isLeft()) {
            xSpeed -= getMovementSpeed();
            setFacingDir(1);
            playerAction = RUNNING_LEFT;
            isMoving = true;
        }
        if (isRight()) {
            xSpeed += getMovementSpeed();
            setFacingDir(0);
            playerAction = RUNNING_RIGHT;
            isMoving = true;
        }
        if (isDown()) {
            ySpeed += getMovementSpeed();
            setFacingDir(3);
            playerAction = RUNNING_DOWN;
            isMoving = true;
        }
        if (isUp()) {
            ySpeed -= getMovementSpeed();
            setFacingDir(2);
            playerAction = RUNNING_UP;
            isMoving = true;
        }
        if (!isMoving) {
            playerAction = IDLE;
        }
        // Normalize diagonal so speed is same as cardinal (diagonal would otherwise be ~1.41x faster)
        if (xSpeed != 0 && ySpeed != 0) {
            float len = (float) Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed);
            float scale = getMovementSpeed() / len;
            xSpeed *= scale;
            ySpeed *= scale;
        }
        game.getCollisionChecker().handleCollision(this, game.getEntities(), game.getLevelLoader().getWorld(), GameController.TILE_SIZE, game.getLevelLoader().getCollisionRects(), xSpeed, ySpeed);
    }

    public void update() {
        if (hasSlowEffect) {
            if (!hasSetSlowEffect) {
                setMovementSpeed(getBaseMovementSpeed() / 4);
                hasSetSlowEffect = true;
            }
            slowEffectTick++;
            if (slowEffectTick >= slowEffectCD) {
                setMovementSpeed(getBaseMovementSpeed());
                hasSlowEffect = false;
                hasSetSlowEffect = false;
                slowEffectTick = 0;
            }
        }
        updateAnimationTick();
        updatePos();
        int aniIndex = getAniIndex();
        if (isMoving) {
            for (int stepFrame : STEP_ANIMATION_FRAMES) {
                if (aniIndex == stepFrame && lastStepAniIndex != stepFrame) {
                    SoundPlayer.playStep();
                    break;
                }
            }
        }
        lastStepAniIndex = aniIndex;
    }

    /** 0=idle down, 1=idle up, 2=idle left, 3=idle right, 4=run down, 5=run up, 6=run left, 7=run right. */
    private int getRunAnimationType() {
        if (IDLE.equals(playerAction)) {
            switch (getFacingDir()) {
                case 3: return 0;
                case 2: return 1;
                case 1: return 2;
                case 0: return 3;
                default: return 0;
            }
        }
        switch (playerAction) {
            case RUNNING_DOWN: return 4;
            case RUNNING_UP: return 5;
            case RUNNING_LEFT: return 6;
            case RUNNING_RIGHT: return 7;
            default: return 0;
        }
    }

    public BufferedImage getCurrentSprite() {
        if (runAnimations == null) return null;
        int type = getRunAnimationType();
        if (type < 0 || type >= runAnimations.length) return null;
        BufferedImage[] frames = runAnimations[type];
        if (frames == null || frames.length == 0) return null;
        int idx = getAniIndex() % frames.length;
        return frames[idx];
    }

    /** Overlay (e.g. hair) drawn on top of the player during idle only; same frame and direction as idle. Null when not idle or no overlay loaded. */
    public BufferedImage getCurrentIdleOverlaySprite() {
        if (!IDLE.equals(playerAction) || idleOverlayFrames == null) return null;
        int type = getRunAnimationType();
        if (type < 0 || type >= idleOverlayFrames.length) return null;
        BufferedImage[] frames = idleOverlayFrames[type];
        if (frames == null || frames.length == 0) return null;
        int idx = getAniIndex() % frames.length;
        return frames[idx];
    }

    /** Overlay (e.g. hair) drawn on top of the player during run only; same frame and direction as run. Null when not running or no overlay loaded. */
    public BufferedImage getCurrentRunOverlaySprite() {
        if (runOverlayFrames == null) return null;
        int type = getRunAnimationType();
        if (type < 4 || type >= runOverlayFrames.length) return null;
        BufferedImage[] frames = runOverlayFrames[type];
        if (frames == null || frames.length == 0) return null;
        int idx = getAniIndex() % frames.length;
        return frames[idx];
    }

    /** Idle or run overlay, whichever applies; null when moving with no run overlay or no overlay loaded. */
    public BufferedImage getCurrentOverlaySprite() {
        if (IDLE.equals(playerAction)) return getCurrentIdleOverlaySprite();
        return getCurrentRunOverlaySprite();
    }
}
