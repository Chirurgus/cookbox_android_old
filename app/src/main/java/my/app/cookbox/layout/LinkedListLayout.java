package my.app.cookbox.layout;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by Alexander on 017, 17 Jun.
 */

public class LinkedListLayout extends LinearLayout {
    public LinkedListLayout(Context context) {
        super(context);
    }

    private LinkedList<View> _list = new LinkedList<>();
}
