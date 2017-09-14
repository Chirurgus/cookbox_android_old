package my.app.cookbox.utility;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;

import my.app.cookbox.R;

/**
 * Created by Alexander on 010, 10 Sep.
 */


/* ActionModeListener for Tags */
public class TagSelectionActionMode implements AbsListView.MultiChoiceModeListener {

    public TagSelectionActionMode(long tag_id) {
        _tag_id = tag_id;
    }

    public long getTagId() {
        return _tag_id;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int pos, long id, boolean b) {
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.main_toolbar, menu);
        actionMode.setTitle("Yay it's working");
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
    }

    private long _tag_id;
}
