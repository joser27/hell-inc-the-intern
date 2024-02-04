package Model;

import Controller.GameController;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Enemy extends Entity{

    private Game game;
    private int gameFreezeFrames = 30;
    private int myX, myY;
    Node[][] node = new Node[GameController.TILES_IN_WIDTH][GameController.TILES_IN_HEIGHT]; // 27, 18
    Node startNode, goalNode, currentNode;
    ArrayList<Node> openList = new ArrayList<>();
    ArrayList<Node> checkedList = new ArrayList<>();
    private ArrayList<Node> path;
    boolean goalReached = false;
    int step = 0;
    public Enemy(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos,yPos,width,height,movementSpeed,game);
        this.game = game;

        // Place Nodes
        int col = 0;
        int row = 0;

        while (col < GameController.TILES_IN_WIDTH && row < GameController.TILES_IN_HEIGHT) {
            node[col][row] = new Node(col,row);

            col++;
            if (col == GameController.TILES_IN_WIDTH) {
                col = 0;
                row++;
            }
        }


    }

    public void update() {
        gameFreezeFrames++;

        float xSpeed = 0f;
        float ySpeed = 0f;
        myX = getxPos();
        myY = getyPos();

        Rectangle2D.Float currHitBox = getHitBox();
        int enemyX = (int) currHitBox.x;
        int enemyY = (int) currHitBox.y;


        Rectangle2D.Float playerHitBox = game.getPlayer1().getHitBox();
        int playerX = (int) playerHitBox.x;
        int playerY = (int) playerHitBox.y;


        // Update the enemy's position based on the path
        if (path != null && !path.isEmpty()) {
            Node nextNode = path.get(0);
            int nextX = nextNode.col * 48;
            int nextY = nextNode.row * 48;

            // Move towards the next node
            if (nextX > enemyX) {
                xSpeed += getMovementSpeed();
            } else if (nextX < enemyX) {
                xSpeed -= getMovementSpeed();
            }

            if (nextY > enemyY) {
                ySpeed += getMovementSpeed();
            } else if (nextY < enemyY) {
                ySpeed -= getMovementSpeed();
            }

            // CHECK COLLISION BETWEEN ENTITIES
            game.getCollisionChecker().handleCollision(this, game.getEntities(), xSpeed, ySpeed);

            // Check if the enemy has reached the next node
            if (myY == nextY && myX == nextY) {
                path.remove(0);  // Move to the next node in the path
            }
        }
    }


    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(getxPos(),getyPos(), (int) getHitBox().width, (int) getHitBox().height);

        Font font = new Font("Arial", Font.BOLD, 20);
        g.setFont(font);
        g.drawString("Bot coords: " + myX/48 + " " + myY/48, 100, 200);

//        g.setColor(Color.BLACK);
//        System.err.println(myX + "|" + myY);
//
//        g.fillRect((myX/48),(myY/48),48,48);
    }
//    private void setStartNode(int col, int row) {
//        node[col][row].setAsStart();
//        startNode = node[col][row];
//        currentNode = startNode;
//    }
//    private void setGoalNode(int col, int row) {
//        node[col][row].setAsGoal();
//        goalNode = node[col][row];
//    }
//    private void setSolidNode(int col, int row) {
//        node[col][row].setAsSolid();
//    }
//    private void setCostOnNodes() {
//        int row = 0;
//        int col = 0;
//
//        while (col < GameController.maxScreenCol && row < GameController.maxScreenRow) {
//
//            getCost(node[col][row]);
//            col++;
//            if (col == GameController.maxScreenCol) {
//                col = 0;
//                row++;
//            }
//        }
//    }
//    private void getCost(Node node) {
//        // GET G COST (The distance from the start node)
//        int xDistance = Math.abs(node.col - startNode.col);
//        int yDistance = Math.abs(node.row - startNode.row);
//        node.gCost = xDistance + yDistance;
//
//        // GET H COST (The distance from the goal node)
//        xDistance = Math.abs(node.col - goalNode.col);
//        yDistance = Math.abs(node.row - goalNode.row);
//        node.hCost = xDistance + yDistance;
//
//        // GET F COST (the total cost)
//        node.fCost = node.gCost + node.hCost;
//    }
//    public void search() {
//
//        // Stop searching after 300 steps
//        while (goalReached == false) {
//            step++;
//            int maxCol = GameController.maxScreenCol;
//            int maxRow = GameController.maxScreenRow;
//            int col = currentNode.row;
//            int row = currentNode.col;
//
//
//
//            currentNode.setAsChecked();
//            checkedList.add(currentNode);
//            openList.remove(currentNode);
//
//            // OPEN THE UP NODE
//            if (row - 1 >= 0) {
//                openNode(node[col][row - 1]);
//            }
//
//            // OPEN THE LEFT NODE
//            if (col - 1 >= 0) {
//                openNode(node[col - 1][row]);
//            }
//
//            // OPEN THE DOWN NODE
//            if (row + 1 < maxRow) {
//                openNode(node[col][row + 1]);
//            }
//
//            // OPEN THE RIGHT NODE
//            if (col + 1 < maxCol) {
//                openNode(node[col + 1][row]);
//            }
//
//            // FIND BEST NODE
//            int bestNodeIndex = 0;
//            int bestNodefCost = 999;
//
//            for (int i = 0; i < openList.size(); i++) {
//
//                // CHECK IF NODES F IS BETTER. (CHECK THE ADJACENT NODES)
//                if (openList.get(i).fCost < bestNodefCost) {
//                    bestNodeIndex = i;
//                    bestNodefCost = openList.get(i).fCost;
//                }
//                // IF F COST IS EQUAL, CHECK G COST
//                else if (openList.get(i).fCost == bestNodefCost) {
//                    if (openList.get(i).gCost < openList.get(bestNodeIndex).gCost) {
//                        bestNodeIndex = i;
//                    }
//
//                }
//            }
//            // AFTER LOOP, WE GET THE BEST NODE WHICH IS OUR NEXT STEP
//            currentNode = openList.get(bestNodeIndex);
//
//            // CHECK IF CURRENT NODE IS GOAL NODE
//            if (currentNode == goalNode) {
//                goalReached = true;
//                trackThePath();
//            }
//        }
//    }
//    private void trackThePath() {
//        // BACKTRACK AND STORE THE PATH
//        path = new ArrayList<>();
//        Node current = goalNode;
//
//        while (current != startNode) {
//            current = current.parent;
//
//            if (current != startNode) {
//                path.add(current);
//            }
//        }
//
//        // Reverse the path to start from the beginning
//        Collections.reverse(path);
//    }
//    private void openNode(Node node) {
//        // IF NOT OPEN AND NOT CHECKED AND NOT SOLID
//        if (node.open == false && node.checked == false && node.solid == false) {
//            System.err.println("TEST");
//            // IF THE NODE IS NOT OPENED YET, ADD IT TO THE OPEN LIST
//            node.setAsOpen();
//            node.parent = currentNode;
//            openList.add(node);
//        }
//    }
    @Override
    public String toString() {
        return "ENEMY";
    }
}
