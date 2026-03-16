package Model.utilz;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Sound playback: preloaded one-shot clips (knock) and looping menu music.
 * Clips are loaded once and replayed — avoids thread/stream/GC issues with short sounds.
 */
public final class SoundPlayer {

    private static final String[] KNOCK_PATHS = {
            "audio/knock.ogg", "audio/knock.wav",
            "res/audio/knock.ogg", "res/audio/knock.wav",
            "/audio/knock.ogg", "/audio/knock.wav",
            "/res/audio/knock.ogg", "/res/audio/knock.wav"
    };

    private static final String[] STEP_PATHS = {
            "audio/Steps_dirt-008.ogg", "res/audio/Steps_dirt-008.ogg",
            "/audio/Steps_dirt-008.ogg", "/res/audio/Steps_dirt-008.ogg",
            "audio/Steps_dirt-008.wav", "res/audio/Steps_dirt-008.wav"
    };

    private static final String[] RUSTLE_PATHS = {
            "audio/rustle.ogg", "res/audio/rustle.ogg",
            "/audio/rustle.ogg", "/res/audio/rustle.ogg",
            "audio/rustle.wav", "res/audio/rustle.wav"
    };

    /** Menu music (main menu, options, about) — 19 10pm _ Towball's Crossing. */
    private static final String[] MENU_MUSIC_PATHS = {
            "audio/19-10pm-_-Towball_s-Crossing.ogg", "res/audio/19-10pm-_-Towball_s-Crossing.ogg",
            "/audio/19-10pm-_-Towball_s-Crossing.ogg", "/res/audio/19-10pm-_-Towball_s-Crossing.ogg",
            "audio/introMusic.ogg", "res/audio/introMusic.ogg",
            "/audio/introMusic.ogg", "/res/audio/introMusic.ogg"
    };

    private static final String[] NIGHT_AMBIENCE_PATHS = {
            "audio/nightAmbience.ogg", "res/audio/nightAmbience.ogg",
            "/audio/nightAmbience.ogg", "/res/audio/nightAmbience.ogg",
            "audio/nightAmbience.wav", "res/audio/nightAmbience.wav"
    };

    /** Encounter music: one track played at random when entering an NPC encounter (OGG only — WAVs removed). */
    private static final String[] ENCOUNTER_MUSIC_FILES = {
            "03 7am _ Towballs Crossing.ogg",
            "07 11am _ Towballs Crossing.ogg",
            "11 3pm _ Towballs Crossing.ogg",
            "25 4am _ Towballs Crossing.ogg",
            "26-5am-Goodbye-_-Towball_s-Crossing.ogg",
            "20 11pm _ Towballs Crossing.ogg"
    };
    private static final String[] ENCOUNTER_MUSIC_PATH_PREFIXES = {
            "audio/encounterMusic/", "res/audio/encounterMusic/",
            "/audio/encounterMusic/", "/res/audio/encounterMusic/"
    };

    private static volatile Clip menuMusicClip = null;
    private static volatile boolean menuMusicLoading = false;
    private static volatile boolean menuMusicWanted = false;
    private static volatile Clip knockClip = null;
    private static volatile boolean knockLoading = false;
    private static volatile Clip stepClip = null;
    private static volatile boolean stepLoading = false;
    private static volatile Clip rustleClip = null;
    private static volatile boolean rustleLoading = false;
    /** Minimum ms between rustle plays; prevents noise when stepping through multiple plants. */
    private static final int RUSTLE_COOLDOWN_MS = 220;
    private static volatile long lastRustlePlayTime = 0;

    private static volatile Clip encounterMusicClip = null;
    private static volatile boolean encounterMusicLoading = false;
    private static volatile boolean encounterMusicWanted = false;
    private static final AtomicBoolean encounterFadeOutRunning = new AtomicBoolean(false);

    private static volatile Clip nightAmbienceClip = null;
    private static volatile boolean nightAmbienceLoading = false;
    private static volatile boolean nightAmbienceWanted = false;

    /** Fade in/out duration in ms. */
    private static final int ENCOUNTER_FADE_IN_MS = 800;
    private static final int ENCOUNTER_FADE_OUT_MS = 600;
    private static final int FADE_STEP_MS = 16;

    /** Global volume 0–100. Applied to all clips. */
    private static volatile int masterVolume = 100;

    public static int getMasterVolume() { return masterVolume; }
    public static void setMasterVolume(int percent) {
        masterVolume = Math.max(0, Math.min(100, percent));
        applyVolumeToClip(menuMusicClip);
        applyVolumeToClip(encounterMusicClip);
        applyVolumeToClip(nightAmbienceClip);
        applyVolumeToClip(stepClip);
    }

    /** Set gain on a clip from current masterVolume. 0 = mute (-80 dB), 100 = full (0 dB). */
    static void applyVolumeToClip(Clip clip) {
        setGainFactor(clip, 1f);
    }

