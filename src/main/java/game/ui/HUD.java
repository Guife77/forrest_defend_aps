package game.ui;

import game.engine.WaveManager;
import game.entities.Player;
import game.utils.Constants;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HUD {

    public static final int PANEL_H = 64;

    // Estado das partículas da tela de vitória — inicia preguiçosamente no primeiro render
    private VictoryFx victoryFx;

    private static final Color BG_PANEL      = new Color(20, 25, 22, 245);
    private static final Color BORDER_PANEL  = new Color(55, 75, 60);
    private static final Color BG_CARD       = new Color(32, 36, 34);
    private static final Color BORDER_CARD   = new Color(55, 60, 58);
    private static final Color TEXT_LABEL    = new Color(140, 150, 145);

    public void render(Graphics2D g, Player player, WaveManager waves,
                       int screenW, int screenH, char selectedTower, boolean showRanges,
                       int speedMultiplier) {

        // Ativar suavização para o texto ficar perfeito
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // ── 1. STATUS DA ONDA FLUTUANTE (No topo do ecrã) ──
        renderFloatingWaveInfo(g, waves, screenW);


        // ── 2. PAINEL INFERIOR PRINCIPAL ──
        int panelY = screenH - PANEL_H;

        g.setColor(BG_PANEL);
        g.fillRect(0, panelY, screenW, PANEL_H);
        g.setColor(BORDER_PANEL);
        g.setStroke(new BasicStroke(2));
        g.drawLine(0, panelY, screenW, panelY);
        g.setStroke(new BasicStroke(1));


        // ── BLOCO ESQUERDA: Status do Jogador (Barra de HP redimensionada e centralizada) ──
        int startX = 15;
        
        int hp = player.getBaseHealth();
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.setColor(TEXT_LABEL);
        g.drawString("INTEGRIDADE DA BASE", startX, panelY + 22);
        
        g.setColor(new Color(45, 45, 45));
        g.fillRoundRect(startX, panelY + 28, 120, 14, 4, 4); // Ficou um pouco mais espessa e robusta
        g.setColor(hpColor(hp));
        int hpWidth = (int) (120 * (Math.max(0, hp) / 100.0));
        g.fillRoundRect(startX, panelY + 28, hpWidth, 14, 4, 4);
        g.setColor(Color.BLACK);
        g.drawRoundRect(startX, panelY + 28, 120, 14, 4, 4);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString(hp + "%", startX + 46, panelY + 39);

        // Caixa de Recursos (RF)
        int rfX = 150;
        g.setColor(new Color(30, 42, 34));
        g.fillRoundRect(rfX, panelY + 10, 95, 46, 6, 6);
        g.setColor(new Color(65, 95, 75));
        g.drawRoundRect(rfX, panelY + 10, 95, 46, 6, 6);
        
        g.setFont(new Font("Arial", Font.BOLD, 9));
        g.setColor(new Color(130, 180, 145));
        g.drawString("RECURSOS", rfX + 10, panelY + 24);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(46, 204, 113));
        g.drawString(player.getForestResources() + " RF", rfX + 10, panelY + 42);


        // ── BLOCO CENTRO: Seleção de Torres ──
        int towerStartX = 260;
        int cardW = 105;
        int cardH = 46;
        int cardY = panelY + 10;

        drawTowerCard(g, "[T] ÁRVORE", Constants.COST_TREE + " RF", selectedTower == 'T', towerStartX, cardY, cardW, cardH, new Color(39, 174, 96));
        drawTowerCard(g, "[A] ARARA", Constants.COST_BIRD + " RF", selectedTower == 'A', towerStartX + 112, cardY, cardW, cardH, new Color(41, 128, 185));
        drawTowerCard(g, "[S] ARANHA", Constants.COST_SPIDER + " RF", selectedTower == 'S', towerStartX + 224, cardY, cardW, cardH, new Color(142, 68, 173));
        drawTowerCard(g, "[B] BARREIRA", Constants.COST_BARRIER + " RF", selectedTower == 'B', towerStartX + 336, cardY, cardW, cardH, new Color(211, 84, 0));


        // ── BLOCO DIREITA: Menu de Controles ──
        int rightX = screenW - 220;
        
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.setColor(TEXT_LABEL);
        g.drawString("ATALHOS & CONTROLES", rightX, panelY + 15);

        g.setFont(new Font("Arial", Font.PLAIN, 11));
        g.setColor(new Color(200, 205, 200)); 
        g.drawString("• [CLIQUE] Construir Torres  |  [2] Velocidade", rightX, panelY + 30);
        g.drawString("• [R] Alcances: " + (showRanges ? "ON" : "OFF") + "  |  [F11] Fullscreen", rightX, panelY + 43);
        g.drawString("• [ESPAÇO] Próxima Onda  |  [ESC] Sair", rightX, panelY + 56);

        // Selo de velocidade flutuante no canto superior esquerdo
        drawSpeedBadge(g, speedMultiplier);
        // Widget de volume logo abaixo do selo de velocidade
        drawVolumeWidget(g);
    }

    private void renderFloatingWaveInfo(Graphics2D g, WaveManager waves, int screenW) {
        String waveText;
        Color waveColor;

        if (waves.getCurrentWave() == 0) {
            waveText = "Pressione [ESPAÇO] para iniciar";
            waveColor = new Color(241, 196, 15);
        } else if (waves.isWaveActive()) {
            waveText = "WAVE " + waves.getCurrentWave() + " EM CURSO !";
            waveColor = new Color(231, 76, 60);
        } else {
            waveText = "WAVE " + waves.getCurrentWave() + " LIMPA! [ESPAÇO]";
            waveColor = new Color(46, 204, 113);
        }

        g.setFont(new Font("Arial", Font.BOLD, 14));
        int txtW = g.getFontMetrics().stringWidth(waveText);

        // ── NOVO POSICIONAMENTO: Canto Superior Direito ──
        int boxW = txtW + 40;
        int boxH = 32;
        int boxX = screenW - boxW - 15; // 15 pixels de distância da borda direita da tela
        int boxY = 15;                  // 15 pixels de distância do teto

        // Desenha o fundo da caixa
        g.setColor(new Color(15, 20, 15, 200));
        g.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);
        
        // Desenha a borda da caixa
        g.setColor(new Color(60, 80, 60));
        g.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        // Desenha o texto perfeitamente centralizado dentro da nova caixa
        g.setColor(waveColor);
        g.drawString(waveText, boxX + 20, boxY + 22);
    }

    private void drawTowerCard(Graphics2D g, String name, String cost, boolean selected, 
                               int x, int y, int w, int h, Color accentColor) {
        if (selected) {
            g.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
            g.fillRoundRect(x, y, w, h, 6, 6);
            g.setColor(accentColor);
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(x, y, w, h, 6, 6);
            g.setStroke(new BasicStroke(1));
        } else {
            g.setColor(BG_CARD);
            g.fillRoundRect(x, y, w, h, 6, 6);
            g.setColor(BORDER_CARD);
            g.drawRoundRect(x, y, w, h, 6, 6);
        }
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.setColor(selected ? Color.WHITE : new Color(175, 180, 175));
        g.drawString(name, x + 8, y + 18);
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(selected ? new Color(241, 196, 15) : new Color(160, 145, 90));
        g.drawString(cost, x + 8, y + 34);
    }

    // ── Volume widget ──────────────────────────────────────
    private static final int VOL_X = 15;
    private static final int VOL_Y = 50;
    private static final int VOL_W = 222;
    private static final int VOL_H = 42;
    private static final int VOL_MUTE_BTN_W = 38;
    private static final int VOL_BTN_W = 28;
    private static final int VOL_BAR_X = VOL_X + VOL_MUTE_BTN_W + VOL_BTN_W + 10;
    private static final int VOL_BAR_W = 76;
    private static final int VOL_PCT_W = 36;

    private float displayedVolume = 0.7f;

    private void drawVolumeWidget(Graphics2D g) {
        int x = VOL_X, y = VOL_Y, w = VOL_W, h = VOL_H;
        boolean muted = game.utils.AudioPlayer.isMuted();
        float targetVol = muted ? 0f : game.utils.AudioPlayer.getRawMusicVolume();

        // Easing para preenchimento suave da barra
        displayedVolume += (targetVol - displayedVolume) * 0.18f;
        if (Math.abs(targetVol - displayedVolume) < 0.001f) displayedVolume = targetVol;

        Color accent = muted ? new Color(190, 75, 65) : new Color(56, 220, 130);
        Color accentDeep = muted ? new Color(120, 40, 35) : new Color(28, 145, 80);

        // Card de fundo com gradiente vertical sutil
        Graphics2D gc = (Graphics2D) g.create();
        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint cardBg = new GradientPaint(
                x, y, new Color(22, 30, 25, 235),
                x, y + h, new Color(10, 16, 12, 235));
        gc.setPaint(cardBg);
        gc.fillRoundRect(x, y, w, h, 14, 14);

        // Borda
        gc.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 220));
        gc.setStroke(new BasicStroke(2f));
        gc.drawRoundRect(x, y, w, h, 14, 14);
        gc.setStroke(new BasicStroke(1));

        // Realce superior interno
        gc.setColor(new Color(255, 255, 255, 18));
        gc.fillRoundRect(x + 2, y + 2, w - 4, h / 2 - 2, 12, 12);

        // Ícone do alto-falante com ondas animadas
        drawSpeakerIcon(gc, x + 9, y + h / 2 - 11, 22, 22, muted, accent);

        // Botão "-"
        int minusX = x + VOL_MUTE_BTN_W + 2;
        int minusY = y + 6;
        drawVolButton(gc, minusX, minusY, VOL_BTN_W, h - 12, "−", accent, accentDeep);

        // Barra de volume com gradiente e brilho
        int barX = VOL_BAR_X;
        int barH = 12;
        int barY = y + h / 2 - barH / 2;
        drawVolumeBar(gc, barX, barY, VOL_BAR_W, barH, displayedVolume, muted, accent, accentDeep);

        // Botão "+"
        int plusX = barX + VOL_BAR_W + 8;
        int plusY = y + 6;
        drawVolButton(gc, plusX, plusY, VOL_BTN_W, h - 12, "+", accent, accentDeep);

        // Label de porcentagem
        int pctX = plusX + VOL_BTN_W + 4;
        drawVolumePercent(gc, pctX, y, VOL_PCT_W, h, muted ? -1 : Math.round(targetVol * 100), accent);

        gc.dispose();
    }

    private void drawVolumeBar(Graphics2D g, int x, int y, int w, int h, float vol, boolean muted,
                                Color accent, Color accentDeep) {
        // Trilho de fundo (escuro com sombra interna)
        g.setColor(new Color(8, 14, 10));
        g.fillRoundRect(x, y, w, h, h, h);
        g.setColor(new Color(0, 0, 0, 120));
        g.drawRoundRect(x, y, w, h, h, h);

        if (vol > 0) {
            int fillW = Math.max(h, (int) (w * vol));
            // Preenchimento com gradiente vertical
            GradientPaint barGrad = new GradientPaint(
                    x, y, accent,
                    x, y + h, accentDeep);
            g.setPaint(barGrad);
            g.fillRoundRect(x, y, fillW, h, h, h);

            // Brilho superior
            g.setColor(new Color(255, 255, 255, 90));
            g.fillRoundRect(x + 2, y + 2, fillW - 4, h / 2 - 1, h / 2, h / 2);

            // Ponta brilhante no fim da barra
            if (!muted && fillW < w) {
                int knobR = h - 2;
                g.setColor(accent.brighter());
                g.fillOval(x + fillW - knobR / 2, y + (h - knobR) / 2, knobR, knobR);
                g.setColor(Color.WHITE);
                g.fillOval(x + fillW - knobR / 2 + 2, y + (h - knobR) / 2 + 2, knobR / 3, knobR / 3);
            }
        }
    }

    private void drawVolumePercent(Graphics2D g, int x, int y, int w, int h, int pct, Color accent) {
        g.setFont(new Font("Arial", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        String label = (pct < 0) ? "OFF" : (pct + "%");
        g.setColor(accent);
        g.drawString(label, x + (w - fm.stringWidth(label)) / 2,
                     y + (h + fm.getAscent()) / 2 - 4);
    }

    private void drawVolButton(Graphics2D g, int x, int y, int w, int h, String label,
                                Color accent, Color accentDeep) {
        // Fundo com gradiente
        GradientPaint bg = new GradientPaint(x, y, new Color(40, 55, 45), x, y + h, new Color(20, 30, 24));
        g.setPaint(bg);
        g.fillRoundRect(x, y, w, h, 8, 8);

        // Borda em accent suave
        g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 170));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x, y, w, h, 8, 8);
        g.setStroke(new BasicStroke(1));

        // Realce superior
        g.setColor(new Color(255, 255, 255, 30));
        g.fillRoundRect(x + 2, y + 2, w - 4, h / 2 - 1, 6, 6);

        // Texto
        g.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.WHITE);
        g.drawString(label, x + (w - fm.stringWidth(label)) / 2,
                     y + (h + fm.getAscent()) / 2 - 4);
    }

    private void drawSpeakerIcon(Graphics2D g, int x, int y, int w, int h, boolean muted, Color accent) {
        long t = System.currentTimeMillis();
        // Corpo do alto-falante (cone trapezoidal)
        int boxW = w * 2 / 5;
        int[] xs = { x, x + boxW, x + w / 2 + 2, x + w / 2 + 2, x + boxW, x };
        int[] ys = { y + h / 3, y + h / 3, y, y + h, y + h * 2 / 3, y + h * 2 / 3 };

        // Sombra do cone
        g.setColor(new Color(0, 0, 0, 80));
        for (int i = 0; i < xs.length; i++) xs[i] += 1;
        for (int i = 0; i < ys.length; i++) ys[i] += 1;
        g.fillPolygon(xs, ys, 6);
        for (int i = 0; i < xs.length; i++) xs[i] -= 1;
        for (int i = 0; i < ys.length; i++) ys[i] -= 1;

        g.setColor(accent);
        g.fillPolygon(xs, ys, 6);

        // Highlight no cone
        g.setColor(new Color(255, 255, 255, 100));
        g.drawLine(x + 1, y + h / 3 + 1, x + boxW - 1, y + h / 3 + 1);

        if (muted) {
            // X vermelho atravessando
            g.setColor(new Color(231, 76, 60));
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int wx1 = x + w / 2 + 4;
            int wy1 = y + 2;
            int wx2 = x + w - 1;
            int wy2 = y + h - 2;
            g.drawLine(wx1, wy1, wx2, wy2);
            g.drawLine(wx2, wy1, wx1, wy2);
            g.setStroke(new BasicStroke(1));
        } else {
            // 3 ondas concêntricas pulsantes
            float volume = game.utils.AudioPlayer.getRawMusicVolume();
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < 3; i++) {
                float phase = (float) ((t * 0.003 + i * 0.33) % 1.0);
                int alpha = (int) (220 * (1 - phase) * Math.min(1f, volume * 2));
                if (alpha < 10) continue;
                int waveR = (int) (6 + phase * 8);
                g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), Math.min(255, alpha)));
                int wx = x + w / 2 + 3;
                int wy = y + h / 2;
                g.drawArc(wx - waveR, wy - waveR, waveR * 2, waveR * 2, -45, 90);
            }
            g.setStroke(new BasicStroke(1));
        }
    }

    /** Hit-test pros botões do volume. Retorna "vol_up", "vol_down", "vol_mute" ou null. */
    public String hitTestVolume(int x, int y) {
        if (x < VOL_X || x > VOL_X + VOL_W || y < VOL_Y || y > VOL_Y + VOL_H) return null;
        if (x < VOL_X + VOL_MUTE_BTN_W) return "vol_mute";
        if (x < VOL_X + VOL_MUTE_BTN_W + VOL_BTN_W + 4) return "vol_down";
        if (x > VOL_BAR_X + VOL_BAR_W + 4 && x < VOL_BAR_X + VOL_BAR_W + 4 + VOL_BTN_W + 4) return "vol_up";
        return null;
    }

    /**
     * Retorna o atalho da torre se o clique caiu em um card do HUD,
     * ou 0 se o clique foi fora dos cards. Coords em espaço lógico.
     */
    public char hitTestTowerCard(int x, int y, int screenW, int screenH) {
        int panelY = screenH - PANEL_H;
        int cardY = panelY + 10;
        int cardH = 46;
        if (y < cardY || y > cardY + cardH) return 0;

        int towerStartX = 260;
        int cardW = 105;
        int gap = 112;
        char[] keys = {'T', 'A', 'S', 'B'};
        for (int i = 0; i < keys.length; i++) {
            int cx = towerStartX + i * gap;
            if (x >= cx && x <= cx + cardW) return keys[i];
        }
        return 0;
    }

    private void drawSpeedBadge(Graphics2D g, int multiplier) {
        int w = 56, h = 28;
        int x = 15, y = 15;
        Color accent = multiplier == 1
                ? new Color(150, 150, 150)
                : (multiplier == 2 ? new Color(241, 196, 15) : new Color(231, 76, 60));

        g.setColor(new Color(15, 20, 15, 200));
        g.fillRoundRect(x, y, w, h, 10, 10);
        g.setColor(accent);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, w, h, 10, 10);
        g.setStroke(new BasicStroke(1));

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(accent);
        String label = multiplier + "x";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, x + (w - fm.stringWidth(label)) / 2, y + 20);
    }

    private Color hpColor(int hp) {
        if (hp > 60) return new Color(39, 174, 96);   
        if (hp > 30) return new Color(241, 196, 15);  
        return new Color(192, 41, 43);     
    }

    public void renderGameOver(Graphics2D g, int w, int h, int wave) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. Fundo Gradiente Radial (Efeito Vignette de Derrota)
        // Centro: Cinza escuro | Bordas: Preto absoluto (trazendo o clima de luto/fim)
        Point2D center = new Point2D.Float(w / 2f, h / 2f);
        float radius = Math.max(w, h);
        float[] dist = {0.0f, 0.8f};
        Color[] colors = {new Color(30, 30, 30, 200), new Color(0, 0, 0, 245)};
        RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
        g.setPaint(p);
        g.fillRect(0, 0, w, h);

        // 2. Painel Central (Card de Derrota com borda vermelha fogo/sangue)
        int pW = 550; // Largura do painel
        int pH = 180; // Altura do painel
        int pX = (w - pW) / 2;
        int pY = (h - pH) / 2;
        
        // Fundo do card (quase preto opaco)
        g.setColor(new Color(15, 10, 10, 230)); 
        g.fillRoundRect(pX, pY, pW, pH, 15, 15);
        // Borda brilhante vermelha
        g.setColor(new Color(231, 76, 60)); // Vermelho Flat combativo
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(pX, pY, pW, pH, 15, 15);
        g.setStroke(new BasicStroke(1)); // Reseta espessura


        // 3. Tipografia e Conteúdo (Alinhado)
        // Linha 1: Título Principal
        g.setFont(new Font("Arial", Font.BOLD, 52));
        g.setColor(new Color(231, 76, 60)); // Mesmo vermelho da borda
        drawCentered(g, "✕ A BASE CAIU ✕", w, pY + 65);

        // Linha 2: Descrição da tragédia e info da Wave
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(new Color(220, 220, 220)); // Branco suave
        String desc = "A floresta foi derrubada na WAVE " + wave;
        drawCentered(g, desc, w, pY + 110);

        // Linha auxiliar de história
        g.setFont(new Font("Arial", Font.ITALIC, 14));
        g.setColor(new Color(180, 150, 150)); // Cinza avermelhado
        drawCentered(g, "Os invasores venceram esta batalha...", w, pY + 130);

        // Linha 3: Controles Separados e discretos
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(new Color(150, 150, 150)); // Cinza secundário
        drawCentered(g, "[ R ] Tentar Novamente   |   [ C ] Curiosidades   |   [ ESC ] Sair", w, pY + 165);
    }

    public void renderVictory(Graphics2D g, int w, int h) {
        renderVictory(g, w, h, 0, 0);
    }

    /** Versão extendida com stats. Mostra wave e RF acumulados. */
    public void renderVictory(Graphics2D g, int w, int h, int waveReached, int rfBanked) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);

        if (victoryFx == null) victoryFx = new VictoryFx(w, h);
        victoryFx.update();
        long now = System.currentTimeMillis();
        float t = (now - victoryFx.startTime) / 1000f;

        // 1. Vignette radial verde
        Point2D center = new Point2D.Float(w / 2f, h / 2f);
        float radius = Math.max(w, h);
        g.setPaint(new RadialGradientPaint(center, radius,
                new float[]{0f, 0.7f},
                new Color[]{new Color(25, 70, 40, 220), new Color(0, 0, 0, 245)}));
        g.fillRect(0, 0, w, h);

        // 2. Raios de luz radiais pulsantes
        drawGodRays(g, w, h, t);

        // 3. Confete caindo
        victoryFx.renderConfetti(g);

        // 4. Painel central com efeitos
        int pW = 680, pH = 280;
        int pX = (w - pW) / 2, pY = (h - pH) / 2;

        // Halo dourado atrás do card
        g.setPaint(new RadialGradientPaint(
                new Point2D.Float(w / 2f, h / 2f), pW * 0.9f,
                new float[]{0f, 1f},
                new Color[]{new Color(241, 196, 15, 80), new Color(241, 196, 15, 0)}));
        g.fillRect(pX - 200, pY - 100, pW + 400, pH + 200);

        // Fundo do card
        g.setColor(new Color(10, 18, 14, 235));
        g.fillRoundRect(pX, pY, pW, pH, 18, 18);
        // Borda dourada com pulsação
        float pulse = 0.5f + 0.5f * (float) Math.sin(t * 2);
        int borderAlpha = (int) (180 + 75 * pulse);
        g.setColor(new Color(241, 196, 15, borderAlpha));
        g.setStroke(new BasicStroke(3.5f));
        g.drawRoundRect(pX, pY, pW, pH, 18, 18);
        g.setStroke(new BasicStroke(1));

        // 5. Coroa estilizada acima do título
        drawCrown(g, w / 2, pY + 18, t);

        // 6. Título com escala pulsante e shine sweep
        drawVictoryTitle(g, w, pY + 95, t);

        // 7. Subtítulo
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(new Color(230, 240, 232));
        drawCentered(g, "Conseguiste expelir todos os invasores da Amazônia!", w, pY + 138);

        g.setFont(new Font("Arial", Font.ITALIC, 14));
        g.setColor(new Color(160, 200, 170));
        drawCentered(g, "A natureza agradece o teu comando sagaz.", w, pY + 162);

        // 8. Stats em duas caixas
        drawVictoryStat(g, pX + 60, pY + 190, 220, 60, "WAVES VENCIDAS", String.valueOf(waveReached),
                        new Color(46, 204, 113));
        drawVictoryStat(g, pX + pW - 60 - 220, pY + 190, 220, 60, "RF ACUMULADOS", rfBanked + " RF",
                        new Color(241, 196, 15));

        // 9. Controles
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(new Color(180, 200, 185));
        drawCentered(g, "[ R ] Novo Jogo    |    [ C ] Curiosidades    |    [ ESC ] Sair", w, pY + pH - 18);
    }

    private void drawGodRays(Graphics2D g, int w, int h, float t) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.12f));
        int rays = 12;
        int cx = w / 2, cy = h / 2;
        int len = Math.max(w, h);
        for (int i = 0; i < rays; i++) {
            double a = (Math.PI * 2 * i / rays) + t * 0.15;
            int x1 = cx + (int)(Math.cos(a) * len);
            int y1 = cy + (int)(Math.sin(a) * len);
            int x2 = cx + (int)(Math.cos(a + 0.18) * len);
            int y2 = cy + (int)(Math.sin(a + 0.18) * len);
            Polygon p = new Polygon(new int[]{cx, x1, x2}, new int[]{cy, y1, y2}, 3);
            g2.setColor(new Color(241, 196, 15));
            g2.fillPolygon(p);
        }
        g2.dispose();
    }

    private void drawCrown(Graphics2D g, int cx, int cy, float t) {
        Graphics2D g2 = (Graphics2D) g.create();
        float scale = 1f + 0.06f * (float) Math.sin(t * 3);
        g2.translate(cx, cy + 14);
        g2.scale(scale, scale);

        // 3 pontas
        int[] xs = {-20, -10, 0, 10, 20, 14, -14};
        int[] ys = {6, -10, 4, -14, 6, 16, 16};
        g2.setColor(new Color(241, 196, 15));
        g2.fillPolygon(xs, ys, xs.length);
        g2.setColor(new Color(180, 130, 20));
        g2.setStroke(new BasicStroke(2f));
        g2.drawPolygon(xs, ys, xs.length);

        // 3 gemas
        g2.setColor(new Color(231, 76, 60));
        g2.fillOval(-12, 0, 6, 6);
        g2.setColor(new Color(46, 204, 113));
        g2.fillOval(-3, -2, 6, 6);
        g2.setColor(new Color(52, 152, 219));
        g2.fillOval(6, 0, 6, 6);

        g2.dispose();
    }

    private void drawVictoryTitle(Graphics2D g, int w, int y, float t) {
        String title = "FLORESTA PROTEGIDA!";
        float pulse = 1f + 0.025f * (float) Math.sin(t * 2.5);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        int titleW = fm.stringWidth(title);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(w / 2f, y);
        g2.scale(pulse, pulse);
        g2.translate(-w / 2f, -y);

        int x = (w - titleW) / 2;
        // Halo
        g2.setColor(new Color(46, 204, 113, 50));
        for (int i = 6; i > 0; i -= 2) {
            g2.drawString(title, x - i, y);
            g2.drawString(title, x + i, y);
            g2.drawString(title, x, y - i);
            g2.drawString(title, x, y + i);
        }

        // Sombra
        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(title, x + 3, y + 3);

        // Texto principal verde
        g2.setColor(new Color(60, 230, 130));
        g2.drawString(title, x, y);

        // Shine sweep
        Shape oldClip = g2.getClip();
        g2.setClip(x, y - fm.getAscent(), titleW, fm.getAscent() + fm.getDescent());
        float sweep = ((t * 0.4f) % 1f) * (titleW + 200) - 100;
        g2.setPaint(new GradientPaint(
                x + sweep - 60, 0, new Color(255, 255, 255, 0),
                x + sweep, 0, new Color(255, 255, 255, 180),
                false));
        g2.drawString(title, x, y);
        g2.setPaint(new GradientPaint(
                x + sweep, 0, new Color(255, 255, 255, 180),
                x + sweep + 60, 0, new Color(255, 255, 255, 0),
                false));
        g2.drawString(title, x, y);
        g2.setClip(oldClip);

        g2.dispose();
    }

    private void drawVictoryStat(Graphics2D g, int x, int y, int w, int h, String label, String value, Color accent) {
        g.setColor(new Color(20, 30, 24, 220));
        g.fillRoundRect(x, y, w, h, 10, 10);
        g.setColor(accent);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, w, h, 10, 10);
        g.setStroke(new BasicStroke(1));

        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.setColor(new Color(140, 160, 145));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, x + (w - fm.stringWidth(label)) / 2, y + 18);

        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.setColor(accent);
        fm = g.getFontMetrics();
        g.drawString(value, x + (w - fm.stringWidth(value)) / 2, y + 46);
    }

    // ── Partículas de confete ─────────────────────────────

    private static class VictoryFx {
        long startTime = System.currentTimeMillis();
        long lastFrame = startTime;
        java.util.List<Confetti> confetti = new java.util.ArrayList<>();
        java.util.Random rng = new java.util.Random(7);
        int width, height;

        VictoryFx(int w, int h) {
            this.width = w; this.height = h;
            for (int i = 0; i < 100; i++) confetti.add(spawn(false));
        }

        Confetti spawn(boolean fromTop) {
            Confetti c = new Confetti();
            c.x = rng.nextFloat() * width;
            c.y = fromTop ? -10 : rng.nextFloat() * height;
            c.vy = 40 + rng.nextFloat() * 120;
            c.vx = (rng.nextFloat() - 0.5f) * 40;
            c.rot = rng.nextFloat() * (float) Math.PI * 2;
            c.spin = (rng.nextFloat() - 0.5f) * 6;
            c.size = 5 + rng.nextInt(7);
            Color[] palette = {
                    new Color(241, 196, 15),
                    new Color(46, 204, 113),
                    new Color(231, 76, 60),
                    new Color(52, 152, 219),
                    new Color(220, 220, 220),
                    new Color(155, 89, 182)
            };
            c.color = palette[rng.nextInt(palette.length)];
            c.shape = rng.nextInt(3);
            return c;
        }

        void update() {
            long now = System.currentTimeMillis();
            float dt = Math.min(0.05f, (now - lastFrame) / 1000f);
            lastFrame = now;
            for (Confetti c : confetti) {
                c.x += c.vx * dt;
                c.y += c.vy * dt;
                c.rot += c.spin * dt;
                if (c.y > height + 20) {
                    Confetti respawn = spawn(true);
                    c.x = respawn.x; c.y = -10;
                    c.vx = respawn.vx; c.vy = respawn.vy;
                }
            }
        }

        void renderConfetti(Graphics2D g) {
            for (Confetti c : confetti) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.translate(c.x, c.y);
                g2.rotate(c.rot);
                g2.setColor(c.color);
                switch (c.shape) {
                    case 0: g2.fillRect(-c.size / 2, -c.size / 4, c.size, c.size / 2); break;
                    case 1: g2.fillOval(-c.size / 2, -c.size / 2, c.size, c.size); break;
                    default: g2.fillPolygon(
                            new int[]{-c.size / 2, c.size / 2, 0},
                            new int[]{c.size / 2, c.size / 2, -c.size / 2}, 3);
                }
                g2.dispose();
            }
        }
    }

    private static class Confetti {
        float x, y, vx, vy, rot, spin;
        int size, shape;
        Color color;
    }

    private void drawCentered(Graphics2D g, String text, int w, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (w - fm.stringWidth(text)) / 2, y);
    }
}