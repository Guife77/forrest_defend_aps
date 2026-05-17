package game.ui;

import game.engine.WaveManager;
import game.entities.Player;
import game.utils.Constants;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HUD {

    public static final int PANEL_H = 64;

    private static final Color BG_PANEL      = new Color(20, 25, 22, 245);
    private static final Color BORDER_PANEL  = new Color(55, 75, 60);
    private static final Color BG_CARD       = new Color(32, 36, 34);
    private static final Color BORDER_CARD   = new Color(55, 60, 58);
    private static final Color TEXT_LABEL    = new Color(140, 150, 145);

    public void render(Graphics2D g, Player player, WaveManager waves,
                       int screenW, int screenH, char selectedTower, boolean showRanges) {

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
        drawTowerCard(g, "[B] BARREIRA", Constants.COST_BARRIER + " RF", selectedTower == 'B', towerStartX + 224, cardY, cardW, cardH, new Color(211, 84, 0));


        // ── BLOCO DIREITA: Menu de Controles (Limpo sem o botão X) ──
        int rightX = 615;
        
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.setColor(TEXT_LABEL);
        g.drawString("ATALHOS & CONTROLES", rightX, panelY + 15);

        g.setFont(new Font("Arial", Font.PLAIN, 11));
        g.setColor(new Color(200, 205, 200)); 
        g.drawString("• [CLIQUE] Construir Torres", rightX, panelY + 30);
        g.drawString("• [R] Ver Alcances: " + (showRanges ? "ON" : "OFF"), rightX, panelY + 43);
        g.drawString("• [ESC] Sair do Jogo", rightX, panelY + 56);
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
        drawCentered(g, "Pressione [ R ] para Tentar Novamente   |   [ ESC ] Sair", w, pY + 165);
    }

    public void renderVictory(Graphics2D g, int w, int h) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. Fundo Gradiente Radial (Efeito Vignette de Sucesso)
        // Centro: Verde Amazônia profundo | Bordas: Preto (Luz no centro da floresta)
        Point2D center = new Point2D.Float(w / 2f, h / 2f);
        float radius = Math.max(w, h);
        float[] dist = {0.0f, 0.7f};
        Color[] colors = {new Color(20, 50, 30, 210), new Color(0, 0, 0, 240)};
        RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
        g.setPaint(p);
        g.fillRect(0, 0, w, h);

        // 2. Painel Central (Card de Vitória com borda dourada/luz)
        int pW = 600;
        int pH = 180;
        int pX = (w - pW) / 2;
        int pY = (h - pH) / 2;
        
        // Fundo do card (quase preto opaco)
        g.setColor(new Color(10, 15, 12, 230)); 
        g.fillRoundRect(pX, pY, pW, pH, 15, 15);
        // Borda dourada brilhante
        g.setColor(new Color(241, 196, 15)); // Amarelo Dourado Flat
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(pX, pY, pW, pH, 15, 15);
        g.setStroke(new BasicStroke(1));


        // 3. Tipografia e Conteúdo (Alinhado)
        // Linha 1: Título Principal
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.setColor(new Color(46, 204, 113)); // Verde Flat vibrante
        drawCentered(g, "✦ FLORESTA PROTEGIDA! ✦", w, pY + 65);

        // Linha 2: Descrição da Conquista
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(new Color(220, 220, 220)); // Branco suave
        drawCentered(g, "Conseguiste expelir todos os invasores da Amazônia!", w, pY + 105);

        // Linha auxiliar de história
        g.setFont(new Font("Arial", Font.ITALIC, 14));
        g.setColor(new Color(150, 180, 150)); // Cinza esverdeado
        drawCentered(g, "A natureza agradece o teu comando sagaz.", w, pY + 125);

        // Linha 3: Controles Separados e discretos
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(new Color(150, 150, 150)); // Cinza secundário
        drawCentered(g, "Pressione [ R ] para Novo Jogo   |   [ ESC ] Sair", w, pY + 165);
    }

    private void drawCentered(Graphics2D g, String text, int w, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (w - fm.stringWidth(text)) / 2, y);
    }
}