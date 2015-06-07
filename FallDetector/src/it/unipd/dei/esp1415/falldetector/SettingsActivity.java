package it.unipd.dei.esp1415.falldetector;


import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class SettingsActivity extends PreferenceActivity {


	private PreferencesHolder preHolder;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefernces);
		
		LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.drawable.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

		preHolder = new PreferencesHolder();

		preHolder.ringChk = (CheckBoxPreference) findPreference("key_ring");
		preHolder.accelSelect = (Preference) findPreference("key_accel");
		preHolder.freqSelect = (Preference) findPreference("key_frequency");
		preHolder.mailTypeChk = (CheckBoxPreference) findPreference("key_rapid_mail");
		preHolder.mailList = (Preference) findPreference("key_mail_add");

		//preHolder.ringChk.setOnPreferenceChangeListener(onPreferenceChangeListener);
	}

	private class PreferencesHolder{
		private CheckBoxPreference ringChk;
		private Preference accelSelect;
		private Preference freqSelect;
		private CheckBoxPreference mailTypeChk; 
		private Preference mailList;
	}

}
