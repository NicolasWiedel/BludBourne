package de.javadevblog.bludbourne.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;

import de.javadevblog.bludbourne.Component;
import de.javadevblog.bludbourne.Entity;
import de.javadevblog.bludbourne.EntityFactory;
import de.javadevblog.bludbourne.Map;
import de.javadevblog.bludbourne.MapManager;

public class MainGameScreen implements Screen{
	public static final String TAG = MainGameScreen.class.getSimpleName();
	
	private Entity player;
	private OrthogonalTiledMapRenderer mapRenderer = null;
	private OrthographicCamera camera = null;
	private static MapManager mapMgr;
	private Json json;
	
	private static class VIEWPORT {
		static float viewportWidth;
		static float viewportHeight;
		static float virtualWidth;
		static float virtualHeight;
		static float physicalWidth;
		static float physicalHeight;
		static float aspectRatio;
	}
	
	public MainGameScreen() {
		mapMgr = new MapManager();
		json = new Json();
	}
	
	@Override
	public void show() {
//		Kamera Setup
		setupViewport(10, 10);
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);
		
		mapRenderer = new OrthogonalTiledMapRenderer(mapMgr.getCurrentTiledMap(), Map.UNIT_SCALE);
		mapRenderer.setView(camera);
		
		mapMgr.setCamera(camera);

		Gdx.app.debug(TAG, "UnitScale value is: " + mapRenderer.getUnitScale());

		player = EntityFactory.getEntity(EntityFactory.EntityType.PLAYER);
		mapMgr.setPlayer(player);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(100/255.0f, 149/255.0f, 237/255.0f, 255/255.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		mapRenderer.setView(camera);
		
		if( mapMgr.hasMapChanged() ){
			mapRenderer.setMap(mapMgr.getCurrentTiledMap());
			player.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(mapMgr.getPlayerStartUnitScaled()));

			camera.position.set(mapMgr.getPlayerStartUnitScaled().x, mapMgr.getPlayerStartUnitScaled().y, 0f);
			camera.update();

			mapMgr.setMapChanged(false);
		}

		mapRenderer.render();

		mapMgr.updateCurrentMapEntities(mapMgr, mapRenderer.getBatch(), delta );

		player.update(mapMgr, mapRenderer.getBatch(), delta);
	}
	
	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void dispose() {
		player.dispose();
		mapRenderer.dispose();
	}
	
	private void setupViewport(int width, int height) {
//		Einstellung des Vieports auf einen Anteil am gesamten Bildschirm
		VIEWPORT.virtualWidth = width;
		VIEWPORT.virtualHeight = height;
		
//		Viewport Dimension
		VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
		VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
		
//		Dimension des Bildschirms in Pixeln
		VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
		VIEWPORT.physicalHeight = Gdx.graphics.getHeight();
		
//		Verhältnis des aktuellen Viewports
		VIEWPORT.aspectRatio = VIEWPORT.virtualWidth / VIEWPORT.virtualHeight;
		
//		Anpassen an Bildschirm
		if(VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= VIEWPORT.aspectRatio) {
			VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth / VIEWPORT.physicalHeight);
			VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
		}
		else {
			VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
			VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalHeight / VIEWPORT.physicalWidth);
		}
		
		Gdx.app.debug(TAG, "WorldRenderer: virtuel: (" + VIEWPORT.virtualWidth + "," + VIEWPORT.virtualHeight + ")");
		Gdx.app.debug(TAG, "WorldRenderer: viewport: (" + VIEWPORT.viewportWidth + "," + VIEWPORT.viewportHeight + ")");
		Gdx.app.debug(TAG, "WorldRenderer: physical: (" + VIEWPORT.physicalWidth + "," + VIEWPORT.physicalHeight + ")");
	}
}
