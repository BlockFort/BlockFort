package edu.wwu.cs412.blockfort;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ListView;
import edu.wwu.cs412.blockfort.HomeFragment.HomeFragmentListener;

/* This is the launcher activity. When the app opens, this activity runs. */

public class HomeActivity 

extends FragmentActivity 
implements HomeFragmentListener {

	/* Variables for switching between fragments within HomeActivity */
	public static final int HOME_FRAGMENT = 0;
	public static final int OPTIONS_FRAGMENT = 1;
	private static final int F_COUNT = OPTIONS_FRAGMENT+1;
	private Fragment[] fragments = new Fragment[F_COUNT];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		/* Start a fragment manager, and build the fragment array */
		FragmentManager fm = getSupportFragmentManager();
		
		fragments[HOME_FRAGMENT] = fm.findFragmentById(R.id.homeFragment);
		fragments[OPTIONS_FRAGMENT] = fm.findFragmentById(R.id.optionsFragment);
		
		/* Hide all of the fragments */
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i=0; i<fragments.length; i++) {
			transaction.hide(fragments[i]);
		}
		transaction.commit();
	}
	
	/* On resume, always show the HOME fragment, using private showFragment method */
	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		showFragment(HOME_FRAGMENT, false);
	}

	@Override
	public void onNewGameClicked() {
		Bundle extras = new Bundle();
		// TODO Set any extras for a new game
		extras.putString("Load", null);
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtras(extras);
		startActivity(intent);
	}

	@Override
	public void onLoadGameClicked() {
		
		ListView savedGamesList = (ListView) findViewById(R.id.load_game_list);
		
		if (savedGamesList.getVisibility()==View.VISIBLE) {
			savedGamesList.setVisibility(View.GONE);
		} else {
			savedGamesList.setVisibility(View.VISIBLE);
		}		
		/*Bundle extras = new Bundle();
		extras.putInt("Load", 1);
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtras(extras);
		startActivity(intent);*/
	}

	@Override
	public void onJoinGameClicked() {
		// TODO Handle locating and joining a network game
		
	}

	@Override
	public void onOptionsClicked() {
		showFragment(OPTIONS_FRAGMENT, true);		
	}
	
	/* Show a fragment in the home activity.
	 * Parameters: fragmentIndex - index in the fragment array of the desired fragment
	 * 			   addToBackStack - should the current fragment be added to the back stack */
	public void showFragment(int fragmentIndex, boolean addToBackStack) {
		/* Start a fragment manager, loop through all of the fragments in the fragment array.
		 * If the current fragment is the desired fragment, show it, otherwise, hide it. */
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i=0; i<fragments.length; i++) {
			if (i == fragmentIndex) {
				transaction.show(fragments[i]);
			} else {
				transaction.hide(fragments[i]);
			}
		}
		/* Add the fragment to the back stack */
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	// Set the action for the list item click
	@Override
	public void onLoadGameItemClicked(String content) {
		Bundle extras = new Bundle();
		// Put the game name in the bundle to the GameActivity.
		extras.putString("Load", content);
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtras(extras);
		// Start the activity with the bundle
		startActivity(intent);
	}
}
