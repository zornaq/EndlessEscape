package com.zorro.game;

import java.util.LinkedHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

public class Player {

	Sprite playerSprite;
	private SpriteBatch playerBatch;
	Vector3 position;
	private Vector2 dimensions;
	float velocity = 32f;
	TweenManager manager;
	private BitmapFont font;
	private SpriteBatch fontBatch;
	Camera camera;
	Vector3 position_unproject;
	LinkedHashMap<String, Rectangle> rectPlates = new LinkedHashMap<String, Rectangle>();
	ShapeRenderer shapeRenderer;
	boolean actionPressed = false;
	TiledMapCreator tiledMapCreator;
	static Vector3 transformTimer;

	private static final int col = 4;
	private static final int row = 4;

	Animation animationNorth;
	Animation animationSouth;
	Animation animationWest;
	Animation animationEast;
	Animation animationCurrent;
	Texture playerTexture;
	TextureRegion[] framesNorth;
	TextureRegion[] framesSouth;
	TextureRegion[] framesEast;
	TextureRegion[] framesWest;
	TextureRegion currentFrame;
	float stateTime;

	public Player(float x, float y, float width, float height) {
		position = new Vector3(x, y, 0);
		transformTimer = new Vector3(0, 0, 0);
		dimensions = new Vector2(width, height);
		position_unproject = new Vector3();

		playerSprite = new Sprite(new Texture(Gdx.files.internal("player.jpg")));
		playerSprite.setSize(dimensions.x, dimensions.y);

		playerTexture = new Texture(Gdx.files.internal("player.png"));
		TextureRegion[][] tmp = TextureRegion.split(playerTexture, playerTexture.getWidth() / col, playerTexture.getHeight() / row);
		framesNorth = new TextureRegion[row];
		framesSouth = new TextureRegion[row];
		framesEast = new TextureRegion[row];
		framesWest = new TextureRegion[row];

		int index = 0;
		for (int i = 0; i < col; i++) {
			framesSouth[index++] = tmp[0][i];
		}
		index = 0;
		for (int i = 0; i < col; i++) {
			framesWest[index++] = tmp[1][i];
		}
		index = 0;
		for (int i = 0; i < col; i++) {
			framesEast[index++] = tmp[2][i];
		}
		index = 0;
		for (int i = 0; i < col; i++) {
			framesNorth[index++] = tmp[3][i];
		}

		animationSouth = new Animation(0.3f, framesSouth);
		animationWest = new Animation(0.3f, framesWest);
		animationEast = new Animation(0.3f, framesEast);
		animationNorth = new Animation(0.3f, framesNorth);
		stateTime = 0f;
		animationCurrent = animationSouth;

		playerBatch = new SpriteBatch();

		Tween.registerAccessor(Sprite.class, new SpriteTween(position));
		manager = new TweenManager();

		// Font
		font = new BitmapFont();
		fontBatch = new SpriteBatch();

		shapeRenderer = new ShapeRenderer();

		// Movement plates for detecting touching
		createRectPlateAtXY((position.x + (32 * 1)), (position.y), "eastPlate", 64, 32);
		createRectPlateAtXY((position.x - (32 * 2)), (position.y), "westPlate", 64, 32);
		createRectPlateAtXY((position.x), (position.y + (32 * 1)), "northPlate", 32, 64);
		createRectPlateAtXY((position.x), (position.y - (32 * 2)), "southPlate", 32, 64);

		// Action plates for detecting touching
		createRectPlateAtXY((position.x), (position.y), "actionPlate", 32, 32);
		createRectPlateAtXY((position.x + (32 * 1)), (position.y), "actionPlateEast", 32, 32);
		createRectPlateAtXY((position.x - (32 * 1)), (position.y), "actionPlateWest", 32, 32);
		createRectPlateAtXY((position.x), (position.y + (32 * 1)), "actionPlateNorth", 32, 32);
		createRectPlateAtXY((position.x), (position.y - (32 * 1)), "actionPlateSouth", 32, 32);

		// Detection plate for the player collision
		createRectPlateAtXY((position.x), (position.y), "playerDetectionPlate", 32, 32);
	}

