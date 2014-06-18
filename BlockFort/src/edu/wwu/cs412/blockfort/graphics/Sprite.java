package edu.wwu.cs412.blockfort.graphics;
import java.io.Serializable;

import org.jbox2d.common.Vec2;

import edu.wwu.cs412.blockfort.physics.PhysicsZone;
import android.graphics.*;
import android.util.Log;

public class Sprite implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;
	
	private int Rid = 0;
	private Bitmap OrgBitmap = null;
	private Bitmap bmp = null;
		
	private int x = 0, y = 0;
	private float angle = 0;
	
	public Sprite(){//Default Constructor for serialization
	}
	
	public Sprite(GameView gameView, int toBmp){
		Rid = toBmp;
		this.bmp = BitmapFactory.decodeResource(gameView.getContext().getResources(), toBmp);
		OrgBitmap = bmp;
	}
	
	public Sprite(GameView gameView, int toBmp, int x, int y, float ang){
		Rid = toBmp;
		this.bmp = BitmapFactory.decodeResource(gameView.getContext().getResources(), toBmp);
		OrgBitmap = bmp;
		this.x = x;
		this.y = y;
		this.angle = ang;
		this.bmp = rotateBMP(-this.angle, this.x, this.y);
	}
	
	public void update(Vec2 xy, float angle, int height){
		update(xy,angle,height,new Vec2(0f,0f), 1);
	}

	
	public void update(Vec2 xy, float angle, int height, Vec2 camera, float zoom){
		// coordinates are given in physics units, which 
		// correspond to PhysicsZone.SCALING pixels each
		this.x = (int)((xy.x*PhysicsZone.SCALING - camera.x)*zoom);
		// note that we need to reverse the y coordinate so that
		// everything isn't upside-down all the time
		this.y = (int)((height - (xy.y*PhysicsZone.SCALING - camera.y))*zoom);
		this.angle = angle;
		this.angle *= 180 / Math.PI;
		// note:  the angle is reversed!  It's different in the physics
		// from what we want in the graphics.
		this.bmp = rotateBMP(-this.angle, this.x, this.y);
	}
	
	private Bitmap rotateBMP(float angle, float px, float py){//Needs a lot of work.
		Matrix matrix = new Matrix();
		matrix.setRotate(angle, bmp.getWidth()/2, bmp.getHeight()/2);
		return Bitmap.createBitmap(OrgBitmap, 0, 0, OrgBitmap.getWidth(), OrgBitmap.getHeight(), matrix, true);
	}
	
	public void Draw(Canvas canvas){
		canvas.drawBitmap(bmp, this.x-bmp.getWidth()/2, y-bmp.getHeight()/2, null);
	}
	
	public int[] data(){
		int[] data = new int[3];
		data[0] = Rid;
		data[1] = x;
		data[2] = y;
		return data;
	}
	
	public float angData(){
		return this.angle;
	}
}


