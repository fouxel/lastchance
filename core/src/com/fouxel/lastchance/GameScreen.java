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
	private int					TTL = 60;
	
	private int					FORCE = 25;
	
	Fixture fixture;
	private ArrayList<KinematicBody>			movingPlatforms;
	private ArrayList<StaticBody>				platforms;
	private ArrayList<StaticBody>				coinsList;
	private ArrayList<StaticBody>				chainsList;
	private ArrayList<DynamicBody>				dynamicBalls;
	private ArrayList<DynamicBody>			    dynamicRectangles;
	
	private ArrayList<Vector2>					startingPoints;
	
	
	private boolean 				plus = true;
	

	private InputListener myListener= new InputListener() {
		 public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
			 return true; 
		 }
		 
		 public void touchUp(InputEvent event, float x, float y, int pointer, int button){
			 if(listener.canJump()){
				 secondJump = true;
				 body.setTransform(body.getPosition().x, body.getPosition().y+1, body.getAngle());
				 body.setLinearVelocity(plus == true ? FORCE : -FORCE, FORCE);
			 }
			 else{
				 if(secondJump){
					 body.setLinearVelocity(plus == true ? FORCE : -FORCE, FORCE);
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
	    FORCE = 25 + second%5;
	    //FORCE= 35;
	    int max = 5;
	    if(plus == false)
	    	max = 12;
		if(body != null){
			int index = second%max;
			
			index = 0;
			plus = true;
			if(plus == true){ 
				switch(index){
				case 0:
					TTL = 55 + hour%2 + second %4 ;
					break;
				case 1:
					TTL = 52 + year%2 + second %4 ;
					break;
				case 2:
					TTL = 48 + month%2 + second %4 ;
					break;
				case 3: 
					TTL = 46 + month%2 + day%4 ;
					break;
				case 4: 
					TTL = 40 + month%3 + day%4 ;
					break;
				default:
					TTL = 35;
					break;
				
				}
			
			}
			else{
				switch(index){
				case 0:
					TTL = 32 + hour%2 + second %3 ;
					break;
				case 1:
					TTL = 38 + year%2 + second %4 ;
					break;
				case 2:
					TTL = 41 + month%2 + second %3 ;
					break;
				case 3:
					TTL = 50 + month%2 + second %4 ;
					break;
				default:
					TTL = 56 + hour%2 + second%4;
				
				}
				
				
			}
			
			TTL -= (second%5)*2;
				
			body.setTransform(startingPoints.get(index), 0);
			body.setLinearVelocity(20, 0);
		}
		secondJump = false;
		listener.setCanJump(false);
		
		for(int i = 0; i < coinsList.size(); ++i){
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
		startingPoints.get(2).x = 280;
		startingPoints.get(2).y = 5;
		
		startingPoints.add(new Vector2());
		startingPoints.get(3).x = 550;
		startingPoints.get(3).y = 5;
		

		startingPoints.add(new Vector2());
		startingPoints.get(4).x = 760;
		startingPoints.get(4).y = 58;
		
		startingPoints.add(new Vector2());
		startingPoints.get(5).x = 990;
		startingPoints.get(5).y = 55;
		
		startingPoints.add(new Vector2());
		startingPoints.get(6).x = 1160;
		startingPoints.get(6).y = 30;
		
		startingPoints.add(new Vector2());
		startingPoints.get(7).x = 1190;
		startingPoints.get(7).y = 58;
		
		startingPoints.add(new Vector2());
		startingPoints.get(8).x = 1220;
		startingPoints.get(8).y = 58;
		
		startingPoints.add(new Vector2());
		startingPoints.get(9).x = 1250;
		startingPoints.get(9).y = 58;
		
		startingPoints.add(new Vector2());
		startingPoints.get(10).x = 1410;
		startingPoints.get(10).y = 17;
		
		startingPoints.add(new Vector2());
		startingPoints.get(11).x = 1640;
		startingPoints.get(11).y = 20;
		
		
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
		
		dynamicBalls.add(new DynamicBody());
		dynamicBalls.get(10).createDynamicBall(1419, 23, 2);
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
		
	
		chainsList.add(new StaticBody());
		Vector2[] vertices6 = new Vector2[8];
		vertices6[0] = new Vector2(1140, 64);
		vertices6[1] = new Vector2(1170, 10);
		vertices6[2] = new Vector2(1200, 64);
		vertices6[3] = new Vector2(1230, 10);
		vertices6[4] = new Vector2(1260, 64);
		vertices6[5] = new Vector2(1290, 10);
		vertices6[6] = new Vector2(1320, 64);
		vertices6[7] = new Vector2(1350, 10);
		chainsList.get(5).createChain(0, 0, vertices6);
		
		

		chainsList.add(new StaticBody());
		Vector2[] vertices7 = new Vector2[15];
		vertices7[0] = new Vector2(1445, 12);
		vertices7[1] = new Vector2(1500, 12);
		vertices7[2] = new Vector2(1500, 25);
		vertices7[3] = new Vector2(1510, 25);
		vertices7[4] = new Vector2(1510, 12);
		vertices7[5] = new Vector2(1550, 12);
		vertices7[6] = new Vector2(1550, 25);
		vertices7[7] = new Vector2(1560, 25);
		vertices7[8] = new Vector2(1560, 12);
		vertices7[9] = new Vector2(1600, 12);
		vertices7[10] = new Vector2(1600, 25);
		vertices7[11] = new Vector2(1610, 25);
		vertices7[12] = new Vector2(1610, 12);
		vertices7[13] = new Vector2(1650, 12);
		vertices7[14] = new Vector2(1650, 25);
		chainsList.get(6).createChain(0, 0, vertices7);
		
		chainsList.add(new StaticBody());
		Vector2[] vertices8 = new Vector2[15];
		
		for(int i = 0; i < 15; ++ i){
			vertices8[i] = new Vector2(vertices7[i].x - 2275,vertices7[i].y+30);
		}
		chainsList.get(7).createChain(0, 0, vertices8);
		
		
		
		
		
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
		
		dynamicRectangles.add(new DynamicBody());
		dynamicRectangles.get(14).createDynamicRectangle(-533, 80, 1,30,0.1f,0f,1.0f);
		
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
		
		platforms.add(new StaticBody());
		platforms.get(18).createPlatform(-520, 45.5f, 25, 3);
		
		platforms.add(new StaticBody());
		platforms.get(19).createPlatform(-535, 50.5f, 1, 1);
		
		platforms.add(new StaticBody());
		platforms.get(20).createPlatform(-595, 50.5f, 10, 1);
		
		platforms.add(new StaticBody());
		platforms.get(21).createPlatform(1370, 10.5f, 5, 1);
		
		platforms.add(new StaticBody());
		platforms.get(22).createPlatform(1420, 10.5f, 20, 1);
		
		platforms.add(new StaticBody());
		platforms.get(23).createPlatform(1420, 16.0f, 2, 4.5f);
		
		
		for(int i = 0; i < 200; ++i)
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
		
		
		coinsList.get(53).createCoin(890, 55, 6,53);
		coinsList.get(54).createCoin(895, 55, 6,54);
		coinsList.get(55).createCoin(900, 55, 6,55);
		coinsList.get(56).createCoin(905, 55, 6,56);
		
		coinsList.get(57).createCoin(960, 55, 6,57);
		coinsList.get(58).createCoin(965, 55, 6,58);
		coinsList.get(59).createCoin(970, 55, 6,59);
		coinsList.get(60).createCoin(975, 55, 6,60);
		
		for(int i = 61; i < 70; ++i){
			coinsList.get(i).createCoin(1000+((i-61)*10), 56+(i-61)*3, 6,i);
		}
		
		coinsList.get(70).createCoin(-40, 3, 6,70);
		coinsList.get(71).createCoin(-50, 4, 6,71);
		coinsList.get(72).createCoin(-60, 8, 6,72);
		coinsList.get(73).createCoin(-70, 9, 6,73);
		coinsList.get(74).createCoin(-80, 1, 6,74);
		coinsList.get(75).createCoin(-90, 9, 6,75);
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
		
		coinsList.get(93).createCoin(-415, 50, 6,93);
		coinsList.get(94).createCoin(-425, 60, 6,94);
		coinsList.get(95).createCoin(-435, 50, 6,95);
		coinsList.get(96).createCoin(-445, 60, 6,96);
		coinsList.get(97).createCoin(-450, 50, 6,97);
		coinsList.get(98).createCoin(-460, 50, 6,98);
		coinsList.get(99).createCoin(-470, 60, 6,99);
		coinsList.get(100).createCoin(-480, 50, 6,100);
		for(int i = 0; i < 3; ++ i){
			for(int j = 0; j < 8; ++j){
				int index = 100 +(i*8) + j;
				coinsList.get(index).createCoin(1170+(i*60)+j*5.2f, 16+j*10, 6,index);
			}
		}
		coinsList.get(124).createCoin(1500, 30, 6,124);
		coinsList.get(125).createCoin(1510, 30, 6,125);
		coinsList.get(126).createCoin(1550, 30, 6,126);
		coinsList.get(127).createCoin(1560, 30, 6,127);
		coinsList.get(128).createCoin(1600, 30, 6,128);
		coinsList.get(129).createCoin(1610, 30, 6,129);
		coinsList.get(130).createCoin(1513, 20, 6,130);
		coinsList.get(131).createCoin(1547, 20, 6,131);
		coinsList.get(132).createCoin(1563, 20, 6,132);
		coinsList.get(133).createCoin(1597, 20, 6,133);
		coinsList.get(134).createCoin(1613, 20, 6,134);
		coinsList.get(135).createCoin(1647, 20, 6,135);
		coinsList.get(136).createCoin(-80, 14, 6,136);
		coinsList.get(137).createCoin(-40, 20, 6,137);
		

		coinsList.get(138).createCoin(-155, 35, 6,138);
		coinsList.get(139).createCoin(-165, 45, 6,139);
		coinsList.get(140).createCoin(-175, 48, 6,140);
		coinsList.get(141).createCoin(-185, 48, 6,141);
		
		coinsList.get(142).createCoin(-280, 43, 6,142);
		coinsList.get(143).createCoin(-320, 43, 6,143);
		
		coinsList.get(144).createCoin(-555, 56, 6,144);
		coinsList.get(145).createCoin(-565, 56, 6,145);
		coinsList.get(146).createCoin(-575, 56, 6,146);
		coinsList.get(147).createCoin(-585, 56, 6,147);
		
		coinsList.get(148).createCoin(-550, 69, 6,148);
		coinsList.get(149).createCoin(-560, 69, 6,149);
		coinsList.get(150).createCoin(-570, 69, 6,150);
		coinsList.get(151).createCoin(-580, 69, 6,151);
		
		
		coinsList.get(152).createCoin(1500-2275, 60, 6,152);
		coinsList.get(153).createCoin(1510-2275, 60, 6,153);
		coinsList.get(154).createCoin(1550-2275, 60, 6,154);
		coinsList.get(155).createCoin(1560-2275, 60, 6,155);
		coinsList.get(156).createCoin(1600-2275, 60, 6,156);
		coinsList.get(157).createCoin(1610-2275, 60, 6,157);
		coinsList.get(158).createCoin(1513-2275, 50, 6,158);
		coinsList.get(159).createCoin(1547-2275, 50, 6,159);
		coinsList.get(160).createCoin(1563-2275, 50, 6,160);
		coinsList.get(161).createCoin(1597-2275, 50, 6,161);
		coinsList.get(162).createCoin(1613-2275, 50, 6,162);
		coinsList.get(163).createCoin(1647-2275, 50, 6,163);
		
		coinsList.get(164).createCoin(85, 18, 6,164);
		coinsList.get(165).createCoin(105, 18, 6,165);
		coinsList.get(166).createCoin(135, 20, 6,166);
		
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
		
	//	HM.l("X: " + body.getPosition().x);
//		HM.l("Y: " + body.getPosition().y);
		
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
		font.setColor(0.08f, 0.34f, 0.031f, 1.0f);
		font.draw(batch, "[/time]$   " + (TTL-frameIndex), -600, 400); 
		font.draw(batch, "[/points]$ " + (coins), -600, 430); 
		
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
		
		
		final float step = 1;
		
		//Odliczanie czasu do koñca levela
		Timer.schedule(new Task(){
			@Override
			public void run() {
				frameIndex++;
				if(frameIndex >= TTL){
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

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}
}