	public void reset(float x, float y) {
		manager.killAll();

		position.x = x;
		position.y = y;

		// Update player location
		playerSprite.setPosition(position.x, position.y);

		// Update RectPlate locations
		rectPlates.get("eastPlate").x = (position.x + (32 * 1));
		rectPlates.get("eastPlate").y = (position.y);
		rectPlates.get("westPlate").x = (position.x - (32 * 2));
		rectPlates.get("westPlate").y = (position.y);
		rectPlates.get("northPlate").x = (position.x);
		rectPlates.get("northPlate").y = (position.y + (32 * 1));
		rectPlates.get("southPlate").x = (position.x);
		rectPlates.get("southPlate").y = (position.y - (32 * 2));
		rectPlates.get("actionPlate").x = position.x;
		rectPlates.get("actionPlate").y = position.y;
		rectPlates.get("playerDetectionPlate").x = position.x;
		rectPlates.get("playerDetectionPlate").y = position.y;
		rectPlates.get("actionPlateEast").x = position.x + (32 * 1);
		rectPlates.get("actionPlateEast").y = position.y;
		rectPlates.get("actionPlateWest").x = position.x - (32 * 1);
		rectPlates.get("actionPlateWest").y = position.y;
		rectPlates.get("actionPlateNorth").x = position.x;
		rectPlates.get("actionPlateNorth").y = position.y + (32 * 1);
		rectPlates.get("actionPlateSouth").x = position.x;
		rectPlates.get("actionPlateSouth").y = position.y - (32 * 1);
	}

	// private void createRectPlateAtCenter(float f, float g, String string, int
	// i, int j) {
	// Rectangle plate = new Rectangle();
	// plate.width = i;
	// plate.height = j;
	// plate.setCenter(f, g);
	// rectPlates.put(string, plate);
	// }

	private void createRectPlateAtXY(float f, float g, String string, int i, int j) {
		Rectangle plate = new Rectangle();
		plate.x = f;
		plate.y = g;
		plate.width = i;
		plate.height = j;
		rectPlates.put(string, plate);
	}

	public void processActionPress(final String actionPlate) {
		boolean timerIsOn = false;
		transformTimer.x = (int) rectPlates.get(actionPlate).getX() + 16;
		transformTimer.y = (int) rectPlates.get(actionPlate).getY() + 16;
		transformTimer.z = 0;

		// grass
		if (tiledMapCreator.mapTable[(int) rectPlates.get(actionPlate).getX() / 32][(int) rectPlates.get(actionPlate).getY() / 32].getTile().getProperties().get("type", Integer.class).equals(1)) {

			if (!timerIsOn) {
				timerIsOn = true;

				new Thread(new Runnable() {

					@Override
					public void run() {
						float counter = 0;
						boolean quit = false;
						GameScreen.processSound.loop(0.3f);

						mainLoop: while (counter <= 1000 && !quit) {
							counter = counter + Gdx.app.getGraphics().getDeltaTime();
							transformTimer.z = counter;
							System.out.println("Counter=" + counter);
							if (Gdx.input.isTouched()) {
								System.out.println("Counter=CANCELLED");
								quit = true;
								transformTimer.z = 0;
								GameScreen.processSound.stop();
								break mainLoop;
							} else if (counter >= 1000) {
								tiledMapCreator.changeTile((int) rectPlates.get(actionPlate).getX(), (int) rectPlates.get(actionPlate).getY(), tiledMapCreator.stoneTile);
								quit = true;
								transformTimer.z = 0;
								GameScreen.processSound.stop();
								break mainLoop;
							}
						}
					}
				}).start();
			}
		}

		// stone
		else if (tiledMapCreator.mapTable[(int) rectPlates.get(actionPlate).getX() / 32][(int) rectPlates.get(actionPlate).getY() / 32].getTile().getProperties().get("type", Integer.class).equals(2)) {

			if (!timerIsOn) {
				timerIsOn = true;

				new Thread(new Runnable() {

					@Override
					public void run() {
						float counter = 0;
						boolean quit = false;
						GameScreen.processSound.loop(0.3f);

						mainLoop: while (counter <= 1000 && !quit) {
							counter = counter + Gdx.app.getGraphics().getDeltaTime();
							transformTimer.z = counter;
							System.out.println("Counter=" + counter);
							if (Gdx.input.isTouched()) {
								System.out.println("Counter=CANCELLED");
								quit = true;
								transformTimer.z = 0;
								GameScreen.processSound.stop();
								break mainLoop;
							} else if (counter >= 1000) {
								tiledMapCreator.changeTile((int) rectPlates.get(actionPlate).getX(), (int) rectPlates.get(actionPlate).getY(), tiledMapCreator.grassTile);
								quit = true;
								transformTimer.z = 0;
								GameScreen.processSound.stop();
								break mainLoop;
							}
						}
					}
				}).start();
			}
		}
	}

