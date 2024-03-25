package Model.entities.abilites;

import Model.entities.Player;
import Model.entities.abilites.Ability;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static Model.utilz.Constants.PlayerConstants.*;
import static Model.utilz.Constants.PlayerConstants.OGRE_SMASH_DOWN;

public class Smash extends Ability {
     public Rectangle2D.Float attackSmashHitBox;
    public boolean attackingSmash  = false;
    public int attackingSmashCD = 20;
    public int attackingSmashTimer;
    public int attackingSmashDuration;
    public boolean canSmashAttack = true;

    private boolean chargingAttack = false;

    private int chargeAttackTick;
    private int chargeAttackLimit = 200;
    private int appliedDamage;
    private int chargingTime;
    private boolean justFinishedCharging = false;
    private int justFinishedChargingTick;
    int appliedDamageTick;

    public Smash(Player player, int scale, int xPos, int yPos, int cd) {
        super(player,scale,xPos,yPos, cd);
        attackSmashHitBox = new Rectangle2D.Float(xPos,yPos,20*scale,20*scale);

    }
    private void attackSmashUpdate() {
        attackingSmashTimer++;
        if (attackingSmashTimer > attackingSmashCD) {
            attackingSmashTimer=0;
            canSmashAttack = true;
        }
        if (attackingSmash) {
            attackingSmashDuration++;
            if (attackingSmashDuration>200) {
                attackingSmashDuration=0;
                attackingSmashTimer=0;
                attackingSmash=false;
            }
        }

        switch(player.getFacingDir()) {//0 = right, 1 = left, 2 = up, 3 = down
            case 0 -> {
                attackSmashHitBox.x = player.getxPos()+(attackSmashHitBox.width/2/2);
                attackSmashHitBox.y = player.getyPos()-(attackSmashHitBox.height/2/2);
                if (attackingSmash)player.playerAction = OGRE_SMASH_RIGHT;
            }
            case 1 -> {
                attackSmashHitBox.x = player.getxPos()-(attackSmashHitBox.width);
                attackSmashHitBox.y = player.getyPos()-(attackSmashHitBox.height/2/2);
                if (attackingSmash)player.playerAction = OGRE_SMASH_LEFT;
            }
            case 2 -> {
                attackSmashHitBox.x = player.getxPos()-(attackSmashHitBox.height/2/2);
                attackSmashHitBox.y = player.getyPos()-(attackSmashHitBox.height/2);
                if (attackingSmash)player.playerAction = OGRE_SMASH_UP;
            }
            case 3 -> {
                attackSmashHitBox.x = player.getxPos()-(attackSmashHitBox.height/2/2);
                attackSmashHitBox.y = player.getyPos()+(attackSmashHitBox.height/2);
                if (attackingSmash)player.playerAction = OGRE_SMASH_DOWN;
            }
        }
    }
    @Override
    public void update() {
        if (chargingAttack) {
            player.setMovementSpeed(player.getBaseMovementSpeed()/3);
            chargeAttackTick++;
            if (chargeAttackTick > 20) {
                appliedDamageTick++;
                if (appliedDamage < 50) {
                    appliedDamage = (int) Math.pow(1.3, appliedDamageTick);
                }
                chargeAttackTick = 0;
                System.out.println(appliedDamage);
            }


        } else if (appliedDamage>1) {
            appliedDamageTick=0;
            appliedDamage=0;
            justFinishedCharging = true;
            player.setMovementSpeed(player.getBaseMovementSpeed());
        }

        if (justFinishedCharging) {

            chargeAttackTick=0;

            justFinishedChargingTick++;
            if (justFinishedChargingTick>100) {
                justFinishedChargingTick=0;
                justFinishedCharging=false;
            }
//            System.out.println("Just finished charging");
        }
        attackSmashUpdate();
    }

    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (chargingAttack) {
            // Hover count
            g.setColor(new Color(230,60,100,255));
            g.drawString(Integer.toString(appliedDamage), (int) ((int) player.getHitBox().x+player.getHitBox().width) - xLvlOffset, (int) player.getHitBox().y-10 - yLvlOffset);

            g.setColor(new Color(200,100,100,50));



            g.fillRect((int) attackSmashHitBox.x-xLvlOffset, (int) attackSmashHitBox.y-yLvlOffset, (int) attackSmashHitBox.width, (int) attackSmashHitBox.height);
            switch (player.getFacingDir()) {
                case 0:
                    player.playerAction = OGRE_SMASH_RIGHT;
                    break;
                case 1:
                    player.playerAction = OGRE_SMASH_LEFT;
                    break;
                case 2:
                    player.playerAction = OGRE_SMASH_UP;
                    break;
                case 3:
                    player.playerAction = OGRE_SMASH_DOWN;
                    break;
            }
        }
        if (justFinishedCharging) {
            switch (player.getFacingDir()) {
                case 0:
                    player.playerAction = OGRE_END_SMASH_RIGHT;
                    break;
                case 1:
                    player.playerAction = OGRE_END_SMASH_LEFT;
                    break;
                case 2:
                    player.playerAction = OGRE_END_SMASH_UP;
                    break;
                case 3:
                    player.playerAction = OGRE_END_SMASH_DOWN;
                    break;
            }
        }
    }

    @Override
    public void renderUI(Graphics g, int xLvlOffset, int yLvlOffset) {

    }

    public void smashAttack(boolean isHolding) {
        chargingAttack = isHolding;
        chargingTime = 0;
        if (!isHolding) {
            justFinishedCharging=true;
        } else {
            justFinishedCharging=false;
        }
    }

    public int getAppliedDamage() {
        return appliedDamage;
    }

    public Rectangle2D.Float getAttackSmashHitBox() {
        return attackSmashHitBox;
    }

    public boolean isJustFinishedCharging() {
        return justFinishedCharging;
    }
}
