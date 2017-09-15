package my.app.cookbox.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

import my.app.cookbox.R;
import my.app.cookbox.fragment.ModifyFragment;
import my.app.cookbox.fragment.RecipeFragment;
import my.app.cookbox.fragment.RecipeListFragment;
import my.app.cookbox.fragment.TagSelectionListFragment;
import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.recipe.RecipeTag;
import my.app.cookbox.sqlite.SqlController;
import my.app.cookbox.utility.RecipeAdapter;
import my.app.cookbox.utility.TagSelectionAdapter;

/**
 * Created by Alexander on 016, 16 Jun.
 */

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setActionBar((Toolbar) findViewById(R.id.main_toolbar));

        selectCategory(null);
        startListFragment(_rlist);

        setupNavigationDrawer();
    }

    public void addToRecipeList(BasicRecipe new_br) {
        for (int i = 0; i < _rlist.size(); ++i) {
            if (_rlist.get(i).getId() == new_br.getId()) {
                _rlist.set(i, new_br);
            }
        }
        ((RecipeAdapter) _toplistfrag.getListAdapter()).updateDataset(_rlist);
    }

    public RecipeListFragment startListFragment(ArrayList<BasicRecipe> rlist) {
        RecipeListFragment new_frag = new RecipeListFragment();
        if (new_frag != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment_frame, new_frag);
            // A ListFragment is only added once.
            //ft.addToBackStack(null);
            ft.commit();

            new_frag.setListAdapter(new RecipeAdapter(rlist, this));
        }
        return _toplistfrag = new_frag;
    }

    public TagSelectionListFragment startTagSelectionListFragment(long tag_id) {
        selectCategory(null);
        TagSelectionListFragment new_frag = new TagSelectionListFragment();
        if (new_frag != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Bundle b = new Bundle();
            b.putLong("tag_id", tag_id);
            new_frag.setArguments(b);
            ft.replace(R.id.main_fragment_frame, new_frag);
            ft.addToBackStack(null);
            ft.commit();

            new_frag.setListAdapter(new TagSelectionAdapter(_rlist,
                    _sqlctrl.getTaggedBasicRecipe(tag_id),
                    this));
        }
        return new_frag;
    }

    public ModifyFragment startModifyFragment(Long id) {
       ModifyFragment new_frag = new ModifyFragment();
        if (new_frag != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment_frame, new_frag);

            ft.addToBackStack(null);
            if (id != null) {
                Bundle args = new Bundle();
                args.putLong("id", id);
                new_frag.setArguments(args);
            }
            ft.commit();
        }
        return new_frag;
    }

    public RecipeFragment startRecipeFragment(Long id) {
        RecipeFragment new_frag = new RecipeFragment();
        if (new_frag != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment_frame, new_frag);
            ft.addToBackStack(null);
            if (id != null) {
                Bundle args = new Bundle();
                args.putLong("id", id);
                new_frag.setArguments(args);
            }
            ft.commit();
        }
        return new_frag;
    }

    public void selectCategory(RecipeTag tag) {
        if (tag == null) {
            Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
            _rlist = _sqlctrl.getAllBasicRecipes();
            return;
        }

        _rlist = _sqlctrl.getTaggedBasicRecipe(tag.getId());
    }

    public ArrayList<BasicRecipe> getAllBasicRecipes() {
        return _rlist;
    }

    public SqlController getSqlController() {
        return _sqlctrl;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.drawer_list) {
            getMenuInflater().inflate(R.menu.drawer_list_context, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.recipe_list_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dlist_context_edit:
                startTagSelectionListFragment(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).id);
                hideToolbar();
                return true;
            case R.id.dlist_context_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                        try {
                            getSqlController().removeTag(getSqlController().getAllRecipeTags().get(pos).getId());
                            populateDrawerTagList((ListView) findViewById(R.id.drawer_list));
                        }
                        catch (SQLiteException e) {
                            Toast toast = Toast.makeText(
                                    MainActivity.this,
                                    "Can't delete " + getSqlController().getAllRecipeTags().get(pos).getName() + ".",
                                    Toast.LENGTH_LONG
                            );
                            toast.show();
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            /*
            //handled in RecipeListFragment
            case R.id.dlist_context_edit:
            case R.menu.drawer_list_context:
             */
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setVisibility(View.INVISIBLE);

    }

    private void hideToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setVisibility(View.VISIBLE);
    }

    private void setupNavigationDrawer() {
        final ListView drawer_list = (ListView) findViewById(R.id.drawer_list);

        populateDrawerTagList(drawer_list);

        drawer_list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        selectCategory((RecipeTag) parent.getAdapter().getItem(pos));
                        ((RecipeAdapter) _toplistfrag.getListAdapter()).updateDataset(_rlist);
                    }
                }
        );

        registerForContextMenu(drawer_list);

        final Button b = (Button) findViewById(R.id.drawer_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter a Tag");

                final EditText tag_name = new EditText(MainActivity.this);
                builder.setView(tag_name);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String tag = tag_name.getText().toString();
                        getSqlController().insertNewTag(tag);
                        populateDrawerTagList(drawer_list);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    private void populateDrawerTagList(ListView drawer_list) {
        ArrayList<RecipeTag> tags = _sqlctrl.getAllRecipeTags();
        drawer_list.setAdapter(
                new ArrayAdapter<RecipeTag>(this, R.layout.tag_list_item,tags)
        );
    }

    private long _tag_id = -1;
    private SqlController _sqlctrl = new SqlController(this);
    private ArrayList<BasicRecipe> _rlist = new ArrayList<>();
    private RecipeListFragment _toplistfrag = null;
}
