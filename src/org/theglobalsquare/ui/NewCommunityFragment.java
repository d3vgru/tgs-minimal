package org.theglobalsquare.ui;

import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.ITGSActivity;
import org.theglobalsquare.framework.TGSDialogFragment;
import org.theglobalsquare.framework.values.TGSCommunity;

public class NewCommunityFragment extends TGSDialogFragment {
	// community
	private TGSCommunity mCommunity = null;
	
	// TODO location, radius, avatar

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mCommunity = new TGSCommunity();
		View view = inflater.inflate(R.layout.community_new, null);
		final ITGSActivity tgsActivity = (ITGSActivity)getActivity();
		
		final EditText etName = (EditText)view.findViewById(R.id.txt_community_name);
		etName.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if(listenForEnter(tgsActivity, view, keyCode, event))
					return true;
				if(event.getAction() == KeyEvent.ACTION_UP) {
					String name = "";
					Editable eName = etName.getText();
					if(eName != null)
						name = eName.toString();
					mCommunity.setName(name);
					return true;
				}
				return false;
			}
		});
		
		final EditText etDescription = (EditText)view.findViewById(R.id.txt_community_description);
		etDescription.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if(listenForEnter(tgsActivity, view, keyCode, event))
					return true;
				if(event.getAction() == KeyEvent.ACTION_UP) {
					String description = "";
					Editable eDescription = etDescription.getText();
					if(eDescription != null)
						description = eDescription.toString();
					mCommunity.setDescription(description);
					return true;
				}
				return false;
			}
		});

		Button createButton = (Button)view.findViewById(R.id.btn_create_community);
		createButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				createCommunity(tgsActivity);
			}
		});
		return view;
	}
	
	protected void createCommunity(ITGSActivity tgsActivity) {
		tgsActivity.createCommunity(mCommunity);
		NewCommunityFragment.this.dismiss();
	}
	
	public boolean listenForEnter(ITGSActivity tgsActivity, View view, int keyCode, KeyEvent event) {
		int viewId = view.getId();
		// if it's the name, advance to description
		if(viewId == R.id.txt_community_name) {
			NewCommunityFragment.this.getView().findViewById(R.id.txt_community_description).requestFocus();
			return true;
		}
		// if it's description, submit
		if(viewId == R.id.txt_community_description) {
			createCommunity(tgsActivity);
			return true;
		}
		return false;
	}
	
}
