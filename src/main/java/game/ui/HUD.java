package game.ui;

import game.engine.WaveManager;
import game.entities.Player;
import game.utils.Constants;

import java.awt.*;

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

        int boxX = (screenW - txtW) / 2 - 20;
        g.setColor(new Color(15, 20, 15, 200));
        g.fillRoundRect(boxX, 15, txtW + 40, 32, 12, 12);
        g.setColor(new Color(60, 80, 60));
        g.drawRoundRect(boxX, 15, txtW + 40, 32, 12, 12);

        g.setColor(waveColor);
        g.drawString(waveText, (screenW - txtW) / 2, 37);
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
        g.setColor(new Color(15, 15, 15, 225));
        g.fillRect(0, 0, w, h);
        g.setFont(new Font("Arial", Font.BOLD, 54));
        g.setColor(new Color(192, 41, 43));
        drawCentered(g, "FIM DE JOGO", w, h / 2 - 20);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(Color.WHITE);
        drawCentered(g, "A floresta foi derrubada na wave " + wave + "  |  [R] Reiniciar", w, h / 2 + 30);
    }

    public void renderVictory(Graphics2D g, int w, int h) {
        g.setColor(new Color(10, 28, 16, 215));
        g.fillRect(0, 0, w, h);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.setColor(new Color(46, 204, 113));
        drawCentered(g, "FLORESTA PROTEGIDA!", w, h / 2 - 20);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(Color.WHITE);
        drawCentered(g, "Conseguiste repelir os invasores da Amazónia!  |  [R] Jogar de Novo", w, h / 2 + 30);
    }

    private void drawCentered(Graphics2D g, String text, int w, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (w - fm.stringWidth(text)) / 2, y);
    }
}