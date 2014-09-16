package com.fouxel.lastchance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fouxel.helper.HM;


public class GameScreen extends AbstractScreen{
	BitmapFont 					font;
	private Stage				stage;
	private OrthographicCamera 	camera;
	private OrthographicCamera 	cameraForGUI;
	private Box2DDebugRenderer 	renderer;
	private World 				world;
	private SpriteBatch 		batch = null;
	private static Body 		body;
	private boolean 			isAndroid = false;
	private ListenerClass		listener;
	private double				delta = 0.0d;
	private int					endGame = 0;//0 - gameplay, 1 - spad³, 2 - times out
	private int 				frameIndex = 0;
	private Timer				timer;
	private int					coins = 0;
	private boolean				secondJump = false;
	
	private Texture 			playerTexture;
	private Sprite 				playerSprite;
	
	
	Fixture fixture;
	private ArrayList<KinematicBody>			movingPlatforms;
	private ArrayList<StaticBody>				platforms;
	private ArrayList<StaticBody>				coinsList;
	private ArrayList<StaticBody>				chainsList;
	private ArrayList<DynamicBody>				dynamicBalls;
	private ArrayList<DynamicBody>			    dynamicRectangles;
	
	private ArrayList<Vector2>					startingPoints;
	
	
	private double				lat, lon;
	
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	private String				text; 
	private InputListener myListener= new InputListener() {
		 public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
			 return true; 
		 }
		 
