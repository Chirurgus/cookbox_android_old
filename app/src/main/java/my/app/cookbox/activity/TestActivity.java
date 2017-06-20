package my.app.cookbox.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import my.app.cookbox.R;
import my.app.cookbox.fragment.ModifyFragment;

/**
 * Created by Alexander on 016, 16 Jun.
 */

public class TestActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        android.app.FragmentManager fm = getFragmentManager();
        android.app.FragmentTransaction ft = fm.beginTransaction();
        ModifyFragment mf = new ModifyFragment();
        ft.add(R.id.test_frame, mf);
        ft.commit();
    }
}
