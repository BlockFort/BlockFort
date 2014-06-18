/**
 * Class implementing android jbox2d for physics
 * Created by Dana Vold on April 26, 2014
 * for the Block Fort game being created in
 * cs412 spring 2014, wwu.
 * Project by Dana Vold, Max Hampton, and Alex Freedman
 * 
 * This class simulates a physics world.  It does
 * NOT simulate any actual graphics.  At any given
 * moment, you can ask this class for what is going
 * on in the physics world, such as coordinates and
 * rotation of shapes.  
 * 
 */

package edu.wwu.cs412.blockfort.physics;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import edu.wwu.cs412.blockfort.graphics.GameView;
import edu.wwu.cs412.blockfort.graphics.Sprite;
import android.util.Log;


public class PhysicsZone {
	
	private static  PhysicsZone physics = null;
	
	private World physicsWorld;
	
	private static final Vec2 BASIC_GRAVITY = new Vec2(0,-10);

	public static final float FLOOR_WIDTH = 26.25f;

	private static final float TIMESTEP = 1/30f;
	private static final int ITERATIONS_V = 8;
	private static final int ITERATIONS_P = 3;
	
	/**
	 * One physics unit equals SCALING pixels
	 * 
	 * If scaling is small, everything will appear to be really zoomed out
	 * If scaling is large, everything will be really large.
	 * 
	 * Sprites must match scaling independently.  If scaling is large, and
	 * sprites are small, then you will see a bunch of tiny objects that
	 * bounce against each other when they are still far away, like objects
	 * encased in perfectly clear glass.
	 * 
	 */
	public static final float SCALING = 80f;
	
	private Body ground;
	
	private boolean midStep = false;
	
	private ArrayList<BlockData> blockList;

	private ArrayList<Vec2> blockCreationPositionQueue = new ArrayList<Vec2>();
	private ArrayList<Integer> blockCreationTypeQueue = new ArrayList<Integer>();
	
	
	
	
	/**
	 * This creates a new PhysicsZone and activates
	 * it immediately.  It does not contain it's own
	 * thread or timer or anything though.  
	 * 
	 * What needs to happen is as follows:  Create
	 * a PhysicsZone, and then at regular intervals,
	 * call the step() function.  This will cause a
	 * small interval of time to pass.  
	 * 
	 * The TIMESTEP variable tells how much time has
	 * passed between each step.  So if TIMESTEP is
	 * 1/30, that means you call step() 30 times per
	 * second.  If TIMESTEP is 1/60, then you should
	 * call step() 60 times per second.  
	 * 
	 */
	private PhysicsZone() {		
		// create the actual physics world
		physicsWorld = new World(BASIC_GRAVITY);
		createGround();
		
		// creates the list of blocks
		blockList = new ArrayList<BlockData>();
		
	}
	
	/**
	 * @return A PhysicsZone, which will always be the
	 * same physics zone as that sent to any other
	 * call to getPhysicsZone.
	 */
	public static PhysicsZone getPhysicsZone() {
		if (physics == null) {
			physics = new PhysicsZone();
		}
		return physics;
	}
	
	public ArrayList<BlockData> saveData(){//Grabs the required data out of here.
		return blockList;
	}
	
	/**
	 * resets the physicsWorld
	 */
	public static void resetPhysicsZone() {
		physics = new PhysicsZone();
	}
	
	//  creates a floor area for objects to fall upon
	private void createGround() {
		// creates a ground object
		BodyDef groundDef = new BodyDef();
		groundDef.type = BodyType.STATIC;
		groundDef.position.set(0,0);

		Vec2[] vertices = {
			new Vec2(          0f,  2f),
			new Vec2(          0f, -4f),
			new Vec2( FLOOR_WIDTH, -4f),
			new Vec2( FLOOR_WIDTH,  2f)
		};
		
		// creates shapes
		PolygonShape floorShape = new PolygonShape();
		floorShape.set(vertices, vertices.length);
		
		// creates fixtures
		FixtureDef groundFix = new FixtureDef();
		groundFix.shape = floorShape;
		groundFix.density = 0f;
		groundFix.friction = 12/16f;
		groundFix.restitution = 1/8f;
		
		ground = physicsWorld.createBody(groundDef);
		ground.createFixture(groundFix);
		
		// walls and ceiling are for debug purposes
		//addWalls(ground);
	}
	
