package game.world;

import game.world.enums.TileType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Define o mapa E a ordem exata dos waypoints.
 *
 * Separar layout (quais tiles são PATH) de rota (ordem que os inimigos percorrem)
 * evita que o BFS pegue atalhos indesejados.
 *
 * Layout (. grass, # path, B base):
 *   row 1:  ####  (cols 0-3)
 *   row 2:     #  (col 3)
 *   row 3:     ################ (cols 3-16)
 *   row 4:                   #  (col 16)
 *   row 5:                   #  (col 16)
 *   row 6:  ###############  #  (cols 3-16)
 *   row 7:  #                   (col 3)
 *   row 8:  #                   (col 3)
 *   row 9:  #############       (cols 3-15)
 *   row 10:              #      (col 15)
 *   row 11:              #      (col 15)
 *   row 12: B#############      (cols 1-15, BASE=col 1)
 */
public class MapLoader {

    private static final int[][] DESIGN = {
            // 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19
            {  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },  // row  0
            {  1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },  // row  1 ← ENTRADA
            {  0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },  // row  2
            {  0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0 },  // row  3
            {  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },  // row  4
            {  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },  // row  5
            {  0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0 },  // row  6
            {  0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },  // row  7
            {  0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },  // row  8
            {  0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0 },  // row  9
            {  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },  // row 10
            {  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },  // row 11
            {  0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0 },  // row 12 ← BASE col 1
            {  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },  // row 13
            {  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },  // row 14
    };

    /**
     * Waypoints na ordem exata de percurso: (col, row).
     * Inimigos seguem essa sequência — sem BFS, sem atalho.
     */
    private static final int[][] WAYPOINTS = {
            // entrada
            {0,1},{1,1},{2,1},{3,1},
            // desce
            {3,2},
            // vai pra direita
            {3,3},{4,3},{5,3},{6,3},{7,3},{8,3},{9,3},{10,3},{11,3},{12,3},{13,3},{14,3},{15,3},{16,3},
            // desce
            {16,4},{16,5},
            // volta pra esquerda
            {16,6},{15,6},{14,6},{13,6},{12,6},{11,6},{10,6},{9,6},{8,6},{7,6},{6,6},{5,6},{4,6},{3,6},
            // desce
            {3,7},{3,8},
            // vai pra direita
            {3,9},{4,9},{5,9},{6,9},{7,9},{8,9},{9,9},{10,9},{11,9},{12,9},{13,9},{14,9},{15,9},
            // desce
            {15,10},{15,11},
            // volta pra esquerda até a base
            {15,12},{14,12},{13,12},{12,12},{11,12},{10,12},{9,12},{8,12},{7,12},{6,12},{5,12},{4,12},{3,12},{2,12},
            // BASE
            {1,12},
    };

    public void load(GameMap map) {
        int rows = Math.min(DESIGN.length, map.getRows());
        int cols = Math.min(DESIGN[0].length, map.getCols());

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                TileType type;
                switch (DESIGN[r][c]) {
                    case 1:  type = TileType.PATH;  break;
                    case 2:  type = TileType.BASE;  break;
                    default: type = TileType.GRASS; break;
                }
                map.setTile(r, c, new Tile(c, r, type));
            }
        }
    }

    /**
     * Retorna os waypoints em pixels (centro de cada tile).
     * Use isso em vez do PathFinder.findPath para o caminho principal.
     */
    public static List<Point> getWaypoints(int tileSize) {
        List<Point> pts = new ArrayList<>();
        for (int[] wp : WAYPOINTS) {
            pts.add(new Point(
                    wp[0] * tileSize + tileSize / 2,
                    wp[1] * tileSize + tileSize / 2
            ));
        }
        return pts;
    }
}