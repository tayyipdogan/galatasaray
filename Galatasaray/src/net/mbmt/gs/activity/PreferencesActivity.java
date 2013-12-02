package net.mbmt.gs.activity;

import net.mbmt.gs.R;
import net.mbmt.gs.service.NewsNotificationManager;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class PreferencesActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
	    final CheckBoxPreference checkboxPref = (CheckBoxPreference) getPreferenceManager().findPreference("GSPrefEnableNotifications");

	    checkboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
	            Log.d("GS Service", "Pref " + preference.getKey() + " changed to " + newValue.toString());
	            NewsNotificationManager mgr = new NewsNotificationManager(PreferencesActivity.this);
	        	if (((Boolean)newValue).booleanValue()) {
	        		mgr.start(true);
	        	}
	        	else {
	        		mgr.stop();
	        	}
	            return true;
			}
		}); 
	}
}