package Model.gamestates;

import Controller.GameController;
import Model.Game;
import Model.utilz.SoundPlayer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class OptionsMenu extends State implements Statemethods {
    public static final int BUTTON_FULLSCREEN = 0;
    public static final int BUTTON_RESUME_OR_BACK = 1;
    public static final int BUTTON_MAIN_MENU = 2;
    public static final int BUTTON_NONE = -1;

    private final GameController controller;
    private Gamestate returnState = Gamestate.MENU;
    private int hoveredButton = BUTTON_NONE;
    private final Rectangle[] buttonBounds = new Rectangle[3];
    /** Slider track for sound volume (0–100). */
    private final Rectangle volumeSliderBounds = new Rectangle();
    private boolean draggingVolume = false;

    public OptionsMenu(Game game, GameController controller) {
        super(game);
        this.controller = controller;
        for (int i = 0; i < 3; i++) buttonBounds[i] = new Rectangle();
        layoutButtons();
    }

    public void setReturnState(Gamestate state) { this.returnState = state; }

    /** True when options was opened from in-game (ESC); show Resume + Main menu. */
    public boolean isOpenedFromPlaying() { return returnState == Gamestate.PLAYING; }

    private void layoutButtons() {
        int w = controller.getDisplayWidth();
        int h = controller.getDisplayHeight();
        int centerX = w / 2;
        int btnW = 280;
        int btnH = 48;
        int gap = 52;
        int sliderW = 280;
        int sliderH = 20;
        int ySlider = (int) (h * 0.28);
        volumeSliderBounds.setBounds(centerX - sliderW / 2, ySlider - sliderH / 2, sliderW, sliderH);
        int y1 = (int) (h * 0.36);
        buttonBounds[0].setBounds(centerX - btnW / 2, y1 - btnH / 2, btnW, btnH);
        int y2 = y1 + btnH + gap;
        buttonBounds[1].setBounds(centerX - btnW / 2, y2 - btnH / 2, btnW, btnH);
        int y3 = y2 + btnH + gap;
        buttonBounds[2].setBounds(centerX - btnW / 2, y3 - btnH / 2, btnW, btnH);
    }

    @Override
    public void update() {
        layoutButtons();
    }

    @Override
    public void render(Graphics g) { /* Done by OptionsView */ }

    public boolean isFullscreen() { return controller != null && controller.isFullscreen(); }

    public void setFullscreen(boolean fullscreen) {
        if (controller != null) controller.setFullscreen(fullscreen);
    }

    public int getVolume() { return SoundPlayer.getMasterVolume(); }
    public void setVolume(int percent) { SoundPlayer.setMasterVolume(percent); }

    public Rectangle getVolumeSliderBounds() { return volumeSliderBounds; }

    /** Update volume from slider X (call when user clicks/drags on track). */
    public void setVolumeFromSliderX(int pixelX) {
        int x = volumeSliderBounds.x;
        int w = volumeSliderBounds.width;
        int pct = (pixelX <= x) ? 0 : (pixelX >= x + w) ? 100 : (int) ((pixelX - x) * 100f / w);
        setVolume(pct);
    }

    public boolean isDraggingVolume() { return draggingVolume; }
    public void setDraggingVolume(boolean v) { this.draggingVolume = v; }

    public Rectangle getButtonBounds(int index) { return buttonBounds[index]; }
    public int getHoveredButton() { return hoveredButton; }

    public int getButtonAt(int x, int y) {
        int count = isOpenedFromPlaying() ? 3 : 2;
        for (int i = 0; i < count; i++) {
            if (buttonBounds[i].contains(x, y)) return i;
        }
        return BUTTON_NONE;
    }

    public void setHoveredButton(int button) { this.hoveredButton = button; }

    public void triggerButton(int button) {
        if (button == BUTTON_FULLSCREEN) {
            setFullscreen(!isFullscreen());
        } else if (button == BUTTON_RESUME_OR_BACK) {
            Gamestate.state = returnState;
        } else if (button == BUTTON_MAIN_MENU) {
            Gamestate.state = Gamestate.MENU;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) {
        if (volumeSliderBounds.contains(e.getX(), e.getY())) {
            draggingVolume = true;
            setVolumeFromSliderX(e.getX());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        draggingVolume = false;
        int btn = getButtonAt(e.getX(), e.getY());
        if (btn != BUTTON_NONE) triggerButton(btn);
    }

    public void mouseDragged(MouseEvent e) {
        if (draggingVolume) setVolumeFromSliderX(e.getX());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        setHoveredButton(getButtonAt(e.getX(), e.getY()));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            Gamestate.state = returnState;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }
}
