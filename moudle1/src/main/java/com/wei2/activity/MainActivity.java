package com.wei2.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.wei2.bikenavi.R;
import com.wei2.pager.LocationPager;
import com.wei2.pager.MyPager;
import com.wei2.pager.NavigationPager;
import com.wei2.pager.ProtectionPager;


import java.util.ArrayList;


public class MainActivity extends Activity {

    int mRadioButtons[] = {R.id.rb_location, R.id.rb_navigation, R.id.rb_protection};
    ArrayList<RadioButton> mRadioButtonList;
    private RadioGroup radioGroup;
    private ViewPager viewPager;
    private int curPagePosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
//获取单选按钮组
        mRadioButtonList = new ArrayList<>();
        radioGroup = findViewById(R.id.rg_tab);
        for (int i = 0; i < mRadioButtons.length; i++) {
            mRadioButtonList.add((RadioButton) findViewById(mRadioButtons[i]));
        }
        viewPager = findViewById(R.id.vp_content);
        viewPager.setOffscreenPageLimit(viewPager.getOffscreenPageLimit() + 2);
        viewPager.setAdapter(new MyAdapter());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                curPagePosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_location:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_navigation:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.rb_protection:
                        viewPager.setCurrentItem(2);
                        break;
//                    case R.id.rb_my:
//                        viewPager.setCurrentItem(3);
//                        break;
                }
            }
        });
    }

    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mRadioButtons.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = null;
            switch (position) {
                case 0:
                    view = new LocationPager(MainActivity.this).mRootView;

                    container.addView(view);
                    break;
                case 1:
                    view = new NavigationPager(MainActivity.this).mRootView;
                    container.addView(view);
                    break;
                case 2:
                    view = new ProtectionPager(MainActivity.this).mRootView;
                    container.addView(view);
                    break;
                case 3:
                    view = new MyPager(MainActivity.this).mRootView;
                    container.addView(view);
                    break;
            }
            return view;
        }

        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPager=null;
    }
}
