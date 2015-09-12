package com.jimmy.effectiverecycleradapter;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by jimmy0251 on 4/7/15.
 */
public abstract class EffectiveRecyclerAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private static final String TAG = "EffectiveRecycler";

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

    public void setMultiChoiceModeListener(AppCompatActivity activity,
                                           MultiChoiceModeListener multiChoiceModeListener) {
        if (activity != null && multiChoiceModeListener != null) {
            this.activity = activity;
            choiceListener = multiChoiceModeListener;
        }
    }

    public void toggleSelected(int position) {
        setSelected(position, !isSelected(position));
    }

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
            finishActionModeIfActive();
        }
        notifyItemChanged(position);
    }

    public void startActionMode() {
        activity.startSupportActionMode(callback);
    }

    public void finishActionModeIfActive() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    public int getSelectedCount() {
        return selectedItems.size();
    }

    public SparseBooleanArray getSelectedPositions() {
        return selectedItems.clone();
    }

    public boolean isSelected(int position) {
        return selectedItems.get(position);
    }

    public boolean isActionModeActive() {
        return actionMode != null;
    }

    public void setSelectedAll() {
        for (int i = 0, count = getItemCount(); i < count; i++) {
            if (!isSelected(i)) {
                setSelected(i, true);
            }
        }
    }

    public void saveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_STATE_ADAPTER, onSaveInstanceState());
    }

    public Parcelable onSaveInstanceState() {
        AdapterState state = new AdapterState();
        state.mSelectedPositions = selectedItems;
        return state;
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState.getParcelable(KEY_STATE_ADAPTER));
        }
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof AdapterState) {
            SparseBooleanArray selectedItems = ((AdapterState) parcelable).mSelectedPositions;
            for (int i = 0; i < selectedItems.size(); i++) {
                setSelected(selectedItems.keyAt(i), true);
            }
        }
    }

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

    static class AdapterState implements Parcelable {
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
