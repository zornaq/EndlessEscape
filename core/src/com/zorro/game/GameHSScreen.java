package com.zorro.game;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

import java.util.ArrayList;

public class GameHSScreen implements Screen {

	private Stage stage;
	private Skin skin;
	private Table table;
	private Label label;
	private TextButton backGameButton;
	private GameMain gameMain;
	private String scoresText = "";
	private OrthographicCamera camera;
	private SQLiteDatabase highscoreDB;

	public GameHSScreen(GameMain gameMain, SQLiteDatabase highscoreDB) {
		this.gameMain = gameMain;
		this.highscoreDB = highscoreDB;
	}

	public GameHSScreen(GameMain gameMain) {
		this.gameMain = gameMain;
	}

	public void getDatabaseResults() {
		// create dummy scores arraylist
		ArrayList<Score> scores = new ArrayList();
		for (int x = 0; x < 10; x++) {
			Score score = new Score();
			score.setCreationTime("Empty");
			score.setScore(99999);
			scores.add(score);
		}

		// for android get data from SQLite database in order of "score"
		if (Gdx.app.getType() == ApplicationType.Android) {
			String[] col = { "_id", "createdAt", "score" };
			String selection = null;
			String[] selectionArgs = null;
			String groupBy = null;
			String having = null;
			String orderBy = "score";
			Cursor c = highscoreDB.query("highscores", col, selection, selectionArgs, groupBy, having, orderBy);

			// get 10 results from query and set them in scores arraylist
			int index = 0;
			while (c.moveToNext() && index < 10) {
				scores.get(index).setCreationTime(c.getString(c.getColumnIndex("createdAt")));
				scores.get(index).setScore(c.getInt(c.getColumnIndex("score")));
				index++;
			}
		}
		// make a string with the results
		scoresText = "";
		for (int x = 0; x < 10; x++) {
			scoresText = scoresText + (x + 1) + ". " + scores.get(x).getTime() + "--> " + scores.get(x).getScore() + "\n";
		}
	}

	@Override
	public void show() {
		// get highscore list from database
		getDatabaseResults();

		skin = new Skin(Gdx.files.internal("uiskin.json"));
		// set viewport for stage using virtual screen size
		stage = new Stage(new ScalingViewport(Scaling.fit, (Gdx.graphics.getWidth() * 1.0f), (Gdx.graphics.getHeight() * 1.0f)));
		table = new Table();

		// setup actors and set "scoresText" in label
		label = new Label("HIGHSCORES\n" + "" + scoresText, skin);
		backGameButton = new TextButton("Back to main menu", skin);

		// Actions for clicking buttons
		backGameButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				event.stop();
				gameMain.setScreen(gameMain.gameMenuScreen);
			}
		});

		// add actors to the table
		table.add(label).padBottom(40);
		table.row();
		table.add(backGameButton);
		// set table position at middle of the screen
		table.setPosition(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() / 2) - (table.getHeight() / 2));
		// set viewport virtual screen size using calculation from table size
		stage.getViewport().setWorldSize((table.getMinWidth() * 1.2f), (table.getMinHeight() * 1.2f));

		// add table to stage area
		stage.addActor(table);

		// initialise camera and set it to ortho using Y-UP coordinates
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		camera.setToOrtho(false);
		camera.update();
		stage.getViewport().setCamera(camera);

		// set input listening to the stage
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		// clear the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// update actors on stage and draw stage
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// Update real screen size for viewport
		stage.getViewport().update(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		// dispose when leaving the screen for better memory management
		stage.dispose();
		skin.dispose();
	}
}
