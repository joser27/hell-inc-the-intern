package Model;

import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;


public class Game {
    private Player1 player1;
    private Player2 player2;
    private Wall[] walls;
    private Enemy[] enemy;
    private Player[] players;
    private Entity[] entities;
    private boolean gameOver = false;
    private CollisionChecker collisionChecker;
    private int entityWidth;
    private int entityHeight;
    private Random random = new Random();
    private int time = 60;
    private int timer = 0;
    private int playerWinner = 1;
    private int player1AttackLimiter;
    private BufferedImage[][] grassTile;
    private BufferedImage rockImg;

    public Game(int entityHeight, int entityWidth) {
        this.entityHeight = entityHeight;
        this.entityWidth = entityWidth;
        player1 = new Player1(13*48, 7*48, 20, 25, .5f, this);
        player2 = new Player2(3*48, 7*48, 20, 25, .5f, this);
        players = new Player[2];
        players[0] = player1;
        players[1] = player2;
        // Initialize walls array
        addWalls();
        enemy = new Enemy[0];
//        generateRandomEnemy();


        int sizeOfEntities = enemy.length + walls.length + players.length;
        entities = new Entity[sizeOfEntities];
        Entity[] allEntities = new Entity[sizeOfEntities];

        // Copy walls into allEntities
        System.arraycopy(walls, 0, allEntities, 0, walls.length);

        // Copy enemy into allEntities
        System.arraycopy(enemy, 0, allEntities, walls.length, enemy.length);

        // Copy players into allEntities
        System.arraycopy(players, 0, allEntities, walls.length + enemy.length, players.length);

        // Copy allEntities into entities
        System.arraycopy(allEntities, 0, entities, 0, sizeOfEntities);

        collisionChecker = new CollisionChecker();

        BufferedImage tempImg = LoadSave.GetSpriteAtlas(LoadSave.GRASS_TILESET);
        grassTile = new BufferedImage[16][9];
        int subimageWidth = 256 / 16;
        int subimageHeight = 144 / 9;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                int subimageX = subimageWidth * i;
                int subimageY = subimageHeight * j;
                    grassTile[i][j] = tempImg.getSubimage(subimageX, subimageY, subimageWidth, subimageHeight);
            }
        }
        rockImg = LoadSave.GetSpriteAtlas(LoadSave.STONE_1);



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
                    walls[wallIndex] = new Wall(j * entityWidth, i * entityHeight, entityWidth-18, entityHeight-20, 0, this);
                    wallIndex++;
                }
            }
        }
    }

//    public void generateRandomEnemy() {
//        enemy[0] = new Enemy(200, 250, 30, 30, 0f, this);
////        enemy[1] = new Enemy(300, 200, 50, 50, 1, this);
//    }
    public void update() {
        if (time == 39) {

        }
        timer++;
        if (timer == 120) {
            timer = 0;
            setTime(1);
        }
        if (getTime() <= 0) {
            gameOver = true;
            playerWinner = 2;
        }
        if (getPlayer1().getAttackHitBox() != null) {
            if (getPlayer1().isAttackingMelee()) {
                player1AttackLimiter++;
                if (player1AttackLimiter > 5) {
                    if (getPlayer1().getAttackHitBox().intersects(getPlayer2().getHitBox())) {
                        player2.decrementHealth(-1);
                        if (player2.getHealth() <= 0) {
                            gameOver = true;
                        }
                    }
                    player1AttackLimiter=0;
                }

            }
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

        for (int i = 0; i < LevelLoader.world.length; i++) {
            for (int j = 0; j < LevelLoader.world[i].length; j++) {
                if (LevelLoader.world[i][j] == 0) {
                    g.drawImage(grassTile[1][3], 48 * j, 48 * i, null);
                }
                if (LevelLoader.world[i][j] == 3) {
                    g.drawImage(rockImg, 48 * j, 48 * i, null);
                }
            }
        }
        entitiesRender(g);
        timerRender(g);

    }
    private void entitiesRender(Graphics g) {

        player2.render(g);
        player1.render(g);
        for (int i = 0; i < walls.length; i++) {
            walls[i].render(g);
        }
        for (int i = 0; i < enemy.length; i++) {
            enemy[i].render(g);
        }
    }
    private void timerRender(Graphics g) {
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
