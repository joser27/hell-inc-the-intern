package Model.entities.abilites;

import Model.entities.Player;

import java.awt.*;

public abstract class Ability {
    int scale;
    Player player;
    public boolean abilityUsed = false;
    public int abilityCoolDownTick = 500;
    public int ticker = 64;
    public int cdTicker;
    public int cd;

    public Ability(Player player, int scale, int xPos, int yPos, int cd) {
        this.scale = scale;
        this.player = player;
        this.cd = cd;
    }

    public abstract void update();
    public void updateUI() {
        if (abilityUsed) {
            abilityCoolDownTick--;
            cdTicker++;
            if (cdTicker>7) {
                ticker--;
                cdTicker=0;
            }
            if (0 >= abilityCoolDownTick) {
                // reset cd
                abilityCoolDownTick=cd;
                abilityUsed = false;
                ticker=64;
            }
        }
    }

    public abstract void render(Graphics g, int xLvlOffset, int yLvlOffset);
    public abstract void renderUI(Graphics g);
}
