package edu.wwu.cs412.blockfort;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/* Placeholder fragment for options menu. Inflates the view from fragment_options.xml */

public class OptionsFragment extends Fragment{
	
	private FrameLayout progressContainer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View view;
		
		view = inflater.inflate(R.layout.fragment_options, parent, false);
		
		progressContainer = (FrameLayout)view.findViewById(R.id.progress_container_options);
		
		progressContainer.setVisibility(View.INVISIBLE);
		
		return view;	
	}
}
