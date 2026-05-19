package game.map;

import game.world.GameMap;
import game.world.Tile;
import game.world.enums.TileType;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Random;

/**
 * Renderizador moderno do mapa:
 *   • Grama com variação procedural em zonas grandes (não mais checker tile-a-tile)
 *   • Caminho como polyline contínua com cantos arredondados (sem bordas de tile visíveis)
 *   • Decoração estratificada: flores → arbustos → árvores
 *   • Base renderizada pela CasaPrincipal
 */
public class MapRenderer {

    // ── Paleta ────────────────────────────────────────────────
    private static final Color GRASS_BASE     = new Color(72, 156, 75);
    private static final Color GRASS_DARK     = new Color(46, 120, 50);
    private static final Color GRASS_LIGHT    = new Color(110, 188, 92);
    private static final Color GRASS_DEEP     = new Color(28,  90,  35);

    private static final Color PATH_OUTER     = new Color(95,  64,  35);
    private static final Color PATH_INNER     = new Color(196, 162, 108);
    private static final Color PATH_HIGHLIGHT = new Color(225, 195, 145);
    private static final Color PATH_SHADOW    = new Color(0, 0, 0, 35);

    private static final Color ENTRY_FILL     = new Color(220,  55,  45);
    private static final Color ENTRY_BORDER   = new Color(130,  20,  20);

    private static final Color TREE_DARK      = new Color(20,  72,  28);
    private static final Color TREE_MID       = new Color(46, 125,  50);
    private static final Color TREE_LIGHT     = new Color(96, 180,  90);
    private static final Color TRUNK          = new Color(73,  48,  35);

    private static final Color BUSH           = new Color(33, 113,  40);

    private static final Color FLOWER_YELLOW  = new Color(249, 198,  47);
    private static final Color FLOWER_PINK    = new Color(233,  90, 140);
    private static final Color FLOWER_WHITE   = new Color(245, 245, 245);
    private static final Color FLOWER_BLUE    = new Color(120, 150, 235);

    // Cache de decorações
    private boolean decorationsBuilt = false;
    private int[] zoneTone;
    private int[] treeX, treeY, treeR, treeKind;
    private int[] bushX, bushY, bushSize;
    private int[] flowerX, flowerY, flowerKind;
    private int[] tuftX, tuftY;
    // Pedras espalhadas pela borda do caminho (cobblestone)
    private int[] stoneX, stoneY, stoneSize;
    private int[] stoneTone;

    private Wildlife wildlife;
    private Waterfall waterfall;
    private final int tileSize;

    public MapRenderer(int tileSize) {
        this.tileSize = tileSize;
    }

    /**
     * Renderiza o mapa completo.
     * @param waypoints centros dos tiles ao longo do caminho — usado para desenhar a estrada contínua.
     */
    public void render(Graphics2D g, GameMap map, List<Point> waypoints) {
        int rows = map.getRows();
        int cols = map.getCols();

        setupHints(g);
        if (!decorationsBuilt) buildDecorations(map, rows, cols, waypoints);
        if (wildlife == null) wildlife = new Wildlife(map, tileSize);
        if (waterfall == null) waterfall = createWaterfall(rows, cols);

        drawGrass(g, rows, cols);
        drawSmoothPath(g, waypoints);
        drawStoneBorders(g);
        drawDecorations(g, map, rows, cols);
        waterfall.render(g);
        wildlife.update();
        drawSpecialTiles(g, map, rows, cols, waypoints);
        wildlife.render(g);
    }

    /** Posiciona a cachoeira na zona aberta entre o caminho superior e o do meio. */
    private Waterfall createWaterfall(int rows, int cols) {
        int cx = 17 * tileSize;          // col 17 (centro do trecho aberto)
        int cyTop = 8 * tileSize;        // logo abaixo do caminho de row 7
        int w = (int)(tileSize * 4.2);
        int h = tileSize * 4;
        return new Waterfall(cx, cyTop, w, h);
    }

