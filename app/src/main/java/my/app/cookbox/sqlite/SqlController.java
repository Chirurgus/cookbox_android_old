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

public class SqlController extends SQLiteOpenHelper{

    public Cursor rawQuery(String table,
                           String[] columns,
                           String selection,
                           String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(table,columns,selection,selectionArgs,null,null,null);
    }

    public SqlController(Context context, String db_name) {
        super(context, db_name, null, DB_VERSION);

        Log.d(TAG, "SqlController started.");
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        //needs to be disabled if db needs Upgrade
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            String create_tag = "CREATE TABLE tag("
                    +"id integer primary key,"
                    +"tag text unique not null);";
            String create_tag_list = "CREATE TABLE tag_list("
                    +"tag_id integer not null references tag(id),"
                    +"recipe_id integer not null references recipe(id));";
            String create_instruction_list = "CREATE TABLE instruction_list("
                    + "recipe_id integer not null references recipe(id),"
                    + "position integer not null,"
                    + "instruction text not null default \"\");";
            String create_comment_list = "CREATE TABLE comment_list("
                    +"recipe_id integer not null references recipe(id),"
                    +"comment text not null default \"\");";
            String create_recipe = "CREATE TABLE recipe("
                    +"id integer primary key,"
                    +"name text not null default \"\","
                    +"short_description text not null default \"\","
                    +"long_description text not null default \"\","
                    +"target_quantity real not null default 1,"
                    +"target_description text not null default \"\","
                    +"preparation_time real not null default 0,"
                    +"source text not null default \"\");";
            String create_ingredient_list = "CREATE TABLE ingredient_list("
                    +"recipe_id integer not null references recipe(id),"
                    +"quantity real not null default 1,"
                    +"description text not null default \"\","
                    +"other_recipe integer null references recipe(id));";

            db.execSQL(create_tag);
            db.execSQL(create_recipe);
            db.execSQL(create_tag_list);
            db.execSQL(create_ingredient_list);
            db.execSQL(create_instruction_list);
            db.execSQL(create_comment_list);

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

    /* This is already called from a transaction so no need to create new one here
        see the declaration from super for details.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int old_ver, int new_ver) {
        if (old_ver < 2) {
            String drop_unit_tbl = "DROP TABLE unit";
            String create_new_recipe_tbl = "CREATE TABLE tmp_recipe(\n" +
                    "  id integer primary key,\n" +
                    "  name text not null default \"\",\n" +
                    "  short_description text not null default \"\",\n" +
                    "  long_description text not null default \"\",\n" +
                    "  target_quantity real not null,\n" +
                    "  target_description TEXT not null default \"\"\n" +
                    ");\n";
            String create_new_ing_tbl = "CREATE TABLE tmp_ingredient_list(\n" +
                    "  recipe_id integer not null references recipe(id),\n" +
                    "  quantity real not null,\n" +
                    "  description text not null,\n" +
                    "  other_recipe integer null references recipe(id)\n" +
                    ");";
            String move_to_new_recipe
                    = "insert into tmp_recipe(id," +
                    "name," +
                    "short_description," +
                    "long_description," +
                    "target_quantity) " +
                    "select id," +
                    "name," +
                    "short_description," +
                    "long_description," +
                    "cast(target_quantity as real) " +
                    "from " +
                    "recipe;";
            String move_to_new_ing
                    = "insert into tmp_ingredient_list(recipe_id," +
                    "quantity," +
                    "description," +
                    "other_recipe) " +
                    "select recipe_id," +
                    "cast(quantity as real)," +
                    "description," +
                    "other_recipe " +
                    "from " +
                    "ingredient_list;";
            String drop_recipe_tbl = "drop table recipe";
            String drop_ing_tbl = "drop table ingredient_list";
            String rename_recipe_tbl = "alter table tmp_recipe rename to recipe";
            String rename_ing_tbl = "alter table tmp_ingredient_list rename to ingredient_list";
            try {
                db.execSQL(drop_unit_tbl);
                db.execSQL(create_new_recipe_tbl);
                db.execSQL(create_new_ing_tbl);
                db.execSQL(move_to_new_recipe);
                db.execSQL(move_to_new_ing);
                db.execSQL(drop_recipe_tbl);
                db.execSQL(drop_ing_tbl);
                db.execSQL(rename_recipe_tbl);
                db.execSQL(rename_ing_tbl);
            } catch (Exception e) {
                Log.e(TAG, TAG + ".onUpgrade transaction failed.");
                throw e;
            }
        }
        if (old_ver < 3) {
            String create_new_recipe_tbl = "CREATE TABLE new_recipe(\n" +
                    "  id integer primary key,\n" +
                    "  name text not null default \"\",\n" +
                    "  short_description text not null default \"\",\n" +
                    "  long_description text not null default \"\",\n" +
                    "  target_quantity real not null default 1,\n" +
                    "  target_description TEXT not null default \"\",\n" +
                    "  source text not null default \"\"\n" +
                    ");";
            String create_new_comment_tbl = "CREATE TABLE new_comment_list(\n" +
                    "  recipe_id integer not null references recipe(id),\n" +
                    "  comment text not null\n" +
                    ");";
            String move_to_new_recipe = "insert into new_recipe(id,name,short_description,long_description,target_quantity,target_description) select id,name,short_description,long_description,target_quantity,target_description from recipe;";
            String move_to_new_comment = "insert into new_comment_list(recipe_id,comment) select recipe_id,comment from comment_list;";
            String drop_recipe_tbl = "drop table recipe";
            String drop_comment_tbl = "drop table comment_list";
            String rename_recipe_tbl = "alter table new_recipe rename to recipe;";
            String rename_comment_tbl = "alter table new_comment_list rename to comment_list;";
            try {
                db.execSQL(create_new_recipe_tbl);
                db.execSQL(create_new_comment_tbl);
                db.execSQL(move_to_new_recipe);
                db.execSQL(move_to_new_comment);
                db.execSQL(drop_recipe_tbl);
                db.execSQL(drop_comment_tbl);
                db.execSQL(rename_recipe_tbl);
                db.execSQL(rename_comment_tbl);
            } catch (Exception e) {
                Log.e(TAG, TAG + ".onUpgrade transaction failed.");
                throw e;
            }
        }
        if (old_ver < 4) {
            String create_new_tag = "CREATE TABLE new_tag("
                    +"id integer primary key,"
                    +"tag text unique not null);";
            String move_to_new_tag = "INSERT INTO new_tag SELECT * FROM tag;";

            String create_new_instruction_list = "CREATE TABLE new_instruction_list("
                    +"recipe_id integer not null references recipe(id),"
                    +"position integer not null,"
                    +"instruction text not null default \"\");";
            String move_to_new_instruction_list = "INSERT INTO new_instruction_list select * from instruction_list;";
            String create_new_comment_list = "CREATE TABLE new_comment_list("
                        +"recipe_id integer not null references recipe(id),"
                        +"comment text not null default \"\");";
            String move_to_new_comment_list = "INSERT INTO new_comment_list select * from comment_list;";
            String add_recipe_column = "ALTER TABLE recipe ADD COLUMN preperation_time real not null default 0;";
            String create_new_ingredient_list =  "CREATE TABLE new_ingredient_list("
                        +"recipe_id integer not null references recipe(id),"
                        +"quantity real not null default 1,"
                        +"description text not null default \"\","
                        +"other_recipe integer null references recipe(id));";
            String move_to_new_ingredient_list = "INSERT INTO new_ingredient_list select * from ingredient_list;";
            String drop_tag = "DROP TABLE tag;";
            String rename_new_tag = "ALTER TABLE new_tag RENAME TO tag;";
            String drop_instruction_list = "DROP TABLE instruction_list;";
            String rename_new_instruction_list = "ALTER TABLE new_instruction_list RENAME TO instruction_list;";
            String drop_comment_list = "DROP TABLE comment_list;";
            String rename_new_comment_list = "ALTER TABLE new_comment_list RENAME TO comment_list;";
            String drop_ingredient_list = "DROP TABLE ingredient_list;";
            String rename_new_ingredient_list = "ALTER TABLE new_ingredient_list RENAME TO ingredient_list;";
            try {
                db.execSQL(create_new_tag);
                db.execSQL(move_to_new_tag);
                db.execSQL(create_new_instruction_list);
                db.execSQL(move_to_new_instruction_list);
                db.execSQL(create_new_comment_list);
                db.execSQL(move_to_new_comment_list);
                db.execSQL(add_recipe_column);
                db.execSQL(create_new_ingredient_list);
                db.execSQL(move_to_new_ingredient_list);
                db.execSQL(drop_tag);
                db.execSQL(rename_new_tag);
                db.execSQL(drop_instruction_list);
                db.execSQL(rename_new_instruction_list);
                db.execSQL(drop_comment_list);
                db.execSQL(rename_new_comment_list);
                db.execSQL(drop_ingredient_list);
                db.execSQL(rename_new_ingredient_list);
            } catch (Exception e) {
                Log.e(TAG, TAG + ".onUpgrade transaction failed.");
                throw e;
            }
        }
    }

    private static int DB_VERSION = 4;

    public static final String defaultDbName = "recipes.db";
    public static final String testDbName = "test.db";
    public static final String defaultDbDir = "";


    private static String TAG = "SqlController";
}
