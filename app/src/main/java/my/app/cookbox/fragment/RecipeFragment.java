package my.app.cookbox.fragment;

import android.database.Cursor;
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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import my.app.cookbox.R;
import my.app.cookbox.recipe.Recipe;
import my.app.cookbox.sqlite.RecipeProvider;

/**
 * Created by Alexander on 011, 11 Jul.
 */

public class RecipeFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstance) {
        Log.v(TAG, "RecipeFragment started.");

        super.onCreate(savedInstance);

        /* init recipe id */
        Bundle args = getArguments();
        final Long default_value = -1l;
        _recipe_id = getArguments() != null ? getArguments().getLong("id",default_value) : default_value;
        if (_recipe_id == default_value) {
             Toast toast = Toast.makeText(getParent(), "Undefined recipe.", Toast.LENGTH_SHORT);
            toast.show();
            getParent().getFragmentManager().popBackStack();
        }

        setHasOptionsMenu(true);

        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        getParent().getSupportActionBar().setTitle("Recipe");

        clearFields();

        populateFields();

        /* set on target quantity edit listener */
        EditText target_qty_et = (EditText) getRootView().findViewById(R.id.recipe_text_qty_tgt);
        target_qty_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    scaleIngredientQty(Float.parseFloat(v.getText().toString()));
                    return true;
                }
                return false;
            }
        });

        getRootView().setKeepScreenOn(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        getRootView().setKeepScreenOn(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setRootView(inflater.inflate(R.layout.recipe, container, false));

        return getRootView();
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
                getParent().startModifyFragment(_recipe_id);
                return true;
            default:
                return false;
        }
    }

    private void clearFields() {
        TextView name = (TextView) getRootView().findViewById(R.id.recipe_text_name);
        name.setText("");

        TextView long_desc = (TextView) getRootView().findViewById(R.id.recipe_text_desc);
        long_desc.setText("");

        TextView target_qty = (TextView) getRootView().findViewById(R.id.recipe_text_qty_tgt);
        target_qty.setText("");

        TextView target_desc = (TextView) getRootView().findViewById(R.id.recipe_text_desc_tgt);
        target_desc.setText("");

        LinearLayout ingredient_ll = (LinearLayout) getRootView().findViewById(R.id.recipe_ingredient_list);
        ingredient_ll.removeAllViews();

        LinearLayout instruction_ll = (LinearLayout) getRootView().findViewById(R.id.recipe_instruction_list);
        instruction_ll.removeAllViews();

        LinearLayout comment_ll = (LinearLayout) getRootView().findViewById(R.id.recipe_comment_list);
        comment_ll.removeAllViews();

        //Tag handle tag list
    }

    private void populateFields() {
        Cursor recipe = getContext()
                .getContentResolver()
                .query(RecipeProvider.recipe_uri,
                        null,
                        "id = ?",
                        new String[] {Long.toString(_recipe_id)},
                        null
                );
        if (!recipe.moveToFirst() || recipe.getColumnCount() != 1) {
            //TODO: abord
        }
        TextView name = (TextView) getRootView().findViewById(R.id.recipe_text_name);
        name.setText(recipe.getString(recipe.getColumnIndex("name")));

        TextView long_desc = (TextView) getRootView().findViewById(R.id.recipe_text_desc);
        long_desc.setText(recipe.getString(recipe.getColumnIndex("long_description")));

        if (_tgt_scale == null) {
            _tgt_scale = recipe.getFloat(recipe.getColumnIndex("target_quantity"));
        }
        TextView target_qty = (TextView) getRootView().findViewById(R.id.recipe_text_qty_tgt);
        target_qty.setText(_tgt_scale.toString());

        TextView target_desc = (TextView) getRootView().findViewById(R.id.recipe_text_desc_tgt);
        target_desc.setText(recipe.getString(recipe.getColumnIndex("target_description")));

        recipe.close();

        Cursor recipe_ingredients = getContext()
                .getContentResolver()
                .query(RecipeProvider.ingredient_uri,
                        null,
                        "recipe_id = ?",
                        new String[] {Long.toString(_recipe_id)},
                        null
                );
        if ( recipe_ingredients.moveToFirst()) {
            do {
                expandIngredientList();

                LinearLayout parent_ll = (LinearLayout) getRootView().findViewById(R.id.recipe_ingredient_list);

                RelativeLayout ing_item = (RelativeLayout) parent_ll.getChildAt(parent_ll.getChildCount() - 1);

                TextView ing_qty = (TextView) ing_item.getChildAt(0);
                ing_qty.setText(Float.toString(recipe_ingredients.getFloat(recipe_ingredients.getColumnIndex("quantity"))));

                TextView ing_desc = (TextView) ing_item.getChildAt(1);
                ing_desc.setText(recipe_ingredients.getString(recipe_ingredients.getColumnIndex("description")));

                TextView other_r = (TextView) ing_item.getChildAt(2);
                if (!recipe_ingredients.isNull(recipe_ingredients.getColumnIndex("other_recipe"))) {
                    Cursor other_recipe = getContext()
                            .getContentResolver()
                            .query(RecipeProvider.recipe_uri,
                                    new String[]{"name"},
                                    "id = ?",
                                    new String[]{Long.toString(_recipe_id)},
                                    null
                            );
                    if (!other_recipe.moveToFirst()) {
                        //TODO
                    }
                    other_r.setText(other_recipe.getString(other_recipe.getColumnIndex("name")) + " View Recipe");
                    other_r.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO: Open link to another recipe_toolbar.
                        }
                    });
                }
            } while (recipe_ingredients.moveToNext());
        }
        recipe_ingredients.close();

        Cursor instruction = getContext()
                .getContentResolver()
                .query(RecipeProvider.instruction_uri,
                        new String[] {"instruction"},
                        "recipe_id = ?",
                        new String[] {Long.toString(_recipe_id)},
                        "position ASC"
                );
        if ( instruction.moveToFirst()) {
            int i = 0;
            do {
                ++i;
                expandInstructionList();

                LinearLayout parent_ll = (LinearLayout) getRootView().findViewById(R.id.recipe_instruction_list);

                TextView ins_desc = (TextView) parent_ll.getChildAt(parent_ll.getChildCount() - 1);
                ins_desc.setText("" + (i + 1) + ": " + instruction.getString(instruction.getColumnIndex("instruction")));
            } while (instruction.moveToNext());
        }
        instruction.close();

        Cursor comment = getContext()
                .getContentResolver()
                .query(RecipeProvider.comment_uri,
                        new String[] {"comment"},
                        "recipe_id = ?",
                        new String[] {Long.toString(_recipe_id)},
                        null
                );
        if (comment.moveToFirst()) {
            do {
                expandCommentList();

                LinearLayout parent_ll = (LinearLayout) getRootView().findViewById(R.id.recipe_comment_list);

                TextView cmnt_desc = (TextView) parent_ll.getChildAt(parent_ll.getChildCount() - 1);
                cmnt_desc.setText(comment.getString(comment.getColumnIndex("comment")));
            } while (comment.moveToNext());
        }
        comment.close();

        // not done.. for now
        /*
        for (int i = 0; i < r.getTags().size(); ++i) {
            expandTagList();
            LinearLayout parent_ll = (LinearLayout) getRootView().findViewById(R.id.recipe_comment_list);

            TextView tag_desc = (TextView) parent_ll.getChildAt((parent_ll.getChildCount() - 1));
            tag_desc.setText(r.getTags().get(i));
        }
        */
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

    private void scaleIngredientQty(Float target_quantity) {
        _tgt_scale = target_quantity;

        Cursor default_quantity_c = getContext()
                .getContentResolver()
                .query(RecipeProvider.recipe_uri,
                        new String[] {"target_quantity"},
                        "id = ?",
                        new String[] {_recipe_id.toString()},
                        null
                );
        Float default_quantity;
        if (default_quantity_c.moveToFirst()) {
            default_quantity
                    = default_quantity_c
                    .getFloat(default_quantity_c.getColumnIndex("target_quantity"));
        }
        else {
            default_quantity = target_quantity;
        }
        default_quantity_c.close();

        Cursor ingredient_qty_c = getContext()
                .getContentResolver()
                .query(RecipeProvider.ingredient_uri,
                        new String[] {"quantity"},
                        "recipe_id = ?",
                        new String[] {_recipe_id.toString()},
                        null
                );
        if (ingredient_qty_c.moveToFirst()) {
            LinearLayout parent_ll = (LinearLayout) getParent().findViewById(R.id.recipe_ingredient_list);
            for (int i = 0; i < parent_ll.getChildCount(); ++i) {
                Float new_qty = ingredient_qty_c.getFloat(ingredient_qty_c.getColumnIndex("quantity")) * target_quantity / default_quantity;

                RelativeLayout rl = (RelativeLayout) parent_ll.getChildAt(i);
                TextView tv = (TextView) rl.findViewById(R.id.recipe_list_text1);
                tv.setText(new_qty.toString());

                if (!ingredient_qty_c.moveToNext()) {
                    break;
                }
            }
        }
        ingredient_qty_c.close();
    }


    private Long _recipe_id = null;
    private Float _tgt_scale = null;

    private static String TAG = "RecipeFragment";
}
