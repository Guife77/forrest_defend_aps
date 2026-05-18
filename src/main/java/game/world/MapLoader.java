package game.world;

import game.world.enums.TileType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapa 40×20, S-shape com 3 voltas grandes, terminando no CENTRO-DIREITA onde fica a Casa Principal.
 *
 * Layout do caminho (linhas/colunas):
 *   • Entrada: col 0, row 2 (esquerda alta)
 *   • Vai para a direita até col 36
 *   • Desce até row 7
 *   • Volta para a esquerda até col 3
 *   • Desce até row 12
 *   • Vai para a direita até col 32
 *   • Desce até row 16
 *   • Vai para a esquerda até col 22  ← BASE/Casa fica aqui
 *
 * 0 = grama, 1 = caminho, 2 = base
 */
public class MapLoader {

    private static final int COLS = 40;
    private static final int ROWS = 20;

    private static final int[][] CORNERS = {
            { 0,  2},   // entrada esquerda
            {36,  2},   // canto NE
            {36,  7},
            { 3,  7},
            { 3, 12},
            {32, 12},
            {32, 16},
            {22, 16},   // BASE
    };

    public void load(GameMap map) {
        int rows = Math.min(ROWS, map.getRows());
        int cols = Math.min(COLS, map.getCols());

        // Inicializa todo o mapa como grama
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                map.setTile(r, c, new Tile(c, r, TileType.GRASS));
            }
        }

        // Marca tiles do caminho derivando dos cantos (linhas e colunas exclusivas)
        for (int i = 0; i < CORNERS.length - 1; i++) {
            int[] a = CORNERS[i];
            int[] b = CORNERS[i + 1];
            int dc = Integer.signum(b[0] - a[0]);
            int dr = Integer.signum(b[1] - a[1]);
            int steps = Math.max(Math.abs(b[0] - a[0]), Math.abs(b[1] - a[1]));
            for (int s = 0; s <= steps; s++) {
                int col = a[0] + dc * s;
                int row = a[1] + dr * s;
                if (row >= 0 && row < rows && col >= 0 && col < cols) {
                    map.setTile(row, col, new Tile(col, row, TileType.PATH));
                }
            }
        }

        // Última posição vira BASE
        int[] last = CORNERS[CORNERS.length - 1];
        map.setTile(last[1], last[0], new Tile(last[0], last[1], TileType.BASE));
    }

    /** Waypoints densos (1 por tile) para movimento e renderização do caminho. */
    public static List<Point> getWaypoints(int tileSize) {
        List<Point> pts = new ArrayList<>();
        for (int i = 0; i < CORNERS.length - 1; i++) {
            int[] a = CORNERS[i];
            int[] b = CORNERS[i + 1];
            int dx = Integer.signum(b[0] - a[0]);
            int dy = Integer.signum(b[1] - a[1]);
            int steps = Math.max(Math.abs(b[0] - a[0]), Math.abs(b[1] - a[1]));
            for (int s = 0; s < steps; s++) {
                int col = a[0] + dx * s;
                int row = a[1] + dy * s;
                pts.add(new Point(col * tileSize + tileSize / 2,
                                  row * tileSize + tileSize / 2));
            }
        }
        int[] last = CORNERS[CORNERS.length - 1];
        pts.add(new Point(last[0] * tileSize + tileSize / 2,
                          last[1] * tileSize + tileSize / 2));
        return pts;
    }
}
