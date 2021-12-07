package com.asa.game;

import com.asa.game.Map.CaveMap;
import com.asa.game.Map.MapRenderer;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainGame extends ApplicationAdapter {
	ShapeRenderer shapeRenderer;
	ShapeRenderer lineRenderer;

	CaveMap caveMap;
	MapRenderer mapRenderer;
	Wizard wizard;

	//for later - use one big floor texture to draw only the visible polygon
	float[] polyVert = new float[] {0, 0, 200, 0, 100, 300, 0, 0};
	TextureRegion floor;
	PolygonSpriteBatch polySpriteBatch;

	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		lineRenderer = new ShapeRenderer();

		caveMap = new CaveMap(32, 16);
		mapRenderer = new MapRenderer(caveMap, shapeRenderer, lineRenderer);
		wizard = new Wizard(caveMap.getCenter());

		//for later - use one big floor texture to draw only the visible polygon
		polySpriteBatch = new PolygonSpriteBatch();
		floor = new TextureRegion(new Texture("raw_textures/floor.jpg"));
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.15f, 0.2f, 0.59f, 1);
		Gdx.graphics.setTitle("" + Gdx.graphics.getFramesPerSecond());

		mapRenderer.calcRaysAndVisibleTiles(3000, wizard.getPosition());

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		mapRenderer.drawTiles();
		wizard.render(shapeRenderer);
		shapeRenderer.end();

		lineRenderer.begin(ShapeRenderer.ShapeType.Line);
		//mapRenderer.drawGrid();
		mapRenderer.drawRays();
		lineRenderer.end();

		//for later - use one big floor texture to draw only the visible polygon
		//drawFloor();

		handleInput();
	}

	private void handleInput() {
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			wizard.setPosition(wizard.getPosition().add(0, 1));
			polyVert[4] += 5;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			wizard.setPosition(wizard.getPosition().add(0, -1));
			polyVert[4] -= 5;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			wizard.setPosition(wizard.getPosition().add(1, 0));
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			wizard.setPosition(wizard.getPosition().add(-1, 0));
		}
	}

	//for later - use one big floor texture to draw only the visible polygon
	private void drawFloor() {
		PolygonRegion floorTexturePolygon = new PolygonRegion(floor, polyVert, new short[] {0, 1, 2, 0, 3, 2});
		polySpriteBatch.begin();
		polySpriteBatch.draw(floorTexturePolygon, 800, 200);
		polySpriteBatch.end();
	}
	
	@Override
	public void dispose () {
		shapeRenderer.dispose();
		lineRenderer.dispose();
		polySpriteBatch.dispose();
	}
}