    /** Linha tracejada amarela no centro do caminho — indica direção do percurso. */
    public void renderPath(Graphics2D g, List<Point> path) {
        if (path == null || path.size() < 2) return;
        g.setColor(new Color(255, 213, 79, 120));
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1, new float[]{10, 9}, 0));
        drawPolyline(g, path);
        g.setStroke(new BasicStroke(1));
    }

    // ────────────────────────────────────────────────────────
    //  GRAMA
    // ────────────────────────────────────────────────────────

    private void setupHints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,       RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,  RenderingHints.VALUE_STROKE_PURE);
    }

    private void drawGrass(Graphics2D g, int rows, int cols) {
        int w = cols * tileSize;
        int h = rows * tileSize;

        // Fundo verde sólido
        g.setColor(GRASS_BASE);
        g.fillRect(0, 0, w, h);

        // Variação em zonas grandes (4x4 tiles), não em cada tile — visual menos repetitivo
        int zoneSize = tileSize * 4;
        int zoneCols = (cols + 3) / 4;
        int zoneRows = (rows + 3) / 4;
        for (int zr = 0; zr < zoneRows; zr++) {
            for (int zc = 0; zc < zoneCols; zc++) {
                int idx = zr * zoneCols + zc;
                if (idx >= zoneTone.length) break;
                int tone = zoneTone[idx];
                if (tone < 0) continue;
                Color tint;
                switch (tone) {
                    case 0: tint = new Color(GRASS_LIGHT.getRed(), GRASS_LIGHT.getGreen(), GRASS_LIGHT.getBlue(), 55); break;
                    case 1: tint = new Color(GRASS_DARK.getRed(),  GRASS_DARK.getGreen(),  GRASS_DARK.getBlue(),  45); break;
                    default: tint = new Color(GRASS_DEEP.getRed(), GRASS_DEEP.getGreen(), GRASS_DEEP.getBlue(), 30); break;
                }
                g.setPaint(new RadialGradientPaint(
                        zc * zoneSize + zoneSize / 2f,
                        zr * zoneSize + zoneSize / 2f,
                        zoneSize * 0.7f,
                        new float[]{0f, 1f},
                        new Color[]{tint, new Color(tint.getRed(), tint.getGreen(), tint.getBlue(), 0)}
                ));
                g.fillRect(zc * zoneSize, zr * zoneSize, zoneSize, zoneSize);
            }
        }

        // Tufos de grama esparsos
        g.setColor(new Color(GRASS_DEEP.getRed(), GRASS_DEEP.getGreen(), GRASS_DEEP.getBlue(), 160));
        for (int i = 0; i < tuftX.length; i++) {
            if (tuftX[i] < 0) continue;
            int x = tuftX[i], y = tuftY[i];
            g.drawLine(x - 1, y, x - 1, y - 3);
            g.drawLine(x, y, x, y - 4);
            g.drawLine(x + 1, y, x + 1, y - 3);
        }
    }

    // ────────────────────────────────────────────────────────
    //  CAMINHO (POLYLINE SUAVE)
    // ────────────────────────────────────────────────────────

    private void drawSmoothPath(Graphics2D g, List<Point> wps) {
        if (wps == null || wps.size() < 2) return;

        Path2D.Double pathShape = new Path2D.Double();
        pathShape.moveTo(wps.get(0).x, wps.get(0).y);
        for (int i = 1; i < wps.size(); i++) {
            pathShape.lineTo(wps.get(i).x, wps.get(i).y);
        }

        // Sombra projetada do caminho
        Graphics2D gs = (Graphics2D) g.create();
        gs.translate(2, 4);
        gs.setColor(PATH_SHADOW);
        gs.setStroke(new BasicStroke(tileSize + 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        gs.draw(pathShape);
        gs.dispose();

        // Borda externa (escura)
        g.setColor(PATH_OUTER);
        g.setStroke(new BasicStroke(tileSize + 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(pathShape);

        // Miolo (areia)
        g.setColor(PATH_INNER);
        g.setStroke(new BasicStroke(tileSize - 6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(pathShape);

        // Realce superior do caminho (iluminação)
        Graphics2D gh = (Graphics2D) g.create();
        gh.translate(0, -1);
        gh.setColor(new Color(PATH_HIGHLIGHT.getRed(), PATH_HIGHLIGHT.getGreen(), PATH_HIGHLIGHT.getBlue(), 100));
        gh.setStroke(new BasicStroke(tileSize - 10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        gh.draw(pathShape);
        gh.dispose();

        g.setStroke(new BasicStroke(1));
    }

    private void drawSpecialTiles(Graphics2D g, GameMap map, int rows, int cols, List<Point> wps) {
        // Fortaleza de madeira no tile BASE
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile t = map.getTile(r, c);
                if (t != null && t.getType() == TileType.BASE) {
                    int cx = c * tileSize + tileSize / 2;
                    int cy = r * tileSize + tileSize / 2;
                    WoodenFortress.render(g, cx, cy, tileSize * 4);
                }
            }
        }

        // Marcador de entrada — no primeiro waypoint
        if (wps != null && !wps.isEmpty()) {
            Point entry = wps.get(0);
            drawEntryMarker(g, entry.x, entry.y);
        }
    }

    private void drawEntryMarker(Graphics2D g, int cx, int cy) {
        long t = System.currentTimeMillis();
        float pulse = 0.7f + 0.3f * (float) Math.sin(t * 0.005);
        int r = (int) (10 * pulse) + 2;

        // Halo pulsante
        g.setColor(new Color(255, 80, 60, 70));
        g.fillOval(cx - r - 5, cy - r - 5, (r + 5) * 2, (r + 5) * 2);

        g.setColor(ENTRY_FILL);
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(ENTRY_BORDER);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(cx - r, cy - r, r * 2, r * 2);

        // Seta →
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(cx - 4, cy, cx + 4, cy);
        g.drawLine(cx + 1, cy - 3, cx + 4, cy);
        g.drawLine(cx + 1, cy + 3, cx + 4, cy);
        g.setStroke(new BasicStroke(1));
    }

    // ────────────────────────────────────────────────────────
    //  DECORAÇÕES
    // ────────────────────────────────────────────────────────

    private void buildDecorations(GameMap map, int rows, int cols, List<Point> waypoints) {
        Random rng = new Random(11);
        buildStoneBorders(waypoints, rng);

        // Tons das zonas (4x4 tiles)
        int zoneCount = ((cols + 3) / 4) * ((rows + 3) / 4);
        zoneTone = new int[zoneCount];
        for (int i = 0; i < zoneCount; i++) {
            zoneTone[i] = rng.nextInt(5) < 3 ? rng.nextInt(3) : -1;
        }

        // Tiles de grama longe do caminho
        java.util.List<int[]> grassCells = new java.util.ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile t = map.getTile(r, c);
                if (t != null && t.getType() == TileType.GRASS && !adjacentToPath(map, r, c, rows, cols)) {
                    grassCells.add(new int[]{c, r});
                }
            }
        }

        // Tufos: mais densos perto das bordas, esparsos no centro
        int nTufts = (rows * cols) / 9;
        tuftX = new int[nTufts]; tuftY = new int[nTufts];
        int ki = 0;
        int safety = 0;
        while (ki < nTufts && safety++ < 2000 && !grassCells.isEmpty()) {
            int[] cell = grassCells.get(rng.nextInt(grassCells.size()));
            tuftX[ki] = cell[0] * tileSize + rng.nextInt(tileSize);
            tuftY[ki] = cell[1] * tileSize + rng.nextInt(tileSize);
            ki++;
        }
        for (; ki < nTufts; ki++) { tuftX[ki] = -100; tuftY[ki] = -100; }

        // Árvores — quantidade menor, mais espaçadas
        int nTrees = Math.min(28, grassCells.size() / 5);
        treeX = new int[nTrees]; treeY = new int[nTrees]; treeR = new int[nTrees]; treeKind = new int[nTrees];
        java.util.Set<Long> used = new java.util.HashSet<>();
        int ti = 0;
        int s2 = 0;
        while (ti < nTrees && s2++ < 2000) {
            int[] cell = grassCells.get(rng.nextInt(grassCells.size()));
            long key = (long) cell[0] * 1000 + cell[1];
            // Espaçamento mínimo: rejeita se já existe árvore num raio próximo
            boolean tooClose = false;
            for (long k : used) {
                int kc = (int) (k / 1000), kr = (int) (k % 1000);
                if (Math.abs(kc - cell[0]) < 2 && Math.abs(kr - cell[1]) < 2) { tooClose = true; break; }
            }
            if (tooClose) continue;
            used.add(key);
            treeX[ti] = cell[0] * tileSize + tileSize / 2 + rng.nextInt(10) - 5;
            treeY[ti] = cell[1] * tileSize + tileSize / 2 + rng.nextInt(10) - 5;
            treeR[ti] = 14 + rng.nextInt(8);
            treeKind[ti] = rng.nextInt(2);
            ti++;
        }
        for (; ti < nTrees; ti++) { treeX[ti] = -100; treeY[ti] = -100; treeR[ti] = 14; }

        // Arbustos — quantidade moderada
        int nBush = Math.min(35, grassCells.size() / 3);
        bushX = new int[nBush]; bushY = new int[nBush]; bushSize = new int[nBush];
        for (int i = 0; i < nBush && i < grassCells.size(); i++) {
            int[] cell = grassCells.get(rng.nextInt(grassCells.size()));
            bushX[i] = cell[0] * tileSize + rng.nextInt(tileSize);
            bushY[i] = cell[1] * tileSize + rng.nextInt(tileSize);
            bushSize[i] = 7 + rng.nextInt(5);
        }

        // Flores
        int nFlowers = Math.min(60, grassCells.size());
        flowerX = new int[nFlowers]; flowerY = new int[nFlowers]; flowerKind = new int[nFlowers];
        for (int i = 0; i < nFlowers && i < grassCells.size(); i++) {
            int[] cell = grassCells.get(rng.nextInt(grassCells.size()));
            flowerX[i] = cell[0] * tileSize + rng.nextInt(tileSize);
            flowerY[i] = cell[1] * tileSize + rng.nextInt(tileSize);
            flowerKind[i] = rng.nextInt(4);
        }

        decorationsBuilt = true;
    }

    private boolean adjacentToPath(GameMap map, int r, int c, int rows, int cols) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = r + dr, nc = c + dc;
                if (nr < 0 || nc < 0 || nr >= rows || nc >= cols) continue;
                Tile t = map.getTile(nr, nc);
                if (t != null && t.getType() != TileType.GRASS) return true;
            }
        }
        return false;
    }

    private void drawDecorations(Graphics2D g, GameMap map, int rows, int cols) {
        // Flores
        for (int i = 0; i < flowerX.length; i++) {
            if (flowerX[i] < 0) continue;
            Color c;
            switch (flowerKind[i]) {
                case 0:  c = FLOWER_YELLOW; break;
                case 1:  c = FLOWER_PINK;   break;
                case 2:  c = FLOWER_WHITE;  break;
                default: c = FLOWER_BLUE;   break;
            }
            int fx = flowerX[i], fy = flowerY[i];
            g.setColor(c);
            g.fillOval(fx - 3, fy - 1, 3, 3);
            g.fillOval(fx + 1, fy - 1, 3, 3);
            g.fillOval(fx - 1, fy - 3, 3, 3);
            g.fillOval(fx - 1, fy + 1, 3, 3);
            g.setColor(FLOWER_YELLOW);
            g.fillOval(fx - 1, fy - 1, 2, 2);
        }

        // Arbustos
        for (int i = 0; i < bushX.length; i++) {
            int s = bushSize[i];
            g.setColor(new Color(0, 0, 0, 45));
            g.fillOval(bushX[i] - s + 1, bushY[i] - s/2 + 2, s * 2, s);
            g.setColor(new Color(BUSH.getRed(), BUSH.getGreen(), BUSH.getBlue(), 220));
            g.fillOval(bushX[i] - s, bushY[i] - s/2, s * 2, s);
            g.setColor(new Color(GRASS_LIGHT.getRed(), GRASS_LIGHT.getGreen(), GRASS_LIGHT.getBlue(), 160));
            g.fillOval(bushX[i] - s + 2, bushY[i] - s/2, s + 2, s - 2);
        }

        // Árvores
        for (int i = 0; i < treeX.length; i++) {
            int tx = treeX[i], ty = treeY[i], tr = treeR[i];
            if (tx < 0) continue;
            if (treeKind[i] == 0) drawRoundTree(g, tx, ty, tr);
            else                  drawPineTree(g, tx, ty, tr);
        }
    }

    private void drawRoundTree(Graphics2D g, int tx, int ty, int tr) {
        g.setColor(new Color(0, 0, 0, 65));
        g.fillOval(tx - tr + 5, ty - tr / 2 + 6, tr * 2, tr);

        g.setColor(TRUNK);
        g.fillRoundRect(tx - 3, ty + tr / 2 - 2, 7, 10, 3, 3);

        g.setColor(TREE_DARK);
        g.fillOval(tx - tr, ty - tr, tr * 2, tr * 2);
        g.setColor(TREE_MID);
        g.fillOval(tx - tr + 3, ty - tr + 2, (int)(tr * 1.55), (int)(tr * 1.55));
        g.setColor(TREE_LIGHT);
        g.fillOval(tx - tr / 2, ty - tr + 2, tr, tr / 2 + 2);
    }

    private void drawPineTree(Graphics2D g, int tx, int ty, int tr) {
        g.setColor(new Color(0, 0, 0, 65));
        g.fillOval(tx - tr + 4, ty + tr / 2 - 2, tr * 2, tr);
        g.setColor(TRUNK);
        g.fillRect(tx - 2, ty + tr / 2, 4, 9);
        for (int i = 0; i < 3; i++) {
            int h = tr - i * 4;
            int w = tr + 5 - i * 2;
            int yOff = ty + tr / 2 - i * (tr / 2);
            int[] xs = { tx - w, tx, tx + w };
            int[] ys = { yOff, yOff - h, yOff };
            g.setColor(i == 2 ? TREE_LIGHT : (i == 1 ? TREE_MID : TREE_DARK));
            g.fillPolygon(xs, ys, 3);
        }
    }

    private void drawPolyline(Graphics2D g, List<Point> pts) {
        for (int i = 0; i < pts.size() - 1; i++) {
            Point a = pts.get(i), b = pts.get(i + 1);
            g.drawLine(a.x, a.y, b.x, b.y);
        }
    }

    // ────────────────────────────────────────────────────────
    //  PEDRAS DA BORDA DO CAMINHO (cobblestone)
    // ────────────────────────────────────────────────────────

    /** Gera posições de pedras dos dois lados do caminho. */
    private void buildStoneBorders(List<Point> waypoints, Random rng) {
        if (waypoints == null || waypoints.size() < 2) {
            stoneX = new int[0]; stoneY = new int[0]; stoneSize = new int[0]; stoneTone = new int[0];
            return;
        }
        List<int[]> stones = new java.util.ArrayList<>();
        float halfWidth = tileSize / 2f + 1;
        float stride = 9f; // espaçamento entre pedras ao longo do caminho

        for (int i = 0; i < waypoints.size() - 1; i++) {
            Point a = waypoints.get(i);
            Point b = waypoints.get(i + 1);
            float dx = b.x - a.x, dy = b.y - a.y;
            float len = (float) Math.hypot(dx, dy);
            if (len < 0.001f) continue;
            float nx = -dy / len, ny = dx / len; // perpendicular unitário
            int n = Math.max(1, (int)(len / stride));
            for (int s = 0; s < n; s++) {
                float t = s / (float) n;
                float px = a.x + dx * t, py = a.y + dy * t;
                // Lado A
                float jitterA = (rng.nextFloat() - 0.5f) * 3f;
                int sxA = (int)(px + nx * (halfWidth + jitterA));
                int syA = (int)(py + ny * (halfWidth + jitterA));
                stones.add(new int[]{ sxA, syA, 4 + rng.nextInt(3), rng.nextInt(3) });
                // Lado B
                float jitterB = (rng.nextFloat() - 0.5f) * 3f;
                int sxB = (int)(px - nx * (halfWidth + jitterB));
                int syB = (int)(py - ny * (halfWidth + jitterB));
                stones.add(new int[]{ sxB, syB, 4 + rng.nextInt(3), rng.nextInt(3) });
            }
        }
        int n = stones.size();
        stoneX = new int[n]; stoneY = new int[n]; stoneSize = new int[n]; stoneTone = new int[n];
        for (int i = 0; i < n; i++) {
            int[] s = stones.get(i);
            stoneX[i] = s[0]; stoneY[i] = s[1]; stoneSize[i] = s[2]; stoneTone[i] = s[3];
        }
    }

    /** Desenha as pedras com 3 variações de cor. */
    private void drawStoneBorders(Graphics2D g) {
        Color[] palette = {
                new Color(170, 170, 165),
                new Color(140, 140, 135),
                new Color(115, 115, 110)
        };
        for (int i = 0; i < stoneX.length; i++) {
            int sx = stoneX[i], sy = stoneY[i], sz = stoneSize[i];
            // Sombra
            g.setColor(new Color(0, 0, 0, 70));
            g.fillOval(sx - sz + 1, sy - sz / 2 + 1, sz * 2, sz);
            // Pedra
            g.setColor(palette[stoneTone[i]]);
            g.fillOval(sx - sz, sy - sz / 2, sz * 2, sz);
            // Realce superior
            g.setColor(new Color(255, 255, 255, 60));
            g.fillOval(sx - sz + 1, sy - sz / 2, sz - 1, sz / 2);
        }
    }
}
