package game.animation;

import java.awt.*;

/**
 * Pixel-art do lenhador desenhado em código.
 *
 * Estrutura:
 *   • Corpo desenhado a partir de um grid 10×12 ("pixels grandes")
 *   • Pernas/botas desenhadas DINAMICAMENTE pra alternar passos (animação de caminhada)
 *   • Machado apoiado no ombro com leve oscilação ao caminhar
 *
 * O sprite é renderizado centralizado em (cx, cy). Bobbing leve simula o vai-e-vem
 * vertical da caminhada; o ciclo de passos alterna entre 4 fases (parado / pé esq /
 * parado / pé dir).
 */
public final class LumberjackArt {

    // ── Paleta ─────────────────────────────────────────────
    private static final Color SKIN       = new Color(232, 188, 145);
    private static final Color SKIN_DARK  = new Color(190, 145, 110);
    private static final Color CAP        = new Color(155,  60,  55);
    private static final Color CAP_DARK   = new Color(110,  35,  35);
    private static final Color SHIRT_RED  = new Color(180,  50,  50);
    private static final Color SHIRT_DARK = new Color(120,  30,  30);
    private static final Color PANTS      = new Color( 65,  85, 130);
    private static final Color PANTS_DARK = new Color( 40,  55,  90);
    private static final Color BOOT       = new Color( 65,  40,  25);
    private static final Color BEARD      = new Color( 95,  60,  40);
    private static final Color EYE        = new Color( 25,  25,  25);
    private static final Color AXE_WOOD   = new Color(130,  85,  45);
    private static final Color AXE_BLADE  = new Color(195, 200, 210);
    private static final Color AXE_DARK   = new Color(110, 115, 125);
    private static final Color SHADOW     = new Color(0, 0, 0, 100);

    // Corpo SEM pernas/botas (linhas 0..9) — pernas desenhadas dinamicamente abaixo
    private static final String[] BODY = {
            "..CCCCCC..",  // 0  topo do gorro
            ".CcccccccC", // 1  gorro
            ".CcccccccC", // 2
            "..SSSSSS..", // 3  testa
            "..SoSoSoo.", // 4  olhos
            "..SSSSSSS.", // 5  rosto
            "..bbbbbb..", // 6  barba
            ".RRRrrRRR.", // 7  ombros
            "SRRRrrRRRS", // 8  mãos + camisa
            "rRRRrrRRRr", // 9  camisa
    };
    private static final int LEG_TOP_ROW = BODY.length; // pernas começam aqui (row 10)

    private LumberjackArt() {}

    /**
     * Desenha o lenhador centralizado em (cx, cy).
     * @param pixelScale tamanho de cada "pixel" do sprite (recomendado 3 a 8)
     * @param phase tempo em segundos — controla bobbing, ciclo de passos e oscilação do machado
     */
    public static void render(Graphics2D g, int cx, int cy, int pixelScale, float phase) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int spriteCols = BODY[0].length();   // 10
        int spriteRows = BODY.length + 2;    // corpo + 2 rows pra pernas/botas
        int spriteW = spriteCols * pixelScale;
        int spriteH = spriteRows * pixelScale;

        // Bobbing — sobe e desce no ritmo dos passos
        int bob = (int) (Math.abs(Math.sin(phase * 8)) * (pixelScale / 2.0));

        // Sobe o sprite pra alinhar os pés com o centro do caminho (em vez de centro do corpo)
        int feetAnchorOffset = pixelScale * 3;

        int x0 = cx - spriteW / 2;
        int y0 = cy - spriteH / 2 - feetAnchorOffset - bob;

        // Sombra sob os pés
        g2.setColor(SHADOW);
        g2.fillOval(cx - spriteW / 3, y0 + spriteH - pixelScale / 2,
                    spriteW * 2 / 3, pixelScale + 1);

        // 1. Corpo (grid)
        drawBody(g2, x0, y0, pixelScale);

