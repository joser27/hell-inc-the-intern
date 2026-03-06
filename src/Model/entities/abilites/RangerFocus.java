package Model.entities.abilites;

import Model.entities.Player;

public class RangerFocus extends Ability{


    public RangerFocus(Player player, int scale, int xPos, int yPos, int cd) {
        super(player, scale, xPos, yPos, cd);

    }

    public void update() {
        updateUI();
    }

}
