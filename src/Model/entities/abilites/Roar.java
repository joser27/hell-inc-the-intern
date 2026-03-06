package Model.entities.abilites;

import Model.entities.Player;

public class Roar extends Ability {
    boolean canUseAbility=true;
    int roarCDTick;
    Projectile projectile;

    public Roar(Player player, int scale, int xPos, int yPos, int cd) {
        super(player, scale, xPos, yPos, cd);

    }

    public void useRoar() {
        if (canUseAbility) {
            projectile = new Projectile(player, (int) player.getHitBox().x, (int) player.getHitBox().y,null,0);
            abilityUsed=true;
            canUseAbility=false;
        }
    }
    @Override
    public void update() {//0 = right, 1 = left, 2 = up, 3 = down
        updateUI();
        if (abilityUsed) {
            projectile.updateProjectile();
        }
    }

    public Projectile getProjectile() { return projectile; }
}
