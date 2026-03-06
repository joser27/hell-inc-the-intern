package Model.entities.abilites;

import Model.entities.Player;

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
        updateUI();
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
        }
        attackSmashUpdate();
        // Animation state (was in render, moved to update so View only draws)
        if (chargingAttack) {
            switch (player.getFacingDir()) {
                case 0 -> player.playerAction = OGRE_SMASH_RIGHT;
                case 1 -> player.playerAction = OGRE_SMASH_LEFT;
                case 2 -> player.playerAction = OGRE_SMASH_UP;
                case 3 -> player.playerAction = OGRE_SMASH_DOWN;
            }
        } else if (justFinishedCharging) {
            switch (player.getFacingDir()) {
                case 0 -> player.playerAction = OGRE_END_SMASH_RIGHT;
                case 1 -> player.playerAction = OGRE_END_SMASH_LEFT;
                case 2 -> player.playerAction = OGRE_END_SMASH_UP;
                case 3 -> player.playerAction = OGRE_END_SMASH_DOWN;
            }
        }
    }

    public boolean isChargingAttack() { return chargingAttack; }

    public void smashAttack(boolean isHolding) {
        if (!abilityUsed) {
            chargingAttack = isHolding;
            chargingTime = 0;
            if (!isHolding) {
                player.setMovementSpeed(player.getBaseMovementSpeed());
                justFinishedCharging = true;
                abilityUsed = true;
            } else {
                justFinishedCharging = false;
            }
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
