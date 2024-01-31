package Model;

import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Medkit implements Item {
    int xPos, yPos;
    BufferedImage[][] img;
    public Medkit(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
//        img = new BufferedImage[45][16];
//        BufferedImage fullImg = LoadSave.GetSpriteAtlas(LoadSave.ZOMBIE_PACK_TILESET);
//        for (int i = 0; i < 45; i++) {
//            for (int j = 0; j < 16; j++) {
//                img[i][j] = fullImg.getSubimage((764/45) * i, (300/16) * j, 764/45, 300/16);
//            }
//        }
    }
    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(img[1][7].getScaledInstance(200,200,Image.SCALE_DEFAULT),xPos,yPos,null);
    }
}
