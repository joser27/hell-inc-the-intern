package Model.entities.abilites;

import Controller.GameController;
import Model.entities.Player;
import Model.entities.Player1;

import java.awt.*;

public class Shield extends Ability {


    int gameTicker;
    int smashCDTick;


    public Shield(Player player, int scale, int xPos, int yPos, int cd) {
        super(player, scale, xPos, yPos, cd);
    }

    public void useShield() {
        if (canUseAbility) {

            abilityUsed=true;
            player.incrementHealth(50);

        }
    }
    public void removeShield() {
        player.decrementHealth(50);
    }

    @Override
    public void update() {
        updateUI();

        if (justFinishedAbility) {
            justFinishedAbility=false;
            removeShield();
        }
    }

    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (abilityUsed) {
            g.setColor(new Color(180, 36, 36, 92));
            g.fillOval((int) (player.getHitBox().x- 10) - xLvlOffset, (int) (player.getHitBox().y -10 )- yLvlOffset, (int) player.getHitBox().width * 2, (int) player.getHitBox().height*2);
        }
    }

    @Override
    public void renderUI(Graphics g) {
        if (abilityUsed) {
            g.setColor(new Color(255, 255, 255, 150));
            g.fillRect(GameController.GAME_WIDTH/2 + 50*GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, 64, ticker);
        }
    }
}
