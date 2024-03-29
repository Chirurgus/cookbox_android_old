package my.app.cookbox.fragment;

import android.app.ListFragment;
import android.content.ContentResolver;
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

import my.app.cookbox.R;
import my.app.cookbox.activity.MainActivity;
import my.app.cookbox.activity.RecipeActivity;
import my.app.cookbox.recipe.Recipe;
import my.app.cookbox.recipe.RecipeTag;
import my.app.cookbox.sqlite.CookboxServerAPIHelper;
import my.app.cookbox.sqlite.RecipeProvider;
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
        if (_tag_id != empty_tag_id) {
           list_cursor = getContext()
                .getContentResolver()
                .query(RecipeProvider.recipe_list_uri,
                        null,
                        "id in (select recipe_id from tag_list where tag_id = ?)",
                        new String[] {Long.toString(_tag_id)},
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
        if (b != null) {
            _tag_id = b.getLong("tag_id", empty_tag_id);
        }
        else {
            _tag_id = empty_tag_id;
        }

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
    private Long _tag_id = null;
    private static long empty_tag_id = -1;


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

                // Get the ids of recipes that were updated since last sync
                final Cursor ids_cursor = cr.query(
                        RecipeProvider.recipe_uri,
                        new String[] {"id"},
                        time_token != null ? "time_modified > ?" : null,
                        time_token != null ? new String[] {time_token} : null,
                        null
                );
                final ArrayList<Long> updated_ids = new ArrayList<>();
                if (ids_cursor.moveToFirst()) {
                    do {
                       updated_ids.add(ids_cursor.getLong(ids_cursor.getColumnIndex("id")));
                    } while (ids_cursor.moveToNext());
                }

                final Cursor tags_cursor = cr.query(
                        RecipeProvider.tag_list_uri,
                        new String[] {"id"},
                        time_token != null ? "time_modified > ?" : null,
                        time_token != null ? new String[] {time_token} : null,
                        null
                );
                final ArrayList<Long> updated_tags = new ArrayList<>();
                if (tags_cursor.moveToFirst()) {
                    do {
                       updated_tags.add(tags_cursor.getLong(tags_cursor.getColumnIndex("id")));
                    } while (tags_cursor.moveToNext());
                }

                // First fetch updates from server

                // First fetch the tags
                JSONArray tagIds = sync.getJSONArray("tag_ids");
                for (int i = 0; i < tagIds.length(); ++i) {
                    final long id = tagIds.getLong(i);
                    final RecipeTag tag = RecipeTag.fromJson(cookboxApi.get_tag(id));
                    RecipeTag.writeToProvider(tag,cr);

                    // Remove this recipe from recipes whose changes will be pushed to the server
                    updated_tags.remove(id);
                }
                Log.d(TAG, "doInBackground: inserted tags.");

                // Then the recipes (without the lists)
                JSONArray recipeIds = sync.getJSONArray("recipe_ids");
                for (int i = 0; i < recipeIds.length(); ++i) {
                    final long id = recipeIds.getLong(i);
                    final Recipe recipe = Recipe.fromJson(cookboxApi.get_recipe(id));
                    Recipe.writeBasicOnlyToProvider(recipe, cr);

                    // Remove this recipe from recipes whose changes will be pushed to the server
                    updated_ids.remove(id);
                }

                // TODO: Avoid making two get_recipe() cookboxApi calls
                // Now write the recipes (with the lists)
                for (int i = 0; i < recipeIds.length(); ++i) {
                    final long id = recipeIds.getLong(i);
                    final Recipe recipe = Recipe.fromJson(cookboxApi.get_recipe(id));
                    Recipe.writeToProvider(recipe, cr);
                }
                Log.d(TAG, "doInBackground: Inserted changes from the server.");

                // Now push changes that weren't overwritten
                for (Long tag_id : updated_tags) {
                    RecipeTag tag = RecipeTag.readFromProvider(tag_id, cr);
                    cookboxApi.put_tag(RecipeTag.toJson(tag));
                }

                // TODO: This will fail if this recipe references another new recipe
                for (Long id : updated_ids) {
                    Recipe recipe = Recipe.readFromProvider(id,cr);
                    cookboxApi.put_recipe(Recipe.toJson(recipe));
                }

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

