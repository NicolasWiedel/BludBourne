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

import de.javadevblog.bludbourne.Entity;
import de.javadevblog.bludbourne.MapManager;
import de.javadevblog.bludbourne.PlayerController;

public class MainGameScreen implements Screen{
	public static final String TAG = MainGameScreen.class.getSimpleName();
	
	private PlayerController controller;
	private TextureRegion currentPlayerFrame;
	private Sprite currentPlayerSprite;
	
	private OrthogonalTiledMapRenderer mapRenderer = null;
	private OrthographicCamera camera = null;
	private static MapManager mapMgr;
	
	private static Entity player;
	
	public MainGameScreen() {
		mapMgr = new MapManager();
	}
	
	@Override
	public void show() {
//		Kamera Setup
		setupViewport(10, 10);
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);
		
		mapRenderer = new OrthogonalTiledMapRenderer(mapMgr.getCurrentMap(), MapManager.UNIT_SCALE);
		mapRenderer.setView(camera);
		
		player = new Entity();
		player.init(mapMgr.getPlayerStartUnitScaled().x, mapMgr.getPlayerStartUnitScaled().y);
		
		currentPlayerSprite = player.getFrameSprite();
		
		controller = new PlayerCorntroller(player);
		Gdx.input.setInputProcessor(controller);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(100/255.0f, 149/255.0f, 237/255.0f, 255/255.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
//		Kamera auf den Player zeigen lassen
		camera.position.set(currentPlayerSprite.getX(), currentPlayerSprite.getY(), 0);
		camera.update();
		
		player.update(delta);
		currentPlayerFrame = player.getFrame();
		
		updatePortalLayerActivation(player.boundingBox);
		
		if(!isCollisionWithMapLayer(player.boundingBox)) {
			player.setNextPositionToCurrent();
		}
		controller.update(delta);
		
		mapRenderer.setView(camera);
		mapRenderer.render();
		
		mapRenderer.getBatch().begin();
		mapRenderer.getBatch().draw(currentPlayerFrame, currentPlayerSprite.getX(), currentPlayerSprite.getY(), 1, 1);
		mapRenderer.getBatch().end();
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
		controller.dispose();
		Gdx.input.setInputProcessor(null);
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
	
	private boolean isCollisionWithMapLayer(Rectangle boundingBox) {
		MapLayer mapCollisionLayer = mapMgr.getCollisionLayer();
		
		if(mapCollisionLayer == null) {
			return false;
		}
		
		Rectangle rectangle = null;
		
		for(MapObject object : mapCollisionLayer.getObjects()) {
			if(object instanceof RectangleMapObject) {
				rectangle =((RectangleMapObject)object).getRectangle();
				if(boundingBox.overlaps(rectangle)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean updatePortalLayerActivation(Rectangle boundingBox) {
		MapLayer mapPortalLayer = mapMgr.getPortalLayer();
		
		if(mapPortalLayer == null) {
			return false;
		}
		
		Rectangle rectangle = null;
		
		for(MapObject object : mapPortalLayer.getObjects()) {
			if(object instanceof RectangleMapObject) {
				rectangle = ((RectangleMapObject)object).getRectangle();
				if(boundingBox.overlaps(rectangle)) {
					String mapName = object.getName();
					if(mapName == null) {
						return false;
					}
					mapMgr.setClosestStartPositionFromScaleUnits(player.getCurrentPosition());
					mapMgr.loadMap(mapName);
					player.init(mapMgr.getPlayerStartUnitScaled().x, mapMgr.getPlayerStartUnitScaled().y);
					mapRenderer.setMap(mapMgr.getCurrentMap());
					Gdx.app.debug(TAG, "Portal aktiviert!");
					return true;
				}
			}	
		}
		return false;
	}
	
	public static class VIEWPORT {
		static float viewportWidth;
		static float viewportHeight;
		static float virtualWidth;
		static float virtualHeight;
		static float physicalWidth;
		static float physicalHeight;
		static float aspectRatio;
	}
}
