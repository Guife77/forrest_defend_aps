package game.map;

import game.world.GameMap;
import game.world.Tile;
import game.world.enums.TileType;

import java.awt.*;
import java.util.List;
import java.util.Random;


public class MapRenderer {

    // ── Paleta ───────────────────────────────────────────────────
    private static final Color GRASS_A        = new Color(52, 130, 50);
    private static final Color GRASS_B        = new Color(44, 112, 42);
    private static final Color GRASS_C        = new Color(60, 148, 55);

    private static final Color DIRT_DARK      = new Color(80,  52, 18);
    private static final Color WOOD_DARK      = new Color(160, 118, 60);
    private static final Color WOOD_MID       = new Color(190, 148, 85);
    private static final Color WOOD_LITE      = new Color(210, 172, 108);

    private static final Color TREE_VERY_DARK = new Color(18,  62, 18);
    private static final Color TREE_DARK      = new Color(28,  88, 28);
    private static final Color TREE_MID       = new Color(40, 118, 38);
    private static final Color TREE_LITE      = new Color(62, 148, 55);
    private static final Color TREE_BRIGHT    = new Color(88, 175, 70);
    private static final Color TRUNK_DARK     = new Color(72,  44, 18);
    private static final Color TRUNK_MID      = new Color(100, 64, 28);

    private static final Color BUSH_DARK      = new Color(28,  90, 28);
    private static final Color BUSH_MID       = new Color(44, 120, 40);
    private static final Color FERN_COLOR     = new Color(50, 140, 45);

    // ── Estado ───────────────────────────────────────────────────
    private final int ts;
    private boolean built = false;

    // Decorações da grama
    private int[] flX, flY, flType;
    private int[] bsX, bsY, bsR;
    private int[] fnX, fnY;
    private int[] trX, trY, trR;

    public MapRenderer(int tileSize) {
        this.ts = tileSize;
    }

    // ─────────────────────────────────────────────────────────────
    public void render(Graphics2D g, GameMap map) {
        setupHints(g);
        int rows = map.getRows(), cols = map.getCols();
        if (!built) buildDecorations(map, rows, cols);

        drawGrass(g, rows, cols);
        drawFloorDecor(g);
        drawPathShadow(g, map, rows, cols);
        drawPathFill(g, map, rows, cols);
        drawBushesAndFerns(g);
        drawTrees(g);
        drawSpecialTiles(g, map, rows, cols);
    }

