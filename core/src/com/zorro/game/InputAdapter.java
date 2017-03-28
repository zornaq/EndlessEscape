package com.zorro.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class InputAdapter implements InputProcessor {

	Player player;
	Viewport viewport;
	TiledMapCreator tiledMapCreator;
	private GameMain gameMain;

	public InputAdapter(Player player, Viewport viewport, TiledMapCreator tiledMapCreator, GameMain gameMain) {
		this.gameMain = gameMain;
		this.player = player;
		this.viewport = viewport;
		this.tiledMapCreator = tiledMapCreator;
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Keys.BACK:
			gameMain.setScreen(gameMain.gameMenuScreen);
			break;

		case Keys.ESCAPE:
			gameMain.setScreen(gameMain.gameMenuScreen);
			break;

		case Keys.W:
			break;

		case Keys.S:
			break;

		case Keys.A:
			break;

		case Keys.D:
			break;

		default:
			return false;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// get coordinates from the touch event and translate it to viewport coordinates
		Vector3 coords = new Vector3();
		coords.x = screenX;
		coords.y = screenY;
		coords = viewport.unproject(coords);

		// get the pressed way for movement and activate actionpress
		int way = 0;
		if (player.rectPlates.get("eastPlate").contains(coords.x, coords.y)) {
			way = 2;
		} else if (player.rectPlates.get("westPlate").contains(coords.x, coords.y)) {
			way = 4;
		} else if (player.rectPlates.get("northPlate").contains(coords.x, coords.y)) {
			way = 1;
		} else if (player.rectPlates.get("southPlate").contains(coords.x, coords.y)) {
			way = 3;
		} else if (player.rectPlates.get("actionPlate").contains(coords.x, coords.y)) {
			way = 0;
			player.actionPressed = true;
		}

		// tween animation move the player to new location
		if (!player.manager.containsTarget(player.playerSprite) && tiledMapCreator.checkMapIfMoveOk(player.position, way)) {
			switch (way) {
			case 1:
				Tween.to(player.playerSprite, SpriteTween.POSITION_Y, 0.15f).target(player.position.y + player.velocity).start(player.manager);
				player.animationAction("north");
				GameScreen.walkSound.play(0.5f);
				break;
			case 2:
				Tween.to(player.playerSprite, SpriteTween.POSITION_X, 0.15f).target(player.position.x + player.velocity).start(player.manager);
				player.animationAction("east");
				GameScreen.walkSound.play(0.5f);
				break;
			case 3:
				Tween.to(player.playerSprite, SpriteTween.POSITION_Y, 0.15f).target(player.position.y - player.velocity).start(player.manager);
				player.animationAction("south");
				GameScreen.walkSound.play(0.5f);
				break;
			case 4:
				Tween.to(player.playerSprite, SpriteTween.POSITION_X, 0.15f).target(player.position.x - player.velocity).start(player.manager);
				player.animationAction("west");
				GameScreen.walkSound.play(0.5f);
				break;
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// get coordinates from the touch event and translate it to viewport coordinates
		Vector3 coords = new Vector3();
		coords.x = screenX;
		coords.y = screenY;
		coords = viewport.unproject(coords);

		// do actions if the actionplate is active
		if (player.actionPressed) {
			if (player.rectPlates.get("actionPlateEast").contains(coords.x, coords.y)) {
				player.processActionPress("actionPlateEast");
			} else if (player.rectPlates.get("actionPlateWest").contains(coords.x, coords.y)) {
				player.processActionPress("actionPlateWest");
			} else if (player.rectPlates.get("actionPlateNorth").contains(coords.x, coords.y)) {
				player.processActionPress("actionPlateNorth");
			} else if (player.rectPlates.get("actionPlateSouth").contains(coords.x, coords.y)) {
				player.processActionPress("actionPlateSouth");
			}
		}
		player.actionPressed = false;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}
}
