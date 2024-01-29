package Model;

import Model.gamestates.Gamestate;
import Model.gamestates.LoadMenu;
import Model.gamestates.Playing;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;


public class Game {
    private Player1 player1;
    private Player2 player2;
    private Wall[] walls;
    private Enemy[] enemy;
    private Entity[] entities;

    private boolean gameOver = false;

    private CollisionChecker collisionChecker;
    private int entityWidth;
    private int entityHeight;
    private Random random = new Random();
    private int time = 450;
    private int timer = 0;
    private int playerWinner = 1;
    public Game(int entityHeight, int entityWidth) {
        this.entityHeight = entityHeight;
        this.entityWidth = entityWidth;
        player1 = new Player1(13*48, 7*48, 30, 30, 1f, this);
        player2 = new Player2(3*48, 7*48, 30, 30, 1f, this);

        // Initialize walls array
        addWalls();

        enemy = new Enemy[1];
        generateRandomEnemy();

        // Initialize the entities array with the player, walls, and enemies
        entities = new Entity[enemy.length + walls.length + 1];
        entities[0] = player1;
        entities[1] = player2;

        // Copy walls into entities
        System.arraycopy(walls, 0, entities, 1, walls.length);

        // Copy enemies into entities
        System.arraycopy(enemy, 0, entities, 1 + walls.length, enemy.length);

        collisionChecker = new CollisionChecker();

    }

    public void addWalls() {
        int count = 0;

        for (int i = 0; i < LevelLoader.world.length; i++) {
            for (int j = 0; j < LevelLoader.world[i].length; j++) {
                if (LevelLoader.world[i][j] == 1) {
                    count++;
                }
            }
        }
        walls = new Wall[count];

        // Populate walls array based on LevelLoader.world
        int wallIndex = 0;
        for (int i = 0; i < LevelLoader.world.length; i++) {
            for (int j = 0; j < LevelLoader.world[i].length; j++) {
                if (LevelLoader.world[i][j] == 1) {
                    walls[wallIndex] = new Wall(j * entityWidth, i * entityHeight, entityWidth, entityHeight, 0, this);
                    wallIndex++;
                }
            }
        }
    }

    public void generateRandomEnemy() {
        enemy[0] = new Enemy(200, 250, 30, 30, 0f, this);
//        enemy[1] = new Enemy(300, 200, 50, 50, 1, this);
    }
    public void update() {
        timer++;
        if (timer == 120) {
            timer = 0;
            setTime(1);
        }
        if (getTime() <= 0) {
            gameOver = true;
            playerWinner = 2;
        }
        if (getPlayer1().getHitBox().intersects(getPlayer2().getHitBox())) {
            gameOver = true;
            playerWinner = 1;
        }
        entitiesUpdates();
        bulletsUpdates();
        landMineUpdates();
    }

    private void entitiesUpdates() {
        player1.update();
        player2.update();

        for (int i = 0; i < enemy.length; i++) {
            enemy[i].update();
        }

        for (int i = 0; i < walls.length; i++) {
            walls[i].update();
        }
    }
    private void bulletsUpdates() {
        ArrayList<Bullet> bullets = player2.getBullets();
        for (Bullet bullet : bullets) {
            for (Wall wall : walls) {
                if (bullet.getBullet().intersects(wall.getHitBox())) {
                    bullet.setBulletDecayed(true);
                }
            }
            if (bullet.getBullet().intersects(getPlayer1().getHitBox())) {
                getPlayer1().decrementHealth(-20);

                bullet.setBulletDecayed(true);
            }
            if (getPlayer1().getHealth()<=0) {
                player1.respawn();
                player1.setHealth(100);
            }
        }
    }
    public void landMineUpdates() {
        ArrayList<LandMine> landMine = getPlayer2().getLandMine();
        for (LandMine mine : landMine) {
            if (!getPlayer1().isGodMode()) {
                if (mine.getLandMineHitBox().intersects(getPlayer1().getHitBox())) {
                    getPlayer1().respawn();
                }
            }
        }

    }
    public void render(Graphics g) {
        entitiesRender(g);
        timeRender(g);
    }
    private void entitiesRender(Graphics g) {
        for (int i = 0; i < walls.length; i++) {
            walls[i].render(g);
        }
        player1.render(g);
        player2.render(g);
        for (int i = 0; i < enemy.length; i++) {
            enemy[i].render(g);
        }
    }
    private void timeRender(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(575,10,150,50);
        g.setColor(Color.RED);
        Font font = new Font("Arial", Font.BOLD, 20);
        g.setFont(font);
        g.drawString("Time: " + time, 575, 40);
    }



    public Entity[] getEntities() {
        return entities;
    }

    public Player1 getPlayer1() {
        return player1;
    }
    public Player2 getPlayer2() {
        return player2;
    }
    public CollisionChecker getCollisionChecker() {
        return collisionChecker;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time -= time;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getPlayerWinner() {
        return playerWinner;
    }
}
