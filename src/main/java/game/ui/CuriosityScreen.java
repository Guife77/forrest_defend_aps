package game.ui;

import game.animation.Acacu;
import game.animation.Aranha;
import game.animation.Arara;
import game.animation.Escavadeira;
import game.animation.LumberjackArt;
import game.animation.Poluicao;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela "Curiosidades" — acessível do menu (tecla C) e das telas de vitória/derrota.
 *
 * Páginas:
 *   0  → Sobre (Por que Forrest Defend?)
 *   1  → Os Criadores (cards individuais)
 *   2+ → Cards de animais e inimigos
 *   N  → Onça-Pintada como menção honrosa
 */
public class CuriosityScreen {

    private final List<Page> pages = new ArrayList<>();
    private int currentPage = 0;

    private final Arara araraAnim   = new Arara("arara");
    private final Aranha aranhaAnim = new Aranha("aranha");
    private final Acacu acacuAnim   = new Acacu();
    private final Escavadeira escavAnim = new Escavadeira("escavadeira", 4);
    private final Poluicao poluicaoAnim = new Poluicao();
    private BufferedImage oncaImg;
    private long lastAcacuAttack = 0;

    public CuriosityScreen() {
        buildPages();
        loadOncaSprite();
    }

    public int getCurrentPage() { return currentPage; }
    public int getPageCount()   { return pages.size(); }

    public void nextPage() { currentPage = (currentPage + 1) % pages.size(); }
    public void prevPage() { currentPage = (currentPage - 1 + pages.size()) % pages.size(); }
    public void reset()    { currentPage = 0; }

    public String hitTest(int x, int y, int screenW, int screenH) {
        if (x >= 40 && x <= 100 && y >= screenH / 2 - 30 && y <= screenH / 2 + 30) return "prev";
        if (x >= screenW - 100 && x <= screenW - 40 && y >= screenH / 2 - 30 && y <= screenH / 2 + 30) return "next";
        if (x >= screenW - 140 && x <= screenW - 20 && y >= 18 && y <= 54) return "back";
        return null;
    }

    public void render(Graphics2D g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawBackground(g2, w, h);
        drawHeader(g2, w);

        int cardW = Math.min(w - 220, 880);
        int cardH = h - 230;
        int cardX = (w - cardW) / 2;
        int cardY = 120;
        drawCard(g2, cardX, cardY, cardW, cardH);

        pages.get(currentPage).render(g2, cardX, cardY, cardW, cardH);

        drawNavArrows(g2, w, h);
        drawPageIndicator(g2, w, h);
        drawBackButton(g2, w);
        drawFooter(g2, w, h);

        g2.dispose();
    }

    // ────────────────────────────────────────────────────────
    //  Background com gradiente + grão verde sutil
    // ────────────────────────────────────────────────────────

    private void drawBackground(Graphics2D g, int w, int h) {
        GradientPaint bg = new GradientPaint(0, 0, new Color(10, 32, 20), 0, h, new Color(3, 10, 6));
        g.setPaint(bg);
        g.fillRect(0, 0, w, h);

        // Glow radial no centro
        g.setPaint(new RadialGradientPaint(
                w / 2f, h / 2f, Math.max(w, h) * 0.6f,
                new float[]{0f, 1f},
                new Color[]{new Color(46, 180, 100, 30), new Color(0, 0, 0, 0)}));
        g.fillRect(0, 0, w, h);
    }

    private void drawHeader(Graphics2D g, int w) {
        g.setFont(new Font("Arial", Font.BOLD, 38));
        g.setColor(new Color(46, 204, 113));
        drawCentered(g, "CURIOSIDADES", w, 72);

        g.setFont(new Font("Arial", Font.ITALIC, 14));
        g.setColor(new Color(150, 180, 160));
        drawCentered(g, "Conheça o motivo, os criadores e os personagens da Amazônia", w, 95);
    }

