package com.tudelft.triblerdroid.first;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import org.theglobalsquare.app.R;

public class Preferences extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		Preference button = (Preference)findPreference("pref_clean_now");
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
            	Util.deleteAllSDContent();
        		Toast.makeText(getApplicationContext(), "All videos DELETED", Toast.LENGTH_SHORT).show();
            	return true;
            }
        });
		button = (Preference)findPreference("pref_tweet");
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) { 
            	String tweetUrl = "https://twitter.com/intent/tweet?text=" +
            			"Video description ppsp.me/2b2fe5f1462e5b7ac4d70fa081e0169160b2d3a6";
            	Uri uri = Uri.parse(tweetUrl);
            	startActivity(new Intent(Intent.ACTION_VIEW, uri));
                return true;
            }
        });
		button = (Preference)findPreference("pref_share");
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) { 
            	 Intent share = new Intent(Intent.ACTION_SEND);
            	 share.putExtra(Intent.EXTRA_TEXT, 
            			 "Video description ppsp.me/2b2fe5f1462e5b7ac4d70fa081e0169160b2d3a6");
            	 startActivity(Intent.createChooser(share, "Share this via"));
                return true;
            }
        });
		
	}
}
