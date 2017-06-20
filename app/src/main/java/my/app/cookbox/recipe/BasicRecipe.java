package my.app.cookbox.recipe;

import java.util.ArrayList;

/**
 * Created by Alexander on 026,  26 Apr.
 */

public  class BasicRecipe {

    public BasicRecipe(long id,
                  String name,
                  String short_desc,
                  ArrayList<String> tags) {
        _id = id; _name = name; _short_desc = short_desc; _tags = tags;
    }
    public long getId() { return _id; }
    public String getName() { return _name; }
    public String getShortDescription() { return _short_desc; }

    public ArrayList<String> getTags() { return _tags; }


    public static long NO_ID = -1;

    private long _id = NO_ID;
    private String _name = "";
    private String _short_desc = "";

    private ArrayList<String> _tags = new ArrayList<>();
}
