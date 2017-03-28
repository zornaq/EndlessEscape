package com.zorro.game;

import java.util.Random;

//import aurelienribon.tweenengine.Tween;
//import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Enemy {

	private Sprite enemySprite;
	private SpriteBatch enemyBatch;

	private Vector3 position;
	private Vector2 dimensions;
	private float velocity = 32f;

	private static final int col = 4;
	private static final int row = 4;

	Animation animationNorth;
	Animation animationSouth;
	Animation animationWest;
	Animation animationEast;
	Animation animationCurrent;
	Texture enemyTexture;
	TextureRegion[] framesNorth;
	TextureRegion[] framesSouth;
	TextureRegion[] framesEast;
	TextureRegion[] framesWest;
	float stateTime;
//	private TweenManager manager;
	int timer = 0;
	Random rand = new Random();
	int way;
	Rectangle plate;

	public Enemy(float x, float y, float width, float height) {
		position = new Vector3(x, y, 0);
		dimensions = new Vector2(width, height);
		createRectPlateAtXY(x, y, width, height);

		enemySprite = new Sprite(new Texture(Gdx.files.internal("badlogic.jpg")));
		enemySprite.setSize(dimensions.x, dimensions.y);

		enemyBatch = new SpriteBatch();

		way = rand.nextInt(4) + 1;

		//animation
		enemyTexture = new Texture(Gdx.files.internal("enemy.png"));
		TextureRegion[][] tmp = TextureRegion.split(enemyTexture, enemyTexture.getWidth() / col, enemyTexture.getHeight() / row);
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

		// Tween.registerAccessor(Sprite.class, new EnemySpriteTween(position));
		// manager = new TweenManager();
	}

	private void createRectPlateAtXY(float f, float g, float i, float j) {
		plate = new Rectangle();
		plate.x = f;
		plate.y = g;
		plate.width = i;
		plate.height = j;
	}

	public void render(Camera camera, TiledMapCreator tiledMapCreator) {
		//do animation
		stateTime += Gdx.graphics.getDeltaTime();
		if (stateTime >= 2) {
			animationCurrent = animationSouth;
			stateTime = 0;
		}

		update(tiledMapCreator);
		// enemySprite.setPosition(position.x, position.y);

		enemyBatch.begin();
		// manager.update(Gdx.graphics.getDeltaTime());
		enemyBatch.draw(animationCurrent.getKeyFrame(stateTime, true), position.x, position.y, dimensions.x, dimensions.y);
		enemyBatch.setProjectionMatrix(camera.combined);
		enemyBatch.end();

	}

	private void update(TiledMapCreator tiledMapCreator) {
		// if(!manager.containsTarget(enemySprite) &&
		// tiledMapCreator.checkMapIfMoveOk(position, rand.nextInt(3+1))){
		// Tween.to(enemySprite,EnemySpriteTween.POSITION_X,0.15f).target(position.x-velocity).start(manager);

		// east west north south

		if (timer >= 30) {
			boolean loop = true;
			int exit = 0;
			while (loop) {
				if (tiledMapCreator.checkMapIfMoveOk(position, way)) {
					switch (way) {
					case 1:
						position.y = position.y + velocity;
						plate.x = position.x;
						plate.y = position.y;
						animationAction("north");
						// Tween.to(enemySprite,SpriteTween.POSITION_Y,0.15f).target(position.y+velocity).start(manager);
						break;
					case 2:
						position.x = position.x + velocity;
						plate.x = position.x;
						plate.y = position.y;
						animationAction("east");
						// Tween.to(enemySprite,SpriteTween.POSITION_X,0.15f).target(position.x+velocity).start(manager);
						break;
					case 3:
						position.y = position.y - velocity;
						plate.x = position.x;
						plate.y = position.y;
						animationAction("south");
						// Tween.to(enemySprite,SpriteTween.POSITION_Y,0.15f).target(position.y-velocity).start(manager);
						break;
					case 4:
						position.x = position.x - velocity;
						plate.x = position.x;
						plate.y = position.y;
						animationAction("west");
						// Tween.to(enemySprite,SpriteTween.POSITION_X,0.15f).target(position.x-velocity).start(manager);
						break;
					}
					loop = false;
				}
				if (exit <= 10) {
					way = rand.nextInt(4) + 1;
					exit++;
				} else {
					loop = false;
				}
			}
			timer = 0;
		}
		timer++;
	}

	public void dispose() {
		enemyBatch.dispose();
	}

	public void animationAction(String way) {
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
