package my.app.cookbox.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import my.app.cookbox.R;
import my.app.cookbox.activity.MainActivity;
import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.recipe.Recipe;
import my.app.cookbox.utility.TagSelectionAdapter;

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
        return inflater.inflate(R.layout.recipe_list, container, false);
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
                    if (((TagSelectionAdapter) getListAdapter()).isItemChecked(i)) {
                        long recipe_id = ((BasicRecipe)getListAdapter().getItem(i)).getId();
                        long tag_id = _tag;
                        ((MainActivity) getActivity())
                                .getSqlController()
                                .addRecipeToTag(recipe_id, tag_id);
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

    private long _tag = Recipe.NO_ID;

    private String TAG = "TagSelectionListFrag";
}
