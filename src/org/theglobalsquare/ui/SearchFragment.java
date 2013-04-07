package org.theglobalsquare.ui;

import org.theglobalsquare.app.R;
import org.theglobalsquare.app.TGSMainActivity;
import org.theglobalsquare.framework.*;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SearchFragment extends TGSFragment {
	private TGSListFragment resultsFragment = null;

	public TGSListFragment getResultsFragment() {
		return resultsFragment;
	}

	public void setResultsFragment(TGSListFragment resultsFragment) {
		this.resultsFragment = resultsFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// search layout
		View view = inflater.inflate(R.layout.main_search, null);
		
		// submit-on-enter
		final EditText terms = (EditText)view.findViewById(R.id.txt_search_terms);
		final TGSMainActivity mainActivity = (TGSMainActivity)getActivity();
		terms.setOnKeyListener(mainActivity);
		Button searchButton = (Button)view.findViewById(R.id.btn_search_community);
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mainActivity.submitCommunitySearch(terms, SearchFragment.this);
			}
		});
		return view;
	}

}
