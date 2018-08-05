package my.app.cookbox.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import my.app.cookbox.R;
import my.app.cookbox.fragment.ModifyFragment;
import my.app.cookbox.fragment.RecipeFragment;
import my.app.cookbox.fragment.RecipeListFragment;
import my.app.cookbox.recipe.Recipe;

/**
 * Created by Alexander on 023, 23 Oct.
 */

public class RecipeActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.recipe_test);
        setSupportActionBar((Toolbar) findViewById(R.id.recipe_toolbar));
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        Bundle b = getIntent().getExtras();
        startRecipeFragment(b.getLong("id"),false);
    }

    @Override
    public ModifyFragment startModifyFragment(Long id, boolean addToBackStack) {
        return super.startModifyFragment(id, R.id.recipe_fragment_frame, addToBackStack);
    }

    @Override
    public ModifyFragment startModifyFragment(Long id) {
        return this.startModifyFragment(id, true);
    }

    @Override
    public RecipeFragment startRecipeFragment(Long id, boolean addToBackStack) {
        return super.startRecipeFragment(id, R.id.recipe_fragment_frame, addToBackStack);
    }

    @Override
    public RecipeFragment startRecipeFragment(Long id) {
        return this.startRecipeFragment(id, true);
    }
}
