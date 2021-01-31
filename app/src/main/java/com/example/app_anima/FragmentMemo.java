package com.example.app_anima;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class FragmentMemo extends Fragment {
    ViewGroup viewGroup;
    private BarChart runChart, waterChart;
    private LineChart lineChart;
    private final String[] timeLabelList = new String[]{"AM12", "AM1", "AM2", "AM3", "AM4", "AM5", "AM6", "AM7", "AM8", "AM9", "AM10", "AM11",
            "PM12", "PM1", "PM2", "PM3", "PM4", "PM5", "PM6", "PM7", "PM8", "PM9", "PM10", "PM11", "AM12"};


    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_memo, container, false);
        runChart = viewGroup.findViewById(R.id.barChart);
        waterChart = viewGroup.findViewById(R.id.barChartWater);
        lineChart = viewGroup.findViewById(R.id.lineChart);
        drawRunChart();
        drawWaterChart();
        drawLineChart();

        return viewGroup;
    }

    public void drawRunChart() {
        //막대그래프
        int[] runValue = {1000, 5008, 9000, 5000, 3000, 2000, 1000, 5008, 9000, 5000, 3000, 2000, 1000, 5008, 9000, 5000, 3000, 2000, 1000, 5008, 9000, 5000, 3000, 2000, 1000};
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < runValue.length; i++) entries.add(new BarEntry(i, runValue[i]));

        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColors(Color.parseColor("#86E57F"));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth((float) 0.5);//막대그래프 너비 설정

        runChart.setData(barData);
        runChart.setFitBars(true);

        runChart.getDescription().setEnabled(false); //차트옆에 표기된 description 안보이게 설정
        runChart.setPinchZoom(false); //줌인아웃 설정

        runChart.setDrawGridBackground(false); //격자구조 여부
        runChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); //x축을 아래에 두기
        runChart.getAxisLeft().setDrawAxisLine(false); //축과 나란한 선을 그리는지
        runChart.getAxisLeft().setDrawGridLines(false); //격자 선을 그릴수 있는지
        runChart.getAxisLeft().setDrawLabels(false); //축의 레이블을 그릴수 있는지
        runChart.getAxisRight().setDrawAxisLine(false); //축과 나란한 선을 그리는지
        runChart.getAxisRight().setDrawGridLines(false); //격자 선을 그릴수 있는지
        runChart.getAxisRight().setDrawLabels(false); //축의 레이블을 그릴수 있는지

        runChart.setHighlightPerTapEnabled(false); //하이라이트 없애기
        runChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(timeLabelList)); //라벨 설정
        runChart.getXAxis().setGranularity(12f); //라벨 표시 설정!! 6씩 표현
        runChart.setVisibleXRangeMaximum(25); //x축 표시 범위

        runChart.getLegend().setEnabled(false); //차트 범례 설정

        runChart.invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void drawLineChart() {
        ArrayList<Entry> entries = new ArrayList<>();
        float[] tempValue = {25.0f, 26.0f, 27.0f, 26.3f, 25.1f, 25.6f, 25.0f, 25.8f, 27.0f, 27.2f, 28.0f, 23.0f, 23.5f, 25.0f, 25.1f, 25.2f, 25.3f, 25.4f, 25.5f, 25.6f, 25.7f, 25.0f, 25.0f, 25.0f, 25.0f};
        for (int i = 0; i < tempValue.length; i++) entries.add(new BarEntry(i, tempValue[i]));
        LineDataSet lineDataSet = new LineDataSet(entries, "label");
        lineDataSet.setColor(Color.RED); //스타일 지정
        lineDataSet.setValueTextColor(Color.BLUE); //스타일 지정

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false); //차트옆에 표기된 description 안보이게 설정
        lineChart.getLegend().setEnabled(false); //차트 범례 설정
        lineChart.getXAxis().setDrawGridLines(false); //격자 선을 그릴수 있는지
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); //x축을 아래에 두기
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(timeLabelList)); //라벨 설정
        lineChart.getXAxis().setGranularity(6f); //라벨 표시 설정!!!!! 이거 수정수정수정
        lineChart.getAxisLeft().setDrawAxisLine(false); //축과 나란한 선을 그리는지
        lineChart.getAxisLeft().setDrawGridLines(false); //격자 선을 그릴수 있는지
        lineChart.getAxisLeft().setDrawLabels(false); //축의 레이블을 그릴수 있는지
        lineChart.getAxisLeft().setAxisMinimum(20f); //최소값
        lineChart.getAxisLeft().setAxisMaximum(30f); //최대값
        lineChart.getAxisRight().setEnabled(false); //오른쪽 데이터 비활성화
        lineChart.setDoubleTapToZoomEnabled(false); //더블탭시 확대 설정
        lineChart.moveViewToX(lineData.getEntryCount()); //맨 오른쪽으로 옮기기
        lineChart.setHighlightPerTapEnabled(false); //하이라이트 없애기


        lineChart.setVisibleXRangeMaximum(25); //x축 표시 범위

        lineChart.invalidate(); //refresh
    }

    public void drawWaterChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        final ArrayList<String> labelList = new ArrayList<>();
        entries.add(new BarEntry(0, 500));
        entries.add(new BarEntry(1, 100));
        entries.add(new BarEntry(2, 700));
        entries.add(new BarEntry(3, 300));
        entries.add(new BarEntry(4, 1000));
        entries.add(new BarEntry(5, 600));
        entries.add(new BarEntry(6, 2000));
        labelList.add("월");
        labelList.add("화");
        labelList.add("수");
        labelList.add("목");
        labelList.add("금");
        labelList.add("토");
        labelList.add("일");

        BarDataSet barDataSet = new BarDataSet(entries, " ");
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
