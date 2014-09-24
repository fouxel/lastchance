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
	private int 				maxScore = 0;
	private boolean				secondJump = false;
	
	
	Fixture fixture;
	private ArrayList<KinematicBody>			movingPlatforms;
	private ArrayList<StaticBody>				platforms;
	private ArrayList<StaticBody>				coinsList;
	private ArrayList<StaticBody>				chainsList;
	private ArrayList<DynamicBody>				dynamicBalls;
	private ArrayList<DynamicBody>			    dynamicRectangles;
	
	private ArrayList<Vector2>					startingPoints;
	
	
	private double				lat, lon;
	private boolean 				plus = true;
	
	
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
			// plus = true;
			 if(listener.canJump()){
				 secondJump = true;
				 body.setTransform(body.getPosition().x, body.getPosition().y+1, body.getAngle());
				 body.setLinearVelocity(plus == true ? -25 : 25, 25);
			 }
			 else{
				 if(secondJump){
					 body.setLinearVelocity(plus == true ? -30 : 30, 25);
					 secondJump = false;
				 }
			 }
		 }
	 };
	 
	public GameScreen(SpriteBatch batch1, int maxScore){
		super();
		batch = batch1;
		this.maxScore = maxScore;

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
	   
	    plus = minute%2 == 0 ? true : false;
		if(body != null){
			body.setTransform(startingPoints.get(second%3), 0);
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
		
		startingPoints.add(new Vector2());
		startingPoints.get(2).x = 750;
		startingPoints.get(2).y = 60;
		
		startingPoints.add(new Vector2());
		startingPoints.get(3).x = -390;
		startingPoints.get(3).y = 70;
		
		
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
		Vector2[] vertices3 = new Vector2[7];
		vertices3[0] = new Vector2(550, 0);
		vertices3[1] = new Vector2(650, 0);
		vertices3[2] = new Vector2(750, 50);
		vertices3[3] = new Vector2(830, 50);
		vertices3[4] = new Vector2(860, 90);
		vertices3[5] = new Vector2(890, 50);
		vertices3[6] = new Vector2(1000, 50);
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
		

		chainsList.add(new StaticBody());
		Vector2[] vertices5 = new Vector2[15];
		vertices5[0] = new Vector2(-40, 0);
		vertices5[1] = new Vector2(-100, -5);
		vertices5[2] = new Vector2(-150, 15);
		vertices5[3] = new Vector2(-150, 20);
		vertices5[4] = new Vector2(-160, 20);
		vertices5[5] = new Vector2(-160, 25);
		vertices5[6] = new Vector2(-170, 25);
		vertices5[7] = new Vector2(-170, 30);
		vertices5[8] = new Vector2(-190, 30);
		vertices5[9] = new Vector2(-190, 35);
		vertices5[10] = new Vector2(-200, 35);
		vertices5[11] = new Vector2(-200, 40);
		vertices5[12] = new Vector2(-210, 40);
		vertices5[13] = new Vector2(-210, 45);
		vertices5[14] = new Vector2(-240, 45);
		chainsList.get(4).createChain(0, 0, vertices5);
		
		
		
		
		
	//	movingPlatforms.add(new KinematicBody());
	//	movingPlatforms.get(0).createMovingPlatform(380, -8, 10, 2);
		
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
		platforms.add(new StaticBody());
		platforms.get(7).createPlatform(380, -8f, 15, 2);
		
		platforms.add(new StaticBody());
		platforms.get(8).createPlatform(770, 52f, 2, 2);
		
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(0).createDynamicRectangle(800, 52.0f, 2, 2, 50000f, 1.0f, 0.0f);
		dynamicRectangles.get(0).getBody().setLinearVelocity(15f, 0);
		platforms.add(new StaticBody());
		platforms.get(9).createPlatform(820, 52f, 2, 2);
		
		
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(1).createDynamicRectangle(170, 3, 2,4,0.1f,0f,1.0f);
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(2).createDynamicRectangle(170, 11, 2,4,0.1f,0f,1.0f);
		
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(3).createDynamicRectangle(930, 54, 2,4,0.1f,0f,1.0f);
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(4).createDynamicRectangle(936, 54, 2,4,0.1f,0f,1.0f);
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(5).createDynamicRectangle(942, 54, 2,4,0.1f,0f,1.0f);
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(6).createDynamicRectangle(948, 54, 2,4,0.1f,0f,1.0f);
		
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(7).createDynamicRectangle(933, 62, 2,4,0.1f,0f,1.0f);
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(8).createDynamicRectangle(945, 62, 2,4,0.1f,0f,1.0f);
		
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(9).createDynamicRectangle(933, 70, 2,4,0.1f,0f,1.0f);
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(10).createDynamicRectangle(945, 70, 2,4,0.1f,0f,1.0f);
		
		
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(11).createDynamicRectangle(-390, 46, 2,4,0.1f,0f,1.0f);
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(12).createDynamicRectangle(-400, 46, 2,4,0.1f,0f,1.0f);
		
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(13).createDynamicRectangle(-393, 51, 12,1,0.1f,0f,1.0f);
		
		platforms.add(new StaticBody());
		platforms.get(10).createPlatform(960, 50.2f, 0.2f, 0.2f);
		platforms.add(new StaticBody());
		platforms.get(11).createPlatform(1020, 55f, 10, 3);
		platforms.add(new StaticBody());
		platforms.get(12).createPlatform(1065, 59.5f, 10, 3);
		platforms.add(new StaticBody());
		platforms.get(13).createPlatform(1110, 64.0f, 10, 3);
		
		
		platforms.add(new StaticBody());
		platforms.get(14).createPlatform(-260, 40.5f, 10, 3);
		platforms.add(new StaticBody());
		platforms.get(15).createPlatform(-300, 45.5f, 10, 3);
		platforms.add(new StaticBody());
		platforms.get(16).createPlatform(-340, 49.5f, 10, 3);
	
		platforms.add(new StaticBody());
		platforms.get(17).createPlatform(-420, 40.5f, 50, 3);
		
		for(int i = 0; i < 100; ++i)
			coinsList.add(new StaticBody());
		coinsList.get(0).createCoin(50, 5, 6,0);
		coinsList.get(1).createCoin(130, 8,6,1);
		
		//coinsy przy platformach wschodz¹cych do góry

		coinsList.get(2).createCoin(420, -2.5f,6,2);
		coinsList.get(3).createCoin(435, -0.5f,6,3);
		coinsList.get(4).createCoin(460, 2.5f,6,4);
		coinsList.get(5).createCoin(475, 4.0f,6,5);
		coinsList.get(6).createCoin(555, 15.5f,6,6);
		coinsList.get(7).createCoin(510, 5.5f,6,7);
		coinsList.get(8).createCoin(70, 5, 6,8);
		coinsList.get(9).createCoin(90, 5, 6,9);
		coinsList.get(10).createCoin(110, 5, 6,10);
		for(int i = 11; i < 19; ++i){
			coinsList.get(i).createCoin(650+((i-11)*10), 3+(i-11)*5, 6,i);
		}
		coinsList.get(19).createCoin(218, 5, 6,19);
		coinsList.get(20).createCoin(260, 7, 6,20);
		coinsList.get(21).createCoin(270, 7, 6,21);
		coinsList.get(22).createCoin(280, 7, 6,22);
		
		coinsList.get(23).createCoin(310, 5, 6,23);
		coinsList.get(24).createCoin(320, 4, 6,24);
		coinsList.get(25).createCoin(330, 3, 6,25);
		coinsList.get(26).createCoin(340, 2, 6,26);
		
		coinsList.get(27).createCoin(370, 5, 6,27);
		coinsList.get(28).createCoin(380, 1, 6,28);
		coinsList.get(29).createCoin(390, 5, 6,29);
		coinsList.get(30).createCoin(400, 9, 6,30);
		

		coinsList.get(31).createCoin(550, 3, 6,31);
		coinsList.get(32).createCoin(560, 8, 6,32);
		coinsList.get(33).createCoin(570, 3, 6,33);
		coinsList.get(34).createCoin(580, 15, 6,34);
		
		coinsList.get(35).createCoin(590, 15, 6,35);
		coinsList.get(36).createCoin(600, 3, 6,36);
		coinsList.get(37).createCoin(610, 8, 6,37);
		coinsList.get(38).createCoin(620, 17, 6,38);
		coinsList.get(39).createCoin(630, 8, 6,39);
		coinsList.get(40).createCoin(640, 17, 6,38);
		
		coinsList.get(41).createCoin(770, 57, 6,41);
		coinsList.get(42).createCoin(770, 62, 6,42);
		coinsList.get(43).createCoin(770, 67, 6,43);
		coinsList.get(44).createCoin(770, 72, 6,44);
		
		coinsList.get(45).createCoin(820, 57, 6,45);
		coinsList.get(46).createCoin(820, 62, 6,46);
		coinsList.get(47).createCoin(820, 67, 6,47);
		coinsList.get(48).createCoin(820, 72, 6,48);
		
		coinsList.get(49).createCoin(860, 93, 6,49);
		coinsList.get(50).createCoin(860, 98, 6,50);
		coinsList.get(51).createCoin(860, 103, 6,51);
		coinsList.get(52).createCoin(860, 108, 6,52);
		
		
		coinsList.get(53).createCoin(900, 55, 6,53);
		coinsList.get(54).createCoin(905, 55, 6,54);
		coinsList.get(55).createCoin(910, 55, 6,55);
		coinsList.get(56).createCoin(915, 55, 6,56);
		
		coinsList.get(57).createCoin(960, 55, 6,57);
		coinsList.get(58).createCoin(965, 55, 6,58);
		coinsList.get(59).createCoin(970, 55, 6,59);
		coinsList.get(60).createCoin(975, 55, 6,60);
		
		for(int i = 61; i < 70; ++i){
			coinsList.get(i).createCoin(1000+((i-61)*10), 56+(i-61)*3, 6,i);
		}
		
		coinsList.get(70).createCoin(-40, 3, 6,70);
		coinsList.get(71).createCoin(-50, 4, 6,71);
		coinsList.get(72).createCoin(-60, 5, 6,72);
		coinsList.get(73).createCoin(-70, 7, 6,73);
		coinsList.get(74).createCoin(-80, 3, 6,74);
		coinsList.get(75).createCoin(-90, 4, 6,75);
		coinsList.get(76).createCoin(-100, 5, 6,76);
		coinsList.get(77).createCoin(-110, 7, 6,77);
		
		coinsList.get(78).createCoin(-155, 25, 6,78);
		coinsList.get(79).createCoin(-165, 30, 6,79);
		coinsList.get(80).createCoin(-175, 35, 6,80);
		coinsList.get(81).createCoin(-185, 35, 6,81);
		
		coinsList.get(82).createCoin(-195, 40, 6,82);
		coinsList.get(83).createCoin(-205, 45, 6,83);
		coinsList.get(84).createCoin(-215, 50, 6,84);
		coinsList.get(85).createCoin(-225, 50, 6,85);
		coinsList.get(86).createCoin(-235, 50, 6,86);
		

		coinsList.get(87).createCoin(-255, 50, 6,87);
		coinsList.get(88).createCoin(-265, 50, 6,88);
		coinsList.get(89).createCoin(-335, 55, 6,89);
		coinsList.get(90).createCoin(-345, 55, 6,90);
		coinsList.get(91).createCoin(-295, 52, 6,91);
		coinsList.get(92).createCoin(-305, 52, 6,92);
		
	/*	vertices5[3] = new Vector2(-150, 20);
		vertices5[4] = new Vector2(-160, 20);
		vertices5[5] = new Vector2(-160, 25);
		vertices5[6] = new Vector2(-170, 25);
		vertices5[7] = new Vector2(-170, 30);
		vertices5[8] = new Vector2(-190, 30);
		vertices5[9] = new Vector2(-190, 35);
		vertices5[10] = new Vector2(-200, 35);
		vertices5[11] = new Vector2(-200, 40);
		vertices5[12] = new Vector2(-210, 40);
		vertices5[13] = new Vector2(-210, 45);
		vertices5[14] = new Vector2(-250, 45);*/
		

//Tutaj jakas kupa
        FileHandle fontFile = Gdx.files.internal("whitrabt.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);

        font = generator.generateFont(40);
        generator.dispose();
        setupPlayer();
        
        
  
	}
	
	
	public void render(float delta){
	
		super.render(delta);
		camera.position.x = body.getPosition().x;
		camera.position.y = body.getPosition().y;
		
		HM.l("X: " + body.getPosition().x);
		HM.l("Y: " + body.getPosition().y);
		
		if(body.getPosition().y < -50){
			setEndGame(1);
			if(coins > maxScore)
				maxScore = coins;
		}

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
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
	///	playerSprite.draw(batch);
		batch.end();

		cameraForGUI.update();   
	    batch.setProjectionMatrix(cameraForGUI.combined);
		batch.begin();
		font.setColor(1.0f, 0.64f, 0.0f, 1.0f);
		font.draw(batch, "" + (60-frameIndex) + " seconds left", -600, 400); 
		font.draw(batch, "Points: " + (coins), 400, 400); 
		
		batch.end();
		
	}
	
	public int getCoins() {
		return coins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
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
		
		
		final float levelTimerSeconds = 60;
		final float step = 1;
		
		//Odliczanie czasu do koñca levela
		Timer.schedule(new Task(){
			@Override
			public void run() {
				frameIndex++;
				if(frameIndex >= levelTimerSeconds){
					setEndGame(2);
					if(coins > maxScore)
						maxScore = coins;
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

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}
}
