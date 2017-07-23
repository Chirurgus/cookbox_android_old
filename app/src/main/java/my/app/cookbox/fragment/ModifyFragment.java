package my.app.cookbox.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import my.app.cookbox.R;
import my.app.cookbox.activity.TestActivity;
import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.recipe.Recipe;

/**
 * Created by Alexander on 015, 15 Jun.
 */

public class ModifyFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        _root_view = inflater.inflate(R.layout.modify_layout, container, false);

        Button ing_b = (Button) _root_view.findViewById(R.id.modify_ingredient_button);
        ing_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandIngredientList();
            }
        });

        Button ins_b = (Button) _root_view.findViewById(R.id.modify_instruction_button);
        ins_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandInstructionList();
            }
        });

        Button cmnt_b = (Button) _root_view.findViewById(R.id.modify_comment_button);
        cmnt_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandCommentList();
            }
        });

        Button tag_b = (Button) _root_view.findViewById(R.id.modify_tag_button);
        tag_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              expandTagList();
            }
        });

        populateFields(_recipe);

        return _root_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _parent = (TestActivity) getActivity();

        Bundle args = getArguments();
        if (args != null && args.getLong("id") != Recipe.NO_ID) {
            _recipe = _parent.getSqlController().getRecipe(args.getLong("id"));
        }
        else {
            _recipe = new Recipe();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, TAG + ".onPause called.");

        updateRecipeList();

        Toast toast = Toast.makeText(getContext(), "Recipe was saved.",Toast.LENGTH_SHORT);
        toast.show();
    }


    private void updateRecipeList() {
        _recipe = readRecipe(_recipe.getId());
        _recipe.setId(_parent.getSqlController().insertRecipe(_recipe));
        _parent.addToRecipeList(_recipe.getBasicRecipe());
    }

    private Recipe readRecipe(long id) {
        String name = getEditTextFromId(R.id.modify_edit_text_name, _root_view).getText().toString();
        String long_desc = getEditTextFromId(R.id.modify_edit_text_long_desc, _root_view).getText().toString();
        String short_desc = getEditTextFromId(R.id.modify_edit_text_long_desc, _root_view).getText().toString();
        String tgt_desc = getEditTextFromId(R.id.modify_edit_text_tgt_desc, _root_view).getText().toString();
        float tgt_qty = Float.parseFloat(getEditTextFromId(R.id.modify_edit_text_tgt_qty, _root_view).getText().toString());

        ArrayList<String> ing_list = new ArrayList<>();
        ArrayList<Float> ing_qty_list = new ArrayList<>();
        ArrayList<Long> ing_other_rec = new ArrayList<>();

        LinearLayout ing_ll = (LinearLayout) _root_view.findViewById(R.id.modify_ingredient_list);
        for (int i = 0; i < ing_ll.getChildCount(); ++i) {
            RelativeLayout rl = (RelativeLayout) ing_ll.getChildAt(i);
            String qty = getEditTextFromId(R.id.modify_list_item_edit_text1, rl).getText().toString();
            try {
                ing_qty_list.add(Float.parseFloat(qty));
            }
            catch (Exception e) {
                ing_qty_list.add((float) -1);
            }
            ing_list.add(getEditTextFromId(R.id.modify_list_item_edit_text2, rl).getText().toString());
            Spinner sp = (Spinner) rl.findViewById(R.id.modify_list_item_spinner);
            if (sp.getSelectedItemPosition() != 0) {
                ing_other_rec.add(_parent.getAllBasicRecipes().get(sp.getSelectedItemPosition() - 1).getId());
            }
            else {
                ing_other_rec.add(Recipe.NO_ID);
            }
        }

        ArrayList<String> ins_list = new ArrayList<>();

        LinearLayout ins_ll = (LinearLayout) _root_view.findViewById(R.id.modify_instruction_list);
        for (int i = 0; i < ins_ll.getChildCount(); ++i) {
            RelativeLayout rl = (RelativeLayout) ins_ll.getChildAt(i);
            TextView desc_tv = ((TextInputLayout) rl.getChildAt(0)).getEditText();

            ins_list.add(desc_tv.getText().toString());
        }

        ArrayList<String> tag_list = new ArrayList<>();

        LinearLayout tag_ll = (LinearLayout) _root_view.findViewById(R.id.modify_tag_list);
        for (int i = 0; i < tag_ll.getChildCount(); ++i) {
            RelativeLayout rl = (RelativeLayout) tag_ll.getChildAt(i);
            TextView tag_tv = ((TextInputLayout) rl.getChildAt(0)).getEditText();

            tag_list.add(tag_tv.getText().toString());
        }

        ArrayList<String> cmnt_list = new ArrayList<>();

        LinearLayout comment_ll = (LinearLayout) _root_view.findViewById(R.id.modify_comment_list);
        for (int i = 0; i < comment_ll.getChildCount(); ++i) {
            RelativeLayout rl = (RelativeLayout) comment_ll.getChildAt(i);
            TextView cmnt_tv = ((TextInputLayout) rl.getChildAt(0)).getEditText();

            cmnt_list.add(cmnt_tv.getText().toString());
        }

        return new Recipe(id, name, short_desc, long_desc, tgt_desc, tgt_qty, ing_qty_list,
                ing_list, ing_other_rec,ins_list,tag_list,cmnt_list);
    }

    private void populateFields(Recipe r) {
        EditText name = getEditTextFromId(R.id.modify_edit_text_name, _root_view);
        name.setText(r.getName());

        EditText short_desc = getEditTextFromId(R.id.modify_edit_text_short_desc, _root_view);
        short_desc.setText(r.getShortDescription());

        EditText long_desc = getEditTextFromId(R.id.modify_edit_text_long_desc, _root_view);
        long_desc.setText(r.getLongDescription());

        EditText tgt_desc = getEditTextFromId(R.id.modify_edit_text_tgt_desc, _root_view);
        tgt_desc.setText(r.getTargetDescription());

        EditText target_qty = getEditTextFromId(R.id.modify_edit_text_tgt_qty, _root_view);
        target_qty.setText(r.getTargetQuantity().toString());

        for (int i = 0; i < r.getIngredientDescriptions().size(); ++i) {
            expandIngredientList();
            LinearLayout parent_ll = (LinearLayout) _root_view.findViewById(R.id.modify_ingredient_list);
            RelativeLayout rl = (RelativeLayout) parent_ll.getChildAt(parent_ll.getChildCount() - 1);

            EditText ing_qty = getEditTextFromId(R.id.modify_list_item_edit_text1, rl);
            ing_qty.setText(r.getIngredientQuantity().get(i).toString());

            EditText ing_desc = getEditTextFromId(R.id.modify_list_item_edit_text2, rl);
            ing_desc.setText(r.getIngredientDescriptions().get(i));

            Spinner other_r_sp = (Spinner) rl.findViewById(R.id.modify_list_item_spinner);
            long other_r_id = r.getOtherRecipeIds().get(i);
            if (other_r_id == Recipe.NO_ID) {
                other_r_sp.setSelection(0);
            }
            else {
                int br_pos = 0;
                for (; br_pos < _parent.getAllBasicRecipes().size(); ++br_pos) {
                    if (_parent.getAllBasicRecipes().get(br_pos).getId() == other_r_id) {
                        break;
                    }
                }
                if (other_r_id == _parent.getAllBasicRecipes().get(br_pos).getId()) {
                    other_r_sp.setSelection(br_pos + 1);
                }
                else {
                    other_r_sp.setSelection(0);
                }
            }
        }

        for (int i = 0; i < r.getInstructions().size(); ++i) {
            expandInstructionList();
            LinearLayout parent_ll = (LinearLayout) _root_view.findViewById(R.id.modify_instruction_list);
            RelativeLayout ll = (RelativeLayout) parent_ll.getChildAt(parent_ll.getChildCount() - 1);

            EditText ins_desc = getEditTextFromId(R.id.modify_list_item_edit_text2, ll);
            ins_desc.setText(r.getInstructions().get(i));
        }

        for (int i = 0; i < r.getTags().size(); ++i) {
            expandTagList();
            LinearLayout parent_ll = (LinearLayout) _root_view.findViewById(R.id.modify_tag_list);
            RelativeLayout ll = (RelativeLayout) parent_ll.getChildAt(parent_ll.getChildCount() - 1);

            EditText tag_desc = getEditTextFromId(R.id.modify_list_item_edit_text2, ll);
            tag_desc.setText(r.getTags().get(i));
        }

        for (int i = 0; i < r.getComments().size(); ++i) {
            expandCommentList();
            LinearLayout parent_ll = (LinearLayout) _root_view.findViewById(R.id.modify_comment_list);
            RelativeLayout ll = (RelativeLayout) parent_ll.getChildAt(parent_ll.getChildCount() - 1);

            EditText cmnt_desc = getEditTextFromId(R.id.modify_list_item_edit_text2, ll);
            cmnt_desc.setText(r.getComments().get(i));
        }
    }

    private void setupOtherRecipeSpinner(Spinner spinner) {
        ArrayList<String> names = new ArrayList<>();
        names.add("None");
        for (BasicRecipe br : getParent().getAllBasicRecipes()) {
            names.add(br.getName());
        }
        //unit_spinner_item will do just fine here
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getContext(), R.layout.unit_spinner_item,names);
        spinner.setAdapter(aa);
    }

    private void expandIngredientList() {
        RelativeLayout rl
                = ((RelativeLayout) getActivity()
                        .getLayoutInflater()
                        .inflate(R.layout.modify_item_layout, null));
        setupOtherRecipeSpinner((Spinner) rl.getChildAt(3));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        final LinearLayout parent_ll
                = (LinearLayout) _root_view.findViewById(R.id.modify_ingredient_list);
        parent_ll.addView(rl,lp);

        ImageButton b = (ImageButton) rl.findViewById(R.id.modify_list_item_del_button1);
        b.setOnClickListener(_onClickListener);
    }

    private void expandInstructionList() {
         RelativeLayout rl
                = ((RelativeLayout) getActivity()
                        .getLayoutInflater()
                        .inflate(R.layout.modify_item_layout, null));
        rl.removeViewAt(3);//remove spinner
        rl.removeViewAt(0);//remove quantity

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        final LinearLayout parent_ll
                = (LinearLayout) _root_view.findViewById(R.id.modify_instruction_list);
        parent_ll.addView(rl,lp);
        ImageButton b = (ImageButton) rl.findViewById(R.id.modify_list_item_del_button1);
        b.setOnClickListener(_onClickListener);
    }

    private void expandCommentList() {
        RelativeLayout rl
                = ((RelativeLayout) getActivity()
                        .getLayoutInflater()
                        .inflate(R.layout.modify_item_layout, null));
        rl.removeViewAt(3);//remove spinner
        rl.removeViewAt(0);//remove quantity

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        final LinearLayout parent_ll
                = (LinearLayout) _root_view.findViewById(R.id.modify_comment_list);
        parent_ll.addView(rl,lp);
        ImageButton b = (ImageButton) rl.findViewById(R.id.modify_list_item_del_button1);
        b.setOnClickListener(_onClickListener);
    }

    private void expandTagList() {
        //TAGS are not implemented as of yet.
        Toast t = Toast.makeText(getContext(), "Tags are not supported, yet.", Toast.LENGTH_SHORT);
        t.show();
    }



    private void popViewFromLinearLayout(LinearLayout parent) {
            if (parent.getChildCount() == 0) {
                return;
            }
            LinearLayout ll = (LinearLayout) parent.getChildAt(parent.getChildCount()-1);
            parent.removeView(ll);
    }

    private EditText getEditTextFromId(int view_id, View parent) {
            return ((TextInputLayout) parent.findViewById(view_id)).getEditText();
    }


    private static String TAG = "ModifyFragment";

    private TestActivity _parent = null;
    private View _root_view = null;
    private Recipe _recipe = new Recipe();

    private View.OnClickListener _onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((ViewGroup) v.getParent().getParent()).removeView((ViewGroup) v.getParent());
        }
    };
}
