package game.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {

    // Toca um efeito sonoro rápido uma única vez (ex: tiro, construir torre)
    public static void play(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
            } else {
                System.out.println("Aviso: Áudio não encontrado -> " + filePath);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Erro ao tocar áudio: " + e.getMessage());
        }
    }

    // Toca uma música de fundo em loop infinito
    public static Clip playLoop(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.loop(Clip.LOOP_CONTINUOUSLY); // Faz o loop infinito
                clip.start();
                return clip;
            } else {
                System.out.println("Aviso: Música de fundo não encontrada -> " + filePath);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Erro ao tocar música: " + e.getMessage());
        }
        return null;
    }
}