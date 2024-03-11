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
        player1_P = LoadSave.GetSpriteAtlas(LoadSave.Death_Surge);
        player1_Q = LoadSave.GetSpriteAtlas(LoadSave.Decimating_Smash);
        player1_W = LoadSave.GetSpriteAtlas(LoadSave.Soul_Furnace);
        player1_E = LoadSave.GetSpriteAtlas(LoadSave.Roar_of_the_Slayer);
        player1_R = LoadSave.GetSpriteAtlas(LoadSave.Unstoppable_Onslaught);

        player2_P = LoadSave.GetSpriteAtlas(LoadSave.Frost_Shot);
        player2_Q = LoadSave.GetSpriteAtlas(LoadSave.Ranger_Focus);
        player2_W = LoadSave.GetSpriteAtlas(LoadSave.Volley);
        player2_E = LoadSave.GetSpriteAtlas(LoadSave.Roar_of_the_Slayer);
        player2_R = LoadSave.GetSpriteAtlas(LoadSave.Enchanted_Crystal_Arrow);




    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        //Player 1 (Right screen)
        g.setColor(new Color(100,200,100,200));
        g.fillRect(GameController.GAME_WIDTH/2,(GameController.GAME_HEIGHT/2) + GameController.GAME_HEIGHT/4,GameController.GAME_WIDTH/2,GameController.GAME_HEIGHT/6);

        g.drawImage(player1_P,GameController.GAME_WIDTH/2 + 10*GameController.SCALE,GameController.GAME_HEIGHT - 50*GameController.SCALE,null);
        g.drawImage(player1_Q,GameController.GAME_WIDTH/2 + 30*GameController.SCALE,GameController.GAME_HEIGHT - 50*GameController.SCALE,null);
        g.drawImage(player1_W,GameController.GAME_WIDTH/2 + 50*GameController.SCALE,GameController.GAME_HEIGHT - 50*GameController.SCALE,null);
        g.drawImage(player1_E,GameController.GAME_WIDTH/2 + 70*GameController.SCALE,GameController.GAME_HEIGHT - 50*GameController.SCALE,null);
        g.drawImage(player1_R,GameController.GAME_WIDTH/2 + 90*GameController.SCALE,GameController.GAME_HEIGHT - 50*GameController.SCALE,null);


        //Player 2 (Left screen)
        g.setColor(new Color(200,100,100,200));
        g.fillRect(0,(GameController.GAME_HEIGHT/2) + GameController.GAME_HEIGHT/4,GameController.GAME_WIDTH/2,GameController.GAME_HEIGHT/6);

        g.drawImage(player2_P,10*GameController.SCALE,GameController.GAME_HEIGHT - 50*GameController.SCALE,null);
        g.drawImage(player2_Q,30*GameController.SCALE,GameController.GAME_HEIGHT - 50*GameController.SCALE,null);
        g.drawImage(player2_W,50*GameController.SCALE,GameController.GAME_HEIGHT - 50*GameController.SCALE,null);
        g.drawImage(player2_E,70*GameController.SCALE,GameController.GAME_HEIGHT - 50*GameController.SCALE,null);
        g.drawImage(player2_R,90*GameController.SCALE,GameController.GAME_HEIGHT - 50*GameController.SCALE,null);


    }

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
