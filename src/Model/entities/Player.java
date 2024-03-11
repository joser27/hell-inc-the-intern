package Model.entities;

import Controller.GameController;
import Model.Game;
import Model.LevelLoader;
import Model.entities.Entity;
import Model.entities.abilites.AutoAttack;


public abstract class Player extends Entity {
    Game game;
    int playerX;
    int playerY;
    private AutoAttack autoAttack;
    public Player(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos,yPos,width,height,movementSpeed,game);
        this.game = game;
        autoAttack = new AutoAttack(this, GameController.SCALE, (int) this.getHitBox().x, (int) this.getHitBox().y);

    }

    public void update() {

        int y = (int) getHitBox().y;
        int x = (int) getHitBox().x;



        for (int i = 0; i < LevelLoader.world.length; i++) {
            for (int j = 0; j < LevelLoader.world[0].length; j++) {
                if (y / 48 == i && x / 48 == j) {
                    playerX = i;
                    playerY = j;
                }
            }
        }
    }



    public void useMedkit() {
        incrementHealth(50);
    }


    @Override
    public String toString() {
        return "PLAYER";
    }
}
