package Model.gamestates;

import Model.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Random;

/**
 * Fake loading screen shown when transitioning Menu → Playing.
 * Holds for LOADING_DURATION_MS then resets game and switches to PLAYING.
 */
public class Loading extends State implements Statemethods {
    private static final long LOADING_DURATION_MS = 2500L;

    public static final String[] FLAVOR_LINES = {
        "Loneliness is the oldest door. It is rarely locked.",
        "The devout are not immune to temptation. They are simply better at pretending.",
        "A demon who listens well will always outperform one who speaks cleverly.",
        "Gerald Finch knows something about everyone. Use this.",
        "Father Creed has never lost an investigation. There is a first time for everything.",
        "Souls collected do not sleep. Neither should you.",
        "Harwick still owes Aldous money. Some debts are more useful unpaid."
    };

    private long startTime = 0;
    private int flavorIndex = 0;
    private final Random rnd = new Random();

    public Loading(Game game) {
        super(game);
    }

    /** Call when entering LOADING state so timer and flavor text are set. */
    public void enter() {
        startTime = System.currentTimeMillis();
        flavorIndex = rnd.nextInt(FLAVOR_LINES.length);
    }

    public String getFlavorLine() {
        if (flavorIndex >= 0 && flavorIndex < FLAVOR_LINES.length)
            return FLAVOR_LINES[flavorIndex];
        return "";
    }

    /** Call when leaving so next enter() re-initializes. */
    public void leave() {
        startTime = 0;
    }

    public long getStartTime() { return startTime; }
    public int getFlavorIndex() { return flavorIndex; }

    @Override
    public void update() {
        if (startTime == 0) enter();
        if (System.currentTimeMillis() - startTime >= LOADING_DURATION_MS) {
            getGame().resetForNewGame();
            leave();
            Gamestate.state = Gamestate.PLAYING;
        }
    }

    @Override
    public void render(Graphics g) { /* Done by LoadingView */ }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }
}
