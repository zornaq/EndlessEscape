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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

public class GameInstructionsScreen implements Screen {

	private Stage stage;
	private Skin skin;
	private Table table;
	private Label label;
	private TextButton backGameButton;
	private GameMain gameMain;
	private OrthographicCamera camera;

	public GameInstructionsScreen(GameMain gameMain) {
		this.gameMain = gameMain;
	}

	@Override
	public void show() {
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		// set viewport for stage using virtual screen size
		stage = new Stage(new ScalingViewport(Scaling.fit, (Gdx.graphics.getWidth() * 1.0f), (Gdx.graphics.getHeight() * 1.0f)));
		table = new Table();

		String instructions = "" 
				+ "ENDLESS ESCAPE instructions\n" 
				+ "\n" + "You wake up in a dungeon and you\n" 
				+ "need to escape the evil demons.\n" 
				+ "There is a portal hidden somewhere\n" 
				+ "that you need to find. You can move\n" 
				+ "by touching the screen near player\n" 
				+ "up/down/right/left. You can also\n" 
				+ "build/demolish blocks by activating\n" 
				+ "player at center and dragging to\n" 
				+ "highlighted area next to you.\n" 
				+ "\n" 
				+ "Be smart and don't let the demons\n" 
				+ "catch you! Good luck!";

		//setup actors
		label = new Label(instructions, skin);
		label.setAlignment(Align.center);
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
		stage.getViewport().setWorldSize((table.getMinWidth() * 1.0f), (table.getMinHeight() * 1.0f));

		// add table to stage area
		stage.addActor(table);

		// initialise camera and set it to ortho using Y-UP coordinates
		camera = new OrthographicCamera(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
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
		//dispose when leaving the screen for better memory management
		stage.dispose();
		skin.dispose();
	}
}
