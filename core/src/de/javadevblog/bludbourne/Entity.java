package de.javadevblog.bludbourne;

import java.util.ArrayList;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Entity {
	private static final String TAG = Entity.class.getSimpleName();
	
	private Json json;
	private EntityConfig entityConfig;
	
	public enum Direction {
		UP,
		RIGHT,
		DOWN, 
		LEFT;
		
		public static Direction getRandomNext() {
			return Direction.values()[MathUtils.random(Direction.values().length-1)];
		}
		
		public Direction getOposite() {
			if (this == LEFT) {
				return RIGHT;
			}
			else if (this == RIGHT) {
				return LEFT;
			}
			else if (this == UP) {
				return DOWN;
			}
			else return UP;
		}
	}
	
	public enum State {
		IDLE, 
		WALKING,
		
		IMMOBILE; // Sollte immer das letzte sein
		
		public static State getRandomNext() {
			// Immobile ignorieren
			return State.values()[MathUtils.random(State.values().length - 2)];
		}
	}
	
	public static enum AnimationType {
		WALK_LEFT,
		WALK_RIGHT,
		WALK_UP,
		WALK_DOWN,
		IDLE,
		IMMOBILE;
	}
	
	public static final int FRAME_WIDTH = 16;
	public static final int FRAME_HEIGHT = 16;
	
	private static final int MAX_COMPONENTS = 5;
	private Array<Component> components;
	
	private InputComponent inputComponent;
	private GraphicsComponent graphicsComponent;
	private PhysicsComponent physicsComponent;
	
	public Entity(
			InputComponent inputComponent,
			PhysicsComponent physicsComponent,
			GraphicsComponent graphicsComponent) {
		
		entityConfig = new EntityConfig();
		json = new Json();
		
		components = new Array<Component>(MAX_COMPONENTS);
		
		this.inputComponent = inputComponent;
		this.physicsComponent = physicsComponent;
		this.graphicsComponent = graphicsComponent;
	}
	
	public EntityConfig getEntityConfig() {
		return entityConfig;
	}
	
	public void sendMessage(Component.MESSAGE messageType, String ... args) {
		
		String fullMessage = messageType.toString();
		
		for(String string : args) {
			fullMessage += Component.MESSAGE_TOKEN + string;
		}
		
		for(Component component : components) {
			component.receiveMessage(fullMessage);
		}
	}
	
	public void update(MapManager mapMgr, Batch batch, float delta) {
		this.inputComponent.update(this, delta);
		this.physicsComponent.update(this, mapMgr, delta);
		this.graphicsComponent.update(this, mapMgr, batch, delta);
	}
	
	public void dispose() {
		for (Component component : components) {
			component.dispose();
		}
	}
	
	public Rectangle getCurrentBoundingBox() {
		return physicsComponent.boundingBox;
	}
	
	public void setEntityConfig(EntityConfig entityConfig) {
		this.entityConfig = entityConfig;
	}
	
	public static EntityConfig getEntityConfig(String configFilePath) {
		Json json = new Json();
		return json.fromJson(EntityConfig.class, Gdx.files.internal(configFilePath));			
	}
	
	public static Array<EntityConfig> getEntityConfigs(String configFilePath){
		Json json = new Json();
		Array<EntityConfig> configs = new Array<EntityConfig>();
		
		ArrayList<JsonValue> list = json.fromJson(ArrayList.class, 
				Gdx.files.internal(configFilePath));
		
		for(JsonValue jsonVal : list){
			configs.add(json.readValue(EntityConfig.class, jsonVal));
		}
		
		return configs;
	}	
}
