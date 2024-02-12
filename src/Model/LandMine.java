package Model;

import Model.utilz.LoadSave;

import java.awt.*;
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
        landMineHitBox.width = 48;
        landMineHitBox.height = 48;
        landMineHitBox.x = (getLandMineHitBox().x/48) * 48;
        landMineHitBox.y = (getLandMineHitBox().y/48) * 48;
    }
    public void render(Graphics g,int xLvlOffset, int yLvlOffset) {
        g.drawRect(landMineHitBox.x, landMineHitBox.y, landMineHitBox.width, landMineHitBox.height);

        if (exploded) {
            g.drawImage(img[aniIndex][17], landMineHitBox.x-8 - xLvlOffset, landMineHitBox.y- yLvlOffset, null);
//            g.drawRect(landMineHitBox.x-xLvlOffset, landMineHitBox.y-yLvlOffset, landMineHitBox.width, landMineHitBox.height);
        } else {
            g.setColor(Color.BLACK);
            g.fillOval(landMineHitBox.x - xLvlOffset, landMineHitBox.y+3 - yLvlOffset, landMineHitBox.width, landMineHitBox.height / 2);
        }
    }


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
