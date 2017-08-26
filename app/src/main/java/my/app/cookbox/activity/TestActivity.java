package my.app.cookbox.activity;

import android.app.Activity;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import my.app.cookbox.R;
import my.app.cookbox.fragment.ModifyFragment;
import my.app.cookbox.fragment.RecipeFragment;
import my.app.cookbox.fragment.RecipeListFragment;
import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.sqlite.SqlController;
import my.app.cookbox.utility.RecipeAdapter;

/**
 * Created by Alexander on 016, 16 Jun.
 */

public class TestActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        _rlist = _sqlctrl.getAllBasicRecipes();
        _listfrag = startListFragment();
        //_modifyfrag = startModifyFragment(null);//_rlist.get(0).getId());
        //startRecipeFramgent(_rlist.get(0).getId());

        ((ListView) findViewById(R.id.drawer_list))
                .setAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_text_layout ,new String[] {"hi", "sup", "la", "what'sup", "how is it it", "1", "1", "fflkes", "how are yotu", "what are you doing", "am i done yet?", "I think so"}));
    }

    public void addToRecipeList(BasicRecipe br) {
        _rlist.add(br);
        if (_listfrag != null) {
            ((RecipeAdapter) _listfrag.getListAdapter()).notifyDataSetChanged();
        }
    }

    public RecipeListFragment startListFragment() {
        RecipeListFragment new_frag = new RecipeListFragment();
        if (new_frag != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment_frame, new_frag);
            // A ListFragment is only added once.
            //ft.addToBackStack(null);
            ft.commit();

            new_frag.setListAdapter(new RecipeAdapter(_rlist, this));
        }
        return new_frag;
    }

    public ModifyFragment startModifyFragment(Long id) {
       ModifyFragment new_frag = new ModifyFragment();
        if (new_frag != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment_frame, new_frag);

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

    public RecipeFragment startRecipeFramgent(Long id) {
        RecipeFragment new_frag = new RecipeFragment();
        if (new_frag != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment_frame, new_frag);
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
