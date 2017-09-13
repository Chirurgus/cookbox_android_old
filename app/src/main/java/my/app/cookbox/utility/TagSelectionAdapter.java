package my.app.cookbox.utility;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import my.app.cookbox.R;
import my.app.cookbox.recipe.BasicRecipe;

/**
 * Created by Alexander on 012, 12 Sep.
 */

public class TagSelectionAdapter extends RecipeAdapter{
    public TagSelectionAdapter(ArrayList<BasicRecipe> d, Context c) {
        super(d,c);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View ret;

        if (convertView != null && convertView instanceof RelativeLayout) {
            ret = convertView;
        } else {
            ret = getInflater().inflate(R.layout.recipe_list_layout, null);

        }
        TextView tv_name = (TextView) ret.findViewById(R.id.recipe_list_text1);
        TextView tv_desc = (TextView) ret.findViewById(R.id.recipe_list_text2);

        tv_name.setText(((BasicRecipe) getItem(pos)).getName());
        tv_desc.setText(((BasicRecipe) getItem(pos)).getShortDescription());

        return ret;
    }
}
