package Model;

import java.awt.*;
import java.util.Random;


public class Game {
    private Player player;
    private Wall[] walls;
    private Enemy[] enemy;
    private Entity[] entities;

    private CollisionChecker collisionChecker;
    private int entityWidth;
    private int entityHeight;
    private Random random = new Random();
    public Game(int entityHeight, int entityWidth) {
        this.entityHeight = entityHeight;
        this.entityWidth = entityWidth;
        player = new Player(100, 100, entityWidth, entityHeight, 3, this);

        // Initialize walls array
        addWalls();

        enemy = new Enemy[1];
        generateRandomEnemy();

        // Initialize the entities array with the player, walls, and enemies
        entities = new Entity[enemy.length + walls.length + 1];
        entities[0] = player;

        // Copy walls into entities
        System.arraycopy(walls, 0, entities, 1, walls.length);

        // Copy enemies into entities
        System.arraycopy(enemy, 0, entities, 1 + walls.length, enemy.length);

        collisionChecker = new CollisionChecker();
    }

    public void addWalls() {
        int count = 0;

        for (int i = 0; i < LevelLoader.world.length; i++) {
            for (int j = 0; j < LevelLoader.world[0].length; j++) {
                if (LevelLoader.world[i][j] == 1) {
                    count++;
                }
            }
        }
        walls = new Wall[count];

        // Populate walls array based on LevelLoader.world
        int wallIndex = 0;
        for (int i = 0; i < LevelLoader.world.length; i++) {
            for (int j = 0; j < LevelLoader.world[0].length; j++) {
                if (LevelLoader.world[i][j] == 1) {
                    walls[wallIndex] = new Wall(j * entityWidth, i * entityHeight, entityWidth, entityHeight, 0, this);
                    wallIndex++;
                }
            }
        }
    }

    public void generateRandomEnemy() {
        enemy[0] = new Enemy(200, 200, entityWidth, entityHeight, 0, this);
//        enemy[1] = new Enemy(300, 200, 50, 50, 1, this);
    }
    public void update() {
        player.update();
        for (int i = 0; i < enemy.length; i++) {
            enemy[i].update();
        }

        for (int i = 0; i < walls.length; i++) {
            walls[i].update();
        }
    }

    public void render(Graphics g) {
        player.render(g);
        for (int i = 0; i < walls.length; i++) {
            walls[i].render(g);
        }
        for (int i = 0; i < enemy.length; i++) {
            enemy[i].render(g);
        }
    }

    public Entity[] getEntities() {
        return entities;
    }

    public Player getPlayer() {
        return player;
    }

    public CollisionChecker getCollisionChecker() {
        return collisionChecker;
    }
}
