package game.map;

import game.world.GameMap;
import game.world.Tile;
import game.world.enums.TileType;

import java.awt.Point;
import java.util.*;

/**
 * BFS da entrada até a BASE.
 * Suporta tiles bloqueados por barreiras — inimigos desviam por GRASS adjacente ao PATH.
 */
public class PathFinder {

    public static List<Point> findPath(GameMap map, int tileSize) {
        return findPath(map, tileSize, Collections.emptySet());
    }

    /**
     * @param blockedTiles conjunto de Point(col, row) bloqueados por barreiras
     * @return waypoints em pixels, ou vazio se não houver caminho possível
     */
    public static List<Point> findPath(GameMap map, int tileSize, Set<Point> blockedTiles) {
        int rows = map.getRows();
        int cols = map.getCols();

        Point start = null, end = null;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile tile = map.getTile(r, c);
                if (tile == null) continue;
                if (tile.getType() == TileType.PATH && c == 0 && start == null)
                    start = new Point(c, r);
                if (tile.getType() == TileType.BASE)
                    end = new Point(c, r);
            }
        }

        if (start == null || end == null) return Collections.emptyList();

        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> parent = new LinkedHashMap<>();
        queue.add(start);
        parent.put(start, null);

        int[] dc = {1, -1, 0, 0};
        int[] dr = {0, 0, 1, -1};

        while (!queue.isEmpty()) {
            Point cur = queue.poll();
            if (cur.equals(end)) break;

            for (int d = 0; d < 4; d++) {
                int nc = cur.x + dc[d];
                int nr = cur.y + dr[d];
                if (nc < 0 || nr < 0 || nc >= cols || nr >= rows) continue;

                Point next = new Point(nc, nr);
                if (parent.containsKey(next)) continue;
                if (blockedTiles.contains(next)) continue;

                Tile tile = map.getTile(nr, nc);
                if (tile == null) continue;
                TileType type = tile.getType();

                boolean walkable = type == TileType.PATH
                        || type == TileType.BASE
                        || (type == TileType.GRASS && isAdjacentToPath(map, nr, nc, blockedTiles));

                if (walkable) {
                    parent.put(next, cur);
                    queue.add(next);
                }
            }
        }

        if (!parent.containsKey(end)) return Collections.emptyList();

        List<Point> path = new ArrayList<>();
        Point cur = end;
        while (cur != null) {
            path.add(0, new Point(
                    cur.x * tileSize + tileSize / 2,
                    cur.y * tileSize + tileSize / 2
            ));
            cur = parent.get(cur);
        }

        return path;
    }

    private static boolean isAdjacentToPath(GameMap map, int row, int col, Set<Point> blocked) {
        int[] dc = {1, -1, 0, 0};
        int[] dr = {0, 0, 1, -1};
        for (int d = 0; d < 4; d++) {
            int nr = row + dr[d], nc = col + dc[d];
            if (nr < 0 || nc < 0 || nr >= map.getRows() || nc >= map.getCols()) continue;
            if (blocked.contains(new Point(nc, nr))) continue;
            Tile t = map.getTile(nr, nc);
            if (t != null && t.getType() == TileType.PATH) return true;
        }
        return false;
    }
}