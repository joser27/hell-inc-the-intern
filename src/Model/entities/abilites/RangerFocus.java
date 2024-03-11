package Model.entities.abilites;

import Model.entities.Player;

import java.awt.*;

public class RangerFocus extends Ability {
    // Ranger focus
    public boolean rangerFocus = false;

    public int rangerFocusTick;
    public boolean rangerFocusUsed = false;
    private int rangerFocusCoolDown;
    public int rangerFocusCoolDownTick = 500;

    public RangerFocus(Player player, int scale, int xPos, int yPos) {
        super(player, scale, xPos, yPos);
    }

    @Override
    public void update() {
        if (rangerFocusUsed) {
            rangerFocusCoolDownTick--;
            System.out.println(rangerFocusCoolDownTick);
            if (0 >= rangerFocusCoolDownTick) {
                // reset cd
                rangerFocusCoolDownTick=500;
                System.out.println("OFF CD RANGER");
                rangerFocusUsed = false;
            }
        }
    }

    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {

    }
}
