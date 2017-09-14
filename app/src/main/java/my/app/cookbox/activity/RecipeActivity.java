package my.app.cookbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import my.app.cookbox.R;
import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.recipe.Recipe;
import my.app.cookbox.sqlite.SqlController;

/**
 * Created by Alexander on 015,  15 Apr.
 */

public class RecipeActivity extends AppCompatActivity {
/*
    @Override
    public void onCreate(Bundle savedInstance) {
        Log.d(TAG, "RecipeActivity started.");

        super.onCreate(savedInstance);
        setContentView(R.layout.recipe);

        _rl = (RelativeLayout) findViewById(R.id.recipe_relative_layout);


        Intent intent = getIntent();
        long rid = intent.getLongExtra("id", Recipe.NO_ID);
        if (rid != Recipe.NO_ID) {
            _recipe = _sqlctrl.getRecipe(rid);

            populateFields(_recipe);
        }
        else {
            Toast toast = Toast.makeText(this, "Undefined recipe_toolbar.", Toast.LENGTH_SHORT);
            toast.show();

            finish();
        }

        //Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

       EditText target_qty_et = (EditText) findViewById(R.id.recipe_text_qty_tgt);
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
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onActivityResult(int request_code, int result_code, Intent intent) {
        Log.v(TAG, TAG + ".onActivityResult called.");

        if (request_code == PROMPT_FOR_NEW_RECIPE_REQUEST_CODE && result_code == RESULT_OK) {
            Log.v(TAG, TAG + ".onActivityResult with RESULT_OK called.");

            long id = intent.getLongExtra("id", Recipe.NO_ID);
            _recipe = _sqlctrl.getRecipe(id);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recipe_edit:
                Intent intent = new Intent(this, ModifyRecipeActivity.class);
                intent.putExtra("id", _recipe.getId());
                startActivityForResult(intent,  0);//don't care for result
                return true;
            default:
                return false;
        }
    }


    private void populateFields(final Recipe r) {
        TextView name = (TextView) findViewById(R.id.recipe_text_name);
        name.setText(r.getName());

        TextView long_desc = (TextView) findViewById(R.id.recipe_text_desc);
        long_desc.setText(r.getLongDescription());

        TextView target_qty = (TextView) findViewById(R.id.recipe_text_qty_tgt);
        target_qty.setText(r.getTargetQuantity().toString());

        TextView target_desc = (TextView) findViewById(R.id.recipe_text_desc_tgt);
        target_desc.setText(r.getTargetDescription().toString());

        for (int i = 0; i < r.getIngredientDescriptions().size(); ++i) {
            expandIngredientList();
            IngredientIds iids = _ingList.get(_ingList.size() - 1);

            TextView ing_qty = (TextView) findViewById(iids.qty_id);
            ing_qty.setText(r.getIngredientQuantity().get(i).toString());

            TextView ing_desc = (TextView) findViewById(iids.desc_id);
            ing_desc.setText(r.getIngredientDescriptions().get(i));

            TextView other_r = (TextView) findViewById(iids.other_id);
            if (r.getOtherRecipeIds().get(i) != Recipe.NO_ID) {
                BasicRecipe basic_other = _sqlctrl.getBasicRecipe(r.getOtherRecipeIds().get(i));
                final int final_i = i;
                other_r.setText(basic_other.getName() + " View Recipe");
                other_r.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
                        intent.putExtra("id", r.getOtherRecipeIds().get(final_i));
                        startActivity(intent);
                    }
                });
            }
        }

        for (int i = 0; i < r.getInstructions().size(); ++i) {
            expandInstructionList();
            InstructionIds iids = _insList.get(_insList.size() - 1);

            TextView ins_desc = (TextView) findViewById(iids.desc_id);
            ins_desc.setText("" + (i + 1) + ": " + r.getInstructions().get(i));
        }

        for (int i = 0; i < r.getTags().size(); ++i) {
            expandTagList();
            TagIds tids = _tagList.get(_tagList.size() - 1);

            TextView tag_desc = (TextView) findViewById(tids.tag_id);
            tag_desc.setText(r.getTags().get(i));
        }

        for (int i = 0; i < r.getComments().size(); ++i) {
            expandCommentList();
            CommentIds cids = _cmntList.get(_cmntList.size() - 1);

            TextView cmnt_desc = (TextView) findViewById(cids.comment_id);
            cmnt_desc.setText(r.getComments().get(i));
        }
    }

    private void expandIngredientList() {
        int last_view_id = -1;
        if (_ingList.isEmpty()) {
            last_view_id = ((TextView) findViewById(R.id.recipe_text_ingredient)).getId();
        }
        else {
            last_view_id = _ingList.get(_ingList.size() - 1).qty_id;
        }

        TextView qty_tv = new TextView(this);
        qty_tv.setId(View.generateViewId());
        qty_tv.setText("00");
        RelativeLayout.LayoutParams qty_tv_lp = new RelativeLayout.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
        qty_tv_lp.addRule(RelativeLayout.BELOW, last_view_id);
        qty_tv_lp.addRule(RelativeLayout.ALIGN_PARENT_START, 1);

        TextView other_ing_tv = new TextView(this);
        other_ing_tv.setId(View.generateViewId());
        other_ing_tv.setText("");
        RelativeLayout.LayoutParams other_ing_lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        other_ing_lp.addRule(RelativeLayout.ALIGN_PARENT_END, 1);
        other_ing_lp.addRule(RelativeLayout.BELOW, last_view_id);

        TextView desc_tv = new TextView(this);
        desc_tv.setId(View.generateViewId());
        desc_tv.setHint("ingredient");
        RelativeLayout.LayoutParams desc_et_lp = new RelativeLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
        desc_et_lp.addRule(RelativeLayout.BELOW, last_view_id);
        desc_et_lp.addRule(RelativeLayout.RIGHT_OF, qty_tv.getId());
        desc_et_lp.addRule(RelativeLayout.LEFT_OF, other_ing_tv.getId());
        desc_et_lp.setMarginStart(10);


        IngredientIds iids = new IngredientIds();
        iids.qty_id = qty_tv.getId();
        iids.desc_id = desc_tv.getId();
        iids.other_id = other_ing_tv.getId();
        _ingList.add(iids);

        _rl.addView(qty_tv,qty_tv_lp);
        _rl.addView(desc_tv,desc_et_lp);
        _rl.addView(other_ing_tv,other_ing_lp);

        TextView tv = (TextView) findViewById(R.id.recipe_text_instruction);
        RelativeLayout.LayoutParams tv_lp = new RelativeLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_lp.addRule(RelativeLayout.BELOW, desc_tv.getId());
        tv_lp.addRule(RelativeLayout.ALIGN_PARENT_START, 1);
        tv.setLayoutParams(tv_lp);
    }

    private void expandInstructionList() {
        int last_view_id = -1;
        if (_insList.isEmpty()) {
            last_view_id = ((TextView) findViewById(R.id.recipe_text_instruction)).getId();
        }
        else {
            last_view_id = _insList.get(_insList.size() - 1).desc_id;
        }

        TextView desc_tv = new TextView(this);
        desc_tv.setId(View.generateViewId());
        desc_tv.setHint("instruction");
        RelativeLayout.LayoutParams desc_et_lp = new RelativeLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
        desc_et_lp.addRule(RelativeLayout.BELOW, last_view_id);
        desc_et_lp.addRule(RelativeLayout.ALIGN_PARENT_START, 1);

        InstructionIds iids = new InstructionIds();
        iids.desc_id = desc_tv.getId();
        _insList.add(iids);

        _rl.addView(desc_tv,desc_et_lp);

        TextView tv = (TextView) findViewById(R.id.recipe_text_tag);
        RelativeLayout.LayoutParams tv_lp = new RelativeLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_lp.addRule(RelativeLayout.BELOW, desc_tv.getId());
        tv_lp.addRule(RelativeLayout.ALIGN_PARENT_START, 1);
        tv.setLayoutParams(tv_lp);
    }

    private void expandTagList() {
        int last_view_id = -1;
        if (_tagList.isEmpty()) {
            last_view_id = ((TextView) findViewById(R.id.recipe_text_tag)).getId();
        }
        else {
            last_view_id = _tagList.get(_tagList.size() - 1).tag_id;
        }

        TextView tag_et = new TextView(this);
        tag_et.setId(View.generateViewId());
        tag_et.setHint("tag");
        RelativeLayout.LayoutParams tag_tv_lp = new RelativeLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
        tag_tv_lp.addRule(RelativeLayout.BELOW, last_view_id);
        tag_tv_lp.addRule(RelativeLayout.ALIGN_PARENT_START, 1);

        TagIds tids = new TagIds();
        tids.tag_id = tag_et.getId();
        _tagList.add(tids);

        _rl.addView(tag_et,tag_tv_lp);

        TextView tv = (TextView) findViewById(R.id.recipe_text_comment);
        RelativeLayout.LayoutParams tv_lp = new RelativeLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_lp.addRule(RelativeLayout.BELOW, tag_et.getId());
        tv_lp.addRule(RelativeLayout.ALIGN_PARENT_START, 1);
        tv.setLayoutParams(tv_lp);
    }

    private void expandCommentList() {
        int last_view_id = -1;
        if (_cmntList.isEmpty()) {
            last_view_id = ((TextView) findViewById(R.id.recipe_text_comment)).getId();
        } else {
            last_view_id = _cmntList.get(_cmntList.size() - 1).comment_id;
        }

        TextView desc_tv = new TextView(this);
        desc_tv.setId(View.generateViewId());
        desc_tv.setHint("comment");
        RelativeLayout.LayoutParams desc_tv_lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        desc_tv_lp.addRule(RelativeLayout.BELOW, last_view_id);
        desc_tv_lp.addRule(RelativeLayout.ALIGN_PARENT_START, 1);

        CommentIds cids = new CommentIds();
        cids.comment_id = desc_tv.getId();
        _cmntList.add(cids);

        _rl.addView(desc_tv, desc_tv_lp);
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
        for  (IngredientIds iids : _ingList) {
            TextView tv = (TextView) findViewById(iids.qty_id);
            Float new_qty = Float.parseFloat(tv.getText().toString()) * _tgt_scale;
            tv.setText(new_qty.toString());
        }
    }

    private static String TAG = "RecipeActivity";

     private class IngredientIds {
        //int spinner_id = -1;
        int qty_id = -1;
        int desc_id = -1;
         int other_id = -1;
    }

    private class InstructionIds {
        int desc_id = -1;
    }

    private class TagIds {
        int tag_id = -1;
    }

    private class CommentIds {
        int comment_id = -1;
    }
    private int PROMPT_FOR_NEW_RECIPE_REQUEST_CODE = 1;

    private Float _tgt_scale = 1.0f;
    private Recipe _recipe = null;
    private SqlController _sqlctrl = new SqlController(this);

    private RelativeLayout _rl = null;
    private ArrayList<IngredientIds> _ingList = new ArrayList<>();
    private ArrayList<InstructionIds> _insList = new ArrayList<>();
    private ArrayList<TagIds> _tagList = new ArrayList<>();
    private ArrayList<CommentIds> _cmntList = new ArrayList<>();
    */
}
