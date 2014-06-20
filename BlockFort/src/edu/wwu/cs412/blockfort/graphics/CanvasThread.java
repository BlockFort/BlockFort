package edu.wwu.cs412.blockfort.graphics;

import java.util.Timer;
import java.util.TimerTask;

import org.jbox2d.common.Vec2;

import edu.wwu.cs412.blockfort.physics.PhysicsZone;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class CanvasThread extends Thread {
	private SurfaceHolder SH;
	private GameView GV;
	private boolean running = false;
	private Timer gameTimer;
	private PhysicsZone physics;
	
	public CanvasThread(SurfaceHolder sh, GameView gv, PhysicsZone pz){
		SH = sh;
		GV = gv;
		physics = pz;
	}
	
	//Sets this thread to running
	public void onExit(){
		if (gameTimer != null) {
			gameTimer.cancel();
			//gameTimer = null;
		}
	}
	
	@Override
	public void run(){
		// create a timer and schedule a new task, so that the timer
		// will cause a gamestep to happen at a regular interval.
		gameTimer = new Timer();
		gameTimer.schedule(new TimerTask(){
			@Override
			public void run() {update();}},
		0, 33);
	}
	
	public void update(){
		Canvas canvas;
		canvas = null;
		try{
			//Try to draw everything on the canvas
			canvas = SH.lockCanvas(null);
			synchronized(SH){
				
				physics.step();
				
				// apply physics pulling effects, if applicable
				physics.pushBlock(
						GV.getTouchedBlockID(),
						GV.getRelativePos(),
						GV.getFingerPosition()
						);
				
				GV.destroyOffscreenBlocks();
				
				
				//TODO game-logic should update after each physics step
				// for example, blocks that are too far in any direction 
				// should vanish (the physics does not do this automatically).  
				//TODO graphics should update after each physics step.
				// you can get the graphics data for each object by calling
				// physics.getBlockPosition(blockID).  The id is an 
				// integer.  A test block is currently created with an
				// id of 0.
				GV.onDraw(canvas);
			}
		} finally {
			if (canvas != null){
				//Keeps Canvas from locking up
				SH.unlockCanvasAndPost(canvas);
			}
		}
	}
	
}
