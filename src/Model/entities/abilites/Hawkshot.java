package Model.entities.abilites;

import Controller.GameController;
import Model.entities.Player;

import java.awt.*;

public class Hawkshot extends Ability {
    public Hawkshot(Player player, int scale, int xPos, int yPos, int cd) {
        super(player, scale, xPos, yPos, cd);
    }

    @Override
    public void update() {
        updateUI();
    }

    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {

    }

    @Override
    public void renderUI(Graphics g) {
        if (abilityUsed) {
            g.setColor(new Color(255, 255, 255, 150));

            g.fillRect(70 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, 64, ticker);
        }
    }
}
