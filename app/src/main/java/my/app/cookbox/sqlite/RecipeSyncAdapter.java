package my.app.cookbox.sqlite;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class RecipeSyncAdapter extends AbstractThreadedSyncAdapter {

    public RecipeSyncAdapter(
            Context context,
            boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public RecipeSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle bundle,
                              String s,
                              ContentProviderClient contentProviderClient,
                              SyncResult syncResult) {
        try {
            final CookboxServerAPIHelper cookboxApi = new CookboxServerAPIHelper("http://10.0.2.2:3000");

            // For now just grab all recipes
            final String time_token = null;
            final JSONObject sync = cookboxApi.sync(time_token);

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
                long id = tagIds.getJSONObject(i).getLong("id");
                final JSONObject tag = cookboxApi.get_tag(id);
                final ContentValues cv = new ContentValues();
                cv.put("tag", tag.getString("tag"));
                cv.put("id", tag.getLong("id"));
                cv.put("time_modified", tag.getString("time_modified"));

                contentProviderClient.insert(RecipeProvider.tag_uri, cv);
            }

            // Then the recipes
            JSONArray recipeIds = sync.getJSONArray("recipe_ids");
            for (int i = 0; i <= recipeIds.length(); ++i) {
                long id = recipeIds.getJSONObject(i).getLong("id");
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

                contentProviderClient.insert(RecipeProvider.recipe_uri, cv);
            }
        }
        catch (Exception err) {
            Log.e(TAG, "onPerformSync: Could not sync ", err);
        }
    }
}
