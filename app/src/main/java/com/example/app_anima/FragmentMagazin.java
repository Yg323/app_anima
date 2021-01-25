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
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_1),"어깨관절", 1);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_2),"골반", 2);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_3),"스트레칭", 3);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_4),"쿠키 스트레칭", 4);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_5),"뒷다리 체중 이동", 5);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_6),"세 다리 서있기", 6);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_7),"원형 돌기", 7);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_8),"한 쪽으로 서기", 8);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_9),"목 굽히기", 9);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_10),"목 펴기", 10);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_11),"가슴 늘리기 운동", 11);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_12),"발목 관절 굽히기, 펴기", 12);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_13),"월 배로우", 13);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_14),"뒷다리 올리기", 14);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_15),"옆으로 걷기", 15);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_16),"팔굽혀 펴기", 16);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_17),"엎드렸다 앉기", 17);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_18),"스텝온 : 밸런스 패드", 18);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_19),"스텝온 : 계단", 19);
        addItem(ContextCompat.getDrawable(getActivity(),R.drawable.trainthumb_20),"스텝온 : 도넛볼", 20);

        recyTrainAdapter.setOnItemClickListener(new RecyTrainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) { // 일단 서버 안쓰니까 숫자로 넘겨서 받겠음
                Intent intent = new Intent(getActivity(), TrainingActivity.class);
                intent.putExtra("id", mList.get(pos).getId());
                intent.putExtra("title", mList.get(pos).getTitle());
                startActivity(intent);
            }
        });

        return viewGroup;
    }

    public void addItem(Drawable icon, String title, int id) {
        RecyTrainItem item = new RecyTrainItem();
        item.setId(id);
        item.setDrawable(icon);
        item.setTitle(title);
        mList.add(item);
    }
}
