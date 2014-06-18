package edu.wwu.cs412.blockfort;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class HomeFragment extends Fragment implements OnClickListener {
	
	ArrayAdapter<String> adapter;

	public interface HomeFragmentListener {
		public void onNewGameClicked();
		public void onLoadGameClicked();
		public void onJoinGameClicked();
		public void onOptionsClicked();
		public void onLoadGameItemClicked(String content);
	}

	private HomeFragmentListener callBack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setRetainInstance(true);		
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d("HomeFragment", "in onAttach");
		super.onAttach(activity);
		try {
			callBack = (HomeFragmentListener) activity;
		} catch (ClassCastException e) {
			Log.d("BlockFort", String.format("%s does not implement HomeFragmentListener", activity));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {	
		Log.d("HomeFragment", "in onCreateView");
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d("HomeFragment", "in onViewCreated");
		super.onViewCreated(view, savedInstanceState);

		view.findViewById(R.id.new_game_button).setOnClickListener(this);
		view.findViewById(R.id.load_game_button).setOnClickListener(this);
		view.findViewById(R.id.options_button).setOnClickListener(this);
		view.findViewById(R.id.join_game_button).setOnClickListener(this);
		
		// checkSavedGames to see whether load button should be enabled
		checkSavedGames(view);
		// hide the list
		view.findViewById(R.id.load_game_list).setVisibility(View.GONE);
		// tried to update the adapter, doesn't work here
		if (adapter != null) {
			adapter.notifyDataSetChanged();
			adapter.notifyDataSetInvalidated();
		}
	}

	@Override
	public void onResume() {
		Log.d("HomeFragment", "in onResume");
		checkSavedGames(getView());
		super.onResume();
	}
	
	// method to check for saved games
	private void checkSavedGames(View view) {
		// Try to open the file
		File file = getActivity().getFileStreamPath("BFJsonSave.dat");
		JSONArray saveArray;
		// If the file exists, get a list of saved games, set up the adapter if saved games > 0
		if (file.exists()) {
			try {
				// Read the file and build the array
				Scanner scanner = new Scanner(file);
				StringBuilder builder = new StringBuilder();
				while (scanner.hasNextLine()) {
					builder.append(scanner.nextLine());
				}
				saveArray = new JSONArray(builder.toString());
				scanner.close();
				// if there's a saved game, set the adapter and enable the load button to hide/show the list of saved games
				if (saveArray.length() > 0) {
					final ListView savedGamesList = (ListView) view.findViewById(R.id.load_game_list);
					ArrayList<String> savedGameNames = new ArrayList<>();
					for (int i=0; i<saveArray.length(); i++) {
						JSONObject savedGame = saveArray.getJSONObject(i);
						savedGameNames.add(savedGame.getString("name"));
					}
					adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, savedGameNames);
					savedGamesList.setAdapter(adapter);
					
					view.findViewById(R.id.load_game_button).setEnabled(true);
					view.findViewById(R.id.load_game_list).setVisibility(View.GONE);
					
					// Set the list item click listener to a callback to HomeActivity, passing the saved game name as a parameter
					savedGamesList.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							String content = savedGamesList.getItemAtPosition(position).toString();
							Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
							callBack.onLoadGameItemClicked(content);
						}
					});					
				// No saved games, we don't need the load button
				} else {
					view.findViewById(R.id.load_game_button).setEnabled(false);
				}
			} catch (FileNotFoundException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// No saved game file, we don't need the load button
		} else {
			view.findViewById(R.id.load_game_button).setEnabled(false);
		}
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.new_game_button:
			callBack.onNewGameClicked();
			break;
		case R.id.load_game_button:
			callBack.onLoadGameClicked();
			break;
		case R.id.options_button:
			callBack.onOptionsClicked();
			break;
		case R.id.join_game_button:
			callBack.onJoinGameClicked();
			break;
		default:
			break;
		}

	}
}
