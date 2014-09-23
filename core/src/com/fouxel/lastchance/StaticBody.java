package com.fouxel.lastchance;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class StaticBody {
	private Body body;
	public static World world;
	private float x, y;
	private int index, numberOfVertices;
	private boolean alive = true;
	
	StaticBody(){

	}
	
	public Body getBody(){
		return body;
	}
	
	public void createPlatform(float x, float y, float w, float h){

		BodyDef boxDef = new BodyDef();
		boxDef.position.set(new Vector2(x, y));

		body = world.createBody(boxDef);
		
		PolygonShape box = new PolygonShape();
		box.setAsBox(w, h);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = 20;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.0f;
		body.createFixture(fixtureDef).setUserData("Ground");;
		box.dispose();
	}
	public void createChain(float x, float y, Vector2[] vertices){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(x, y);

		body = world.createBody(bodyDef);
		
		
		ChainShape chain = new ChainShape();
		chain.createChain(vertices);

		FixtureDef fixtureDef = new FixtureDef();
    	fixtureDef.shape = chain;
    	fixtureDef.density = 20;
    	fixtureDef.friction = 0.0f;
    	fixtureDef.restitution = 0.0f;
	        
	    body.createFixture(fixtureDef).setUserData("Chain");
	}
	
	

	
	public void createCoin(float x, float y, int numberOfVertices, int index){
		this.x = x;
		this.y = y;
		this.numberOfVertices = numberOfVertices;
		this.index = index;
		createCoinInner();
	}
	
	public void createCoinInner(){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(x, y);
		body = world.createBody(bodyDef);
		this.alive = true;

		float r = 2;
		Vector2[] vertices = new Vector2[numberOfVertices];
		for (int i = 0; i < numberOfVertices; i++) {
			  double mx = r * Math.cos(2 * Math.PI * i / numberOfVertices);
			  double my = r * Math.sin(2 * Math.PI * i / numberOfVertices);
			  vertices[i] = new Vector2((float)mx,(float)my);
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
	    body.setUserData(index);
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
