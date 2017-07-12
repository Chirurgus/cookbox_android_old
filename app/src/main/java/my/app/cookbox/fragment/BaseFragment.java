package my.app.cookbox.fragment;

import android.app.Fragment;
import android.view.View;

import junit.framework.Test;

import my.app.cookbox.activity.TestActivity;

/**
 * Created by Alexander on 011, 11 Jul.
 */

abstract public class BaseFragment extends Fragment {
    public BaseFragment() {
    }

    public TestActivity getParent() {
        return (TestActivity) super.getActivity();
    }

    public View getRootView() {
        return _root_view;
    }

    public void setRootView(View new_root) {
        _root_view = new_root;
    }

    private View _root_view = null;
}
