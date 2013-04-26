package org.theglobalsquare.framework.activity;

import org.kivy.android.PythonActivity;
import org.theglobalsquare.app.Facade;
import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.*;
import org.theglobalsquare.framework.values.*;
import org.theglobalsquare.ui.SearchFragment;
import org.theglobalsquare.ui.SearchResultsFragment;

import android.app.Activity;
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
	protected MenuItem mMenuClose = null;
	protected MenuItem mMenuCompose = null;
	protected MenuItem mMenuRefresh = null;
	protected MenuItem mMenuShare = null;
	protected MenuItem mMenuCreate = null;
	
	protected static SearchFragment sSearchFragment = null;
	protected static SearchResultsFragment sSearchResults = null;
	
	protected boolean mComposerShowing = false;
	
	protected static Facade sFacade = null;
	
	protected static String sMonitorTxt = "";
	
	public String getMonitorTxt() {
		return sMonitorTxt;
	}
	    
	public Facade getFacade() {
		return (Facade)getApplication();
	}
	
	public static Facade getStaticFacade() {
		return sFacade;
	}
	
	public ITGSActivity getTGSActivity() {
		return (ITGSActivity)this;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		sFacade = getFacade();
		
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
				
				monitor(TAG + ": sent message: " + body);
			}
		});
	}
	
	public static void showSearchTerms(Activity a) {
		View terms = a.findViewById(R.id.group_search_terms);
		if(terms != null)
			terms.setVisibility(View.VISIBLE);
		EditText et = (EditText)a.findViewById(R.id.txt_search_terms);
		if(et == null)
			return;
		et.setText(null);
		showKeyboardFor(a, et);
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
		if(mComposerShowing)
			return;
		getComposer().setVisibility(View.VISIBLE);
		showKeyboardFor(this, (EditText)findViewById(R.id.messageTxt));
		mComposerShowing = true;
	}
	
	public void hideComposer() {
		if(!mComposerShowing)
			return;
		// http://stackoverflow.com/questions/3553779/android-dismiss-keyboard
		dismissKeyboardFor(this, (EditText)findViewById(R.id.messageTxt));
		getComposer().setVisibility(View.GONE);
		mComposerShowing = false;
	}
	
	public static void showKeyboardFor(Activity a, EditText editText) {
		if(editText == null)
			return;
		editText.requestFocus();
		// FIXME make sure we don't have a hardware keyboard open
		InputMethodManager imm = (InputMethodManager)a.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);		
	}

	public static void dismissKeyboardFor(Activity a, EditText editText) {
		if(editText == null)
			return;
		InputMethodManager imm = (InputMethodManager)a.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
	public void monitor(String message) {
		sMonitorTxt = message + "\n" + sMonitorTxt;
		final TextView monitor = (TextView) findViewById(R.id.monitor);
		if(monitor != null)
			monitor.setText(sMonitorTxt);
		final TextView status = (TextView)findViewById(R.id.statusMessage);
		if(status != null)
			status.setText(message);
	}
	
}
