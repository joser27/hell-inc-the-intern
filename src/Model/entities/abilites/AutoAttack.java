package Model.entities.abilites;

import Model.entities.Player;

import java.awt.*;

public class AutoAttack extends Ability {
    boolean canAutoAttack;
    boolean autoAttackSpeed;
    public AutoAttack(Player player, int scale, int xPos, int yPos) {
        super(player, scale, xPos, yPos);
    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {

    }
}
