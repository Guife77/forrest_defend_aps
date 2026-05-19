package game.utils;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class AudioPlayer {

    private static Clip menuClip;
    private static float musicVolume = 0.7f; // 0..1
    private static boolean muted = false;

    // Toca um efeito sonoro rápido uma única vez (ex: tiro, construir torre)
    public static void play(String filePath) {
        InputStream in = Assets.open(filePath);
        if (in == null) {
            System.out.println("Aviso: Áudio não encontrado -> " + filePath);
            return;
        }
        try (InputStream stream = in) {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(stream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Erro ao tocar áudio: " + e.getMessage());
        }
    }

    // Toca uma música de fundo em loop infinito
    public static Clip playLoop(String filePath) {
        InputStream in = Assets.open(filePath);
        if (in == null) {
            System.out.println("Aviso: Música de fundo não encontrada -> " + filePath);
            return null;
        }
        try (InputStream stream = in) {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(stream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Erro ao tocar música: " + e.getMessage());
        }
        return null;
    }

    /**
     * Toca uma música do menu em loop, reiniciando do zero ao atingir o tempo de corte.
     * Se o áudio for menor que loopCutoffSeconds, faz loop na duração total.
     */
    public static void playMenuLoop(String filePath, double loopCutoffSeconds) {
        stopMenuMusic();

        InputStream in = Assets.open(filePath);
        if (in == null) {
            System.out.println("Aviso: Música do menu não encontrada -> " + filePath);
            return;
        }
        try (InputStream stream = in) {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(stream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);

            AudioFormat fmt = clip.getFormat();
            int totalFrames = clip.getFrameLength();
            int cutoffFrames = (int) Math.round(fmt.getFrameRate() * loopCutoffSeconds);
            int endFrame = Math.min(cutoffFrames, totalFrames) - 1;
            if (endFrame < 1) endFrame = totalFrames - 1;

            clip.setLoopPoints(0, endFrame);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            menuClip = clip;
            applyVolume();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Erro ao tocar música do menu: " + e.getMessage());
        }
    }

    // ── Volume ──────────────────────────────────────────────

    public static float getMusicVolume() { return muted ? 0f : musicVolume; }
    public static float getRawMusicVolume() { return musicVolume; }
    public static boolean isMuted() { return muted; }

    public static void setMusicVolume(float v) {
        musicVolume = Math.max(0f, Math.min(1f, v));
        if (musicVolume > 0f) muted = false;
        applyVolume();
    }

    public static void adjustMusicVolume(float delta) {
        setMusicVolume(musicVolume + delta);
    }

    public static void toggleMute() {
        muted = !muted;
        applyVolume();
    }

    /** Aplica o volume atual ao clip da música em decibéis (escala logarítmica natural). */
    private static void applyVolume() {
        if (menuClip == null) return;
        try {
            FloatControl gain = (FloatControl) menuClip.getControl(FloatControl.Type.MASTER_GAIN);
            float effective = muted ? 0f : musicVolume;
            float dB;
            if (effective <= 0.0001f) {
                dB = gain.getMinimum();
            } else {
                dB = (float) (20.0 * Math.log10(effective));
                dB = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB));
            }
            gain.setValue(dB);
        } catch (IllegalArgumentException ignored) {
            // Sistema sem suporte a MASTER_GAIN — silenciosamente ignora.
        }
    }

    public static void stopMenuMusic() {
        if (menuClip != null) {
            try {
                if (menuClip.isRunning()) menuClip.stop();
                menuClip.close();
            } catch (Exception ignored) {}
            menuClip = null;
        }
    }

}
