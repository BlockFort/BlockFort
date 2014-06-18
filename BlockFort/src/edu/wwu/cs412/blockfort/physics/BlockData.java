/**
 * Data for a single movable block structure.
 * Created by Dana Vold on April 26, 2014
 * for the Block Fort game being created in
 * cs412 spring 2014, wwu.
 * Project by Dana Vold, Max Hampton, and Alex Freedman
 * 
 * This class represents a single block.  It keeps
 * track of data beyond what box2d normally would,
 * such as the graphical information for the object.
 * 
 * The Body object is the key part of this class.
 * The body's "userdata" variable should link
 * to it's corresponding BlockData.
 * 
 */

package edu.wwu.cs412.blockfort.physics;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import android.graphics.BitmapFactory;
import android.util.Log;
import edu.wwu.cs412.blockfort.graphics.GameView;
import edu.wwu.cs412.blockfort.graphics.Sprite;

public class BlockData {

	
	// 1st 4 bits describe a block's size
	public static final int BLOCK_SIZE_VERY_TINY = 0;
	public static final int BLOCK_SIZE_TINY = 1;
	public static final int BLOCK_SIZE_VERY_SMALL = 2;
	public static final int BLOCK_SIZE_SMALLER = 3;
	public static final int BLOCK_SIZE_SMALL = 4;
	public static final int BLOCK_SIZE_MEDIUM_SMALL = 5;
	public static final int BLOCK_SIZE_MEDIUM = 6;
	public static final int BLOCK_SIZE_MEDIUM_LARGE = 7;
	public static final int BLOCK_SIZE_LARGE = 8;
	public static final int BLOCK_SIZE_LARGER = 9;
	public static final int BLOCK_SIZE_VERY_LARGE = 10;
	public static final int BLOCK_SIZE_HUGE = 11;
	public static final int BLOCK_SIZE_VERY_HUGE = 12;
	public static final int BLOCK_SIZE_COLOSSAL = 13;
	public static final int BLOCK_SIZE_VERY_COLOSSAL = 14;
	public static final int BLOCK_SIZE_MAXIMUM = 15;

	// 2nd 4 bits describe the material of a shape
	public static final int BLOCK_MATTER_WOOD = 0<<4;
	public static final int BLOCK_MATTER_STEEL = 1<<4;
	public static final int BLOCK_MATTER_STONE = 2<<4;
	public static final int BLOCK_MATTER_CARDBOARD = 3<<4;
	public static final int BLOCK_MATTER_PLASTIC = 4<<4;
	public static final int BLOCK_MATTER_RUBBER = 5<<4;
	public static final int BLOCK_MATTER_COTTON = 6<<4;
	public static final int BLOCK_MATTER_ICE = 7<<4;
	public static final int BLOCK_MATTER_BALLOON = 8<<4;
	
	// final 8 bits determine the block's shape
	public static final int BLOCK_SHAPE_CIRCLE = 0<<8;
	public static final int BLOCK_SHAPE_SQUARE = 1<<8;
	public static final int BLOCK_SHAPE_RECTANGLE_SHORT = 2<<8;
	public static final int BLOCK_SHAPE_RECTANGLE_LONG = 3<<8;
	public static final int BLOCK_SHAPE_RECTANGLE_MEDIUM = 4<<8;
	public static final int BLOCK_SHAPE_TRIANGLE = 5<<8;
	public static final int BLOCK_SHAPE_TRIANGLE_RIGHT = 6<<8;
	public static final int BLOCK_SHAPE_SPIKE = 7<<8;
	public static final int BLOCK_SHAPE_SPIKE_RIGHT = 8<<8;
	public static final int BLOCK_SHAPE_ARCH = 9<<8;

	private static final float MAX_PUSH_FORCE = 100;
	private static final float FINGER_FORCE = 120;

	private Body body;
	
	private float damageTaken;
	
	private int blockType;
	
	private static final float MAX_PULL_FORCE = 12f;

	public BlockData(Body newBody, int blockType) {
		body = newBody;
	}
	
	
	public BlockData(Body newBody, int blockType, float angularVel, Vec2 linearVel, float rotation) {
		body = newBody;
		this.blockType = blockType;
		newBody.setAngularVelocity(angularVel);
		newBody.setLinearVelocity(linearVel);
		body.setTransform(newBody.getPosition(), rotation);
	}
	
	// this should be called on all blocks being created, but not during 
	// the step() function
	public void confirm() {
		body.m_userData = this;
	}
	
	// when an impact occurs, information about that impact
	// is given here.
	public void impact(double imp, BlockData hitter) {
		// this function does nothing for now.
		// other subclasses of BlockData may do something.
		return;
	}

	// apply a force to this object.  The force changes
	// linear and angular velocity based on the mass
	// of the object and it's current speed.
	// the force originates in the object's center
	// of mass.
	public void push(float xStrength, float yStrength) {
		push(xStrength, yStrength, 
				body.getWorldCenter().x,
				body.getWorldCenter().y);
	}

	public void push(int xStrength, int yStrength) {
		push((float)xStrength,(float)yStrength);
	}

	// apply a force to this object from a specific point
	public void push(float xStrength, float yStrength, 
			float xStart, float yStart) {
		body.applyForce(new Vec2(xStrength,yStrength), 
				new Vec2(xStart,yStart));
	}
	
