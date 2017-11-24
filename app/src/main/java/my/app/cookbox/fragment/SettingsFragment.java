package my.app.cookbox.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import my.app.cookbox.R;

/**
 * Created by Alexander on 010,  10 May.
 */

public class SettingsFragment extends PreferenceFragment {
    public static final String PREFERENCE_FILE_NAME = "cookbox_user_preferences";
    public static final String PREFERENCE_DB_BACKUP_FILE_LOCATION_KEY = "backup_file_location";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(PREFERENCE_FILE_NAME);

        addPreferencesFromResource(R.xml.preferences);
    }
}
