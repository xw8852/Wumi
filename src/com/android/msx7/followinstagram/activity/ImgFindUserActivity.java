package com.android.msx7.followinstagram.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.msx7.followinstagram.IMApplication;
import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.common.BaseActivity;
import com.android.msx7.followinstagram.common.BaseAdapter;
import com.android.msx7.followinstagram.ui.actionbar.ActionBar;
import com.android.msx7.followinstagram.util.InputKeyBoardUtils;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.ViewUtils;
import com.faceplusplus.api.FaceDetecter;
import com.faceplusplus.api.FaceDetecter.Face;
import com.facepp.http.HttpRequests;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josn on 2015/9/13.
 */
public class ImgFindUserActivity extends BaseActivity {
    public static final String PARAM_PATH = "param_path";
    //    HandlerThread detectThread = null;
//    Handler detectHandler = null;
//    FaceDetecter detecter = null;
//    HttpRequests request = null;// 在线api
    String path;
    ImageView img;
    FrameLayout frameLayout;
    TextView mText;
    int bitmapWidth;
    int bitmapHeight;
    ListView listView;
    List<SimpleContact> data = new ArrayList<SimpleContact>();
    List<SimpleContact> quanList = new ArrayList<SimpleContact>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_face);
        addBack();
        img = (ImageView) findViewById(R.id.img);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        path = getIntent().getStringExtra(PARAM_PATH);
//        detectThread = new HandlerThread("detect");
//        detectThread.start();
//        detectHandler = new Handler(detectThread.getLooper());
//        detecter = new FaceDetecter();
//        detecter.init(this, "54662625acbadb3c5b395cd2d39c98d9");
        if (getIntent().hasExtra("data")) {
            quanList = new Gson().fromJson(getIntent().getStringExtra("data"), new TypeToken<List<SimpleContact>>() {
            }.getType());
        }
        IMApplication.getApplication().displayImage(path, img, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                L.d("---onLoadingComplete----");
                bitmapWidth = bitmap.getWidth();
                bitmapHeight = bitmap.getHeight();
                DisplayMetrics dm = getResources().getDisplayMetrics();
                int _width = dm.widthPixels;
                int _height = _width * bitmapHeight / bitmapWidth;
                ViewGroup.LayoutParams params = img.getLayoutParams();
                params.width = _width;
                params.height = _height;
                bitmapHeight = params.height;
                bitmapWidth = params.width;
                img.setLayoutParams(params);
                findFace(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
        mText = (TextView) findViewById(R.id.text);
        searchView = findViewById(R.id.search_bar);
        editText = (EditText) searchView.findViewById(R.id.action_bar_search_edit_text);
        btn = (TextView) searchView.findViewById(R.id.clear);
        searchView.setVisibility(View.GONE);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionBar();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString().trim())) return;
                mAdapter.changeData(findName(s.toString()));
            }
        });
        getTitleBar().setTitle("圈人", null);
        mAdapter = new SimpleAdapter(this, new ArrayList<SimpleContact>());
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentTag.setText(mAdapter.getItem(position));
                showActionBar();
            }
        });

        getTitleBar().setRightBtn("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<SimpleContact> arrs = new ArrayList<SimpleContact>();
                int count = frameLayout.getChildCount();
                for (int i = 0; i < count; i++) {
                    View _tmp = frameLayout.getChildAt(i);
                    if (_tmp instanceof Tag) {
                        Tag _tag = (Tag) _tmp;
                        SimpleContact contact = _tag.simpleContact;
                        if (contact == null) continue;
                        float wCenter = _tag.getWidth() / 2.0f;
                        contact.x = _tag.getX() / bitmapWidth;
                        contact.centerX = (_tag.getX() + wCenter) / bitmapWidth;
                        contact.y = (_tag.getY() - img.getTop()) / bitmapHeight;
                        Rect rect = new Rect();
                        _tag.getHitRect(rect);
                        L.d("__________FACE___" + new Gson().toJson(contact));
                        L.d("____bitmap_____FACE___" + bitmapWidth + "," + bitmapHeight);
                        L.d("____bitmap_____FACE___" + _tag.getX() + "," + _tag.getY());
                        L.d("______TAG__FACE____" + rect + _tag.getWidth());
                        arrs.add(contact);
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("data", new Gson().toJson(arrs));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public void showActionBar() {
        getTitleBar().setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        mAdapter.clear();
        InputKeyBoardUtils.autoDismiss(ImgFindUserActivity.this);
        editText.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0); //强制隐藏键盘
    }


    SimpleAdapter mAdapter;

    @Override
    public void onBackPressed() {
//        if (listView.getVisibility() == View.VISIBLE) {
//            showActionBar();
//            return;
//        }
//        List<SimpleContact> arrs = new ArrayList<SimpleContact>();
//        int count = frameLayout.getChildCount();
//        for (int i = 0; i < count; i++) {
//            View _tmp = frameLayout.getChildAt(i);
//            if (_tmp instanceof Tag) {
//                Tag _tag = (Tag) _tmp;
//                SimpleContact contact = _tag.simpleContact;
//                if (contact == null) continue;
//                contact.x = bitmapWidth / (_tag.getX() - img.getLeft());
//                contact.y = bitmapHeight / (_tag.getY() - img.getTop());
//                arrs.add(contact);
//            }
//        }
        Intent intent = new Intent();
//        intent.putExtra("data", new Gson().toJson(arrs));
        setResult(RESULT_OK, intent);
        finish();
    }

    class SimpleAdapter extends BaseAdapter<SimpleContact> {
        public SimpleAdapter(Context ctx, List<SimpleContact> data) {
            super(ctx, data);
        }

        public SimpleAdapter(Context ctx, SimpleContact... data) {
            super(ctx, data);
        }

        @Override
        public View getView(int position, View convertView, LayoutInflater inflater) {
            TextView textView;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.action_bar_button_text, null);
                convertView.setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.action_bar_height));
            }
            textView = (TextView) convertView;
            textView.setText(getItem(position).name + "(" + getItem(position).phone + ")");
            return convertView;
        }
    }

    View searchView;
    EditText editText;
    TextView btn;
    boolean layout;

    public void findFace(final Bitmap bitmap) {
        L.d("---findFace----");
        final GestureDetector mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (frameLayout.getChildCount() < 5) {
                    Tag tag = new Tag(ImgFindUserActivity.this);
                    ViewUtils.measureView(tag);
                    int width = tag.getMeasuredWidth();
                    FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layout.leftMargin = (int) e.getX() - width / 2;
                    if (layout.leftMargin + width > getResources().getDisplayMetrics().widthPixels) {
                        layout.leftMargin = getResources().getDisplayMetrics().widthPixels - width;
                    }
                    L.d("TOP---" + img.getTop());
                    L.d("TOP---" + img.getHeight() + "," + bitmapHeight);

                    layout.topMargin = (int) e.getY() + img.getTop();
                    tag.setLayoutParams(layout);
                    tag.setVisibility(View.VISIBLE);
                    frameLayout.addView(tag);
                }
                return true;
            }
        });
        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                if (!quanList.isEmpty()) {
                    for (SimpleContact contact : quanList) {
                        // contact.position = bitmapWidth / _tag.getX() + "ABCD" + bitmapHeight / _tag.getY();
                        if (!(contact.x >= 0 && contact.x <= 1)) continue;

                        Tag tag = new Tag(ImgFindUserActivity.this);
                        ViewUtils.measureView(tag);
                        int width = tag.getMeasuredWidth();
                        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layout.leftMargin = (int) (bitmapWidth * contact.x);
                        if (layout.leftMargin + width > getResources().getDisplayMetrics().widthPixels) {
                            layout.leftMargin = getResources().getDisplayMetrics().widthPixels - width;
                        }
                        layout.topMargin = (int) (img.getTop() + bitmapHeight * contact.y);
                        L.d("TOP---" + img.getTop());
                        L.d("TOP---" + img.getHeight() + "," + bitmapHeight);
                        tag.setText(contact);
                        tag.setLayoutParams(layout);
                        tag.setVisibility(View.VISIBLE);
                        frameLayout.addView(tag);
                    }
                }
            }
        }, 500);
