package org.theglobalsquare.framework;

import org.kivy.android.PythonActivity;
import org.theglobalsquare.app.Facade;
import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.values.TGSConfig;
import org.theglobalsquare.framework.values.TGSConfigEvent;
import org.theglobalsquare.framework.values.TGSMessage;
import org.theglobalsquare.framework.values.TGSMessageEvent;
import org.theglobalsquare.framework.values.TGSUser;
import org.theglobalsquare.ui.SearchFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

// this class sets up and manages the tabs in the main_activity layout

// adapted from https://bitbucket.org/owentech/abstabsviewpager
// depends on ActionBarSherlock
public abstract class TGSBaseActivity extends PythonActivity implements ITGSActivity {
	public final static String TAG = "TGSBase";
	protected MenuItem mMenuCompose = null;
	protected MenuItem mMenuRefresh = null;
	protected MenuItem mMenuShare = null;
	protected MenuItem mMenuCreate = null;
	
	protected static SearchFragment sSearchFragment = null;
	protected static TGSListFragment sSearchResults = null;
	
	protected boolean mComposerShowing = false;
	
	protected static String sMonitorTxt = "";
	
	public String getMonitorTxt() {
		return sMonitorTxt;
	}
	    
	public Facade getFacade() {
		return (Facade)getApplication();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar bar = getSupportActionBar();        
        bar.setDisplayShowHomeEnabled(false);
	}
			
	protected void configureButtons() {
		ImageButton sendBtn = (ImageButton)findViewById(R.id.sendBtn);
		sendBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// SEND A MESSAGE
				
				// get text from input
				EditText et = (EditText)findViewById(R.id.messageTxt);
				String body = et.getText().toString();
				
				// create message
				TGSMessage subject = new TGSMessage();
				subject.setFrom(TGSUser.getMe());
				subject.setBody(body);
				
				// send message to event queue
				TGSMessageEvent event = new TGSMessageEvent();
				event.setVerb(TGSMessage.SEND);
				event.setSubject(subject);
				Facade.sendEvent(event, true);
				
				// clear text
				et.setText(null);
				
				if(mComposerShowing)
					hideComposer();
				
				monitor(TGSBaseActivity.TAG + ": sent message: " + body);
			}
		});
	}
	
	public void freshenConfig() {
		TGSConfig config = getFacade().getConfig();
		TGSConfigEvent e = TGSConfigEvent.forParamUpdated(config);
		Facade.sendEvent(e, true);
	}

	private ViewGroup getComposer() {
		return (ViewGroup)findViewById(R.id.mainActivityComposer);
	}
	
	public void showComposer() {
		getComposer().setVisibility(View.VISIBLE);
		findViewById(R.id.messageTxt).requestFocus();
		mComposerShowing = true;
	}
	
	public void hideComposer() {
		// http://stackoverflow.com/questions/3553779/android-dismiss-keyboard
		dismissKeyboardFor((EditText)findViewById(R.id.messageTxt));
		getComposer().setVisibility(View.GONE);
		mComposerShowing = false;
	}

	public void dismissKeyboardFor(EditText editText) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
	public void monitor(String message) {
		TGSUIActivity.sMonitorTxt = message + "\n" + TGSUIActivity.sMonitorTxt;
		final TextView monitor = (TextView) findViewById(R.id.monitor);
		if(monitor != null)
			monitor.setText(TGSUIActivity.sMonitorTxt);
		final TextView status = (TextView)findViewById(R.id.statusMessage);
		if(status != null)
			status.setText(message);
	}
	
}
