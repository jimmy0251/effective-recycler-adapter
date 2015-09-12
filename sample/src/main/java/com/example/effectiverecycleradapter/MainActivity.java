package com.example.effectiverecycleradapter;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jimmy.effectiverecycleradapter.EffectiveRecyclerAdapter;
import com.jimmy.effectiverecycleradapter.MultiChoiceModeListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MultiChoiceModeListener {

    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMyAdapter = new MyAdapter();
        // Set listener for MultiChoice Actions
        mMyAdapter.setMultiChoiceModeListener(this, this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMyAdapter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Call if you want to restore selected items on Activity recreate
        mMyAdapter.restoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Call if you want to save selected items on Activity recreate
        mMyAdapter.saveInstanceState(outState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelectionChanged(ActionMode mode, int position, boolean selected) {
        showToast(String.format("Item %d is now %s", position, selected ? "Selected" : "Unselected"));
        mode.setTitle(mMyAdapter.getSelectedCount() + " selected");
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, MenuInflater inflater, Menu menu) {
        showToast("On Create Action Mode, Inflate the menu");
        inflater.inflate(R.menu.menu_action, menu);
        return true;
    }

    @Override
    public void onDestroyActionMode() {
        showToast("Destroy Action Mode");
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_all:
                showToast("Select All");
                mMyAdapter.selectAll();
                return true;
            case R.id.action_settings:
                showToast("Settings");
                return true;
        }
        return false;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView item;

        public MyViewHolder(TextView itemView) {
            super(itemView);
            item = itemView;
            item.setOnClickListener(this);
            item.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // You decide when Selected state should be changed
            if (mMyAdapter.isActionModeActive()) {
                mMyAdapter.toggleSelected(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            // You decide when Selected state should be changed
            mMyAdapter.toggleSelected(getAdapterPosition());
            return true;
        }
    }

    class MyAdapter extends EffectiveRecyclerAdapter<MyViewHolder> {

        List<String> items;

        MyAdapter() {
            items = getDummyItems();
        }

        private List<String> getDummyItems() {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                list.add("My Item " + i);
            }
            return list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView item = (TextView) getLayoutInflater().inflate(R.layout.list_item, parent, false);
            return new MyViewHolder(item);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.item.setText(items.get(position));

            // Check if item is selected and set the appropriate view
            holder.item.setBackgroundColor(isSelected(position) ? Color.GRAY : Color.WHITE);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
