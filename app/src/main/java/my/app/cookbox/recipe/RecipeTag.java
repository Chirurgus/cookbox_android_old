package my.app.cookbox.recipe;

/**
 * Created by Alexander on 031, 31 Aug.
 *
 * A struct for Category
 */

public class RecipeTag {

    public RecipeTag(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() { return name; }

    public long getId() { return id; }

    /* Implemeted to work with ArrayAdapter */
    @Override
    public String toString() {
        return getName();
    }

    private long id = Recipe.NO_ID;
    private String name = null;
}
