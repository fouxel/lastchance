package com.fouxel.lastchance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fouxel.helper.HM;


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
	    boolean clicked = false;
	    private int maxScore;
		
		MenuScreen(SpriteBatch batch1, Skin skin, int maxScore){
			batch = batch1;
			this.skin = skin;
			this.setMaxScore(maxScore);

			
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
			stage = new Stage(viewport);
			Gdx.input.setInputProcessor(stage);

		 	font = new BitmapFont();

	        textButtonStyle = new TextButtonStyle();
	        textButtonStyle.font = font;
	        textButtonStyle.up = skin.getDrawable("normalButton");
	        textButtonStyle.down = skin.getDrawable("pushedButton");
	        textButtonStyle.checked = skin.getDrawable("pushedButton");
	        button = new TextButton("", textButtonStyle);

	        button.setPosition(-button.getWidth()/2,-button.getHeight()/2);
	        stage.addActor(button);
	        
	        FileHandle fontFile = Gdx.files.internal("whitrabt.ttf");
	        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);

	        font = generator.generateFont(40);
	        generator.dispose();
			camera.position.set(0, 0, 0);

	      
	}
		@Override
		public void show() {
			Gdx.input.setInputProcessor(stage);
			clicked = false;
			  HM.l("show");
			 
	        button.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
						clicked = true;
				}
	        });
			
		}
	public void render(float delta){
		super.render(delta);

		stage.getRoot().setVisible(true);
		stage.act(delta);
		//camera.update();
		//
		 camera.update();   
		    batch.setProjectionMatrix(camera.combined);
	        batch.begin();
	        font.setColor(1.0f, 0.64f, 0.0f, 1.0f);
			font.draw(batch, "highscore: " + maxScore,-150,-button.getHeight()/2-75); 
	        batch.end();
        stage.draw();
        
       
		
	}
	public void resize(int width, int height){
	//	stage.getViewport().update(width, height,true);

		
	}
	

	@Override
	public void hide() {
		//  button.clearListeners();
		  HM.l("hide");
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

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}
	
}
