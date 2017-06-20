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

    LayoutIterator iterator() {
        return new LayoutIterator((ListIterator<View>) _list.iterator());
    }

    private class LayoutIterator implements ListIterator<View> {
        LayoutIterator(ListIterator<View> li) {
            _curr = li;
        }

        @Override
        public int nextIndex() {
            return _curr.nextIndex();
        }

        @Override
        public void add(View view) {
            addView(view,_curr.nextIndex());
            _curr.add(view);
        }

        @Override
        public View previous() {
            return _curr.previous();
        }

        @Override
        public int previousIndex() {
            return _curr.previousIndex();
        }

        @Override
        public View next() {
            return _curr.next();
        }

        @Override
        public boolean hasPrevious() {
            return _curr.hasPrevious();
        }

        @Override
        public void remove() {
            if (!_curr.hasNext()) {
                return;
            }
            //so that the item deleted is always the one in front of the i
            _curr.next();
            removeView(_curr.previous());
            _curr.remove();
        }

        @Override
        public void set(View view) {
            _curr.remove();
            _curr.add(view);
        }

        @Override
        public boolean hasNext() {
            return _curr.hasNext();
        }

        private ListIterator<View> _curr;
    }

    private LinkedList<View> _list = new LinkedList<>();
}
