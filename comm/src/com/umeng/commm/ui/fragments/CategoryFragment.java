package com.umeng.commm.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.Category;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.commm.ui.live.LiveActivity;
import com.umeng.commm.ui.activities.SearchTopicActivity;
import com.umeng.commm.ui.activities.TopicActivity;
import com.umeng.commm.ui.adapters.CategoryAdapter;
import com.umeng.commm.ui.adapters.TopicAdapter;
import com.umeng.common.ui.adapters.BackupAdapter;
import com.umeng.common.ui.adapters.RecommendTopicAdapter;
import com.umeng.common.ui.dialogs.CustomCommomDialog;
import com.umeng.common.ui.fragments.BaseFragment;
import com.umeng.common.ui.mvpview.MvpCategoryView;
import com.umeng.common.ui.presenter.impl.CategoryPresenter;
import com.umeng.common.ui.util.FontUtils;
import com.umeng.common.ui.widgets.BaseView;
import com.umeng.common.ui.widgets.RefreshLayout;
import com.umeng.common.ui.widgets.RefreshLvLayout;
import com.umeng_community_library_project.R;

import java.util.List;

/**
 * Created by wangfei on 15/11/24.
 */
public class CategoryFragment extends BaseFragment<List<Category>, CategoryPresenter>
        implements MvpCategoryView {

    private static final int STATUS_NORMAL = 0x01;// 正常状态。无意义
    private static final int STATUS_SHOW = 0x02;// 显示状态
    private static final int STATUS_DISMISS = 0x03;// 隐藏状态

    private int mLastScrollY = 0;// 上次滑动时Y的起始坐标
    private int mSlop;
    private transient int currentStatus = STATUS_NORMAL; // 当前Float Button的状态
    private transient boolean isExecutingAnim = false; // 是否正在执行动画


    private CategoryAdapter mAdapter;
    private BackupAdapter<Topic, ?> topicAdapter;
    protected ListView mCategoryListView;
    protected RefreshLvLayout mRefreshLvLayout;
    protected BaseView mBaseView;
    protected View mSearchLayout;
    //    private EditText mSearchEdit;
    private boolean mIsBackup = false;
    private InputMethodManager mInputMan;
    /**
     * 点击进入直播列表的button
     */
    protected ImageView mPostBtn;
    protected Dialog mProcessDialog;

    private boolean isFirstCreate = true;

    public CategoryFragment() {
    }

    public static CategoryFragment newCategoryFragment() {
        return new CategoryFragment();
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        FontUtils.changeTypeface(mRootView);
        initRefreshView(mRootView);
        mProcessDialog = new CustomCommomDialog(getActivity(), ResFinder.getString("umeng_comm_logining"));
        initSearchView(mRootView);
        initTitleView(mRootView);
        initAdapter();
        mInputMan = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
    }

    /**
     * 初始化搜索话题View跟事件处理</br>
     *
     * @param rootView
     */
    protected void initSearchView(View rootView) {
        View headerView = LayoutInflater.from(getActivity()).inflate(
                ResFinder.getLayout("umeng_comm_search_header_view"), null);
        headerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchTopicActivity.class);
                getActivity().startActivity(intent);

            }
        });
        TextView searchtv = (TextView) headerView.findViewById(ResFinder.getId("umeng_comm_comment_send_button"));
        searchtv.setText(ResFinder.getString("umeng_comm_search_topic"));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) searchtv.getLayoutParams();
        params.topMargin = 0;
        params.bottomMargin = CommonUtils.dip2px(getActivity(), 7);
        mSearchLayout = findViewById(ResFinder.getId("umeng_comm_topic_search_title_layout"));
        mSearchLayout.setVisibility(View.GONE);
        mCategoryListView.addHeaderView(headerView, null, false);
//        View headerView = LayoutInflater.from(getActivity()).inflate(
//                ResFinder.getLayout("umeng_comm_search_header_view"), null);
//        headerView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), SearchTopicBaseActivity.class);
//                getActivity().startActivity(intent);
//            }
//        });
//        mTipView = (TextView) headerView.findViewById(ResFinder.getId("umeng_comm_feeds_tips"));
//        rootView.addv(headerView);


    }

    @Override
    protected CategoryPresenter createPresenters() {
        return new CategoryPresenter(this);
    }

    /**
     * 初始化刷新相关的view跟事件</br>
     *
     * @param rootView
     */

    protected void initRefreshView(View rootView) {
        int refreshResId = ResFinder.getId("umeng_comm_topic_refersh");
        mRefreshLvLayout = (RefreshLvLayout) rootView.findViewById(refreshResId);

        mRefreshLvLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mPresenter.loadDataFromServer();
            }
        });
        mRefreshLvLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                mPresenter.loadMoreData();
            }
        });


        int listViewResId = ResFinder.getId("umeng_comm_topic_listview");
        mCategoryListView = mRefreshLvLayout.findRefreshViewById(listViewResId);

        mRefreshLvLayout.setDefaultFooterView();


        mBaseView = (BaseView) rootView.findViewById(ResFinder.getId("umeng_comm_baseview"));
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_topic"));
    }

    protected void initTitleView(View rootView) {
        mPostBtn = (ImageView) rootView.findViewById(R.id.post_play);
        mPostBtn.setVisibility(View.GONE);
        mPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LiveActivity.class));
            }
        });
