package my.app.cookbox.recipe;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import my.app.cookbox.sqlite.RecipeProvider;

/**
 * Created by Alexander on 026,  26 Apr.
 */

public  class BasicRecipe {
    public Long id = null;
    public String name = "";
    public String short_desc = "";
    public String long_desc = "";
    public double target_quantity = 1f;
    public String target_description = "";
    public String source = "";
    public double preparation_time = 0;
    public String time_modified;
    public boolean deleted = false;

    public ArrayList<RecipeIngredient> ingredients = new ArrayList<>();
    public ArrayList<String> instructions = new ArrayList<>();
    public ArrayList<String> comments = new ArrayList<>();
    public ArrayList<Long> tags = new ArrayList<>();

    public static JSONObject toJson(BasicRecipe br) throws JSONException {
        JSONObject jsonRecipe = new JSONObject();

        jsonRecipe.put("id", br.id);
        jsonRecipe.put("name", br.name);
        jsonRecipe.put("short_description", br.short_desc);
        jsonRecipe.put("long_description", br.long_desc);
        jsonRecipe.put("target_description", br.target_description);
        jsonRecipe.put("target_quantity", br.target_quantity);
        jsonRecipe.put("preparation_time", br.preparation_time);
        jsonRecipe.put("source", br.source);
        jsonRecipe.put("time_modified", br.time_modified);
        jsonRecipe.put("deleted", br.deleted);

        JSONArray ingredient_list = new JSONArray();
        for (RecipeIngredient ingredient : br.ingredients) {
            ingredient_list.put(ingredient.toJson());
        }
        jsonRecipe.put("ingredient_list", ingredient_list);

        JSONArray instruction_list = new JSONArray();
        for (String instruction : br.instructions) {
            instruction_list.put(instruction);
        }
        jsonRecipe.put("instruction_list", ingredient_list);

        JSONArray comment_list = new JSONArray();
        for (String comment : br.comments) {
            comment_list.put(comment);
        }
        jsonRecipe.put("comment_list", comment_list);

        JSONArray tag_list = new JSONArray();
        for (long tag_id : br.tags){
            tag_list.put(tag_id);
        }
        jsonRecipe.put("tag_list", tag_list);

        return jsonRecipe;
    }
    public static BasicRecipe fromJson(JSONObject jsonRecipe) throws JSONException {
        BasicRecipe recipe = new BasicRecipe();

        recipe.id = jsonRecipe.getLong("id");
        recipe.name = jsonRecipe.getString("name");
        recipe.short_desc = jsonRecipe.getString("short_description");
        recipe.long_desc = jsonRecipe.getString("long_description");
        recipe.target_quantity = jsonRecipe.getDouble("target_description");
        recipe.target_description = jsonRecipe.getString("target_description");
        recipe.preparation_time = jsonRecipe.getDouble("preparation_time");
        recipe.source = jsonRecipe.getString("source");
        recipe.time_modified = jsonRecipe.getString("time_modified");
        recipe.deleted = jsonRecipe.getBoolean("deleted");

        JSONArray ingredients = jsonRecipe.getJSONArray("ingredient_list");
        for (int j = 0; j < ingredients.length(); ++j) {
            final RecipeIngredient ingredient = RecipeIngredient.fromJson(ingredients.getJSONObject(j));
            recipe.ingredients.add(ingredient);
        }

        JSONArray instructions = jsonRecipe.getJSONArray("instruction_list");
        for (int j = 0; j < instructions.length(); ++j) {
            recipe.instructions.add(instructions.getString(j));
        }

        JSONArray comments = jsonRecipe.getJSONArray("comment_list");
        for (int j = 0; j < comments.length(); ++j) {
            recipe.comments.add(comments.getString(j));
        }

        JSONArray tags = jsonRecipe.getJSONArray("tag_list");
        for (int j = 0; j <  tags.length(); ++j) {
            recipe.tags.add(tags.getLong(j));
        }

        return recipe;
    }

    public static long writeBasicOnlyToProvider(BasicRecipe br, ContentResolver cr) {
        ContentValues recipe = new ContentValues();
        recipe.put("name", br.name);
        recipe.put("short_description", br.short_desc);
        recipe.put("long_description", br.long_desc);
        recipe.put("target_quantity", br.target_quantity);
        recipe.put("target_description", br.target_description);
        recipe.put("preparation_time", br.preparation_time);
        recipe.put("source", br.source);
        recipe.put("time_modified", br.time_modified);
        recipe.put("deleted", br.deleted);

        // Update
        if (br.id != null) {
            recipe.put("id",br.id);
            cr.update(RecipeProvider.recipe_uri,
                        recipe,
                        "id = ?",
                        new String[] {br.id.toString()});
        }
        // insert
        else {
            long id = ContentUris.parseId(cr.insert(RecipeProvider.recipe_uri, recipe));
            br.id = id;
        }

        return br.id;
    }

