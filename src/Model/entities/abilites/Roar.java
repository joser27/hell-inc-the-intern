package Model.entities.abilites;

import Controller.GameController;
import Model.entities.Player;

import java.awt.*;

public class Roar extends Ability {
    boolean canUseAbility=true;
    int roarCDTick;
    Projectile projectile;

    public Roar(Player player, int scale, int xPos, int yPos, int cd) {
        super(player, scale, xPos, yPos, cd);

    }

    public void useRoar() {
        if (canUseAbility) {
            projectile = new Projectile(player, (int) player.getHitBox().x, (int) player.getHitBox().y,null,0);
            abilityUsed=true;
            canUseAbility=false;
        }
    }
    @Override
    public void update() {//0 = right, 1 = left, 2 = up, 3 = down
        updateUI();
        if (abilityUsed) {
            projectile.updateProjectile();
        }
    }

    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (abilityUsed) {
            g.setColor(Color.red);
            g.fillOval(projectile.hitBox.x-xLvlOffset, projectile.hitBox.y-yLvlOffset, projectile.hitBox.width, projectile.hitBox.height);
        }
    }

    @Override
    public void renderUI(Graphics g) {
        if (abilityUsed) {
            g.setColor(new Color(255, 255, 255, 150));

            g.fillRect(GameController.GAME_WIDTH/2 + 70*GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, 64, ticker);
        }
    }
}