	private void addWalls(Body ground) {


		// create extra fixtures: walls and ceilings
		// ceiling
		Vec2[] vertices2 = {
			new Vec2(          0f,  FLOOR_WIDTH+6f),
			new Vec2(          0f,  FLOOR_WIDTH+0f),
			new Vec2( FLOOR_WIDTH,  FLOOR_WIDTH+0f),
			new Vec2( FLOOR_WIDTH,  FLOOR_WIDTH+6f)
		};
		PolygonShape floorShape2 = new PolygonShape();
		floorShape2.set(vertices2, vertices2.length);
		FixtureDef groundFix2 = new FixtureDef();
		groundFix2.shape = floorShape2;
		groundFix2.density = 0f;
		groundFix2.friction = 5/16f;
		groundFix2.restitution = 1/2f;
		ground.createFixture(groundFix2);

		// lwall
		Vec2[] vertices3 = {
			new Vec2(          0f,  FLOOR_WIDTH),
			new Vec2(          0f,  0f),
			new Vec2(          3f,  0f),
			new Vec2(          3f,  FLOOR_WIDTH)
		};
		PolygonShape floorShape3 = new PolygonShape();
		floorShape3.set(vertices3, vertices3.length);
		FixtureDef groundFix3 = new FixtureDef();
		groundFix3.shape = floorShape3;
		groundFix3.density = 0f;
		groundFix3.friction = 5/16f;
		groundFix3.restitution = 1/2f;
		ground.createFixture(groundFix3);
		

		// rwall
		Vec2[] vertices4 = {
			new Vec2(  FLOOR_WIDTH+0f,  FLOOR_WIDTH),
			new Vec2(  FLOOR_WIDTH+0f,  0f),
			new Vec2(  FLOOR_WIDTH+3f,  0f),
			new Vec2(  FLOOR_WIDTH+3f,  FLOOR_WIDTH)
		};
		PolygonShape floorShape4 = new PolygonShape();
		floorShape4.set(vertices4, vertices4.length);
		FixtureDef groundFix4 = new FixtureDef();
		groundFix4.shape = floorShape4;
		groundFix4.density = 0f;
		groundFix4.friction = 5/16f;
		groundFix4.restitution = 1/2f;
		ground.createFixture(groundFix4);
		
	}
	
	/**
	 * Causes a small period of time to pass in
	 * the physics world.
	 */
	public void step() {
		// creates all shapes

		for (int i = 0; i < blockCreationTypeQueue.size(); i++) {
			createBlock(blockCreationTypeQueue.get(i),blockCreationPositionQueue.get(i));
		}
		blockCreationPositionQueue.clear();
		blockCreationTypeQueue.clear();
		// does the main step
		midStep = true;
		physicsWorld.step(TIMESTEP, ITERATIONS_V, ITERATIONS_P);
		midStep = false;
		
	}

	
	/**
	 * creates a new block and returns that block's id
	 * 
	 * @param blockTypeID
	 * @param position
	 * @return blockID
	 */
	public int queueNewBlock(int blockTypeID, Vec2 position) {
		blockCreationPositionQueue.add(position);
		blockCreationTypeQueue.add(blockTypeID);
		return blockList.size()+blockCreationTypeQueue.size()-1;
	}
	
	/**
	 * creates a new block and returns that block's id
	 * 
	 * @param blockTypeID
	 * @param position
	 * @return blockID
	 */
	private int createBlock(int blockTypeID, Vec2 position) {
		return createBlock(blockTypeID, position, 0);
	}

	private int createBlock(int blockTypeID, Vec2 position, float rotation) {
		return createBlock(blockTypeID, position, rotation, new Vec2(0,0), 0);
	}
	
