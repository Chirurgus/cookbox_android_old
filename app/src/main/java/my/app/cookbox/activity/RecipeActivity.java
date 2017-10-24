package my.app.cookbox.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import my.app.cookbox.R;
import my.app.cookbox.fragment.RecipeFragment;
import my.app.cookbox.fragment.RecipeListFragment;

/**
 * Created by Alexander on 023, 23 Oct.
 */

public class RecipeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.recipe_test);
        setSupportActionBar((Toolbar) findViewById(R.id.recipe_toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            //startRecipeFragment(b.getLong("id", -1));
        }
        else {
           // startRecipeFragment(-1);
        }
    }

    private void startRecipeFragment(long id) {
        RecipeFragment new_frag = new RecipeFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        Bundle b = new Bundle();
        b.putLong("id", id);
        new_frag.setArguments(b);

        ft.replace(R.id.recipe_fragment_frame, new_frag);
        ft.commit();
    }
}
