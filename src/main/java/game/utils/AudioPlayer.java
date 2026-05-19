package game.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {

    private static Clip menuClip;

    // Toca um efeito sonoro rápido uma única vez (ex: tiro, construir torre)
    public static void play(String filePath) {
        File file = resolve(filePath);
        if (file == null) {
            System.out.println("Aviso: Áudio não encontrado -> " + filePath);
            return;
        }
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Erro ao tocar áudio: " + e.getMessage());
        }
    }

    // Toca uma música de fundo em loop infinito
    public static Clip playLoop(String filePath) {
        File file = resolve(filePath);
        if (file == null) {
            System.out.println("Aviso: Música de fundo não encontrada -> " + filePath);
            return null;
        }
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(file);
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

        File file = resolve(filePath);
        if (file == null) {
            System.out.println("Aviso: Música do menu não encontrada -> " + filePath);
            return;
        }
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(file);
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
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Erro ao tocar música do menu: " + e.getMessage());
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

    private static File resolve(String filePath) {
        String[] candidates = {
                filePath,
                "src/main/resources/" + filePath,
                "src/main/resources/public/" + stripPublicPrefix(filePath),
                "src/public/" + stripPublicPrefix(filePath),
                "public/" + stripPublicPrefix(filePath),
                "resources/" + filePath
        };
        for (String p : candidates) {
            File f = new File(p);
            if (f.exists()) return f;
        }
        return null;
    }

    private static String stripPublicPrefix(String path) {
        if (path.startsWith("public/")) return path.substring("public/".length());
        return path;
    }
}
