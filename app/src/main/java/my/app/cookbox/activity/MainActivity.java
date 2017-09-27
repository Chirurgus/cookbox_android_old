package my.app.cookbox.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.List;

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

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log.d(TAG, TAG + ".onCreate(): ");

        _rlist = _sqlctrl.getAllBasicRecipes();
        startListFragment(getAllBasicRecipes());

        setupNavigationDrawer();
    }

    public void addToRecipeList(BasicRecipe new_br) {
        _rlist = _sqlctrl.getAllBasicRecipes();
        startListFragment(getAllBasicRecipes());
    }

    public RecipeListFragment startListFragment(ArrayList<BasicRecipe> rlist) {
        RecipeListFragment new_frag = new RecipeListFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_frame, new_frag);
        // A ListFragment is only added once.
        //ft.addToBackStack(null);
        ft.commit();

        new_frag.setListAdapter(new RecipeAdapter(rlist, this));
        return new_frag;
    }

    public TagSelectionListFragment startTagSelectionListFragment(long tag_id) {
        TagSelectionListFragment new_frag = new TagSelectionListFragment();

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

        return new_frag;
    }

    public ModifyFragment startModifyFragment(Long id) {
        ModifyFragment new_frag = new ModifyFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_frame, new_frag);

        ft.addToBackStack(null);
        if (id != null) {
            Bundle args = new Bundle();
            args.putLong("id", id);
            new_frag.setArguments(args);
        }
        ft.commit();
        return new_frag;
    }

    public RecipeFragment startRecipeFragment(Long id) {
        RecipeFragment new_frag = new RecipeFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_frame, new_frag);
        ft.addToBackStack(null);
        if (id != null) {
            Bundle args = new Bundle();
            args.putLong("id", id);
            new_frag.setArguments(args);
        }
        ft.commit();
        return new_frag;
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
            case R.id.dlist_context_edit: {
                int tag_pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                RecipeTag tag = ((RecipeTag) ((ListView) findViewById(R.id.drawer_list)).getAdapter().getItem(tag_pos));
                long tag_id = tag.getId();
                startTagSelectionListFragment(tag_id);
                return true;
            }
            case R.id.dlist_context_delete: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                        try {
                            getSqlController().removeTag(getSqlController().getAllRecipeTags().get(pos).getId());
                            populateDrawerTagList((ListView) findViewById(R.id.drawer_list));
                        } catch (SQLiteException e) {
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
            }
            case R.id.dlist_context_rename: {
                int tag_pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                RecipeTag tag = ((RecipeTag) ((ListView) findViewById(R.id.drawer_list)).getAdapter().getItem(tag_pos));
                final long tag_id = tag.getId();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter new Tag name");

                final EditText tag_name = new EditText(MainActivity.this);
                builder.setView(tag_name);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = tag_name.getText().toString();
                        getSqlController().renameTag(tag_id, name);
                        populateDrawerTagList((ListView) findViewById(R.id.drawer_list));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
                return true;
            }
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
                        long tag_id = ((RecipeTag) parent.getAdapter().getItem(pos)).getId();
                        startListFragment(_sqlctrl.getTaggedBasicRecipe(tag_id));
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

    private SqlController _sqlctrl = new SqlController(this);
    private ArrayList<BasicRecipe> _rlist = new ArrayList<>();

    private String TAG = "MainActivity";
}
