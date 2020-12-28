package com.example.app_anima;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class FragmentMemo extends Fragment {
    ViewGroup viewGroup ;
    private BarChart barChart;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup =  (ViewGroup) inflater.inflate(R.layout.fragment_memo, container, false);
        barChart = viewGroup.findViewById(R.id.barChart);

        //막대그래프
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1,10));
        entries.add(new BarEntry(2,55));
        entries.add(new BarEntry(3,71));
        entries.add(new BarEntry(4,30));
        entries.add(new BarEntry(5,25));

        BarDataSet barDataSet = new BarDataSet(entries," ");
        barDataSet.setColors(Color.parseColor("#86E57F"));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth((float) 0.5);//막대그래프 너비 설정


        barChart.setData(barData);
        barChart.setFitBars(true);

        barChart.getDescription().setEnabled(false); //차트옆에 표기된 description 안보이게 설정
        barChart.setPinchZoom(false); //줌인아웃 설정
        barChart.setDrawGridBackground(false); //격자구조 여부
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); //x축을 아래에 두기
        barChart.getAxisLeft().setDrawLabels(false); // 왼쪽 y축 값 보이지 않게
        barChart.setTouchEnabled(false); //그래프 터치햇을때
        barChart.getLegend().setEnabled(false); //차트 범례 설정
        barChart.getAxisRight().setAxisMinimum(0f); //y축 최소값
        barChart.getAxisRight().setAxisMaximum(100f); //y축 최대값
        barChart.getAxisRight().setGranularity(50f); //y축 증가율 50
        barChart.getXAxis().setGranularity(1f);//x축 값 1씩 표시
        barChart.invalidate();


        return viewGroup;
    }

}
