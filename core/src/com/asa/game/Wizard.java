package com.asa.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Wizard {
    private Vector2 position;
    private int size;

    public Wizard(Vector2 position) {
        this.position = position;
        size = 8;
    }

    public void render(ShapeRenderer shapes) {
        shapes.setColor(Color.FIREBRICK);
        shapes.circle(position.x, position.y, size);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 pos) {
        position = pos;
    }
}
