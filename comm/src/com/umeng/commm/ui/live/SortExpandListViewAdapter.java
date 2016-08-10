package com.umeng.commm.ui.live;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.common.ui.widgets.RoundImageView;
import com.umeng_community_library_project.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by turbo on 2016/3/25.
 */
public class SortExpandListViewAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<Platform> platforms;
    private List<String> mNodes;
    private int[] colorList = new int[]{0xfff95d51, 0xffff9c00,
            0xff7dcd43, 0xfff95d51};
    private int[] mipmapList = new int[]{R.drawable.douyu, R.drawable.quanmin, R.drawable.huya,
            R.drawable.zhanqi,};
    private GroupViewHolder mGroupViewHolder;

    public SortExpandListViewAdapter(Context context, List<Platform> list) {
        this.mContext = context;
        this.platforms = list;
    }

    @Override
    public int getGroupCount() {
        return platforms.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return platforms.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return platforms.get(groupPosition).getPlayers().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        mGroupViewHolder = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = View.inflate(mContext, R.layout.umeng_comm_player_group_item, null);
            mGroupViewHolder = new GroupViewHolder();
            mGroupViewHolder.typeImageView = (ImageView) convertView.findViewById(R.id.sort_icon_iv1);
            mGroupViewHolder.textView = (TextView) convertView.findViewById(R.id.sort_tv1);
            mGroupViewHolder.arrowImageView = (ImageView) convertView.findViewById(R.id.sort_iv1);
            mGroupViewHolder.view = convertView.findViewById(R.id.view_sort_group_item);
            convertView.setTag(mGroupViewHolder);
        } else {
            mGroupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        mGroupViewHolder.typeImageView.setImageResource(mipmapList[groupPosition]);
        mGroupViewHolder.textView.setText(platforms.get(groupPosition).getName());
        if (isExpanded) {
            mGroupViewHolder.view.setVisibility(View.GONE);
            mGroupViewHolder.arrowImageView.setImageResource(R.drawable.on);
            mGroupViewHolder.textView.setTextColor(colorList[groupPosition % 6]);
        } else {
            mGroupViewHolder.view.setVisibility(View.VISIBLE);
            mGroupViewHolder.arrowImageView.setImageResource(R.drawable.off);
            mGroupViewHolder.textView.setTextColor(0xff000000);
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = View.inflate(mContext, R.layout.umeng_comm_player_child_item, null);
            viewHolder = new ChildViewHolder();

            viewHolder.icon = (RoundImageView) convertView.findViewById(R.id.play_icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.fanscount = (TextView) convertView.findViewById(R.id.tv_fanscount);
            viewHolder.online = (ImageView) convertView.findViewById(R.id.iv_online);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class ChildViewHolder {
        RoundImageView icon;
        TextView name;
        TextView title;
        TextView fanscount;
        ImageView online;
    }

    static class GroupViewHolder {
        ImageView typeImageView;
        TextView textView;
        ImageView arrowImageView;
        View view;
    }
}
