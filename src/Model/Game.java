package Model;

import Controller.GameController;
import Model.entities.*;
import Model.entities.abilites.Projectile;
import Model.entities.abilites.RangedAttack;

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
    private int yLvlOffset = -200;
    private int lvlMovingTick;
    Medkit medkit;

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


    public void update() {
        if (player1.getHealth() <=0) {
            gameOver=true;
            playerWinner=2;
        }
        if (player2.getHealth() <=0) {
            gameOver=true;
            playerWinner=1;
        }
        // Smash
        if (player1.getSmash().isJustFinishedCharging()) {
            if (player1.getSmash().getAttackSmashHitBox().intersects(player2.getHitBox())) {
                player2.decrementHealth((int) (player1.getSmash().getAppliedDamage()));
            }
        }
        //Volley
        if (player2.getVolleyShot().abilityUsed) {
            ArrayList<Projectile> bullet = player2.getVolleyShot().getBullet();
            for (Projectile bullets : bullet) {
                if (bullets.hitBox.intersects(player1.getHitBox())) {
                    player1.decrementHealth(10);
                }
            }
        }
        //ranged attack
//        if (player2.getRangedAttacks().abilityUsed) {
//            if (player2.getRangedAttacks().getBullet().hitsPlayer(player2)) {
//                System.out.println("P@");
//            }
//        }
//        if (player2.getRangedAttacks().getBullet()!=null) {
//            if (player2.getRangedAttacks().hitPlayer(player1)) {
//                player1.decrementHealth(10);
//            }
//        }
        if (player2.getRangedAttacks().getBullet()!=null) {
            player2.getRangedAttacks().getBullet().hitsPlayer(player1);
        }
//        if (player2.getRangedAttacks().abilityUsed) {
//            if (player2.getRangedAttacks().getBullet().hitBox.intersects(player2.getHitBox())) {
//                player1.decrementHealth(20);
//            }
//        }

//        if (player2.getVolleyShot()!=null) {
//            player2.getVolleyShot().update();
//
//            if (player2.getVolleyShot().isBulletDecayed()) {
//                player2.removeVolleyShot();
//            }
//        }


        if (getPlayer2().getxPos() / GameController.TILE_SIZE== 2 && getPlayer2().getyPos()/GameController.TILE_SIZE == 2) {
            System.out.println("Player 2 on loc");
        }


        timer++;
        if (time == 599) {


        }
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
                        player2.decrementHealth(1);

                    }
                    player1AttackLimiter=0;
                }

            }
        }
        entitiesUpdates();
        //bulletsUpdates();
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


    public LevelLoader getLevelLoader() { return levelLoader; }
    public Wall[] getWalls() { return walls; }

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
