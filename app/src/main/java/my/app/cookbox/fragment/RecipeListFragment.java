package my.app.cookbox.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import junit.framework.Test;

import java.util.Collections;
import java.util.Comparator;

import my.app.cookbox.R;
import my.app.cookbox.activity.TestActivity;
import my.app.cookbox.recipe.BasicRecipe;

/**
 * Created by Alexander on 020, 20 Jun.
 */

public class RecipeListFragment extends ListFragment {

    public RecipeListFragment() {
       super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                ((TestActivity) getActivity()).startRecipeFramgent(id);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.main_fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TestActivity) getActivity()).startModifyFragment(null);//null for new recipe
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.main_refresh:
                //TODO
                return true;
            case R.id.main_search:
                //TODO
                return true;
            case R.id.main_sort:
                sortRecipes();
                return true;
            case R.id.main_backup:
                //backupRecipes();
                return true;
            default:
                return false;
        }
    }

    private void sortRecipes() {
        sort_order = !sort_order;
         Collections.sort(((TestActivity) getActivity()).getAllBasicRecipes(), new Comparator<BasicRecipe>() {
                    @Override
                    public int compare(BasicRecipe o1, BasicRecipe o2) {
                        if (sort_order) return o1.getName().compareTo(o2.getName());
                        else return o1.getName().compareTo(o2.getName()) * -1;
                    }
                }
         );
        //_radapter.notifyDataSetChanged();
        //TODO
    }
    private boolean sort_order = false;

}
