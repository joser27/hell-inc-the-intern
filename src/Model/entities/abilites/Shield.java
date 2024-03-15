package Model.entities.abilites;

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
    public void renderUI(Graphics g, int xLvlOffset, int yLvlOffset) {

            g.setColor(new Color(155,150,200,100));
            g.fillRect((int) player.getHitBox().x - xLvlOffset, (int) player.getHitBox().y - yLvlOffset, (int) ((int) player.getHitBox().width*1.5), (int) ((int) player.getHitBox().height*1.5));

    }
}
