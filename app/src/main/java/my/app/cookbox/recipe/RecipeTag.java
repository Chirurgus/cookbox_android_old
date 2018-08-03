package my.app.cookbox.recipe;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import my.app.cookbox.sqlite.RecipeProvider;

/**
 * Created by Alexander on 031, 31 Aug.
 *
 * A struct for Category
 */

public class RecipeTag {
    public RecipeTag() { }

    public Long id = null;
    public String tag = null;
    public String time_modified = null;
    public boolean deleted = false;

    public static JSONObject toJson(RecipeTag tag) throws JSONException {
        JSONObject jsonTag = new JSONObject();
        jsonTag.put("id", tag.id);
        jsonTag.put("tag", tag.tag);
        jsonTag.put("time_modified", tag.time_modified);
        jsonTag.put("deleted", tag.deleted);
        return jsonTag;
    }
    public static RecipeTag fromJson(JSONObject jsonTag) throws JSONException {
        RecipeTag tag = new RecipeTag();
        tag.id = jsonTag.getLong("id");
        tag.tag = jsonTag.getString("tag"):
        tag.time_modified = jsonTag.getString("time_modified");
        tag.deleted = jsonTag.getBoolean("deleted");
        return tag;
    }

    public static void writeToProvider(RecipeTag tag, ContentResolver cr) {
        final ContentValues cv = new ContentValues();
        cv.put("id", tag.id);
        cv.put("tag", tag.tag);
        cv.put("time_modified", tag.time_modified);
        cv.put("deleted", tag.deleted);
        cr.insert(RecipeProvider.tag_list_uri, cv);
    }
    public static RecipeTag readFromProvider(long id, ContentResolver cr) {
        final Cursor tagCursor = cr.query(RecipeProvider.tag_list_uri,
                null,
                "id = ?",
                new String[] {Long.toString(id)},
                null);
        tagCursor.moveToFirst();
        final RecipeTag tag = new RecipeTag();
        tag.id = tagCursor.getLong(tagCursor.getColumnIndex("id"));
        tag.tag = tagCursor.getString(tagCursor.getColumnIndex("tag"));
        tag.time_modified = tagCursor.getString(tagCursor.getColumnIndex("time_modified"));
        tag.deleted = tagCursor.getInt(tagCursor.getColumnIndex("deleted")) != 0;
        return tag;
    }
}
