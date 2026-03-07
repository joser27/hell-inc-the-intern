package View;

import Controller.GameController;
import Model.Game;
import Model.gamestates.Playing;
import Model.gamestates.PlayingUI;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renders the Playing state: full-screen game world and Player1 ability UI.
 * When the widow encounter is active, draws the first-person door frame (widowFrame.png) over the overworld; ESC closes it.
 */
public class PlayingView {
    private final GameView gameView = new GameView();
    private BufferedImage widowFrameImage;

    public void render(Graphics g, Game game, Playing playing) {
        gameView.render(g, game, playing.getXLvlOffset(), playing.getYLvlOffset());
        drawPlayingUI(g, playing.getPlayingUI());
        if (game.isShowWidowFrame()) {
            drawWidowFrame(g);
        }
    }

    /** First-person encounter view for the Widow (Demonic Contractor: door encounter). */
    private void drawWidowFrame(Graphics g) {
        if (widowFrameImage == null) {
            try {
                widowFrameImage = LoadSave.GetSpriteAtlas(LoadSave.WIDOW_FRAME);
            } catch (Exception e) {
                g.setColor(new Color(40, 20, 30));
                g.fillRect(0, 0, GameController.GAME_WIDTH, GameController.GAME_HEIGHT);
                g.setColor(Color.WHITE);
                g.drawString("Widow encounter (widowFrame.png not found)", 50, 100);
                drawEscapeHint(g);
                return;
            }
        }
        int w = GameController.GAME_WIDTH;
        int h = GameController.GAME_HEIGHT;
        g.drawImage(widowFrameImage, 0, 0, w, h, null);
        drawEscapeHint(g);
    }

    private void drawEscapeHint(Graphics g) {
        g.setColor(new Color(255, 255, 255, 180));
        g.drawString("ESC — Back to overworld", 20, GameController.GAME_HEIGHT - 20);
    }

    private void drawPlayingUI(Graphics g, PlayingUI ui) {
        // if (ui == null) return;
        // int barY = GameController.GAME_HEIGHT - 50 * GameController.SCALE;
        // g.setColor(new Color(255, 255, 255, 100));
        // g.fillRect(0, barY - 10 * GameController.SCALE, GameController.GAME_WIDTH, 60 * GameController.SCALE);
        // int startX = GameController.GAME_WIDTH / 2 - (5 * 20 * GameController.SCALE) / 2;
        // g.drawImage(ui.getPlayer1_P(), startX, barY, null);
        // g.drawImage(ui.getPlayer1_Q(), startX + 20 * GameController.SCALE, barY, null);
        // g.drawImage(ui.getPlayer1_W(), startX + 40 * GameController.SCALE, barY, null);
        // g.drawImage(ui.getPlayer1_E(), startX + 60 * GameController.SCALE, barY, null);
        // g.drawImage(ui.getPlayer1_R(), startX + 80 * GameController.SCALE, barY, null);
    }

}
