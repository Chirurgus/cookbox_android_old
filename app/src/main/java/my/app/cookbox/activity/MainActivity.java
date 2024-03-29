package my.app.cookbox.activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v4.provider.DocumentFile;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import my.app.cookbox.R;
import my.app.cookbox.fragment.ModifyFragment;
import my.app.cookbox.fragment.RecipeFragment;
import my.app.cookbox.fragment.RecipeListFragment;
import my.app.cookbox.fragment.TagSelectionListFragment;
import my.app.cookbox.recipe.Recipe;
import my.app.cookbox.recipe.RecipeTag;

/**
 * Created by Alexander on 016, 16 Jun.
 */

public class MainActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        Log.d(TAG, TAG + ".onCreate(): ");

        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            startListFragment();
        }

        setupNavigationDrawer();
    }

    public void addToRecipeList(Recipe new_br) {
        startListFragment();
    }

    public RecipeListFragment startListFragment() {
        return startListFragment(null);
    }

     /* shows all recipes, always on the bottom of the fragment stack */
    public RecipeListFragment startBaseListFragment() {
        RecipeListFragment frag = _bottom_rlist_frag;
        if (frag == null) {
            frag = new RecipeListFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();

            ft.replace(R.id.main_fragment_frame, frag);
            ft.commit();
        }
        return frag;
    }

    public RecipeListFragment startListFragment(Long tag_id) {
        RecipeListFragment new_frag = new RecipeListFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (tag_id != null) {
            Bundle b = new Bundle();
            b.putLong("tag_id", tag_id);
            new_frag.setArguments(b);
        }

        ft.replace(R.id.main_fragment_frame, new_frag);
        ft.addToBackStack(null);
        ft.commit();

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

        /*
        new_frag.setListAdapter(new TagSelectionAdapter(getAllBasicRecipes(),
                getSqlController().getTaggedBasicRecipe(tag_id),
                this));
                */

        return new_frag;
    }

    @Override
    public ModifyFragment startModifyFragment(Long id, boolean addToBackStack) {
        return super.startModifyFragment(id, R.id.main_fragment_frame, addToBackStack);
    }

    @Override
    public ModifyFragment startModifyFragment(Long id) {
        return this.startModifyFragment(id, true);
    }

    @Override
    public RecipeFragment startRecipeFragment(Long id, boolean addToBackStack) {
        return super.startRecipeFragment(id, R.id.main_fragment_frame, addToBackStack);
    }

    @Override
    public RecipeFragment startRecipeFragment(Long id) {
        return this.startRecipeFragment(id, true);
    }

    @Override
    public void onActivityResult(int request_code, int result_code, Intent intent) {
        Log.v(TAG, TAG + ".onActivityResult called.");

        if (request_code == PROMPT_FOR_BACKUP_DIR_REQUEST_CODE  && result_code == RESULT_OK) {
            if (intent != null) {
                _db_backup_location = intent.getData();
                backupRecipes();
            }
        }
        else {
            Log.v(TAG, TAG + ".onActivityResult with RESULT_CANCELED called.");
        }
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
                long tag_id = tag.id;
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
                            //getSqlController().removeTag(getSqlController().getAllRecipeTags().get(pos).getId());
                            populateDrawerTagList((ListView) findViewById(R.id.drawer_list));
                        } catch (SQLiteException e) {
                            /*
                            Toast toast = Toast.makeText(
                                    MainActivity.this,
                                    "Can't delete " + getSqlController().getAllRecipeTags().get(pos).getName() + ".",
                                    Toast.LENGTH_LONG
                            );
                            toast.show();
                            */
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
                final long tag_id = tag.id;

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter new Tag name");

                final EditText tag_name = new EditText(MainActivity.this);
                builder.setView(tag_name);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = tag_name.getText().toString();
                        //getSqlController().renameTag(tag_id, name);
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

    public void backupRecipes() {
        if (_db_backup_location == null) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, PROMPT_FOR_BACKUP_DIR_REQUEST_CODE);
            return;
        }

        File old_db = new File(getApplicationInfo().dataDir + File.separator  +"databases/recipes.db");
        DocumentFile df = DocumentFile.fromTreeUri(this, _db_backup_location);
        DocumentFile backup = df.findFile("recipe.db");
        if (backup != null && backup.exists()) {
            ContentResolver cr = getContentResolver();
            try {
                OutputStream os = cr.openOutputStream(backup.getUri());
                InputStream is = new FileInputStream(old_db);
                byte[] data = new byte[is.available()];
                is.read(data);
                os.write(data);
                is.close();
                os.close();


                Toast.makeText(this, "recipe.db backed up.", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e) {
                Log.e(TAG, TAG + ".backupRecipe: " + e.toString());
                Toast.makeText(this, "Could not back up the recipe", Toast.LENGTH_LONG).show();
            }
        }
        else {
            df.createFile("recipe/database", "recipe.db");
            backupRecipes();
        }
    }

    private void setupNavigationDrawer() {
        ((Button) findViewById(R.id.drawer_settings_button))
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startSettingsActivity();
                                        }
                                    }
                );
        final ListView drawer_list = (ListView) findViewById(R.id.drawer_list);

        populateDrawerTagList(drawer_list);

        drawer_list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        long tag_id = ((RecipeTag) parent.getAdapter().getItem(pos)).id;
                        startListFragment(tag_id);
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
                        //getSqlController().insertNewTag(tag);
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
        /*
        ArrayList<RecipeTag> tags = getSqlController().getAllRecipeTags();
        drawer_list.setAdapter(
                new ArrayAdapter<RecipeTag>(this, R.layout.tag_list_item,tags)
        );
        */
    }

    private void startSettingsActivity() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }
    private RecipeListFragment _bottom_rlist_frag = null;

    private Uri _db_backup_location = null;
    private int PROMPT_FOR_BACKUP_DIR_REQUEST_CODE = 1;

    private String TAG = "MainActivity";
}
