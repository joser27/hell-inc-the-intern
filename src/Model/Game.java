package Model;

import Controller.GameController;
import Model.entities.*;
import java.util.ArrayList;
import java.util.Random;

public class Game {
    private Player1 player1;
    private Wall[] walls;
    private Enemy[] enemy;
    private Player[] players;
    private Entity[] entities;
    private boolean gameOver = false;
    private CollisionChecker collisionChecker;
    private boolean wasInWidowTrigger = false;
    private int time = 600;
    private int timer = 0;
    private int playerWinner = 1;

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
        player1 = new Player1(50 * GameController.TILE_SIZE, 40 * GameController.TILE_SIZE, 6 * GameController.SCALE, 8 * GameController.SCALE, .16f * GameController.SCALE, this);
        players = new Player[]{player1};
        walls = new Wall[0];  // Level collision from Tiled solid layer (tile map), not Wall entities
        enemy = new Enemy[0];

        int sizeOfEntities = enemy.length + walls.length + players.length;
        entities = new Entity[sizeOfEntities];
        Entity[] allEntities = new Entity[sizeOfEntities];
        System.arraycopy(walls, 0, allEntities, 0, walls.length);
        System.arraycopy(enemy, 0, allEntities, walls.length, enemy.length);
        System.arraycopy(players, 0, allEntities, walls.length + enemy.length, players.length);
        System.arraycopy(allEntities, 0, entities, 0, sizeOfEntities);

        collisionChecker = new CollisionChecker();
    }

    public void update() {
        entitiesUpdates();
        checkTriggers();
    }

    /** When the player enters the trigger with npc_id=widow, print once to console. */
    private void checkTriggers() {
        var hitBox = player1.getHitBox();
        boolean inWidow = false;
        for (Trigger t : levelLoader.getTriggers()) {
            if (t.intersects(hitBox) && "widow".equals(t.getNpcId())) {
                inWidow = true;
                break;
            }
        }
        if (inWidow && !wasInWidowTrigger) {
            System.out.println("Trigger: npc_id=widow");
        }
        wasInWidowTrigger = inWidow;
    }

    private void entitiesUpdates() {
        player1.update();
        for (int i = 0; i < enemy.length; i++)
            enemy[i].update();
        for (int i = 0; i < walls.length; i++)
            walls[i].update();
    }


    public LevelLoader getLevelLoader() { return levelLoader; }
    public Wall[] getWalls() { return walls; }

    public Entity[] getEntities() {
        return entities;
    }

    public Player1 getPlayer1() {
        return player1;
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
