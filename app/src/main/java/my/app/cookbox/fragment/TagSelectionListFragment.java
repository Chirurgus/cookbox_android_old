package my.app.cookbox.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import my.app.cookbox.R;
import my.app.cookbox.activity.MainActivity;
import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.utility.TagSelectionAdapter;

/**
 * Created by Alexander on 012, 12 Sep.
 */

public class TagSelectionListFragment extends ListFragment {
    /*
    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Tag recipes");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        _tag = b.getLong("tag_id", -1);

        if (_tag == -1) {
            Log.e(TAG, TAG + " created without a tag id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.tag_selection_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.tag_selection_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tag_selection_confirm:
                for (int i = 0; i < getListAdapter().getCount(); ++i) {
                    long recipe_id = ((BasicRecipe)getListAdapter().getItem(i)).getId();
                    long tag_id = _tag;
                    if (((TagSelectionAdapter) getListAdapter()).isItemChecked(i)) {
                    }
                    else {
                    }
                }
                Toast.makeText(getContext(), "Recipes tagged.", Toast.LENGTH_SHORT).show();
                getActivity().getFragmentManager().popBackStack();
                return true;
            case R.id.tag_selection_cancel:
                getActivity().getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private long _tag;
    public class TagSelectionTag {
        long tag;
        ArrayList<Long> recipes;
    }
    private String TAG = "TagSelectionListFrag";
    */
}
