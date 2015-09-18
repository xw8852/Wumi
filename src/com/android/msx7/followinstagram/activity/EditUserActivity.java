package com.android.msx7.followinstagram.activity;

import android.os.Bundle;
import android.view.View;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.common.BaseActivity;

import java.util.HashMap;

/**
 * Created by Josn on 2015/9/17.
 */
public class EditUserActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        getTitleBar().setTitle("编辑个人资料", null);
        getTitleBar().setLeftBtn("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getTitleBar().setRightBtn("完成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    void submit() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    /**
     * ## 返回该用户名的用户信息
     * #_item = {}
     * #_item['type'] = 'queryuname'
     * #_item['s_user_name'] = '大水牛'
     * #response = bt.httpget(_url = myurl, _params = _item)
     * #print response.status
     * #print json.dumps(json.loads(response.read()), indent = 4)
     * <p/>
     * ## 返回结果
     * ## {
     * ##     "message": "OK",
     * ##     "retcode": 0,
     * ##     "retbody": {
     * ##         "i_status": 0,
     * ##         "s_user_name": "\u5927\u6c34\u725b",
     * ##         "s_user_image": "http://pic.yooho.me/p/22_abs",
     * ##         "i_user_id": 1,
     * ##         "s_address": ""
     * ##     }
     * ## }
     */
    void loadData() {
        showLoadingDialog(-1);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("type", "queryuname");
        map.put("s_user_name", IMApplication.getApplication().getUserInfo().userName);
    }


}
