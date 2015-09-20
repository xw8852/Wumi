package com.android.msx7.followinstagram.ui.push;

import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.msx7.followinstagram.R;


/**
 * Created by Xiaowei on 2014/5/20.
 */
public class PageFooter {
    ListView mListView;
    TextView mFooterView;
    int curPage;
    int totalPage;
    Adapter mAdapter;
    ILoadMoreListener l;
    boolean isEndID;
    int nextEndID;
    boolean isLoading;

    public PageFooter(ListView listView, Adapter adapter) {
        mListView = listView;
        mAdapter = adapter;
        listView.setOnScrollListener(mScrollListener);
        addFooterView();
    }

    public PageFooter(ListView listView, Adapter adapter, boolean isEndID) {
        mListView = listView;
        mAdapter = adapter;
        this.isEndID = isEndID;
        listView.setOnScrollListener(mScrollListener);
        addFooterView();
    }

    ListView.OnScrollListener mListener;

    public void setOnScrollListener(ListView.OnScrollListener listener) {
        this.mListener = listener;
    }

    /**
     * @param curPage   当前页
     * @param totalPage 总计多少页
     */
    public void updateStatus(int curPage, int totalPage) {
        isEndID = false;
        this.curPage = curPage;
        this.totalPage = totalPage;
        updateFooterView();
    }

    public void updateFooterFail() {
        isLoading = false;
        updateFooterView();
    }

    public void update(int EndID) {
        isEndID = true;
        nextEndID = EndID;
        updateFooterView();
    }

    public int getNextPage() {
        if (isEndID) return nextEndID;
        return curPage + 1;
    }

    boolean isEndPage() {
        return isEndID ? nextEndID == 0 : curPage >= totalPage;
    }

    Resources getResources() {
        return mListView.getResources();
    }

    public void setLoadMoreListener(ILoadMoreListener l) {
        this.l = l;
    }

    AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {
        int mLastItem;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (mListener != null) mListener.onScrollStateChanged(view, scrollState);
            int count = mListView.getAdapter() == null ? 0 : mListView.getAdapter().getCount();
//            count += mListView.getHeaderViewsCount()+mListView.getFooterViewsCount();
            // 下拉到空闲时，且最后一个item的数等于数据的总数时，进行更新
            if (mLastItem >= count && scrollState == SCROLL_STATE_IDLE) {
                if ( getNextPage() <= 0) {
                    //TODO: 加载到最后一页
//                    ToastUtil.show(view.getContext(), R.string.is_end_page);
                    return;
                }
                if (totalPage > 0 && getNextPage() > totalPage) {
//                    ToastUtil.show(view.getContext(), R.string.is_end_page);
                    return;
                }
                if (l != null && !isLoading) {
                    pushLoadMore();
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            mLastItem = firstVisibleItem + visibleItemCount;
            if (mListener != null)
                mListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    };

    public void pushLoadMore() {
        if (isEndPage()) return;
        updateFooterView();
        mFooterView.setText("正在加载数据....");
        isLoading = true;
        l.loadMore(getNextPage());
    }

    /**
     * 创建footView
     */
    private void addFooterView() {
        if (mFooterView != null) {
            return;
        }
        isLoading = false;
        mFooterView = new TextView(mListView.getContext());
        mFooterView.setGravity(Gravity.CENTER);
        int pix = (int) getResources().getDimension(R.dimen.row_text_button_padding);
        mFooterView.setPadding(0, pix, 0, pix);
        mFooterView.setMinimumHeight(pix * 8);
        mFooterView.setVisibility(View.INVISIBLE);
        mFooterView.setText("点击加载更多");
        mListView.addFooterView(mFooterView);
        updateFooterView();
        mFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoading) return;
                pushLoadMore();
            }
        });
    }


    /**
     * 更新footerView
     */
    private void updateFooterView() {
        if (isEndPage()) {
            mFooterView.setVisibility(View.INVISIBLE);
            return;
        }
        mFooterView.setText("点击加载更多");
        mFooterView.setVisibility(View.VISIBLE);
        isLoading = false;
    }

    public static interface ILoadMoreListener {
        public void loadMore(int nextPage);
    }
}
