package com.wei2.pager;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wei2.bikenavi.R;


public class MyPager {
    public Activity mActivity;
    public TextView tvTitle;
    public FrameLayout flContent;
    public final View mRootView;
    private View mContent;

    public MyPager(Activity activity){
        mActivity = activity;
        mRootView = initView();
    }

    public View initView(){
       View view =  View.inflate(mActivity, R.layout.pager, null);
        //标题部分
        tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText("个人中心");

       //内容部分
        mContent = View.inflate(mActivity, R.layout.pager_my,null);

        flContent = view.findViewById(R.id.fl_content);
        flContent.addView(mContent);

        return view;
    }
    public void initDate(){

    }
}
