package org.theglobalsquare.ui;

import android.os.Bundle;


import org.theglobalsquare.app.*;
import org.theglobalsquare.framework.ui.*;

public class FilesListFragment extends TGSListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// show "private storage empty" message when empty
		setEmptyText(getActivity().getString(R.string.noFilesMsg));
		setListShown(true);
	}

}
