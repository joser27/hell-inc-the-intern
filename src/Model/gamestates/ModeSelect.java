package Model.gamestates;

import Controller.GameController;
import Model.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Mode selection screen shown after pressing Play on the main menu.
 * Three buttons: Campaign, Endless, and a toggleable How-to-Play panel.
 */
public class ModeSelect extends State implements Statemethods {
    public static final int BTN_CAMPAIGN = 0;
    public static final int BTN_ENDLESS = 1;
    public static final int BTN_HOW_TO_PLAY = 2;
    public static final int BTN_BACK = 3;
    public static final int BTN_NONE = -1;

    private final GameController controller;
    private final Rectangle[] buttonBounds = new Rectangle[4];
    private int hoveredButton = BTN_NONE;
    private boolean showingHowToPlay = false;

    public ModeSelect(Game game, GameController controller) {
        super(game);
        this.controller = controller;
        for (int i = 0; i < buttonBounds.length; i++) buttonBounds[i] = new Rectangle();
        layoutButtons();
    }

    private void layoutButtons() {
        int w = controller.getDisplayWidth();
        int h = controller.getDisplayHeight();
        int btnW = 320;
        int btnH = 52;
        int centerX = w / 2;
        int startY = (int) (h * 0.40);
        int gap = 28;
        for (int i = 0; i < buttonBounds.length; i++) {
            int y = startY + i * (btnH + gap);
            buttonBounds[i].setBounds(centerX - btnW / 2, y, btnW, btnH);
        }
    }

    @Override
    public void update() { layoutButtons(); }

    @Override
    public void render(Graphics g) { /* ModeSelectView handles rendering */ }

    public Rectangle getButtonBounds(int idx) { return buttonBounds[idx]; }
    public int getHoveredButton() { return hoveredButton; }
    public boolean isShowingHowToPlay() { return showingHowToPlay; }

    public int getButtonAt(int x, int y) {
        for (int i = 0; i < buttonBounds.length; i++) {
            if (buttonBounds[i].contains(x, y)) return i;
        }
        return BTN_NONE;
    }

    public void triggerButton(int btn) {
        switch (btn) {
            case BTN_CAMPAIGN -> {
                showingHowToPlay = false;
                getGame().setGameMode(Game.GameMode.CAMPAIGN);
                Gamestate.state = Gamestate.LOADING;
            }
            case BTN_ENDLESS -> {
                showingHowToPlay = false;
                getGame().setGameMode(Game.GameMode.ENDLESS);
                Gamestate.state = Gamestate.LOADING;
            }
            case BTN_HOW_TO_PLAY -> showingHowToPlay = !showingHowToPlay;
            case BTN_BACK -> {
                showingHowToPlay = false;
                Gamestate.state = Gamestate.MENU;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) {
        int btn = getButtonAt(e.getX(), e.getY());
        if (btn != BTN_NONE) triggerButton(btn);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        hoveredButton = getButtonAt(e.getX(), e.getY());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            showingHowToPlay = false;
            Gamestate.state = Gamestate.MENU;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }
}
