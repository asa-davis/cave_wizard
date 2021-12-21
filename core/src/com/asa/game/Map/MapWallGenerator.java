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

        //todo: change this to take dir enum
        private int getIndexFromDir(Direction dir) {
            if (dir == Direction.NORTH) return 0;
            if (dir == Direction.SOUTH) return 1;
            if (dir == Direction.EAST) return 2;
            if (dir == Direction.WEST) return 3;
            return -1; // what's an exception??
        }

        public void setHasNeighbor(Direction dir) {
            hasNeighbor[getIndexFromDir(dir)] = true;
        }

        public void setEdge(Direction dir, Line edge) {
            edges[getIndexFromDir(dir)] = edge;
        }

        public Line getEdge(Direction dir) {
            return edges[getIndexFromDir(dir)];
        }

        public MapCell getSouthernNeighbor() {
            return southernNeighbor;
        }

        public MapCell getWesternNeighbor() {
            return westernNeighbor;
        }
    }

    private enum Direction {
        NORTH (0, 1) {
            @Override
            public Line getEdge(CaveMap map, int x, int y) {
                return new Line(map.getPosTopL(x, y), map.getPosTopR(x, y));
            }
        },
        SOUTH( 0, -1) {
            @Override
            public Line getEdge(CaveMap map, int x, int y) {
                return new Line(map.getPosBotL(x, y), map.getPosBotR(x, y));
            }
        },
        EAST(1, 0) {
            @Override
            public Direction getDirToExtend() {
                return SOUTH;
            }

            @Override
            public Line getEdge(CaveMap map, int x, int y) {
                return new Line(map.getPosBotR(x, y), map.getPosTopR(x, y));
            }
        },
        WEST(-1, 0){
            @Override
            public Direction getDirToExtend() {
                return SOUTH;
            }

            @Override
            public Line getEdge(CaveMap map, int x, int y) {
                return new Line(map.getPosBotL(x, y), map.getPosTopL(x, y));
            }
        };

        private int xMod;
        private int yMod;

        Direction(int xMod, int yMod ) {
            this.xMod = xMod;
            this.yMod = yMod;
        }

        public GridPoint2 getTilePos(int x, int y) {
            return new GridPoint2(x + xMod, y + yMod);
        }

        public Direction getDirToExtend() {
            return WEST;
        }

        public abstract Line getEdge(CaveMap map, int x, int y);
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

                    //calculate edge for all directions
                    for(Direction dir : Direction.values()) {
                        // if northern tile is in bounds and not a wall we need a northern edge
                        GridPoint2 dirPos = dir.getTilePos(x, y);
                        GridPoint2 extendDirPos = dir.getDirToExtend().getTilePos(x, y);
                        Line edge = dir.getEdge(map, x, y);

                        if(map.isInBounds(dirPos) && !map.isWall(dirPos)) {
                            // extend northern edge of western neighbor if it exists
                            if(map.isWall(extendDirPos) && mapCells[extendDirPos.y][extendDirPos.x].getEdge(dir) != null) {
                                mapCells[extendDirPos.y][extendDirPos.x].getEdge(dir).end = edge.end;
                                cell.setEdge(dir, mapCells[extendDirPos.y][extendDirPos.x].getEdge(dir));
                            }
                            // otherwise, create a new northern edge
                            else {
                                lines.add(edge);
                                cell.setEdge(dir, edge);
                            }
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
    }
}
