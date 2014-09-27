package com.fouxel.lastchance;

import javax.swing.Timer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class EndScreen extends AbstractScreen {
	
	private OrthographicCamera 	camera;
	private Texture splashTexture;
	private Sprite splashSprite;
	private SpriteBatch batch;
	private Timer t;
	private boolean zmien = false;
	private int index = 0;
	Skin skin;
	
	private int score = 0;
	private int highScore = 0;
	BitmapFont 			font;
	public boolean getZmien() {
		return zmien;
	}

	public void setZmien(boolean zmien) {
		this.zmien = zmien;
	}

	public EndScreen(SpriteBatch batchIn, Skin skin){
		super();
		batch = batchIn;
		this.skin = skin;
		
		camera = new OrthographicCamera(1280, 720);
		camera.position.set(0, 0, 0);

		
		
		splashSprite = skin.getSprite("gameover");
		splashSprite.setOrigin(0,0);
		splashSprite.setPosition(-splashSprite.getWidth()/2,-splashSprite.getHeight()/2);
		

        FileHandle fontFile = Gdx.files.internal("whitrabt.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);

        font = generator.generateFont(60);
        generator.dispose();
		
	}
	
	public void show(){
		super.show();
		index = 0;
		zmien = false;
	}
	
	public void render(float delta){
		super.render(delta);
		if(index > 5)
			Sleep(2500);
		
	    camera.update();   
	    batch.setProjectionMatrix(camera.combined);
		batch.begin();
		splashSprite.draw(batch);
		
		font.setColor(1.0f, 0.64f, 0.0f, 1.0f);
		font.draw(batch, "score: "+ score, -140, -220); 
		font.draw(batch, "highscore: "+ highScore, -200, 250); 

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

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getHighScore() {
		return highScore;
	}

	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}


}
