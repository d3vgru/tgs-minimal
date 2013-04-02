package org.theglobalsquare.framework;

import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.values.*;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

// this class sets up and manages the main UI
public class TGSUIActivity extends TGSBaseActivity {
	public final static String TAG = "TGSUI";
	
	private boolean composerShowing = false;
	
	protected static TGSEventProxy events = new TGSEventProxy();
	
	protected static String monitorTxt = "";
	
	public String getMonitorTxt() {
		return monitorTxt;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// load full layout
		ViewGroup mainLayout = (ViewGroup)findViewById(R.id.mainLayout);
		getLayoutInflater().inflate(R.layout.main_activity, mainLayout);
		
		monitor(TAG + ": INIT");
		// set the window title
        setTitle(getString(R.string.short_name));
        
        // attach click handlers
        configureButtons();
		
        // setup the default tabs
        configureTabs();
        
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
				events.sendEvent(event, true);
				
				// clear text
				et.setText(null);
				
				if(composerShowing)
					hideComposer();
				
				monitor(TGSUIActivity.TAG + ": sent message: " + body);
			}
		});
	}
	
	public void monitor(String message) {
		TGSUIActivity.monitorTxt = message + "\n" + TGSUIActivity.monitorTxt;
		final TextView monitor = (TextView) findViewById(R.id.monitor);
		if(monitor != null)
			monitor.setText(TGSUIActivity.monitorTxt);
		final TextView status = (TextView)findViewById(R.id.statusMessage);
		if(status != null)
			status.setText(message);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    boolean visible = showActionButtons(selectedTab);
		menuCompose = menu.findItem(R.id.menu_compose);
		menuCompose.setVisible(visible);
		menuRefresh = menu.findItem(R.id.menu_refresh);
		menuRefresh.setVisible(visible);
		menuShare = menu.findItem(R.id.menu_share);
		menuShare.setVisible(visible);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// FIXME disable until startup complete
		switch(item.getItemId()) {
			case R.id.menu_compose:
				if(composerShowing)
					hideComposer();
				else showComposer();
				break;
			case R.id.menu_refresh:
				mTabsAdapter.notifyDataSetChanged();
				break;
			case R.id.menu_share:
				// TODO share action
				Toast.makeText(this, R.string.shareBtnLabel, Toast.LENGTH_SHORT).show();
				break;
			case R.id.menu_create:
				// TODO show new square dialog
				Toast.makeText(this, R.string.createBtnLabel, Toast.LENGTH_SHORT).show();
				break;
			case R.id.menu_search:
				// select Search tab
		        setTab(TAB_SEARCH);
				break;
			case R.id.menu_settings:
				Intent prefsIntent = new Intent();
				prefsIntent.setClass(this, PreferenceActivity.class);
				startActivity(prefsIntent);
				break;
			case R.id.menu_help:
				// TODO show help dialog
				Toast.makeText(this, R.string.helpBtnLabel, Toast.LENGTH_SHORT).show();
				break;
			case R.id.menu_about:
				// TODO show about dialog
				Toast.makeText(this, R.string.aboutBtnLabel, Toast.LENGTH_SHORT).show();
				break;
			default:
				// unknown option, maybe a superclass can handle it
				return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	@Override
	public void onBackPressed() {
		if(composerShowing)
			hideComposer();
		else super.onBackPressed();
	}
	

	public void dismissKeyboardFor(EditText editText) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
	private ViewGroup getComposer() {
		return (ViewGroup)findViewById(R.id.mainActivityComposer);
	}
	
	public void showComposer() {
		getComposer().setVisibility(View.VISIBLE);
		findViewById(R.id.messageTxt).requestFocus();
		composerShowing = true;
	}
	
	public void hideComposer() {
		// http://stackoverflow.com/questions/3553779/android-dismiss-keyboard
		dismissKeyboardFor((EditText)findViewById(R.id.messageTxt));
		getComposer().setVisibility(View.GONE);
		composerShowing = false;
	}

	// http://stackoverflow.com/questions/13179620/force-overflow-menu-in-actionbarsherlock
	// Also need to update com.actionbarsherlock.internal.view.menu.ActionMenuPresenter
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
	        if (event.getAction() == KeyEvent.ACTION_UP &&
	            keyCode == KeyEvent.KEYCODE_MENU) {
	            openOptionsMenu();
	            return true;
	        }
	    }
	    return super.onKeyUp(keyCode, event);
	}
	
}
