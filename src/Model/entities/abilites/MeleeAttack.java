package Model.entities.abilites;

import Model.entities.Player;

import java.awt.geom.Rectangle2D;

import static Model.utilz.Constants.PlayerConstants.*;
import static Model.utilz.Constants.PlayerConstants.OGRE_ATTACK_DOWN;

public class MeleeAttack extends Ability {
    public Rectangle2D.Float attackHitBox ;
//    public boolean attackingMelee  = false;
//    public int attackingCD = 20;
//    public int attackingTimer;
//    public int attackingDuration;
//    public boolean canAttack = true;

    public MeleeAttack(Player player, int scale, int xPos, int yPos, int cd) {
        super(player, scale, xPos, yPos, cd);
        attackHitBox = new Rectangle2D.Float(xPos,yPos,scale,scale);
        abilityUptime=120;//1 second
    }

    @Override
    public void update() {
        attackingUpdate();
        updateUI();
    }


    private void attackingUpdate() {
        if (abilityUsed) {
//            abilityActive=true;
//            abilityCoolDownTick--;
//
//            abilityUptimeTicker++;
//            if (abilityUptimeTicker>abilityUptime) {
//                abilityActive=false;
//            }
            if (abilityUsed) {
                switch (player.getFacingDir()) {
                    case 0 -> {
                        attackHitBox.x = player.getxPos() + 10;
                        attackHitBox.y = player.getyPos() - 6;
                        player.playerAction = OGRE_ATTACK_RIGHT;
                    }
                    case 1 -> {
                        attackHitBox.x = player.getxPos() - 18;
                        attackHitBox.y = player.getyPos() - 6;
                        player.playerAction = OGRE_ATTACK_LEFT;
                    }
                    case 2 -> {
                        attackHitBox.x = player.getxPos() - 6;
                        attackHitBox.y = player.getyPos() - 15;
                        player.playerAction = OGRE_ATTACK_UP;
                    }
                    case 3 -> {
                        attackHitBox.x = player.getxPos() - 6;
                        attackHitBox.y = player.getyPos() + 4;
                        player.playerAction = OGRE_ATTACK_DOWN;
                    }
                }
            }
        }


    }



//    private void attackingUpdate() {
//        if (abilityUsed) {
//
//        }
//        attackingTimer++;
//        if (attackingTimer > attackingCD) {
//            attackingTimer=0;
//            canAttack = true;
//        }
//        if (abilityUsed) {
//            attackingDuration++;
//            if (attackingDuration>cd) {
//                attackingDuration=0;
//                attackingTimer=0;
//                abilityUsed=false;
//            }
//        }
//
//        switch(player.getFacingDir()) {//0 = right, 1 = left, 2 = up, 3 = down
//            case 0 -> {
//                attackHitBox.x = player.getxPos()+10;
//                attackHitBox.y = player.getyPos()-6;
//                if (attackingMelee)player.playerAction = OGRE_ATTACK_RIGHT;
//
//            }
//            case 1 -> {
//                attackHitBox.x = player.getxPos()-18;
//                attackHitBox.y = player.getyPos()-6;
//                if (attackingMelee)player.playerAction = OGRE_ATTACK_LEFT;
//            }
//            case 2 -> {
//                attackHitBox.x = player.getxPos()-6;
//                attackHitBox.y = player.getyPos()-15;
//                if (attackingMelee)player.playerAction = OGRE_ATTACK_UP;
//            }
//            case 3 -> {
//                attackHitBox.x = player.getxPos()-6;
//                attackHitBox.y = player.getyPos()+4;
//                if (attackingMelee)player.playerAction = OGRE_ATTACK_DOWN;
//            }
//        }
//    }
    // Animation state: set in update when attacking (View only draws)

//    public void attack() {
//        if (canAttack) {
//            canAttack = false;
//            attackingMelee = true;
//        }
//    }
}
