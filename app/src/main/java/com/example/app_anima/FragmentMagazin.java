package com.example.app_anima;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FragmentMagazin extends Fragment {
    private RecyclerView recy_train;
    private RecyTrainAdapter recyTrainAdapter;
    ArrayList<RecyTrainItem> mList = new ArrayList<RecyTrainItem>();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_magazin, container, false);
        recy_train = (RecyclerView)viewGroup.findViewById(R.id.recy_train);
        recyTrainAdapter = new RecyTrainAdapter(mList);

        recy_train.setAdapter(recyTrainAdapter);
        recy_train.setLayoutManager(new LinearLayoutManager(viewGroup.getContext(),LinearLayoutManager.HORIZONTAL,false));

        //리사이클러뷰 아이템 추가
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.icon_playdog),"어깨관절");
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.icon_playdog),"골반");
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.icon_playdog),"스트레칭");
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.icon_playdog),"쿠키 스트레칭");
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.icon_playdog),"뒷다리 체중 이동");

        recyTrainAdapter.setOnItemClickListener(new RecyTrainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) { // 일단 서버 안쓰니까 숫자로 넘겨서 받겠음
                Intent intent = new Intent(getActivity(), TrainingActivity.class);
                intent.putExtra("index", pos);
                startActivity(intent);
            }
        });

        return viewGroup;
    }
    public void addItem(Drawable icon, String title) {
        RecyTrainItem item = new RecyTrainItem();
        item.setDrawable(icon);
        item.setTitle(title);
        mList.add(item);
    }
}
