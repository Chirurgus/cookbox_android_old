package my.app.cookbox.fragment;

import android.app.ListFragment;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import my.app.cookbox.R;
import my.app.cookbox.activity.MainActivity;
import my.app.cookbox.activity.SettingsActivity;
import my.app.cookbox.activity.RecipeActivity;
import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.recipe.Recipe;
import my.app.cookbox.sqlite.CookboxServerAPIHelper;
import my.app.cookbox.sqlite.RecipeProvider;
import my.app.cookbox.sqlite.RecipeSyncAdapter;
import my.app.cookbox.sqlite.SqlController;
import my.app.cookbox.utility.RecipeCursorAdapter;

import static android.content.ContentValues.TAG;

/**
 * Created by Alexander on 020, 20 Jun.
 */

public class RecipeListFragment extends ListFragment {

    public RecipeListFragment() {
       super();
    }

    @Override
    public void onStart() {
        super.onStart();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("All recipes");

        Cursor list_cursor = null;
        if (_tag_id != Recipe.NO_ID) {
           list_cursor = getContext()
                .getContentResolver()
                .query(RecipeProvider.recipe_list_uri,
                        null,
                        "id in (select recipe_id from tag_list where tag_id = ?)",
                        new String[] {Float.toString(_tag_id)},
                        null
                );
        }
        else {
           list_cursor = getContext()
                .getContentResolver()
                .query(RecipeProvider.recipe_list_uri,
                        null,
                        null,
                        null,
                        null
                );
        }

        setListAdapter(new RecipeCursorAdapter(getContext(),list_cursor,R.layout.recipe_list_item,true));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle b = getArguments();
        _tag_id = b.getLong("tag_id", Recipe.NO_ID);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recipe_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerForContextMenu(getListView());

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                ((MainActivity) getActivity()).startRecipeFragment(id);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.main_fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).startModifyFragment(null);//null for new recipe_toolbar
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.recipe_list_toolbar, menu);
    }

    /* ContextMenu is created in MainActivity.onContextMenuCreated */
    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rlist_context_edit:
                ((MainActivity) getActivity()).startModifyFragment(
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
                         MainActivity parent = ((MainActivity) getActivity());
                         try {
                             /*
                             parent.getSqlController().removeRecipe(parent.getAllBasicRecipes().get(pos).getId());
                             parent.getAllBasicRecipes().remove(pos);
                             */
                         }
                        catch (SQLiteException e) {
                             /*
                            Toast toast = Toast.makeText(
                                    parent,
                                    "Can't delete " + parent.getAllBasicRecipes().get(pos).getName() + ".",
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
                Toast.makeText(getContext(), "TODO", Toast.LENGTH_SHORT).show();
                //TODO
                return true;
            case R.id.main_sort:
                sortRecipes();
                return true;
            case R.id.main_open_db:
                Cursor c = getContext().getContentResolver().query(// Select short_description from recipe
                        RecipeProvider.recipe_uri,
                        null,
                        null,
                        null,
                        null
                );
                if (c == null) {
                    Toast.makeText(getContext(), "cursor is null!", Toast.LENGTH_SHORT).show();
                }
                if (c.moveToFirst()) {
                    Toast.makeText(getContext(), "first:", Toast.LENGTH_SHORT).show();
                    String s = DatabaseUtils.dumpCursorToString(c);
                    Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
                    /*
                    for (int i = 0; i < c.getColumnCount(); ++i) {
                        if (!c.isNull(i)) {
                            Toast.makeText(getContext(), c.getString(i), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), c.getColumnName(i) + "is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                    */
                }
                else {
                    Toast.makeText(getContext(), "first is emtpy", Toast.LENGTH_SHORT).show();
                }
                c.close();
                return true;

            case R.id.main_backup:
                ((MainActivity) getActivity()).backupRecipes();
                return true;
            case R.id.main_test_recipe:
                TestHTTPRequest httpRequest = new TestHTTPRequest(getContext().getContentResolver());
                httpRequest.execute();
                return true;
            case R.id.main_test_settings:
                startPreferenceActivity();
                return true;
            default:
                return false;
        }
    }

    private void startRecipeActivity(long id) {
        Intent i = new Intent(getContext(), RecipeActivity.class);
        i.putExtra("id",id);
        startActivity(i);
    }

    private void startPreferenceActivity() {
        Intent i = new Intent(getContext(),my.app.cookbox.activity.SettingsActivity.class);
        startActivity(i);
    }

    private void sortRecipes() {

    }

    private boolean sort_order = false;
    private long _tag_id = Recipe.NO_ID;


    class TestHTTPRequest extends AsyncTask<Void, Void, Boolean> {
        ContentResolver cr = null;

        TestHTTPRequest(ContentResolver cr) {
            this.cr = cr;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... arg0)
        {
            try {
                final CookboxServerAPIHelper cookboxApi = new CookboxServerAPIHelper("http://10.0.2.2:3000");
                // For now just grab all recipes
                final String time_token = null;
                final JSONObject sync = cookboxApi.sync(time_token);
                Log.d(TAG, "doInBackground: Got sync data.");

                /*
                // Compare recipes updated
                final Cursor ids = contentProviderClient.query(
                        RecipeProvider.recipe_uri,
                        new String[] {"id"},
                        "time_modified > ?",
                        new String[] {"2018"},
                        null
                );
                */

                /*
                // Update db if necessairy
                final SqlController sqlDb = new SqlController(getContext(), "recipe.db");
                if (sync.getInt("schema_version") > sqlDb.getDbVersion()) {
                    final int ver = sync.getInt("schema_version");
                    final String migration = cookboxApi.get_migration(ver).getString("migration");
                    sqlDb.execSQL(migration);
                }
                */

                // First fetch updates from server

                // First fetch the tags
                JSONArray tagIds = sync.getJSONArray("tag_ids");
                for (int i = 0; i <= tagIds.length(); ++i) {
                    final long id = tagIds.getLong(i);
                    final JSONObject tag = cookboxApi.get_tag(id);
                    final ContentValues cv = new ContentValues();
                    cv.put("tag", tag.getString("tag"));
                    cv.put("id", tag.getLong("id"));
                    cv.put("time_modified", tag.getString("time_modified"));

                    cr.insert(RecipeProvider.tag_uri, cv);
                }
                Log.d(TAG, "doInBackground: inserted tags.");

                // Then the recipes
                JSONArray recipeIds = sync.getJSONArray("recipe_ids");
                for (int i = 0; i <= recipeIds.length(); ++i) {
                    //long id = recipeIds.getJSONObject(i).getLong("id");
                    final long id = recipeIds.getLong(i);
                    final JSONObject recipe = cookboxApi.get_recipe(id);
                    final ContentValues cv = new ContentValues();
                    cv.put("id", recipe.getLong("id"));
                    cv.put("name", recipe.getString("name"));
                    cv.put("short_description", recipe.getString("short_description"));
                    cv.put("long_description", recipe.getString("long_description"));
                    cv.put("target_quantity", recipe.getString("target_quantity"));
                    cv.put("target_description", recipe.getString("target_description"));
                    cv.put("preparation_time", recipe.getDouble("preparation_time"));
                    cv.put("source", recipe.getString("source"));
                    cv.put("time_modified", recipe.getString("time_modified"));
                    cv.put("deleted", recipe.getBoolean("deleted"));

                    cr.insert(RecipeProvider.recipe_uri, cv);
                    //TODO: Insert all the lists
                }
                Log.d(TAG, "doInBackground: Inserted recipes.");
                return true;
            }
            catch (Exception err) {
                Log.e(TAG, "onPerformSync: Could not sync ", err);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
            if (result) {
                Log.d(TAG, "onOptionsItemSelected: Recived\n" + result.toString());
            }
            else {
                Log.d(TAG, "onOptionsItemSelected: null response");
            }

        }
    }
}

