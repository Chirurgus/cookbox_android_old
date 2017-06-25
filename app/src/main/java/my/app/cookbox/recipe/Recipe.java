package my.app.cookbox.recipe;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Alexander on 012,  12 Apr.
 */

public class Recipe {

    public Recipe() { _id = NO_ID; }
    public Recipe(long id) { _id = id; }
    public Recipe(long id,
                  String name,
                  String short_desc,
                  String long_desc,
                  String target_desc,
                  float target_qty,
                  ArrayList<Float> ingredient_qty,
                  ArrayList<String> ingredient_desc,
                  ArrayList<Long> other_recipes,
                  ArrayList<String> instructions,
                  ArrayList<String> tags,
                  ArrayList<String> comments) {
        _id = id; _name = name; _short_desc = short_desc; _long_desc = long_desc;
        _target_desc = target_desc; _target_qty = target_qty; _ingredient_qty = ingredient_qty;
        _ingredient_desc = ingredient_desc; _other_recipes = other_recipes;
        _instructions = instructions; _tags = tags; _comments = comments;
    }

    //public enum Unit { unity, gram, undef }

    public BasicRecipe getBasicRecipe() {
        return new BasicRecipe(_id, _name, _short_desc, _tags);
    }

    public long getId() { return _id; }
    public String getName() { return _name; }
    public String getShortDescription() { return _short_desc; }
    public String getLongDescription() { return _long_desc; }
    //public Unit getTargetUnit() { return _target_unit; }
    public String getTargetDescription() { return _target_desc; }
    public Float getTargetQuantity() { return _target_qty; }

    public ArrayList<Float> getIngredientQuantity() { return _ingredient_qty; }
    public ArrayList<String> getIngredientDescriptions() { return _ingredient_desc; }
    public ArrayList<Long> getOtherRecipeIds() { return _other_recipes; }

    public ArrayList<String> getInstructions() { return _instructions; }

    public ArrayList<String> getTags() { return _tags; }
    public ArrayList<String> getComments() { return _comments; }

    public void setId(long id) { _id = id; }

    /*
    public static String unitToString(Unit u) {
        switch (u) {
            case unity:
                return "units";
            case gram:
                return "grams";
            default:
                return "undef";
        }
    }
    */

    /*
    public static Unit stringToUnit(String s) {
        switch (s) {
            case "units":
                return Unit.unity;
            case "grams":
                return Unit.gram;
            default:
                return Unit.undef;
        }
    }
    */
    public static long NO_ID = -1;

    private long _id = NO_ID;
    private String _name = "";
    private String _short_desc = "";
    private String _long_desc = "";
    //private Unit _target_unit = Unit.undef;
    private String _target_desc = "";
    private float _target_qty = 0;

    private ArrayList<Float> _ingredient_qty = new ArrayList<>();
    private ArrayList<String> _ingredient_desc = new ArrayList<>();
    private ArrayList<Long> _other_recipes = new ArrayList<>();

    private ArrayList<String> _instructions = new ArrayList<>();

    private ArrayList<String> _tags = new ArrayList<>();
    private ArrayList<String> _comments = new ArrayList<>();
}
