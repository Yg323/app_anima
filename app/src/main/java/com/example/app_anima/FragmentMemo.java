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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class FragmentMemo extends Fragment {
    ViewGroup viewGroup ;
    private BarChart runChart, waterChart;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup =  (ViewGroup) inflater.inflate(R.layout.fragment_memo, container, false);
        runChart = viewGroup.findViewById(R.id.barChart);
        waterChart = viewGroup.findViewById(R.id.barChartWater);

        drawWaterChart();

        return viewGroup;
    }
    public void drawRunChart(){
        //막대그래프
        ArrayList<BarEntry> entries = new ArrayList<>();
        final ArrayList<String> labelList = new ArrayList<>();
        entries.add(new BarEntry(0,10));
        entries.add(new BarEntry(1,55));
        entries.add(new BarEntry(2,71));
        entries.add(new BarEntry(3,30));
        entries.add(new BarEntry(4,25));
        labelList.add("월"); labelList.add("화"); labelList.add("수"); labelList.add("목"); labelList.add("금");

        BarDataSet barDataSet = new BarDataSet(entries," ");
        barDataSet.setColors(Color.parseColor("#86E57F"));

        runChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labelList));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth((float) 0.5);//막대그래프 너비 설정


        runChart.setData(barData);
        runChart.setFitBars(true);

        runChart.getDescription().setEnabled(false); //차트옆에 표기된 description 안보이게 설정
        runChart.setPinchZoom(false); //줌인아웃 설정
        runChart.setDrawGridBackground(false); //격자구조 여부
        runChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); //x축을 아래에 두기
        runChart.getAxisLeft().setDrawLabels(false); // 왼쪽 y축 값 보이지 않게
        runChart.setTouchEnabled(false); //그래프 터치햇을때
        runChart.getLegend().setEnabled(false); //차트 범례 설정
        runChart.getAxisRight().setAxisMinimum(0f); //y축 최소값
        runChart.getAxisRight().setAxisMaximum(100f); //y축 최대값
        runChart.getAxisRight().setGranularity(50f); //y축 증가율 50
        runChart.getXAxis().setGranularity(1f);//x축 값 1씩 표시
        runChart.invalidate();
    }
    public void drawLineChart(){

    }
    public void drawWaterChart(){
        ArrayList<BarEntry> entries = new ArrayList<>();
        final ArrayList<String> labelList = new ArrayList<>();
        entries.add(new BarEntry(0,500));
        entries.add(new BarEntry(1,100));
        entries.add(new BarEntry(2,700));
        entries.add(new BarEntry(3,300));
        entries.add(new BarEntry(4,1000));
        entries.add(new BarEntry(5,600));
        entries.add(new BarEntry(6,2000));
        labelList.add("월"); labelList.add("화"); labelList.add("수"); labelList.add("목"); labelList.add("금"); labelList.add("토"); labelList.add("일");

        BarDataSet barDataSet = new BarDataSet(entries," ");
        barDataSet.setColors(Color.parseColor("#1070DE"));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth((float) 0.5);//막대그래프 너비 설정


        waterChart.setData(barData);
        waterChart.setFitBars(true);

        waterChart.getDescription().setEnabled(false); //차트옆에 표기된 description 안보이게 설정
        waterChart.setPinchZoom(false); //줌인아웃 설정
        waterChart.setDrawGridBackground(false); //격자구조 여부
        waterChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); //x축을 아래에 두기
        waterChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labelList)); //라벨 설정
        waterChart.getAxisLeft().setDrawLabels(false); // 왼쪽 y축 값 보이지 않게
        waterChart.setTouchEnabled(false); //그래프 터치햇을때
        waterChart.getLegend().setEnabled(false); //차트 범례 설정
        waterChart.invalidate();
    }
}
