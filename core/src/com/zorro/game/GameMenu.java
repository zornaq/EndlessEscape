package com.zorro.game;

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

public class GameMenu implements Screen {

	private Stage stage;
	private Skin skin;
	private Table table;
	private Label label;
	private TextButton newGameButton;
	private TextButton highscoreButton;
	private TextButton instructionsButton;
	private TextButton quitGameButton;
	private GameMain gameMain;
	private OrthographicCamera camera;

	public GameMenu(GameMain gameMain) {
		this.gameMain = gameMain;
	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
	}

	@Override
	public void resize(int width, int height) {
		// Update real screen size for viewport
		stage.getViewport().update(width, height);
	}

	@Override
	public void show() {
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		// set viewport for stage using virtual screen size
		stage = new Stage(new ScalingViewport(Scaling.fit, (Gdx.graphics.getWidth() * 1.0f), (Gdx.graphics.getHeight() * 1.0f)));
		table = new Table();
		label = new Label("ENDLESS ESCAPE", skin);
		newGameButton = new TextButton("New Game", skin);
		highscoreButton = new TextButton("HighScores", skin);
		instructionsButton = new TextButton("Instructions", skin);
		quitGameButton = new TextButton("Quit Game", skin);

		// Actions for clicking buttons
		newGameButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				event.stop();
				gameMain.setScreen(gameMain.gamePlayScreen);
			}
		});

		highscoreButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				event.stop();
				gameMain.setScreen(gameMain.gameHighScoresScreen);
			}
		});

		instructionsButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				event.stop();
				gameMain.setScreen(gameMain.gameInstructionsScreen);
			}
		});

		quitGameButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				event.stop();
				Gdx.app.exit();
			}
		});

		// add actors to the table
		table.add(label).padBottom(40);
		table.row();
		table.add(newGameButton).padBottom(30);
		table.row();
		table.add(highscoreButton).padBottom(30);
		table.row();
		table.add(instructionsButton).padBottom(30);
		table.row();
		table.add(quitGameButton);
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
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
