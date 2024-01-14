package Model;

import java.awt.*;



public class Player extends Entity {
    Game game;
    int playerX;
    int playerY;
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
                if (y / 48 == i && x / 48 == j) {
                    playerX = i;
                    playerY = j;
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


        Font font = new Font("Arial", Font.BOLD, 20);
        g.setFont(font);
        g.drawString("Player coords: " + playerY + " " + playerX, 100, 100);

        g.setColor(Color.BLACK);
        //System.err.println(playerX + "|" + playerY);

        g.fillRect((playerY*48),(playerX*48),48,48);
    }
    @Override
    public String toString() {
        return "PLAYER";
    }
}
