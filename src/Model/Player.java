package Model;

import java.awt.*;



public class Player extends Entity {
    Game game;
    public Player(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos,yPos,width,height,movementSpeed,game);
        this.game = game;

    }

    public void update() {

        updatePos();
        int y = (int) getHitBox().y;
        int x = (int) getHitBox().x;


        for (int i = 0; i < LevelLoader.world.length; i++) {
            for (int j = 0; j < LevelLoader.world[0].length; j++) {
                if (y / 48 == 2 && x / 48 == 2) {
                    System.out.println("We're at the 2,2");
                }
            }
        }
    }

    public void updatePos() {
        int xSpeed = 0;
        int ySpeed = 0;
        if (isLeft()) {
            xSpeed -= getMovementSpeed();
        }
        if (isRight()) {
            xSpeed += getMovementSpeed();
        }
        if (isDown()) {
            ySpeed += getMovementSpeed();

        }
        if (isUp()) {
            ySpeed -= getMovementSpeed();
        }
        game.getCollisionChecker().handleCollision(this, game.getEntities(),xSpeed,ySpeed);
    }
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(getxPos(),getyPos(), (int) getHitBox().width, (int) getHitBox().height);

        int num = 20;
        g.drawString(String.valueOf(num), 200,100);
    }
    @Override
    public String toString() {
        return "PLAYER";
    }
}
