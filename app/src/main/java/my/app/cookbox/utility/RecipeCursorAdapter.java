package my.app.cookbox.utility;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import my.app.cookbox.R;

/**
 * Created by Alexander on 007, 7 Feb.
 */

public class RecipeCursorAdapter extends ResourceCursorAdapter {
    public RecipeCursorAdapter(Context context, Cursor cursor,int layout_resource, boolean auto_requery){
        super(context,layout_resource,cursor,auto_requery);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_name = (TextView) view.findViewById(R.id.recipe_list_text1);
        TextView tv_desc = (TextView) view.findViewById(R.id.recipe_list_text2);

        tv_name.setText(cursor.getString(cursor.getColumnIndex("name")));
        tv_desc.setText(cursor.getString(cursor.getColumnIndex("short_description")));
    }
}