	public void update(TiledMapCreator tiledMapCreator, Viewport viewport) {

		// if(Gdx.input.isKeyJustPressed(Input.Keys.A)){
		// if(!manager.containsTarget(playerSprite) &&
		// tiledMapCreator.checkMapIfMoveOk(position, 4)){
		// Tween.to(playerSprite,SpriteTween.POSITION_X,0.15f).target(position.x-velocity).start(manager);
		// }
		// }
		//
		// if(Gdx.input.isKeyJustPressed(Input.Keys.D)){
		// if(!manager.containsTarget(playerSprite) &&
		// tiledMapCreator.checkMapIfMoveOk(position, 2)){
		// Tween.to(playerSprite,SpriteTween.POSITION_X,0.15f).target(position.x+velocity).start(manager);
		// }
		// }
		//
		// if(Gdx.input.isKeyJustPressed(Input.Keys.W)){
		// if(!manager.containsTarget(playerSprite) &&
		// tiledMapCreator.checkMapIfMoveOk(position, 1)){
		// Tween.to(playerSprite,SpriteTween.POSITION_Y,0.15f).target(position.y+velocity).start(manager);
		// }
		// }
		//
		// if(Gdx.input.isKeyJustPressed(Input.Keys.S)){
		// if(!manager.containsTarget(playerSprite) &&
		// tiledMapCreator.checkMapIfMoveOk(position, 3)){
		// Tween.to(playerSprite,SpriteTween.POSITION_Y,0.15f).target(position.y-velocity).start(manager);
		// }
		// }

		// Update player location
		playerSprite.setPosition(position.x, position.y);

		// Update RectPlate locations
		rectPlates.get("eastPlate").x = (position.x + (32 * 1));
		rectPlates.get("eastPlate").y = (position.y);
		rectPlates.get("westPlate").x = (position.x - (32 * 2));
		rectPlates.get("westPlate").y = (position.y);
		rectPlates.get("northPlate").x = (position.x);
		rectPlates.get("northPlate").y = (position.y + (32 * 1));
		rectPlates.get("southPlate").x = (position.x);
		rectPlates.get("southPlate").y = (position.y - (32 * 2));
		rectPlates.get("actionPlate").x = position.x;
		rectPlates.get("actionPlate").y = position.y;
		rectPlates.get("playerDetectionPlate").x = position.x;
		rectPlates.get("playerDetectionPlate").y = position.y;
		rectPlates.get("actionPlateEast").x = position.x + (32 * 1);
		rectPlates.get("actionPlateEast").y = position.y;
		rectPlates.get("actionPlateWest").x = position.x - (32 * 1);
		rectPlates.get("actionPlateWest").y = position.y;
		rectPlates.get("actionPlateNorth").x = position.x;
		rectPlates.get("actionPlateNorth").y = position.y + (32 * 1);
		rectPlates.get("actionPlateSouth").x = position.x;
		rectPlates.get("actionPlateSouth").y = position.y - (32 * 1);

		if (actionPressed) {
			Gdx.gl.glEnable(GL30.GL_BLEND);
			Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(new Color(0, 0, 1, 0.3f));

			// if east is grass
			if (tiledMapCreator.mapTable[(int) rectPlates.get("actionPlateEast").getX() / 32][(int) rectPlates.get("actionPlateEast").getY() / 32].getTile().getProperties().get("type", Integer.class).equals(1)) {
				shapeRenderer.setColor(new Color(0, 0, 1, 0.3f));
				shapeRenderer.rect(rectPlates.get("actionPlateEast").getX(), rectPlates.get("actionPlateEast").getY(), rectPlates.get("actionPlateEast").getWidth(), rectPlates.get("actionPlateEast").getHeight());
			}
			// if east is stone
			else if (tiledMapCreator.mapTable[(int) rectPlates.get("actionPlateEast").getX() / 32][(int) rectPlates.get("actionPlateEast").getY() / 32].getTile().getProperties().get("type", Integer.class).equals(2)) {
				shapeRenderer.setColor(new Color(1, 0, 0, 0.3f));
				shapeRenderer.rect(rectPlates.get("actionPlateEast").getX(), rectPlates.get("actionPlateEast").getY(), rectPlates.get("actionPlateEast").getWidth(), rectPlates.get("actionPlateEast").getHeight());
			}
			// if west is grass
			if (tiledMapCreator.mapTable[(int) rectPlates.get("actionPlateWest").getX() / 32][(int) rectPlates.get("actionPlateWest").getY() / 32].getTile().getProperties().get("type", Integer.class).equals(1)) {
				shapeRenderer.setColor(new Color(0, 0, 1, 0.3f));
				shapeRenderer.rect(rectPlates.get("actionPlateWest").getX(), rectPlates.get("actionPlateWest").getY(), rectPlates.get("actionPlateWest").getWidth(), rectPlates.get("actionPlateWest").getHeight());
			}
			// if west is stone
			else if (tiledMapCreator.mapTable[(int) rectPlates.get("actionPlateWest").getX() / 32][(int) rectPlates.get("actionPlateWest").getY() / 32].getTile().getProperties().get("type", Integer.class).equals(2)) {
				shapeRenderer.setColor(new Color(1, 0, 0, 0.3f));
				shapeRenderer.rect(rectPlates.get("actionPlateWest").getX(), rectPlates.get("actionPlateWest").getY(), rectPlates.get("actionPlateWest").getWidth(), rectPlates.get("actionPlateWest").getHeight());
			}
			// if north is grass
			if (tiledMapCreator.mapTable[(int) rectPlates.get("actionPlateNorth").getX() / 32][(int) rectPlates.get("actionPlateNorth").getY() / 32].getTile().getProperties().get("type", Integer.class).equals(1)) {
				shapeRenderer.setColor(new Color(0, 0, 1, 0.3f));
				shapeRenderer.rect(rectPlates.get("actionPlateNorth").getX(), rectPlates.get("actionPlateNorth").getY(), rectPlates.get("actionPlateNorth").getWidth(), rectPlates.get("actionPlateNorth").getHeight());
			}
			// if north is stone
			else if (tiledMapCreator.mapTable[(int) rectPlates.get("actionPlateNorth").getX() / 32][(int) rectPlates.get("actionPlateNorth").getY() / 32].getTile().getProperties().get("type", Integer.class).equals(2)) {
				shapeRenderer.setColor(new Color(1, 0, 0, 0.3f));
				shapeRenderer.rect(rectPlates.get("actionPlateNorth").getX(), rectPlates.get("actionPlateNorth").getY(), rectPlates.get("actionPlateNorth").getWidth(), rectPlates.get("actionPlateNorth").getHeight());
			}
			// if south is grass
			if (tiledMapCreator.mapTable[(int) rectPlates.get("actionPlateSouth").getX() / 32][(int) rectPlates.get("actionPlateSouth").getY() / 32].getTile().getProperties().get("type", Integer.class).equals(1)) {
				shapeRenderer.setColor(new Color(0, 0, 1, 0.3f));
				shapeRenderer.rect(rectPlates.get("actionPlateSouth").getX(), rectPlates.get("actionPlateSouth").getY(), rectPlates.get("actionPlateSouth").getWidth(), rectPlates.get("actionPlateSouth").getHeight());
			}
			// if south is stone
			else if (tiledMapCreator.mapTable[(int) rectPlates.get("actionPlateSouth").getX() / 32][(int) rectPlates.get("actionPlateSouth").getY() / 32].getTile().getProperties().get("type", Integer.class).equals(2)) {
				shapeRenderer.setColor(new Color(1, 0, 0, 0.3f));
				shapeRenderer.rect(rectPlates.get("actionPlateSouth").getX(), rectPlates.get("actionPlateSouth").getY(), rectPlates.get("actionPlateSouth").getWidth(), rectPlates.get("actionPlateSouth").getHeight());
			}
			shapeRenderer.end();
			Gdx.gl.glDisable(GL30.GL_BLEND);
		}
	}

