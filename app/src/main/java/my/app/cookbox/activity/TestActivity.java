package my.app.cookbox.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import java.util.ArrayList;

import my.app.cookbox.R;
import my.app.cookbox.fragment.ModifyFragment;
import my.app.cookbox.fragment.RecipeListFragment;
import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.sqlite.SqlController;
import my.app.cookbox.utility.RecipeAdapter;

/**
 * Created by Alexander on 016, 16 Jun.
 */

public class TestActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        _rlist = _sqlctrl.getAllBasicRecipes();
        //_listfrag = startListFragment();
        _modifyfrag = startModifyFragment(null);//_rlist.get(0).getId());
    }

    public void addToRecipeList(BasicRecipe br) {
        _rlist.add(br);
        if (_listfrag != null) {
            ((RecipeAdapter) _listfrag.getListAdapter()).notifyDataSetChanged();
        }
    }

    private RecipeListFragment startListFragment() {
        RecipeListFragment new_frag = new RecipeListFragment();
        if (new_frag != null) {
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.main_fragment_frame, new_frag);
            ft.addToBackStack(null);
            ft.commit();

            new_frag.setListAdapter(new RecipeAdapter(_rlist, this));
        }
        return new_frag;
    }

    private ModifyFragment startModifyFragment(Long id) {
       ModifyFragment new_frag = new ModifyFragment();
        if (new_frag != null) {
             android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.main_fragment_frame, new_frag);
            ft.addToBackStack(null);
            if (id != null) {
                Bundle args = new Bundle();
                args.putLong("id", id);
                new_frag.setArguments(args);
            }

            ft.commit();
        }
        return new_frag;
    }

    public ArrayList<BasicRecipe> getAllBasicRecipes() {
        return _rlist;
    }

    public SqlController getSqlController() {
        return _sqlctrl;
    }

    private SqlController _sqlctrl = new SqlController(this);
    private ArrayList<BasicRecipe> _rlist;
    private RecipeListFragment _listfrag = null;
    private ModifyFragment _modifyfrag = null;
}
