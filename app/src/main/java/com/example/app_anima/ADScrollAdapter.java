package com.example.app_anima;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ADScrollAdapter extends PagerAdapter {

    Context context;
    ArrayList<Drawable> data;

    public ADScrollAdapter(Context context, ArrayList<Drawable> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = null;
        //뷰페이지 슬라이딩 할 레이아웃 인플레이션
        if(context != null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.ad_viewpager, null);
            ImageView image_container = (ImageView) v.findViewById(R.id.image_container);
            Glide.with(context).load(data.get(position)).into(image_container);
        }
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
