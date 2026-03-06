package Model.entities.abilites;

import Model.entities.Player;

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

}
