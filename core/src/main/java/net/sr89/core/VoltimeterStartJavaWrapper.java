package net.sr89.core;

import com.badlogic.gdx.ApplicationAdapter;

public class VoltimeterStartJavaWrapper extends ApplicationAdapter {
	ApplicationAdapter voltimeterStart = new VoltimeterStart();

	@Override
	public void create() {
		voltimeterStart.create();
	}

	@Override
	public void render() {
		voltimeterStart.render();
	}

	@Override
	public void dispose() {
		voltimeterStart.dispose();
	}
}