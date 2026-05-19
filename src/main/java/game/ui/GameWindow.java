package game.ui;

import game.engine.GameEngine;
import game.utils.AudioPlayer;
import game.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Janela principal do jogo.
 * Renderiza num canvas lógico de SCREEN_W × SCREEN_H e escala para o tamanho real do JPanel:
 *   • Janelado: preserva aspect ratio (letterbox)
 *   • Fullscreen: estica X/Y independentemente para preencher 100% da tela
 */
public class GameWindow extends JPanel {

    private static final int LOGICAL_W = Constants.SCREEN_W;
    private static final int LOGICAL_H = Constants.SCREEN_H;

    private final GameEngine engine;
    private JFrame frame;
    private boolean fullscreen = false;
    private GraphicsDevice device;

    private double lastScaleX = 1.0;
    private double lastScaleY = 1.0;
    private double lastOffsetX = 0;
    private double lastOffsetY = 0;

    public GameWindow() {
        engine = new GameEngine();
        setPreferredSize(new Dimension(LOGICAL_W, LOGICAL_H));
        setBackground(Color.BLACK);
        setFocusable(true);

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { requestFocusInWindow(); }
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
                int lx = (int) ((e.getX() - lastOffsetX) / lastScaleX);
                int ly = (int) ((e.getY() - lastOffsetY) / lastScaleY);
                if (lx < 0 || ly < 0 || lx >= LOGICAL_W || ly >= LOGICAL_H) return;
                engine.handleClickAt(lx, ly, e.getButton());
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F11) { toggleFullscreen(); return; }
                engine.onKeyPressed(e.getKeyCode());
            }
        });

        AudioPlayer.playMenuLoop("public/O_Despertar_do_Reino.wav", 30.0);

        // Render @ 60Hz. Engine.update é chamada N vezes por frame conforme speedMultiplier.
        new Timer(1000 / Constants.FPS, e -> {
            int steps = Math.max(1, engine.getSpeedMultiplier());
            for (int i = 0; i < steps; i++) engine.update();
            repaint();
        }).start();
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
        this.device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }

    private void toggleFullscreen() {
        if (frame == null || device == null) return;
        fullscreen = !fullscreen;
        frame.dispose();
        frame.setUndecorated(fullscreen);
        if (fullscreen) {
            device.setFullScreenWindow(frame);
        } else {
            device.setFullScreenWindow(null);
            frame.setSize(LOGICAL_W, LOGICAL_H);
            frame.setLocationRelativeTo(null);
        }
        frame.setVisible(true);
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        double sx, sy, offX, offY;
        if (fullscreen) {
            // Estica X e Y independentemente — preenche 100%, sem barra preta
            sx = (double) w / LOGICAL_W;
            sy = (double) h / LOGICAL_H;
            offX = 0;
            offY = 0;
        } else {
            // Preserva aspect ratio, letterbox preto
            double s = Math.min((double) w / LOGICAL_W, (double) h / LOGICAL_H);
            sx = sy = s;
            offX = (w - LOGICAL_W * s) / 2.0;
            offY = (h - LOGICAL_H * s) / 2.0;
        }
        lastScaleX = sx;
        lastScaleY = sy;
        lastOffsetX = offX;
        lastOffsetY = offY;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, w, h);

        g2.translate(offX, offY);
        g2.scale(sx, sy);
        g2.setClip(0, 0, LOGICAL_W, LOGICAL_H);

        engine.render(g2);
        g2.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Forrest Defend");
            GameWindow panel = new GameWindow();
            panel.setFrame(frame);
            frame.add(panel);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            applyIcon(frame);
            frame.setVisible(true);
            panel.requestFocusInWindow();
        });
    }

    /**
     * Carrega o ícone e gera versões pré-escaladas em 16/32/48/64/128 px com interpolação de qualidade.
     * Sem isso, o Windows usa nearest-neighbor ao reduzir o PNG original (muito grande), e o ícone
     * fica granulado no título e na taskbar.
     */
    private static void applyIcon(JFrame frame) {
        BufferedImage original = game.utils.Assets.loadImage("public/icon_Forrest_Defend.png");
        if (original == null) {
            System.out.println("[Icon] icon_Forrest_Defend.png não encontrado");
            return;
        }

        int[] sizes = {16, 24, 32, 48, 64, 128, 256};
        List<Image> icons = new ArrayList<>();
        for (int s : sizes) icons.add(scaleHighQuality(original, s, s));
        frame.setIconImages(icons);

        try {
            Image best = icons.get(icons.size() - 1);
            java.awt.Taskbar.getTaskbar().setIconImage(best);
        } catch (UnsupportedOperationException | SecurityException ignored) {}
    }

    /**
     * Progressive bilinear downscaling + unsharp mask final.
     *
     * Reduzir uma PNG grande pra ícone pequeno em um único passe gera aliasing pesado.
     * Solução clássica em 3 partes:
     *   1) Halvear repetidamente até chegar perto do tamanho alvo (≤ 2× o alvo)
     *   2) Passe final em bicúbico pro tamanho exato
     *   3) Sharpening (kernel 3×3) pra recuperar definição perdida nas reduções
     */
    private static BufferedImage scaleHighQuality(BufferedImage src, int targetW, int targetH) {
        BufferedImage current = src;
        int curW = src.getWidth();
        int curH = src.getHeight();
        while (curW > targetW * 2 && curH > targetH * 2) {
            curW /= 2;
            curH /= 2;
            BufferedImage next = new BufferedImage(curW, curH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = next.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(current, 0, 0, curW, curH, null);
            g.dispose();
            current = next;
        }
        BufferedImage scaled = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(current, 0, 0, targetW, targetH, null);
        g.dispose();

        return sharpen(scaled);
    }

    /** Unsharp mask leve — reforça bordas sem deixar o ícone com aspecto serrilhado. */
    private static BufferedImage sharpen(BufferedImage src) {
        float[] kernel = {
                 0f,   -0.2f,  0f,
                -0.2f,  1.8f, -0.2f,
                 0f,   -0.2f,  0f
        };
        java.awt.image.ConvolveOp op = new java.awt.image.ConvolveOp(
                new java.awt.image.Kernel(3, 3, kernel),
                java.awt.image.ConvolveOp.EDGE_NO_OP, null);
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        op.filter(src, out);
        return out;
    }
}
