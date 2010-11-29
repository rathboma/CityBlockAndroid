package com.matthewrathbone.cityblock;

import java.util.ArrayList;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.HorizontalAlign;

import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;




public class CityBlock extends BaseGameActivity {

	public static ArrayList<Block> activeBlocks = new ArrayList<Block>();
	public static PhysicsWorld physics = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
	public static Engine engine;
	public static Scene scene;
	public static Font blockFont;
	
	public class BoxUpdater implements IUpdateHandler{
		
		public void onUpdate(float pSecondsElapsed) {
			for (Block b : CityBlock.activeBlocks) {
				
				if(b.touchDown){
					b.touchDown = false;
					b.addText();
				}
				if(b.shouldUpdate){
					b.clearPhysics();
					b.updatePosition();	
				}
				if(b.updatedone){
					b.removeText();
					b.updatedone = false;
					b.setupBody();
				}
				
			}
			
		}

		public void reset() {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
	
	private Texture mTexture;
	private TiledTextureRegion mTextureRegion;
	private PhysicsWorld mPhysics;
	private Engine mEngine;
	private Camera mCamera;
	public static int CAMERA_HEIGHT = 720;
	public static int CAMERA_WIDTH = 480;
	private Texture mFontTexture;
	private Font mFont;
	
	
	public Engine onLoadEngine() {
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return CityBlock.engine = mEngine = new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera));
	}

	public void onLoadResources() {
		this.mTexture = new Texture(64, 32);
		TextureRegionFactory.setAssetBasePath("gfx/");
		this.mTextureRegion = TextureRegionFactory.createTiledFromAsset(mTexture, this, "face_box_tiled.png",0, 0, 2, 1);
		mEngine.getTextureManager().loadTexture(mTexture);
		this.mFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		this.mFont = CityBlock.blockFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);

		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);

		
	}

	public Scene onLoadScene() {
		CityBlock.scene = new Scene(1);
		this.mEngine.registerUpdateHandler(new FPSLogger());
		this.mPhysics = CityBlock.physics;
		
		
		//replace this with a iteration over the physics.getContactList() using the boxUpdater class above.
		CityBlock.physics.setContactListener(new ContactListener(){

			public void beginContact(Contact contact) {
				
				Body a = contact.getFixtureA().getBody();
				Body b = contact.getFixtureB().getBody();
				for(Block blk : CityBlock.activeBlocks){
					if(blk.body == a || blk.body == b) {
						blk.setColor(0, 0, 1);
						blk.touching++;
					}
				}
				
				
				
			}

			public void endContact(Contact contact) {
				Body a = contact.getFixtureA().getBody();
				Body b = contact.getFixtureB().getBody();
				for(Block blk : CityBlock.activeBlocks){
					if(blk.body == a || blk.body == b) {
						blk.touching--;
						if(blk.touching <= 0) blk.setColor(1, 1, 1);
					}
				}
				
			}
			
			
		});
		
		
		
		Block face = new Block(CAMERA_WIDTH / 2 - 16, CAMERA_HEIGHT - 100, 1, this.mTextureRegion);
		Block face2 = new Block(0, 0, 3, this.mTextureRegion);
		CityBlock.activeBlocks.add(face);
		CityBlock.activeBlocks.add(face2);
		
		

		
		Shape ground = new Rectangle(0, CAMERA_HEIGHT - 62, CAMERA_WIDTH, 2);
		final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
		final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
		final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);



		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(mPhysics, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysics, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysics, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysics, right, BodyType.StaticBody, wallFixtureDef);

		scene.getBottomLayer().addEntity(ground);
		scene.getBottomLayer().addEntity(roof);
		scene.getBottomLayer().addEntity(left);
		scene.getBottomLayer().addEntity(right);
		
		final Text textCenter = new Text(100, 60, this.mFont, "Hello AndEngine!\nYou can even have multilined text!", HorizontalAlign.CENTER);
		scene.getTopLayer().addEntity(textCenter);
		for (Block b : CityBlock.activeBlocks) {
			b.setup();
			scene.getTopLayer().addEntity(b);
			scene.registerTouchArea(b);	
		}
		
		
		//this.mPhysics.registerPhysicsConnector(face.connector);
		
		scene.registerUpdateHandler(this.mPhysics);
		
		scene.setTouchAreaBindingEnabled(true);
		scene.registerUpdateHandler(new BoxUpdater());
		
		return scene;
	}

	public void onLoadComplete() {
		// TODO Auto-generated method stub
		
	}


}