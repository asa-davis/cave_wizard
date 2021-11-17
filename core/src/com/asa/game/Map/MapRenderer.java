package com.asa.game.Map;

import com.asa.game.Map.Map;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class MapRenderer {
    private Map map;
    private ShapeRenderer shapeRenderer;
    private ShapeRenderer lineRenderer;

    
    private List<GridPoint2> visible;
    private List<Vector2[]> rays;

    public MapRenderer(Map map, ShapeRenderer shapeRenderer, ShapeRenderer lineRenderer) {
        this.map = map;
        this.shapeRenderer = shapeRenderer;
        this.lineRenderer = lineRenderer;

        visible = new ArrayList<>();
        rays = new ArrayList<>();
    }

    public void drawGrid() {
        lineRenderer.setColor(Color.BLUE);
        for(int i = 0; i <= map.size; i++) {
            lineRenderer.line(i * map.tileSize, 0, i * map.tileSize, map.size * map.tileSize);
            lineRenderer.line(0, i * map.tileSize, map.size * map.tileSize, i * map.tileSize);
        }
    }

    public void calcRaysAndVisibleTiles(int numRays, Vector2 pov) {
        rays.clear();
        visible.clear();
        visible.add(map.getTile(pov));
        //cast a number of rays from pos at angles equally distributed over 360 degrees.
        //for each ray, set each tile it passes through as visible and terminate it when it hits a wall.

        double angleStep = 2 * Math.PI/numRays;
        for(int i = 0; i < numRays; i++) {
            double angle = i * angleStep;
            Vector2 dir = new Vector2((float) Math.cos(angle), (float) Math.sin(angle));
            Vector2 end = calcRayEndpoint(pov, dir);
            rays.add(new Vector2[]{pov, end});
        }
    }

    public void drawTiles() {
        for(int x = 0; x < map.size; x++) {
            for(int y = 0; y < map.size; y++) {
                GridPoint2 tile = new GridPoint2(x, y);
                Color wallColor = Color.BLACK;
                Color floorColor = Color.LIGHT_GRAY;

                if(!visible.contains(tile)) {
                    wallColor = Color.DARK_GRAY;
                    floorColor = Color.GRAY;
                }

                if(map.isWall(tile))
                    shapeRenderer.setColor(wallColor);
                else
                    shapeRenderer.setColor(floorColor);

                shapeRenderer.rect(x * map.tileSize, y * map.tileSize, map.tileSize, map.tileSize);
            }
        }
    }

    public void drawRays() {
        lineRenderer.setColor(Color.RED);
        for(Vector2[] ray : rays) {
            lineRenderer.line(ray[0], ray[1]);
        }
    }

    //DDA algorithm from One Lone Coder
    private Vector2 calcRayEndpoint(Vector2 start, Vector2 dir) {
        //distance along ray to travel one unit in the x or y axis
        Vector2 rayUnitStepSize = new Vector2((float) Math.sqrt(1 + Math.pow(dir.y/dir.x, 2)) * map.tileSize, (float) Math.sqrt(1 + Math.pow(dir.x/dir.y, 2)) * map.tileSize);
        GridPoint2 currTile = map.getTile(start);
        //length of ray if we travel in even unit steps in x or y axis
        Vector2 rayLength1D = new Vector2(0, 0);
        //amount/direction we travel along x and y axis
        GridPoint2 step = new GridPoint2(0, 0);

        if(dir.x < 0) {
            step.x = -1;
            rayLength1D.x = ((start.x - map.getPos(currTile).x) / map.tileSize) * rayUnitStepSize.x;
        } else {
            step.x = 1;
            rayLength1D.x = ((map.getPos(currTile).x + map.tileSize - start.x) / map.tileSize) * rayUnitStepSize.x;
        }

        if(dir.y < 0) {
            step.y = -1;
            rayLength1D.y = ((start.y - map.getPos(currTile).y) / map.tileSize) * rayUnitStepSize.y;
        } else {
            step.y = 1;
            rayLength1D.y = ((map.getPos(currTile).y + map.tileSize - start.y) / map.tileSize) * rayUnitStepSize.y;
        }

        float distance = 0;
        while(map.isInBounds(currTile) && !map.isWall(currTile)) {
            if(rayLength1D.x < rayLength1D.y) {
                currTile.x += step.x;
                distance = rayLength1D.x;
                rayLength1D.x += rayUnitStepSize.x;
            } else {
                currTile.y += step.y;
                distance = rayLength1D.y;
                rayLength1D.y += rayUnitStepSize.y;
            }
            if(!visible.contains(currTile))
                visible.add(new GridPoint2(currTile));
        }

        return new Vector2(dir.x * distance, dir.y * distance).add(start);
    }
}
