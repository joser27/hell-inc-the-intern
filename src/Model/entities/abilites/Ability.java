package Model.entities.abilites;

import Model.entities.Player;

import java.awt.*;

public abstract class Ability {
    int scale;
    Player player;

    public Ability(Player player, int scale, int xPos, int yPos) {
        this.scale = scale;
        this.player = player;
    }

    public abstract void update();

    public abstract void render(Graphics g, int xLvlOffset, int yLvlOffset);
}
