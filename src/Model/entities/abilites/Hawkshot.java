package Model.entities.abilites;

import Model.entities.Player;

public class Hawkshot extends Ability {
    public Hawkshot(Player player, int scale, int xPos, int yPos, int cd) {
        super(player, scale, xPos, yPos, cd);
    }

    @Override
    public void update() {
        updateUI();
    }

}
