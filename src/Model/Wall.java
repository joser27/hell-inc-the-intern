package Model;

import Controller.GameController;
import Model.entities.Entity;
import Model.utilz.LoadSave;

import java.awt.Image;

public class Wall extends Entity {

    private Image img;
    private static final int DRAW_OFFSET_X = 18;
    private static final int DRAW_OFFSET_Y = 48;

    public Wall(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos, yPos, width, height, movementSpeed, game);

        img = LoadSave.GetSpriteAtlas(LoadSave.TREE_4).getScaledInstance((int) getHitBox().width + 11 * GameController.SCALE, (int) getHitBox().height+16 * GameController.SCALE,Image.SCALE_DEFAULT);
    }

    public void update(int xLvlOffset) {
        getHitBox().x += xLvlOffset;
    }




    public Image getImage() { return img; }
    public int getDrawOffsetX() { return DRAW_OFFSET_X; }
    public int getDrawOffsetY() { return DRAW_OFFSET_Y; }
}
