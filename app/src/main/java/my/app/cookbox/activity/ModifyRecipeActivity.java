package my.app.cookbox.activity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;
import java.util.zip.Inflater;

import my.app.cookbox.R;
import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.recipe.Recipe;
import my.app.cookbox.sqlite.SqlController;

/**
 * Created by Alexander on 015,  15 Apr.
 */

/* THIS ACTIVITY IS DEPRICATED
    Use ModifyRecipeFragment in stead.
 */
public class ModifyRecipeActivity extends AppCompatActivity {
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "ModifyRecipeActivity stared.");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.modify_recipe);

        long rid = getIntent().getLongExtra("id", Recipe.NO_ID);

        if (rid != Recipe.NO_ID) {
            Log.d(TAG, TAG + ".onCreate entered with an existing recipe_toolbar.");
            _recipe = _sqlctrl.getRecipe(rid);
        }
        else {
            Log.d(TAG, TAG + ".onCreate entered for new recipe_toolbar.");
            _recipe = new Recipe(rid);
        }

        _br_list = _sqlctrl.getAllBasicRecipes();

        _rl = (RelativeLayout) findViewById(R.id.modify_recipe_relative_layout);

        populateFields(_recipe);

        Button b_ing = (Button) findViewById(R.id.modify_recipe_button_ing);
        b_ing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandIngredientList();
            }
        });

        Button b_ins = (Button) findViewById(R.id.modify_recipe_button_ins);
        b_ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandInstructionList();
            }
        });

        Button b_cmnt = (Button) findViewById(R.id.modify_recipe_button_cmnt);
        b_cmnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandCommentList();
            }
        });

        Button b_tag = (Button) findViewById(R.id.modify_recipe_button_tag);
        b_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandTagList();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, TAG + ".onPause called.");

        updateSqlDatabase();

        Toast toast = Toast.makeText(this, "Recipe saved.",Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateSqlDatabase();
        outState.putLong("id", _recipe.getId());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        long id = savedInstanceState.getLong("id");
        _recipe = _sqlctrl.getRecipe(id);
        populateFields(_recipe);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar,menu);
        return true;
    }

    @Override
    public void finish() {
        updateSqlDatabase();
        Intent intent = new Intent();
        intent.putExtra("id", _recipe.getId());

        setResult(RESULT_OK, intent);
        super.finish();
    }

    private void updateSqlDatabase() {
        _recipe = readRecipe(_recipe.getId());
        _recipe.setId(_sqlctrl.insertRecipe(_recipe));
    }

    private Recipe readRecipe(long id) {
        String name = ((EditText) findViewById(R.id.modify_recipe_edit_text_name)).getText().toString();
        String long_desc = ((EditText) findViewById(R.id.modify_recipe_edit_text_l_desc)).getText().toString();
        String short_desc = ((EditText) findViewById(R.id.modify_recipe_edit_text_s_desc)).getText().toString();
        String tgt_desc = ((EditText) findViewById(R.id.modify_recipe_edit_text_desc_tgt)).getText().toString();
        float tgt_qty = Float.parseFloat(((EditText)
                    findViewById(R.id.modify_recipe_edit_text_qty_tgt)).getText().toString());

        ArrayList<String> ing_list = new ArrayList<>();
        ArrayList<Float> ing_qty_list = new ArrayList<>();
        ArrayList<Long> ing_other_rec = new ArrayList<>();

        LinearLayout ing_ll = (LinearLayout) findViewById(R.id.modify_recipe_ingredient_view);
        for (int i = 0; i < ing_ll.getChildCount(); ++i) {
            LinearLayout ll = (LinearLayout) ing_ll.getChildAt(i);
            String qty = ((EditText) ll.getChildAt(0)).getText().toString();
            try {
                ing_qty_list.add(Float.parseFloat(qty));
            }
            catch (Exception e) {
                ing_qty_list.add((float) -1);
            }
            ing_list.add(((EditText) ll.getChildAt(1)).getText().toString());
            Spinner sp = (Spinner) ll.getChildAt(2);
            if (sp.getSelectedItemPosition() != 0) {
                ing_other_rec.add(_br_list.get(sp.getSelectedItemPosition() - 1).getId());
            }
            else {
                ing_other_rec.add(Recipe.NO_ID);
            }
        }

        ArrayList<String> ins_list = new ArrayList<>();

        LinearLayout ins_ll = (LinearLayout) findViewById(R.id.modify_recipe_instruction_view);
        for (int i = 0; i < ins_ll.getChildCount(); ++i) {
            LinearLayout ll = (LinearLayout) ins_ll.getChildAt(i);
            TextView desc_tv = (TextView) ll.getChildAt(0);

            ins_list.add(desc_tv.getText().toString());
        }

        ArrayList<String> tag_list = new ArrayList<>();

        LinearLayout tag_ll = (LinearLayout) findViewById(R.id.modify_recipe_tag_view);
        for (int i = 0; i < tag_ll.getChildCount(); ++i) {
            LinearLayout ll = (LinearLayout) tag_ll.getChildAt(i);
            TextView tag_tv = (TextView) ll.getChildAt(0);

            tag_list.add(tag_tv.getText().toString());
        }

        ArrayList<String> cmnt_list = new ArrayList<>();

        LinearLayout comment_ll = (LinearLayout) findViewById(R.id.modify_recipe_comment_view);
        for (int i = 0; i < comment_ll.getChildCount(); ++i) {
            LinearLayout ll = (LinearLayout) comment_ll.getChildAt(i);
            TextView cmnt_tv = (TextView) ll.getChildAt(0);

            cmnt_list.add(cmnt_tv.getText().toString());
        }

        return new Recipe(id, name, short_desc, long_desc, tgt_desc, tgt_qty, ing_qty_list,
                ing_list, ing_other_rec,ins_list,tag_list,cmnt_list);
    }

    private void populateFields(Recipe r) {
        EditText name = (EditText) findViewById(R.id.modify_recipe_edit_text_name);
        name.setText(r.getName());

        EditText short_desc = (EditText) findViewById(R.id.modify_recipe_edit_text_s_desc);
        short_desc.setText(r.getShortDescription());

        EditText long_desc = (EditText) findViewById(R.id.modify_recipe_edit_text_l_desc);
        long_desc.setText(r.getLongDescription());

        EditText tgt_desc = (EditText) findViewById(R.id.modify_recipe_edit_text_desc_tgt);
        tgt_desc.setText(r.getTargetDescription());

        EditText target_qty = (EditText) findViewById(R.id.modify_recipe_edit_text_qty_tgt);
        target_qty.setText(r.getTargetQuantity().toString());

        for (int i = 0; i < r.getIngredientDescriptions().size(); ++i) {
            expandIngredientList();
            LinearLayout parent_ll = (LinearLayout) findViewById(R.id.modify_recipe_ingredient_view);
            LinearLayout ll = (LinearLayout) parent_ll.getChildAt(parent_ll.getChildCount() - 1);

            EditText ing_qty = (EditText) ll.getChildAt(0);
            ing_qty.setText(r.getIngredientQuantity().get(i).toString());

            EditText ing_desc = (EditText) ll.getChildAt(1);
            ing_desc.setText(r.getIngredientDescriptions().get(i));

            Spinner other_r_sp = (Spinner) ll.getChildAt(2);
            long other_r_id = r.getOtherRecipeIds().get(i);
            if (other_r_id == Recipe.NO_ID) {
                other_r_sp.setSelection(0);
            }
            else {
                int br_pos = 0;
                for (; br_pos < _br_list.size(); ++br_pos) {
                    if (_br_list.get(br_pos).getId() == other_r_id) {
                        break;
                    }
                }
                if (other_r_id == _br_list.get(br_pos).getId()) {
                    other_r_sp.setSelection(br_pos + 1);
                }
                else {
                    other_r_sp.setSelection(0);
                }
            }
        }

        for (int i = 0; i < r.getInstructions().size(); ++i) {
            expandInstructionList();
            LinearLayout parent_ll = (LinearLayout) findViewById(R.id.modify_recipe_instruction_view);
            LinearLayout ll = (LinearLayout) parent_ll.getChildAt(parent_ll.getChildCount() - 1);

            EditText ins_desc = (EditText) ll.getChildAt(0);
            ins_desc.setText(r.getInstructions().get(i));
        }

        for (int i = 0; i < r.getTags().size(); ++i) {
            expandTagList();
            LinearLayout parent_ll = (LinearLayout) findViewById(R.id.modify_recipe_tag_view);
            LinearLayout ll = (LinearLayout) parent_ll.getChildAt(parent_ll.getChildCount() - 1);

            EditText tag_desc = (EditText) ll.getChildAt(0);
            tag_desc.setText(r.getTags().get(i));
        }

        for (int i = 0; i < r.getComments().size(); ++i) {
            expandCommentList();
            LinearLayout parent_ll = (LinearLayout) findViewById(R.id.modify_recipe_instruction_view);
            LinearLayout ll = (LinearLayout) parent_ll.getChildAt(parent_ll.getChildCount() - 1);

            EditText cmnt_desc = (EditText) ll.getChildAt(0);
            cmnt_desc.setText(r.getComments().get(i));
        }
    }

    private void setupOtherRecipeSpinner(Spinner spinner) {
        ArrayList<String> names = new ArrayList<>();
        names.add("None");
        for (BasicRecipe br : _br_list) {
            names.add(br.getName());
        }
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.modify_recipe_spinner_item, names);//R.layout.modify_recipe_spinner_item,names);
        spinner.setAdapter(aa);
    }

    private void expandIngredientList() {
        LinearLayout ll = (LinearLayout) ((LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.modify_recipe_item,null);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        //ll.setLayoutParams(ll_lp);
        setupOtherRecipeSpinner((Spinner) ll.getChildAt(2));

        ((LinearLayout) findViewById(R.id.modify_recipe_ingredient_view)).addView(ll,ll_lp);

        Button del_b = (Button) findViewById(R.id.modify_recipe_button_ing_del);
        del_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout parent_ll = (LinearLayout) findViewById(R.id.modify_recipe_ingredient_view);
                if (parent_ll.getChildCount() == 0) {
                    return;
                }
                LinearLayout ll = (LinearLayout) parent_ll.getChildAt(parent_ll.getChildCount()-1);
                parent_ll.removeView(ll);
            }
        });
    }

    private void expandInstructionList() {
        LinearLayout ll = (LinearLayout) ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.modify_recipe_item,null);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ll.setLayoutParams(ll_lp);
        //ll weight sum is 20 by default, see res/layout/modify_recipe_list_item_layout.xml
        //Don't need the quantity EditText nor the other recipe_toolbar spinner
        ll.removeView(ll.getChildAt(2));
        ll.removeView(ll.getChildAt(0));
        ll.setWeightSum(5);

        ((LinearLayout) findViewById(R.id.modify_recipe_instruction_view)).addView(ll);

        Button del_b = (Button) findViewById(R.id.modify_recipe_button_ins_del);
        del_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               LinearLayout parent = (LinearLayout) findViewById(R.id.modify_recipe_instruction_view);
                popViewFromLinearLayout(parent);
            }
        });
    }

    private void expandTagList() {
         LinearLayout ll = (LinearLayout) ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.modify_recipe_item,null);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ll.setLayoutParams(ll_lp);
        //ll weight sum is 20 by default, see res/layout/modify_recipe_list_item_layout.xml
        //Don't need the quantity EditText nor the other recipe_toolbar spinner
        ll.removeView(ll.getChildAt(2));
        ll.removeView(ll.getChildAt(0));
        ll.setWeightSum(5);

        ((LinearLayout) findViewById(R.id.modify_recipe_tag_view)).addView(ll);

        Button del_b = (Button) findViewById(R.id.modify_recipe_button_tag_del);
        del_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               LinearLayout parent = (LinearLayout) findViewById(R.id.modify_recipe_tag_view);
                popViewFromLinearLayout(parent);
            }
        });
    }

    private void expandCommentList() {
        LinearLayout ll = (LinearLayout) ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.modify_recipe_item,null);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ll.setLayoutParams(ll_lp);
        //ll weight sum is 20 by default, see res/layout/modify_recipe_list_item_layout.xml
        //Don't need the quantity EditText nor the other recipe_toolbar spinner
        ll.removeView(ll.getChildAt(2));
        ll.removeView(ll.getChildAt(0));
        ll.setWeightSum(5);

        ((LinearLayout) findViewById(R.id.modify_recipe_comment_view)).addView(ll);

        Button del_b = (Button) findViewById(R.id.modify_recipe_button_cmnt_del);
        del_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               LinearLayout parent = (LinearLayout) findViewById(R.id.modify_recipe_comment_view);
                popViewFromLinearLayout(parent);
            }
        });
    }

    private void popViewFromLinearLayout(LinearLayout parent) {
            if (parent.getChildCount() == 0) {
                return;
            }
            LinearLayout ll = (LinearLayout) parent.getChildAt(parent.getChildCount()-1);
            parent.removeView(ll);
    }

    private static String TAG = "ModifyRecipeActivity";

    private Recipe _recipe = new Recipe();
    private ArrayList<BasicRecipe> _br_list;
    private SqlController _sqlctrl = new SqlController(this);
    private RelativeLayout _rl = null;
    */
}
