package com.asa.game.Map;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapWallGenerator {

    // why do inner classes always go at the bottom? nice to know what they are before they are used...
    public static class Line {
        public Vector2 start;
        public Vector2 end;

        public Line(Vector2 start, Vector2 end) {
            this.start = start;
            this.end = end;
        }
    }

    private static class MapCell {
        // all arrays are indexed N,S,E,W
        private boolean[] hasNeighbor;
        private Line[] edges;

        // since we scan from bottom left, these have already been processed
        private MapCell southernNeighbor;
        private MapCell westernNeighbor;

        public MapCell() {
            hasNeighbor = new boolean[4];
            edges = new Line[4];
        }

        private int getIndexFromDir(char dir) {
            if (dir == 'N') return 0;
            if (dir == 'S') return 1;
            if (dir == 'E') return 2;
            if (dir == 'W') return 3;
            return -1; // what's an exception??
        }

        public void setHasNeighbor(char dir) {
            hasNeighbor[getIndexFromDir(dir)] = true;
        }

        public void setEdge(char dir, Line edge) {
            edges[getIndexFromDir(dir)] = edge;
        }

        public Line getEdge(char dir) {
            return edges[getIndexFromDir(dir)];
        }

        public MapCell getSouthernNeighbor() {
            return southernNeighbor;
        }

        public MapCell getWesternNeighbor() {
            return westernNeighbor;
        }
    }

    // algorithm based on this: https://www.youtube.com/watch?v=fc3nnG2CG8U
    // key point to remember: each cell only has knowledge of it's southern and western neighbors,
    //                        we scan from bottom left, so these have already been processed.
    public static List<Line> getWallLines(CaveMap map) {
        List<Line> lines = new ArrayList<>();
        MapCell[][] mapCells = new MapCell[map.getSize()][map.getSize()];

        for(int y = 0; y < map.getSize(); y++) {
            for(int x = 0; x < map.getSize(); x++) {
                if(map.isWall(x, y)) {
                    MapCell cell = new MapCell();
                    mapCells[y][x] = cell;

                    // todo: fix repetitions

                    // if northern tile is in bounds and not a wall we need a northern edge
                    if(map.isInBounds(x, y + 1) && !map.isWall(x, y + 1)) {
                        // extend northern edge of western neighbor if it exists
                        if(map.isWall(x - 1, y) && mapCells[y][x - 1].getEdge('N') != null) {
                            mapCells[y][x - 1].getEdge('N').end = map.getPosTopR(x, y);
                            cell.setEdge('N', mapCells[y][x - 1].getEdge('N'));
                        }
                        // otherwise, create a new northern edge
                        else {
                            Line edge = new Line(map.getPosTopL(x, y), map.getPosTopR(x, y));
                            lines.add(edge);
                            cell.setEdge('N', edge);
                        }
                    }

                    // if southern tile is in bounds and not a wall we need a southern edge
                    if(map.isInBounds(x, y - 1) && !map.isWall(x, y - 1)) {
                        // extend southern edge of western neighbor if it exists
                        if(map.isWall(x - 1, y) && mapCells[y][x - 1].getEdge('S') != null) {
                            mapCells[y][x - 1].getEdge('S').end = map.getPosBotR(x, y);
                            cell.setEdge('S', mapCells[y][x - 1].getEdge('S'));
                        }
                        // otherwise, create a new southern edge
                        else {
                            Line edge = new Line(map.getPosBotL(x, y), map.getPosBotR(x, y));
                            lines.add(edge);
                            cell.setEdge('S', edge);
                        }
                    }

                    // if eastern tile is in bounds and not a wall we need an eastern edge
                    if(map.isInBounds(x + 1, y) && !map.isWall(x + 1, y)) {
                        // extend eastern edge of southern neighbor if it exists
                        if(map.isWall(x, y - 1) && mapCells[y - 1][x].getEdge('E') != null) {
                            mapCells[y - 1][x].getEdge('E').end = map.getPosTopR(x, y);
                            cell.setEdge('E', mapCells[y - 1][x].getEdge('E'));
                        }
                        // otherwise, create a new eastern edge
                        else {
                            Line edge = new Line(map.getPosBotR(x, y), map.getPosTopR(x, y));
                            lines.add(edge);
                            cell.setEdge('E', edge);
                        }
                    }

                    // if western tile is in bounds and not a wall we need an western edge
                    if(map.isInBounds(x - 1, y) && !map.isWall(x - 1, y)) {
                        // extend western edge of southern neighbor if it exists
                        if(map.isWall(x, y - 1) && mapCells[y - 1][x].getEdge('W') != null) {
                            mapCells[y - 1][x].getEdge('W').end = map.getPosTopL(x, y);
                            cell.setEdge('W', mapCells[y - 1][x].getEdge('W'));
                        }
                        // otherwise, create a new western edge
                        else {
                            Line edge = new Line(map.getPosBotL(x, y), map.getPosTopL(x, y));
                            lines.add(edge);
                            cell.setEdge('W', edge);
                        }
                    }
                }
            }
        }

        System.out.println(lines.size() + " wall lines generated");

        return lines;
    }




    //brute force, deprecated.
    //performance difference???
    public static List<Line> getWallLinesBruteForce(CaveMap map) {
        List<Line> lines = new ArrayList<>();

        for(int y = 0; y < map.getSize(); y++) {
            for(int x = 0; x < map.getSize(); x++) {
                if(map.isWall(x, y))
                    lines.addAll(getTileLines(new GridPoint2(x, y), map));
            }
        }

        return lines;
    }

    private static List<Line> getTileLines(GridPoint2 tile, CaveMap map) {
        List<Line> lines = new ArrayList<>();
        lines.add(new Line(map.getPosBotL(tile), map.getPosBotR(tile)));
        lines.add(new Line(map.getPosBotR(tile), map.getPosTopR(tile)));
        lines.add(new Line(map.getPosTopR(tile), map.getPosTopL(tile)));
        lines.add(new Line(map.getPosTopL(tile), map.getPosBotL(tile)));
        return lines;
    }

    public static void main(String[] args) {
        MapCell[] cells = new MapCell[4];
        cells[0] = new MapCell();
        System.out.println(cells[0].getEdge('N'));
    }
}
