package Model.gamestates;

import Model.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Transition screen shown in Endless when the player meets the assignment quota.
 * Displays a Gary message, stats, and the next assignment's quota. ENTER → next assignment.
 * Campaign never reaches this state — it goes straight to GAMEOVER (win).
 */
public class DaySummary extends State implements Statemethods {

    private static final String[] GARY_MESSAGES = {
            "OUTSTANDING. I never doubted you. Please disregard all previous messages. — Gary",
            "Great numbers. I told the Director you were my pick. I did not, but I will now. — Gary",
            "Good news: quota met. Bad news: new quota. Corporate never sleeps. — Gary",
            "You hit your numbers! Now hit bigger numbers. That's how growth works. — Gary",
            "Unfortunately, greatness is the new baseline. — Gary",
            "I've raised your target. Think of it as a compliment. — Gary",
            "Quota complete. I've adjusted expectations upward. You're welcome. — Gary",
    };

    private String garyMessage = "";
    private int snapshotSouls;
    private int snapshotQuota;
    private float snapshotSuspicion;
    private int snapshotTotalSouls;
    private int nextQuota;

    public DaySummary(Game game) {
        super(game);
    }

    /** Call when entering DAY_SUMMARY (Endless only). */
    public void enter() {
        Game g = getGame();
        snapshotSouls = g.getSouls();
        snapshotQuota = g.getSoulQuota();
        snapshotSuspicion = g.getSuspicion();
        snapshotTotalSouls = g.getTotalSouls();
        nextQuota = g.getNextAssignmentQuota();
        garyMessage = pick(GARY_MESSAGES);
    }

    public String getGaryMessage() { return garyMessage; }
    public int getSnapshotSouls() { return snapshotSouls; }
    public int getSnapshotQuota() { return snapshotQuota; }
    public float getSnapshotSuspicion() { return snapshotSuspicion; }
    public int getSnapshotTotalSouls() { return snapshotTotalSouls; }
    public int getNextQuota() { return nextQuota; }

    /** Player pressed ENTER — advance to next assignment (Endless). */
    public void confirm() {
        getGame().advanceAfterQuota();
        Gamestate.state = Gamestate.PLAYING;
    }

    private static String pick(String[] arr) {
        return arr[ThreadLocalRandom.current().nextInt(arr.length)];
    }

    @Override public void update() { }
    @Override public void render(Graphics g) { }
    @Override public void mouseClicked(MouseEvent e) { }
    @Override public void mousePressed(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseMoved(MouseEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
            confirm();
        }
    }

    @Override public void keyReleased(KeyEvent e) { }
}
