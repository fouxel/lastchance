package com.fouxel.lastchance;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.fouxel.helper.HM;

public class DynamicBody {
	public static World world;
	private Body body;
	
	private float oldX;
	private float oldY;
	
	DynamicBody(){
	}
	
	public void RestorePosition(){
		body.setAngularVelocity(0);
		body.setLinearVelocity(0, 0);
		body.setTransform(oldX,oldY, 0);
	}
	
	
	public void createDynamicBall(float x, float y, float r){
		oldX = x;
		oldY = y;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.linearDamping = 0.0f;
		bodyDef.position.set(x,y);
		
		body = world.createBody(bodyDef);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(r);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.1f;
		fixtureDef.friction = 1.0f;
		fixtureDef.restitution = 1.03f;
		Fixture fixture = body.createFixture(fixtureDef);
		fixture.setUserData("DynamicBall");
		circle.dispose();
		body.setBullet(true);
	}
	
	public Body getBody(){
		
		return body;
	}
	
	public void createDynamicRectangle(float x, float y, float w, float h, float density, float restitution, float friction){
		oldX = x;
		oldY = y;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.linearDamping = 0.0f;
		bodyDef.position.set(x,y);
		
		body = world.createBody(bodyDef);
		

		PolygonShape box = new PolygonShape();
		box.setAsBox(w, h);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = box;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;
		Fixture fixture = body.createFixture(fixtureDef);
		fixture.setUserData("DynamicRectangle");
		//body.setActive(false);
		body.setBullet(true);
		box.dispose();
	}
	
	
	
	

}
