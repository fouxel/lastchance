package com.fouxel.lastchance;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class KinematicBody {
	public static World world;
	private Body body;
	private float velX, velY;
	
	KinematicBody(){
		setVelX(100.0f);
		setVelY(0.0f);
	}
	
	public void createMovingPlatform(float x, float y, float w, float h){

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);
		PolygonShape box = new PolygonShape();
		box.setAsBox(w, h);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 1.0f;
        fixtureDef.isSensor = false;
        fixtureDef.restitution = 0.0f;
        

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData("Platform");
        body.setLinearVelocity(10.0f, 0.0f);
       
        
	}
	public void ReverseVelocity(){
		 body.setLinearVelocity(-body.getLinearVelocity().x, 0.0f);
	}

	public float getVelY() {
		return velY;
	}

	public void setVelY(float velY) {
		this.velY = velY;
	}

	public float getVelX() {
		return velX;
	}

	public void setVelX(float velX) {
		this.velX = velX;
	}
	
	

}
