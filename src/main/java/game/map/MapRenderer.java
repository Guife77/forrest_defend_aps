package game.map;

import game.world.GameMap;
import game.world.Tile;
import game.world.enums.TileType;

import java.awt.*;
import java.util.List;

public class MapRenderer {

    private static final Color GRASS = new Color(56, 120, 56);
    private static final Color PATH  = new Color(185, 150, 100);
    private static final Color BASE  = new Color(60, 100, 200);
    private static final Color GRID  = new Color(0, 0, 0, 25);

    private final int tileSize;

    public MapRenderer(int tileSize) {
        this.tileSize = tileSize;
    }

    public void render(Graphics2D g, GameMap map) {
        int rows = map.getRows();
        int cols = map.getCols();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile tile = map.getTile(r, c);
                int px = c * tileSize;
                int py = r * tileSize;

                Color fill = GRASS;
                if (tile != null) {
                    switch (tile.getType()) {
                        case PATH:  fill = PATH; break;
                        case BASE:  fill = BASE; break;
                        default:    fill = GRASS; break;
                    }
                }

                g.setColor(fill);
                g.fillRect(px, py, tileSize, tileSize);

                g.setColor(GRID);
                g.drawRect(px, py, tileSize, tileSize);

                // Destaque visual da BASE
                if (tile != null && tile.getType() == TileType.BASE) {
                    g.setColor(new Color(255, 255, 100, 180));
                    g.setStroke(new BasicStroke(2));
                    g.drawRect(px + 1, py + 1, tileSize - 2, tileSize - 2);
                    g.setStroke(new BasicStroke(1));
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 9));
                    g.drawString("BASE", px + 2, py + tileSize / 2 + 3);
                }
            }
        }
    }

    /** Linha pontilhada mostrando o caminho dos inimigos */
    public void renderPath(Graphics2D g, List<Point> path) {
        if (path == null || path.size() < 2) return;
        g.setColor(new Color(255, 220, 50, 60));
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1, new float[]{5, 5}, 0));
        for (int i = 0; i < path.size() - 1; i++) {
            Point a = path.get(i), b = path.get(i + 1);
            g.drawLine(a.x, a.y, b.x, b.y);
        }
        g.setStroke(new BasicStroke(1));
    }
}