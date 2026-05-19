package game.map;

import game.world.GameMap;
import game.world.Tile;
import game.world.enums.TileType;

import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.Random;

/**
 * MapRenderer estilo Bloons TD6.
 * Grama rica, caminho de areia com bordas, árvores e arbustos decorativos.
 */
public class MapRenderer {

    // Paleta de cores
    private static final Color GRASS_BASE    = new Color(46, 125, 50);
    private static final Color GRASS_DARK    = new Color(27, 94,  32);
    private static final Color GRASS_LIGHT   = new Color(76, 153, 56);

    private static final Color PATH_BASE     = new Color(194, 158, 100);
    private static final Color PATH_DARK     = new Color(150, 112,  60);
    private static final Color PATH_EDGE     = new Color(120,  82,  35);
    private static final Color PATH_HIGHLIGHT= new Color(220, 185, 130);

    private static final Color BASE_FILL     = new Color(21,  101, 192);
    private static final Color BASE_BORDER   = new Color(13,   71, 161);
    private static final Color BASE_SHINE    = new Color(66,  165, 245);

    private static final Color ENTRY_FILL    = new Color(198,  40,  40);
    private static final Color ENTRY_BORDER  = new Color(183,  28,  28);

    private static final Color TREE_DARK     = new Color(27,  94,  32);
    private static final Color TREE_MID      = new Color(46, 125,  50);
    private static final Color TREE_LIGHT    = new Color(76, 175,  80);
    private static final Color TRUNK         = new Color(93,  64,  55);
    private static final Color BUSH          = new Color(33, 113,  40);

    private static final Color FLOWER_YELLOW = new Color(249, 168,  37);
    private static final Color FLOWER_PINK   = new Color(233,  30,  99);

    private static final Color GRID_LINE     = new Color(0, 0, 0, 18);


    private boolean decorationsBuilt = false;
    private int[]   treeX, treeY, treeR;
    private int[]   bushX, bushY;
    private int[]   flowerX, flowerY;
    private boolean[] flowerYellow;

    private final int tileSize;

    public MapRenderer(int tileSize) {
        this.tileSize = tileSize;
    }


    //  RENDER PRINCIPAL

    public void render(Graphics2D g, GameMap map) {
        int rows = map.getRows();
        int cols = map.getCols();

        setupHints(g);
        if (!decorationsBuilt) buildDecorations(map, rows, cols);

        // 1. Base da grama (todos os tiles)
        drawGrassBase(g, rows, cols);

        // 2. Caminho (bordas antes do miolo)
        drawPathBorders(g, map, rows, cols);
        drawPathFill(g, map, rows, cols);

        // 3. Grade sutil
        drawGrid(g, rows, cols);

        // 4. Detalhes do caminho (centro tracejado)
        // feito no renderPath()

        // 5. Decorações da grama
        drawDecorations(g, map);

        // 6. Base e entrada com destaque
        drawSpecialTiles(g, map, rows, cols);
    }

