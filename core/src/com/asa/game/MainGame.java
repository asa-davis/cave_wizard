package com.asa.game;

import com.asa.game.Map.Map;
import com.asa.game.Map.MapRenderer;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainGame extends ApplicationAdapter {
	ShapeRenderer shapeRenderer;
	ShapeRenderer lineRenderer;

	Map map;
	MapRenderer mapRenderer;
	Wizard wizard;

	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		lineRenderer = new ShapeRenderer();

		map = new Map(32, 16);
		mapRenderer = new MapRenderer(map, shapeRenderer, lineRenderer);
		wizard = new Wizard(map.getCenter());
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.15f, 0.2f, 0.59f, 1);

		mapRenderer.calcRaysAndVisibleTiles(3000, wizard.getPosition());

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		mapRenderer.drawTiles();
		wizard.render(shapeRenderer);
		shapeRenderer.end();

		lineRenderer.begin(ShapeRenderer.ShapeType.Line);
		//mapRenderer.drawGrid();
		//mapRenderer.drawRays();
		lineRenderer.end();

		handleInput();
	}

	private void handleInput() {
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			wizard.setPosition(wizard.getPosition().add(0, 1));
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			wizard.setPosition(wizard.getPosition().add(0, -1));
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			wizard.setPosition(wizard.getPosition().add(1, 0));
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			wizard.setPosition(wizard.getPosition().add(-1, 0));
		}
	}
	
	@Override
	public void dispose () {
		shapeRenderer.dispose();
		lineRenderer.dispose();
	}
}
