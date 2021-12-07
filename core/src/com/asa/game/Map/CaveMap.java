package com.asa.game.Map;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CaveMap {
    final int size;
    final int tileSize;

    private boolean[][] walls; //true = wall
    private Random rand;

    public CaveMap(int size, int tileSize) {
        this.size = size;
        this.tileSize = tileSize;

        walls = new boolean[size][size];
        rand = new Random();

        addRandomWalls(size/2, 1, size/2);
        clearCenter();
    }

    public boolean[][] getWalls() {
        return walls;
    }

    public Vector2 getCenter() {
        return new Vector2((size * tileSize) / 2, (size * tileSize) / 2);
    }

    //removes walls from center nine tiles if odd size, or center four tiles if even
    public void clearCenter() {
        GridPoint2 start = new GridPoint2((size/2) - 1, (size/2) - 1);
        if(size % 2 == 0)
            clearSquare(start, 2);
        else
            clearSquare(start, 3);
    }

    public boolean isWall(GridPoint2 tile) {
        if(!isInBounds(tile))
            return false;

        return walls[tile.y][tile.x];
    }

    public boolean isWall(int x, int y) {
        if(!isInBounds(x, y))
            return false;

        return walls[y][x];
    }

    public GridPoint2 getTile(Vector2 pos) {
        return new GridPoint2((int) pos.x / tileSize, (int) pos.y / tileSize);
    }

    public Vector2 getPos(GridPoint2 tile) {
        return new Vector2(tile.x * tileSize, tile.y * tileSize);
    }

    public boolean isInBounds(GridPoint2 tile) {
        return tile.x >= 0 && tile.y >= 0 && tile.x < size && tile.y < size;
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < size && y < size;
    }

    private void addRandomWalls(int numWalls, int minSize, int maxSize) {
        for(int i = 0; i < numWalls; i++) {
            GridPoint2 startTile = getRandomTile();
            GridPoint2 direction = getRandomDirection();
            int length = rand.nextInt(maxSize - minSize) + minSize;
            addWall(startTile, direction, length);
        }
    }

    private GridPoint2 getRandomTile() {
        int x = rand.nextInt(size);
        int y = rand.nextInt(size);
        return new GridPoint2(x, y);
    }

    private GridPoint2 getRandomDirection() {
        int i = rand.nextInt(4);
        switch(i) {
            case(0):
                return new GridPoint2(1,0);
            case(1):
                return new GridPoint2(-1,0);
            case(2):
                return new GridPoint2(0,1);
            default:
                return new GridPoint2(0,-1);
        }
    }

    private void addWall(GridPoint2 start, GridPoint2 dir, int length) {
        for (int i = 0; i < length; i++) {
            boolean inBounds = setWall(start, true);
            if (!inBounds)
                return;
            start.add(dir);
        }
    }

    private void clearSquare(GridPoint2 start, int size) {
        for(int x = start.x; x < start.x + size; x++) {
            for(int y = start.y; y < start.y + size; y++) {
                setWall(x, y, false);
            }
        }
    }

    private boolean setWall(GridPoint2 tile, boolean wall) {
        if(!isInBounds(tile))
            return false;

        walls[tile.y][tile.x] = wall;
        return true;
    }

    private boolean setWall(int x, int y, boolean wall) {
        if(!isInBounds(x, y))
            return false;

        walls[y][x] = wall;
        return true;
    }


}
