package Model.entities.abilites;

import Model.entities.Player;

import java.awt.*;

public abstract class Ability {
    int scale;
    Player player;
    public boolean abilityUsed = false;
    public boolean canUseAbility=true;
    public int abilityCoolDownTick;
    public int ticker = 64;
    int cdTicker;
    public int cdLimiter;
    public int cd;
    int temp;
    int abilityUptime;
    int abilityUptimeTicker;
    boolean abilityActive;
    boolean justFinishedAbility;

    public Ability(Player player, int scale, int xPos, int yPos, int cd) {
        this.scale = scale;
        this.player = player;
        this.cd = cd;
        abilityCoolDownTick=cd;


        temp = cd/120;
        System.out.println(temp);
        cdLimiter= 120/temp;
        System.out.println(cdLimiter);
    }

    public abstract void update();
    public void updateUI() {//1sec=120ups
        if (abilityUsed) {
            canUseAbility=false;
            abilityCoolDownTick--;
            cdTicker++;
            if (cdTicker>cdLimiter) {
                ticker--;
                cdTicker = 0;
            }
            if (abilityCoolDownTick<=0) {
                System.out.println("Can use ability now");
                justFinishedAbility=true;
                canUseAbility=true;
                abilityUsed=false;
                abilityCoolDownTick=cd;
                ticker=64;
            }
        }
    }

    public void getFacingDir() {

    }
//    public void updateUI() {
//        if (abilityUsed) {
//            abilityCoolDownTick--;
//            cdTicker++;
//            if (cdTicker>7) {
//                ticker--;
//                cdTicker=0;
//            }
//            if (0 >= abilityCoolDownTick) {
//                // reset cd
//                abilityCoolDownTick=cd;
//                abilityUsed = false;
//                ticker=64;
//            }
//        }
//    }

    public abstract void render(Graphics g, int xLvlOffset, int yLvlOffset);
    public abstract void renderUI(Graphics g);
}
