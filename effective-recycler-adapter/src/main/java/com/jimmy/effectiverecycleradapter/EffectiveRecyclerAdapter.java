package com.jimmy.effectiverecycleradapter;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by jimmy0251 on 4/7/15.
 */
public abstract class EffectiveRecyclerAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private static final String KEY_STATE_ADAPTER = "state_adapter";

    private ActionMode actionMode;

    private AppCompatActivity activity;

    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    private MultiChoiceModeListener choiceListener = MultiChoiceModeListener.EMPTY_LISTENER;

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            return choiceListener.onCreateActionMode(actionMode, mode.getMenuInflater(), menu);
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return choiceListener.onActionItemClicked(actionMode, item);
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            choiceListener.onDestroyActionMode();
            selectedItems.clear();
            notifyDataSetChanged();
            actionMode = null;
        }
    };

    /**
     * Sets MultiChoiceModeListener for this Activity.
     *
     * @param activity                AppCompatActivity
     * @param multiChoiceModeListener callback for MultiChoice actions
     */
    public void setMultiChoiceModeListener(AppCompatActivity activity,
                                           MultiChoiceModeListener multiChoiceModeListener) {
        if (activity != null && multiChoiceModeListener != null) {
            this.activity = activity;
            choiceListener = multiChoiceModeListener;
        }
    }

    /**
     * Toggles selected state
     *
     * @param position position of the item
     */
    public void toggleSelected(int position) {
        setSelected(position, !isSelected(position));
    }

    /**
     * Sets item's selected state
     *
     * @param position position of the item
     * @param selected whether item is selected or not
     */
    public void setSelected(int position, boolean selected) {
        if (activity == null) return;

        if (selectedItems.size() == 0) {
            startActionMode();
        }

        if (selected) {
            selectedItems.put(position, true);
        } else {
            selectedItems.delete(position);
        }

        choiceListener.onItemSelectionChanged(actionMode, position, selected);

        if (selectedItems.size() == 0) {
            finishActionMode();
        }
        notifyItemChanged(position);
    }

    /**
     * Starts ActionMode, Should not be invoked directly
     * <p/>
     * <p/>
     * Instead use setSelected and toggleSelected method which will start ActionMode when
     * appropriate
     */
    public void startActionMode() {
        activity.startSupportActionMode(callback);
    }

    /**
     * Finishes Action Mode
     * <p/>
     * <p/>
     * Clears selected items and notifyDataSetChanged will be invoked
     */
    public void finishActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    /**
     * Provides selected items count
     *
     * @return selected items count
     */
    public int getSelectedCount() {
        return selectedItems.size();
    }

    /**
     * Provides selected item's positions
     *
     * @return SparseBooleanArray containing selected positions
     */
    public SparseBooleanArray getSelectedPositions() {
        return selectedItems.clone();
    }

    /**
     * Checks whether given position is selected
     *
     * @param position position of item
     * @return whether position is selected
     */
    public boolean isSelected(int position) {
        return selectedItems.get(position);
    }

    /**
     * Checks whether ActionMode is active
     *
     * @return whether ActionMode is acive
     */
    public boolean isActionModeActive() {
        return actionMode != null;
    }

    /**
     * Selects all positions of adapter
     * <p/>
     * It will invoke onSelectionChanged callback only on the positions which are not selected
     */
    public void selectAll() {
        for (int i = 0, count = getItemCount(); i < count; i++) {
            if (!isSelected(i)) {
                setSelected(i, true);
            }
        }
    }

    /**
     * Saves Adapter state in given bundle, later it can be restored with restoreInstanceState
     *
     * @param outState Bundle in which Adapter state will be saved
     */
    public void saveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_STATE_ADAPTER, onSaveInstanceState());
    }

    /**
     * Provides current Adapter State
     *
     * @return Current adapter state
     */
    public AdapterState onSaveInstanceState() {
        AdapterState state = new AdapterState();
        state.mSelectedPositions = selectedItems;
        return state;
    }

    /**
     * Restores Adapter state from given Bundle if it was previously saved with saveInstanceState
     *
     * @param savedInstanceState Bundle from which Adapter state will be restored
     */
    public void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            onRestoreInstanceState((AdapterState) savedInstanceState.getParcelable(KEY_STATE_ADAPTER));
        }
    }

    /**
     * Sets given Adapter state as the current adapter state
     *
     * @param adapterState Adapter state of EffectiveRecyclerView
     */
    public void onRestoreInstanceState(AdapterState adapterState) {
        SparseBooleanArray selectedItems = adapterState.mSelectedPositions;
        for (int i = 0; i < selectedItems.size(); i++) {
            setSelected(selectedItems.keyAt(i), true);
        }
    }

    public static class AdapterState implements Parcelable {
        private SparseBooleanArray mSelectedPositions;

        private AdapterState() {
        }

        private AdapterState(Parcel in) {
            mSelectedPositions = in.readSparseBooleanArray();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeSparseBooleanArray(mSelectedPositions);
        }

        public static Creator<AdapterState> CREATOR = new Creator<AdapterState>() {
            @Override
            public AdapterState createFromParcel(Parcel source) {
                return new AdapterState(source);
            }

            @Override
            public AdapterState[] newArray(int size) {
                return new AdapterState[size];
            }
        };
    }
}