    private void drawCard(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(0, 0, 0, 130));
        g.fillRoundRect(x + 5, y + 7, w, h, 22, 22);
        g.setColor(new Color(20, 30, 24, 235));
        g.fillRoundRect(x, y, w, h, 22, 22);
        g.setColor(new Color(46, 204, 113, 180));
        g.setStroke(new BasicStroke(2.5f));
        g.drawRoundRect(x, y, w, h, 22, 22);
        g.setStroke(new BasicStroke(1));
    }

    private void drawNavArrows(Graphics2D g, int w, int h) {
        long t = System.currentTimeMillis();
        int bob = (int) (Math.sin(t * 0.005) * 2);
        int cy = h / 2;
        drawNavArrow(g, 70, cy, true, bob);
        drawNavArrow(g, w - 70, cy, false, bob);
    }

    private void drawNavArrow(Graphics2D g, int cx, int cy, boolean left, int bob) {
        int dx = left ? -1 : 1;
        g.setColor(new Color(46, 204, 113, 140));
        g.fillOval(cx - 22, cy - 22 + bob, 44, 44);
        g.setColor(new Color(46, 204, 113));
        g.setStroke(new BasicStroke(2.5f));
        g.drawOval(cx - 22, cy - 22 + bob, 44, 44);
        Polygon p = new Polygon(
                new int[]{cx + dx * 8, cx - dx * 6, cx - dx * 6},
                new int[]{cy + bob, cy + bob - 9, cy + bob + 9}, 3);
        g.setColor(Color.WHITE);
        g.fillPolygon(p);
        g.setStroke(new BasicStroke(1));
    }

    private void drawPageIndicator(Graphics2D g, int w, int h) {
        int n = pages.size();
        int gap = 16;
        int totalW = n * gap;
        int startX = (w - totalW) / 2;
        int y = h - 75;
        for (int i = 0; i < n; i++) {
            boolean active = (i == currentPage);
            g.setColor(active ? new Color(46, 204, 113) : new Color(60, 80, 70));
            int r = active ? 7 : 4;
            g.fillOval(startX + i * gap, y, r * 2, r * 2);
        }
    }

    private void drawBackButton(Graphics2D g, int w) {
        int x = w - 140, y = 18, bw = 120, bh = 36;
        g.setColor(new Color(20, 30, 24, 220));
        g.fillRoundRect(x, y, bw, bh, 10, 10);
        g.setColor(new Color(46, 204, 113));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, bw, bh, 10, 10);
        g.setStroke(new BasicStroke(1));
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.WHITE);
        g.drawString("← VOLTAR  [ESC]", x + 8, y + 23);
    }

    private void drawFooter(Graphics2D g, int w, int h) {
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(new Color(120, 145, 130));
        drawCentered(g, "← →  navegar entre páginas    |    ESC  voltar", w, h - 35);
    }

    private static void drawCentered(Graphics2D g, String text, int w, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (w - fm.stringWidth(text)) / 2, y);
    }

    /** Quebra texto em linhas que cabem em maxW. */
    private static int drawTextBlock(Graphics2D g, String text, int x, int y, int maxW, int lineH) {
        FontMetrics fm = g.getFontMetrics();
        String[] paragraphs = text.split("\n");
        int cy = y;
        for (String para : paragraphs) {
            String[] words = para.split(" ");
            StringBuilder line = new StringBuilder();
            for (String word : words) {
                String test = line.length() == 0 ? word : line + " " + word;
                if (fm.stringWidth(test) > maxW) {
                    g.drawString(line.toString(), x, cy);
                    line = new StringBuilder(word);
                    cy += lineH;
                } else {
                    line = new StringBuilder(test);
                }
            }
            if (line.length() > 0) {
                g.drawString(line.toString(), x, cy);
                cy += lineH;
            }
        }
        return cy;
    }

    // ────────────────────────────────────────────────────────
    //  Construção
    // ────────────────────────────────────────────────────────

    private void buildPages() {
        pages.add(new AboutPage());
        pages.add(new CreatorsPage());
        pages.add(new AcacuPage());
        pages.add(new AraraPage());
        pages.add(new AranhaPage());
        pages.add(new LumberjackPage());
        pages.add(new EscavadeiraPage());
        pages.add(new PollutionPage());
        pages.add(new OncaPage());
    }

    private void loadOncaSprite() {
        String[] candidates = {
                "public/onca1.png",
                "src/main/resources/public/onca1.png",
                "src/public/onca1.png"
        };
        for (String p : candidates) {
            try {
                File f = new File(p);
                if (f.exists()) { oncaImg = ImageIO.read(f); return; }
            } catch (IOException ignored) {}
        }
    }

    private interface Page {
        void render(Graphics2D g, int x, int y, int w, int h);
    }

    // ────────────────────────────────────────────────────────
    //  Página: Sobre o jogo
    // ────────────────────────────────────────────────────────

    private static class AboutPage implements Page {
        @Override
        public void render(Graphics2D g, int x, int y, int w, int h) {
            // Título
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(new Color(241, 196, 15));
            drawCenteredIn(g, "POR QUE FORREST DEFEND?", x, y + 60, w);

            // Subtítulo
            g.setFont(new Font("Arial", Font.ITALIC, 15));
            g.setColor(new Color(160, 200, 170));
            drawCenteredIn(g, "Uma homenagem em forma de jogo à maior floresta do mundo", x, y + 88, w);

            // Linha decorativa
            g.setColor(new Color(46, 204, 113, 180));
            g.fillRect(x + w / 4, y + 102, w / 2, 2);

            // Texto principal
            int tx = x + 60;
            int tw = w - 120;
            g.setFont(new Font("Arial", Font.PLAIN, 15));
            g.setColor(new Color(220, 230, 222));
            String body =
                    "Forrest Defend nasceu de um desejo simples: fazer os brasileiros olharem para a " +
                    "Amazônia não como um lugar distante e exótico, mas como o coração verde do próprio país.\n" +
                    "A maior floresta tropical do planeta está aqui — 60% dela em território brasileiro. " +
                    "São mais de 5,5 milhões de km², 30 mil espécies de plantas, 1.300 espécies de aves, " +
                    "e povos originários com saberes milenares.\n" +
                    "Cada wave deste jogo é uma metáfora pequena de uma luta enorme: a defesa começa com " +
                    "informação, consciência cultural e noção do tamanho do que temos em mãos.";
            drawTextBlock(g, body, tx, y + 138, tw, 22);

            // Stats em 3 caixas
            int boxY = y + h - 130;
            int boxW = (tw - 30) / 3;
            drawAboutStat(g, tx,                       boxY, boxW, 80, "DO TERRITÓRIO BR",   "60%",     new Color(46, 204, 113));
            drawAboutStat(g, tx + boxW + 15,           boxY, boxW, 80, "ESPÉCIES VEGETAIS",  "30.000+", new Color(241, 196, 15));
            drawAboutStat(g, tx + (boxW + 15) * 2,     boxY, boxW, 80, "MILHÕES DE HABITANTES", "20+",  new Color(52, 152, 219));
        }

        private static void drawAboutStat(Graphics2D g, int x, int y, int w, int h, String label, String value, Color accent) {
            g.setColor(new Color(15, 25, 18, 220));
            g.fillRoundRect(x, y, w, h, 12, 12);
            g.setColor(accent);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(x, y, w, h, 12, 12);
            g.setStroke(new BasicStroke(1));

            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.setColor(accent);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(value, x + (w - fm.stringWidth(value)) / 2, y + 38);

            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.setColor(new Color(140, 160, 145));
            fm = g.getFontMetrics();
            g.drawString(label, x + (w - fm.stringWidth(label)) / 2, y + 60);
        }

        private static void drawCenteredIn(Graphics2D g, String text, int x, int y, int w) {
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, x + (w - fm.stringWidth(text)) / 2, y);
        }
    }

    // ────────────────────────────────────────────────────────
    //  Página: Criadores (modernizada — cards individuais)
    // ────────────────────────────────────────────────────────

    private static class CreatorsPage implements Page {
        private static final String[] NAMES = {
                "Guilherme Fernandes Claudino Costa",
                "Miguel da Silva Bahia",
                "Paulo Vitor Veríssimo Pereira",
                "Renato Aparecido Rodrigues da Silva Stabile"
        };
        private static final Color[] ACCENTS = {
                new Color(46, 204, 113),
                new Color(52, 152, 219),
                new Color(241, 196, 15),
                new Color(231, 76, 60)
        };

        @Override
        public void render(Graphics2D g, int x, int y, int w, int h) {
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.setColor(new Color(241, 196, 15));
            FontMetrics fm = g.getFontMetrics();
            String title = "OS CRIADORES";
            g.drawString(title, x + (w - fm.stringWidth(title)) / 2, y + 64);

            g.setFont(new Font("Arial", Font.ITALIC, 14));
            g.setColor(new Color(160, 200, 170));
            String sub = "Equipe de desenvolvimento — Projeto APS UNIP 2026";
            fm = g.getFontMetrics();
            g.drawString(sub, x + (w - fm.stringWidth(sub)) / 2, y + 90);

            g.setColor(new Color(46, 204, 113, 180));
            g.fillRect(x + w / 4, y + 102, w / 2, 2);

            // 4 cards individuais empilhados
            int cardW = w - 140;
            int cardH = 78;
            int gap = 14;
            int startY = y + 130;
            for (int i = 0; i < NAMES.length; i++) {
                drawDevCard(g, x + 70, startY + i * (cardH + gap), cardW, cardH,
                            NAMES[i], ACCENTS[i]);
            }
        }

        private void drawDevCard(Graphics2D g, int x, int y, int w, int h, String name, Color accent) {
            // Sombra
            g.setColor(new Color(0, 0, 0, 100));
            g.fillRoundRect(x + 3, y + 4, w, h, 14, 14);
            // Fundo
            g.setColor(new Color(28, 40, 32, 240));
            g.fillRoundRect(x, y, w, h, 14, 14);
            // Borda
            g.setColor(accent);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(x, y, w, h, 14, 14);
            g.setStroke(new BasicStroke(1));

            // Faixa lateral colorida
            g.setColor(accent);
            g.fillRoundRect(x, y, 8, h, 14, 14);
            g.fillRect(x + 4, y, 4, h);

            // Avatar circular com iniciais
            int avSize = h - 18;
            int avX = x + 25;
            int avY = y + (h - avSize) / 2;
            g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60));
            g.fillOval(avX - 4, avY - 4, avSize + 8, avSize + 8);
            g.setColor(accent);
            g.fillOval(avX, avY, avSize, avSize);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2f));
            g.drawOval(avX, avY, avSize, avSize);
            g.setStroke(new BasicStroke(1));

            // Iniciais no avatar
            String initials = computeInitials(name);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(Color.WHITE);
            g.drawString(initials,
                    avX + (avSize - fm.stringWidth(initials)) / 2,
                    avY + (avSize + fm.getAscent()) / 2 - 4);

            // Nome
            g.setFont(new Font("Arial", Font.BOLD, 17));
            g.setColor(new Color(235, 240, 232));
            g.drawString(name, avX + avSize + 22, y + 32);

            // Cargo
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.setColor(new Color(140, 170, 150));
            g.drawString("Desenvolvedor", avX + avSize + 22, y + 53);
        }

        private static String computeInitials(String name) {
            String[] parts = name.split(" ");
            StringBuilder sb = new StringBuilder();
            for (String p : parts) {
                if (p.length() > 0 && Character.isUpperCase(p.charAt(0))) {
                    sb.append(p.charAt(0));
                    if (sb.length() >= 2) break;
                }
            }
            return sb.toString();
        }
    }

    // ────────────────────────────────────────────────────────
    //  Páginas de Animais/Inimigos
    // ────────────────────────────────────────────────────────

    private abstract class AnimalPage implements Page {
        abstract String name();
        abstract String tag();
        abstract Color tagColor();
        abstract String description();
        abstract String curiosity();
        /** Sprite size desejado (em pixels lógicos). */
        abstract int artSize();
        abstract void drawArt(Graphics2D g, int cx, int cy, int size);

        @Override
        public void render(Graphics2D g, int x, int y, int w, int h) {
            int artCx = x + w / 4;
            int artCy = y + h / 2 - 20;
            int artR = 110;

            // Halo radial
            g.setPaint(new RadialGradientPaint(
                    artCx, artCy, artR * 1.4f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(46, 204, 113, 80), new Color(46, 204, 113, 0)}));
            g.fillOval(artCx - artR * 2, artCy - artR * 2, artR * 4, artR * 4);

            // Círculo da arte
            g.setColor(new Color(46, 204, 113, 50));
            g.fillOval(artCx - artR, artCy - artR, artR * 2, artR * 2);
            g.setColor(new Color(46, 204, 113, 160));
            g.setStroke(new BasicStroke(2.5f));
            g.drawOval(artCx - artR, artCy - artR, artR * 2, artR * 2);
            g.setStroke(new BasicStroke(1));

            drawArt(g, artCx, artCy, artSize());

            // Bloco texto direito
            int tx = x + w / 2 - 20;
            int tw = w / 2 - 20;

            g.setFont(new Font("Arial", Font.BOLD, 34));
            g.setColor(new Color(241, 196, 15));
            g.drawString(name(), tx, y + 80);

            g.setFont(new Font("Arial", Font.BOLD, 11));
            int tagW = g.getFontMetrics().stringWidth(tag()) + 22;
            g.setColor(tagColor());
            g.fillRoundRect(tx, y + 95, tagW, 24, 8, 8);
            g.setColor(Color.WHITE);
            g.drawString(tag(), tx + 11, y + 112);

            g.setFont(new Font("Arial", Font.BOLD, 13));
            g.setColor(new Color(140, 200, 160));
            g.drawString("FUNÇÃO NO JOGO", tx, y + 150);
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.setColor(new Color(220, 230, 222));
            drawTextBlock(g, description(), tx, y + 172, tw, 20);

            g.setFont(new Font("Arial", Font.BOLD, 13));
            g.setColor(new Color(140, 200, 160));
            g.drawString("CURIOSIDADE", tx, y + 280);
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.setColor(new Color(220, 230, 222));
            drawTextBlock(g, curiosity(), tx, y + 302, tw, 20);
        }
    }

    /** Aplica escala temporária em torno de (cx, cy) e renderiza algo. */
    private static Graphics2D scaledAround(Graphics2D g, int cx, int cy, double scale) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        AffineTransform at = new AffineTransform();
        at.translate(cx, cy);
        at.scale(scale, scale);
        at.translate(-cx, -cy);
        g2.transform(at);
        return g2;
    }

    private class AcacuPage extends AnimalPage {
        String name()      { return "AÇAÇU"; }
        String tag()       { return "DEFENSOR"; }
        Color tagColor()   { return new Color(39, 174, 96); }
        int artSize()      { return 220; }
        String description() {
            return "Árvore guardiã que se transforma em criatura furiosa quando ataca. " +
                   "Defesa terrestre de médio alcance — boa pra controlar ondas de inimigos " +
                   "que passam perto.";
        }
        String curiosity() {
            return "O Açaçu (Hura crepitans) é nativo da Amazônia, conhecido também como " +
                   "'árvore-dinamite'. Seu tronco é coberto de espinhos cônicos pretos — " +
                   "defesa natural contra animais. Mas o grande truque vem dos frutos: " +
                   "eles EXPLODEM ao amadurecer, lançando sementes a até 100 metros. " +
                   "O látex é tão tóxico que povos originários o usaram em pontas de flecha.";
        }
        void drawArt(Graphics2D g, int cx, int cy, int size) {
            // Dispara o ataque a cada 2.5s pra mostrar a transformação na tela de curiosidade
            long now = System.currentTimeMillis();
            if (now - lastAcacuAttack > 2500) {
                acacuAnim.playAttack();
                lastAcacuAttack = now;
            }
            acacuAnim.update();
            Graphics2D g2 = scaledAround(g, cx, cy, size / 80.0);
            acacuAnim.render(g2, cx, cy);
            g2.dispose();
        }
    }

    private class AraraPage extends AnimalPage {
        String name()      { return "ARARA-AZUL"; }
        String tag()       { return "DEFENSOR"; }
        Color tagColor()   { return new Color(39, 174, 96); }
        int artSize()      { return 180; }
        String description() {
            return "Defesa aérea de longo alcance. Ataca inimigos a distância com bicadas " +
                   "rápidas em formação de mergulho. Ideal contra invasores velozes.";
        }
        String curiosity() {
            return "Em algumas tribos amazônicas, a arara é mensageira dos ancestrais. " +
                   "A arara-azul-grande quase foi extinta nos anos 80 — hoje, programas " +
                   "de conservação trouxeram a espécie de volta às matas brasileiras.";
        }
        void drawArt(Graphics2D g, int cx, int cy, int size) {
            araraAnim.update();
            Graphics2D g2 = scaledAround(g, cx, cy, size / 44.0);
            araraAnim.render(g2, cx, cy + 10);
            g2.dispose();
        }
    }

    private class AranhaPage extends AnimalPage {
        String name()      { return "CARANGUEJEIRA"; }
        String tag()       { return "DEFENSOR"; }
        Color tagColor()   { return new Color(142, 68, 173); }
        int artSize()      { return 180; }
        String description() {
            return "Defensora terrestre rápida que injeta veneno. Cooldown curto entre " +
                   "ataques — devastadora contra grupos de inimigos fracos.";
        }
        String curiosity() {
            return "A caranguejeira amazônica intimida pelo tamanho, mas é vital para " +
                   "controlar pragas que destruiriam plantações. Caça em silêncio " +
                   "absoluto e raramente ataca humanos sem provocação.";
        }
        void drawArt(Graphics2D g, int cx, int cy, int size) {
            aranhaAnim.update();
            Graphics2D g2 = scaledAround(g, cx, cy, size / 48.0);
            aranhaAnim.render(g2, cx, cy + 10);
            g2.dispose();
        }
    }

    private class LumberjackPage extends AnimalPage {
        String name()      { return "LENHADOR"; }
        String tag()       { return "INVASOR"; }
        Color tagColor()   { return new Color(231, 76, 60); }
        int artSize()      { return 0; } // não usado — desenha direto
        String description() {
            return "Humano armado com machado, vem buscar madeira ilegal. Frágil " +
                   "individualmente, mas costuma chegar em grandes grupos coordenados.";
        }
        String curiosity() {
            return "17% da Amazônia já foi desmatada — um território maior que a França " +
                   "inteira. A maior parte do desmatamento começa com extração ilegal " +
                   "de madeiras nobres como mogno e cedro.";
        }
        void drawArt(Graphics2D g, int cx, int cy, int size) {
            float phase = System.currentTimeMillis() * 0.001f;
            LumberjackArt.render(g, cx, cy + 20, 9, phase);
        }
    }

    private class EscavadeiraPage extends AnimalPage {
        String name()      { return "ESCAVADEIRA"; }
        String tag()       { return "INVASOR"; }
        Color tagColor()   { return new Color(231, 76, 60); }
        int artSize()      { return 180; }
        String description() {
            return "Máquina pesada e resistente — tem 50% de resistência a ataques físicos. " +
                   "Lenta mas devastadora: causa muito dano à base se chegar.";
        }
        String curiosity() {
            return "Uma única escavadeira pode derrubar mais de 100m² de mata por hora. " +
                   "Símbolo da destruição mecanizada para mineração ilegal e abertura " +
                   "de estradas clandestinas em áreas de proteção.";
        }
        void drawArt(Graphics2D g, int cx, int cy, int size) {
            escavAnim.update();
            Graphics2D g2 = scaledAround(g, cx, cy, size / 46.0);
            escavAnim.render(g2, cx, cy + 10);
            g2.dispose();
        }
    }

    private class PollutionPage extends AnimalPage {
        String name()      { return "POLUIÇÃO"; }
        String tag()       { return "INVASOR"; }
        Color tagColor()   { return new Color(120, 80, 140); }
        int artSize()      { return 180; }
        String description() {
            return "Nuvem tóxica que avança rápido pelo caminho. Imune a ataques de veneno — " +
                   "use defensoras como a Arara (projétil) contra ela.";
        }
        String curiosity() {
            return "Não é um ser vivo, mas se espalha como um. Representa fumaça de " +
                   "queimadas, resíduos de garimpo (mercúrio) e contaminação química. " +
                   "Mercúrio em rios amazônicos persiste por décadas no ecossistema.";
        }
        void drawArt(Graphics2D g, int cx, int cy, int size) {
            Graphics2D g2 = scaledAround(g, cx, cy, size / 38.0);
            poluicaoAnim.render(g2, cx, cy + 10);
            g2.dispose();
        }
    }

    // ── Página especial: Onça como menção honrosa ──────────

    private class OncaPage implements Page {
        @Override
        public void render(Graphics2D g, int x, int y, int w, int h) {
            int artCx = x + w / 4;
            int artCy = y + h / 2 - 20;
            int artR = 110;

            g.setPaint(new RadialGradientPaint(
                    artCx, artCy, artR * 1.4f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(241, 196, 15, 80), new Color(241, 196, 15, 0)}));
            g.fillOval(artCx - artR * 2, artCy - artR * 2, artR * 4, artR * 4);

            g.setColor(new Color(241, 196, 15, 50));
            g.fillOval(artCx - artR, artCy - artR, artR * 2, artR * 2);
            g.setColor(new Color(241, 196, 15, 160));
            g.setStroke(new BasicStroke(2.5f));
            g.drawOval(artCx - artR, artCy - artR, artR * 2, artR * 2);
            g.setStroke(new BasicStroke(1));

            // Onça (sprite ampliada)
            if (oncaImg != null) {
                int s = 200;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(oncaImg, artCx - s / 2, artCy - s / 2 + 10, s, s, null);
                g2.dispose();
            }

            int tx = x + w / 2 - 20;
            int tw = w / 2 - 20;

            g.setFont(new Font("Arial", Font.BOLD, 34));
            g.setColor(new Color(241, 196, 15));
            g.drawString("ONÇA-PINTADA", tx, y + 80);

            g.setFont(new Font("Arial", Font.BOLD, 11));
            String tagText = "PERSONAGEM PLANEJADO";
            int tagW = g.getFontMetrics().stringWidth(tagText) + 22;
            g.setColor(new Color(155, 89, 182));
            g.fillRoundRect(tx, y + 95, tagW, 24, 8, 8);
            g.setColor(Color.WHITE);
            g.drawString(tagText, tx + 11, y + 112);

            g.setFont(new Font("Arial", Font.BOLD, 13));
            g.setColor(new Color(220, 180, 100));
            g.drawString("MENÇÃO HONROSA", tx, y + 150);
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.setColor(new Color(220, 230, 222));
            drawTextBlock(g,
                    "A onça-pintada estava planejada para entrar como defensora especial — " +
                    "uma patrulheira de altíssimo dano corpo-a-corpo. Por falta de tempo " +
                    "para finalizar as animações de movimento e ataque, ficou de fora desta " +
                    "versão. Mas ela merece o seu espaço nas curiosidades.",
                    tx, y + 172, tw, 20);

            g.setFont(new Font("Arial", Font.BOLD, 13));
            g.setColor(new Color(220, 180, 100));
            g.drawString("CURIOSIDADE", tx, y + 290);
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.setColor(new Color(220, 230, 222));
            drawTextBlock(g,
                    "Reverenciada como deusa pelos Maias e personagem central de várias " +
                    "lendas indígenas brasileiras. É o maior felino das Américas — sua " +
                    "mordida quebra a casca de tartarugas.",
                    tx, y + 312, tw, 20);
        }
    }
}
