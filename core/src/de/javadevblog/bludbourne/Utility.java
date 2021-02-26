package de.javadevblog.bludbourne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Utilityklasse zum laden der Assets
 * @author Nicolas
 *
 */
public class Utility {

	private static final String TAG = Utility.class.getSimpleName();

	public static final AssetManager ASSET_MANAGER = new AssetManager();

	private static final InternalFileHandleResolver FILE_PATH_RESOLVER = new InternalFileHandleResolver();

	public static void unloadAsset(String assetFilenamePath) {

		if (ASSET_MANAGER.isLoaded(assetFilenamePath)) {
			ASSET_MANAGER.unload(assetFilenamePath);
		} else {
			Gdx.app.debug(TAG, "Asset kann nicht geladen werden! Nichts zum Unload auf : " + assetFilenamePath);
		}
	}

	public static float loadCompletes() {
		return ASSET_MANAGER.getProgress();
	}

	public static int numberOfAssetsQueued() {
		return ASSET_MANAGER.getQueuedAssets();
	}

	public static boolean updateAssetLoading() {
		return ASSET_MANAGER.update();
	}

	public static boolean isAssetLoaded(String fileName) {
		return ASSET_MANAGER.isLoaded(fileName);
	}

	public static void LoadMapAsset(String mapFilenamPath) {
		if (mapFilenamPath == null || mapFilenamPath.isEmpty()) {
			return;
		}

//		assets laden
		if (FILE_PATH_RESOLVER.resolve(mapFilenamPath).exists()) {
			ASSET_MANAGER.setLoader(TiledMap.class, new TmxMapLoader(FILE_PATH_RESOLVER));

			ASSET_MANAGER.load(mapFilenamPath, TiledMap.class);
			ASSET_MANAGER.finishLoadingAsset(mapFilenamPath);
		} else {
			Gdx.app.debug(TAG, "Die Map existiert nicht: " + mapFilenamPath);
		}
	}

	public static TiledMap getMapAsset(String mapFilenamePath) {
		TiledMap map = null;
		
//		wenn der AssetManager zu Ende geladen hat
		if(ASSET_MANAGER.isLoaded(mapFilenamePath)) {
			map = ASSET_MANAGER.get(mapFilenamePath, TiledMap.class);
		}
		else {
			Gdx.app.debug(TAG, "Die Map wurde nicht geladen: " + mapFilenamePath);
		}
		return map;
	}
	
	public static void loadTextureAsset(String textureFilePath) {
		if(textureFilePath == null || textureFilePath.isEmpty()) {
			return;
		}
//		asset laden
		if(FILE_PATH_RESOLVER.resolve(textureFilePath).exists()) {
			ASSET_MANAGER.setLoader(Texture.class, new TextureLoader(FILE_PATH_RESOLVER));
			
			ASSET_MANAGER.load(textureFilePath, Texture.class);
//			Bis wir einen LoadingScreen implementiert haben
//			warten, bis die Texture geladen ist
			ASSET_MANAGER.finishLoadingAsset(textureFilePath);
		}
		else {
			Gdx.app.debug(TAG, "Die Texture existiertnicht: " + textureFilePath);
		}
	}
	
	public static Texture getTextureAsset(String textureFileNamePath) {
		Texture texture = null;
		
		if(ASSET_MANAGER.isLoaded(textureFileNamePath, Texture.class)) {
			ASSET_MANAGER.get(textureFileNamePath, Texture.class);
		}
		else {
			Gdx.app.debug(TAG, "Die Texture wurde nicht geladen: " + textureFileNamePath);
		}
		return texture;
	}
}
