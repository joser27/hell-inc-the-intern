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
    private boolean medkitSpawnedAt58 = false;
    private boolean medkitSpawnedAt56 = false;
    private boolean medkitSpawnedAt54 = false;
    public void update() {
        if (time == 58 && !medkitSpawnedAt58) {
            medkitSpawnedAt58 = true;
            placedMedkit = true;
            activeMedkits.add(new Medkit(200, 200));
            activeMedkits.get(activeMedkits.size()-1).setActive(true);
        }
        if (time == 56 && !medkitSpawnedAt56) {
            medkitSpawnedAt56 = true;
            placedMedkit = true;
            activeMedkits.add(new Medkit(300, 200));
            activeMedkits.get(activeMedkits.size()-1).setActive(true);
        }
        if (time == 54 && !medkitSpawnedAt54) {
            medkitSpawnedAt54 = true;
            placedMedkit = true;
            activeMedkits.add(new Medkit(300, 300));
            activeMedkits.get(activeMedkits.size()-1).setActive(true);
        }
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
    public void render(Graphics g, int xLvlOffset) {
        Iterator<Medkit> iterator = activeMedkits.iterator();
        while (iterator.hasNext()) {
            Medkit medkit = iterator.next();
            if (medkit.isActive()) {
                medkit.render(g);
            }
        }
        levelLoader.render(g,xLvlOffset);
        entitiesRender(g,xLvlOffset);
        timerRender(g);
    }
    private void entitiesRender(Graphics g, int xLvlOffset) {


        player2.render(g,xLvlOffset);
        player1.render(g,xLvlOffset);

        for (int i = 0; i < walls.length; i++) {
            walls[i].render(g,xLvlOffset);
        }
        for (int i = 0; i < enemy.length; i++) {
            enemy[i].render(g);
        }

        g.setColor(Color.WHITE);
        Font font = new Font("Arial", Font.BOLD, 15);
        g.setFont(font);

        g.drawString("Player1 coords: " + getPlayer1().getxPos()/GameController.TILE_SIZE + " " + getPlayer1().getyPos()/GameController.TILE_SIZE + ", Boosts: " + getPlayer1().getSpeedBoostUsages() + "; HP:" + getPlayer1().getHealth(), 80, 100);
        g.drawString("Player2 coords: " + getPlayer2().getxPos()/48 + " " + getPlayer2().getyPos()/48 + ", Mines: " + getPlayer2().getLandMineCount() + "; HP: " + getPlayer2().getHealth(), 80, 150);

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
