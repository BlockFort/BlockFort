package edu.wwu.cs412.blockfort.graphics;


import java.util.ArrayList;

import org.jbox2d.common.Vec2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import edu.wwu.cs412.blockfort.R;
import edu.wwu.cs412.blockfort.physics.PhysicsZone;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
	private static GameView GV;
	
	public ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
	private CanvasThread CT;
	private PhysicsZone pz;
	
	// the floor of the world
	private Sprite ground;
	
	public VelocityTracker vt = null;
	
	private Vec2 camera;
	private Vec2 cameraVelocity;
	private float cameraZoom = 1.0f;
	
	private float CAMERA_SCALING = 0.01f;

	private float MAX_ZOOM = 5.0f;
	private float MIN_ZOOM = 0.2f;
	
	private float worldWidth = 30;
	private float worldHeight = 30;

	private int skyColor;

	private static final int AUTUMN = 0;
	private static final int GREEN_GRASS = 1;
	private static final int SALMON_SKY = 2;
	
	// the following are for tracking the position of a touch
	private Vec2 touchFingerPosition = new Vec2();
	// touchGrabPosition can store a few different, but very similar
	// pieces of information. It stores where the screen has been grabbed
	// when moving the camera around, and when doing a pinch-zoom, it
	// stores the center of the pinch.
	private Vec2 touchGrabPosition = new Vec2();
	
	// When the user grabs a block, touchGrabDistance tells how far 
	// from the center of mass of the block that grab was
	private float touchGrabDistance = 0;
	// When the user grabs a block, touchGrabDistance tells the angle of
	// the grab position relative to the block's center of mass.
	private float touchGrabAngle = 0;
	
	//Putting a new Vec2 to take the place of the above two variables.
	private Vec2 relativePos = null;
	
	// touchBlockID tells which block is currently grabbed.  If no block
	// is grabbed, the id is -1.  If the camera is grabbed, it has a
	// special negative number value CAMERA_ID or CAMERA_PINCH_ID
	private int touchBlockID = -1;
	
	private final int CAMERA_ID = -2;
	private final int CAMERA_PINCH_ID = -3;
	
	public GameView(Context context) {
		super(context);
		init(context);
	}
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public static GameView getGameView(Context context){
		if(GV == null){
			GV = new GameView(context);
		}
		return GV;
	}
	
	protected static class State extends BaseSavedState { //This State is used to store the arrayList
	    protected static final String STATE = "YourCustomView.STATE";

	    private final ArrayList<Sprite> List;

	    public State(Parcelable superState, ArrayList<Sprite> List) {
	        super(superState);
	        this.List = List;
	    }

	    public ArrayList<Sprite> getList(){
	        return this.List;
	    }
	}
	
	@Override
	public Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();//Create a bundle
		State state = new State(super.onSaveInstanceState(), spriteList);//Create a state class(above) that holds the ArrayList
		bundle.putParcelable(State.STATE, state);//Put the state in the bundle
	    return bundle;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
	        Bundle bundle = (Bundle) state;
	        State CustomState = (State) bundle.getParcelable(State.STATE);
	        // The saved SpriteList
	        spriteList = CustomState.getList();
	        super.onRestoreInstanceState(CustomState.getSuperState());//Sets the arrayList
	        return;
	    }
	    super.onRestoreInstanceState(BaseSavedState.EMPTY_STATE);//Stops a bug
	}
	
	public ArrayList<Sprite> SaveData(){//Grab the sprites here.
		return spriteList;
	}

	private void init(Context context) {
		//This is a surface holder, it is for updating this surface
		getHolder().addCallback(this);

		//Grab Physics Zone and give it the current View
		pz = PhysicsZone.getPhysicsZone();

		//Create a new Thread for drawing the canvas
		CT = new CanvasThread(getHolder(), this, pz);
		setFocusable(true);
		
		// determine color scheme randomly
		int theme = (int)Math.floor(Math.random()*3);
		if (theme == AUTUMN) {
			skyColor = getResources().getColor(R.color.background_sky_grey);
			ground = new Sprite(this,R.drawable.grass_brown_10);
		} else if (theme == SALMON_SKY) {
			skyColor = getResources().getColor(R.color.background_sky_grey);
			ground = new Sprite(this,R.drawable.grass_blue_10);
		} else {
			skyColor = getResources().getColor(R.color.background_sky_blue);
			ground = new Sprite(this,R.drawable.grass_green_10);
		}

		camera = new Vec2(0,0);
		cameraVelocity = new Vec2(0,0);

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// On creation this should start the CanvasThread from above
		if (CT.getState() == Thread.State.NEW) {
			CT.start();
		}
	}
	
	public void Create(Context context){
		init(context);
		if (CT.getState() == Thread.State.NEW) {
			CT.start();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d("GameView", "SurfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//This of course is where you have to stop the thread and join it
		boolean retry = true;
		CT.onExit();
		while (retry) {
			try {
				CT.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
		
		if(!CT.isAlive()) //This proves that the Thread does exit, if you are wondering.
			Log.d("CHECK", "THIS HAPPENED! VERY IMPORTANT");
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (canvas != null) {
			
			// move camera as applicable
			if (vt != null) {
				if (cameraGrabbed()) {
					vt.computeCurrentVelocity(1000);
					// set camera velocity based on touch
					int reset = 0;
					if (vt.getXVelocity() != 0f) {
						// note that we are reversing the direction here.
						// we don't need to do this for y, since we are already
						// reversing the y direction when we draw the sprite
						cameraVelocity.x = -vt.getXVelocity();
					}
					if (vt.getYVelocity() != 0f) {
						// left corner is 0,0
						cameraVelocity.y = vt.getYVelocity();
					}
					
				} else {
					// reduce camera velocity gradually
					float speed = 1 / CAMERA_SCALING;
					if (cameraVelocity.x > speed) {
						cameraVelocity.x -= speed;
					} else if (cameraVelocity.x < -speed) {
						cameraVelocity.x += speed;
					} else {
						cameraVelocity.x = 0;
					}
					if (cameraVelocity.y > speed) {
						cameraVelocity.y -= speed;
					} else if (cameraVelocity.y < -speed) {
						cameraVelocity.y += speed;
					} else {
						cameraVelocity.y = 0;
					}
				}
				
				// moves the camera around
				camera.x += cameraVelocity.x*CAMERA_SCALING;
				camera.y += cameraVelocity.y*CAMERA_SCALING;
				if (camera.x > worldWidth*PhysicsZone.SCALING) {
					camera.x = worldWidth*PhysicsZone.SCALING;
				} else if (camera.x < -worldWidth*PhysicsZone.SCALING) {
					camera.x = -worldWidth*PhysicsZone.SCALING;
				}
				if (camera.y > worldHeight*PhysicsZone.SCALING) {
					camera.y = worldHeight*PhysicsZone.SCALING;
				} else if (camera.y < 0) {
					camera.y = 0;
				}
			}
			
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(4);
			canvas.drawColor(skyColor);
			int count = spriteList.size() - pz.getIncompleteBlockCount();
			if (count > 0){
				for(int i = 0; i < count; i++){
					//Log.d("ondraw", "x = "+pz.getBlockPosition(i).x+", y = "+pz.getBlockPosition(i).y);
					spriteList.get(i).update(pz.getBlockPosition(i), pz.getBlockRotation(i), getHeight(), camera, cameraZoom);
					spriteList.get(i).Draw(canvas);
					if(pz.getBlockDest(i) != null){
						Log.d("ondraw", pz.getBlockPosition(i) + "   " + pz.getBlockDest(i));
						canvas.drawLine(pz.getBlockPosition(i).x, pz.getBlockPosition(i).y, pz.getBlockDest(i).x, pz.getBlockDest(i).y, paint);
					}
				}
			}
			
			// draws the ground
			ground.update(pz.getGroundPosition(), 0, getHeight(),camera,cameraZoom);
			ground.Draw(canvas);
			
		}
	}
	
	
	public int createNewBlock(int blockType, Vec2 position, int drawable) {
		Sprite sprite = new Sprite(this,drawable);
		spriteList.add(sprite);
		return pz.queueNewBlock(blockType, position);
	}
	
	
	/**
	 * this is exactly the same as createNewBlock, except that
	 * the starting position originates at the top-left, not the bottom left
	 * and we need to correct for this
	 * 
	 * @param blockType
	 * @param position
	 * @return
	 */
	public int createNewBlockWithScreenCoords(int blockType, Vec2 position, int drawable) {
		// scales the position appropriately
		position.x = (position.x + camera.x)/PhysicsZone.SCALING;
		position.y = ((getHeight() - position.y) + camera.y)/PhysicsZone.SCALING;
		Sprite sprite = new Sprite(this,drawable);
		spriteList.add(sprite);
		return pz.queueNewBlock(blockType, position);
	}
	
	public boolean destroyBlock(int blockID) {
		// destroy a block and the block's graphic
		if (pz.destroyBlock(blockID)) {
			spriteList.remove(blockID);
			
			// the ids of the blocks has changed!  If you are holding onto a block,
			// that block's id may change, and this if statement corrects for that
			if (blockID < touchBlockID) {
				touchBlockID--;
			} else if (blockID == touchBlockID) {
				touchBlockID = -1;
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * destroy the block with the given id, including both the physics object
	 * and the sprite
	 */
	public void destroyOffscreenBlocks() {
		ArrayList<Integer> blocksToDelete = new ArrayList<Integer>();
		int count = pz.getBlockCount();
		for (int i = 0; i < count; i++) {
			Vec2 currentPosition = pz.getBlockPosition(i);
			if (currentPosition.x+12 > (worldWidth*1.5f+PhysicsZone.FLOOR_WIDTH*0.5f)
					|| currentPosition.x-12 < -worldWidth*1.5f+PhysicsZone.FLOOR_WIDTH*0.5f
					//||currentPosition.y > worldHeight*1.5f+PhysicsZone.FLOOR_WIDTH*0.5f
					|| currentPosition.y < -5 
					) {
				blocksToDelete.add(i);
			}
		}
		for (int i = blocksToDelete.size()-1; i > -1; i--) {
			boolean deleted = destroyBlock(blocksToDelete.get(i));
			Log.d("CHECK","THING "+i+" DELETED: "+deleted);
		}
	}
	
	/**
	 * touch handler, which checks for touches and then
	 * applies a force if that touch has grabbed a block
	 */
	@Override
	public boolean onTouchEvent(MotionEvent e) {

		// get the position touched, corrected for zoom and camera displacement
		touchFingerPosition = new Vec2(
				(e.getX()/cameraZoom + camera.x)/PhysicsZone.SCALING, 
				((getHeight() - e.getY())/cameraZoom + camera.y)/PhysicsZone.SCALING
				);

		
		int act = e.getAction() & MotionEvent.ACTION_MASK;

		if (act == MotionEvent.ACTION_UP || act == MotionEvent.ACTION_CANCEL
				|| act == MotionEvent.ACTION_POINTER_UP) {
				touchBlockID = -1;
				return false;
		} else if (act == MotionEvent.ACTION_MOVE) {
			if (touchBlockID == CAMERA_ID) {
				// camera is being dragged around
				vt.addMovement(e);
			} else if (touchBlockID == CAMERA_PINCH_ID) {
				// camera is being pinched/zoomed
				// note that there is a min and max zoom
				
				float newZoom = Math.max(Math.min(
						(float)Math.sqrt(
							Math.pow(e.getX(0) - e.getX(1),2)
							+Math.pow(e.getY(0) - e.getY(1),2)
						)/touchGrabDistance,
						MAX_ZOOM),MIN_ZOOM);
				// corrects the camera position based on the zoom
				camera.x = camera.x + touchGrabPosition.x*(cameraZoom - newZoom);
				camera.y = camera.y + touchGrabPosition.y*(cameraZoom - newZoom);
				// sets the camera zoom
				cameraZoom = newZoom;
				Log.d("CHECK","new zoom: "+cameraZoom);
			}
		/* TODO Zoom functionality is incomplete
		} else if (act == MotionEvent.ACTION_POINTER_DOWN) {
			// second touch just occurred
			// get distance between touches
			Log.d("CHECK","second touch occurs");
			touchGrabDistance = FloatMath.sqrt(
					FloatMath.pow(e.getX(0) - e.getX(1),2)
					+FloatMath.pow(e.getY(0) - e.getY(1),2)
				);
			// set touchgrabposition to be the midpoint between the two finger points
			touchGrabPosition = new Vec2(e.getX(0) + e.getX(1)*0.5f,
					e.getY(0) + e.getY(1)*0.5f);
			touchBlockID = CAMERA_PINCH_ID;*/
		} else if (act == MotionEvent.ACTION_DOWN) {
			// first, test to see if any block is being touched.
			touchBlockID = pz.getBlockAtPoint(touchFingerPosition);
			
			// if we have touched a block, record the current position
			if (touchBlockID > -1) {
				Vec2 relativeGrab = pz.getRelativeDistAndAngle(touchBlockID, touchFingerPosition);
					
				/*touchGrabDistance = relativeGrab.x; Taken out for now.
				touchGrabAngle = relativeGrab.y;*/
				
				this.relativePos = new Vec2(relativeGrab.x, relativeGrab.y);
				
			} else {
				// non-block has been grabbed, so that means the camera is grabbed
				if (vt == null) {
					vt = VelocityTracker.obtain();
				} else {
					vt.clear();
				}
				vt.addMovement(e);
				touchBlockID = CAMERA_ID;
				touchGrabPosition = touchFingerPosition.clone();
			
			}
		}
		
		return true;
	}
	
	public int getTouchedBlockID() {
		return touchBlockID;
	}

	public Vec2 getFingerPosition() {
		return touchFingerPosition;
	}

	public Vec2 getRelativePos() {
		return relativePos;
	}

	/*public float getGrabDistance() { Not needed right now
		return touchGrabDistance;
	}

	public float getGrabAngle() {
		return touchGrabAngle;
	}*/

	public boolean cameraGrabbed() {
		return touchBlockID == CAMERA_ID;
	}
	
	
}
