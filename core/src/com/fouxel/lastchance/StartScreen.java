package com.fouxel.lastchance;

import javax.swing.Timer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StartScreen extends AbstractScreen {
	
	private OrthographicCamera 	camera;
	private Texture splashTexture;
	private Sprite splashSprite;
	private SpriteBatch batch;
	private Timer t;
	private boolean zmien = false;
	private int index = 0;
	
	public boolean getZmien() {
		return zmien;
	}

	public void setZmien(boolean zmien) {
		this.zmien = zmien;
	}

	public StartScreen(SpriteBatch batchIn){
		super();
		batch = batchIn;
		
		camera = new OrthographicCamera(1280, 720);
		camera.position.set(0, 0, 0);
		splashTexture = new Texture("logo.png");
		splashTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		
		splashSprite = new Sprite(splashTexture);
		splashSprite.setOrigin(0,0);
		splashSprite.setPosition(-splashSprite.getWidth()/2,-splashSprite.getHeight()/2);
		
	}
	
	public void show(){
		super.show();
		
	}
	
	public void render(float delta){
		super.render(delta);
		if(index > 5)
			Sleep(1000);
		
	    camera.update();   
	    batch.setProjectionMatrix(camera.combined);
		batch.begin();
	//	batch.draw(splashTexture, -640,0);
		splashSprite.draw(batch);
		batch.end();
		index++;
		
	}
	
	public void dispose(){
		super.dispose();
		splashTexture.dispose();
		
	}
	
	private void Sleep(int mili){
		try {
			Thread.sleep(mili);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		zmien = true;
		
	}
}
