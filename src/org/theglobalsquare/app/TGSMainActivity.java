package org.theglobalsquare.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.*;

import org.theglobalsquare.framework.*;
import org.theglobalsquare.framework.values.*;
import org.theglobalsquare.ui.*;

public class TGSMainActivity extends TGSBaseActivity
		implements PropertyChangeListener {
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		android.util.Log.i("MainActivity", "propertyChange:" + event);
		if(msgHandler == null)
			return;
		Message message = new Message();
		android.util.Log.i("MainActivity", "android.os.Message obtained:" + message);
		message.obj = event.getNewValue();
		msgHandler.sendMessage(message);
	}

	public final static int TAB_COUNT_BASE = 3;
	public final static int TAB_SEARCH = 0;
	public final static int TAB_OVERVIEW = 1;
	public final static int TAB_MONITOR = 2;
	
	private boolean composerShowing = false;
	
	private int selectedTab = -1;
	
	private MenuItem menuCompose = null;
	private MenuItem menuRefresh = null;
	private MenuItem menuShare = null;
	private MenuItem menuCreate = null;
	
	private String monitorTxt = "";
	
	public String getMonitorTxt() {
		return monitorTxt;
	}
	
	private static Handler msgHandler = null;
	private static TGSEventProxy events = new TGSEventProxy();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		monitor("MainActivity: INIT");
		
		// initialize UI handler
		msgHandler = new MessageHandler(this);
		
		ViewGroup mainLayout = (ViewGroup)findViewById(R.id.mainLayout);
		
		// load full layout
		getLayoutInflater().inflate(R.layout.main_activity, mainLayout);
		
		// set the window title
        setTitle(getString(R.string.short_name));
        
        // setup the default tabs
        configureTabs();
        
        // attach click handlers
        configureButtons();
        
        // register to receive system events from python
        events.addListener(TGSSystemEvent.class, this);
	}
	
	protected void configureTabs() {
		ActionBar bar = getSupportActionBar();
		
		// make sure to setContentView() first (in super.onCreate)
		mViewPager = (ViewPager)findViewById(R.id.mainActivityPager);

		mTabsAdapter = new TabsAdapter(this, mViewPager);

		// TAB_SEARCH
        mTabsAdapter.addTab(
                bar.newTab().setText(R.string.searchBtnLabel),
                SearchFragment.class, null);
        // TAB_OVERVIEW
        mTabsAdapter.addTab(
                bar.newTab().setText(R.string.overviewLabel),
                OverviewListFragment.class, null);
        // TAB_MONITOR
        mTabsAdapter.addTab(
        		bar.newTab().setText(R.string.monitorLabel),
        		MonitorFragment.class, null);
        
        // TODO tabs for each square the user has joined
        /*
        mTabsAdapter.addTab(
                bar.newTab().setText("abc"),
                MessageListFragment.class, null);
        mTabsAdapter.addTab(
                bar.newTab().setText("Another Square"),
                MessageListFragment.class, null);
        */
        
        // listener for tab change
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// NOOP
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// NOOP
			}

			@Override
			public void onPageSelected(int tab) {
				selectedTab = tab;
				// hide action buttons if not viewing a square
				boolean visible = showActionButtons(tab);
				if(menuCompose != null)
					menuCompose.setVisible(visible);
				if(menuRefresh != null)
					menuRefresh.setVisible(visible);
				if(menuShare != null)
					menuShare.setVisible(visible);
				if(menuCreate != null)
					menuCreate.setVisible(visible);
			}
        	
        });
        
        // select Monitor tab
        mViewPager.setCurrentItem(TAB_MONITOR);
	}
	
	protected boolean showActionButtons(int tab) {
		return tab >= TAB_COUNT_BASE;
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
				
				monitor("UI: sent message: " + body);
			}
		});
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
		menuCreate = menu.findItem(R.id.menu_create);
		menuCreate.setVisible(visible);
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
		        mViewPager.setCurrentItem(TAB_SEARCH);
				break;
			case R.id.menu_settings:
				// TODO show settings dialog
				Toast.makeText(this, R.string.settingsBtnLabel, Toast.LENGTH_SHORT).show();
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
	
	// http://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler
	static class MessageHandler extends Handler {
	    private final WeakReference<TGSMainActivity> mActivity; 

	    MessageHandler(TGSMainActivity service) {
	        mActivity = new WeakReference<TGSMainActivity>(service);
	    }
		@Override
	    public void handleMessage(Message msg)
	    {
			// the TGSEvent object
			Object value = msg.obj;
			String out = "";
			TGSMainActivity activity = mActivity.get();
			
			android.util.Log.i("TGSMainActivity.MsgHandler", "value: " + value);
			android.util.Log.i("TGSMainActivity.MsgHandler", "class: " + value.getClass().getName());
			
			// turn LED green on dispersy start
			if(value instanceof TGSEvent) {
				TGSEvent event = (TGSEvent)value;
				if(activity != null
						&& value instanceof TGSSystemEvent
						&& TGSMessage.START.equals(event.getVerb())) {
					((ImageView)activity.findViewById(R.id.statusLight)).setImageResource(R.drawable.led_green);
				}
				out = "EVENT: " + value.getClass().getName() + ": " + event.toString();
			} else if(value instanceof TGSObject) {
				out = "VALUE: " + ((TGSObject)value).getName() + ": " + value;
			} else {
				if(activity != null && value != null) {
					activity.monitor(value.toString());
				}
				return;
	        }
			if(activity != null)
				activity.monitor(out);
	    }
	}

	public void monitor(String message) {
		this.monitorTxt += "\n" + message;
		final TextView monitor = (TextView) findViewById(R.id.monitor);
		if(monitor != null)
			monitor.setText(this.monitorTxt);
		final TextView status = (TextView)findViewById(R.id.statusMessage);
		if(status != null)
			status.setText(message);
	}
	
	// to be used by python
	public static void log(String message) {
		android.util.Log.i("MainActivity", "got message from python: " + message);
		if(msgHandler != null) {
			android.util.Log.i("MainActivity", "msgHandler not null");
			Message msg = new Message();
			android.util.Log.i("MainActivity", "android.os.Message obtained: " + msg);
			msg.obj = message;
			android.util.Log.i("MainActivity", "obj field of Message set");
			msgHandler.sendMessage(msg);
			android.util.Log.i("MainActivity", "logged");
		}
	}
	
	public TGSEventProxy getEvents() {
		return events;
	}
	
	public static boolean sendEvent(TGSEvent e) {
		if(events == null)
			return false;
		events.sendEvent(e);
		return true;
	}
}