    public static long writeToProvider(BasicRecipe br, ContentResolver cr) {
        br.id = writeBasicOnlyToProvider(br, cr);

        // Insert lists
        cr.delete(RecipeProvider.ingredient_uri,
                        "recipe_id = ?",
                        new String[] {br.id.toString()});

        for (BasicRecipe.RecipeIngredient ing : br.ingredients) {
            ContentValues ingredient = new ContentValues();

            ingredient.put("quantity", ing.quantity);
            ingredient.put("description", ing.description);
            ingredient.put("other_recipe", ing.other_recipe_id);
            ingredient.put("recipe_id", br.id);

            cr.insert(RecipeProvider.ingredient_uri,ingredient);
        }

        cr.delete(RecipeProvider.instruction_uri,
                        "recipe_id = ?",
                        new String[] {br.id.toString()});

        for (int i = 0; i < br.instructions.size(); ++i)  {
            ContentValues instruction = new ContentValues();
            instruction.put("instruction", br.instructions.get(i));
            instruction.put("position", i);
            instruction.put("recipe_id",br.id);

            cr.insert(RecipeProvider.instruction_uri,instruction);
        }

        cr.delete(RecipeProvider.comment_uri,
                        "recipe_id = ?",
                        new String[] {br.id.toString()});

        for (String com : br.comments) {
            ContentValues comment = new ContentValues();
            comment.put("comment", com);
            comment.put("recipe_id", br.id);

            cr.insert(RecipeProvider.comment_uri, comment);
        }

        for (long tag_id : br.tags) {
            final ContentValues tag_cv = new ContentValues();
            tag_cv.put("recipe_id", br.id);
            tag_cv.put("tag_id", tag_id);

            cr.insert(RecipeProvider.tag_uri, tag_cv);
        }

        return br.id;
    }
    public static BasicRecipe readFromProvider(long id, ContentResolver cr) {
        final BasicRecipe br = new BasicRecipe();

        final Cursor recipe = cr.query(RecipeProvider.recipe_uri,
                null,
                "id = ?",
                new String[] {Long.toString(id)},
                null);
        recipe.moveToFirst();
        br.id = id;
        br.name = recipe.getString(recipe.getColumnIndex("name"));
        br.short_desc = recipe.getString(recipe.getColumnIndex("short_description"));
        br.long_desc = recipe.getString(recipe.getColumnIndex("long_description"));
        br.target_quantity = recipe.getFloat(recipe.getColumnIndex("target_quantity"));
        br.target_description = recipe.getString(recipe.getColumnIndex("target_description"));
        br.preparation_time = recipe.getDouble(recipe.getColumnIndex("preparation_time"));
        br.source = recipe.getString(recipe.getColumnIndex("source"));
        br.time_modified = recipe.getString(recipe.getColumnIndex("time_modified"));
        br.deleted = recipe.getInt(recipe.getColumnIndex("deleted")) != 0;
        recipe.close();

        final Cursor ingredient_list = cr.query(RecipeProvider.ingredient_uri,
                null,
                "recipe_id = ?",
                new String[] {Long.toString(id)},
                null);
        if (ingredient_list.moveToFirst()) {
            do {
                RecipeIngredient ing = new RecipeIngredient();
                ing.quantity = ingredient_list.getFloat(ingredient_list.getColumnIndex("quantity"));
                ing.description = ingredient_list.getString(ingredient_list.getColumnIndex("description"));
                if (ingredient_list.isNull(ingredient_list.getColumnIndex("other_recipe_id"))) {
                    ing.other_recipe_id = null;
                }
                else {
                    ing.other_recipe_id = ingredient_list.getLong(ingredient_list.getColumnIndex("other_recipe_id"));
                }
                br.ingredients.add(ing);
            } while(ingredient_list.moveToNext());
        }
        ingredient_list.close();

        final Cursor instruction_list = cr.query(RecipeProvider.instruction_uri,
                null,
                "recipe_id = ?",
                new String[] {Long.toString(id)},
                null);
        if (instruction_list.moveToFirst()) {
            do {
                br.instructions.add(instruction_list.getString(instruction_list.getColumnIndex("instruction")));
            } while(instruction_list.moveToNext());
        }
        instruction_list.close();

        final Cursor comment_list = cr.query(RecipeProvider.comment_uri,
                null,
                "recipe_id = ?",
                new String[] {Long.toString(id)},
                null);
        if (comment_list.moveToFirst()) {
            do {
                br.comments.add(comment_list.getString(comment_list.getColumnIndex("comment")));
            } while(comment_list.moveToNext());
        }
        comment_list.close();


        final Cursor tag_list = cr.query(RecipeProvider.tag_uri,
                null,
                "recipe_id = ?",
                new String[] {Long.toString(id)},
                null);
        if (tag_list.moveToFirst()) {
            do {
                br.tags.add(tag_list.getLong(tag_list.getColumnIndex("tag_id")));
            } while(tag_list.moveToNext());
        }
        tag_list.close();

        return br;
    }

    public static class RecipeIngredient {
        public RecipeIngredient() {}

        public Double quantity = 1d;
        public String description = "";
        public Long other_recipe_id = null;

        public JSONObject toJson() throws JSONException {
            final JSONObject ingredientJson = new JSONObject();
            ingredientJson.put("quantity", quantity);
            ingredientJson.put("description", description);
            ingredientJson.put("other_recipe_id", other_recipe_id);
            return ingredientJson;
        }

        public static RecipeIngredient fromJson(JSONObject jsonIngredient) throws JSONException {
            final RecipeIngredient ingredient = new RecipeIngredient();
            ingredient.quantity = jsonIngredient.getDouble("quantity");
            ingredient.description = jsonIngredient.getString("description");
            if (!jsonIngredient.isNull("other_recipe_id")) {
                ingredient.other_recipe_id = jsonIngredient.getLong("other_recipe_id");
            }
            return ingredient;
        }
    }
}
