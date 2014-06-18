package edu.wwu.cs412.blockfort;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Scanner;

import org.jbox2d.common.Vec2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;
import edu.wwu.cs412.blockfort.graphics.GameView;
import edu.wwu.cs412.blockfort.graphics.Sprite;
import edu.wwu.cs412.blockfort.physics.BlockData;
import edu.wwu.cs412.blockfort.physics.PhysicsZone;

public class GameActivity extends FragmentActivity {
	@SuppressWarnings("unused")
	private GameMenuFragment gameMenuFragment;
	@SuppressWarnings("unused")
	private GameFragment 	 gameFragment;
	private GameView		 gameView;
	
	// Block buttons
	ImageView arch3;
	ImageView arch5;
	ImageView arch7;
	ImageView circle3;
	ImageView circle5;
	ImageView circle7;
	ImageView circle11;
	ImageView circleRubber3;
	ImageView circleRubber5;
	ImageView circleRubber7;
	ImageView circleRubber11;
	ImageView rect3;
	ImageView rect5;
	ImageView rect7;
	ImageView rectMed3;
	ImageView rectMed5;
	ImageView rectLong3;
	ImageView rectLong5;
	ImageView square3;
	ImageView square5;
	ImageView square7;
	ImageView squareIce3;
	ImageView squareIce5;
	ImageView squareIce7;
	ImageView triangle3;
	ImageView triangle5;
	ImageView triangle7;
	ImageView triangleRight3;
	ImageView triangleRight5;
	ImageView triangleRight7;
	
	
	int draggedBlockType;
	int draggedBlockDrawable;

	int Load;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("GameActivity", "in onCreate");
		Intent intent = getIntent();
		
		// Get the name of the game to load
		String gameToLoad = intent.getStringExtra("Load");

