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
        Log.d(TAG, "onCreate: onCreate called.");
        db.beginTransaction();
        try {
            final String s1 = "CREATE TABLE tag(\n" +
                    "  id integer primary key,\n" +
                    "  tag text unique not null,\n" +
                    "  time_modified text not null default CURRENT_TIMESTAMP\n" +
                    ");";
            final String s2 = "CREATE TABLE tag_list(                                \n" +
                    "  tag_id integer not null references tag(id) ON DELETE CASCADE,         \n" +
                    "  recipe_id integer not null references recipe(id) ON DELETE CASCADE    \n" +
                    ");";
            final String s3 = "CREATE TABLE instruction_list(                        \n" +
                    "  recipe_id integer not null references recipe(id) ON DELETE CASCADE,            \n" +
                    "  position integer not null,                          \n" +
                    "  instruction text not null default \"\"                           \n" +
                    ");";
            final String s4 = "CREATE TABLE comment_list(                            \n" +
                    "  recipe_id integer not null references recipe(id) ON DELETE CASCADE,   \n" +
                    "  comment text not null default \"\"\n" +
                    ");";
            final String s5 = "CREATE TABLE recipe(\n" +
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
                    ");";
            final String s6 = "CREATE TABLE ingredient_list(\n" +
                    "  recipe_id integer not null references recipe(id) ON DELETE CASCADE,   \n" +
                    "  quantity real not null default 1,                             \n" +
                    "  description text not null default \"\",                          \n" +
                    "  other_recipe integer null references recipe(id) ON DELETE RESTRICT     \n" +
                    ");";
            final String s7 = "CREATE TRIGGER on_update_recipe\n" +
                    "AFTER UPDATE ON recipe\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.id;\n" +
                    "END;";
            final String s8 = "CREATE TRIGGER on_update_ingredient_list\n" +
                    "AFTER UPDATE ON ingredient_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;";
            final String s9 = "CREATE TRIGGER on_update_instruction_list\n" +
                    "AFTER UPDATE ON instruction_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;";
            final String s10 = "CREATE TRIGGER on_update_comment_list\n" +
                    "AFTER UPDATE ON comment_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;";
            final String s11 = "CREATE TRIGGER on_update_tag_list\n" +
                    "AFTER UPDATE ON tag_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;";
            final String s12 = "CREATE TRIGGER on_insert_ingredient_list\n" +
                    "AFTER INSERT ON ingredient_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;\n";
            final String s13 = "CREATE TRIGGER on_insert_instruction_list\n" +
                    "AFTER INSERT ON instruction_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;\n";
            final String s14 = "CREATE TRIGGER on_insert_comment_list\n" +
                    "AFTER INSERT ON comment_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;\n";
            final String s15 = "CREATE TRIGGER on_insert_tag_list\n" +
                    "AFTER INSERT ON tag_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.recipe_id;\n" +
                    "END;\n";
            final String s16 = "CREATE TRIGGER on_delete_ingredient_list\n" +
                    "AFTER DELETE ON ingredient_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = OLD.recipe_id;\n" +
                    "END;\n";
            final String s17 = "CREATE TRIGGER on_delete_instruction_list\n" +
                    "AFTER DELETE ON instruction_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = OLD.recipe_id;\n" +
                    "END;\n";
            final String s18 = "CREATE TRIGGER on_delete_tag_list\n" +
                    "AFTER DELETE ON tag_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = OLD.recipe_id;\n" +
                    "END;\n";
            final String s19 = "CREATE TRIGGER on_delete_comment_list\n" +
                    "AFTER DELETE ON comment_list\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE recipe\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = OLD.recipe_id;\n" +
                    "END;\n";
            final String s20 = "CREATE TRIGGER on_update_tag\n" +
                    "AFTER UPDATE ON tag\n" +
                    "BEGIN                                                     \n" +
                    "  UPDATE tag\n" +
                    "  SET time_modified = datetime()\n" +
                    "  WHERE id = NEW.id;\n" +
                    "END;";
            db.execSQL(s1);
            db.execSQL(s2);
            db.execSQL(s3);
            db.execSQL(s4);
            db.execSQL(s5);
            db.execSQL(s6);
            db.execSQL(s7);
            db.execSQL(s8);
            db.execSQL(s9);
            db.execSQL(s10);
            db.execSQL(s11);
            db.execSQL(s12);
            db.execSQL(s13);
            db.execSQL(s14);
            db.execSQL(s15);
            db.execSQL(s16);
            db.execSQL(s17);
            db.execSQL(s18);
            db.execSQL(s19);
            db.execSQL(s20);
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
