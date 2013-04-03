package org.theglobalsquare.framework;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

// http://stackoverflow.com/questions/14076073/how-could-i-use-the-same-set-of-preference-screens-for-all-android-versions-from
public class CompatiblePreferenceActivity extends PreferenceActivity
{
    private int prefs=0;

    //Get/Set
    public void setPrefs(int prefs)
    {
        this.prefs=prefs;
    }

    //Exception
    protected static class PrefsNotSetException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        PrefsNotSetException()
        {
            super("\"prefs\" should be set to a valid preference resource ID.");
        }
    }

    //Creation
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (prefs==0)
            throw new PrefsNotSetException();
        else
            try {
                getClass().getMethod("getFragmentManager");
                AddResourceApi11AndGreater();
                }
            catch (NoSuchMethodException e) { //Api < 11
                    AddResourceApiLessThan11();
                }
    }

    protected void AddResourceApiLessThan11()
    {
        addPreferencesFromResource(prefs);
    }

    @TargetApi(11)
    protected void AddResourceApi11AndGreater()
    {
        PF.prefs=prefs;
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PF()).commit();
    }

    @TargetApi(11)
    public static class PF extends PreferenceFragment
    {
        private static int prefs;
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(prefs);
        }
    }

    //Sub-screen background glitch fix
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
    {
        super.onPreferenceTreeClick(preferenceScreen, preference);
        if (preference!=null)
            if (preference instanceof PreferenceScreen)
                if (((PreferenceScreen)preference).getDialog()!=null)
                    ((PreferenceScreen)preference).getDialog().getWindow().getDecorView().setBackgroundDrawable(this.getWindow().getDecorView().getBackground().getConstantState().newDrawable());
        return false;
    }
}