		 public void touchUp(InputEvent event, float x, float y, int pointer, int button){
			 System.out.println("Delta: " + delta);
			 delta = 100;
			 if(listener.canJump()){
				 secondJump = true;
				 body.setTransform(body.getPosition().x, body.getPosition().y+1, body.getAngle());
				 body.setLinearVelocity(25, 25);
			 }
			 else{
				 if(secondJump){
					 body.setLinearVelocity(30, 25);
					 secondJump = false;
				 }
			 }
		 }
	 };
	 
	public GameScreen(SpriteBatch batch1){
		super();
		batch = batch1;

		create();
	}
	
	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		setEndGame(0);
		frameIndex = 0;
		coins = 0;
		timer.start();

        
       DateFormat hoursFormat = new SimpleDateFormat("HH");
       DateFormat minutesFormat = new SimpleDateFormat("mm");
       DateFormat secondsFormat = new SimpleDateFormat("ss");
       
       
       DateFormat yearsFormat = new SimpleDateFormat("yyyy");
       DateFormat monthsFormat = new SimpleDateFormat("MM");
       DateFormat daysFormat = new SimpleDateFormat("dd");

       
 	   Date date = new Date();
 	   int hour = Integer.parseInt(hoursFormat.format(date));
 	   int minute = Integer.parseInt(minutesFormat.format(date));
 	   int second = Integer.parseInt(secondsFormat.format(date));
 	   
 	   int year = Integer.parseInt(yearsFormat.format(date));
	   int month = Integer.parseInt(monthsFormat.format(date));
	   int day = Integer.parseInt(daysFormat.format(date));
		if(body != null){
			body.setTransform(startingPoints.get(0), 0);
			body.setLinearVelocity(20, 0);
		}
		secondJump = false;
		listener.setCanJump(false);
		
		//Sprawdzanie, które body s¹ zdeletowane po tym jak u¿ytkownik je zebra³
		for(int i = 0; i < coinsList.size(); ++i){
			HM.l("i: " + i);
			if(coinsList.get(i).isAlive() == false){
				coinsList.get(i).createCoinInner();
				}
		}
		
		
		for(int i = 0; i < dynamicBalls.size(); ++i)
			dynamicBalls.get(i).RestorePosition();
		for(int i = 0; i < dynamicRectangles.size(); ++i)
			dynamicRectangles.get(i).RestorePosition();
		
	}
	public void create(){
		text = "Lat:   , lon:   ";
		setLat(setLon(0.0d));
		
		setupTimer();
		
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		
		stage.addListener(myListener);
		camera = new OrthographicCamera(64, 36);
		camera.position.set(0, 100, 0);
		
		cameraForGUI = new OrthographicCamera(1280, 720);
		cameraForGUI.position.set(0, 100, 0);
		world = new World(new Vector2(0, -60), true); 
		renderer = new Box2DDebugRenderer();
		listener = new ListenerClass(world);
		world.setContactListener(listener);
		
		movingPlatforms = new ArrayList<KinematicBody>();
		platforms = new ArrayList<StaticBody>();
		coinsList = new ArrayList<StaticBody>();
		dynamicBalls = new ArrayList<DynamicBody>();
		dynamicRectangles = new ArrayList<DynamicBody>();
		chainsList = new ArrayList<StaticBody>();
		startingPoints = new ArrayList<Vector2>();
		
		HM.world = world;
		StaticBody.world = world;
		KinematicBody.world = world;
		DynamicBody.world = world;
		
		startingPoints.add(new Vector2());
		startingPoints.get(0).x = 0;
		startingPoints.get(0).y = 45;
		
		startingPoints.add(new Vector2());
		startingPoints.get(1).x = 195;
		startingPoints.get(1).y = 45;
		
		chainsList.add(new StaticBody());
		Vector2[] vertices = new Vector2[4];
		vertices[0] = new Vector2(-40,0);
		vertices[1] = new Vector2(0,30);
		vertices[2] = new Vector2(40,0);
		vertices[3] = new Vector2(200,0);
		chainsList.get(0).createChain(0, 0,vertices);
		
		chainsList.add(new StaticBody());
		Vector2[] vertices2 = new Vector2[3];
		vertices2[0] = new Vector2(280, 3);
		vertices2[1] = new Vector2(300, -5);
		vertices2[2] = new Vector2(350, -8);
		chainsList.get(1).createChain(0, 0, vertices2);
		
		
		//Naparzanie kulkami
		chainsList.add(new StaticBody());
		Vector2[] vertices3 = new Vector2[4];
		vertices3[0] = new Vector2(550, 0);
		vertices3[1] = new Vector2(650, 0);
		vertices3[2] = new Vector2(750, 50);
		vertices3[3] = new Vector2(830, 50);
		chainsList.get(2).createChain(0, 0, vertices3);
		
		for(int i = 0; i < 10; ++ i){
		dynamicBalls.add(new DynamicBody());
		dynamicBalls.get(i).createDynamicBall(570 + i*5, 10, 2);
		}
		chainsList.add(new StaticBody());
		Vector2[] vertices4 = new Vector2[2];
		vertices4[0] = new Vector2(550, 20);
		vertices4[1] = new Vector2(650, 20);
		chainsList.get(3).createChain(0, 0, vertices4);
		
		
		
		
		movingPlatforms.add(new KinematicBody());
		movingPlatforms.get(0).createMovingPlatform(380, -8, 10, 2);
		
		platforms.add(new StaticBody());
		platforms.get(0).createPlatform(80, 3, 3, 3);
		platforms.add(new StaticBody());
		platforms.get(1).createPlatform(130, 3, 3, 3);
		platforms.add(new StaticBody());
		platforms.get(2).createPlatform(220, -5, 10, 3);
		platforms.add(new StaticBody());
		platforms.get(3).createPlatform(270, -6.5f, 10, 10);
		platforms.add(new StaticBody());
		platforms.get(4).createPlatform(420, -8.5f, 10, 3);
		platforms.add(new StaticBody());
		platforms.get(5).createPlatform(465, -4.5f, 10, 3);
		platforms.add(new StaticBody());
		platforms.get(6).createPlatform(510, -0.5f, 10, 3);
		
		
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(0).createDynamicRectangle(170, 3, 2,4);
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(1).createDynamicRectangle(170, 11, 2,4);
		
		
		coinsList.add(new StaticBody());
		coinsList.get(0).createCoin(50, 5, 6,0);
		coinsList.add(new StaticBody());
		coinsList.get(1).createCoin(130, 8,6,1);
		
		//coinsy przy platformach wschodz¹cych do góry
		coinsList.add(new StaticBody());
		coinsList.get(2).createCoin(420, -2.5f,6,2);
		coinsList.add(new StaticBody());
		coinsList.get(3).createCoin(435, -0.5f,6,3);
		coinsList.add(new StaticBody());
		coinsList.get(4).createCoin(460, 2.5f,6,4);
		coinsList.add(new StaticBody());
		coinsList.get(5).createCoin(475, 4.0f,6,5);
		coinsList.add(new StaticBody());
		coinsList.get(6).createCoin(550, 5.5f,6,6);
		coinsList.add(new StaticBody());
		coinsList.get(7).createCoin(510, 5.5f,6,7);
		
		coinsList.add(new StaticBody());
		coinsList.get(8).createCoin(70, 5, 6,8);
		coinsList.add(new StaticBody());
		coinsList.get(9).createCoin(90, 5, 6,9);
		coinsList.add(new StaticBody());
		coinsList.get(10).createCoin(110, 5, 6,10);
		
		for(int i = 11; i < 19; ++i){
			coinsList.add(new StaticBody());
			coinsList.get(i).createCoin(650+((i-11)*10), 3+(i-11)*5, 6,i);
		}


        FileHandle fontFile = Gdx.files.internal("whitrabt.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);

        font = generator.generateFont(40);
        generator.dispose();
        
        playerTexture = new Texture("playa.png");
        playerTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
	
        playerSprite = new Sprite(playerTexture);
        playerSprite.setOrigin(0,0);
        playerSprite.setPosition(-playerSprite.getWidth()/2,-playerSprite.getHeight()/2);
        setupPlayer();
        
        
  
	}
	
	
	public void render(float delta){
	
		super.render(delta);
		camera.position.x = body.getPosition().x;
		camera.position.y = body.getPosition().y;
		
		if(body.getPosition().y < -50)
			endGame = 1;

		if(listener.getBodyToDestroy() != null){
			++coins;
			int index = (Integer) listener.getBodyToDestroy().getUserData();
			coinsList.get(index).setAlive(false);
			world.destroyBody(listener.getBodyToDestroy());
			
			listener.setBodyToDestroy(null);
		}
		
		world.step(Gdx.graphics.getDeltaTime(),2, 6);
		camera.update();
		renderer.render(world, camera.combined);
		
		playerSprite.setPosition(body.getPosition().x-64, body.getPosition().y-64);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
	///	playerSprite.draw(batch);
		batch.end();

		cameraForGUI.update();   
	    batch.setProjectionMatrix(cameraForGUI.combined);
		batch.begin();
		font.setColor(1.0f, 0.0f, 1.0f, 1.0f);
		font.draw(batch, "Left: " + (60-frameIndex) + " seconds", -600, 400); 
		font.draw(batch, "Coins: " + (coins), 400, 400); 
		
		batch.end();
		
	}
	
	private void setupPlayer(){

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.linearDamping = 0.0f;
		bodyDef.position.set(0,45);
		
		body = world.createBody(bodyDef);
		
		
		CircleShape circle = new CircleShape();
		circle.setRadius(4f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 2.2f;
		fixtureDef.friction = 1.0f;
		fixtureDef.restitution = 0.0f;
		fixture = body.createFixture(fixtureDef);
		fixture.setUserData("Player");
		body.setUserData(playerSprite);
		circle.dispose();
	}
	
	private void setupTimer(){
		timer = new Timer();

		final float delayMovement = 2;
		final int count = 99999999;
		
		//Zmiana kierunku Velocity
		Timer.schedule(new Task(){
		    @Override
		    public void run() {
		    	for(KinematicBody bo : movingPlatforms){
		    		bo.ReverseVelocity();
		    	}
		    }
		}, delayMovement,delayMovement,count);
		
		
		final float levelTimerSeconds = 30;
		final float step = 1;
		
		//Odliczanie czasu do koñca levela
		Timer.schedule(new Task(){
			@Override
			public void run() {
				frameIndex++;
				if(frameIndex >= levelTimerSeconds){
					setEndGame(2);
					
				}
			}
			
			
		}, step,step,count);
		
		
		timer.stop();
		
	}
	

	public void resize(int width, int height){
		
	}
	

	@Override
	public void hide() {
		timer.stop();
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		
	}

	public boolean isAndroid() {
		return isAndroid;
	}

	public void setAndroid(boolean isAndroid) {
		this.isAndroid = isAndroid;
		
	}

	public double getDelta() {
		return delta;
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}

	public int isEndGame() {
		return endGame;
	}

	public void setEndGame(int endGame) {
		this.endGame = endGame;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public double setLon(double lon) {
		this.lon = lon;
		return lon;
	}
}
