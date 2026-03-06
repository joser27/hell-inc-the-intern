package Model.entities.abilites;

import Controller.GameController;
import Model.utilz.LoadSave;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class LandMine {

    private Rectangle landMineHitBox;
    private int landMineSize = 10;
    private boolean exploded = false;
    private int landMineDecayTime = 200;
    private int landMineDetonateTime = 300;
    private int landMineTimer = 0;
    BufferedImage[][] img;
    private int aniIndex = 0;
    private int aniMaxIndex = 7;
    private int aniTick;
    private int aniSpeed = 30;
    public LandMine(int xPos, int yPos, int width, int height) {
        landMineHitBox = new Rectangle(xPos,yPos,width,height);
        BufferedImage fullImg = LoadSave.GetSpriteAtlas(LoadSave.BOMB_EXPLOSION);
        img = new BufferedImage[8][24];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 24; j++) {
                img[i][j] = fullImg.getSubimage((512/8) * i, (1536/24) * j, 512/8, 1536/24);
            }
        }
    }

    public void update() {
        landMineTimer++;
        updateAnimation();
    }
    private void updateAnimation() {
        if (exploded) {
            aniTick++;
            if (aniTick >= aniSpeed) {
                aniTick = 0;
                aniIndex++;
            }
        }
    }

    public void explode() {
        exploded = true;
        landMineHitBox.width = GameController.SCALE;
        landMineHitBox.height = GameController.SCALE;
        landMineHitBox.x = (getLandMineHitBox().x/GameController.SCALE) * GameController.SCALE;
        landMineHitBox.y = (getLandMineHitBox().y/GameController.SCALE) * GameController.SCALE;
    }
    public boolean isExploded() { return exploded; }
    public int getAniIndex() { return aniIndex; }
    public BufferedImage getCurrentFrameImage() { return exploded ? img[aniIndex][17] : null; }

    public int getLandMineDecayTime() {
        return landMineDecayTime;
    }

    public void setLandMineDecayTime(int landMineDecayTime) {
        this.landMineDecayTime = landMineDecayTime;
    }

    public int getLandMineDetonateTime() {
        return landMineDetonateTime;
    }

    public void setLandMineDetonateTime(int landMineDetonateTime) {
        this.landMineDetonateTime = landMineDetonateTime;
    }

    public int getLandMineTimer() {
        return landMineTimer;
    }

    public void setLandMineTimer(int landMineTimer) {
        this.landMineTimer = landMineTimer;
    }

    public Rectangle getLandMineHitBox() {
        return landMineHitBox;
    }
}
