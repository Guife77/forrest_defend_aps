package game.ui;

import game.engine.WaveManager;
import game.entities.Player;
import game.utils.Constants;

import java.awt.*;

public class HUD {

    public static final int PANEL_H = 58;

    private static final Color BG_PANEL  = new Color(15, 15, 15, 215);
    private static final Color SEPARATOR = new Color(80, 80, 80);

    public void render(Graphics2D g, Player player, WaveManager waves,
                       int screenW, int screenH, char selectedTower, boolean showRanges) {

        int panelY = screenH - PANEL_H;

        // Fundo do painel
        g.setColor(BG_PANEL);
        g.fillRect(0, panelY, screenW, PANEL_H);
        g.setColor(SEPARATOR);
        g.drawLine(0, panelY, screenW, panelY);

        // ── Linha 1: stats ──
        int y1 = panelY + 20;

        // HP da base
        drawStat(g, "HP BASE", player.getBaseHealth() + "%",
                hpColor(player.getBaseHealth()), 14, y1);

        // RF
        drawStat(g, "FLORESTA", "" + player.getForestResources() + " RF",
                new Color(80, 220, 100), 160, y1);

        // Mana
        drawStat(g, "MANA", "" + player.getMana(),
                new Color(100, 160, 255), 330, y1);

        // Wave
        String waveText = waves.getCurrentWave() == 0
                ? "Pressione SPACE"
                : waves.isWaveActive()
                ? "Wave " + waves.getCurrentWave() + " — ativa"
                : "Wave " + waves.getCurrentWave() + " concluida — SPACE p/ próxima";
        drawStat(g, "ONDA", waveText, new Color(255, 210, 60), 470, y1);

        // ── Linha 2: seleção de torre + dica ──
        int y2 = panelY + 42;
        g.setFont(new Font("Arial", Font.PLAIN, 11));

        // Torres disponíveis com custo
        drawTowerBtn(g, "[T] Árvore", Constants.COST_TREE   + " RF", selectedTower == 'T', 14, y2);
        drawTowerBtn(g, "[A] Arara",  Constants.COST_BIRD   + " RF", selectedTower == 'A', 160, y2);
        drawTowerBtn(g, "[B] Barreira", Constants.COST_BARRIER + " RF", selectedTower == 'B', 290, y2);

        // Atalhos
        g.setColor(new Color(130, 130, 130));
        String keys = "[R] ranges" + (showRanges ? " ON" : " OFF") + "  |  CLIQUE = construir  |  ESC = sair";
        g.drawString(keys, 460, y2);
    }

    private void drawStat(Graphics2D g, String label, String value, Color valueColor, int x, int y) {
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setColor(new Color(150, 150, 150));
        g.drawString(label, x, y - 8);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(valueColor);
        g.drawString(value, x, y);
    }

    private void drawTowerBtn(Graphics2D g, String name, String cost, boolean selected, int x, int y) {
        if (selected) {
            g.setColor(new Color(80, 200, 80, 60));
            g.fillRoundRect(x - 4, y - 13, 130, 17, 4, 4);
            g.setColor(new Color(80, 200, 80));
        } else {
            g.setColor(new Color(160, 160, 160));
        }
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.drawString(name, x, y);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setColor(new Color(200, 180, 80));
        g.drawString(cost, x + 80, y);
    }

    private Color hpColor(int hp) {
        if (hp > 60) return new Color(80, 220, 100);
        if (hp > 30) return new Color(255, 200, 50);
        return new Color(255, 80, 80);
    }

    public void renderGameOver(Graphics2D g, int w, int h, int wave) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, w, h);

        g.setFont(new Font("Arial", Font.BOLD, 52));
        g.setColor(new Color(255, 70, 70));
        drawCentered(g, "GAME OVER", w, h / 2 - 20);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        drawCentered(g, "Chegou até a wave " + wave + "  |  [R] para reiniciar", w, h / 2 + 30);
    }

    public void renderVictory(Graphics2D g, int w, int h) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, w, h);

        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.setColor(new Color(80, 255, 120));
        drawCentered(g, "FLORESTA SALVA!", w, h / 2 - 20);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        drawCentered(g, "Você defendeu a floresta!  |  [R] jogar novamente", w, h / 2 + 30);
    }

    private void drawCentered(Graphics2D g, String text, int w, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (w - fm.stringWidth(text)) / 2, y);
    }
}