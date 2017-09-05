package my.app.cookbox.fragment;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;

import my.app.cookbox.R;
import my.app.cookbox.activity.TestActivity;
import my.app.cookbox.recipe.BasicRecipe;

/**
 * Created by Alexander on 028, 28 Aug.
 */

public class TagListFragment extends ListFragment {

    public TagListFragment() {
       super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerForContextMenu(getListView());

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            }
        });

        getListView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //have to return true, otherwise an exception is thrown (in onCreateActionMode)
                //the actual longlick is handled via registerForContextMenu()
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.main_fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //getActivity().getMenuInflater().inflate(R.menu.recipe_list_context,menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
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
