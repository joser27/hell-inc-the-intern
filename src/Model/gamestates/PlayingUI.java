package Model.gamestates;

import Controller.GameController;
import Model.Game;
import Model.LevelLoader;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PlayingUI extends State implements Statemethods {

    // Player 1 Abilities
    Image player1_P;
    Image player1_Q;
    Image player1_W;
    Image player1_E;
    Image player1_R;

    public PlayingUI(Game game) {
        super(game);
        player1_P = LoadSave.GetSpriteAtlas(LoadSave.Death_Surge_PX);
        player1_Q = LoadSave.GetSpriteAtlas(LoadSave.Decimating_Smash_PX);
        player1_W = LoadSave.GetSpriteAtlas(LoadSave.Soul_Furnace_PX);
        player1_E = LoadSave.GetSpriteAtlas(LoadSave.Roar_of_the_Slayer_PX);
        player1_R = LoadSave.GetSpriteAtlas(LoadSave.Unstoppable_Onslaught_PX);
    }

    @Override
    public void update() {

    }

    public Image getPlayer1_P() { return player1_P; }
    public Image getPlayer1_Q() { return player1_Q; }
    public Image getPlayer1_W() { return player1_W; }
    public Image getPlayer1_E() { return player1_E; }
    public Image getPlayer1_R() { return player1_R; }

    @Override
    public void render(Graphics g) { /* Rendering done by View.PlayingView */ }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
