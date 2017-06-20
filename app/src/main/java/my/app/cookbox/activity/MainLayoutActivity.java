package my.app.cookbox.activity;

import android.app.VoiceInteractor;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.DocumentsProvider;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import my.app.cookbox.R;
import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.recipe.Recipe;
import my.app.cookbox.sqlite.SqlController;
import my.app.cookbox.utility.Permissions;

public class MainLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "MainLayoutActivity started.");

        setContentView(R.layout.main_layout);

        //this.deleteDatabase("recipes.db");

        _sqlctrl = new SqlController(this);
        _rlist = _sqlctrl.getAllBasicRecipes();
        _radapter = new RecipeAdapter(_rlist, this);
        _lv =  (ListView) findViewById(R.id.main_list_view1);
        _lv.setAdapter(_radapter);
        _lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);
            }
        });

        _lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemLongClick(position);
                return true;
            }
        });

        _lv.setMultiChoiceModeListener(_amCallback);

        _am = null;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptNewRecipe();
            }
        });
    }

    @Override
    protected void onPause() {
        Log.d(TAG, TAG + ".onPause called.");

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.main_refresh:
                _radapter.notifyDataSetChanged();
                return true;
            case R.id.main_search:
                //TODO
                return true;
            case R.id.main_sort:
                sortRecipes();
                return true;
            case R.id.main_backup:
                backupRecipes();
                return true;
            case R.id.main_test:
                test();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int request_code, int result_code, Intent intent) {
        Log.v(TAG, TAG + ".onActivityResult called.");

        if (request_code == PROMPT_FOR_NEW_RECIPE_REQUEST_CODE && result_code == RESULT_OK) {
            Log.v(TAG, TAG + ".onActivityResult with RESULT_OK called.");

            long id = intent.getLongExtra("id", Recipe.NO_ID);
                _rlist.add(_sqlctrl.getBasicRecipe(id));
            _radapter.notifyDataSetChanged();
        }
        else if (request_code == PROMPT_FOR_BACKUP_DIR_REQUEST_CODE  && result_code == RESULT_OK) {
            if (intent != null) {
                backaup_dir_uri = intent.getData();
                backupRecipes();
            }
        }
        else {
            Log.v(TAG, TAG + ".onActivityResult with RESULT_CANCELED called.");
        }

    }

    private void onListItemClick(int pos) {
        Log.v(TAG, TAG + ".OnItemClick called.");

        Intent intent = new Intent(this, RecipeActivity.class);
        BasicRecipe recipe = _rlist.get(pos);

        intent.putExtra("id", recipe.getId());

        startActivity(intent);
    }

    private void onListItemLongClick(int pos) {
        Log.v(TAG, TAG + ".onListItemLongClick called");

        if (_am == null) {
            Log.d(TAG, TAG + ": ActionMode started.");
            _am = startActionMode(_amCallback);
        }
        _lv.setItemChecked(pos,!_lv.isItemChecked(pos));
    }

    private void promptNewRecipe() {
        Log.v(TAG, TAG + ".PromptNewRecipe called.");

        Intent intent = new Intent(this, ModifyRecipeActivity.class);

        startActivityForResult(intent, PROMPT_FOR_NEW_RECIPE_REQUEST_CODE);
    }

    private void deleteSelectedRecipes() {
        Log.v(TAG, TAG + ".deleteSelectedRecipes called.");

        for (int i = _rlist.size(); i-- > 0;) {
            if (_lv.isItemChecked(i)) {
                try {
                    _sqlctrl.removeRecipe(_rlist.get(i).getId());
                    _rlist.remove(_rlist.get(i));
                }
                catch (SQLiteException e) {
                    Toast toast = Toast.makeText(this,
                            "Can't delete " + _rlist.get(i).getName() + ".",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }

        }
        _radapter.notifyDataSetChanged();
    }

    private void sortRecipes() {
        sort_order = !sort_order;
         Collections.sort(_rlist, new Comparator<BasicRecipe>() {
                    @Override
                    public int compare(BasicRecipe o1, BasicRecipe o2) {
                        if (sort_order) return o1.getName().compareTo(o2.getName());
                        else return o1.getName().compareTo(o2.getName()) * -1;
                    }
                }
         );
        _radapter.notifyDataSetChanged();
    }

    private void backupRecipes() {

        if (backaup_dir_uri == null) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, PROMPT_FOR_BACKUP_DIR_REQUEST_CODE);
            return;
        }

        File old_db = new File(getApplicationInfo().dataDir + File.separator  +"databases/recipes.db");
        DocumentFile df = DocumentFile.fromTreeUri(this, backaup_dir_uri);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void test() {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }

    private static String TAG = "MainLayoutActivity";
    private static int PROMPT_FOR_NEW_RECIPE_REQUEST_CODE = 1;
    private static int PROMPT_FOR_BACKUP_DIR_REQUEST_CODE = 2;

    Uri backaup_dir_uri = null;
    static boolean sort_order = true;//for the sortRecipes function
    private SqlController _sqlctrl;
    private ArrayList<BasicRecipe> _rlist;
    private ListView _lv;
    private RecipeAdapter _radapter;
    private ActionMode _am;
    private AbsListView.MultiChoiceModeListener _amCallback = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            Log.v(TAG, TAG + "._amCallback.onItemCheckedStateChanged called.");
            if (mode != null) {
                mode.setTitle("Selected: " + _lv.getCheckedItemCount());
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Log.v(TAG, TAG + "._amCallback.onCreateActionMode called.");

            getMenuInflater().inflate(R.menu.main_context_menu,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            Log.v(TAG, TAG + "._amCallback.onPrepareActionMode called.");
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Log.v(TAG, TAG + "._amCallback.onActionItemClicked called.");
            switch(item.getItemId()) {
                case R.id.main_delete:
                    deleteSelectedRecipes();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Log.v(TAG, TAG + "._amCallback.onDestroyActionMode called.");
            _am = null;
        }
    };
}