//            int searchButtonResId = ResFinder.getId("umeng_comm_topic_search");
//            rootView.findViewById(searchButtonResId).setOnClickListener(
//                    new Listeners.LoginOnViewClickListener() {
//
//                        @Override
//                        protected void doAfterLogin(View v) {
////                            mInputMan.hideSoftInputFromWindow(mSearchEdit.getWindowToken(), 0);
////                            mAdapter.backupData();
////                            ((TopicFgPresenter) mPresenter).executeSearch(mSearchEdit
////                                    .getText().toString().trim());
//                        };
//                    });
//            rootView.findViewById(ResFinder.getId("umeng_comm_back")).setVisibility(View.GONE);
//            int paddingRight = DeviceUtils.dp2px(getActivity(), 10);
//            int paddingLeft = mSearchEdit.getPaddingLeft();
//            int paddingTop = mSearchEdit.getPaddingTop();
//            int paddingBottom = mSearchEdit.getPaddingBottom();
//            mSearchEdit.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    protected void initAdapter() {
        CategoryAdapter adapter = new CategoryAdapter(getActivity());

        mAdapter = adapter;

        mCategoryListView.setAdapter(mAdapter);
        mCategoryListView.setDividerHeight(CommonUtils.dip2px(getActivity(), 1));
        mCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                mPresenter.loadTopic(getBindDataSource().get(i).id);
//                com.umeng.comm.core.utils.Log.e("xxxxxx", "i = " + i + "   size = " + getBindDataSource().size());
                Intent intent = new Intent(getActivity(), TopicActivity.class);
                intent.putExtra("uid", getBindDataSource().get(i - 1).id);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_category_recommend");
    }

    @Override
    public List<Category> getBindDataSource() {
        return mAdapter.getDataSource();
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefreshEndNoOP() {
        mRefreshLvLayout.setRefreshing(false);
        mRefreshLvLayout.setLoading(false);
        mBaseView.hideEmptyView();
    }

    @Override
    public void ChangeAdapter(List<Topic> list) {
        topicAdapter = new TopicAdapter(getActivity());
        ((TopicAdapter) topicAdapter).setFollowListener(new RecommendTopicAdapter.FollowListener<Topic>() {

            @Override
            public void onFollowOrUnFollow(Topic topic, ToggleButton toggleButton,
                                           boolean isFollow) {
                mPresenter.checkLoginAndExecuteOp(topic, toggleButton, isFollow);
            }
        });
        topicAdapter.addData(list);
        mCategoryListView.setAdapter(topicAdapter);
        mCategoryListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkWhetherExecuteAnimation(event);
                return false;
            }
        });
    }

    @Override
    public void onRefreshStart() {
        mRefreshLvLayout.setRefreshing(true);
    }

    @Override
    public void onRefreshEnd() {
        onRefreshEndNoOP();
        if (mAdapter.getCount() == 0) {
            mBaseView.showEmptyView();
        } else {
            mBaseView.hideEmptyView();
        }
    }

    @Override
    public void onPause() {
//        mInputMan.hideSoftInputFromWindow(mSearchEdit.getWindowToken(), 0);
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mProcessDialog != null) {
            mProcessDialog.dismiss();
        }
    }

    /**
     * 检查是否为Float button执行动画</br>
     *
     * @param event
     */
    private void checkWhetherExecuteAnimation(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastScrollY = y;
                break;

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
                int deltaY = mLastScrollY - y;
                mLastScrollY = y;

                if (Math.abs(deltaY) < mSlop) {
                    return;
                }
                if (deltaY > 0) {
                    executeAnimation(false);
                } else {
                    executeAnimation(true);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 为Float button执行动画</br>
     *
     * @param show 显示 or 隐藏
     */
    private void executeAnimation(final boolean show) {

        if (isListViewEmpty()) {
            return;
        }

        if (isExecutingAnim || (show && currentStatus == STATUS_SHOW)
                || (!show && currentStatus == STATUS_DISMISS)) {
            return;
        }
        isExecutingAnim = true;
        int moveDis = ((FrameLayout.LayoutParams) (mPostBtn.getLayoutParams())).bottomMargin
                + mPostBtn.getHeight();
        Animation animation = null;
        if (show) {
            animation = new TranslateAnimation(0, 0, moveDis, 0);
        } else {
            animation = new TranslateAnimation(0, 0, 0, moveDis);
        }
        animation.setDuration(300);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isExecutingAnim = false;
                if (show) {
                    currentStatus = STATUS_SHOW;
                } else {
                    currentStatus = STATUS_DISMISS;
                }
                // 对于3.0以下系统，原来的地方仍有点击事件。由于我们的需要是处理可见性，因此此处不在对Float
                // Button做layout处理。
                mPostBtn.setClickable(show);
            }
        });
        mPostBtn.startAnimation(animation);
    }

    private boolean isListViewEmpty() {
        int count = mCategoryListView.getAdapter().getCount();
        int otherCount = mCategoryListView.getFooterViewsCount()
                + mCategoryListView.getHeaderViewsCount();
        // listview中没有数据时不隐藏发布按钮
        return count == otherCount;
    }
}