	/**
	 * creates a new block and returns that block's id.
	 * The block ID should be defined using the static values
	 * associated with PhysicsZone, such as BLOCK_MATTER_WOOD
	 * and BLOCK_SHAPE_SQUARE.
	 * 
	 * So to get a large square wooden block, blockTypeID
	 * should be BLOCK_MATTER_WOOD | BLOCK_SHAPE_SQUARE |
	 * BLOCK_SIZE_LARGE
	 * 
	 * @param blockTypeID tells you what sort of block is being created
	 * @param position
	 * @return blockID
	 */
	public int createBlock(int blockTypeID, Vec2 position, float rotation,
			Vec2 linearVelocity, float angularVelocity) {
		
		BodyDef blockDef = new BodyDef();
		blockDef.type = BodyType.DYNAMIC;
		blockDef.position.set(position);
		
		ArrayList<FixtureDef> blockFixList = new ArrayList<FixtureDef>();

		int matterTypeID = ((blockTypeID>>4) % 16)<<4;
		int sizeTypeID = (blockTypeID) % 16;
		int shapeTypeID = (blockTypeID>>8)<<8;
		
		// size multiplier
		float sm = (float) ((sizeTypeID+1)*0.25);
		
		// (2 + 7)/2 *9/4
		
		// creates shapes
		if (shapeTypeID == BlockData.BLOCK_SHAPE_CIRCLE) {
			// creates a circle
			CircleShape circleDef = new CircleShape();
			circleDef.setRadius(sm*0.5f);

			FixtureDef blockFix = new FixtureDef();
			blockFix.shape = circleDef;
			blockFixList.add(blockFix);
			
		} else if (shapeTypeID == BlockData.BLOCK_SHAPE_ARCH) {
			// creates an arch shape, which consists of multiple fixtures
			Vec2[] vertices1 = {
					new Vec2(  sm * -12/8f,  sm *  0/8f),
					new Vec2(  sm * -12/8f,  sm * -6/8f),
					new Vec2(  sm * -6/8f,  sm * -6/8f),
					new Vec2(  sm * -6/8f,  sm *  0/8f)
				};
			PolygonShape polyShape1 = new PolygonShape();
			polyShape1.set(vertices1, vertices1.length);
			FixtureDef blockFix1 = new FixtureDef();
			blockFix1.shape = polyShape1;

			Vec2[] vertices2 = {
					new Vec2(  sm *  6/8f,  sm *  0/8f),
					new Vec2(  sm *  6/8f,  sm * -6/8f),
					new Vec2(  sm *  12/8f,  sm * -6/8f),
					new Vec2(  sm *  12/8f,  sm *  0/8f)
				};
			PolygonShape polyShape2 = new PolygonShape();
			polyShape2.set(vertices2, vertices2.length);
			FixtureDef blockFix2 = new FixtureDef();
			blockFix2.shape = polyShape2;

			Vec2[] vertices3 = {
					new Vec2(  sm * -12/8f,  sm *  6/8f),
					new Vec2(  sm * -12/8f,  sm *  0/8f),
					new Vec2(  sm *  12/8f,  sm *  0/8f),
					new Vec2(  sm *  12/8f,  sm *  6/8f)
				};
			PolygonShape polyShape3 = new PolygonShape();
			polyShape3.set(vertices3, vertices3.length);
			FixtureDef blockFix3 = new FixtureDef();
			blockFix3.shape = polyShape3;

			blockFixList.add(blockFix1);
			blockFixList.add(blockFix2);
			blockFixList.add(blockFix3);
		} else if (shapeTypeID == BlockData.BLOCK_SHAPE_SQUARE) {
			// creates a square shape
			PolygonShape polyShape = new PolygonShape();
			polyShape.setAsBox(sm * 3/4f,sm * 3/4f);
			

			FixtureDef blockFix = new FixtureDef();
			blockFix.shape = polyShape;
			blockFixList.add(blockFix);
		} else if (shapeTypeID == BlockData.BLOCK_SHAPE_RECTANGLE_SHORT) {
			// creates a short rectangle shape
			Vec2[] vertices = {
				new Vec2(  sm * -4/4f,  sm *  3/4f),
				new Vec2(  sm * -4/4f,  sm * -3/4f),
				new Vec2(  sm *  4/4f,  sm * -3/4f),
				new Vec2(  sm *  4/4f,  sm *  3/4f)
			};
				
			// creates shapes
			PolygonShape polyShape = new PolygonShape();
			polyShape.set(vertices, vertices.length);

			FixtureDef blockFix = new FixtureDef();
			blockFix.shape = polyShape;
			blockFixList.add(blockFix);
		} else if (shapeTypeID == BlockData.BLOCK_SHAPE_RECTANGLE_MEDIUM) {
			// creates a medium-length rectangle shape
			Vec2[] vertices = {
				new Vec2(  sm * -6/4f,  sm *  1/4f),
				new Vec2(  sm * -6/4f,  sm * -1/4f),
				new Vec2(  sm *  6/4f,  sm * -1/4f),
				new Vec2(  sm *  6/4f,  sm *  1/4f)
			};
				
			// creates shapes
			PolygonShape polyShape = new PolygonShape();
			polyShape.set(vertices, vertices.length);

			FixtureDef blockFix = new FixtureDef();
			blockFix.shape = polyShape;
			blockFixList.add(blockFix);
		} else if (shapeTypeID == BlockData.BLOCK_SHAPE_RECTANGLE_LONG) {
			// creates a long rectangle shape
			Vec2[] vertices = {
				new Vec2(  sm * -2.85f,  sm *  1/4f),
				new Vec2(  sm * -2.85f,  sm * -1/4f),
				new Vec2(  sm *  2.85f,  sm * -1/4f),
				new Vec2(  sm *  2.85f,  sm *  1/4f)
			};
				
			// creates shapes
			PolygonShape polyShape = new PolygonShape();
			polyShape.set(vertices, vertices.length);

			FixtureDef blockFix = new FixtureDef();
			blockFix.shape = polyShape;
			blockFixList.add(blockFix);
		} else if (shapeTypeID == BlockData.BLOCK_SHAPE_TRIANGLE_RIGHT) {
			// creates a right-triangle
			Vec2[] vertices = {
				new Vec2(  sm * -3/4f,  sm *  3/4f),
				new Vec2(  sm * -3/4f,  sm * -3/4f),
				new Vec2(  sm *  3/4f,  sm * -3/4f)
			};
				
			// creates shapes
			PolygonShape polyShape = new PolygonShape();
			polyShape.set(vertices, vertices.length);

			FixtureDef blockFix = new FixtureDef();
			blockFix.shape = polyShape;
			blockFixList.add(blockFix);
		} else if (shapeTypeID == BlockData.BLOCK_SHAPE_TRIANGLE) {
			// creates an equilateral triangle
			Vec2[] vertices = {
				new Vec2(         0,  sm * 0.649519052838329f),
				new Vec2(  sm * -3/4f,  sm * -3/4f),
				new Vec2(  sm *  3/4f,  sm * 3/4f)
			};
				
			// creates shapes
			PolygonShape polyShape = new PolygonShape();
			polyShape.set(vertices, vertices.length);

			FixtureDef blockFix = new FixtureDef();
			blockFix.shape = polyShape;
			blockFixList.add(blockFix);
		} else if (shapeTypeID == BlockData.BLOCK_SHAPE_SPIKE_RIGHT) {
			// creates a right-triangle that is relatively tall
			Vec2[] vertices = {
				new Vec2(  sm * -1/4f,  sm *  4/4f),
				new Vec2(  sm * -1/4f,  sm * -1/4f),
				new Vec2(  sm *  1/4f,  sm * -1/4f)
			};
				
			// creates shapes
			PolygonShape polyShape = new PolygonShape();
			polyShape.set(vertices, vertices.length);

			FixtureDef blockFix = new FixtureDef();
			blockFix.shape = polyShape;
			blockFixList.add(blockFix);
		} else if (shapeTypeID == BlockData.BLOCK_SHAPE_SPIKE) {
			// creates an equilateral triangle
			Vec2[] vertices = {
				new Vec2(         0,  sm * 1f),
				new Vec2(  sm * -0.25f,  sm * -1f),
				new Vec2(  sm *  0.25f,  sm * -1f)
			};
				
			// creates shapes
			PolygonShape polyShape = new PolygonShape();
			polyShape.set(vertices, vertices.length);

			FixtureDef blockFix = new FixtureDef();
			blockFix.shape = polyShape;
			blockFixList.add(blockFix);
		} else {
			// placeholder shape: if an inappropriate shape is given,
			// default to a square
			PolygonShape polyShape = new PolygonShape();
			polyShape.setAsBox(sm * 3/4f,sm * 3/4f);

			FixtureDef blockFix = new FixtureDef();
			blockFix.shape = polyShape;
			blockFixList.add(blockFix);
		}
		
		float density;
		float friction;
		float restitution;

		if (matterTypeID == BlockData.BLOCK_MATTER_WOOD) {
			density = 1f;
			friction = 5/16f;
			restitution = 1/2f;
		} else if (matterTypeID == BlockData.BLOCK_MATTER_STONE) {
			density = 2.5f;
			friction = 6/16f;
			restitution = 1/3f;
		} else if (matterTypeID == BlockData.BLOCK_MATTER_ICE) {
			density = 1f;
			friction = 1/32f;
			restitution = 1/2f;
		} else if (matterTypeID == BlockData.BLOCK_MATTER_STEEL) {
			density = 3f;
			friction = 4/16f;
			restitution = 1/2f;
		} else if (matterTypeID == BlockData.BLOCK_MATTER_PLASTIC) {
			density = 0.75f;
			friction = 4/16f;
			restitution = 3/5f;
		} else if (matterTypeID == BlockData.BLOCK_MATTER_CARDBOARD) {
			density = 0.2f;
			friction = 5/16f;
			restitution = 1/2f;
		} else if (matterTypeID == BlockData.BLOCK_MATTER_RUBBER) {
			density = 1;
			friction = 5/16f;
			restitution = 7/8f;
		} else if (matterTypeID == BlockData.BLOCK_MATTER_BALLOON) {
			density = 0.125f;
			friction = 6/16f;
			restitution = 4/5f;
		} else if (matterTypeID == BlockData.BLOCK_MATTER_COTTON) {
			density = 0.25f;
			friction = 8/16f;
			restitution = 3/5f;
		} else {
			// any other material defaults using the following values
			density = 1;
			friction = 5/16f;
			restitution = 1/2f;
		}

		
		Body newBlockBody = physicsWorld.createBody(blockDef);
		
		BlockData newBlock = new BlockData(newBlockBody,blockTypeID,
				angularVelocity,linearVelocity,rotation);
		blockList.add(newBlock);
		
		// sets final properties of fixtures and adds them
		// to the new block
		for (int i = 0; i < blockFixList.size(); i++) {
			blockFixList.get(i).density = density;
			blockFixList.get(i).friction = friction;
			blockFixList.get(i).restitution = restitution;

			newBlockBody.createFixture(blockFixList.get(i));
		}
		
		newBlock.confirm();
		
		
		return blockList.size()-1;
		
	}
	
