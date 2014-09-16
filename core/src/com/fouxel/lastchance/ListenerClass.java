package com.fouxel.lastchance;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.fouxel.helper.HM;


public class ListenerClass implements ContactListener {
		private boolean canJump = false;
		
		private Body bodyToDestroy = null;
		World		world;
		
		
		ListenerClass(World world){
			this.world = world;
			
		}
		
		@Override
		public void endContact(Contact contact) {
			if(contact.getFixtureA().getUserData() == "Player"){
				String userData = (String)contact.getFixtureB().getUserData();
				System.out.println("End: " + userData);
				
				if(userData == "Platform"){
					System.out.println("A");
					//can = true;
				}
				canJump = false;
			}
			if(contact.getFixtureB().getUserData() == "Player"){
				String userData = (String)contact.getFixtureA().getUserData();
				System.out.println("End: " + userData);
				if(userData == "Platform"){
					System.out.println("B");
					//can = true;
				}
				canJump = false;
			}
		}
		
		@Override
		public void beginContact(Contact contact) {
			if(contact.getFixtureA().getUserData() == "Player" ){
				String userData = (String)contact.getFixtureB().getUserData();
				if(userData == "Coin"){
					setBodyToDestroy(contact.getFixtureB().getBody());
				}
				if(userData == "Platform"){
					contact.getFixtureA().setFriction(0.0f);
					contact.getFixtureA().setDensity(0.0f);
				}
			}
			if(contact.getFixtureB().getUserData() == "Player"){
				String userData = (String)contact.getFixtureB().getUserData();
				if(contact.getFixtureA().getUserData() == "Coin"){
					setBodyToDestroy(contact.getFixtureA().getBody());
				}
				if(userData == "Platform"){
					contact.getFixtureB().setFriction(0.0f);
					contact.getFixtureB().setDensity(0.0f);
				}
			}
		}
		
		@Override
		public void preSolve(Contact contact, Manifold oldManifold) {
			if(contact.getFixtureA().getUserData() == "Player" || contact.getFixtureB().getUserData() == "Player"){
				canJump = true;
			}
			if(contact.getFixtureA().getUserData() == "Player"){
				String userData = (String)contact.getFixtureB().getUserData();
				if(userData == "Platform"){
					contact.getFixtureA().getBody().setLinearVelocity(contact.getFixtureA().getBody().getLinearVelocity().x  + contact.getFixtureB().getBody().getLinearVelocity().x/10, contact.getFixtureA().getBody().getLinearVelocity().y);
				}
			}
			if(contact.getFixtureB().getUserData() == "Player"){
				String userData = (String)contact.getFixtureA().getUserData();
				if(userData.startsWith("Platform")){
					contact.getFixtureB().getBody().setLinearVelocity(contact.getFixtureB().getBody().getLinearVelocity().x + contact.getFixtureA().getBody().getLinearVelocity().x/10, contact.getFixtureA().getBody().getLinearVelocity().y);
				}
			}
			
			
		}
		
		@Override
		public void postSolve(Contact contact, ContactImpulse impulse) {
			if(contact.getFixtureA().getUserData() == "Player" || contact.getFixtureB().getUserData() == "Player"){
				canJump = true;
			}
		}

		public boolean canJump() {
			return canJump;
		}
		
		public void setCanJump(boolean value){
			canJump = value;
		}

		public Body getBodyToDestroy() {
			return bodyToDestroy;
		}

		public void setBodyToDestroy(Body bodyToDestroy) {
			this.bodyToDestroy = bodyToDestroy;
		}

};