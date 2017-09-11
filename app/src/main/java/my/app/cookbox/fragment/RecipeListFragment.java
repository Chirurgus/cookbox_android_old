package my.app.cookbox.fragment;

import android.animation.Animator;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
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
        ((TestActivity) getActivity()).onListFragmentContentViewCreated();

        Bundle args = getArguments();
        if (args.getLong("tag_id", -1) != -1) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        }
        getListView().startActionMode()

        registerForContextMenu(getListView());

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                ((TestActivity) getActivity()).startRecipeFragment(id);
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

    /* ContextMenu is created in TestActivity.onContextMenuCreated */
    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rlist_context_edit:
                Toast.makeText(getActivity(), "rlist_context_edit", Toast.LENGTH_SHORT).show();
                ((TestActivity) getActivity()).startModifyFragment(
                        ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).id
                );
                return true;
            case R.id.rlist_context_delete:
                Toast.makeText(getActivity(), "rlist_context_delete", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Are you sure?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //User clicked YES
                         int pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                         TestActivity parent = ((TestActivity) getActivity());
                         try {
                             parent.getSqlController().removeRecipe(parent.getAllBasicRecipes().get(pos).getId());
                             parent.getAllBasicRecipes().remove(pos);
                         }
                        catch (SQLiteException e) {
                            Toast toast = Toast.makeText(
                                    parent,
                                    "Can't delete " + parent.getAllBasicRecipes().get(pos).getName() + ".",
                                    Toast.LENGTH_LONG
                            );
                            toast.show();
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //User clicked now
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.main_search:
                //TODO
                return true;
            case R.id.main_sort:
                sortRecipes();
                return true;
            case R.id.main_backup:
                //backupRecipes();
                return true;
            case R.id.main_test:
                Toast.makeText(getContext(),"" + Float.parseFloat("0.5"), Toast.LENGTH_LONG).show();
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

