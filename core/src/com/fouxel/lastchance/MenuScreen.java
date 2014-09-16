package com.fouxel.lastchance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


/*
 * W tej klasie zarzadzac flaga(sprawdzanie jej wartosci w render())
 * potem wysy³anie sygna³u do klasy MyGame*/

/*
 * Dodaæ viewport zeby bylo skalowanie.
 * 
 * */
public class MenuScreen extends AbstractScreen{
		private OrthographicCamera 	camera;
		private Stage stage;
		TextButton button;
	    TextButtonStyle textButtonStyle;
	    BitmapFont font;
	    Skin skin;
	    TextureAtlas buttonAtlas;
	    boolean clicked = false;
		
		MenuScreen(SpriteBatch batch1){
			batch = batch1;
			
			create();
		}
		
		public boolean getClicked(){
			return clicked;
		}
		public void setClicked(boolean click){
			clicked = click;
		}
		public void create(){

	    	camera = new OrthographicCamera(1280, 720);
			camera.position.set(0, 0, 0);
			Viewport viewport = new ScreenViewport(camera);
			stage = new Stage(viewport,batch);
			Gdx.input.setInputProcessor(stage);
					
			//stage.add
		 	font = new BitmapFont();
	        skin = new Skin();
	        buttonAtlas = new TextureAtlas(Gdx.files.internal("but.pack"));
	        skin.addRegions(buttonAtlas);
	        textButtonStyle = new TextButtonStyle();
	        textButtonStyle.font = font;
	        textButtonStyle.up = skin.getDrawable("normalButton");
	        textButtonStyle.down = skin.getDrawable("pushedButton");
	        textButtonStyle.checked = skin.getDrawable("pushedButton");
	        button = new TextButton("", textButtonStyle);
	        button.setPosition(0, 0);
	   //     button.setScale(0.25f);
	        stage.addActor(button);
	        
	        button.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
						clicked = true;
				}
	        });
	}
		@Override
		public void show() {
			Gdx.input.setInputProcessor(stage);
			
		}
	public void render(float delta){
		super.render(delta);
		stage.act(delta);
		camera.update();
	//	batch.begin();
        stage.draw();
     //   batch.end();
		
	}
	public void resize(int width, int height){
	//	stage.getViewport().update(width, height);
		
	}
	

	@Override
	public void hide() {
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
		// TODO Auto-generated method stub
		
	}
	
}
