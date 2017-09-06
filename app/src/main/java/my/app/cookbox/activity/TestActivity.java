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

import java.util.ArrayList;

import my.app.cookbox.R;
import my.app.cookbox.fragment.ModifyFragment;
import my.app.cookbox.fragment.RecipeFragment;
import my.app.cookbox.fragment.RecipeListFragment;
import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.recipe.RecipeTag;
import my.app.cookbox.sqlite.SqlController;
import my.app.cookbox.utility.RecipeAdapter;

/**
 * Created by Alexander on 016, 16 Jun.
 */

public class TestActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        selectCategory(null);
        _listfrag = startListFragment();

        setupNavigationDrawer();
    }

    public void addToRecipeList(BasicRecipe br) {
        _rlist.add(br);
        if (_listfrag != null) {
            ((RecipeAdapter) _listfrag.getListAdapter()).updateDataset(_rlist);
        }
    }

    public RecipeListFragment startListFragment() {
        RecipeListFragment new_frag = new RecipeListFragment();
        if (new_frag != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment_frame, new_frag);
            // A ListFragment is only added once.
            //ft.addToBackStack(null);
            ft.commit();

            new_frag.setListAdapter(new RecipeAdapter(_rlist, this));
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

        _rlist = _sqlctrl.getTaggedBasicRecipe(tag);
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
        getMenuInflater().inflate(R.menu.recipe_list_context,menu);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
             case R.id.rlist_context_edit:
                 Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.rlist_context_delete:
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
                                    TestActivity.this,
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
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void setupNavigationDrawer() {
        final ListView drawer_list = (ListView) findViewById(R.id.drawer_list);
        populateDrawerTagList(drawer_list);
        drawer_list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        selectCategory((RecipeTag) parent.getAdapter().getItem(pos));
                        ((RecipeAdapter) _listfrag.getListAdapter()).updateDataset(_rlist);
                    }
                }
        );
        registerForContextMenu(drawer_list);

        final Button b = (Button) findViewById(R.id.drawer_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
                builder.setTitle("Enter a Tag");

                final EditText tag_name = new EditText(TestActivity.this);
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
                new ArrayAdapter<RecipeTag>(this, R.layout.simple_list_text_layout ,tags)
        );
    }

    private SqlController _sqlctrl = new SqlController(this);
    private ArrayList<BasicRecipe> _rlist = new ArrayList<>();
    private RecipeListFragment _listfrag = null;
}