    /** Linha tracejada no centro do caminho — indica rota dos inimigos */
    public void renderPath(Graphics2D g, List<Point> path) {
        if (path == null || path.size() < 2) return;

        // Sombra da linha
        g.setColor(new Color(0, 0, 0, 30));
        g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1, new float[]{10, 10}, 0));
        drawPolyline(g, path);

        // Linha central amarela suave
        g.setColor(new Color(255, 213, 79, 80));
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1, new float[]{10, 10}, 0));
        drawPolyline(g, path);

        g.setStroke(new BasicStroke(1));
    }


    //  DETALHES INTERNOS


    private void setupHints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,       RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,  RenderingHints.VALUE_STROKE_PURE);
    }

    private void drawGrassBase(Graphics2D g, int rows, int cols) {
        int w = cols * tileSize;
        int h = rows * tileSize;

        // Fundo uniforme
        g.setColor(GRASS_BASE);
        g.fillRect(0, 0, w, h);

        // Variação de brilho em xadrez 2x2
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean checker = ((r + c) % 2 == 0);
                if (checker) {
                    g.setColor(new Color(0, 0, 0, 10));
                    g.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
                }
            }
        }
    }

    private void drawPathBorders(Graphics2D g, GameMap map, int rows, int cols) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile t = map.getTile(r, c);
                if (t == null) continue;
                if (t.getType() == TileType.PATH || t.getType() == TileType.BASE) {
                    int px = c * tileSize, py = r * tileSize;
                    g.setColor(PATH_EDGE);
                    g.fillRect(px, py, tileSize, tileSize);
                }
            }
        }
    }

    private void drawPathFill(Graphics2D g, GameMap map, int rows, int cols) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile t = map.getTile(r, c);
                if (t == null || t.getType() == TileType.GRASS) continue;
                if (t.getType() == TileType.BASE) continue; // desenhado depois

                int px = c * tileSize + 3;
                int py = r * tileSize + 3;
                int pw = tileSize - 6;
                int ph = tileSize - 6;

                // Miolo de areia
                g.setColor(PATH_BASE);
                g.fillRect(px, py, pw, ph);

                // Listras de textura
                g.setColor(new Color(0, 0, 0, 12));
                for (int i = 0; i < pw; i += 6) {
                    g.drawLine(px + i, py, px + i, py + ph);
                }

                // Realce superior esquerd
                g.setColor(new Color(255, 255, 255, 20));
                g.fillRect(px, py, pw, 3);
                g.fillRect(px, py, 3, ph);
            }
        }
    }

    private void drawGrid(Graphics2D g, int rows, int cols) {
        g.setColor(GRID_LINE);
        g.setStroke(new BasicStroke(0.5f));
        for (int r = 0; r <= rows; r++)
            g.drawLine(0, r * tileSize, cols * tileSize, r * tileSize);
        for (int c = 0; c <= cols; c++)
            g.drawLine(c * tileSize, 0, c * tileSize, rows * tileSize);
        g.setStroke(new BasicStroke(1));
    }

    private void drawSpecialTiles(Graphics2D g, GameMap map, int rows, int cols) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile t = map.getTile(r, c);
                if (t == null) continue;

                int px = c * tileSize;
                int py = r * tileSize;

                if (t.getType() == TileType.BASE) {
                    drawBaseTile(g, px, py);
                }

                // Entrada: tile PATH na coluna 0
                if (t.getType() == TileType.PATH && c == 0) {
                    drawEntryMarker(g, px, py);
                }
            }
        }
    }

    private void drawBaseTile(Graphics2D g, int px, int py) {
        int pad = 3;

        // Fundo azul do caminho sob a base
        g.setColor(PATH_EDGE);
        g.fillRect(px, py, tileSize, tileSize);
        g.setColor(PATH_BASE);
        g.fillRect(px + pad, py + pad, tileSize - pad * 2, tileSize - pad * 2);

        // Ícone da base (castelo simplificado)
        int bx = px + 5, by = py + 5, bw = tileSize - 10, bh = tileSize - 10;

        // Corpo
        g.setColor(BASE_FILL);
        g.fillRoundRect(bx, by + bh / 3, bw, bh * 2 / 3, 4, 4);

        // Ameias (3 blocos no topo)
        g.setColor(BASE_FILL);
        int aw = bw / 5;
        for (int i = 0; i < 3; i++) {
            int ax = bx + i * (aw + 2);
            g.fillRect(ax, by, aw, bh / 3);
        }

        // Borda
        g.setColor(BASE_BORDER);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(bx, by + bh / 3, bw, bh * 2 / 3, 4, 4);
        for (int i = 0; i < 3; i++) {
            int ax = bx + i * (aw + 2);
            g.drawRect(ax, by, aw, bh / 3);
        }

        // Brilho superior
        g.setColor(new Color(BASE_SHINE.getRed(), BASE_SHINE.getGreen(), BASE_SHINE.getBlue(), 80));
        g.fillRect(bx + 1, by + bh / 3 + 1, bw - 2, 3);
        g.setStroke(new BasicStroke(1));

        // Label
        g.setFont(new Font("Arial", Font.BOLD, 8));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String label = "BASE";
        g.drawString(label, px + (tileSize - fm.stringWidth(label)) / 2, py + tileSize - 4);
    }

    private void drawEntryMarker(Graphics2D g, int px, int py) {
        // Pequena seta vermelha na borda esquerda
        int cx = px + tileSize / 2;
        int cy = py + tileSize / 2;
        int r  = 7;

        g.setColor(ENTRY_FILL);
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(ENTRY_BORDER);
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(cx - r, cy - r, r * 2, r * 2);

        // Mini seta →
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(cx - 3, cy, cx + 3, cy);
        g.drawLine(cx + 1, cy - 2, cx + 3, cy);
        g.drawLine(cx + 1, cy + 2, cx + 3, cy);
        g.setStroke(new BasicStroke(1));
    }

    // ─────────────────────────────────────────────────────────────
    //  DECORAÇÕES (árvores, arbustos, flores)
    // ─────────────────────────────────────────────────────────────

    private void buildDecorations(GameMap map, int rows, int cols) {
        Random rng = new Random(42); // seed fixo = posições consistentes

        // Coleta tiles de grama disponíveis (longe do caminho)
        java.util.List<int[]> grassCells = new java.util.ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile t = map.getTile(r, c);
                if (t != null && t.getType() == TileType.GRASS && !adjacentToPath(map, r, c, rows, cols)) {
                    grassCells.add(new int[]{c, r});
                }
            }
        }

        // Árvores grandes
        int nTrees = Math.min(14, grassCells.size() / 4);
        treeX = new int[nTrees]; treeY = new int[nTrees]; treeR = new int[nTrees];
        java.util.Set<Integer> usedTree = new java.util.HashSet<>();
        int ti = 0;
        while (ti < nTrees && grassCells.size() > 0) {
            int idx = rng.nextInt(grassCells.size());
            if (usedTree.contains(idx)) { continue; }
            usedTree.add(idx);
            int[] cell = grassCells.get(idx);
            treeX[ti] = cell[0] * tileSize + tileSize / 2 + rng.nextInt(10) - 5;
            treeY[ti] = cell[1] * tileSize + tileSize / 2 + rng.nextInt(10) - 5;
            treeR[ti] = 14 + rng.nextInt(6);
            ti++;
        }
        // preenche restantes se ficou curto
        for (; ti < nTrees; ti++) { treeX[ti] = -100; treeY[ti] = -100; treeR[ti] = 14; }

        // Arbustos
        int nBush = Math.min(20, grassCells.size() / 3);
        bushX = new int[nBush]; bushY = new int[nBush];
        for (int i = 0; i < nBush && i < grassCells.size(); i++) {
            int idx = rng.nextInt(grassCells.size());
            int[] cell = grassCells.get(idx);
            bushX[i] = cell[0] * tileSize + rng.nextInt(tileSize);
            bushY[i] = cell[1] * tileSize + rng.nextInt(tileSize);
        }

        // Flores
        int nFlowers = Math.min(25, grassCells.size() / 2);
        flowerX = new int[nFlowers]; flowerY = new int[nFlowers]; flowerYellow = new boolean[nFlowers];
        for (int i = 0; i < nFlowers && i < grassCells.size(); i++) {
            int idx = rng.nextInt(grassCells.size());
            int[] cell = grassCells.get(idx);
            flowerX[i] = cell[0] * tileSize + rng.nextInt(tileSize);
            flowerY[i] = cell[1] * tileSize + rng.nextInt(tileSize);
            flowerYellow[i] = rng.nextBoolean();
        }

        decorationsBuilt = true;
    }

    private boolean adjacentToPath(GameMap map, int r, int c, int rows, int cols) {
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d], nc = c + dc[d];
            if (nr < 0 || nc < 0 || nr >= rows || nc >= cols) continue;
            Tile t = map.getTile(nr, nc);
            if (t != null && t.getType() != TileType.GRASS) return true;
        }
        return false;
    }

    private void drawDecorations(Graphics2D g, GameMap map) {
        // Flores (fundo, abaixo das árvores)
        for (int i = 0; i < flowerX.length; i++) {
            g.setColor(flowerYellow[i] ? FLOWER_YELLOW : FLOWER_PINK);
            g.fillOval(flowerX[i] - 2, flowerY[i] - 2, 5, 5);
            g.setColor(Color.WHITE);
            g.fillOval(flowerX[i] - 1, flowerY[i] - 1, 3, 3);
        }

        // Arbustos
        for (int i = 0; i < bushX.length; i++) {
            g.setColor(new Color(BUSH.getRed(), BUSH.getGreen(), BUSH.getBlue(), 180));
            g.fillOval(bushX[i] - 8, bushY[i] - 5, 16, 10);
            g.setColor(new Color(GRASS_DARK.getRed(), GRASS_DARK.getGreen(), GRASS_DARK.getBlue(), 120));
            g.fillOval(bushX[i] - 5, bushY[i] - 4, 12, 8);
        }

        // Árvores grandes (com sombra + duas camadas de copa)
        for (int i = 0; i < treeX.length; i++) {
            int tx = treeX[i], ty = treeY[i], tr = treeR[i];
            if (tx < 0) continue;

            // Sombra
            g.setColor(new Color(0, 0, 0, 40));
            g.fillOval(tx - tr + 4, ty - tr / 2 + 4, tr * 2, tr);

            // Tronco
            g.setColor(TRUNK);
            g.fillRoundRect(tx - 3, ty + tr / 2 - 2, 6, 8, 2, 2);

            // Copa externa (escura)
            g.setColor(TREE_DARK);
            g.fillOval(tx - tr, ty - tr, tr * 2, tr * 2);

            // Copa interna (clara, deslocada)
            g.setColor(TREE_MID);
            g.fillOval(tx - tr + 3, ty - tr + 2, (int)(tr * 1.6), (int)(tr * 1.6));

            // Brilho do topo
            g.setColor(TREE_LIGHT);
            g.fillOval(tx - tr / 2, ty - tr + 2, tr, tr / 2 + 2);
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  UTILIDADE
    // ─────────────────────────────────────────────────────────────

    private void drawPolyline(Graphics2D g, List<Point> pts) {
        for (int i = 0; i < pts.size() - 1; i++) {
            Point a = pts.get(i), b = pts.get(i + 1);
            g.drawLine(a.x, a.y, b.x, b.y);
        }
    }
}