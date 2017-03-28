package com.zorro.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GridCreator {

	private ShapeRenderer shapeRenderer;

	private int GRID_CELL;

	public GridCreator(int x) {
		this.GRID_CELL = x;
		shapeRenderer = new ShapeRenderer();
	}

	public void render(int width_cells, int height_cells, Camera camera) {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		for (int x = 0; x < width_cells * GRID_CELL; x += this.GRID_CELL) {
			for (int y = 0; y < height_cells * GRID_CELL; y += this.GRID_CELL) {
				shapeRenderer.rect(x, y, this.GRID_CELL, this.GRID_CELL);
			}
		}
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.end();
	}

	public void dispose() {
		shapeRenderer.dispose();
	}

}
