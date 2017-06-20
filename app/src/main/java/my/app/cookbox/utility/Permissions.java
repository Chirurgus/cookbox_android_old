package my.app.cookbox.utility;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Alexander on 010,  10 May.
 */

public final class Permissions {
    static int WRITE_EXTERNAL_STOARE_PERM_REQUEST = 1;
    public static void checkExternalStoragePermisson(Activity activity) {
        int perm = ActivityCompat.checkSelfPermission(activity,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (perm != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STOARE_PERM_REQUEST);
        }
    }
}
