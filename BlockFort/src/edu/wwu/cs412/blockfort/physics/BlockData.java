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
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

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
	
	private Vec2 Force = new Vec2(0,0);//These are the three movement variables. I am attempting the midpoint integration method first.
	private Vec2 acc = new Vec2(0,0);//Second I will try to implement RK4.
	private Vec2 vel = new Vec2(0,0);

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
	
	public Body getBody() {
		return this.body;
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
		relativePoint.x = worldPoint.x - relativePoint.x;
		relativePoint.y = worldPoint.y - relativePoint.y;

		// take the position on this block where the worldPoint is touching.
		// now rotate the shape, along with this point, back to it's default
		// rotation.  This new point is the value returned.
		//relativePoint.x /= Math.cos(-body.getAngle());
		//relativePoint.y /= Math.sin(-body.getAngle());
		//I am taking this out for now and trying something new.
		/*float distance = (float) Math.sqrt(
				Math.pow(relativePoint.x,2)+Math.pow(relativePoint.x,2));
		float angle = (float)Math.acos(relativePoint.x / distance);
		angle -= body.getAngle();*/ // corrects for current rotation
				
		return relativePoint;
	}
	
	
}
