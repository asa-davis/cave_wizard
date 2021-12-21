package com.asa.game.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class MapRenderer {
    private CaveMap caveMap;
    private ShapeRenderer shapeRenderer;
    private ShapeRenderer lineRenderer;

    
    private List<GridPoint2> visible;
    private List<Vector2[]> rays;

    public MapRenderer(CaveMap caveMap, ShapeRenderer shapeRenderer, ShapeRenderer lineRenderer) {
        this.caveMap = caveMap;
        this.shapeRenderer = shapeRenderer;
        this.lineRenderer = lineRenderer;

        visible = new ArrayList<>();
        rays = new ArrayList<>();
    }

    public void drawGrid() {
        lineRenderer.setColor(Color.BLUE);
        for(int i = 0; i <= caveMap.size; i++) {
            lineRenderer.line(i * caveMap.tileSize, 0, i * caveMap.tileSize, caveMap.size * caveMap.tileSize);
            lineRenderer.line(0, i * caveMap.tileSize, caveMap.size * caveMap.tileSize, i * caveMap.tileSize);
        }
    }

    public void calcRaysAndVisibleTiles(int numRays, Vector2 pov) {
        rays.clear();
        visible.clear();
        visible.add(caveMap.getTile(pov));
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
        for(int x = 0; x < caveMap.size; x++) {
            for(int y = 0; y < caveMap.size; y++) {
                GridPoint2 tile = new GridPoint2(x, y);
                Color wallColor = Color.BLACK;
                Color floorColor = Color.LIGHT_GRAY;

                if(!visible.contains(tile)) {
                    //wallColor = Color.DARK_GRAY;
                    //floorColor = Color.GRAY;
                }

                if(caveMap.isWall(tile))
                    shapeRenderer.setColor(wallColor);
                else
                    shapeRenderer.setColor(floorColor);

                shapeRenderer.rect(x * caveMap.tileSize, y * caveMap.tileSize, caveMap.tileSize, caveMap.tileSize);
            }
        }
    }

    public void drawRays() {
        lineRenderer.setColor(Color.RED);
        for(Vector2[] ray : rays) {
            lineRenderer.line(ray[0], ray[1]);
        }
    }

    //DDA algorithm
    private Vector2 calcRayEndpoint(Vector2 start, Vector2 dir) {
        //distance along ray to travel one unit in the x or y axis
        Vector2 rayUnitStepSize = new Vector2((float) Math.sqrt(1 + Math.pow(dir.y/dir.x, 2)) * caveMap.tileSize, (float) Math.sqrt(1 + Math.pow(dir.x/dir.y, 2)) * caveMap.tileSize);
        GridPoint2 currTile = caveMap.getTile(start);
        //length of ray if we travel in even unit steps in x or y axis
        Vector2 rayLength1D = new Vector2(0, 0);
        //amount/direction we travel along x and y axis
        GridPoint2 step = new GridPoint2(0, 0);

        if(dir.x < 0) {
            step.x = -1;
            rayLength1D.x = ((start.x - caveMap.getPosBotL(currTile).x) / caveMap.tileSize) * rayUnitStepSize.x;
        } else {
            step.x = 1;
            rayLength1D.x = ((caveMap.getPosBotL(currTile).x + caveMap.tileSize - start.x) / caveMap.tileSize) * rayUnitStepSize.x;
        }

        if(dir.y < 0) {
            step.y = -1;
            rayLength1D.y = ((start.y - caveMap.getPosBotL(currTile).y) / caveMap.tileSize) * rayUnitStepSize.y;
        } else {
            step.y = 1;
            rayLength1D.y = ((caveMap.getPosBotL(currTile).y + caveMap.tileSize - start.y) / caveMap.tileSize) * rayUnitStepSize.y;
        }

        float distance = 0;
        while(caveMap.isInBounds(currTile) && !caveMap.isWall(currTile)) {
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

    public void drawPolys() {
        //testing polygons for walls
        lineRenderer.setColor(Color.GREEN);
        for(MapWallGenerator.Line line : caveMap.getWallLines()) {
            lineRenderer.line(line.start, line.end);
        }
    }
}