	/**
	 * destroys the given block.  This function should
	 * only be called when the block's graphics data
	 * is also being destroyed
	 * 
	 * @param blockID
	 * @return whether the block was destroyed
	 */
	public boolean destroyBlock(int blockID) {
		if (blockList.size() > blockID  && !midStep) {
			blockList.remove(blockID).destroy();
			return true; 
		} else {
			return false;
		}
	}
	
	
	public Vec2 getBlockPosition(int blockID) {
		return blockList.get(blockID).getPosition();
	}
	
	public float getBlockRotation(int blockID) {
		return blockList.get(blockID).getRotation();
	}
	
	public int getIncompleteBlockCount() {
		return blockCreationTypeQueue.size();
	}
	
	public int getBlockCount() {
		return blockList.size();
	}
	
	/**
	 * 
	 * @param position
	 * @return
	 */
	public int getBlockAtPoint(Vec2 position) {
		int count = blockList.size();
		for (int i = 0; i < count; i++) {
			if (blockList.get(i).testPoint(position)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * This function gives you the position of the given point relative to
	 * the given block's center of mass, if the block wasn't rotated.
	 * 
	 * @param blockID
	 * @param position
	 * @return position of the given point relative to the block
	 * 
	 */
	public Vec2 getRelativeDistAndAngle(int blockID, Vec2 position) {
		return blockList.get(blockID).getRelativeDistAndAngle(position);
	}
	
	/**
	 * push the given block
	 * 
	 * @param blockID
	 * @param grabP Position on the block where it is grabbed
	 * @param fingerP Position in the world where finger is
	 */
	public boolean pushBlock(int blockID, float dist, float angle, Vec2 fingerP) {

		if (blockID > -1 && !midStep) {
			blockList.get(blockID).pushTo(dist, angle, fingerP);
			return true;
		}
		return false;
	}
	
	public Vec2 getGroundPosition() {
		return new Vec2(FLOOR_WIDTH*0.5f,1.15625f);
	}
	
	
}

