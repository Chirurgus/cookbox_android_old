package my.app.cookbox.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by Alexander on 018,  18 Apr.
 */

public class CheckableRelativeLayout extends RelativeLayout implements Checkable {

    public CheckableRelativeLayout(Context context) {
        super(context);

        Log.v(TAG, TAG + "constructor entered.");

        _checked = false;
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

         Log.d(TAG, TAG + "constructor entered.");

        _checked = false;
    }

    @Override
    public boolean isChecked() {
        return _checked;
    }

    @Override
    public void setChecked(boolean checked) {
        Log.d(TAG, TAG + ".setChecked(" + checked + ") called.");

        _checked = checked;
    }

    @Override
    public void toggle() {
        setChecked(!_checked);
    }

    private static String TAG = "CheckableRelativeLayout";
    private boolean _checked;
}
