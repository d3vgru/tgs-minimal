package org.theglobalsquare.framework.ui;

import org.theglobalsquare.framework.*;
import org.theglobalsquare.app.R;

//import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;

public class TGSListFragment extends SherlockListFragment {
	public final static String TAG = "TGSList";
	
	public ITGSActivity getTGSActivity() {
		return (ITGSActivity)getActivity();
	}
	
	public ITGSFacade getTGSFacade() {
		return getTGSActivity().getTGSFacade();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		v.setBackgroundColor(getResources().getColor(android.R.color.white));
		return v;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		setEmptyText(getString(R.string.emptyListLabel));
	}
	
	/* FIXME not sure we need this
	public void showEmptyText(final String emptyText) {
		Activity activity = getActivity();
		if(activity != null) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(getView() == null)
						return;
					setEmptyText(emptyText);
					setListShown(true);
				}	
			});
		}
		else android.util.Log.d(TAG, "activity null");
	}
	*/

	protected TGSListAdapter getTGSListAdapter() {
		return (TGSListAdapter)getListAdapter();
	}

	@Override
	public void setListShown(boolean shown) {
		super.setListShown(shown);
		/* FIXME remove debugging code
		if(shown && !this.getClass().getName().equals("org.theglobalsquare.ui.FilesListFragment"))
			throw new RuntimeException("showing list");
		*/
		android.util.Log.w(TAG, "setListShown: " + shown);
	}
	
}
