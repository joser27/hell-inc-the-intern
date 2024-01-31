package Model;

import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Shotgun extends Weapon implements Item {

    BufferedImage[][] img;
    protected Shotgun(int xPos, int yPos, int width, int height, Player player) {
        super(xPos, yPos, width, height, player);
//        img = new BufferedImage[45][16];
//        BufferedImage fullImg = LoadSave.GetSpriteAtlas(LoadSave.ZOMBIE_PACK_TILESET);
//
//        int subimageWidth = 764 / 45;
//        int subimageHeight = 300 / 16;
//
//        for (int i = 0; i < 45; i++) {
//            for (int j = 0; j < 16; j++) {
//                img[i][j] = fullImg.getSubimage(subimageWidth * i, subimageHeight * j, subimageWidth, subimageHeight);
//            }
//        }
    }


    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(img[1][7].getScaledInstance(400,400,Image.SCALE_DEFAULT),200,200,null);
    }
}
