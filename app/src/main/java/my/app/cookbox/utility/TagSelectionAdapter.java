package my.app.cookbox.utility;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import my.app.cookbox.R;
import my.app.cookbox.recipe.BasicRecipe;

/**
 * Created by Alexander on 012, 12 Sep.
 */

public class TagSelectionAdapter extends RecipeAdapter{
    public TagSelectionAdapter(ArrayList<BasicRecipe> d, ArrayList<BasicRecipe> tagged_recipes, Context c) {
        super(d,c);

        _tagged = new ArrayList<>();
        for (int i = 0; i < d.size(); ++i) {
            _tagged.add(false);
        }
        for (BasicRecipe r : tagged_recipes) {
            for (int i = 0; i < d.size(); ++i) {
                if (d.get(i).getId() == r.getId()) {
                    _tagged.set(i, true);
                    break;
                }
            }
        }
        int i= 1;
    }

    public boolean isItemChecked(int pos) {
        return _tagged.get(pos);
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent) {
        View ret;

        if (convertView != null && convertView instanceof RelativeLayout) {
            ret = convertView;
        } else {
            ret = getInflater().inflate(R.layout.tag_selection_list_item, null);

        }
        TextView tv_name = (TextView) ret.findViewById(R.id.tag_selection_list_item_text1);
        TextView tv_desc = (TextView) ret.findViewById(R.id.tag_selection_list_item_text2);

        tv_name.setText(((BasicRecipe) getItem(pos)).getName());
        tv_desc.setText(((BasicRecipe) getItem(pos)).getShortDescription());

        CheckBox c = (CheckBox) ret.findViewById(R.id.tag_selection_list_item_checkbox);
        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                return;//do nothing
            }
        });
        c.setChecked(_tagged.get(pos));
        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _tagged.set(pos, b);
            }
        });

        return ret;
    }
    ArrayList<Boolean> _tagged;
}
