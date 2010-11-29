package com.matthewrathbone.cityblock;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.view.MotionEvent;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Block extends AnimatedSprite{
	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
	public Body body;
	public PhysicsConnector connector;
	public boolean shouldUpdate = false;
	public boolean updatedone = false;
	public boolean touchDown = false;
	public float newX = 0;
	public float newY = 0;
	public int touching = 0;
	
	private int baseDim = 32;
	private int gameHeight;
	private int gameWidth;
	private Text mWidthText;
	private Text mHeightText;
	
	public double getArea(){
		return gameHeight*(double)gameWidth;
	}
	
	public Block(float pX, float pY, int scale,
			TiledTextureRegion pTiledTextureRegion) {
		super(pX, pY, pTiledTextureRegion);
		this.setScale(scale);
		gameHeight = gameWidth = scale;
		mWidthText = new Text(0, 0, CityBlock.blockFont, String.valueOf(gameWidth));
		mHeightText = new Text(0, 0, CityBlock.blockFont, String.valueOf(gameHeight));
		
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		//I don't actually update the position of the shape here cus it screws with the physics engine if I do it here.
		this.shouldUpdate = true;
		this.newX = pSceneTouchEvent.getX();
		this.newY = pSceneTouchEvent.getY();
		if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN){
			this.touchDown = true;
		}
		
		if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP) {
			this.updatedone = true;
			shouldUpdate = false;
			
		}
		
		return true;
	}
	
	public void setup(){
		this.animate(200);
		this.setUpdatePhysics(false);
		setupBody();
	}
	public void setupBody(){
		Body body = PhysicsFactory.createBoxBody(CityBlock.physics, this, BodyType.DynamicBody,FIXTURE_DEF );
		this.body = body;
		this.connector = new PhysicsConnector(this, body, true, true, false, false);
		CityBlock.physics.registerPhysicsConnector(this.connector);	
	}
	
	public void updateBody(){
		if(newY <= CityBlock.CAMERA_HEIGHT - 62){
			//this.body.setType(BodyType.DynamicBody);
		}else{
			//this.body.setType(BodyType.StaticBody);
		}
	}
	
	public void addText(){
		CityBlock.scene.getTopLayer().addEntity(mHeightText);
		CityBlock.scene.getTopLayer().addEntity(mWidthText);
	}
	public void removeText(){
		CityBlock.scene.getTopLayer().removeEntity(mHeightText);
		CityBlock.scene.getTopLayer().removeEntity(mWidthText);
	}
	
	public void clearPhysics(){
		Block b = this;
		if(b.connector != null) CityBlock.physics.unregisterPhysicsConnector(b.connector);
		if(b.body != null) CityBlock.physics.destroyBody(b.body);
		b.body = null;
		b.connector = null;
	}
	public void updatePosition(){
		Block b = this;
		
		float height = b.getHeight() * b.getScaleY();
		float width = b.getWidth() * b.getScaleX();
		mHeightText.setPosition(this.getX() - (width/2 + 10), this.getY() + this.getHeight()/2 - mHeightText.getHeight()/2);
		mWidthText.setPosition(this.getX() + this.getWidth()/2 - mWidthText.getWidth()/2 , this.getY() - (height/2 + 20));
		
		b.setPosition((float)(b.newX - width/2), b.newY - height);
	}
	

	

}