    /**
     * Set clip volume as a linear factor 0..1 of current master volume.
     * Prefers the linear VOLUME control for perceptually smooth fades.
     * Falls back to MASTER_GAIN with dB conversion (20*log10) so the ramp
     * sounds even instead of jumping at the end.
     */
    private static void setGainFactor(Clip clip, float factor) {
        if (clip == null || !clip.isOpen()) return;
        float vol = Math.max(0f, Math.min(1f, factor)) * (masterVolume / 100f);
        // Prefer linear VOLUME control (0.0–1.0) — perceptually smooth
        try {
            FloatControl volCtrl = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
            volCtrl.setValue(Math.max(volCtrl.getMinimum(), Math.min(vol, volCtrl.getMaximum())));
            return;
        } catch (Exception ignored) { /* VOLUME not available, fall back */ }
        // Fall back to MASTER_GAIN (dB) with proper log conversion
        try {
            FloatControl gainCtrl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gainCtrl.getMinimum();
            if (vol <= 0.0001f) {
                gainCtrl.setValue(min);
            } else {
                float dB = (float) (20.0 * Math.log10(vol));
                gainCtrl.setValue(Math.max(dB, min));
            }
        } catch (Exception ignored) { /* no gain control at all */ }
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
                InputStream in = findResource(MENU_MUSIC_PATHS);
                if (in == null) return;
                in = toMarkResetStream(in);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
                audioIn = toPcmIfNeeded(audioIn);
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

    // --- Night ambience (overworld when not in encounter) ---

    /** Start looping night ambience. Idempotent. */
    public static void startNightAmbience() {
        nightAmbienceWanted = true;
        Clip clip = nightAmbienceClip;
        if (clip != null && clip.isOpen()) {
            if (!clip.isRunning()) {
                applyVolumeToClip(clip);
                clip.setFramePosition(0);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            return;
        }
        if (nightAmbienceLoading) return;
        nightAmbienceLoading = true;
        Thread t = new Thread(() -> {
            try {
                InputStream in = findResource(NIGHT_AMBIENCE_PATHS);
                if (in == null) {
                    System.err.println("[Night ambience] Resource not found");
                    return;
                }
                in = toMarkResetStream(in);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
                audioIn = toPcmIfNeeded(audioIn);
                Clip c = AudioSystem.getClip();
                c.open(audioIn);
                nightAmbienceClip = c;
                applyVolumeToClip(c);
                if (nightAmbienceWanted) {
                    c.loop(Clip.LOOP_CONTINUOUSLY);
                }
            } catch (Exception e) {
                System.err.println("[Night ambience] " + e.getMessage());
            } finally {
                nightAmbienceLoading = false;
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public static void stopNightAmbience() {
        nightAmbienceWanted = false;
        Clip clip = nightAmbienceClip;
        if (clip != null && clip.isOpen() && clip.isRunning())
            clip.stop();
    }

    // --- Encounter music (random track, looping, fade in/out) ---

    /** Start a random encounter track when entering an NPC encounter. Fades in, loops until stopEncounterMusic(). */
    public static void startEncounterMusic() {
        encounterMusicWanted = true;
        // If already playing, just ensure volume (e.g. after options change)
        Clip existing = encounterMusicClip;
        if (existing != null && existing.isOpen() && existing.isRunning()) {
            applyVolumeToClip(existing);
            return;
        }
        if (encounterMusicLoading) return;
        encounterMusicLoading = true;
        String file = ENCOUNTER_MUSIC_FILES[ThreadLocalRandom.current().nextInt(ENCOUNTER_MUSIC_FILES.length)];
        Thread t = new Thread(() -> {
            try {
                InputStream in = findEncounterMusicResource(file);
                if (in == null) {
                    System.err.println("[Encounter music] Resource not found: " + file);
                    return;
                }
                in = toMarkResetStream(in);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
                audioIn = toPcmIfNeeded(audioIn);
                Clip c = AudioSystem.getClip();
                c.open(audioIn);
                encounterMusicClip = c;
                if (encounterMusicWanted) {
                    c.loop(Clip.LOOP_CONTINUOUSLY);
                    setGainFactor(c, 0f);
                    runFadeIn(c);
                }
            } catch (Exception e) {
                System.err.println("[Encounter music] " + e.getMessage());
            } finally {
                encounterMusicLoading = false;
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private static void runFadeIn(Clip clip) {
        Thread fade = new Thread(() -> {
            if (!encounterMusicWanted || clip == null || !clip.isOpen()) return;
            setGainFactor(clip, 0f);
            int steps = Math.max(1, ENCOUNTER_FADE_IN_MS / FADE_STEP_MS);
            for (int i = 1; i <= steps && encounterMusicWanted; i++) {
                setGainFactor(clip, i / (float) steps);
                try { Thread.sleep(FADE_STEP_MS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
            }
            if (encounterMusicWanted && clip.isOpen()) setGainFactor(clip, 1f);
        });
        fade.setDaemon(true);
        fade.start();
    }

    /** Fade out encounter music then stop. Non-blocking. */
    public static void stopEncounterMusic() {
        encounterMusicWanted = false;
        Clip clip = encounterMusicClip;
        if (clip == null || !clip.isOpen()) return;
        if (!clip.isRunning()) return;
        if (!encounterFadeOutRunning.compareAndSet(false, true)) return;
        Thread fadeOut = new Thread(() -> {
            try {
                int steps = Math.max(1, ENCOUNTER_FADE_OUT_MS / FADE_STEP_MS);
                for (int i = steps; i >= 0; i--) {
                    setGainFactor(clip, i / (float) steps);
                    if (i > 0) {
                        try { Thread.sleep(FADE_STEP_MS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                    }
                }
                if (clip.isOpen() && clip.isRunning())
                    clip.stop();
            } finally {
                encounterFadeOutRunning.set(false);
            }
        });
        fadeOut.setDaemon(true);
        fadeOut.start();
    }

    private static InputStream findEncounterMusicResource(String fileName) {
        for (String prefix : ENCOUNTER_MUSIC_PATH_PREFIXES) {
            String path = prefix + fileName;
            InputStream in = SoundPlayer.class.getResourceAsStream(path.startsWith("/") ? path : "/" + path);
            if (in == null) in = SoundPlayer.class.getClassLoader().getResourceAsStream(path);
            if (in != null) return in;
        }
        return null;
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
                in = toMarkResetStream(in);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
                audioIn = toPcmIfNeeded(audioIn);
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

    // --- Player steps (preloaded one-shot) ---

    /** Call once at startup to preload the step clip. */
    public static void preloadSteps() {
        if (stepClip != null || stepLoading) return;
        stepLoading = true;
        Thread t = new Thread(() -> {
            try {
                InputStream in = findResource(STEP_PATHS);
                if (in == null) {
                    System.err.println("[Steps] Preload: resource not found");
                    return;
                }
                in = toMarkResetStream(in);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
                audioIn = toPcmIfNeeded(audioIn);
                Clip c = AudioSystem.getClip();
                c.open(audioIn);
                stepClip = c;
            } catch (Exception e) {
                System.err.println("[Steps] Preload failed: " + e);
            } finally {
                stepLoading = false;
            }
        });
        t.setDaemon(true);
        t.start();
    }

    /** Play one step. Uses preloaded clip. */
    public static void playStep() {
        Clip c = stepClip;
        if (c == null || !c.isOpen()) return;
        applyVolumeToClip(c);
        c.stop();
        c.setFramePosition(0);
        c.start();
    }

    // --- Plant rustle (preloaded one-shot) ---

    /** Call once at startup to preload the rustle clip. */
    public static void preloadRustle() {
        if (rustleClip != null || rustleLoading) return;
        rustleLoading = true;
        Thread t = new Thread(() -> {
            try {
                InputStream in = findResource(RUSTLE_PATHS);
                if (in == null) {
                    System.err.println("[Rustle] Preload: resource not found");
                    return;
                }
                in = toMarkResetStream(in);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
                audioIn = toPcmIfNeeded(audioIn);
                Clip c = AudioSystem.getClip();
                c.open(audioIn);
                rustleClip = c;
            } catch (Exception e) {
                System.err.println("[Rustle] Preload failed: " + e);
            } finally {
                rustleLoading = false;
            }
        });
        t.setDaemon(true);
        t.start();
    }

    /** Play rustle once (plant stepped on). Cooldown prevents overlapping when stepping through multiple plants. */
    public static void playRustle() {
        Clip c = rustleClip;
        if (c == null || !c.isOpen()) return;
        long now = System.currentTimeMillis();
        if (c.isRunning()) return;
        if (now - lastRustlePlayTime < RUSTLE_COOLDOWN_MS) return;
        lastRustlePlayTime = now;
        applyVolumeToClip(c);
        c.stop();
        c.setFramePosition(0);
        c.start();
    }

    // --- Util ---

    /**
     * Convert to PCM if the stream is in a format Clip doesn't support (e.g. VORBISENC from OGG).
     * Clip only supports PCM; without this, "line with format VORBISENC ... not supported" occurs.
     */
    private static AudioInputStream toPcmIfNeeded(AudioInputStream in) throws Exception {
        AudioFormat fmt = in.getFormat();
        if (fmt.getEncoding() == AudioFormat.Encoding.PCM_SIGNED
                || fmt.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED) {
            return in;
        }
        AudioFormat pcm = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                fmt.getSampleRate(),
                16,
                fmt.getChannels(),
                fmt.getChannels() * 2,
                fmt.getSampleRate(),
                false);
        return AudioSystem.getAudioInputStream(pcm, in);
    }

    /** Copy stream into memory so it supports mark/reset — required by Java Sound when loading from JAR. */
    private static InputStream toMarkResetStream(InputStream in) throws IOException {
        byte[] buf = in.readAllBytes();
        in.close();
        return new ByteArrayInputStream(buf);
    }

    private static InputStream findResource(String[] paths) {
        for (String path : paths) {
            InputStream in = SoundPlayer.class.getResourceAsStream(path.startsWith("/") ? path : "/" + path);
            if (in == null) in = SoundPlayer.class.getClassLoader().getResourceAsStream(path);
            if (in != null) return in;
        }
        return null;
    }
}
