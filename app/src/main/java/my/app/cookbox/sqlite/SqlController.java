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
    public SqlController(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        Log.d(TAG, "SqlController started.");
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(false);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
        String create_table = "CREATE TABLE " + SqlRecipe.getTableName() +  "(";
        for (String s : SqlRecipe.getColumnNames()) {
            create_table = create_table + " " +  s + " " + SqlRecipe.getColumnType().get(s)
                                            + " " + SqlRecipe.getColumnOptions().get(s)
                        + ",";
        }
        //pop a char from the back of the string
        create_table = create_table.substring(0,create_table.length() - 1);
        create_table = create_table + ");";
        */
        db.beginTransaction();
        try {
            String create_recipe_table = "CREATE TABLE recipe(\n" +
                    "  id integer primary key,\n" +
                    "  name text not null default \"\",\n" +
                    "  short_description text not null default \"\",\n" +
                    "  long_description text not null default \"\",\n" +
                    "  target_quantity real not null,\n" +
                    "  target_description text not null default \"\"\n" +
                    ");";
            /*
            String create_unit_table = "CREATE TABLE unit(\n" +
                    "  id integer primary key,\n" +
                    "  unit text unique not null\n" +
                    ");";
                    */
            String create_tag_table = "CREATE TABLE tag(\n" +
                    "  id integer primary key,\n" +
                    "  tag text non null unique\n" +
                    ");";
            String create_tag_list_table = "CREATE TABLE tag_list(\n" +
                    "  tag_id integer not null references tag(id),\n" +
                    "  recipe_id integer not null references recipe(id)\n" +
                    ");";
            String create_instruction_list = "CREATE TABLE instruction_list(\n" +
                    "  recipe_id integer references recipe(id),\n" +
                    "  position integer not null,\n" +
                    "  instruction text not null\n" +
                    ");";
            String create_ingredient_list = "CREATE TABLE ingredient_list(\n" +
                    "  recipe_id integer not null references recipe(id),\n" +
                    "  quantity real not null,\n" +
                    "  description text not null,\n" +
                    "  other_recipe integer null references recipe(id)\n" +
                    ");";
            String create_comment_list = "CREATE TABLE comment_list(\n" +
                    "  recipe_id integer not null references recipe(id),\n" +
                    "  comment text not null unique\n" +
                    ");";

            db.execSQL(create_tag_table);
            db.execSQL(create_recipe_table);
            db.execSQL(create_tag_list_table);
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
        if (new_ver == 2) {
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
        if (new_ver == 3) {
            //TODO: remove the unique constraint on comments
        }
    }

    @Override
    public String getDatabaseName() {
        return "recipes.db";
    }

    public ArrayList<BasicRecipe> getAllBasicRecipes() {
        Log.v(TAG, TAG + ".getAllRecipes called.");

        SQLiteDatabase db = getReadableDatabase();
        ArrayList<BasicRecipe> ret = new ArrayList<>();
        try
        {
            db.beginTransaction();

            for (long id : getAllRecipeIds()) {
                ret.add(getBasicRecipe(id));
            }

            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e(TAG, TAG + ".getAllBasicRecipes transaction failed.");
            throw e;
        }
        finally {
            db.endTransaction();
        }
        return ret;
    }

    public ArrayList<Long> getAllRecipeIds() {
        Log.v(TAG, TAG + ".getAllRecipeNames called.");

        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Long> ret = new ArrayList<>();
        db.beginTransaction();
        try {
            String q = "SELECT id FROM recipe;";
            Cursor c = db.rawQuery(q, null);
            if (c.moveToFirst()) {
                do {
                    ret.add(c.getLong(c.getColumnIndex("id")));
                } while (c.moveToNext());
            }
            c.close();
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e(TAG, TAG + ".getALlRecipeIds transaction failed.");
            throw e;
        }
        finally {
            db.endTransaction();
        }
        return ret;
    }

    public Recipe getRecipe(long id) {
        Log.v(TAG, TAG + ".getRecipe called.");

        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        Recipe ret = null;
        try {
            ret = readRecipe(id,db);
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e(TAG, TAG + ".getRecipe transaction failed.");
            throw e;
        }
        finally {
            db.endTransaction();
        }
        return ret;
    }

    public BasicRecipe getBasicRecipe(long id) {

        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        BasicRecipe ret = null;
        try {
            Cursor c = db.query("recipe",
                    new String[] {"name", "short_description"},
                    "id = ?",
                    new String[] {"" + id},
                    null,
                    null,
                    null);
            if (c.moveToFirst()) {
                String name = c.getString(c.getColumnIndex("name"));
                String short_desc = c.getString(c.getColumnIndex("short_description"));
                ArrayList<String> tags = readTags(id, db);

                ret = new BasicRecipe(id, name, short_desc, tags);
            }
            c.close();
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e(TAG, TAG + ".getBasicRecipe transaction failed.");
        }
        finally {
            db.endTransaction();
        }
        return ret;
    }

    public ArrayList<RecipeTag> getAllRecipeTags() {
        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        ArrayList<RecipeTag> ret = new ArrayList<>();
        try {
            Cursor c = db.query("tag",
                    new String[] {"id", "tag"},
                    null,
                    null,
                    null,
                    null,
                    null);
            if (c.moveToFirst()) {
                do {
                    long id = c.getLong(c.getColumnIndex("id"));
                    String name = c.getString(c.getColumnIndex("tag"));
                    RecipeTag category = new RecipeTag(id, name);

                    ret.add(category);
                } while (c.moveToNext());
            }
            c.close();
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e(TAG, TAG + ".getAllRecipeTags transaction failed.");
        }
        finally {
            db.endTransaction();
        }
        return ret;
    }

    public ArrayList<BasicRecipe> getTaggedBasicRecipe(long tag_id) {
        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        ArrayList<BasicRecipe> ret = new ArrayList<>();
        try {
            Cursor c = db.query("tag_list",
                    new String[] {"recipe_id"},
                    "tag_id = ?",
                    new String[] {"" + tag_id},
                    null,
                    null,
                    null);
            if (c.moveToFirst()) {
                ArrayList<Long> recipe_ids = new ArrayList<>(c.getCount());
                do {
                    recipe_ids.add(c.getLong(c.getColumnIndex("recipe_id")));
                } while (c.moveToNext());

                for (Long id : recipe_ids) {
                    ret.add(getBasicRecipe(id));
                }
            }
            c.close();
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e(TAG, TAG + ".getAllTaggedBasicRecipe transaction failed.");
        }
        finally {
            db.endTransaction();
        }
        return ret;

    }

    /*
    public void updateRecipesTags(long tag_id, long[] recipe_ids) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (long recipe_id : recipe_ids) {
                ContentValues cv = new ContentValues();
                cv.put("tag_id", tag_id);
                cv.put("recipe_id", recipe_id);
                int u = db.updateWithOnConflict("tag_list",
                        cv,
                        null,
                        null,
                        SQLiteDatabase.CONFLICT_REPLACE
                );
                if (u == 0) {
                    db.insertOrThrow("tag_list", null, cv);
                }
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e(TAG, TAG + ".updateRecipeTags transaction failed.");
        }
        finally {
            db.endTransaction();
        }
    }
    */

    public long insertRecipe(Recipe r) {
        Log.v(TAG, TAG + ".insertRecipe called.");

        SQLiteDatabase db = getWritableDatabase();
        long ret = Recipe.NO_ID;
        db.beginTransaction();
        try {
            ret = updateRecipe(r,db);
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e(TAG, TAG + ".insertRecipe Transaction failed.");
            throw e;
        }
        finally {
            db.endTransaction();
        }
        return ret;
    }

    private long lastInsertId() {
        Log.v(TAG, TAG + ".lastInsertId called.");

        SQLiteDatabase db = getReadableDatabase();
        long ret;
        db.beginTransaction();
        try {
            String q = "SELECT LAST_INSERT_ROWID();";
            Cursor c = db.rawQuery(q, null);
            if (c.moveToFirst()) {
                ret = c.getLong(0);
            }
            ret = Recipe.NO_ID;

            c.close();
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e(TAG, TAG + ".lastInsertId transaction failed.");
            throw e;
        }
        finally {
            db.endTransaction();
        }
        return ret;
    }

    public void addRecipeToTag(long recipe_id, long tag_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("recipe_id", recipe_id);
            cv.put("tag_id", tag_id);
            int u = db.updateWithOnConflict("tag_list",
                    cv,
                    "recipe_id = ? && tag_id = ?",
                    new String[] {recipe_id + "", tag_id + ""},
                    SQLiteDatabase.CONFLICT_REPLACE);
            if (u == 0) {
                db.insertOrThrow("tag_list", null, cv);
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e(TAG, TAG + ".addRecipeToTag transaction failed.");
            throw e;
        }
        finally {
            db.endTransaction();
        }
    }

    public long insertNewTag(String tag_name) {
        long ret = Recipe.NO_ID;
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("tag", tag_name);
            ret = db.replaceOrThrow("tag", null, cv);

            db.setTransactionSuccessful();
        }
        catch(Exception e) {
            Log.e(TAG, TAG + ".insertTag transaction failed.");
            throw e;
        }
        finally {
            db.endTransaction();
        }
        return ret;
    }

    public void removeRecipe(long id) {
        Log.v(TAG, TAG + ".removeRecipe called.");

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            db.delete("comment_list",
                    "recipe_id =?",
                    new String[] {""+id});
            db.delete("ingredient_list",
                    "recipe_id = ?",
                    new String[] {"" + id});
            db.delete("instruction_list",
                    "recipe_id = ?",
                    new String[] {"" + id});
            db.delete("tag_list",
                    "recipe_id = ?",
                    new String[] {"" + id});
            db.delete("recipe",
                    "id = ?",
                    new String[] {"" + id});
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e(TAG, TAG + ".removeRecipe transaction failed.");
            throw e;
        }
        finally {
           db.endTransaction();
        }
    }

    public void removeTag(long id) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            db.delete("tag_list",
                    "tag_id = ?",
                    new String[] {"" + id}
            );
            db.delete("tag",
                    "id = ?",
                    new String[] {"" + id}
            );
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e(TAG, TAG + ".removeTag transaction failed.");
            throw e;
        }
        finally {
            db.endTransaction();
        }
    }

    public void clearOpenedDatabase() {
        Log.v(TAG, TAG + ".clearOpenedDatabase called.");

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (long id : getAllRecipeIds()) {
                removeRecipe(id);
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e(TAG, TAG + ".clearOpenedDatabase transaction failed");
            throw e;
        }
        finally {
            db.endTransaction();
        }
    }

    private Recipe readRecipe(long id, SQLiteDatabase db) {
        Recipe ret = null;
        Cursor c = db.query("recipe",
                null,
                "id = ?",
                new String[] {"" + id},
                null,
                null,
                null);
        if (c.moveToFirst()) {
            String name = c.getString(c.getColumnIndex("name"));
            String short_desc = c.getString(c.getColumnIndex("short_description"));
            String long_desc = c.getString(c.getColumnIndex("long_description"));
            String target_desc = c.getString(c.getColumnIndex("target_description"));
            float target_qty = c.getFloat(c.getColumnIndex("target_quantity"));

            ArrayList<Ingredient> ing_list = readIngredients(id, db);
            ArrayList<Float> ing_qty = new ArrayList<>();
            ArrayList<String> ing_desc = new ArrayList<>();
            ArrayList<Long> ing_other_rec = new ArrayList<>();
            for (Ingredient i : ing_list) {
                ing_qty.add(i.quantity);
                ing_desc.add(i.desc);
                ing_other_rec.add(i.other_recipe);
            }

            ArrayList<String> instruction_list = readInstructions(id,db);

            ArrayList<String> tag_list = readTags(id, db);
            ArrayList<String> comment_list = readComments(id, db);

            ret = new Recipe(id, name, short_desc, long_desc, target_desc, target_qty,
                                ing_qty, ing_desc, ing_other_rec, instruction_list, tag_list,
                                comment_list);
        }

        c.close();
        return ret;
    }

    private ArrayList<Ingredient> readIngredients(long recipe_id, SQLiteDatabase db) {
        ArrayList<Ingredient> ret = new ArrayList<>();
        Cursor c = db.query("ingredient_list",
                null,
                "recipe_id = ?",
                new String[] {"" + recipe_id},
                null,
                null,
                null);
        if (c.moveToFirst()) {
            do {
                Ingredient ing = new Ingredient();

                ing.quantity = c.getFloat(c.getColumnIndex("quantity"));
                ing.desc = c.getString(c.getColumnIndex("description"));
                if (!c.isNull(c.getColumnIndex("other_recipe"))) {
                    ing.other_recipe = c.getLong(c.getColumnIndex("other_recipe"));
                }
                ret.add(ing);
            } while (c.moveToNext());
        }
        c.close();
        return ret;
    }

     private ArrayList<String> readInstructions(long recipe_id, SQLiteDatabase db) {
        ArrayList<String> ret = new ArrayList<>();
        Cursor c = db.query("instruction_list",
                 null,
                 "recipe_id = ?",
                 new String[] {""+recipe_id},
                 null,
                 null,
                 "position ASC");
        if (c.moveToFirst()) {
            do {
                ret.add(c.getString(c.getColumnIndex("instruction")));
            } while (c.moveToNext());
        }
        c.close();
        return ret;
    }

     private ArrayList<String> readTags(long recipe_id, SQLiteDatabase db) {
        ArrayList<String> ret = new ArrayList<>();
        Cursor c = db.query("tag_list",
                new String[] {"tag_id"},
                "recipe_id = ?",
                new String[] {"" + recipe_id},
                null,
                null,
                null);
        if (c.moveToFirst()) {
            do {
                ret.add(lookupTag(c.getLong(c.getColumnIndex("tag_id")),db));
            } while (c.moveToNext());
        }
        c.close();
        return ret;
     }

    private ArrayList<String> readComments(long recipe_id, SQLiteDatabase db) {
        ArrayList<String> ret = new ArrayList<>();
        Cursor c = db.query("comment_list",
                new String[] {"comment"},
                "recipe_id = ?",
                new String[] {""+ recipe_id},
                null,
                null,
                null);
        if (c.moveToFirst()) {
            do {
                ret.add(c.getString(c.getColumnIndex("comment")));
            } while (c.moveToNext());
        }
        c.close();
        return ret;
    }

    private long updateRecipe(Recipe r, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put("id", r.getId());
        cv.put("name", r.getName());
        cv.put("short_description", r.getShortDescription());
        cv.put("long_description", r.getLongDescription());
        cv.put("target_quantity", r.getTargetQuantity());
        cv.put("target_description", r.getTargetDescription());
        r.setId(db.replaceOrThrow("recipe", null, cv));
        updateIngredients(r,db);
        updateInstructions(r,db);
        updateTags(r,db);
        updateComments(r,db);
        return r.getId();
    }

    private void updateIngredients(Recipe r, SQLiteDatabase db) {
        db.delete("ingredient_list",
                "recipe_id = ?",
                new String[] {"" + r.getId()});
        for (int i = 0; i < r.getIngredientDescriptions().size(); ++i) {
            ContentValues cv = new ContentValues();
            cv.put("recipe_id", r.getId());
            cv.put("quantity", r.getIngredientQuantity().get(i));
            cv.put("description", r.getIngredientDescriptions().get(i));
            if (r.getOtherRecipeIds().get(i) != Recipe.NO_ID) {
                cv.put("other_recipe", r.getOtherRecipeIds().get(i));
            }
            else {
                cv.putNull("other_recipe");
            }
            db.insertOrThrow("ingredient_list",
                    null,
                    cv);
        }
    }

    private void updateInstructions(Recipe r, SQLiteDatabase db) {
        db.delete("instruction_list",
                "recipe_id = ?",
                new String[] {"" + r.getId()});
        for (int i = 0; i < r.getInstructions().size(); ++i) {
           ContentValues cv = new ContentValues(3);
            cv.put("recipe_id",r.getId());
            cv.put("position",i);
            cv.put("instruction",r.getInstructions().get(i));
            db.insertOrThrow("instruction_list", null, cv);
        }
    }

    private void updateTags(Recipe r, SQLiteDatabase db) {
        db.delete("tag_list",
                "recipe_id = ?",
                new String[] {"" + r.getId()});
        for (String tag : r.getTags()) {
            ContentValues cv = new ContentValues();
            cv.put("recipe_id", r.getId());
            cv.put("tag_id", lookupTag(tag,db));
            db.insertOrThrow("tag_list",
                    null,
                    cv);
        }
    }

    private void updateComments(Recipe r, SQLiteDatabase db) {
        db.delete("comment_list",
                "recipe_id = ?",
                new String[] {"" + r.getId()});

        for (String comment : r.getComments()) {
            ContentValues cv = new ContentValues();
            cv.put("recipe_id", r.getId());
            cv.put("comment", comment);
            db.insertOrThrow("comment_list",
                    null,
                    cv);
        }
    }

    private long lookupTag(String tag, SQLiteDatabase db) {
        long ret;
        Cursor tag_c = db.query("tag",
                new String[] {"id"},
                "tag = ?",
                new String[] {tag},
                null,
                null,
                null);
        if (tag_c.moveToFirst()) {
            ret = tag_c.getLong(tag_c.getColumnIndex("id"));
        }
        else {
            ContentValues cv = new ContentValues();
            cv.put("tag", tag);
            ret = db.insertOrThrow("tag",null,cv);
        }
        tag_c.close();
        return ret;
    }

    private String lookupTag(long tag_id, SQLiteDatabase db) {
        String ret = null;
        Cursor tag_c = db.query("tag",
                new String[] {"tag"},
                "id = ?",
                new String[] {"" + tag_id},
                null,
                null,
                null);
        if (tag_c.moveToFirst()) {
            ret = tag_c.getString(tag_c.getColumnIndex("tag"));
        }
        else {
            ret = "Tag not found, tag_id = " + tag_id;
        }
        tag_c.close();
        return ret;
    }

    private class Ingredient {
        float quantity;
        String desc;
        Long other_recipe = Recipe.NO_ID;
    }

    private static String TAG = "SqlController";

    private static int DB_VERSION = 2;
    private static String DB_NAME = "recipes.db";
}