	public void render(Camera camera, TiledMapCreator tiledMapCreator, Viewport viewport) {
		this.camera = camera;
		this.tiledMapCreator = tiledMapCreator;

		// Animation happening
		stateTime += Gdx.graphics.getDeltaTime();
		if (!manager.containsTarget(playerSprite) && stateTime >= 2) {
			animationCurrent = animationSouth;
			stateTime = 0;
		}

		// Draw player and center camera on player
		playerBatch.begin();
		manager.update(Gdx.graphics.getDeltaTime());
		camera.position.set(position.x + playerSprite.getWidth() / 2, position.y + playerSprite.getHeight() / 2, position.z);
		camera.update();
		playerBatch.draw(animationCurrent.getKeyFrame(stateTime, true), position.x, position.y, dimensions.x, dimensions.y);
		playerBatch.setProjectionMatrix(camera.combined);
		update(tiledMapCreator, viewport);
		playerBatch.end();

		// Action process timer
		fontBatch.begin();
		fontBatch.setProjectionMatrix(camera.combined);
		if (transformTimer.z != 0) {
			GlyphLayout glyphLayout = new GlyphLayout();
			String text = (int) transformTimer.z * 100 / 1000 + "%";
			glyphLayout.setText(font, text);
			font.draw(fontBatch, glyphLayout, transformTimer.x - (glyphLayout.width / 2), transformTimer.y + (glyphLayout.height / 2));
		}
		fontBatch.end();

		// Debug rectangles for movement
		// shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		// shapeRenderer.rect(rectPlates.get("eastPlate").getX(),
		// rectPlates.get("eastPlate").getY(),
		// rectPlates.get("eastPlate").getWidth(),
		// rectPlates.get("eastPlate").getHeight());
		// shapeRenderer.rect(rectPlates.get("westPlate").getX(),
		// rectPlates.get("westPlate").getY(),
		// rectPlates.get("westPlate").getWidth(),
		// rectPlates.get("westPlate").getHeight());
		// shapeRenderer.rect(rectPlates.get("northPlate").getX(),
		// rectPlates.get("northPlate").getY(),
		// rectPlates.get("northPlate").getWidth(),
		// rectPlates.get("northPlate").getHeight());
		// shapeRenderer.rect(rectPlates.get("southPlate").getX(),
		// rectPlates.get("southPlate").getY(),
		// rectPlates.get("southPlate").getWidth(),
		// rectPlates.get("southPlate").getHeight());
		// shapeRenderer.setProjectionMatrix(camera.combined);
		// shapeRenderer.end();
	}

	public void dispose() {
		playerBatch.dispose();
		fontBatch.dispose();
	}

	public void animationAction(String way) {
		// get the way for animation
		switch (way) {
		case "north":
			animationCurrent = animationNorth;
			break;
		case "south":
			animationCurrent = animationSouth;
			break;
		case "east":
			animationCurrent = animationEast;
			break;
		case "west":
			animationCurrent = animationWest;
			break;
		default:
			break;
		}
	}
}