        // 2. Pernas animadas
        drawWalkingLegs(g2, x0, y0, pixelScale, phase);

        // 3. Machado oscilante
        drawAxe(g2, x0, y0, pixelScale, phase);

        g2.dispose();
    }

    private static void drawBody(Graphics2D g, int x0, int y0, int p) {
        for (int r = 0; r < BODY.length; r++) {
            String row = BODY[r];
            for (int c = 0; c < row.length(); c++) {
                Color color = mapColor(row.charAt(c));
                if (color == null) continue;
                g.setColor(color);
                g.fillRect(x0 + c * p, y0 + r * p, p, p);
            }
        }
    }

    /**
     * Pernas e botas em 4 fases de caminhada:
     *   fase 0  → parado (ambas pernas juntas no centro)
     *   fase 1  → pé esquerdo avança (esq pra cima/frente)
     *   fase 2  → parado
     *   fase 3  → pé direito avança
     */
    private static void drawWalkingLegs(Graphics2D g, int x0, int y0, int p, float phase) {
        int step = ((int) (phase * 6)) & 3; // 4 fases, ~6 trocas por segundo

        int legTopY = y0 + LEG_TOP_ROW * p;
        int legW = 2 * p;
        int legH = p;
        int bootY = legTopY + legH;

        // Posições horizontais base (centro do corpo)
        int leftCenter  = x0 + 2 * p;
        int rightCenter = x0 + 6 * p;

        // Offsets verticais para o pé "no ar"
        int liftLeft  = 0;
        int liftRight = 0;
        // Pequena variação horizontal pro pé avançado
        int dxLeft  = 0;
        int dxRight = 0;

        switch (step) {
            case 1: liftLeft  = -p / 2; dxLeft  = -p / 3; break;
            case 3: liftRight = -p / 2; dxRight =  p / 3; break;
            default: /* parado */ break;
        }

        // Perna esquerda
        g.setColor(PANTS);
        g.fillRect(leftCenter + dxLeft, legTopY + liftLeft, legW, legH);
        g.setColor(BOOT);
        g.fillRect(leftCenter + dxLeft, bootY + liftLeft, legW, legH);

        // Perna direita
        g.setColor(PANTS);
        g.fillRect(rightCenter + dxRight, legTopY + liftRight, legW, legH);
        g.setColor(BOOT);
        g.fillRect(rightCenter + dxRight, bootY + liftRight, legW, legH);

        // Sombra sutil entre as pernas (efeito de profundidade)
        g.setColor(PANTS_DARK);
        g.fillRect(x0 + 4 * p, legTopY, 2 * p, 1);
    }

    private static Color mapColor(char ch) {
        switch (ch) {
            case '.': return null;
            case 'C': return CAP_DARK;
            case 'c': return CAP;
            case 'S': return SKIN;
            case 's': return SKIN_DARK;
            case 'o': return EYE;
            case 'b': return BEARD;
            case 'R': return SHIRT_RED;
            case 'r': return SHIRT_DARK;
            default:  return null;
        }
    }

    private static void drawAxe(Graphics2D g, int x0, int y0, int p, float phase) {
        int baseX = x0 + 9 * p;
        int baseY = y0 + 7 * p;
        int tilt = (int) (Math.sin(phase * 8) * (p / 2.0));

        // Cabo
        g.setColor(AXE_WOOD);
        for (int i = 0; i < 7; i++) {
            int hx = baseX + i / 3 + tilt;
            int hy = baseY + i * p;
            g.fillRect(hx, hy, p, p);
        }

        // Lâmina
        int bx = baseX - p + tilt;
        int by = baseY - p * 2;
        g.setColor(AXE_BLADE);
        g.fillRect(bx, by, p * 3, p * 2);
        g.fillRect(bx + p, by - p, p * 2, p);
        g.setColor(AXE_DARK);
        g.fillRect(bx, by + p, p * 3, 1);
        g.fillRect(bx + p * 3 - 1, by, 1, p * 2);
    }
}
