package com.example.app_anima;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class FragmentHome extends Fragment {
    private Button btn_test;
    private ImageButton btn_menu;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private TextView tv_menu;

    private AutoScrollViewPager autoViewPager;
    private ArrayList<Drawable> mList;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        btn_test = (Button) viewGroup.findViewById(R.id.button2);
        btn_menu = (ImageButton) viewGroup.findViewById(R.id.btn_menu);
        scrollView = (ScrollView) viewGroup.findViewById(R.id.sv_main);
        linearLayout = (LinearLayout) viewGroup.findViewById(R.id.appbar);
        tv_menu = (TextView) viewGroup.findViewById(R.id.tv_menu);
        autoViewPager = viewGroup.findViewById(R.id.autoViewPager);

        mList = new ArrayList<Drawable>();
        mList.add(ResourcesCompat.getDrawable(getResources(),R.drawable.img_ad1,null));
        mList.add(ResourcesCompat.getDrawable(getResources(),R.drawable.img_ad2,null));
        mList.add(ResourcesCompat.getDrawable(getResources(),R.drawable.img_ad3,null));
        mList.add(ResourcesCompat.getDrawable(getResources(),R.drawable.img_ad4,null));


        ADScrollAdapter adScrollAdapter = new ADScrollAdapter(getContext(),mList);
        autoViewPager.setAdapter(adScrollAdapter); //Auto Viewpager에 Adapter 장착
        autoViewPager.setInterval(5000); // 페이지 넘어갈 시간 간격 설정
        autoViewPager.startAutoScroll(); //Auto Scroll 시작

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY(); // For ScrollView
                int scrollX = scrollView.getScrollX(); // For HorizontalScrollView
                if(scrollY>=btn_test.getBottom()){
                    linearLayout.setBackgroundColor(Color.parseColor("#E7D0C8"));
                    tv_menu.setVisibility(View.VISIBLE);
                    btn_menu.setColorFilter(Color.parseColor("#000000"));
                }
                else {
                    linearLayout.setBackgroundColor(Color.parseColor("#00E7D0C8"));
                    tv_menu.setVisibility(View.INVISIBLE);
                    btn_menu.setColorFilter(Color.parseColor("#FFFFFF"));
                }
            }
        });
        return viewGroup;
    }


}
