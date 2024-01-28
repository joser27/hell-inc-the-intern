package Model;

import java.awt.*;
import java.util.ArrayList;

/*
 * The Runner
 */
public class Player2 extends Player{

    private int landMineCount = 5;
    private ArrayList<LandMine> landMine;
    private int landMineTimer = 0;

    public Player2(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos, yPos, width, height, movementSpeed, game);
        landMine = new ArrayList<>();
    }


    public void update() {
        updatePos();

        if (landMine.size()>0) {
            landMineTimer++;
            if (landMineTimer >= 200) {
                landMine.get(0).explode();

                if (landMineTimer >= 320) {
                    landMine.remove(0);
                    landMineTimer = 0;
                }
            }
        }
    }
    @Override
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
    public void placeMine() {
        if (landMineCount > 0) {
            landMineCount--;
            landMine.add(new LandMine(getxPos(), getyPos(), 10, 10));
        }
    }
    @Override
    public void render(Graphics g) {

        g.setColor(Color.RED);
        g.fillRect(getxPos(),getyPos(), (int) getHitBox().width, (int) getHitBox().height);


        Font font = new Font("Arial", Font.BOLD, 20);
        g.setFont(font);
        g.drawString("Player2 coords: " + getxPos()/48 + " " + getyPos()/48 + ", Mines: " + landMineCount, 50, 150);

        if (landMine !=null) {
            for (LandMine mine : landMine) {
                mine.render(g);
            }
        }

//        g.setColor(Color.BLACK);
//        //System.err.println(playerX + "|" + playerY);
//
//        g.fillRect((playerY*48),(playerX*48),48,48);
    }

    public ArrayList<LandMine> getLandMine() {
        return landMine;
    }
}
