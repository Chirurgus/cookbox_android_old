package my.app.cookbox.activity;

import android.app.Activity;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import my.app.cookbox.R;
import my.app.cookbox.fragment.ModifyFragment;
import my.app.cookbox.fragment.RecipeFragment;
import my.app.cookbox.fragment.RecipeListFragment;
import my.app.cookbox.recipe.BasicRecipe;
import my.app.cookbox.recipe.RecipeTag;
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

        selectCategory(null);
        _listfrag = startListFragment();

        setupNavigationDrawer();
    }

    public void addToRecipeList(BasicRecipe br) {
        _rlist.add(br);
        if (_listfrag != null) {
            ((RecipeAdapter) _listfrag.getListAdapter()).updateDataset(_rlist);
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

    public void selectCategory(RecipeTag tag) {
        if (tag == null) {
            Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
            _rlist = _sqlctrl.getAllBasicRecipes();
            return;
        }

        _rlist = _sqlctrl.getTaggedBasicRecipe(tag);
    }

    public ArrayList<BasicRecipe> getAllBasicRecipes() {
        return _rlist;
    }

    public SqlController getSqlController() {
        return _sqlctrl;
    }

    private void setupNavigationDrawer() {
        ArrayList<RecipeTag> tags = _sqlctrl.getAllRecipeTags();
        ((ListView) findViewById(R.id.drawer_list)).setAdapter(
                new ArrayAdapter<RecipeTag>(this, R.layout.simple_list_text_layout ,tags)
        );
        ((ListView) findViewById(R.id.drawer_list)).setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        selectCategory((RecipeTag) parent.getAdapter().getItem(pos));
                        ((RecipeAdapter) _listfrag.getListAdapter()).updateDataset(_rlist);
                    }
                }
        );
    }

    private SqlController _sqlctrl = new SqlController(this);
    private ArrayList<BasicRecipe> _rlist = new ArrayList<>();
    private RecipeListFragment _listfrag = null;
}
