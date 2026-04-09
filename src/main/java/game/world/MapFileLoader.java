package game.world;

import game.world.enums.TileType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFileLoader {

    public GameMap load(String filePath) throws IOException {
        List<int[]> lines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.trim().split(" ");
            int[] row = new int[parts.length];

            for (int i = 0; i < parts.length; i++) {
                row[i] = Integer.parseInt(parts[i]);
            }

            lines.add(row);
        }

        reader.close();

        int rows = lines.size();
        int cols = lines.get(0).length;
        GameMap map = new GameMap(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                TileType type;
                switch (lines.get(i)[j]) {
                    case 1: type = TileType.PATH; break;
                    case 2: type = TileType.BASE; break;
                    default: type = TileType.GRASS; break;
                }
                map.setTile(i, j, new Tile(j, i, type));
            }
        }

        return map;
    }
}