package com.fouxel.lastchance;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.fouxel.helper.*;


public class MyGame extends Game {

	public enum SCREEN_ENUM{
		START_SCREEN,
		MENU_SCREEN,
		GAME_SCREEN,
		END_SCREEN
	}
	
	private Skin skin;
	private TextureAtlas buttonAtlas;
	
	private SCREEN_ENUM actScreen;
	private SpriteBatch batch;
	private MenuScreen 	sMenu;
	private StartScreen sStart;
	private GameScreen 	sGame;
	private EndScreen	sEnd;
	private int 		maxScore;
	
	private Preferences prefs;
	
	private double latitude, longitude, delta;
	
	@Override
	public void create () {
		
		batch = new SpriteBatch();
		prefs = Gdx.app.getPreferences("GameState");
		maxScore = prefs.getInteger("dupencja",0);
		HM.l("maxScore: " + maxScore);
		
	    skin = new Skin();
        buttonAtlas = new TextureAtlas(Gdx.files.internal("all.pack"));
        skin.addRegions(buttonAtlas);
		
		sStart = new StartScreen(batch);
		sMenu = new MenuScreen(batch, skin, maxScore);
		sGame = new GameScreen(batch, maxScore);
		sEnd = new EndScreen(batch,skin);
		setScreen(sStart);
		actScreen = SCREEN_ENUM.START_SCREEN;
		
		latitude = longitude = delta = 0.0;
		
	}

	@Override
	public void render () {
		super.render();
		
		switch(actScreen){
		
				case MENU_SCREEN:
						if(sMenu.getClicked()){
							actScreen = SCREEN_ENUM.GAME_SCREEN;
							setScreen(sGame);
							sMenu.setClicked(false);
						}break;
				case START_SCREEN:
						if(sStart.getZmien()){
							actScreen = SCREEN_ENUM.MENU_SCREEN;
							setScreen(sMenu);
						}break;	
				case GAME_SCREEN:
						if(sGame.isEndGame() == 1 || sGame.isEndGame() == 2){
							maxScore = sGame.getMaxScore();
							prefs.putInteger("dupencja",maxScore);
							prefs.flush();
							sMenu.setMaxScore(maxScore);
							sEnd.setScore(sGame.getCoins());
							sEnd.setHighScore(maxScore);

							actScreen = SCREEN_ENUM.END_SCREEN;
							setScreen(sEnd);
						}
						break;
						
				case END_SCREEN:
					if(sEnd.getZmien()){
						actScreen = SCREEN_ENUM.MENU_SCREEN;
						setScreen(sMenu);
					}
					break;
					
		default:
			break;
		
		}
	}
	
	public void onBackPressed(){
		actScreen = SCREEN_ENUM.MENU_SCREEN;
		setScreen(sMenu);
	}
	
	public void UpdateLocation(double lat, double lon){
		if((lat == 0 && lon == 0))
			return;
		if(latitude == 0 && longitude == 0){
			latitude = lat;
			longitude = lon;
			return;
		}
		
		
		if(sGame != null){
			latitude = lat;
			longitude = lon;
			
			sGame.setLat(latitude);
			sGame.setLon(longitude);
			
		}
	/*	switch(actScreen){	
			case GAME_SCREEN:
				delta = Math.sqrt(Math.pow((latitude - lat),2) + Math.pow((longitude - lon),2));
				sGame.setText("lat: " + String.valueOf(lat) + " lon: " + String.valueOf(lon) + " delta: " + String.valueOf(delta));
				sGame.setDelta(delta);
				latitude = lat;
				longitude = lon;
				
				break;
			
			default:
				break;

			}*/
		return;
	}
	
}
