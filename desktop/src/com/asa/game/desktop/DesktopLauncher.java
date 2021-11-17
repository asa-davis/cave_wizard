package com.asa.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.asa.game.MainGame;

public class DesktopLauncher {

	static final float SCREEN_SIZE_RATIO = 3/5f;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "cave wizard";
		config.width = (int)(1920 * SCREEN_SIZE_RATIO);
		config.height = (int)(1080 * SCREEN_SIZE_RATIO);
		config.resizable = false;

		new LwjglApplication(new MainGame(), config);
	}
}
