package game.ui;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Menu {

    public void render(Graphics2D g, int w, int h) {
        // Ativar suavização de alta qualidade para fontes e formas
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Fundo Gradiente Atmosférico (Cenário de Floresta Densa/Madrugada)
        // Começa com um verde floresta escuro no topo e desce até o preto absoluto
        GradientPaint jungleGradient = new GradientPaint(
                0, 0, new Color(10, 35, 18),
                0, h, new Color(5, 10, 8)
        );
        g.setPaint(jungleGradient);
        g.fillRect(0, 0, w, h);

        // Moldura sutil nas bordas do ecrã para dar acabamento de UI
        g.setColor(new Color(40, 65, 48, 100));
        g.setStroke(new BasicStroke(4));
        g.drawRect(4, 4, w - 8, h - 8);
        g.setStroke(new BasicStroke(1));

        // 2. TÍTULO PRINCIPAL (Com efeito de sombra e volume)
        String title = "FORREST DEFEND";
        g.setFont(new Font("Arial", Font.BOLD, 64));
        
        // Camada 1: Sombra projetada profunda (Deslocada para baixo e direita)
        g.setColor(new Color(0, 0, 0, 200));
        drawCentered(g, title, w, h / 2 - 76);
        
        // Camada 2: Brilho de contorno sutil
        g.setColor(new Color(30, 80, 45));
        drawCentered(g, title, w, h / 2 - 82);

        // Camada 3: Texto Principal Frontal (Verde Esmeralda Vibrante)
        g.setColor(new Color(46, 204, 113));
        drawCentered(g, title, w, h / 2 - 80);


        // 3. TEXTO PULSANTE DO ENTER (Efeito de animação puramente via código)
        // Usa o tempo em milissegundos para gerar uma oscilação suave de opacidade (Alpha)
        long time = System.currentTimeMillis();
        int alpha = (int) (130 + 125 * Math.sin(time * 0.004)); 
        alpha = Math.max(0, Math.min(255, alpha)); // Garante que o valor fique estritamente entre 0 e 255

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(new Color(255, 255, 255, alpha)); // Aplica a opacidade variável
        drawCentered(g, "Pressione [ ENTER ] para Iniciar a Defesa", w, h / 2 + 20);


        // 4. CARD DE VISUALIZAÇÃO DE COMANDOS (Na parte inferior)
        int cardW = 460;
        int cardH = 100;
        int cardX = (w - cardW) / 2;
        int cardY = h - cardH - 60;

        // Fundo do painel de comandos (Transparência escura elegante)
        g.setColor(new Color(20, 30, 24, 180));
        g.fillRoundRect(cardX, cardY, cardW, cardH, 12, 12);
        g.setColor(new Color(55, 85, 65, 150));
        g.drawRoundRect(cardX, cardY, cardW, cardH, 12, 12);

        // Título do painel de comandos
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(new Color(130, 160, 140));
        drawCentered(g, "GUIA RÁPIDO DE COMANDOS", w, cardY + 22);

        // Lista de atalhos organizada e espaçada
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        g.setColor(new Color(210, 220, 215));
        drawCentered(g, "• [Clique Esquerdo] Construir a Defesa Selecionada", w, cardY + 46);
        drawCentered(g, "• [T], [A], [B] Alternar Torres  |  [R] Mostrar Alcances", w, cardY + 66);
        drawCentered(g, "• [Espaço] Iniciar Próxima Onda  |  [ESC] Fechar Jogo", w, cardY + 86);


        // Rodapé acadêmico discreto
        g.setFont(new Font("Arial", Font.ITALIC, 12));
        g.setColor(new Color(90, 115, 100));
        drawCentered(g, "Projeto APS — UNIP 2026", w, h - 25);
    }

    private void drawCentered(Graphics2D g, String text, int w, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}