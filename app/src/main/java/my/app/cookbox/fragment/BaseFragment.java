package my.app.cookbox.fragment;

import android.app.Fragment;
import android.view.View;

import my.app.cookbox.activity.BaseActivity;
import my.app.cookbox.activity.MainActivity;

/**
 * Created by Alexander on 011, 11 Jul.
 */

abstract public class BaseFragment extends Fragment {
    public BaseFragment() {
    }

    public BaseActivity getParent() {
        return (BaseActivity) super.getActivity();
    }

    public View getRootView() {
        return _root_view;
    }

    public void setRootView(View new_root) {
        _root_view = new_root;
    }

    private View _root_view = null;
}
