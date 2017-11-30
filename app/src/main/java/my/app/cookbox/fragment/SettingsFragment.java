package my.app.cookbox.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import my.app.cookbox.R;

/**
 * Created by Alexander on 010,  10 May.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(getString(R.string.preference_file_name));

        addPreferencesFromResource(R.xml.preferences);
    }
}
