package Model.gamestates;

import Controller.GameController;
import Model.Game;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class LoadMenu extends State implements Statemethods {
    public static final int BUTTON_PLAY = 0;
    public static final int BUTTON_OPTIONS = 1;
    public static final int BUTTON_ABOUT = 2;
    public static final int BUTTON_QUIT = 3;
    public static final int BUTTON_NONE = -1;

    private final GameController controller;
    private BufferedImage backgroundImage;
    private int hoveredButton = BUTTON_NONE;

    private final Rectangle[] buttonBounds = new Rectangle[4];

    public LoadMenu(Game game, GameController controller) {
        super(game);
        this.controller = controller;
        backgroundImage = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
        for (int i = 0; i < 4; i++) buttonBounds[i] = new Rectangle();
        layoutButtons();
    }

    private void layoutButtons() {
        int w = controller.getDisplayWidth();
        int h = controller.getDisplayHeight();
        int btnW = 280;
        int btnH = 48;
        int centerX = w / 2;
        int startY = (int) (h * 0.54);
        int gap = 48;
        for (int i = 0; i < 4; i++) {
            int y = startY + i * (btnH + gap);
            buttonBounds[i].setBounds(centerX - btnW / 2, y - btnH / 2, btnW, btnH);
        }
    }

    @Override
    public void update() {
        layoutButtons();
    }

    @Override
    public void render(Graphics g) { /* Done by LoadMenuView */ }

    public BufferedImage getBackgroundImage() { return backgroundImage; }
    public Rectangle getButtonBounds(int index) { return buttonBounds[index]; }
    public int getHoveredButton() { return hoveredButton; }

    /** Returns BUTTON_PLAY, BUTTON_OPTIONS, BUTTON_ABOUT, BUTTON_QUIT, or BUTTON_NONE. */
    public int getButtonAt(int x, int y) {
        for (int i = 0; i < 4; i++) {
            if (buttonBounds[i].contains(x, y)) return i;
        }
        return BUTTON_NONE;
    }

    public void setHoveredButton(int button) { this.hoveredButton = button; }

    public void triggerButton(int button) {
        if (button == BUTTON_PLAY) {
            Gamestate.state = Gamestate.MODE_SELECT;
        } else if (button == BUTTON_OPTIONS) {
            controller.getOptionsMenu().setReturnState(Gamestate.MENU);
            Gamestate.state = Gamestate.OPTIONS;
        } else if (button == BUTTON_ABOUT) {
            Gamestate.state = Gamestate.ABOUT;
        } else if (button == BUTTON_QUIT) {
            controller.quitGame();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int btn = getButtonAt(x, y);
        if (btn != BUTTON_NONE) triggerButton(btn);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        setHoveredButton(getButtonAt(e.getX(), e.getY()));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            Gamestate.state = Gamestate.MODE_SELECT;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }
}
