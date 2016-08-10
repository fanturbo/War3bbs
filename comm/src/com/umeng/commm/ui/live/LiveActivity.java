package com.umeng.commm.ui.live;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import com.umeng_community_library_project.R;

import java.util.ArrayList;
import java.util.List;

public class LiveActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private List<Platform> platforms;
    private SortExpandListViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.umeng_comm_live_activity);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
    }

    protected void initData() {
        platforms = new ArrayList<>();
        fillData();
    }

    private void fillData() {
        mAdapter = new SortExpandListViewAdapter(this, platforms);
        expandableListView.setAdapter(mAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < expandableListView.getExpandableListAdapter().getGroupCount(); i++) {
                    if (expandableListView.isGroupExpanded(i) && i != groupPosition) {
                        expandableListView.collapseGroup(i);
                    }
                }
            }
        });
        expandableListView.expandGroup(0);
    }
}
