package org.kivy.android;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import android.util.Log;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

import com.actionbarsherlock.app.*;

// This class can altered to extend any type of Activity (for example, SherlockFragmentActivity).
// Also, it may be extended by a class in a different package. If you use a different class as
// your main activity, it _must_ extend this class or the JNI bindings will fail.
public class PythonActivity extends SherlockFragmentActivity implements Runnable {
	public static String TAG = "PythonActivity";
	
	public static PythonActivity mActivity = null;

    // Did we launch our thread?
    private boolean mLaunchedThread = false;

    private ResourceManager resourceManager;

    // The name of the directory where the context stores its files.
    private String mFilesDirectory = null;

    // The path to the directory contaning our external storage.
    private File externalStorage;

    // The path to the directory containing the game.
    private File mPath = null;
    private String mArgument = null;

    boolean _isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		// landscape mode for large screens
		if ((getResources().getConfiguration().screenLayout &
				Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
        super.onCreate(savedInstanceState);

        Hardware.context = this;
        Action.context = this;
		PythonActivity.mActivity = this;

        getWindowManager().getDefaultDisplay().getMetrics(Hardware.metrics);

        resourceManager = new ResourceManager(this);
        mFilesDirectory = getFilesDir().getAbsolutePath();
        externalStorage = new File(Environment.getExternalStorageDirectory(), getPackageName());
        if (resourceManager.getString("public_version") != null) {
            mPath = externalStorage;
        } else {
            mPath = getFilesDir();
        }
        mArgument = mPath.getAbsolutePath();

        // Figure out the directory where the game is. If the game was
        // given to us via an intent, then we use the scheme-specific
        // part of that intent to determine the file to launch. We
        // also use the android.txt file to determine the orientation.
        //
        // Otherwise, we use the public data, if we have it, or the
        // private data if we do not.
        /*
        if (getIntent().getAction().equals("org.renpy.LAUNCH")) {
            mPath = new File(getIntent().getData().getSchemeSpecificPart());

            Project p = Project.scanDirectory(mPath);

            if (p != null) {
                if (p.landscape) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }

        } else if (resourceManager.getString("public_version") != null) {
            mPath = externalStorage;
        } else {
            mPath = getFilesDir();
        }
        */

        // uncomment to go to fullscreen mode
        /*
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        */

        // can't do this since mView doesn't exist
        Hardware.view = null; //mView;
        setContentView(resourceManager.getIdentifier("main", "layout"));
    }

    /**
     * Show an error using a toast. (Only makes sense from non-UI
     * threads.)
     */
    public void toastError(final String msg) {

        final Activity thisActivity = this;

        runOnUiThread(new Runnable () {
                public void run() {
                    Toast.makeText(thisActivity, msg, Toast.LENGTH_LONG).show();
                }
            });

        // Wait to show the error.
        synchronized (this) {
            try {
                this.wait(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    public void recursiveDelete(File f) {
        if (f.isDirectory()) {
            for (File r : f.listFiles()) {
                recursiveDelete(r);
            }
        }
        f.delete();
    }


    /**
     * This determines if unpacking one the zip files included in
     * the .apk is necessary. If it is, the zip file is unpacked.
     */
    public void unpackData(final String resource, File target) {

        // The version of data in memory and on disk.
        String data_version = resourceManager.getString(resource + "_version");
        String disk_version = null;

        // If no version, no unpacking is necessary.
        if (data_version == null) {
            return;
        }

        // Check the current disk version, if any.
        String filesDir = target.getAbsolutePath();
        String disk_version_fn = filesDir + "/" + resource + ".version";

        try {
            byte buf[] = new byte[64];
            InputStream is = new FileInputStream(disk_version_fn);
            int len = is.read(buf);
            disk_version = new String(buf, 0, len);
            is.close();
        } catch (Exception e) {
            disk_version = "";
        }

        // If the disk data is out of date, extract it and write the
        // version file.
        if (! data_version.equals(disk_version)) {
            Log.v("python", "Extracting " + resource + " assets.");

            recursiveDelete(target);
            target.mkdirs();

            AssetExtract ae = new AssetExtract(this);
            if (!ae.extractTar(resource + ".mp3", target.getAbsolutePath())) {
                toastError("Could not extract " + resource + " data.");
            }

            try {
                // Write .nomedia.
                new File(target, ".nomedia").createNewFile();

                // Write version file.
                FileOutputStream os = new FileOutputStream(disk_version_fn);
                os.write(data_version.getBytes());
                os.close();
            } catch (Exception e) {
                Log.w("python", e);
            }
        }

    }

    public void run() {

        unpackData("private", getFilesDir());
        unpackData("public", externalStorage);

		System.loadLibrary("python2.7");
        System.loadLibrary("application");
        System.loadLibrary("minimal_main");

		System.load(getFilesDir() + "/lib/python2.7/lib-dynload/_io.so");
        System.load(getFilesDir() + "/lib/python2.7/lib-dynload/unicodedata.so");

        try {
            System.loadLibrary("sqlite3");
            System.load(getFilesDir() + "/lib/python2.7/lib-dynload/_sqlite3.so");
        } catch(UnsatisfiedLinkError e) {
        }

		/*
        try {
            System.load(getFilesDir() + "/lib/python2.7/lib-dynload/_imaging.so");
            System.load(getFilesDir() + "/lib/python2.7/lib-dynload/_imagingft.so");
            System.load(getFilesDir() + "/lib/python2.7/lib-dynload/_imagingmath.so");
        } catch(UnsatisfiedLinkError e) {
        }
        */

        android.util.Log.d(TAG, "Calling nativeSetEnv");
        android.util.Log.v(TAG, "ANDROID_PRIVATE: " + mFilesDirectory);
        nativeSetEnv("ANDROID_PRIVATE", mFilesDirectory);
        android.util.Log.v(TAG, "ANDROID_ARGUMENT: " + mArgument);
        nativeSetEnv("ANDROID_ARGUMENT", mPath.getAbsolutePath());
        
        // comment out to enable __debug__? (no, what else?)
        
        android.util.Log.v(TAG, "PYTHONOPTIMIZE: 2");
        nativeSetEnv("PYTHONOPTIMIZE", "2");
        
        /*
        android.util.Log.i(TAG, "PYTHONDEBUG: 1");
        nativeSetEnv("PYTHONDEBUG", "1");
        */
        android.util.Log.v(TAG, "PYTHONHOME: " + mFilesDirectory);
        nativeSetEnv("PYTHONHOME", mFilesDirectory);
        android.util.Log.v(TAG, "PYTHONPATH: " + mFilesDirectory + "/lib");
        nativeSetEnv("PYTHONPATH", mArgument + ":" + mFilesDirectory + "/lib");

        android.util.Log.d(TAG, "Calling nativeInit");
        nativeInit();
    }

    @Override
    protected void onPause() {
        _isPaused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        _isPaused = false;

        if (!mLaunchedThread) {
            mLaunchedThread = true;
            new Thread(this).start();
        }
    }

    public boolean isPaused() {
        return _isPaused;
    }

	protected void onDestroy() {
		Log.i(TAG, "on destroy (exit1)");
        System.exit(0);
	}
	
    public static native void nativeSetEnv(String name, String value);
    public static native void nativeInit();

}

