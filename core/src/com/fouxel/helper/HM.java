package com.fouxel.helper;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class HM {
	
	public static World 			world;
	
	public static final void  l(String msg){
		System.out.println(msg);
	}
	
	
	public static void addPolygon(float x, float y, int numberOfVertices){
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(x, y);
		Body body = world.createBody(bodyDef);

		float r = 5;
		Vector2[] vertices = new Vector2[numberOfVertices];
		for (int i = 0; i < numberOfVertices; i++) {
			  double mx = r * Math.cos(2 * Math.PI * i / numberOfVertices);
			  double my = r * Math.sin(2 * Math.PI * i / numberOfVertices);
			  vertices[i] = new Vector2((float)mx*10,(float)my*10);
		}
		PolygonShape poly = new PolygonShape();
		poly.set(vertices);
		
		FixtureDef fixtureDef = new FixtureDef();
    	fixtureDef.shape = poly;
    	fixtureDef.density = 20;
    	fixtureDef.friction = 0;
    	fixtureDef.isSensor = true;
    	fixtureDef.restitution = 0.6f;
	        
	    body.createFixture(fixtureDef).setUserData("Coin");
		
	}
	
	public static void addPlatform(float x, float y){

		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(new Vector2(x, y));
		
		Body groundBody = world.createBody(groundBodyDef);
		PolygonShape groundBox = new PolygonShape();
		
		groundBox.setAsBox(500, 10.0f);
		groundBody.createFixture(groundBox,0.0f);
		groundBox.dispose();
		
	}
	
	
	public static void addKinematicBody(float x, float y){

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(20f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 20;

        fixtureDef.friction = 0;
        fixtureDef.restitution = 0.6f;

        Fixture fixture = body.createFixture(fixtureDef);
        body.setLinearVelocity(0.0f, 1.0f);
        
        
	}
	
	public static void createPolygonShape(float x, float y){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(x, y);

		Body body = world.createBody(bodyDef);

		PolygonShape poly = new PolygonShape();
		poly.set(new Vector2[] { new Vector2(0,0), new Vector2(100,0), new Vector2(100,100), new Vector2(0,100)});
		
		FixtureDef fixtureDef = new FixtureDef();
    	fixtureDef.shape = poly;
    	fixtureDef.density = 20;
    	fixtureDef.friction = 0;
    	fixtureDef.restitution = 0.6f;
	        
	    body.createFixture(fixtureDef);
	}

}
