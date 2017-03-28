package com.zorro.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class GameMain extends Game {

	public GameMenu gameMenuScreen;
	public GameScreen gamePlayScreen;
	public GameHSScreen gameHighScoresScreen;
	public GameInstructionsScreen gameInstructionsScreen;
	Context context;
	public SQLiteDatabase highscoreDB;

	public GameMain() {
	}

	public GameMain(Context context) {
		this.context = context;
	}

	@Override
	public void dispose() {
		gameMenuScreen.dispose();
		if (highscoreDB != null) {
			highscoreDB.close();
			System.out.println("Database closed");
		}
		super.dispose();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void setScreen(Screen screen) {
		super.setScreen(screen);
	}

	@Override
	public Screen getScreen() {
		return super.getScreen();
	}

	@Override
	public void create() {
		switch (Gdx.app.getType()) {
		case Android:
			DbHelper dbHelper = new DbHelper(context);
			highscoreDB = dbHelper.getWritableDatabase();
			gamePlayScreen = new GameScreen(this, highscoreDB);
			gameHighScoresScreen = new GameHSScreen(this, highscoreDB);
			break;
		case Desktop:
			gamePlayScreen = new GameScreen(this);
			gameHighScoresScreen = new GameHSScreen(this);
			break;
		case WebGL:
			break;
		case iOS:
			break;
		default:
			break;
		}
		gameMenuScreen = new GameMenu(this);
		gameInstructionsScreen = new GameInstructionsScreen(this);
		setScreen(gameMenuScreen);
	}

	@Override
	public void render() {
		super.render();
	}
}
