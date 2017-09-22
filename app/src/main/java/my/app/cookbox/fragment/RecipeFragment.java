package my.app.cookbox.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import my.app.cookbox.R;
import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.recipe.Recipe;

/**
 * Created by Alexander on 011, 11 Jul.
 */

public class RecipeFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setRootView(inflater.inflate(R.layout.recipe, container, false));

        populateFields(_recipe);

        EditText target_qty_et = (EditText) getRootView().findViewById(R.id.recipe_text_qty_tgt);
        target_qty_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updateIngredientScale(Float.parseFloat(v.getText().toString()));
                    scaleIngredientQty();
                    return true;
                }
                return false;
            }
        });

        return getRootView();
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        Log.v(TAG, "RecipeFragment started.");

        super.onCreate(savedInstance);

        Bundle args = getArguments();
        if (args != null && args.getLong("id") != Recipe.NO_ID) {
            _recipe = getParent().getSqlController().getRecipe(args.getLong("id"));
        }
        else {
            Toast toast = Toast.makeText(getParent(), "Undefined recipe.", Toast.LENGTH_SHORT);
            toast.show();
            //TODO exit an activity
            _recipe = new Recipe();
        }

        setHasOptionsMenu(true);
        //Keep screen on
        getParent().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recipe_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recipe_edit :
                getParent().startModifyFragment(_recipe.getId());
                return true;
            default:
                return false;
        }
    }

    private void populateFields(final Recipe r) {
        TextView name = (TextView) getRootView().findViewById(R.id.recipe_text_name);
        name.setText(r.getName());

        TextView long_desc = (TextView) getRootView().findViewById(R.id.recipe_text_desc);
        long_desc.setText(r.getLongDescription());

        TextView target_qty = (TextView) getRootView().findViewById(R.id.recipe_text_qty_tgt);
        target_qty.setText(r.getTargetQuantity().toString());

        TextView target_desc = (TextView) getRootView().findViewById(R.id.recipe_text_desc_tgt);
        target_desc.setText(r.getTargetDescription().toString());

        for (int i = 0; i < r.getIngredientDescriptions().size(); ++i) {
            expandIngredientList();

            LinearLayout parent_ll = (LinearLayout) getRootView().findViewById(R.id.recipe_ingredient_list);

            RelativeLayout ing_item = (RelativeLayout) parent_ll.getChildAt(parent_ll.getChildCount() - 1);

            TextView ing_qty = (TextView) ing_item.getChildAt(0);
            ing_qty.setText(r.getIngredientQuantity().get(i).toString());

            TextView ing_desc = (TextView) ing_item.getChildAt(1);
            ing_desc.setText(r.getIngredientDescriptions().get(i));

            TextView other_r = (TextView) ing_item.getChildAt(2);
            if (r.getOtherRecipeIds().get(i) != Recipe.NO_ID) {
                final BasicRecipe br = getParent().getSqlController().getBasicRecipe(r.getOtherRecipeIds().get(i));
                other_r.setText(br.getName() + " View Recipe");
                other_r.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO: Open link to another recipe_toolbar.
                    }
                });
            }
        }

        for (int i = 0; i < r.getInstructions().size(); ++i) {
            expandInstructionList();

            LinearLayout parent_ll = (LinearLayout) getRootView().findViewById(R.id.recipe_instruction_list);

            TextView ins_desc = (TextView) parent_ll.getChildAt(parent_ll.getChildCount() - 1);
            ins_desc.setText("" + (i + 1) + ": " + r.getInstructions().get(i));
        }

        for (int i = 0; i < r.getComments().size(); ++i) {
            expandCommentList();

            LinearLayout parent_ll = (LinearLayout) getRootView().findViewById(R.id.recipe_comment_list);

            TextView cmnt_desc = (TextView) parent_ll.getChildAt(parent_ll.getChildCount() - 1);
            cmnt_desc.setText(r.getComments().get(i));
        }

        for (int i = 0; i < r.getTags().size(); ++i) {
            expandTagList();
            /*
            LinearLayout parent_ll = (LinearLayout) getRootView().findViewById(R.id.recipe_comment_list);

            TextView tag_desc = (TextView) parent_ll.getChildAt((parent_ll.getChildCount() - 1));
            tag_desc.setText(r.getTags().get(i));
            */
        }
    }

    private void expandIngredientList() {
        LinearLayout parent_ll = (LinearLayout) getRootView().findViewById(R.id.recipe_ingredient_list);

        RelativeLayout rl = ((RelativeLayout) getParent()
                        .getLayoutInflater()
                        .inflate(R.layout.recipe_item, null));

        RelativeLayout.LayoutParams rl_lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        parent_ll.addView(rl, rl_lp);
    }

    private void expandInstructionList() {
        LinearLayout parent_ll = (LinearLayout) getRootView().findViewById(R.id.recipe_instruction_list);

        TextView tv = new TextView(getParent());

        LinearLayout.LayoutParams tv_lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        parent_ll.addView(tv, tv_lp);
    }

    private void expandCommentList() {
        LinearLayout parent_ll = (LinearLayout) getRootView().findViewById(R.id.recipe_comment_list);

        TextView tv = new TextView(getParent());

        LinearLayout.LayoutParams tv_lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        parent_ll.addView(tv, tv_lp);
    }

    private void expandTagList() {
        //DO Nothing, tags are not supported.
    }

    private void updateIngredientScale(float user_target) {
        if (_recipe.getTargetQuantity() == 0) {
            _tgt_scale = 1.0f;
        }
        else {
            _tgt_scale = user_target / _recipe.getTargetQuantity();
        }
    }

    private void scaleIngredientQty() {

        LinearLayout parent_ll = (LinearLayout) getParent().findViewById(R.id.recipe_ingredient_list);

        for (int i = 0; i < parent_ll.getChildCount(); ++i) {
            RelativeLayout rl = (RelativeLayout) parent_ll.getChildAt(i);
            TextView tv = (TextView) rl.findViewById(R.id.recipe_list_text1);
            Float new_qty = _recipe.getIngredientQuantity().get(i) * _tgt_scale;
            tv.setText(new_qty.toString());
        }
    }

    private static String TAG = "RecipeFragment";

    private Float _tgt_scale = 1.0f;
    private Recipe _recipe = null;
}
