package Model.entities.abilites;

import Controller.GameController;
import Model.entities.Player;
import Model.entities.Player1;

import java.awt.*;

public class Shield extends Ability {

    boolean usedShield = false;
    public Shield(Player player, int scale, int xPos, int yPos, int cd) {
        super(player, scale, xPos, yPos, cd);
    }

    public void useShield(Player player) {
        abilityUsed=true;
        if (!usedShield) {
            player.incrementHealth(50);
            usedShield = true;
        }
    }
    public void removeShield() {
        player.decrementHealth(50);
    }

    @Override
    public void update() {
        cdTicker++;
        System.out.println(cdTicker);
        if (cdTicker>cd) {
            abilityUsed=false;
            removeShield();
        }
    }

    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {

    }

    @Override
    public void renderUI(Graphics g) {
        if (abilityUsed) {
            g.setColor(new Color(255, 255, 255, 150));
            g.fillRect(150 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, 64, ticker);
        }
    }
}
