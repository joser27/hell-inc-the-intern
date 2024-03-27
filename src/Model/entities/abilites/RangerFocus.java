package Model.entities.abilites;

import Controller.GameController;
import Model.entities.Player;

import java.awt.*;

public class RangerFocus extends Ability{


    public RangerFocus(Player player, int scale, int xPos, int yPos, int cd) {
        super(player, scale, xPos, yPos, cd);

    }

    public void update() {
        updateUI();
    }

    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {

    }

    @Override
    public void renderUI(Graphics g) {
        g.drawString(Integer.toString(abilityCoolDownTick),160,800);
        if (abilityUsed) {
            g.setColor(new Color(255, 255, 255, 150));

            g.fillRect(30 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, 64, ticker);
        }
    }
}
