package game.world;

import game.world.enums.TileType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapa estilo Bloons TD6:
 * Entrada pela esquerda (meio da tela), S-duplo horizontal, desce até a base.
 *
 * Grade: 25 colunas × 16 linhas  (800px / 32 = 25, (600-58)/32 ≈ 16)
 *
 * Layout visual (. = grass, # = path, B = base):
 *
 *  row  0: . . . . . . . . . . . . . . . . . . . . . . . . .
 *  row  1: . . . . . . . . . . . . . . . . . . . . . . . . .
 *  row  2: # # # # # # # # # # # . . . . . . . . . . . . . .   ← ENTRADA col 0
 *  row  3: . . . . . . . . . . # . . . . . . . . . . . . . .
 *  row  4: . . . . . . . . . . # # # # # # # # # # # . . . .
 *  row  5: . . . . . . . . . . . . . . . . . . . . # . . . .
 *  row  6: . . . . # # # # # # # # # # # # # # # # # . . . .
 *  row  7: . . . . # . . . . . . . . . . . . . . . . . . . .
 *  row  8: . . . . # # # # # # # # # # # # . . . . . . . . .
 *  row  9: . . . . . . . . . . . . . . . # . . . . . . . . .
 *  row 10: . . . . . . . . . . . . . . . # . . . . . . . . .
 *  row 11: . . . . . . . . . . . . . . . # # # # # # # # # .
 *  row 12: . . . . . . . . . . . . . . . . . . . . . . . . .   (col 24 = borda)
 *  row 13: . B # # # # # # # # # # # # # # # # # # # # # # .  ← BASE col 1
 *  row 14: . . . . . . . . . . . . . . . . . . . . . . . . .
 *  row 15: . . . . . . . . . . . . . . . . . . . . . . . . .
 */
public class MapLoader {

    private static final int COLS = 25;
    private static final int ROWS = 16;

    private static final int[][] DESIGN = {
            // 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
            {  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // row  0
            {  0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // row  1
            {  0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // row  2 ← ENTRADA
            {  0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // row  3
            {  1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0 }, // row  4
            {  0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0 }, // row  5
            {  0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0 }, // row  6
            {  0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0 }, // row  7
            {  0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }, // row  8
            {  0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }, // row  9
            {  0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0 }, // row 10
            {  0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // row 11
            {  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // row 12
            {  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // row 13 ← BASE col 1
            {  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // row 14
            {  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // row 15
    };

    /**
     * Waypoints em {col, row} — ordem exata de percurso dos inimigos.
     * Entrada col 0 row 2 → ... → BASE col 1 row 13.
     */
    private static final int[][] WAYPOINTS = {

            {0,4}, {1,4}, {2,4}, {3,4}, {4,4}, {5,4}, {6,4}, {7,4}, {8,4}, {9,4}, {10,4}, {11,4}, {12,4},

            // Sobe a primeira alça
            {12,3}, {12,2}, {12,1},

            // Volta pela esquerda na alça superior
            {11,1}, {10,1}, {9,1}, {8,1},

            // Desce cruzando os caminhos em (8,4) e (8,7) até a curva inferior
            {8,2}, {8,3}, {8,4}, {8,5}, {8,6}, {8,7}, {8,8}, {8,9}, {8,10}, {8,11},

            // Esquerda na alça inferior
            {7,11}, {6,11}, {5,11}, {4,11},

            // Sobe na alça inferior
            {4,10}, {4,9}, {4,8}, {4,7},

            // Vai para a direita até a terceira alça (passando reto pelo cruzamento em 8,7)
            {5,7}, {6,7}, {7,7}, {8,7}, {9,7}, {10,7}, {11,7}, {12,7}, {13,7}, {14,7}, {15,7}, {16,7},

            // Sobe na terceira alça
            {16,6}, {16,5}, {16,4},

            // Vai para a direita
            {17,4}, {18,4}, {19,4},

            // Desce na extremidade direita
            {19,5}, {19,6}, {19,7}, {19,8}, {19,9}, {19,10},

            // Retorna para a esquerda até o corredor da base
            {18,10}, {17,10}, {16,10}, {15,10}, {14,10}, {13,10}, {12,10},

            // Desce até a BASE (valor 2)
            {12,11}, {12,12}, {12,13}, {12,14}, {12,15}
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