//        detectHandler.post(new Runnable() {
//
//            @Override
//            public void run() {
//                L.d("---findFace---detectHandler---");
//                Face[] faceinfo = detecter.findFaces(bitmap);// 进行人脸检测
//                L.d("---findFace---faceinfo---");
//                if (faceinfo == null) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(ImgFindUserActivity.this, "未发现人脸信息", Toast.LENGTH_LONG).show();
//                        }
//                    });
//                    return;
//                }
//                L.d("MSG--" + new Gson().toJson(faceinfo));
//                Message msg = new Message();
//                msg.what = 100;
//                msg.obj = faceinfo;
//                handler.sendMessage(msg);
//            }
//        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                int width = 0;
                Face[] faceinfo = (Face[]) msg.obj;
                if (faceinfo != null && faceinfo.length > 0) {
                    for (Face face : faceinfo) {
                        Tag tag = new Tag(ImgFindUserActivity.this);
                        if (width == 0) {
                            ViewUtils.measureView(tag);
                            width = tag.getMeasuredWidth();
                        }
                        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layout.leftMargin = (int) (bitmapWidth * (face.left + face.right)) / 2 - width / 2;
                        if (layout.leftMargin + width > getResources().getDisplayMetrics().widthPixels) {
                            layout.leftMargin = getResources().getDisplayMetrics().widthPixels - width;
                        }
                        layout.topMargin = (int) (bitmapHeight * face.bottom) + img.getTop();
                        tag.setLayoutParams(layout);
                        tag.setVisibility(View.VISIBLE);
                        frameLayout.addView(tag, 1);
                    }
                }

            }
        }
    };


    /**
     * // 获取手机联系人
     * Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
     */
    private static final String[] PHONES_PROJECTION = new String[]{
            Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID};

    public List<SimpleContact> findName(String name) {
        List<SimpleContact> arr = new ArrayList<SimpleContact>();
        ContentResolver resolver = getContentResolver();
        String selection = Phone.DISPLAY_NAME + " like '%" + name + "%'or " + Phone.NUMBER + " like '%" + name + "%'";
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, selection, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                //得到联系人名称
                String contactName = phoneCursor.getString(0);
                String contactNumber = phoneCursor.getString(1);
                //得到联系人ID
                Long contactid = phoneCursor.getLong(2);
                L.d("msg:" + contactName + "," + contactNumber + "," + contactid);
                arr.add(new SimpleContact(contactName, contactNumber));
            }
            phoneCursor.close();
        }
        return arr;
    }

    public static class SimpleContact {
        @SerializedName("name")
        public String name;
        @SerializedName("phone_no")
        public String phone;
        @SerializedName("position")
        public String position;
        @SerializedName("i_user_id")
        public long userId = -1;
        @SerializedName("x")
        public float x = -1;
        @SerializedName("y")
        public float y = -1;
        @SerializedName("centerX")
        public float centerX;

        //  # 姓名首字母
        @SerializedName("s_firstchar")
        public String s_firstchar;
        //# 姓名每个汉子的首字母
        @SerializedName("s_pinyin_abs")
        public String s_pinyin_abs;
        // # 姓名全拼
        @SerializedName("s_pinyin")
        public String s_pinyin;
        //# 姓名
        @SerializedName("s_ctt_uname")
        public String s_ctt_uname;
        //# 格式化后的电话号码
        @SerializedName("i_ctt_telno")
        public String i_ctt_telno;


        public SimpleContact() {
        }

        public SimpleContact(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }
    }

    public void showSearch() {
        startActivityForResult(new Intent(this, ContactActivity.class), 100);
//        listView.setVisibility(View.VISIBLE);
//        getTitleBar().setVisibility(View.GONE);
//        searchView.setVisibility(View.VISIBLE);
//        editText.setText("");
//        editText.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);   //显示键盘
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.hasExtra("data")) {
            SimpleContact contact = new Gson().fromJson(data.getStringExtra("data"), SimpleContact.class);
            contact.name = contact.s_ctt_uname;
            contact.phone = contact.i_ctt_telno;
            if (currentTag != null) currentTag.setText(contact);
        }
        currentTag = null;
    }

    Tag currentTag;

    class Tag extends LinearLayout {
        SimpleContact simpleContact;

        public Tag(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.layout_img_tag, this);
            init();
            ((TextView) findViewById(R.id.text)).setHint("点我圈人");
        }

        boolean isclick;

        void init() {
            setOnTouchListener(new TagTouchListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isclick) {
                        isclick = false;
                        return;
                    }
                    showSearch();
                    currentTag = Tag.this;
                }
            }));
            findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.removeView(Tag.this);
                }
            });
        }


        public void setText(SimpleContact simpleContact) {
            this.simpleContact = simpleContact;
            ((TextView) findViewById(R.id.text)).setText(simpleContact.name);
        }

        class TagTouchListener implements View.OnTouchListener {
            float offset = 10;
            float last_x, last_y;
            float dx, dy;
            FrameLayout.LayoutParams layout;
            RectF rect = null;
            View.OnClickListener listener;

            public TagTouchListener(OnClickListener listener) {
                this.listener = listener;
            }

            long curtime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        layout = (FrameLayout.LayoutParams) v.getLayoutParams();
                        last_x = event.getRawX();
                        last_y = event.getRawY();
                        curtime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - curtime < ViewConfiguration.getTapTimeout()) {
                            listener.onClick(v);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float distanceX = last_x - event.getRawX();
                        float distanceY = last_y - event.getRawY();
                        float nextY = v.getY() - distanceY;
                        float nextX = v.getX() - distanceX;

                        if (rect == null) {
                            rect = new RectF(0, img.getTop(), getResources().getDisplayMetrics().widthPixels - v.getWidth(), img.getBottom() - v.getHeight());
                        }
                        if (!rect.contains(nextX, nextY)) {
                            nextX = Math.min(rect.right, nextX);
                            nextX = Math.max(rect.left, nextX);
                            nextY = Math.max(rect.top, nextY);
                            nextY = Math.min(rect.bottom, nextY);
                        }
                        // 属性动画移动
                        ObjectAnimator y = ObjectAnimator.ofFloat(v, "y", v.getY(), nextY);
                        ObjectAnimator x = ObjectAnimator.ofFloat(v, "x", v.getX(), nextX);

                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(x, y);
                        animatorSet.setDuration(0);
                        animatorSet.start();

                        last_x = event.getRawX();
                        last_y = event.getRawY();
                        isclick = true;
                }
                return true;
            }
        }

    }

}
