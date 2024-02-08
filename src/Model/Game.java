package Model;

import Controller.GameController;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
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
    private int tileSize;
    private Random random = new Random();
    private int time = 600;
    private int timer = 0;
    private int playerWinner = 1;
    private int player1AttackLimiter;

    ArrayList<Medkit> activeMedkits = new ArrayList<>();
    private boolean placedMedkit = false;
    private int[][] world;
    private LevelLoader levelLoader;
    private int xLvlOffset;
    private int lvlMovingTick;


    public Game() {
        levelLoader = new LevelLoader();
        world = levelLoader.getWorld();
        player1 = new Player1(13*GameController.TILE_SIZE, 7*GameController.TILE_SIZE, 6*GameController.SCALE, 8*GameController.SCALE, .16f*GameController.SCALE, this);
        player2 = new Player2(3*GameController.TILE_SIZE, 7*GameController.TILE_SIZE, 6* GameController.SCALE, 8* GameController.SCALE, .16f*GameController.SCALE, this);
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

    }

    public void addWalls() {
        int count = 0;

        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                if (LevelLoader.world[i][j] == 1) {
                    count++;
                }
            }
        }
        walls = new Wall[count];

        // Populate walls array based on LevelLoader.world
        int wallIndex = 0;
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                if (LevelLoader.world[i][j] == 1) {
                    walls[wallIndex] = new Wall(j * GameController.TILE_SIZE, i * GameController.TILE_SIZE, GameController.TILE_SIZE, GameController.TILE_SIZE, 0, this);
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
        Iterator<Medkit> iterator = activeMedkits.iterator();
        while (iterator.hasNext()) {
            Medkit medkit = iterator.next();
            if (medkit.getHitBox().intersects(getPlayer1().getHitBox())) {
                player1.useMedkit();
                iterator.remove();
            }
            if (medkit.getHitBox().intersects(getPlayer2().getHitBox())) {
                player2.useMedkit();
                iterator.remove();
            }
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
//        lvlMovingTick++;
//        if (lvlMovingTick>10) {
//            lvlMovingTick=0;
//            xLvlOffset++;
//        }
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
                if (bullet.getBulletHitBox().intersects(wall.getHitBox())) {
                    bullet.setBulletDecayed(true);
                }
            }
            if (bullet.getBulletHitBox().intersects(getPlayer1().getHitBox())) {
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
    public void renderLeftScreen(Graphics g, int xLvlOffset) {//Player 2 is left
        int screenWidth = GameController.GAME_WIDTH;
        int screenHeight = GameController.GAME_HEIGHT;
        int tileSize = GameController.TILE_SIZE;

        g.fillRect(0, 0, screenWidth / 2, screenHeight);


        for (int i = 0; i < LevelLoader.world.length; i++) {
            for (int j = 0; j < LevelLoader.world[i].length; j++) {
                if ((j*GameController.TILE_SIZE) - xLvlOffset < GameController.GAME_WIDTH/2) {
//                    g.setColor(Color.YELLOW);
//                    g.fillRect(GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i,GameController.TILE_SIZE,GameController.TILE_SIZE);
                    if (LevelLoader.world[i][j] == 0) {
//                        g.setColor(Color.YELLOW);
//                        g.fillRect(GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i,GameController.TILE_SIZE,GameController.TILE_SIZE);

                        g.drawImage(levelLoader.getGrassImage(), GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i, null);

                    }
                    if (LevelLoader.world[i][j] == 3) {
                        g.drawImage(levelLoader.getRock1Image(), GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i, null);
                    }
                    if (LevelLoader.world[i][j] == 4) {
                        g.drawImage(levelLoader.getRock2Image(), GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i, null);
                    }
                }
//                if (LevelLoader.world[i][j] == 0) {
//                    g.drawImage(grassTile[1][3], GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i, null);
//                }
//                if (LevelLoader.world[i][j] == 3) {
//                    g.drawImage(rockImg, GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i, null);
//                }
//                if (LevelLoader.world[i][j] == 4) {
//                    g.drawImage(rockImg2, GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i, null);
//                }
            }
        }


        player2.render(g, xLvlOffset);
        if (player1.getxPos() - xLvlOffset < GameController.GAME_WIDTH / 2 ) {
            player1.render(g,xLvlOffset);
        }
        for (int i = 0; i < walls.length; i++) {
            if (walls[i].getHitBox().x -xLvlOffset< GameController.GAME_WIDTH/2) {
                walls[i].render(g, xLvlOffset);
            }
        }

        g.setColor(Color.WHITE);
        g.drawString("Player2 coords: " + getPlayer2().getxPos()/48 + " " + getPlayer2().getyPos()/48 + ", Mines: " + getPlayer2().getLandMineCount() + "; HP: " + getPlayer2().getHealth(), 80, 150);

    }

    public void renderRightScreen(Graphics g, int xLvlOffset) {//Player 1 is right
        int screenWidth = GameController.GAME_WIDTH;
        int screenHeight = GameController.GAME_HEIGHT;
        int tileSize = GameController.TILE_SIZE;

        g.fillRect(screenWidth / 2, 0, screenWidth / 2, screenHeight);

        //Bullets rending when offscreen
        if (player2.getBullets().size()>0) {
            ArrayList<Bullet> bullets = player2.getBullets();
            for (Bullet bullet : bullets) {
                if (bullet.getBulletHitBox().getX()-xLvlOffset > GameController.GAME_WIDTH / 2) {
                    bullet.render(g,xLvlOffset);
                }
            }
        }
        for (int i = 0; i < LevelLoader.world.length; i++) {
            for (int j = 0; j < LevelLoader.world[i].length; j++) {
                if ((j*GameController.TILE_SIZE) - xLvlOffset> GameController.GAME_WIDTH/2 && (j*GameController.TILE_SIZE) - xLvlOffset< GameController.GAME_WIDTH) {
//                    g.setColor(Color.YELLOW);
//                    g.fillRect(GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i,GameController.TILE_SIZE,GameController.TILE_SIZE);
                    if (LevelLoader.world[i][j] == 0) {
//                        g.setColor(Color.YELLOW);
//                        g.fillRect(GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i,GameController.TILE_SIZE,GameController.TILE_SIZE);

                        g.drawImage(levelLoader.getGrassImage(), GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i, null);

                    }
                    if (LevelLoader.world[i][j] == 3) {
                        g.drawImage(levelLoader.getRock1Image(), GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i, null);
                }
                    if (LevelLoader.world[i][j] == 4) {
                        g.drawImage(levelLoader.getRock2Image(), GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i, null);
                    }
                }
//                if (LevelLoader.world[i][j] == 0) {
//                    g.drawImage(grassTile[1][3], GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i, null);
//                }
//                if (LevelLoader.world[i][j] == 3) {
//                    g.drawImage(rockImg, GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i, null);
//                }
//                if (LevelLoader.world[i][j] == 4) {
//                    g.drawImage(rockImg2, GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i, null);
//                }
            }
        }
        player1.render(g, xLvlOffset);
        if (player2.getxPos() - xLvlOffset > GameController.GAME_WIDTH / 2 && player2.getxPos() - xLvlOffset < GameController.GAME_WIDTH) {
            player2.render(g, xLvlOffset);
        }
        for (int i = 0; i < walls.length; i++) {
            // Check if the wall is in the right half of the screen before rendering
            if (walls[i].getHitBox().x -xLvlOffset> screenWidth / 2 && walls[i].getHitBox().x -xLvlOffset< screenWidth) {
                walls[i].render(g, xLvlOffset);
            }
        }
        g.setColor(Color.WHITE);
        g.drawString("Player1 coords: " + getPlayer1().getxPos() / GameController.TILE_SIZE + " " +
                getPlayer1().getyPos() / GameController.TILE_SIZE + ", Boosts: " +
                getPlayer1().getSpeedBoostUsages() + "; HP:" + getPlayer1().getHealth(), 80, 100);

    }



//    public void render(Graphics g, int xLvlOffset) {
//        Iterator<Medkit> iterator = activeMedkits.iterator();
//        while (iterator.hasNext()) {
//            Medkit medkit = iterator.next();
//            if (medkit.isActive()) {
//                medkit.render(g);
//            }
//        }
//        levelLoader.render(g,xLvlOffset);
//        entitiesRender(g,xLvlOffset);
//        timerRender(g);
//        g.setColor(Color.WHITE);
//        Font font = new Font("Arial", Font.BOLD, 15);
//        g.setFont(font);
//        g.drawString("Player1 coords: " + getPlayer1().getxPos()/GameController.TILE_SIZE + " " + getPlayer1().getyPos()/GameController.TILE_SIZE + ", Boosts: " + getPlayer1().getSpeedBoostUsages() + "; HP:" + getPlayer1().getHealth(), 80, 100);
//        g.drawString("Player2 coords: " + getPlayer2().getxPos()/48 + " " + getPlayer2().getyPos()/48 + ", Mines: " + getPlayer2().getLandMineCount() + "; HP: " + getPlayer2().getHealth(), 80, 150);
//    }
    private void entitiesRender(Graphics g, int xLvlOffset) {
        player2.render(g,xLvlOffset);
        player1.render(g,xLvlOffset);

        for (int i = 0; i < walls.length; i++) {
            walls[i].render(g,xLvlOffset);
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

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public int getPlayerWinner() {
        return playerWinner;
    }

}
