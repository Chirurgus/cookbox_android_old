package my.app.cookbox.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import my.app.cookbox.R;
import my.app.cookbox.recipe.Recipe;

/**
 * Created by Alexander on 012, 12 Sep.
 */

public class TagSelectionListFragment extends ListFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        _tag = b.getLong("tag_id", Recipe.NO_ID);

        if (_tag == Recipe.NO_ID) {
            Log.e(TAG, TAG + " created without a tag id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.main_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private long _tag = Recipe.NO_ID;

    private String TAG = "TagSelectionListFrag";
}
