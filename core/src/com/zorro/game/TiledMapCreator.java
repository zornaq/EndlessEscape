package com.zorro.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector3;

public class TiledMapCreator {

	private TiledMap map;
	private Texture tiles;
	TiledMapTile grassTile;
	TiledMapTile stoneTile;
	private BatchTiledMapRenderer bt;
	public Vector3 mousePress;
	public TiledMapTileLayer mapLayer;
	public Cell[][] mapTable;
	Camera camera;
	int x_tiles, y_tiles, layers, cell_size;

	public TiledMapCreator(int x_tiles, int y_tiles, int layers, int cell_size) {
		this.x_tiles = x_tiles;
		this.y_tiles = y_tiles;
		this.layers = layers;
		this.cell_size = cell_size;

		mousePress = new Vector3();

		// CreateMap and initialise
		createTiledMap();
		mapLayer = (TiledMapTileLayer) map.getLayers().get(0);
		writeMapIntoTable(mapLayer);

		// Room for player
		changeTile(320, 320, grassTile);
		changeTile(352, 320, grassTile);
		changeTile(320, 352, grassTile);
		changeTile(288, 320, grassTile);
		changeTile(320, 288, grassTile);

		bt = new OrthogonalTiledMapRenderer(map);
	}

	public void reset() {
		// CreateMap and initialise
		createTiledMap();
		mapLayer = (TiledMapTileLayer) map.getLayers().get(0);
		writeMapIntoTable(mapLayer);

		// Room for player
		changeTile(320, 320, grassTile);
		changeTile(352, 320, grassTile);
		changeTile(320, 352, grassTile);
		changeTile(288, 320, grassTile);
		changeTile(320, 288, grassTile);

		bt = new OrthogonalTiledMapRenderer(map);
	}

	public void createTiledMap() {
		tiles = new Texture(Gdx.files.internal("tiles.png"));
		TextureRegion[][] splitTiles = TextureRegion.split(tiles, this.cell_size, this.cell_size);
		grassTile = new StaticTiledMapTile(splitTiles[0][12]);
		stoneTile = new StaticTiledMapTile(splitTiles[4][0]);
		map = new TiledMap();
		MapLayers layers = map.getLayers();
		Random rand = new Random();

		// Number of layers
		for (int l = 0; l < this.layers; l++) {
			TiledMapTileLayer layer = new TiledMapTileLayer(this.x_tiles, this.y_tiles, this.cell_size, this.cell_size);
			for (int x = 0; x < this.x_tiles; x++) {
				for (int y = 0; y < this.y_tiles; y++) {

					// Type 0 walls, 1 grass, 2 stoneblock, 3 spawner

					Cell cell = new Cell();
					if (l == 0) {

						switch (rand.nextInt(2)) {
						case 0:
							cell.setTile(new StaticTiledMapTile(splitTiles[4][0]));
							cell.getTile().getProperties().put("type", 2);
							cell.getTile().getProperties().put("hp", 100);
							cell.getTile().getProperties().put("portal", 0);
							break;
						case 1:
							cell.setTile(new StaticTiledMapTile(splitTiles[0][12]));
							cell.getTile().getProperties().put("type", 1);
							cell.getTile().getProperties().put("hp", 100);
							cell.getTile().getProperties().put("portal", 0);
							break;
						}
					}
					// Set walls around map
					if (x == 0 || y == 0 || x == this.x_tiles - 1 || y == this.y_tiles - 1) {
						cell.setTile(new StaticTiledMapTile(splitTiles[0][14]));
						cell.getTile().getProperties().put("type", 0);
						cell.getTile().getProperties().put("hp", 100);
						cell.getTile().getProperties().put("portal", 0);
					}
					// Room for enemies
					if (((y == 1 || y == this.y_tiles - 2) && x >= 1 && x <= this.x_tiles - 2) || ((x == 1 || x == this.x_tiles - 2) && y >= 1 && y <= this.y_tiles - 2)) {
						cell.setTile(new StaticTiledMapTile(splitTiles[0][12]));
						cell.getTile().getProperties().put("type", 1);
						cell.getTile().getProperties().put("hp", 100);
						cell.getTile().getProperties().put("portal", 1);
					}
					// Spawn place SW & NE corners
					if (x == 1 && y == 1 || x == this.x_tiles - 2 && y == this.y_tiles - 2) {
						cell.setTile(new StaticTiledMapTile(splitTiles[7][10]));
						cell.getTile().getProperties().put("type", 3);
						cell.getTile().getProperties().put("hp", 100);
						cell.getTile().getProperties().put("portal", 0);
					}
					// Spawn place NW & SE corners
					if (x == this.x_tiles - 2 && y == 1 || x == 1 && y == this.y_tiles - 2) {
						cell.setTile(new StaticTiledMapTile(splitTiles[7][10]));
						cell.getTile().getProperties().put("type", 3);
						cell.getTile().getProperties().put("hp", 100);
						cell.getTile().getProperties().put("portal", 0);
					}
					layer.setCell(x, y, cell);
				}
			}
			layers.add(layer);
		}
	}

