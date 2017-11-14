package my.app.cookbox.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import my.app.cookbox.R;
import my.app.cookbox.fragment.ModifyFragment;
import my.app.cookbox.fragment.RecipeFragment;
import my.app.cookbox.fragment.RecipeListFragment;
import my.app.cookbox.fragment.TagSelectionListFragment;
import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.recipe.Recipe;
import my.app.cookbox.sqlite.SqlController;
import my.app.cookbox.utility.TagSelectionAdapter;

/**
 * Created by Alexander on 024, 24 Oct.
 */

abstract public class BaseActivity extends AppCompatActivity {


    public ArrayList<BasicRecipe> getAllBasicRecipes() {
        return getSqlController().getAllBasicRecipes();
    }

    public SqlController getSqlController() {
        return _sqlctrl;
    }

    abstract public RecipeFragment startRecipeFragment(Long id);
    abstract public ModifyFragment startModifyFragment(Long id);
    abstract public RecipeFragment startRecipeFragment(Long id, boolean addToBackStack);
    abstract public ModifyFragment startModifyFragment(Long id, boolean addToBackStack);

    protected RecipeFragment startRecipeFragment(Long id, int frameResource, boolean addToBack) {
        RecipeFragment new_frag = new RecipeFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (addToBack) {
            ft.addToBackStack(null);
        }
        if (id != null) {
            Bundle args = new Bundle();
            args.putLong("id", id);
            new_frag.setArguments(args);
        }
        ft.replace(frameResource, new_frag);
        ft.commit();
        return new_frag;
    }

    protected ModifyFragment startModifyFragment(Long id, int frameResource, boolean addToBack) {
        ModifyFragment new_frag = new ModifyFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (addToBack) {
            ft.addToBackStack(null);
        }
        if (id != null) {
            Bundle args = new Bundle();
            args.putLong("id", id);
            new_frag.setArguments(args);
        }
        ft.replace(frameResource, new_frag);
        ft.commit();
        return new_frag;
    }

    private SqlController _sqlctrl = new SqlController(this);
}
