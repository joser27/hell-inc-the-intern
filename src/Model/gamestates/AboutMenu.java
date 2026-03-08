package Model.gamestates;

import Controller.GameController;
import Model.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class AboutMenu extends State implements Statemethods {
    public static final int BUTTON_BACK = 0;
    public static final int BUTTON_NONE = -1;

    private final GameController controller;
    private int hoveredButton = BUTTON_NONE;
    private final Rectangle backButtonBounds = new Rectangle();

    public AboutMenu(Game game, GameController controller) {
        super(game);
        this.controller = controller;
        layoutButtons();
    }

    private void layoutButtons() {
        int w = controller.getDisplayWidth();
        int h = controller.getDisplayHeight();
        int btnW = 200;
        int btnH = 44;
        backButtonBounds.setBounds(w / 2 - btnW / 2, (int) (h * 0.72) - btnH / 2, btnW, btnH);
    }

    @Override
    public void update() {
        layoutButtons();
    }

    @Override
    public void render(Graphics g) { /* Done by AboutView */ }

    public Rectangle getBackButtonBounds() { return backButtonBounds; }
    public int getHoveredButton() { return hoveredButton; }

    public int getButtonAt(int x, int y) {
        return backButtonBounds.contains(x, y) ? BUTTON_BACK : BUTTON_NONE;
    }

    public void setHoveredButton(int button) { this.hoveredButton = button; }

    public void triggerButton(int button) {
        if (button == BUTTON_BACK) Gamestate.state = Gamestate.MENU;
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) {
        int btn = getButtonAt(e.getX(), e.getY());
        if (btn != BUTTON_NONE) triggerButton(btn);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        setHoveredButton(getButtonAt(e.getX(), e.getY()));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) Gamestate.state = Gamestate.MENU;
    }

    @Override
    public void keyReleased(KeyEvent e) { }
}
