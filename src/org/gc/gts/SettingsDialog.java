package org.gc.gts;

import org.gc.gts.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsDialog extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.setari);
	}

}
