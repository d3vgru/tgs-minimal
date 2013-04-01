package org.theglobalsquare.framework;

public class CallbackThread extends Thread {
	private boolean tick = false;
	public boolean getTick() {
		if(tick) {
			tick = false;
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		super.run();
		try {
			while(true) {
				sleep(500);
				if(!tick)
					tick = true;
			}
		} catch(Exception ex) {
			android.util.Log.e("CallbackThread", "exception running callback", ex);
		}
	}
	
}
