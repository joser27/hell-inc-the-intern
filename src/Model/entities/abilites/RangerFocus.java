package Model.entities.abilites;

import Controller.GameController;
import Model.entities.Player;

import java.awt.*;

public class RangerFocus{
    // Ranger focus
    public boolean rangerFocus = false;

    public int rangerFocusTick;
    public boolean rangerFocusUsed = false;
    private int rangerFocusCoolDown;
    public int rangerFocusCoolDownTick = 500;
    int ticker = 64;
    int cdTicker;

    public RangerFocus(Player player, int scale, int xPos, int yPos) {

    }

    public void update() {
        if (rangerFocusUsed) {
            rangerFocusCoolDownTick--;
            cdTicker++;
            if (cdTicker>7) {
                ticker--;
                cdTicker=0;
            }
            if (0 >= rangerFocusCoolDownTick) {
                // reset cd
                rangerFocusCoolDownTick=500;
                rangerFocusUsed = false;
                ticker=64;
            }
        }
    }

    public void render(Graphics g) {
        g.drawString(Integer.toString(rangerFocusCoolDownTick),160,800);
        if (rangerFocusUsed) {
            g.setColor(new Color(255, 255, 255, 150));

            g.fillRect(30 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, 64, ticker);
        }
    }
}