	public void writeMapIntoTable(TiledMapTileLayer layer) {
		// Make 2D table and set cells in it to reflect the worldmap
		mapTable = new TiledMapTileLayer.Cell[layer.getWidth()][layer.getHeight()];
		for (int x = 0; x < layer.getWidth(); x++) {
			for (int y = layer.getHeight() - 1; y >= 0; y--) {
				TiledMapTileLayer.Cell cell = layer.getCell(x, y);

				if (cell == null)
					continue; // There is no cell
				if (cell.getTile() == null)
					continue; // No tile inside cell

				mapTable[x][y] = cell;
			}
		}
	}

	public void changeTile(int x, int y, TiledMapTile tile) {
		System.out.println("ChangeTile X:" + x / 32 + " Y:" + y / 32);
		if (tile.equals(grassTile)) {
			mapTable[x / 32][y / 32].setTile(tile);
			mapTable[x / 32][y / 32].getTile().getProperties().put("type", 1);
			mapTable[x / 32][y / 32].getTile().getProperties().put("portal", 0);
		} else if (tile.equals(stoneTile)) {
			mapTable[x / 32][y / 32].setTile(tile);
			mapTable[x / 32][y / 32].getTile().getProperties().put("type", 2);
			mapTable[x / 32][y / 32].getTile().getProperties().put("portal", 0);
		}
	}

	public boolean checkMapIfMoveOk(Vector3 position, int way) {
		int player_x = (int) (position.x / 32);
		int player_y = (int) (position.y / 32);

		// check if entity can make a move in set way
		switch (way) {
		case 1: // north
			if (mapTable[player_x][player_y + 1].getTile().getProperties().get("type", Integer.class).equals(2) || mapTable[player_x][player_y + 1].getTile().getProperties().get("type", Integer.class).equals(0)) {
				return false;
			}
			break;
		case 2: // east
			if (mapTable[player_x + 1][player_y].getTile().getProperties().get("type", Integer.class).equals(2) || mapTable[player_x + 1][player_y].getTile().getProperties().get("type", Integer.class).equals(0)) {
				return false;
			}
			break;
		case 3: // south
			if (mapTable[player_x][player_y - 1].getTile().getProperties().get("type", Integer.class).equals(2) || mapTable[player_x][player_y - 1].getTile().getProperties().get("type", Integer.class).equals(0)) {
				return false;
			}
			break;
		case 4: // west
			if (mapTable[player_x - 1][player_y].getTile().getProperties().get("type", Integer.class).equals(2) || mapTable[player_x - 1][player_y].getTile().getProperties().get("type", Integer.class).equals(0)) {
				return false;
			}
			break;
		}
		return true;
	}

	public void render(Camera camera) {
		this.camera = camera;
		bt.setView(camera.combined, 0, 0, 640, 640);
		bt.render();
	}
}