		//Load = intent.getIntExtra("Load", 0);

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_game);
		FragmentManager fm = getSupportFragmentManager();
		gameFragment = (GameFragment) fm.findFragmentById(R.id.gameFragment);
		gameMenuFragment = (GameMenuFragment) fm.findFragmentById(R.id.gameMenuFragment);
		gameView = (GameView) findViewById(R.id.gameView);
		
		// If the game to load name is not null, load from the JSON file
		if (gameToLoad != null) {
			jsonLoad(gameToLoad);
		}
		/*if(Load > 0){
			Load();
			Log.d("CHECK", ""+Load);
		}	*/

		findViewById(R.id.gameView).setOnDragListener(new BlockDragListener());

		arch3 = (ImageView) findViewById(R.id.block_arch_3);
		arch5 = (ImageView) findViewById(R.id.block_arch_5);
		arch7 = (ImageView) findViewById(R.id.block_arch_7);
		circle3 = (ImageView) findViewById(R.id.block_circle_3);
		circle5 = (ImageView) findViewById(R.id.block_circle_5);
		circle7 = (ImageView) findViewById(R.id.block_circle_7);
		circle11 = (ImageView) findViewById(R.id.block_circle_11);
		circleRubber3 = (ImageView) findViewById(R.id.block_circle_rubber_3);
		circleRubber5 = (ImageView) findViewById(R.id.block_circle_rubber_5);
		circleRubber7 = (ImageView) findViewById(R.id.block_circle_rubber_7);
		circleRubber11 = (ImageView) findViewById(R.id.block_circle_rubber_11);
		rect3 = (ImageView) findViewById(R.id.block_rect_3);
		rect5 = (ImageView) findViewById(R.id.block_rect_5);
		rect7 = (ImageView) findViewById(R.id.block_rect_7);
		rectMed3 = (ImageView) findViewById(R.id.block_rect_med_3);
		rectMed5 = (ImageView) findViewById(R.id.block_rect_med_5);
		rectLong3 = (ImageView) findViewById(R.id.block_rect_long_3);
		rectLong5 = (ImageView) findViewById(R.id.block_rect_long_5);
		square3 = (ImageView) findViewById(R.id.block_square_3);
		square5 = (ImageView) findViewById(R.id.block_square_5);
		square7 = (ImageView) findViewById(R.id.block_square_7);
		squareIce3 = (ImageView) findViewById(R.id.block_square_ice_3);
		squareIce5 = (ImageView) findViewById(R.id.block_square_ice_5);
		squareIce7 = (ImageView) findViewById(R.id.block_square_ice_7);
		triangle3 = (ImageView) findViewById(R.id.block_triangle_3);
		triangle5 = (ImageView) findViewById(R.id.block_triangle_5);
		triangle7 = (ImageView) findViewById(R.id.block_triangle_7);
		triangleRight3 = (ImageView) findViewById(R.id.block_triangle_right_3);
		triangleRight5 = (ImageView) findViewById(R.id.block_triangle_right_5);
		triangleRight7 = (ImageView) findViewById(R.id.block_triangle_right_7);
		

		arch3.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.arch_3);
		arch5.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.arch_5);
		arch7.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.arch_7);
		circle3.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.circle_3);
		circle5.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.circle_5);
		circle7.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.circle_7);
		circle11.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.circle_11);
		circleRubber3.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.circle_pink_3);
		circleRubber5.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.circle_pink_5);
		circleRubber7.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.circle_pink_7);
		circleRubber11.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.circle_pink_11);
		rect3.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.rect_3);
		rect5.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.rect_5);
		rect7.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.rect_7);
		rectMed3.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.rect_med_3);
		rectMed5.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.rect_med_5);
		rectLong3.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.rect_long_3);
		rectLong5.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.rect_long_5);
		square3.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.square_3);
		square5.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.square_5);
		square7.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.square_7);
		squareIce3.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.square_ice_3);
		squareIce5.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.square_ice_5);
		squareIce7.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.square_ice_7);
		triangle3.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.triangle_3);
		triangle5.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.triangle_5);
		triangle7.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.triangle_7);
		triangleRight3.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.triangle_right_3);
		triangleRight5.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.triangle_right_5);
		triangleRight7.setTag(R.id.BLOCK_DRAWABLE_TAG, R.drawable.triangle_right_7);


		arch3.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_ARCH|
				3);
		arch5.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_ARCH|
				5);
		arch7.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_ARCH|
				7);
		circle3.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_CIRCLE|
				3);
		circle5.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_CIRCLE|
				5);
		circle7.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_CIRCLE|
				7);
		circle11.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_CIRCLE|
				11);
		circleRubber3.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_RUBBER|
				BlockData.BLOCK_SHAPE_CIRCLE|
				3);
		circleRubber5.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_RUBBER|
				BlockData.BLOCK_SHAPE_CIRCLE|
				5);
		circleRubber7.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_RUBBER|
				BlockData.BLOCK_SHAPE_CIRCLE|
				7);
		circleRubber11.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_RUBBER|
				BlockData.BLOCK_SHAPE_CIRCLE|
				11);
		rect3.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_RECTANGLE_SHORT|
				3);
		rect5.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_RECTANGLE_SHORT|
				5);
		rect7.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_RECTANGLE_SHORT|
				7);
		rectMed3.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_RECTANGLE_MEDIUM|
				3);
		rectMed5.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_RECTANGLE_MEDIUM|
				5);
		rectLong3.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_RECTANGLE_LONG|
				3);
		rectLong5.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_RECTANGLE_LONG|
				5);
		square3.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_SQUARE|
				3);
		square5.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_SQUARE|
				5);
		square7.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_SQUARE|
				7);
		squareIce3.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_ICE|
				BlockData.BLOCK_SHAPE_SQUARE|
				3);
		squareIce5.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_ICE|
				BlockData.BLOCK_SHAPE_SQUARE|
				5);
		squareIce7.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_ICE|
				BlockData.BLOCK_SHAPE_SQUARE|
				7);
		triangle3.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_TRIANGLE|
				3);
		triangle5.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_TRIANGLE|
				5);
		triangle7.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_TRIANGLE|
				7);
		triangleRight3.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_TRIANGLE_RIGHT|
				3);
		triangleRight5.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_TRIANGLE_RIGHT|
				5);
		triangleRight7.setTag(R.id.BLOCK_TYPE_TAG,
				BlockData.BLOCK_MATTER_WOOD|
				BlockData.BLOCK_SHAPE_TRIANGLE_RIGHT|
				7);
		
		
		findViewById(R.id.hideMenuButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				findViewById(R.id.gameMenuContent).setVisibility(View.GONE);
				
			}
		});
		
		findViewById(R.id.blocksGameMenuButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				findViewById(R.id.gameMenuContent).setVisibility(View.VISIBLE);
				
			}
		});
		
		findViewById(R.id.optionsGameMenuButton).setOnClickListener(new OnClickListener() {//Big note here. Since we aren't using options I switched this to save, at least for now.
			
			@Override
			public void onClick(View v) {
				Save();				
			}
		});
		
		findViewById(R.id.saveJsonMenuButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				jsonSave();				
			}
		});
	}
	
	// Method to save the current state of a game into a JSONObject
	public void jsonSave() {
		// Lists of sprites and blocks
		ArrayList<Sprite> spriteList = gameView.SaveData();
		ArrayList<BlockData> blockList = PhysicsZone.getPhysicsZone().saveData();
		
		if (spriteList.size() > 0) {
			// Open the save file
			File file = getFileStreamPath("BFJsonSave.dat");
			JSONArray saveArray = null;
			
			// If the file exists, we need to read the JSONArray from the file
			if (file.exists()) {
				try {
					Scanner scanner = new Scanner(file);
					StringBuilder builder = new StringBuilder();
					while (scanner.hasNextLine()) {
						builder.append(scanner.nextLine());
					}
					saveArray = new JSONArray(builder.toString());
					scanner.close();
				} catch (FileNotFoundException | JSONException e) {
					e.printStackTrace();
				}
			// Otherwise, the array will be a new JSONArray
			} else {
				saveArray = new JSONArray();
			}
			// newSaveObject is the current game being saved
			JSONObject newSaveObject = new JSONObject();
			// blockArray is the array of blocks for the current game
			JSONArray blockArray = new JSONArray();
			// Files will be saved as SaveGameN where n=the count of saved games
			String saveName = String.format("SaveGame%3d", saveArray.length()+1);
			try {
				// add the name to key "name"
				newSaveObject.put("name", saveName);
				// for each block...
				for (int i=0; i<spriteList.size(); i++) {
					// each block is represented in the save file as a JSONObject
					JSONObject singleBlockObject = new JSONObject();
					// get the corresponding block form the sprite and block lists
					Sprite sprite = spriteList.get(i);
					BlockData block = blockList.get(i);
					int[] spriteData = sprite.data();
					
					// save sprite information
					int Rid = spriteData[0];
					int spriteX = spriteData[1];
					int spriteY = spriteData[2];
					double spriteAng = ((Float)sprite.angData()).doubleValue();
					
					singleBlockObject.put("Rid", Rid);
					singleBlockObject.put("spriteX", spriteX);
					singleBlockObject.put("spriteY", spriteY);
					singleBlockObject.put("spriteAng", (Double)spriteAng);
					
					// save block information
					// casting is due to the types that JSONObjects allow
					int blockType = block.getBlockType();
					double blockAngularVelocity = ((Float)block.getAngularVelocity()).doubleValue();
					Vec2 blockLinearVelocity = block.getLinearVelocity();
					double blockLinearVelocityX = ((Float)blockLinearVelocity.x).doubleValue();
					double blockLinearVelocityY = ((Float)blockLinearVelocity.y).doubleValue();
					Vec2 blockPosition = block.getPosition();
					double blockPositionX = ((Float)blockPosition.x).doubleValue();
					double blockPositionY = ((Float)blockPosition.y).doubleValue();
					double blockRotation = ((Float)block.getRotation()).doubleValue();
					
					singleBlockObject.put("blockType", blockType);
					singleBlockObject.put("blockAngularVelocity", (Double)blockAngularVelocity);
					singleBlockObject.put("blockLinearVelocityX", (Double)blockLinearVelocityX);
					singleBlockObject.put("blockLinearVelocityY", (Double)blockLinearVelocityY);
					singleBlockObject.put("blockPositionX", (Double)blockPositionX);
					singleBlockObject.put("blockPositionY", (Double)blockPositionY);
					singleBlockObject.put("blockRotation", (Double)blockRotation);					
					
					blockArray.put(singleBlockObject);
				}
				// at this point blockArray contains a JSONObject for each block, save it to newSaveObject as "data"
				newSaveObject.put("data", blockArray);
				// add the newSaveObject to the main JSONArray at the end
				saveArray.put(saveArray.length(), newSaveObject);
				String saveString = saveArray.toString();
				// Now write to the file
				FileOutputStream output = openFileOutput("BFJsonSave.dat", Context.MODE_PRIVATE);
				output.write(saveString.getBytes());
				output.flush();
				output.close();
				Toast.makeText(this, "Game Saved - " + saveName, Toast.LENGTH_SHORT).show();
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}			
		}
	}
	
	// Method to load a game from the save file
	// We will search the file by gameName parameter
	public void jsonLoad(String gameName) {
		try {
			// Open and read the file, and get the JSONArray
			File file = getFileStreamPath("BFJsonSave.dat");
			Scanner scanner = new Scanner(file);
			StringBuilder builder = new StringBuilder();
			while (scanner.hasNextLine()) {
				builder.append(scanner.nextLine());
			}
			JSONArray saveArray = new JSONArray(builder.toString());
			scanner.close();
			// Loop through all the saved games
			for (int i=0; i<saveArray.length(); i++) {
				// If name key matches the game we want load the game
				JSONObject savedGameObject = saveArray.getJSONObject(i);
				if (savedGameObject.getString("name").equals(gameName)) {
					
					// Get the list of blocks
					JSONArray blockArray = savedGameObject.getJSONArray("data");
					// Loop through the blocks and add them to the physics zone then the gameview
					// Casting here and in save is due to the types that JSONObjects allow
					for (int ii=0; ii<blockArray.length(); ii++) {
						JSONObject blockData = blockArray.getJSONObject(ii);
						
						// Get physics data and create the block
						int blockType = blockData.getInt("blockType");
						float blockAngularVelocity = ((Double)blockData.getDouble("blockAngularVelocity")).floatValue();
						float blockLinearVelocityX = ((Double)blockData.getDouble("blockLinearVelocityX")).floatValue();
						float blockLinearVelocityY = ((Double)blockData.getDouble("blockLinearVelocityY")).floatValue();
						Vec2 blockLinearVelocity = new Vec2(blockLinearVelocityX, blockLinearVelocityY);
						float blockPositionX = ((Double)blockData.getDouble("blockPositionX")).floatValue();
						float blockPositionY = ((Double)blockData.getDouble("blockPositionY")).floatValue();
						Vec2 blockPosition = new Vec2(blockPositionX, blockPositionY);
						float blockRotation = ((Double)blockData.getDouble("blockRotation")).floatValue();
						
						PhysicsZone.getPhysicsZone().createBlock(blockType, blockPosition, blockRotation, 
								blockLinearVelocity, blockAngularVelocity);
						
						// Get sprite data and create the sprite
						int Rid = blockData.getInt("Rid");
						int spriteX = blockData.getInt("spriteX");
						int spriteY = blockData.getInt("spriteY");
						float spriteAng = ((Double)blockData.getDouble("spriteAng")).floatValue();
						
						Log.d("GameActivity", "Rid: " + Rid + " spriteX: " + spriteX + " spriteY: " + spriteY + " spriteAng: " + spriteAng);
						if (gameView == null) {
							Log.d("GameActivity", "gameView is null");
						}
						gameView.spriteList.add(new Sprite(gameView, Rid, spriteX, spriteY, spriteAng));						
					}
				}
			}
		} catch (FileNotFoundException | JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void Save(){
		ArrayList<Sprite> Slist = gameView.SaveData();
		ArrayList<BlockData> Blist = PhysicsZone.getPhysicsZone().saveData();
		if(Slist.size() > 0){
			try{
				FileOutputStream fos = openFileOutput("BFdata.dat", Context.MODE_PRIVATE);//Create a new File Output Stream
				ObjectOutputStream oos = null;
				try {
					oos = new ObjectOutputStream(fos);
					oos.writeInt(Slist.size());	
					//Log.d("CHECK", "GOT HERE  " + Slist.size());
					for(Sprite s : Slist){
						int[] dat = s.data();
						float ang = s.angData();
						for(int i = 0; i < 3; i++){
							oos.writeInt(dat[i]);//Writes the objects
						}
						oos.writeFloat(ang);
					}
					oos.writeInt(Blist.size());
					for(BlockData bd : Blist){
						//oos.writeObject(bd);
					}
					oos.flush();//Flush the stream
					oos.close();//Close the stream
					Toast.makeText(this, "Save Complete", Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e){
				
			}
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		arch3.setOnTouchListener(new BlockTouchListener());
		arch5.setOnTouchListener(new BlockTouchListener());
		arch7.setOnTouchListener(new BlockTouchListener());
		circle3.setOnTouchListener(new BlockTouchListener());
		circle5.setOnTouchListener(new BlockTouchListener());
		circle7.setOnTouchListener(new BlockTouchListener());
		circle11.setOnTouchListener(new BlockTouchListener());
		circleRubber3.setOnTouchListener(new BlockTouchListener());
		circleRubber5.setOnTouchListener(new BlockTouchListener());
		circleRubber7.setOnTouchListener(new BlockTouchListener());
		circleRubber11.setOnTouchListener(new BlockTouchListener());
		rect3.setOnTouchListener(new BlockTouchListener());
		rect5.setOnTouchListener(new BlockTouchListener());
		rect7.setOnTouchListener(new BlockTouchListener());
		rectMed3.setOnTouchListener(new BlockTouchListener());
		rectMed5.setOnTouchListener(new BlockTouchListener());
		rectLong3.setOnTouchListener(new BlockTouchListener());
		rectLong5.setOnTouchListener(new BlockTouchListener());
		square3.setOnTouchListener(new BlockTouchListener());
		square5.setOnTouchListener(new BlockTouchListener());
		square7.setOnTouchListener(new BlockTouchListener());
		squareIce3.setOnTouchListener(new BlockTouchListener());
		squareIce5.setOnTouchListener(new BlockTouchListener());
		squareIce7.setOnTouchListener(new BlockTouchListener());
		triangle3.setOnTouchListener(new BlockTouchListener());
		triangle5.setOnTouchListener(new BlockTouchListener());
		triangle7.setOnTouchListener(new BlockTouchListener());
		triangleRight3.setOnTouchListener(new BlockTouchListener());
		triangleRight5.setOnTouchListener(new BlockTouchListener());
		triangleRight7.setOnTouchListener(new BlockTouchListener());
		
	}
	
	public void onResume(){
		this.gameView.Create(this);
		super.onResume();
	}
	
	@Override
	public void onBackPressed(){
		PhysicsZone.resetPhysicsZone();
		super.onBackPressed();
	}

	private final class BlockTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				Log.d("GameActivity", "Block Touched: " + v.getId());			
				ClipData data = ClipData.newPlainText("", "");
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
				
				
				draggedBlockType = (Integer) shadowBuilder.getView().getTag(R.id.BLOCK_TYPE_TAG);
				draggedBlockDrawable = (Integer) shadowBuilder.getView().getTag(R.id.BLOCK_DRAWABLE_TAG);
				v.startDrag(data, shadowBuilder, v, 0);

				return true;
			} else {
				return false;
			}
		}		
	}

	class BlockDragListener implements OnDragListener {

		@Override
		public boolean onDrag(View v, DragEvent event) {
			if (v == null) {
				Log.d("OnDrag", "View is null");
			} else {
				Log.d("OnDrag", "View is " + v.getId());
			}
			int action = event.getAction();
			switch (action) {
			case DragEvent.ACTION_DRAG_STARTED:
				Log.d("GameActivity", "Drag Started: " + v.getId());
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				Log.d("GameActivity", "Drag Ended: " + v.getId());
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				Log.d("GameActivity", "Drag Entered: " + v.getId());
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				Log.d("GameActivity", "Drag Started: " + v.getId());
				break;
			case DragEvent.ACTION_DRAG_LOCATION:
				Log.d("GameActivity", "Drag Location: " + v.getId());
				break;
			case DragEvent.ACTION_DROP:
				Log.d("GameActivity", "Drag Drop: " + v.getId());
				// get height
				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);

				Vec2 newPosition;
				newPosition = new Vec2(event.getX(), 
						event.getY());			
				gameView.createNewBlockWithScreenCoords(draggedBlockType, newPosition, draggedBlockDrawable);									
				
				break;
			default:
				break;
			}
			return true;
		}

	}
	
	public void Load(){
		try {
			FileInputStream fis = openFileInput("BFdata.dat");//This is all very similar to Save, just input instead of output.
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(fis);
				int spriteSize = ois.readInt();
				for(int i = 0; i < spriteSize; i++){
					gameView.spriteList.add(new Sprite(gameView, ois.readInt(), ois.readInt(), ois.readInt(), ois.readFloat()));//Reads Rid, than x, y and finally angle.
				}
				ois.close();
				Toast.makeText(this, "Load Complete", Toast.LENGTH_SHORT).show();
			} catch (StreamCorruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}