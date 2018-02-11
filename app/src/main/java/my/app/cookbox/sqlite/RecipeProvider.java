package my.app.cookbox.sqlite;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by Alexander on 003, 3 Feb.
 *
 * RecipeProvider
 * my.app.cookbox.recipe_provider/
 *                               /recipe        // recipe table in sql db
 *                               /ingredient    // ingredient_list table in sql db
 *                               /instruction   // instruction_list table in sql db
 *                               /comment       // comment_list table in sql db
 *                               /tag           // tag table in sql db
 */

public class RecipeProvider extends ContentProvider {
    public static final String authority = "my.app.cookbox.recipe_provider";

    public static final String recipe_table = "recipe";
    public static final String instruction_table = "instruction_list";
    public static final String ingredient_table = "ingredient_list";
    public static final String comment_table = "comment_list";
    public static final String tag_table = "tag_list";
    public static final String tag_list_table = "tag";

    private static final String content_uri = "content://" + authority;

    public static final Uri recipe_uri = Uri.parse(content_uri + "/" + recipe_table);
    public static final Uri ingredient_uri = Uri.parse(content_uri + "/" + ingredient_table);
    public static final Uri instruction_uri = Uri.parse(content_uri + "/" + instruction_table);
    public static final Uri comment_uri = Uri.parse(content_uri + "/" + comment_table);
    public static final Uri tag_uri = Uri.parse(content_uri + "/" + tag_table);
    public static final Uri tag_list_uri = Uri.parse(content_uri + "/" + tag_list_table);


    @Override
    public boolean onCreate() {
        if (getContext() == null) {
            return false;
        }
        initSqlCtrl();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selection_arg,
                        @Nullable String sort_order) {
        String table = null;
        switch (_uriMatcher.match(uri))  {
            case recipe:
                table = recipe_table;
                break;
            case ingredient:
                table = ingredient_table;
                break;
            case instruction:
                table = instruction_table;
                break;
            case comments:
                table = comment_table;
                break;
            case tag:
                table = tag_table;
                break;
            case tag_list:
                table = tag_list_table;
                break;
            default:
                return null;
        }
        initSqlCtrl();
        SQLiteDatabase db = getReadableDatabase();
        return db.query(table,projection,selection,selection_arg,null,null,sort_order);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri,
                      @Nullable ContentValues contentValues) {
        String table = null;
        switch (_uriMatcher.match(uri))  {
            case recipe:
                table = recipe_table;
                break;
            case ingredient:
                table = ingredient_table;
                break;
            case instruction:
                table = instruction_table;
                break;
            case comments:
                table = comment_table;
                break;
            case tag:
                table = tag_table;
                break;
            case tag_list:
                table = tag_list_table;
                break;
            default:
                return null;
        }
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insertWithOnConflict(table,null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues contentValues,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        String table = null;
        switch (_uriMatcher.match(uri))  {
            case recipe:
                table = recipe_table;
                break;
            case ingredient:
                table = ingredient_table;
                break;
            case instruction:
                table = instruction_table;
                break;
            case comments:
                table = comment_table;
                break;
            case tag:
                table = tag_table;
                break;
            case tag_list:
                table = tag_list_table;
                break;
            default:
                return 0;
        }
        SQLiteDatabase db = getWritableDatabase();
        return db.updateWithOnConflict(
                table,
                contentValues,
                selection,
                selectionArgs,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        String table = null;
        switch (_uriMatcher.match(uri))  {
            case recipe:
                table = recipe_table;
                break;
            case ingredient:
                table = ingredient_table;
                break;
            case instruction:
                table = instruction_table;
                break;
            case comments:
                table = comment_table;
                break;
            case tag:
                table = tag_table;
                break;
/*
            case all_tags:
                table = "tag";
            case all_recipes:
                table = "recipe";
            case all_ingredients:
                table = "ingredient";
            case all_instructions:
                table = "instruction";
            case all_comments:
                table = "comment";
                */
            default:
                return 0;
        }
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(table, selection, selectionArgs);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String vnd = "vnd.android.cursor.";
        String dir = vnd + "dir/vnd." + authority + ".";
        String item = vnd + "item/vnd." + authority + ".";
        switch (_uriMatcher.match(uri)) {
            case recipe:
                return dir + recipe_table;
            case ingredient:
                return dir + "ingredient";
            case instruction:
                return dir + "instruction";
            case comments:
                return dir + "instruction";
            case tag:
                return dir + "tag";
            default:
                return null;
        }
    }

    private SQLiteDatabase getReadableDatabase() {
         if (_sql_ctrl == null) {
            initSqlCtrl();
        }
        return _sql_ctrl.getReadableDatabase();
    }
    private SQLiteDatabase getWritableDatabase() {
        if (_sql_ctrl == null) {
            initSqlCtrl();
        }
        return _sql_ctrl.getWritableDatabase();
    }

    private void initSqlCtrl() {
        _sql_ctrl = new SqlController(getContext(),SqlController.defaultDbName);
    }

    private SqlController _sql_ctrl = null;
    private static final UriMatcher _uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int recipe = 1;
    private static final int ingredient = 3;
    private static final int instruction = 5;
    private static final int comments = 7;
    private static final int tag = 9;
    private static final int tag_list = 11;

    static {
       _uriMatcher.addURI(authority, recipe_table,recipe);
       _uriMatcher.addURI(authority, ingredient_table,ingredient);
       _uriMatcher.addURI(authority, instruction_table,instruction);
       _uriMatcher.addURI(authority, comment_table,comments);
       _uriMatcher.addURI(authority, tag_table,tag);
       _uriMatcher.addURI(authority, tag_list_table,tag_list);
    }
}
