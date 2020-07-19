package net.sr89.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class VoltimeterStart extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture image;
	private Array<Vector2> stars;
	private ShapeRenderer shapeRenderer;

	@Override
	public void create() {
		batch = new SpriteBatch();
		image = new Texture("badlogic.png");
		shapeRenderer = new ShapeRenderer();
		stars = new StarSupplier(0.01F).initStars();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
		for (int i = 0; i < stars.size; i++) {
			Vector2 star = stars.get(i);
			shapeRenderer.point(star.x * width, star.y * height, 0);
		}
		shapeRenderer.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		image.dispose();
		shapeRenderer.dispose();
	}
}