	//Push block to a specific point if not there.
	public void pushTo(float dist, float angle, Vec2 fingerP){
		Vec2 bodyPosition = body.getPosition().clone();

		// placeholder:  this just pulls based on the object's center of mass
		float x = (fingerP.x-bodyPosition.x);
		float y = (fingerP.y-bodyPosition.y);
		
		double force = Math.sqrt(x*x+y*y);//hypotenuse 
		double forceAngle = Math.atan(y/x);
		float sign = Math.signum(x);
		
		/*if (force > MAX_PUSH_FORCE*MAX_PUSH_FORCE) {
			// if force is too large, reduce force
			force = Math.sqrt(force);
			x = (float)(MAX_PUSH_FORCE*x/force);
			y = (float)(MAX_PUSH_FORCE*y/force);
		}*/
		
		float Lessen = 2;//THIS IS WHERE I STOPPED WORKING
		if(x+y == 0){
			Lessen = 0;
		}
		else if(x+y > 0 && x+y < 2){
			Lessen = (float)1;
			Log.d("Close", "In Zone 0 to .5 - " + Lessen);
		}
		else{
			Lessen = (float)4;
			Log.d("Close", "In Zone .5 to 1.5 - " + Lessen);
		}
		Vec2 Force = new Vec2(
				(float)(sign*force*Math.cos(forceAngle)*Lessen), 
				(float)(sign*force*Math.sin(forceAngle)*Lessen));
		
		body.setLinearVelocity(Force);
}

	public void push(int xStrength, int yStrength, 
			int xStart, int yStart) {
		push((float)xStrength,(float)yStrength,
				(float)xStart,(float)yStart);
	}
	
	/**
	 * Apply a point on this block at grabP in the direction of fingerP
	 * 
	 * @param grabP
	 * @param fingerP
	 */
	public void push(float dist, float angle, Vec2 fingerP) {
		Vec2 bodyPosition = body.getPosition().clone();

		// placeholder:  this just pulls based on the object's center of mass
		float x = (fingerP.x-bodyPosition.x);
		float y = (fingerP.y-bodyPosition.y);
		
		double force = Math.sqrt(x*x+y*y);
		double forceAngle = Math.atan(y/x);
		float sign = Math.signum(x);
		if (force > MAX_PUSH_FORCE*MAX_PUSH_FORCE) {
			// if force is too large, reduce force
			force = Math.sqrt(force);
			x = (float)(MAX_PUSH_FORCE*x/force);
			y = (float)(MAX_PUSH_FORCE*y/force);
		}
		
		Vec2 grabbedPosition = new Vec2(
				(float)Math.cos(angle)*dist, 
				(float)Math.sin(angle)*dist
				);
		

		body.applyLinearImpulse(
			new Vec2(
				(float)(sign*FINGER_FORCE*Math.cos(forceAngle)), 
				(float)(sign*FINGER_FORCE*Math.sin(forceAngle))
			), 
			new Vec2(
					bodyPosition.x + grabbedPosition.x,
					bodyPosition.y + grabbedPosition.y
					)
			);
	}

	// causes the object to move at a certain speed.
	// ignores mass and inertia:  Whatever the object is,
	// and whatever it was doing before, now it will be
	// moving in the direction specified at the speed
	// specified.
	public void launch(float xSpeed, float ySpeed) {
		body.setLinearVelocity(new Vec2(xSpeed,ySpeed));
	}

	// returns this block's body's position
	public Vec2 getPosition() {
		return body.getPosition();
	}

	// returns this block's body's rotation
	public float getRotation() {
		return body.getAngle();
	}


	// returns this block's body's velocity
	public Vec2 getLinearVelocity() {
		return body.getLinearVelocity();
	}



	// returns this block's body's velocity
	public float getAngularVelocity() {
		return body.getAngularVelocity();
	}

	public float getDamageTaken() {
		return damageTaken;
	}
	
	// destroys the physics object, making this 
	// blockData unusable
	public void destroy() {
		body.getWorld().destroyBody(body);
	}
	
	public int getBlockType() {
		return blockType;
	}
	
	public boolean testPoint(Vec2 p) {
		for (Fixture f = body.getFixtureList(); f != null; f = f.getNext()) {
			if (f.testPoint(p)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * get the relative distance of a point in the world from this block's
	 * center, and also the angle of this distance as though the block
	 * had an angle of 0.
	 * 
	 * @param worldPosition
	 * @return vec2 containing distance as the x value and angle as y
	 */
	public Vec2 getRelativeDistAndAngle(Vec2 worldPoint) {
		Vec2  relativePoint = body.getPosition().clone();

		
		// initially, we get the location of worldPoint relative to
		// the middle of this block.
		relativePoint.x -= worldPoint.x;
		relativePoint.y -= worldPoint.y;

		// take the position on this block where the worldPoint is touching.
		// now rotate the shape, along with this point, back to it's default
		// rotation.  This new point is the value returned.
		//relativePoint.x /= Math.cos(-body.getAngle());
		//relativePoint.y /= Math.sin(-body.getAngle());
		
		float distance = (float) Math.sqrt(
				Math.pow(relativePoint.x,2)+Math.pow(relativePoint.x,2));
		float angle = (float)Math.acos(relativePoint.x / distance);
		angle -= body.getAngle(); // corrects for current rotation
		
		return new Vec2(distance,angle);
	}
	
	
}
