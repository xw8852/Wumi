package com.android.msx7.followinstagram.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.activity.ImgFindUserActivity.SimpleContact;
import com.android.msx7.followinstagram.common.BaseActivity;
import com.android.widget.PinnedAdapter;
import com.android.widget.PinnedHeaderListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josn on 2015/9/20.
 */
public class ContactActivity extends BaseActivity {
    PinnedHeaderListView listView;
    ContactAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contact);
        getTitleBar().setTitle("通讯录", null);
        addBack();
        listView = (PinnedHeaderListView)findViewById(R.id.pinListView);
        View _view = findViewById(R.id.pin_header);
        _view.setVisibility(View.INVISIBLE);
        listView.setPinHeader(_view);
        mAdapter = new ContactAdapter(this, new ArrayList<SimpleContact>());
        listView.setAdapter(mAdapter);
    }



    class ContactAdapter extends PinnedAdapter<SimpleContact> {
        public ContactAdapter(Context ctx, List<SimpleContact> data) {
            super(ctx, data);
        }

        public ContactAdapter(Context ctx, SimpleContact... data) {
            super(ctx, data);
        }

        @Override
        public void configHeaderView(int position, View header) {

        }

        @Override
        public View getView(int position, View convertView, LayoutInflater inflater) {
            return null;
        }
    }

}
