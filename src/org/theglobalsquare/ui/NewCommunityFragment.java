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
import org.theglobalsquare.framework.ui.TGSDialogFragment;
import org.theglobalsquare.framework.values.TGSCommunity;

public class NewCommunityFragment extends TGSDialogFragment {
	private View mView = null;
	
	// TODO location, radius, avatar

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.community_new, null);
		final ITGSActivity tgsActivity = (ITGSActivity)getActivity();
		
		OnKeyListener enterListener = new OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP) {
					if(listenForEnter(tgsActivity, view, keyCode, event))
						return true;
				}
				return false;
			}
		};
		
		EditText etName = (EditText)mView.findViewById(R.id.txt_community_name);
		etName.setOnKeyListener(enterListener);

		EditText etDescription = (EditText)mView.findViewById(R.id.txt_community_description);
		etDescription.setOnKeyListener(enterListener);

		Button createButton = (Button)mView.findViewById(R.id.btn_create_community);
		createButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				createCommunity(tgsActivity);
			}
		});
		
		getDialog().setTitle(R.string.createCommunityBtnLabel);
		
		return mView;
	}
	
	protected void createCommunity(ITGSActivity tgsActivity) {
		TGSCommunity c = new TGSCommunity();
		EditText etName = (EditText)mView.findViewById(R.id.txt_community_name);
		Editable eName = etName.getText();
		String name = eName == null ? "" : eName.toString();
		c.setName(name);
		EditText etDescription = (EditText)mView.findViewById(R.id.txt_community_description);
		Editable eDescription = etDescription.getText(); 
		String description = eDescription == null ? "" : eDescription.toString();
		c.setDescription(description);
		android.util.Log.i("NewCommunity", "-community: " + c);
		tgsActivity.createCommunity(c);
		NewCommunityFragment.this.dismiss();
	}
	
	public boolean listenForEnter(ITGSActivity tgsActivity, View view, int keyCode, KeyEvent event) {
		if(keyCode != KeyEvent.KEYCODE_ENTER)
			return false;
		int viewId = view.getId();
		// if it's the name, advance to description
		if(viewId == R.id.txt_community_name) {
			android.util.Log.i("NewCommunity", "advancing to description");
			NewCommunityFragment.this.getView().findViewById(R.id.txt_community_description).requestFocus();
			return true;
		}
		// if it's description, submit
		if(viewId == R.id.txt_community_description) {
			android.util.Log.i("NewCommunity", "creating community");
			createCommunity(tgsActivity);
			return true;
		}
		return false;
	}
	
}
