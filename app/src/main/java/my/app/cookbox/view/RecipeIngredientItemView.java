package my.app.cookbox.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import my.app.cookbox.R;

import my.app.cookbox.view.RecipeItem;

/**
 * Created by Alexander on 007, 7 Feb.
 *
 * Inflates a certain layout, and gives an iterface for it
 */

public class RecipeIngredientItemView extends RecipeItem {
    public RecipeIngredientItemView(Context context, ViewGroup parent) {
        super(context, parent, R.layout.recipe_ingredient_item);
    }

    public void setQuantity(float qty) {
        ((TextView) getView().getChildAt(0)).setText(Float.toString(qty));
    }

    public void setIngredient(String ing) {
        ((TextView) getView().getChildAt(0)).setText(ing);
    }

    public void setOtherRecipe() {

    }
}
