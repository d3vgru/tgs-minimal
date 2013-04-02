package org.theglobalsquare.framework;


import org.theglobalsquare.app.R;
import android.os.Bundle;

public class PreferenceActivity extends CompatiblePreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setPrefs(R.xml.settings);
		super.onCreate(savedInstanceState);
	}

}
