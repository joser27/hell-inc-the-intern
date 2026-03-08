package Model.utilz;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.InputStream;

/**
 * One-shot sound playback. Plays in a background thread so the game loop isn't blocked.
 * OGG support requires a SPI (e.g. VorbisSPI) on the classpath; otherwise playback is skipped.
 */
public final class SoundPlayer {

    /** Paths tried in order to load knock.ogg from res/audio. */
    private static final String[] KNOCK_PATHS = { "audio/knock.ogg", "res/audio/knock.ogg", "/audio/knock.ogg", "/res/audio/knock.ogg" };

    /** Play knock.ogg once (non-blocking). Safe to call from game thread. */
    public static void playKnock() {
        Thread t = new Thread(() -> {
            try {
                InputStream in = null;
                for (String path : KNOCK_PATHS) {
                    in = SoundPlayer.class.getResourceAsStream(path.startsWith("/") ? path : "/" + path);
                    if (in == null) in = SoundPlayer.class.getClassLoader().getResourceAsStream(path);
                    if (in != null) break;
                }
                if (in == null) return;
                try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(in)) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                    // Don't close clip until done (clip runs in background)
                    while (clip.isRunning()) Thread.sleep(20);
                    clip.close();
                }
            } catch (Exception ignored) {
                // OGG may be unsupported without VorbisSPI; game continues without sound
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
