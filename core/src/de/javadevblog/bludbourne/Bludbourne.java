package de.javadevblog.bludbourne;

import com.badlogic.gdx.Game;

public class Bludbourne extends Game{
	
	public static final MainGameSceen mainGameScreen = new MainGameScreen();

	@Override
	public void create() {
		setScreen(mainGameScreen);
	}

	@Override
	public void dispose() {
		mainGameScreen.dispose();
	}
}
