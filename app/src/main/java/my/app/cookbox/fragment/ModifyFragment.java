package my.app.cookbox.fragment;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import my.app.cookbox.activity.MainActivity;
import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.sqlite.RecipeProvider;

/**
 * Created by Alexander on 015, 15 Jun.
 */

public class ModifyFragment extends BaseFragment {

    @Override
    public void onStart() {
        super.onStart();
        getParent().getSupportActionBar().setTitle("Edit recipe");

        populateFields(_brecipe);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        _root_view = inflater.inflate(R.layout.modify_recipe, container, false);

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

        return _root_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _parent = (MainActivity) getActivity();

        Bundle args = getArguments();
        if (args != null && args.getLong("id", -1) != -1) {
            long id = args.getLong("id");
            Cursor recipe = getContext()
                    .getContentResolver()
                    .query(RecipeProvider.recipe_uri,
                            new String[] {"name", "short_description","long_description","target_quantity", "target_description"},
                            "id = ?",
                            new String[] {Long.toString(id)},
                            null
                    );
            if (recipe != null && recipe.moveToFirst()) {
                _brecipe.id = id;
                _brecipe.name = recipe.getString(recipe.getColumnIndex("name"));
                _brecipe.short_desc = recipe.getString(recipe.getColumnIndex("short_description"));
                _brecipe.long_desc = recipe.getString(recipe.getColumnIndex("long_description"));
                _brecipe.target_quantity = recipe.getFloat(recipe.getColumnIndex("target_quantity"));
                _brecipe.target_description = recipe.getString(recipe.getColumnIndex("target_description"));
            }
            else {
                // no such recipe => finish
                getFragmentManager().popBackStack();
            }
            recipe.close();

            Cursor ingredients = getContext()
                    .getContentResolver()
                    .query(
                            RecipeProvider.ingredient_uri,
                            new String[] {"quantity", "description", "other_recipe"},
                            "recipe_id = ?",
                            new String[] {Long.toString(_brecipe.id)},
                            null);
            if (ingredients != null && ingredients.moveToFirst()) {
                do {
                    BasicRecipe.RecipeIngredient ingredient = new BasicRecipe.RecipeIngredient();
                    ingredient.quantity = ingredients.getDouble(ingredients.getColumnIndex("quantity"));
                    ingredient.description = ingredients.getString(ingredients.getColumnIndex("description"));
                    ingredient.other_recipe_id
                            = ingredients.isNull(ingredients.getColumnIndex("other_recipe")) ?
                                ingredients.getLong(ingredients.getColumnIndex("other_recipe")) : null;

                    _brecipe.ingredients.add(ingredient);
                } while (ingredients.moveToNext());
            }
            ingredients.close();

            Cursor instructions = getContext()
                    .getContentResolver()
                    .query(
                            RecipeProvider.instruction_uri,
                            new String[] {"instruction"},
                            "recipe_id = ?",
                            new String[] {Long.toString(_brecipe.id)},
                            "position ASC");
            if (instructions != null && instructions.moveToFirst()) {
                do {
                    String instruction = instructions.getString(instructions.getColumnIndex("instruction"));

                    _brecipe.instructions.add(instruction);
                } while (ingredients.moveToNext());
            }
            instructions.close();

            Cursor comments = getContext()
                    .getContentResolver()
                    .query(
                            RecipeProvider.comment_uri,
                            new String[] {"comment"},
                            "recipe_id = ?",
                            new String[] {Long.toString(_brecipe.id)},
                            null);
            if (comments != null && comments.moveToFirst()) {
                do {
                    String comment = comments.getString(comments.getColumnIndex("comment"));

                    _brecipe.comments.add(comment);
                } while (comments.moveToNext());
            }
            comments.close();
        }
        else {
            _brecipe = new BasicRecipe();
        }

        setHasOptionsMenu(true);

        setRetainInstance(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, TAG + ".onPause called.");

        updateRecipe();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_recipe_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_recipe_save :
                updateRecipe();
                saveRecipe();
                Toast toast = Toast.makeText(getContext(), "Recipe was saved.",Toast.LENGTH_SHORT);
                toast.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Updates _brecipe with data from the user
    private void updateRecipe() {
        _brecipe = readRecipe(_brecipe.id);
    }

    //  write _brecipe to RecipeProvider
    private void saveRecipe() {
        ContentValues recipe = new ContentValues();
        recipe.put("name", _brecipe.name);
        recipe.put("short_description", _brecipe.short_desc);
        recipe.put("long_description", _brecipe.long_desc);
        recipe.put("target_quantity", _brecipe.target_quantity);
        recipe.put("target_description", _brecipe.target_description);

        /* update */
        if (_brecipe.id != null) {
            recipe.put("id",_brecipe.id);
            getContext()
                .getContentResolver()
                .update(RecipeProvider.recipe_uri,
                        recipe,
                        "id = ?",
                        new String[] {_brecipe.id.toString()});
        }
        /* insert */
        else {
            long id = ContentUris.parseId(getContext()
                .getContentResolver()
                .insert(RecipeProvider.recipe_uri, recipe));
            _brecipe.id = id;
        }

        getContext().getContentResolver()
                .delete(RecipeProvider.ingredient_uri,
                        "recipe_id = ?",
                        new String[] {_brecipe.id.toString()});

        for (BasicRecipe.RecipeIngredient ing : _brecipe.ingredients) {
            ContentValues ingredient = new ContentValues();

            ingredient.put("quantity", ing.quantity);
            ingredient.put("description", ing.description);
            ingredient.put("other_recipe", ing.other_recipe_id);
            ingredient.put("recipe_id", _brecipe.id);

            getContext().getContentResolver().insert(RecipeProvider.ingredient_uri,ingredient);
        }

        getContext().getContentResolver()
                .delete(RecipeProvider.instruction_uri,
                        "recipe_id = ?",
                        new String[] {_brecipe.id.toString()});

        for (int i = 0; i < _brecipe.instructions.size(); ++i)  {
            ContentValues instruction = new ContentValues();
            instruction.put("instruction", _brecipe.instructions.get(i));
            instruction.put("position", i);
            instruction.put("recipe_id",_brecipe.id);

            getContext()
                    .getContentResolver()
                    .insert(RecipeProvider.instruction_uri,instruction);
        }

        getContext().getContentResolver()
                .delete(RecipeProvider.comment_uri,
                        "recipe_id = ?",
                        new String[] {_brecipe.id.toString()});

        for (String com : _brecipe.comments) {
            ContentValues comment = new ContentValues();
            comment.put("comment", com);
            comment.put("recipe_id", _brecipe.id);

            getContext()
                    .getContentResolver()
                    .insert(RecipeProvider.comment_uri, comment);
        }
    }

    // Read recipe from data provided by the user
    private BasicRecipe readRecipe(Long id) {
        BasicRecipe ret = new BasicRecipe();

        ret.id = id;
        ret.name = getEditTextFromId(R.id.modify_edit_text_name, _root_view).getText().toString();
        ret.short_desc = getEditTextFromId(R.id.modify_edit_text_short_desc, _root_view).getText().toString();
        ret.long_desc = getEditTextFromId(R.id.modify_edit_text_long_desc, _root_view).getText().toString();
        ret.target_description = getEditTextFromId(R.id.modify_edit_text_tgt_desc, _root_view).getText().toString();
        ret.target_quantity = Float.parseFloat(getEditTextFromId(R.id.modify_edit_text_tgt_qty, _root_view).getText().toString());

        LinearLayout ing_ll = (LinearLayout) _root_view.findViewById(R.id.modify_ingredient_list);
        for (int i = 0; i < ing_ll.getChildCount(); ++i) {
            BasicRecipe.RecipeIngredient ingredient = new BasicRecipe.RecipeIngredient();

            RelativeLayout rl = (RelativeLayout) ing_ll.getChildAt(i);
            String qty = getEditTextFromId(R.id.modify_list_item_edit_text1, rl).getText().toString();
            try {
                ingredient.quantity = Double.parseDouble(qty);
            }
            catch (Exception e) {
                ingredient.quantity = 1d;
            }
            ingredient.description = getEditTextFromId(R.id.modify_list_item_edit_text2, rl).getText().toString();
            Spinner sp = (Spinner) rl.findViewById(R.id.modify_list_item_spinner);
            if (sp.getSelectedItemPosition() != 0) {
                Cursor other_r = getContext()
                        .getContentResolver()
                        .query(RecipeProvider.recipe_uri,
                            new String[] {"id"},
                            null,
                            null,
                            null);
                int pos = sp.getSelectedItemPosition() - 1;
                if (other_r.getCount() < pos) {
                    ingredient.other_recipe_id = null;
                }
                other_r.moveToPosition(pos);
                ingredient.other_recipe_id = other_r.getLong(other_r.getColumnIndex("id"));
                other_r.close();
            }
            else {
                ingredient.other_recipe_id = null;
            }
            ret.ingredients.add(ingredient);
        }

        LinearLayout ins_ll = (LinearLayout) _root_view.findViewById(R.id.modify_instruction_list);
        for (int i = 0; i < ins_ll.getChildCount(); ++i) {
            RelativeLayout rl = (RelativeLayout) ins_ll.getChildAt(i);
            TextView desc_tv = ((TextInputLayout) rl.getChildAt(0)).getEditText();

            ret.instructions.add(desc_tv.getText().toString());
        }

        LinearLayout comment_ll = (LinearLayout) _root_view.findViewById(R.id.modify_comment_list);
        for (int i = 0; i < comment_ll.getChildCount(); ++i) {
            RelativeLayout rl = (RelativeLayout) comment_ll.getChildAt(i);
            TextView cmnt_tv = ((TextInputLayout) rl.getChildAt(0)).getEditText();

            ret.comments.add(cmnt_tv.getText().toString());
        }

        // tags dont' work form here
        /*
        LinearLayout tag_ll = (LinearLayout) _root_view.findViewById(R.id.modify_tag_list);
        for (int i = 0; i < tag_ll.getChildCount(); ++i) {
            RelativeLayout rl = (RelativeLayout) tag_ll.getChildAt(i);
            TextView tag_tv = ((TextInputLayout) rl.getChildAt(0)).getEditText();

            tag_list.add(tag_tv.getText().toString());
        }
        */

        return ret;
    }

    private void populateFields(BasicRecipe r) {
        EditText name = getEditTextFromId(R.id.modify_edit_text_name, _root_view);
        name.setText(r.name == null ? "" : r.name);

        EditText short_desc = getEditTextFromId(R.id.modify_edit_text_short_desc, _root_view);
        short_desc.setText(r.short_desc == null ? "" : r.short_desc);

        EditText long_desc = getEditTextFromId(R.id.modify_edit_text_long_desc, _root_view);
        long_desc.setText(r.long_desc == null ? "" : r.long_desc);

        EditText tgt_desc = getEditTextFromId(R.id.modify_edit_text_tgt_desc, _root_view);
        tgt_desc.setText(r.target_description == null ? "" : r.target_description);

        EditText target_qty = getEditTextFromId(R.id.modify_edit_text_tgt_qty, _root_view);
        if (r.target_quantity <= 0) {
            target_qty.setText("1");
        }
        else {
            target_qty.setText(Double.toString(r.target_quantity));
        }

        for (int i = 0; i < r.ingredients.size(); ++i) {
            expandIngredientList();
            LinearLayout parent_ll = (LinearLayout) _root_view.findViewById(R.id.modify_ingredient_list);
            RelativeLayout rl = (RelativeLayout) parent_ll.getChildAt(parent_ll.getChildCount() - 1);

            EditText ing_qty = getEditTextFromId(R.id.modify_list_item_edit_text1, rl);
            ing_qty.setText(r.ingredients.get(i).quantity.toString());

            EditText ing_desc = getEditTextFromId(R.id.modify_list_item_edit_text2, rl);
            ing_desc.setText(r.ingredients.get(i).description);

            Spinner other_r_sp = (Spinner) rl.findViewById(R.id.modify_list_item_spinner);
            Long other_r_id = r.ingredients.get(i).other_recipe_id;
            if (other_r_id == null) {
                other_r_sp.setSelection(0);
            }
            else {
                Cursor other_r = getContext()
                        .getContentResolver()
                        .query(RecipeProvider.recipe_uri,
                                new String[] {"id"},
                                null,
                                null,
                                null);
                if (other_r.moveToFirst()) {
                    int br_pos = 0;
                    do {
                        if (br_pos < other_r.getCount() ||
                                other_r.getLong(other_r.getColumnIndex("id")) == r.ingredients.get(i).other_recipe_id) {
                            break;
                        }
                    } while (other_r.moveToNext());
                    if (other_r.getLong(other_r.getColumnIndex("id")) == r.ingredients.get(i).other_recipe_id) {
                        other_r_sp.setSelection(br_pos + 1);
                    } else {
                        other_r_sp.setSelection(0);
                    }
                    other_r.close();
                }
            }
        }

        for (int i = 0; i < r.instructions.size(); ++i) {
            expandInstructionList();
            LinearLayout parent_ll = (LinearLayout) _root_view.findViewById(R.id.modify_instruction_list);
            RelativeLayout ll = (RelativeLayout) parent_ll.getChildAt(parent_ll.getChildCount() - 1);

            EditText ins_desc = getEditTextFromId(R.id.modify_list_item_edit_text2, ll);
            ins_desc.setText(r.instructions.get(i));
        }

        for (int i = 0; i < r.comments.size(); ++i) {
            expandCommentList();
            LinearLayout parent_ll = (LinearLayout) _root_view.findViewById(R.id.modify_comment_list);
            RelativeLayout ll = (RelativeLayout) parent_ll.getChildAt(parent_ll.getChildCount() - 1);

            EditText cmnt_desc = getEditTextFromId(R.id.modify_list_item_edit_text2, ll);
            cmnt_desc.setText(r.comments.get(i));
        }

        for (int i = 0; i < r.tags.size(); ++i) {
            /* tags not implemented with ContentProvier (yet)
            expandTagList();

            LinearLayout parent_ll = (LinearLayout) _root_view.findViewById(R.id.modify_tag_list);
            RelativeLayout ll = (RelativeLayout) parent_ll.getChildAt(parent_ll.getChildCount() - 1);

            EditText tag_desc = getEditTextFromId(R.id.modify_list_item_edit_text2, ll);
            tag_desc.setText(r.tags.get(i));
            */
        }


    }

    private void setupOtherRecipeSpinner(Spinner spinner) {
        ArrayList<String> names = new ArrayList<>();
        names.add("None");
        Cursor r_names = getContext()
                .getContentResolver()
                .query(RecipeProvider.recipe_uri,
                        new String[] {"name"},
                        null,
                        null,
                        null);
        if (r_names.moveToFirst()) {
            do {
                names.add(r_names.getString(r_names.getColumnIndex("name")));
            } while (r_names.moveToNext());
        }
        r_names.close();
        //modify_recipe_spinner_item will do just fine here
        ArrayAdapter<String> aa = new ArrayAdapter<>(getContext(), R.layout.modify_recipe_spinner_item,names);
        spinner.setAdapter(aa);
    }

    private void expandIngredientList() {
        RelativeLayout rl
                = ((RelativeLayout) getActivity()
                        .getLayoutInflater()
                        .inflate(R.layout.modify_recipe_item, null));
        getEditTextFromId(R.id.modify_list_item_edit_text2, rl).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
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
                        .inflate(R.layout.modify_recipe_item, null));
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
                        .inflate(R.layout.modify_recipe_item, null));
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
        RelativeLayout rl
                = ((RelativeLayout) getActivity()
                        .getLayoutInflater()
                        .inflate(R.layout.modify_recipe_item, null));
        rl.removeViewAt(3);//remove spinner
        rl.removeViewAt(0);//remove quantity

        rl.findViewById(R.id.modify_list_item_edit_text2).setEnabled(false);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        final LinearLayout parent_ll
                = (LinearLayout) _root_view.findViewById(R.id.modify_tag_list);
        parent_ll.addView(rl,lp);
        ImageButton b = (ImageButton) rl.findViewById(R.id.modify_list_item_del_button1);
        b.setOnClickListener(_onClickListener);
    }

    private static EditText getEditTextFromId(int view_id, View parent) {
            return ((TextInputLayout) parent.findViewById(view_id)).getEditText();
    }

    private MainActivity _parent = null;
    private View _root_view = null;
    private BasicRecipe _brecipe = new BasicRecipe();

    /* Remove item from list onClickListener */
    private static final View.OnClickListener _onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((ViewGroup) v.getParent().getParent()).removeView((ViewGroup) v.getParent());
        }
    };

    private static String TAG = "ModifyFragment";
}
