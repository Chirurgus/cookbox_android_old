package my.app.cookbox.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import my.app.cookbox.R;

/**
 * Created by Alexander on 007, 7 Feb.
 *
 * Base class for list items in Reciperagment
 * It abstracts from having to know how a certain layout
 * is layed out
 */

abstract public class RecipeItem {
    /*
     *
     */
    public RecipeItem(Context context, ViewGroup parent, int ressource) {
        view = (RelativeLayout) parent.inflate(context,R.layout.recipe_item, parent);
    }

    public void setText(String text) {
        ((TextView) view.getChildAt(0)).setText(text);
    }

    protected void setView(RelativeLayout view) {
        this.view = view;
    }

    protected RelativeLayout getView() {
        return view;
    }

    private RelativeLayout view = null;
}
