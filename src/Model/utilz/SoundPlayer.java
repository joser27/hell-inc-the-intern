package Model.utilz;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.InputStream;

/**
 * Sound playback: preloaded one-shot clips (knock) and looping menu music.
 * Clips are loaded once and replayed — avoids thread/stream/GC issues with short sounds.
 */
public final class SoundPlayer {

    private static final String[] KNOCK_PATHS = {
            "audio/knock.wav", "audio/knock.ogg",
            "res/audio/knock.wav", "res/audio/knock.ogg",
            "/audio/knock.wav", "/audio/knock.ogg",
            "/res/audio/knock.wav", "/res/audio/knock.ogg"
    };

    private static final String[] INTRO_MUSIC_PATHS = {
            "audio/introMusic.wav", "res/audio/introMusic.wav",
            "/audio/introMusic.wav", "/res/audio/introMusic.wav"
    };

    private static volatile Clip menuMusicClip = null;
    private static volatile boolean menuMusicLoading = false;
    private static volatile boolean menuMusicWanted = false;
    private static volatile Clip knockClip = null;
    private static volatile boolean knockLoading = false;

    /** Global volume 0–100. Applied to all clips. */
    private static volatile int masterVolume = 100;

    public static int getMasterVolume() { return masterVolume; }
    public static void setMasterVolume(int percent) {
        masterVolume = Math.max(0, Math.min(100, percent));
        applyVolumeToClip(menuMusicClip);
    }

    /** Set gain on a clip from current masterVolume. 0 = mute (-80 dB), 100 = full (0 dB). */
    static void applyVolumeToClip(Clip clip) {
        if (clip == null || !clip.isOpen()) return;
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();
            float gain = (masterVolume == 0) ? min : min + (masterVolume / 100f) * (max - min);
            gainControl.setValue(gain);
        } catch (Exception ignored) { /* MASTER_GAIN not supported on this line */ }
    }

    // --- Menu music (looping) ---

    public static void startMenuMusic() {
        menuMusicWanted = true;
        Clip clip = menuMusicClip;
        if (clip != null && clip.isOpen()) {
            if (!clip.isRunning()) {
                applyVolumeToClip(clip);
                clip.setFramePosition(0);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            return;
        }
        if (menuMusicLoading) return;
        menuMusicLoading = true;
        Thread t = new Thread(() -> {
            try {
                InputStream in = findResource(INTRO_MUSIC_PATHS);
                if (in == null) return;
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
                Clip c = AudioSystem.getClip();
                c.open(audioIn);
                menuMusicClip = c;
                applyVolumeToClip(c);
                if (menuMusicWanted) {
                    c.loop(Clip.LOOP_CONTINUOUSLY);
                }
            } catch (Exception e) {
                System.err.println("Menu music: " + e.getMessage());
            } finally {
                menuMusicLoading = false;
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public static void stopMenuMusic() {
        menuMusicWanted = false;
        Clip clip = menuMusicClip;
        if (clip != null && clip.isOpen() && clip.isRunning())
            clip.stop();
    }

    // --- Knock (preloaded one-shot) ---

    /** Call once at startup to preload the knock clip. */
    public static void preloadKnock() {
        if (knockClip != null || knockLoading) return;
        knockLoading = true;
        Thread t = new Thread(() -> {
            try {
                InputStream in = findResource(KNOCK_PATHS);
                if (in == null) {
                    System.err.println("[Knock] Preload: resource not found");
                    return;
                }
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
                Clip c = AudioSystem.getClip();
                c.open(audioIn);
                knockClip = c;
                System.out.println("[Knock] Preloaded — format: " + audioIn.getFormat()
                        + ", frames: " + c.getFrameLength()
                        + ", duration: " + (c.getMicrosecondLength() / 1000) + " ms");
            } catch (Exception e) {
                System.err.println("[Knock] Preload failed: " + e);
            } finally {
                knockLoading = false;
            }
        });
        t.setDaemon(true);
        t.start();
    }

    /** Play knock once. Uses preloaded clip — instant, no thread needed. */
    public static void playKnock() {
        Clip c = knockClip;
        if (c == null || !c.isOpen()) return;
        applyVolumeToClip(c);
        c.stop();
        c.setFramePosition(0);
        c.start();
    }

    // --- Util ---

    private static InputStream findResource(String[] paths) {
        for (String path : paths) {
            InputStream in = SoundPlayer.class.getResourceAsStream(path.startsWith("/") ? path : "/" + path);
            if (in == null) in = SoundPlayer.class.getClassLoader().getResourceAsStream(path);
            if (in != null) return in;
        }
        return null;
    }
}
