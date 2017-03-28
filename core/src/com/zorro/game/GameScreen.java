package com.zorro.game;

import android.database.sqlite.SQLiteDatabase;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Random;

// Tehtävää:
// GitHub tai Bitbucket
// Vaikeusaste
// Elämät
// Pisteet
// Vihollisten Tween animaatio
// JDBC/ODBC desktop clientille tietokantaan

// ENDLESS ESCAPE
// Versiohistoria:
//	0.1 - tiilikartta ruudulle valmiiksi määriteltynä
//	0.2 - tiilikartta rakennetaan proseduraalisesti
//	0.3 - pelaaja ja liikkuminen
//	0.4 - pelaajan tween animointi
//	0.5 - tiilien ominaisuudet ja törmäystunnistus
//	0.6 - vihollisten lisääminen ja liikkuminen
//	0.7 - tiilikartta hiomista ja kuva-alueen rajaaminen
//	0.8 - aktivointipaneelien lisäys ja toiminnallisuus
// 	0.9 - aktivointi ajastin, äänet, pelaajan ja vihollisen törmäys, pelin resetointi
//	1.0 - main menu, skaalaus, nappi actionit
//	1.1 - pelaajan ja vihollisten animointi
//	1.2 - SQLite (androidille) ja highscorelista
//	1.3 - highscorelist viimeistely ja ohjeet
//	1.4 - kommentointia, formaatti käyty läpi, poistettu turhia


public class GameScreen implements Screen {

	private TiledMapCreator tiledMapCreator;
	private OrthographicCamera camera;
	private SpriteBatch fontBatch;
	private InputProcessor inputAdapter;
	private Player player;
	private GridCreator gridCreator;
	private Viewport viewport;
	ArrayList<Enemy> enemyList = new ArrayList<Enemy>();

	int worldBlocksX = 20;
	int worldBlocksY = 20;
	float worldWidth = worldBlocksX * 32;
	float worldHeight = worldBlocksY * 32;

	static Sound walkSound;
	static Sound processSound;
	static Sound catchedSound;
	static Sound portalSound;

	private Texture portalImage;
	private TextureRegion[] portalAnimation;
	private Animation animation;
	float elapsedTime;
	ArrayList<Vector2> portalList = new ArrayList<Vector2>();
	Vector2 portal;
	Rectangle portalPlate;
	private GameMain gameMain;

	java.util.Date utilDateBegin = new java.util.Date();
	private long score_begin = utilDateBegin.getTime();
	private long score_end = 0;
	private SQLiteDatabase highscoreDB;

	public GameScreen(GameMain gameMain, SQLiteDatabase highscoreDB) {
		this.gameMain = gameMain;
		this.highscoreDB = highscoreDB;
	}

	public GameScreen(GameMain gameMain) {
		this.gameMain = gameMain;
	}

	@Override
	public void show() {

		// Camera and viewport
		camera = new OrthographicCamera();
		camera.setToOrtho(false, worldWidth, worldHeight);
		camera.update();
		viewport = new ExtendViewport(188, 188, camera);

		// Font
		fontBatch = new SpriteBatch();

		// TiledMap (TiledMapCreator(cellsX,cellsY,layers,cell_size))
		tiledMapCreator = new TiledMapCreator(worldBlocksX, worldBlocksY, 1, 32);

		// Player
		player = new Player(worldWidth / 2, worldHeight / 2, 32, 32);

		// Enemy
		enemyList.clear();
		for (int i = 0; i < tiledMapCreator.mapTable.length; i++) {
			for (int j = 0; j < tiledMapCreator.mapTable[i].length; j++) {

				if (tiledMapCreator.mapTable[i][j].getTile().getProperties().get("type", Integer.class).equals(3)) {
					enemyList.add(new Enemy(i * 32, j * 32, 32, 32));
					enemyList.add(new Enemy(i * 32, j * 32, 32, 32));
					enemyList.add(new Enemy(i * 32, j * 32, 32, 32));
					enemyList.add(new Enemy(i * 32, j * 32, 32, 32));
				}
			}
		}

		// Grid drawing
		gridCreator = new GridCreator(32);

		// Input processor
		inputAdapter = new InputAdapter(player, viewport, tiledMapCreator, gameMain);
		Gdx.input.setInputProcessor(inputAdapter);

		// Sounds initialise
		walkSound = Gdx.audio.newSound(Gdx.files.internal("walk.wav"));
		processSound = Gdx.audio.newSound(Gdx.files.internal("process.wav"));
		catchedSound = Gdx.audio.newSound(Gdx.files.internal("catched.wav"));
		portalSound = Gdx.audio.newSound(Gdx.files.internal("portal.wav"));

		// Escape portal animation and possible placement list
		portalImage = new Texture("portal.png");
		TextureRegion[][] tmpFrames = TextureRegion.split(portalImage, portalImage.getWidth() / 4, portalImage.getHeight());
		portalAnimation = new TextureRegion[4];
		int index = 0;
		for (int i = 0; i < 4; i++) {
			portalAnimation[index++] = tmpFrames[0][i];
		}
		animation = new Animation(0.25f, portalAnimation);

		for (int i = 0; i < tiledMapCreator.mapTable.length; i++) {
			for (int j = 0; j < tiledMapCreator.mapTable[i].length; j++) {

				if (tiledMapCreator.mapTable[i][j].getTile().getProperties().get("portal", Integer.class).equals(1)) {
					Vector2 portalVector = new Vector2();
					portalVector.x = i * 32;
					portalVector.y = j * 32;
					portalList.add(portalVector);
				}
			}
		}

		// Portal placement randomly from portalList
		Random rand = new Random();
		portal = portalList.get(rand.nextInt(portalList.size() - 1));

		portalPlate = new Rectangle();
		portalPlate.x = portal.x;
		portalPlate.y = portal.y;
		portalPlate.width = 32;
		portalPlate.height = 32;
	}

