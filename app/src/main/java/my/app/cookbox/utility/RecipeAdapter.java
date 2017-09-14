package my.app.cookbox.utility;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import my.app.cookbox.R;
import my.app.cookbox.recipe.BasicRecipe;

/**
 * Created by Alexander on 014,  14 Apr.
 */

public class RecipeAdapter extends BaseAdapter {

    public RecipeAdapter(ArrayList<BasicRecipe> d, Context c) {
        _data = d;
        _context = c;
        _inflater = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateDataset(ArrayList<BasicRecipe> d) {
        _data.clear();
        _data.addAll(d);
        notifyDataSetChanged();
    }

    public Context getContext() {
        return _context;
    }

    public LayoutInflater getInflater() {
        return _inflater;
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int pos) {
        return _data.get(pos);
    }

    @Override
    public long getItemId(int pos) { return _data.get(pos).getId(); }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View ret;

        Log.d(TAG, "Entered RecipeAdapter.getView with pos = " + pos);

        if (convertView != null && convertView instanceof RelativeLayout) {
            ret = convertView;
        }
        else {
            ret = getInflater().inflate(R.layout.recipe_list_item, null);
        }
        TextView tv_name = (TextView) ret.findViewById(R.id.recipe_list_text1);
        TextView tv_desc = (TextView) ret.findViewById(R.id.recipe_list_text2);

        tv_name.setText(((BasicRecipe) getItem(pos)).getName());
        tv_desc.setText(((BasicRecipe) getItem(pos)).getShortDescription());

        return ret;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static String TAG = "RecipeAdapter";

    private ArrayList<BasicRecipe> _data;
    private Context _context;
    private LayoutInflater _inflater;
}
