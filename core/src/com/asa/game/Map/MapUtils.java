package com.asa.game.Map;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapUtils {
    public static List<Polygon> getWallPolygons (CaveMap map) {
        List<Polygon> wallPolys = new ArrayList<>();
        List<Line> wallLines = getWallLines(map.getWalls());


        return wallPolys;
    }

    private static List<Line> getWallLines(boolean[][] walls) {
        List<Line> lines = new ArrayList<>();

        for(int y = 0; y < walls.length; y++) {
            for(int x = 0; x < walls[y].length; y++) {

            }
        }

        return lines;
    }

    public class Line {
        public final Vector2 start;
        public final Vector2 end;

        public Line(Vector2 start, Vector2 end) {
            this.start = start;
            this.end = end;
        }
    }
}
