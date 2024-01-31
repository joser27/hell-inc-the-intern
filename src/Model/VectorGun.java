package Model;

import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class VectorGun extends Weapon implements Item {

    BufferedImage[] img;
    public VectorGun(int xPos, int yPos, int width, int height, Player player) {
        super(xPos, yPos, width, height, player);
        BufferedImage fullImg = LoadSave.GetSpriteAtlas(LoadSave.VECTOR_45);
        img = new BufferedImage[15];
        for (int i = 0; i < 15; i++) {
            img[i] = fullImg.getSubimage(0, (750/15) * i, 120, 750/15);
        }
    }
    @Override
    public void update() {
        aniTick++;
        if (aniTick>aniSpeed) {
            aniIndex++;
            aniTick=0;
            if (aniIndex>=15) {
                aniIndex=0;
            }
        }
    }

    @Override
    public void render(Graphics g) {

        g.drawImage(img[aniIndex], getPlayer().getxPos(), getPlayer().getyPos(), -getWidth()-200, getHeight()+100, null);
    }
}
