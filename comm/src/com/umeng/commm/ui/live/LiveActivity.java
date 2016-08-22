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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.umeng_comm_live_activity);
    }

    protected void initData() {
        platforms = new ArrayList<>();
        fillData();
    }

    private void fillData() {
    }
}
