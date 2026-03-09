package Controller;

import Model.gamestates.Gamestate;
import View.GameView;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardInputs implements KeyListener {
    GameController gameController;
    public KeyboardInputs(GameController gameController) {
        this.gameController = gameController;
    }
    @Override
    public void keyTyped(KeyEvent e) {
        if (Gamestate.state == Gamestate.PLAYING) {
            gameController.getPlayingState().keyTyped(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F3) {
            GameView.toggleDebugHitboxes();
            return;
        }
        // Dev cheats — only on overworld (not during encounter typing)
        if (Gamestate.state == Gamestate.PLAYING && !gameController.getGame().isShowWidowFrame()) {
            if (handleDevCheat(e)) return;
        }
        switch(Gamestate.state) {
            case MENU -> gameController.getMenuState().keyPressed(e);
            case MODE_SELECT -> gameController.getModeSelectState().keyPressed(e);
            case DAY_SUMMARY -> gameController.getDaySummaryState().keyPressed(e);
            case OPTIONS -> gameController.getOptionsMenu().keyPressed(e);
            case ABOUT -> gameController.getAboutMenu().keyPressed(e);
            case PLAYING -> gameController.getPlayingState().keyPressed(e);
            case GAMEOVER -> gameController.getGameOverState().keyPressed(e);
            case LOADING -> gameController.getLoadingState().keyPressed(e);
            case PAUSEMENU -> gameController.getPauseMenu().keyPressed(e);
        }
    }

    /**
     * Dev cheat keys (overworld only, not during encounters):
     *   F7 — Toggle god mode (suspicion frozen)
     *   F8 — Add 1 soul toward quota
     *   F9 — Clear suspicion to 0
     *   F10 — Halve suspicion
     *   F11 — Instant win (fill quota)
     *   F12 — Reset NPC memory + collected souls
     */
    private boolean handleDevCheat(KeyEvent e) {
        Model.Game game = gameController.getGame();
        return switch (e.getKeyCode()) {
            case KeyEvent.VK_F7  -> { game.cheatToggleGodMode();    yield true; }
            case KeyEvent.VK_F8  -> { game.cheatAddSoul();          yield true; }
            case KeyEvent.VK_F9  -> { game.cheatClearSuspicion();   yield true; }
            case KeyEvent.VK_F10 -> { game.cheatHalveSuspicion();   yield true; }
            case KeyEvent.VK_F11 -> { game.cheatInstantWin();       yield true; }
            case KeyEvent.VK_F12 -> { game.cheatClearNpcMemory();   yield true; }
            default -> false;
        };
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(Gamestate.state) {
            case MENU -> gameController.getMenuState().keyReleased(e);
            case PLAYING -> gameController.getPlayingState().keyReleased(e);
        }
    }
}
