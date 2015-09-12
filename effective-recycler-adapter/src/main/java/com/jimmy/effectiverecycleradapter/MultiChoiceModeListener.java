package com.jimmy.effectiverecycleradapter;

import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by jimmy on 9/13/2015.
 */
public interface MultiChoiceModeListener {
    MultiChoiceModeListener EMPTY_LISTENER = new MultiChoiceModeListener() {
        @Override
        public void onItemSelectionChanged(ActionMode mode, int position, boolean selected) {

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, MenuInflater inflater, Menu menu) {
            return false;
        }

        @Override
        public void onDestroyActionMode() {

        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }
    };

    void onItemSelectionChanged(ActionMode mode, int position, boolean selected);

    boolean onCreateActionMode(ActionMode mode, MenuInflater inflater, Menu menu);

    void onDestroyActionMode();

    boolean onActionItemClicked(ActionMode mode, MenuItem item);
}
