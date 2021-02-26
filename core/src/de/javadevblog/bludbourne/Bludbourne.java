package de.javadevblog.bludbourne;

import com.badlogic.gdx.Game;

import de.javadevblog.bludbourne.screens.MainGameScreen;

public class Bludbourne extends Game{
	
	public static final MainGameScreen mainGameScreen = new MainGameScreen();

	@Override
	public void create() {
		setScreen(mainGameScreen);
	}

	@Override
	public void dispose() {
		mainGameScreen.dispose();
	}
}
