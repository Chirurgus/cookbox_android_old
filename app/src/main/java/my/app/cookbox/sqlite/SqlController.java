package my.app.cookbox.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.recipe.Recipe;
import my.app.cookbox.recipe.RecipeTag;

/**
 *
 * Created by Alexander on 012,  12 Apr.
 */

public class SqlController extends SQLiteOpenHelper {

    public SqlController(Context context, String db_name) {
        super(context, db_name, null, DB_VERSION);

        Log.d(TAG, "SqlController started.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            final String create_table = "CREATE TABLE tag(\n" +
                    "  id integer primary key,\n" +
                    "  tag text unique not null,\n" +
                    "  time_modified text not null default CURRENT_TIMESTAMP\n" +
                    ");                                               \n" +
                    "CREATE TABLE tag_list(                                \n" +
                    "  tag_id integer not null references tag(id) ON DELETE CASCADE,         \n" +
                    "  recipe_id integer not null references recipe(id) ON DELETE CASCADE    \n" +
                    ");                                                    \n" +
                    "CREATE TABLE instruction_list(                        \n" +
                    "  recipe_id integer not null references recipe(id) ON DELETE CASCADE,            \n" +
                    "  position integer not null,                          \n" +
                    "  instruction text not null default \"\"                           \n" +
                    ");                                                    \n" +
                    "CREATE TABLE comment_list(                            \n" +
                    "  recipe_id integer not null references recipe(id) ON DELETE CASCADE,   \n" +
                    "  comment text not null default \"\"\n" +
                    ");                                                    \n" +
                    "CREATE TABLE recipe(\n" +
                    "  id integer primary key,                             \n" +
                    "  name text not null default \"\",                      \n" +
                    "  short_description text not null default \"\",         \n" +
                    "  long_description text not null default \"\",          \n" +
                    "  target_quantity real not null default 1,                      \n" +
                    "  target_description text not null default \"\",\n" +
                    "  preparation_time real not null default 0,\n" +
                    "  source text not null default \"\",\n" +
                    "  deleted boolean not null default false,\n" +
                    "  time_modified text not null default CURRENT_TIMESTAMP\n" +
                    ");                                           \n" +
                    "CREATE TABLE ingredient_list(\n" +
                    "  recipe_id integer not null references recipe(id) ON DELETE CASCADE,   \n" +
                    "  quantity real not null default 1,                             \n" +
                    "  description text not null default \"\",                          \n" +
                    "  other_recipe integer null references recipe(id) ON DELETE RESTRICT     \n" +
                    ");\n" +
                    "\n" +
                    "CREATE TRIGGER on_update_recipe\n" +
                    "AFTER UPDATE ON recipe\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.id;\n" +
                    "END;\n" +
                    "\n" +
                    "CREATE TRIGGER on_update_ingredient_list\n" +
                    "AFTER UPDATE ON ingredient_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;\n" +
                    "\n" +
                    "CREATE TRIGGER on_update_instruction_list\n" +
                    "AFTER UPDATE ON instruction_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;\n" +
                    "\n" +
                    "CREATE TRIGGER on_update_comment_list\n" +
                    "AFTER UPDATE ON comment_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;\n" +
                    "\n" +
                    "CREATE TRIGGER on_update_tag_list\n" +
                    "AFTER UPDATE ON tag_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;\n" +
                    "\n" +
                    "CREATE TRIGGER on_insert_ingredient_list\n" +
                    "AFTER INSERT ON ingredient_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;\n" +
                    "\n" +
                    "CREATE TRIGGER on_insert_instruction_list\n" +
                    "AFTER INSERT ON instruction_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;\n" +
                    "\n" +
                    "CREATE TRIGGER on_insert_comment_list\n" +
                    "AFTER INSERT ON comment_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;\n" +
                    "\n" +
                    "CREATE TRIGGER on_insert_tag_list\n" +
                    "AFTER INSERT ON tag_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;\n" +
                    "\n" +
                    "CREATE TRIGGER on_delete_ingredient_list\n" +
                    "AFTER DELETE ON ingredient_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = OLD.recipe_id;\n" +
                    "END;\n" +
                    "\n" +
                    "CREATE TRIGGER on_delete_instruction_list\n" +
                    "AFTER DELETE ON instruction_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = OLD.recipe_id;\n" +
                    "END;\n" +
                    "\n" +
                    "CREATE TRIGGER on_delete_tag_list\n" +
                    "AFTER DELETE ON tag_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = OLD.recipe_id;\n" +
                    "END;\n" +
                    "\n" +
                    "CREATE TRIGGER on_delete_comment_list\n" +
                    "AFTER DELETE ON comment_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = OLD.recipe_id;\n" +
                    "END;\n" +
                    "\n" +
                    "CREATE TRIGGER on_update_tag\n" +
                    "AFTER UPDATE ON tag\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE tag\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.id;\n" +
                    "END;";
            db.execSQL(create_table);
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e(TAG, TAG + ".onCreate transaction failed.");
            throw e;
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Cursor rawQuery(String table,
                           String[] columns,
                           String selection,
                           String[] selectionArgs) {
        final SQLiteDatabase db = getReadableDatabase();
        return db.query(table,columns,selection,selectionArgs,null,null,null);
    }

    public void execSQL(String sql) {
        final SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        //needs to be disabled if db needs Upgrade
        db.setForeignKeyConstraintsEnabled(true);
    }

    private static final int DB_VERSION = 7;

    public static final String defaultDbName = "recipes.db";
    public static final String testDbName = "test.db";
    public static final String defaultDbDir = "";


    private static final String TAG = "SqlController";
}
