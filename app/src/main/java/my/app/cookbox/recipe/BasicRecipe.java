package my.app.cookbox.recipe;

import java.util.ArrayList;

/**
 * Created by Alexander on 026,  26 Apr.
 */

public  class BasicRecipe {

    public static final long NO_ID = -1;

    public Long id = null;
    public String name = "";
    public String short_desc = "";
    public String long_desc = "";
    public Float target_quantity = 1f;
    public String target_description = "";
    public String source = "";

    public ArrayList<RecipeIngredient> ingredients = new ArrayList<>();
    public ArrayList<String> instructions = new ArrayList<>();
    public ArrayList<String> comments = new ArrayList<>();
    public ArrayList<Long> tags = new ArrayList<>();

    public static class RecipeIngredient {
        public RecipeIngredient() {}

        public Float quantity = 1f;
        public String description = "";
        public Long other_recipe_id = null;
    }
}