    public void renderPath(Graphics2D g, List<Point> path) {
        if (path == null || path.size() < 2) return;
        g.setColor(new Color(220, 190, 100, 60));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1, new float[]{ts * 0.55f, ts * 0.45f}, 0));
        for (int i = 0; i < path.size() - 1; i++) {
            Point a = path.get(i), b = path.get(i + 1);
            g.drawLine(a.x, a.y, b.x, b.y);
        }
        g.setStroke(new BasicStroke(1));
    }

    // ─────────────────────────────────────────────────────────────
    // GRAMA
    // ─────────────────────────────────────────────────────────────
    private void drawGrass(Graphics2D g, int rows, int cols) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int px = c * ts, py = r * ts;
                int pat = (r * 7 + c * 13) % 6;
                Color base = (pat < 3) ? GRASS_A : (pat < 5) ? GRASS_B : GRASS_C;
                g.setColor(base);
                g.fillRect(px, py, ts, ts);

                int edge = Math.min(Math.min(r, rows-1-r), Math.min(c, cols-1-c));
                if (edge == 0) {
                    g.setColor(new Color(0, 0, 0, 90));
                    g.fillRect(px, py, ts, ts);
                } else if (edge == 1) {
                    g.setColor(new Color(0, 0, 0, 35));
                    g.fillRect(px, py, ts, ts);
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // FLORES NA GRAMA
    // ─────────────────────────────────────────────────────────────
    private void drawFloorDecor(Graphics2D g) {
        for (int i = 0; i < flX.length; i++) drawFlower(g, flX[i], flY[i], flType[i]);
    }

    private void drawFlower(Graphics2D g, int x, int y, int type) {
        Color[] colors = {new Color(248, 200, 40), new Color(240, 240, 220), new Color(200, 120, 210)};
        g.setColor(colors[type]);
        for (int a = 0; a < 5; a++) {
            double ang = Math.toRadians(a * 72);
            g.fillOval(x + (int)(Math.cos(ang)*3) - 2, y + (int)(Math.sin(ang)*3) - 2, 4, 4);
        }
        g.setColor(new Color(255, 230, 80));
        g.fillOval(x - 2, y - 2, 4, 4);
    }

    // ─────────────────────────────────────────────────────────────
    // CAMINHO
    // ─────────────────────────────────────────────────────────────
    private void drawPathShadow(Graphics2D g, GameMap map, int rows, int cols) {
        g.setColor(new Color(0, 0, 0, 48));
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                Tile t = map.getTile(r, c);
                if (t != null && t.getType() != TileType.GRASS)
                    g.fillRect(c*ts + 3, r*ts + 3, ts, ts);
            }
    }

    private void drawPathFill(Graphics2D g, GameMap map, int rows, int cols) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile t = map.getTile(r, c);
                if (t == null || t.getType() == TileType.GRASS || t.getType() == TileType.BASE) continue;
                int px = c*ts, py = r*ts;

                g.setColor(DIRT_DARK);
                g.fillRect(px, py, ts, ts);

                g.setColor(WOOD_DARK);
                g.fillRect(px+2, py+2, ts-4, ts-4);

                g.setColor(WOOD_MID);
                g.fillRect(px+3, py+3, ts-6, (ts-6)/2);

                g.setColor(WOOD_LITE);
                g.fillRect(px+4, py+4, ts-8, (ts-8)/3);

                g.setColor(new Color(0, 0, 0, 35));
                g.drawLine(px+3, py+ts/2, px+ts-3, py+ts/2);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ARBUSTOS E SAMAMBAIAS
    // ─────────────────────────────────────────────────────────────
    private void drawBushesAndFerns(Graphics2D g) {
        for (int i = 0; i < bsX.length; i++) drawBush(g, bsX[i], bsY[i], bsR[i]);
        for (int i = 0; i < fnX.length; i++) drawFern(g, fnX[i], fnY[i]);
    }

    private void drawBush(Graphics2D g, int x, int y, int r) {
        g.setColor(new Color(0,0,0,35));
        g.fillOval(x-r+3, y+2, r*2, r/2+2);
        g.setColor(BUSH_DARK);
        g.fillOval(x-r, y-r/2, r*2, r+2);
        g.setColor(BUSH_MID);
        g.fillOval(x-r+2, y-r/2-2, (int)(r*1.4), r);
        g.fillOval(x-r/2-2, y-r, r+4, r);
        g.setColor(new Color(90,160,70,100));
        g.fillOval(x-r/2, y-r+2, r, r/2);
    }

    private void drawFern(Graphics2D g, int x, int y) {
        g.setColor(FERN_COLOR);
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = -2; i <= 2; i++) {
            double ang = Math.toRadians(i*30 - 80);
            int ex = x + (int)(Math.cos(ang)*10);
            int ey = y + (int)(Math.sin(ang)*10);
            g.drawLine(x, y, ex, ey);
            g.fillOval(ex-2, ey-2, 5, 4);
        }
        g.setStroke(new BasicStroke(1));
    }

    // ─────────────────────────────────────────────────────────────
    // ÁRVORES GRANDES
    // ─────────────────────────────────────────────────────────────
    private void drawTrees(Graphics2D g) {
        for (int i = 0; i < trX.length; i++) drawBigTree(g, trX[i], trY[i], trR[i]);
    }

    private void drawBigTree(Graphics2D g, int x, int y, int r) {
        g.setColor(new Color(0,0,0,40));
        g.fillOval(x-r+5, y+r/2, r*2-6, r/2);

        int tw = Math.max(4, r/3), th = r/2;
        g.setColor(TRUNK_DARK);
        g.fillRoundRect(x-tw/2+1, y+r/3+1, tw, th, 3, 3);
        g.setColor(TRUNK_MID);
        g.fillRoundRect(x-tw/2, y+r/3, tw, th, 3, 3);

        int[] offX  = {-r/4,  r/5,   0, -r/6};
        int[] offY  = { r/6,  r/5, -r/5,  0};
        int[] radii = {r, (int)(r*.85), (int)(r*.75), (int)(r*.65)};
        Color[] cols= {TREE_VERY_DARK, TREE_DARK, TREE_MID, TREE_LITE};
        for (int k = 0; k < 4; k++) {
            g.setColor(cols[k]);
            g.fillOval(x+offX[k]-radii[k], y+offY[k]-radii[k], radii[k]*2, radii[k]*2);
        }
        g.setColor(new Color(TREE_BRIGHT.getRed(), TREE_BRIGHT.getGreen(), TREE_BRIGHT.getBlue(), 160));
        g.fillOval(x-r/2, y-r+r/6, (int)(r*.8), (int)(r*.5));
        g.setColor(new Color(150,220,120,65));
        g.fillOval(x-r/3, y-r+r/4, r/3, r/4);
    }

    // ─────────────────────────────────────────────────────────────
    // TILES ESPECIAIS: BASE E ENTRADA
    // ─────────────────────────────────────────────────────────────
    private void drawSpecialTiles(Graphics2D g, GameMap map, int rows, int cols) {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                Tile t = map.getTile(r, c);
                if (t == null) continue;
                int px = c*ts, py = r*ts;
                if (t.getType() == TileType.BASE)              drawBase(g, px+ts/2, py+ts/2);
                if (t.getType() == TileType.PATH && c == 0)    drawEntry(g, px, py);
            }
    }

    private void drawBase(Graphics2D g, int cx, int cy) {
        int r = ts/2 + 4;
        for (int i = 4; i >= 1; i--) {
            g.setColor(new Color(60,200,60,12*i));
            g.fillOval(cx-r-i*4, cy-r-i*4, (r+i*4)*2, (r+i*4)*2);
        }
        g.setColor(new Color(28,95,26));  g.fillOval(cx-r, cy-r, r*2, r*2);
        g.setColor(new Color(42,125,38)); g.fillOval(cx-r+2, cy-r+2, r*2-4, r*2-4);
        g.setColor(new Color(80,220,80,190));
        g.setStroke(new BasicStroke(2.5f));
        g.drawOval(cx-r, cy-r, r*2, r*2);
        g.setStroke(new BasicStroke(1));

        g.setColor(TRUNK_DARK); g.fillRoundRect(cx-4, cy-1, 8, 12, 3, 3);
        g.setColor(TRUNK_MID);  g.fillRoundRect(cx-3, cy-2, 6, 10, 3, 3);
        g.setColor(TREE_VERY_DARK); g.fillOval(cx-13, cy-15, 26, 20);
        g.setColor(TREE_DARK);      g.fillOval(cx-12, cy-17, 24, 18);
        g.setColor(TREE_MID);       g.fillOval(cx- 9, cy-17, 18, 15);
        g.setColor(TREE_LITE);      g.fillOval(cx- 5, cy-15, 10,  9);

        g.setFont(new Font("Arial", Font.BOLD, 9));
        FontMetrics fm = g.getFontMetrics();
        String txt = "BASE";
        int tx = cx - fm.stringWidth(txt)/2;
        g.setColor(new Color(0,0,0,120)); g.drawString(txt, tx+1, cy+r-3+1);
        g.setColor(new Color(180,255,150)); g.drawString(txt, tx, cy+r-3);
    }

    private void drawEntry(Graphics2D g, int px, int py) {
        int cy = py + ts/2;
        int[] xs = {px+2, px+ts/2+2, px+2};
        int[] ys = {cy-7, cy, cy+7};
        g.setColor(new Color(60,200,60,190)); g.fillPolygon(xs, ys, 3);
        g.setColor(new Color(120,255,100));
        g.setStroke(new BasicStroke(1.5f)); g.drawPolygon(xs, ys, 3);
        g.setStroke(new BasicStroke(1));
    }

    // ─────────────────────────────────────────────────────────────
    // PRÉ-CÔMPUTO DE DECORAÇÕES
    // ─────────────────────────────────────────────────────────────
    private void buildDecorations(GameMap map, int rows, int cols) {
        Random rng = new Random(1337);

        java.util.List<int[]> grass  = new java.util.ArrayList<>();
        java.util.List<int[]> border = new java.util.ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile t = map.getTile(r, c);
                if (t == null || t.getType() != TileType.GRASS) continue;
                boolean edge = (r==0||r==rows-1||c==0||c==cols-1);
                if (edge) border.add(new int[]{c, r});
                else      grass.add(new int[]{c, r});
            }
        }

        // Flores
        int nFl = Math.min(55, grass.size()*2);
        flX = new int[nFl]; flY = new int[nFl]; flType = new int[nFl];
        for (int i = 0; i < nFl; i++) {
            int[] cell = grass.get(rng.nextInt(grass.size()));
            flX[i] = cell[0]*ts + 4 + rng.nextInt(ts-8);
            flY[i] = cell[1]*ts + 4 + rng.nextInt(ts-8);
            flType[i] = rng.nextInt(3);
        }

        // Arbustos
        int nBs = Math.min(20, grass.size());
        bsX=new int[nBs]; bsY=new int[nBs]; bsR=new int[nBs];
        for (int i = 0; i < nBs; i++) {
            int[] cell = grass.get(rng.nextInt(grass.size()));
            bsX[i] = cell[0]*ts + ts/2 + rng.nextInt(8)-4;
            bsY[i] = cell[1]*ts + ts/2 + rng.nextInt(8)-4;
            bsR[i] = 8 + rng.nextInt(8);
        }

        // Samambaias
        int nFn = Math.min(16, grass.size());
        fnX=new int[nFn]; fnY=new int[nFn];
        for (int i = 0; i < nFn; i++) {
            int[] cell = grass.get(rng.nextInt(grass.size()));
            fnX[i] = cell[0]*ts + 4 + rng.nextInt(ts-8);
            fnY[i] = cell[1]*ts + 4 + rng.nextInt(ts-8);
        }

        // Árvores: bordas grandes + interior menores
        java.util.Collections.shuffle(border, rng);
        java.util.Collections.shuffle(grass,  rng);
        int nB = Math.min(border.size(), 20), nI = Math.min(6, grass.size()/5);
        trX=new int[nB+nI]; trY=new int[nB+nI]; trR=new int[nB+nI];
        for (int i = 0; i < nB; i++) {
            trX[i]=border.get(i)[0]*ts+ts/2; trY[i]=border.get(i)[1]*ts+ts/2;
            trR[i]=(int)(ts*.72)+rng.nextInt(ts/3);
        }
        for (int i = 0; i < nI; i++) {
            trX[nB+i]=grass.get(i)[0]*ts+ts/2; trY[nB+i]=grass.get(i)[1]*ts+ts/2;
            trR[nB+i]=(int)(ts*.42)+rng.nextInt(ts/4);
        }

        built = true;
    }

    private boolean isGrass(GameMap map, int r, int c, int rows, int cols) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) return true;
        Tile t = map.getTile(r, c);
        return t == null || t.getType() == TileType.GRASS;
    }

    private void setupHints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,      RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }
}