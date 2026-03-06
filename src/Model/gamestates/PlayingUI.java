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

    // Player 2 Abilities
    Image player2_P;
    Image player2_Q;
    Image player2_W;
    Image player2_E;
    Image player2_R;

    public PlayingUI(Game game) {//(GameController.GAME_HEIGHT/2) + GameController.GAME_HEIGHT/4
        super(game);
        // Player 1 Abilities
        player1_P = LoadSave.GetSpriteAtlas(LoadSave.Death_Surge_PX);
        player1_Q = LoadSave.GetSpriteAtlas(LoadSave.Decimating_Smash_PX);
        player1_W = LoadSave.GetSpriteAtlas(LoadSave.Soul_Furnace_PX);
        player1_E = LoadSave.GetSpriteAtlas(LoadSave.Roar_of_the_Slayer_PX);
        player1_R = LoadSave.GetSpriteAtlas(LoadSave.Unstoppable_Onslaught_PX);

        //Player 2
        player2_P = LoadSave.GetSpriteAtlas(LoadSave.Frost_Shot);
        player2_Q = LoadSave.GetSpriteAtlas(LoadSave.Ranger_Focus);
        player2_W = LoadSave.GetSpriteAtlas(LoadSave.Volley);
        player2_E = LoadSave.GetSpriteAtlas(LoadSave.Hawkshot);
        player2_R = LoadSave.GetSpriteAtlas(LoadSave.Enchanted_Crystal_Arrow);
    }

    @Override
    public void update() {

    }

    public Image getPlayer1_P() { return player1_P; }
    public Image getPlayer1_Q() { return player1_Q; }
    public Image getPlayer1_W() { return player1_W; }
    public Image getPlayer1_E() { return player1_E; }
    public Image getPlayer1_R() { return player1_R; }
    public Image getPlayer2_P() { return player2_P; }
    public Image getPlayer2_Q() { return player2_Q; }
    public Image getPlayer2_W() { return player2_W; }
    public Image getPlayer2_E() { return player2_E; }
    public Image getPlayer2_R() { return player2_R; }

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
