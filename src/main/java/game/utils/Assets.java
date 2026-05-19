package game.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Resolução unificada de assets (sprites, áudio, ícones).
 *
 * Estratégia em duas etapas — sempre tenta classpath primeiro, disco depois:
 *   1) Classpath: funciona quando o jogo roda do Fat JAR (recursos empacotados via Maven)
 *   2) Disco: funciona quando se desenvolve direto da IDE, com recursos em src/main/resources
 *      ou src/public (alguns assets que estão fora do diretório padrão)
 *
 * Aceita caminhos com ou sem "/" inicial. O nome "public/foo.png" é equivalente
 * a "/public/foo.png" no classpath.
 */
public final class Assets {

    private Assets() {}

    /**
     * Abre um asset como InputStream. Sempre retorna stream com mark/reset (BufferedInputStream)
     * pra ser compatível com APIs como AudioSystem.getAudioInputStream.
     * Retorna null se o recurso não existir em nenhum dos caminhos.
     * O caller é responsável por fechar o stream.
     */
    public static InputStream open(String name) {
        String classpathName = name.startsWith("/") ? name : "/" + name;
        InputStream in = Assets.class.getResourceAsStream(classpathName);
        if (in != null) return new BufferedInputStream(in);

        for (String diskPath : diskCandidates(name)) {
            File f = new File(diskPath);
            if (f.exists()) {
                try {
                    return new BufferedInputStream(new FileInputStream(f));
                } catch (IOException ignored) {}
            }
        }
        return null;
    }

    /**
     * Carrega uma imagem PNG/JPG. Retorna null em caso de falha.
     */
    public static BufferedImage loadImage(String name) {
        InputStream in = open(name);
        if (in == null) return null;
        try (InputStream stream = in) {
            return ImageIO.read(stream);
        } catch (IOException e) {
            return null;
        }
    }

    private static String[] diskCandidates(String name) {
        String stripped = name.startsWith("public/") ? name.substring("public/".length()) : name;
        return new String[]{
                name,
                "src/main/resources/" + name,
                "src/main/resources/public/" + stripped,
                "src/public/" + stripped,
                "public/" + stripped
        };
    }
}