	public void resetGame() {

		// Map reset
		tiledMapCreator.reset();

		// Player reset
		player.reset(worldWidth / 2, worldHeight / 2);

		// Enemy reset
		enemyList.clear();
		for (int i = 0; i < tiledMapCreator.mapTable.length; i++) {
			for (int j = 0; j < tiledMapCreator.mapTable[i].length; j++) {

				if (tiledMapCreator.mapTable[i][j].getTile().getProperties().get("type", Integer.class).equals(3)) {
					enemyList.add(new Enemy(i * 32, j * 32, 32, 32));
					enemyList.add(new Enemy(i * 32, j * 32, 32, 32));
					enemyList.add(new Enemy(i * 32, j * 32, 32, 32));
					enemyList.add(new Enemy(i * 32, j * 32, 32, 32));
				}
			}
		}

		// Portal placement reset
		Random rand = new Random();
		portal = portalList.get(rand.nextInt(portalList.size() - 1));
		portalPlate.x = portal.x;
		portalPlate.y = portal.y;

		utilDateBegin = new java.util.Date();
		score_begin = utilDateBegin.getTime();
	}

	@Override
	public void render(float delta) {
		elapsedTime += Gdx.graphics.getDeltaTime();

		// Clear screen
		viewport.apply();
		camera.update();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Update camera and Render map
		tiledMapCreator.render(camera);

		// Render grids
		// gridCreator.render(20,20,camera);

		// Render enemy and player
		for (int e = 0; e < enemyList.size(); e++) {
			enemyList.get(e).render(camera, tiledMapCreator);
		}

		player.render(camera, tiledMapCreator, viewport);

		// Render portal
		fontBatch.begin();
		fontBatch.setProjectionMatrix(camera.combined);
		fontBatch.draw(animation.getKeyFrame(elapsedTime, true), portal.x, portal.y, 32, 32);
		fontBatch.end();

		// Collision detection END STATE
		for (int e = 0; e < enemyList.size(); e++) {
			if (!player.manager.containsTarget(player.playerSprite) && enemyList.get(e).plate.overlaps(player.rectPlates.get("playerDetectionPlate"))) {
				catchedSound.play();

				if (Gdx.app.getType() == ApplicationType.Android) {
					saveScore();
				}
				resetGame();
			}
		}
		if (!player.manager.containsTarget(player.playerSprite) && portalPlate.overlaps(player.rectPlates.get("playerDetectionPlate"))) {
			portalSound.play();

			if (Gdx.app.getType() == ApplicationType.Android) {
				saveScore();
			}
			gameMain.setScreen(gameMain.gameMenuScreen);
		}
	}

	private void saveScore() {
		// make a temporary score object and save it's values into Database
		java.util.Date utilDateEnd = new java.util.Date();
		score_end = utilDateEnd.getTime();
		Score score = new Score((score_end - score_begin) / 1000, utilDateEnd);
		String sql = "insert into highscores (createdAt,score) values (datetime('now', 'localtime')," + score.getScore() + ")";
		highscoreDB.execSQL(sql);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void pause() {
		dispose();
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		if (enemyList.get(0) != null) {
			for (int e = 0; e < enemyList.size(); e++) {
				enemyList.get(e).dispose();
			}
		}
		player.dispose();
		gridCreator.dispose();
		fontBatch.dispose();
		walkSound.dispose();
		processSound.dispose();
		catchedSound.dispose();
	}
}
