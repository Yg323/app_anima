package com.example.app_anima;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentHome extends Fragment {
    private Button btn_test;
    private ImageButton btn_menu;
    private ScrollView scrollView;
    private LinearLayout appbar, vp_layout;
    private TextView tv_menu;

    private ArrayList<Drawable> mList;
    private ViewPager viewPager;
    private ADScrollAdapter adScrollAdapter;
    private boolean load = false;
    private Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
    private DrawerLayout drawer;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        btn_test = (Button) viewGroup.findViewById(R.id.button2);
        btn_menu = (ImageButton) viewGroup.findViewById(R.id.btn_menu);
        scrollView = (ScrollView) viewGroup.findViewById(R.id.sv_main);
        appbar = (LinearLayout) viewGroup.findViewById(R.id.appbar);
        tv_menu = (TextView) viewGroup.findViewById(R.id.tv_menu);
        viewPager = (ViewPager) viewGroup.findViewById(R.id.viewPager);
        vp_layout = (LinearLayout) viewGroup.findViewById(R.id.vp_layout);

        //drawer
        drawer = (DrawerLayout) viewGroup.findViewById(R.id.drawer) ;
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.openDrawer(Gravity.LEFT) ;
                }
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT) ;
                }
            }
        });

        mList = new ArrayList<Drawable>();
        mList.add(ResourcesCompat.getDrawable(getResources(),R.drawable.img_ad1,null));
        mList.add(ResourcesCompat.getDrawable(getResources(),R.drawable.img_ad2,null));
        mList.add(ResourcesCompat.getDrawable(getResources(),R.drawable.img_ad3,null));
        mList.add(ResourcesCompat.getDrawable(getResources(),R.drawable.img_ad4,null));
        adScrollAdapter = new ADScrollAdapter(getContext(),mList);
        viewPager.setAdapter(adScrollAdapter);

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            @Override
            public void run() {
                if(load) {
                    if (viewPager.getCurrentItem() == adScrollAdapter.getCount() - 1) {
                        viewPager.setCurrentItem(0, true);
                    } else {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    }
                }
                else{
                    load = true;
                    viewPager.setCurrentItem(0, true);
                }
            }

        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }

        }, DELAY_MS, PERIOD_MS);


        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY(); // For ScrollView
                if(scrollY>=vp_layout.getBottom()){
                    appbar.setBackgroundColor(Color.parseColor("#E7D0C8"));
                    tv_menu.setVisibility(View.VISIBLE);
                    btn_menu.setColorFilter(Color.parseColor("#000000"));
                }
                else {
                    appbar.setBackgroundColor(Color.parseColor("#00E7D0C8"));
                    tv_menu.setVisibility(View.INVISIBLE);
                    btn_menu.setColorFilter(Color.parseColor("#FFFFFF"));
                }
            }
        });
        return viewGroup;
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }
}
