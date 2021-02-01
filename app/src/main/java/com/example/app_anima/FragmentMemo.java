package com.example.app_anima;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FragmentMemo extends Fragment {
    ViewGroup viewGroup;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private Button btn_day, btn_week;

    private Calendar cal;
    private BarChart runChart, waterChart;
    private LineChart lineChart;
    private final String[] timeLabelList = new String[]{"AM12", "AM1", "AM2", "AM3", "AM4", "AM5", "AM6", "AM7", "AM8", "AM9", "AM10", "AM11",
            "PM12", "PM1", "PM2", "PM3", "PM4", "PM5", "PM6", "PM7", "PM8", "PM9", "PM10", "PM11", "AM12"};

    private int[] stepSums = new int[24];
    private float[] tempValues = new float[24];
    private int hour;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_memo, container, false);
        runChart = viewGroup.findViewById(R.id.barChart);
        waterChart = viewGroup.findViewById(R.id.barChartWater);
        lineChart = viewGroup.findViewById(R.id.lineChart);
        btn_day = viewGroup.findViewById(R.id.btn_day);
        btn_week = viewGroup.findViewById(R.id.btn_week);

        cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR);
        dbHelper = new DBHelper(getContext());
        processingStep();
        processingTemp();

        drawDayRunChart();
        drawWaterChart();
        drawLineChart();

        btn_day.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawDayRunChart();
            }
        });

        btn_week.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawWeekRunChart();
            }
        });

        return viewGroup;
    }

    public void processingStep(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(Calendar.getInstance().getTime());

        dbHelper = new DBHelper(getContext());
        db = dbHelper.getReadableDatabase();

        for (int i = 0; i < hour + 1; i++) {
            String newDate;
            int stepSum = 0;

            if(i<10) newDate = date + " 0" + i;
            else newDate = date + " " + i;

            Cursor cursor = db.rawQuery("SELECT stepcnt FROM steptable WHERE writedate LIKE '"+newDate+"%'", null);
            while (cursor.moveToNext()){
                int stepCnt = cursor.getInt(0);
                stepSum += stepCnt;
            }
            stepSums[i] = stepSum;
        }

        db.close();

    }

    public void processingTemp(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(Calendar.getInstance().getTime());

        dbHelper = new DBHelper(getContext());
        db = dbHelper.getReadableDatabase();

        for (int i = 0; i <= hour + 1; i++) {
            String newDate;
            int cnt = 0;
            float tempSum = (float) 0.00;
            if(i<10) newDate = date + " 0" + i;
            else newDate = date + " " + i;

            Cursor cursor = db.rawQuery("SELECT tempvalue FROM temptable WHERE writedate LIKE '"+newDate+"%'", null);
            while (cursor.moveToNext()){
                float tempValue = cursor.getFloat(0);
                cnt++;
                tempSum += tempValue;
            }
            if(cnt == 0){
                tempValues[i] = (float) 27.0;
            }else{
                tempValues[i] = (float) tempSum / cnt;
            }

        }
        db.close();

    }

    public void drawDayRunChart() {
        //막대그래프

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i <= hour; i++) entries.add(new BarEntry(i, stepSums[i]));

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
        runChart.getXAxis().setGranularity(1f); //라벨 표시 설정!! 6씩 표현
        runChart.setVisibleXRangeMaximum(25); //x축 표시 범위

        runChart.getLegend().setEnabled(false); //차트 범례 설정

        runChart.invalidate();
    }

    public void drawWeekRunChart() {
        //막대그래프

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i <= hour; i++) entries.add(new BarEntry(i, stepSums[i]));

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
        runChart.getXAxis().setGranularity(1f); //라벨 표시 설정!! 6씩 표현
        runChart.setVisibleXRangeMaximum(25); //x축 표시 범위

        runChart.getLegend().setEnabled(false); //차트 범례 설정

        runChart.invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void drawLineChart() {
        ArrayList<Entry> entries = new ArrayList<>();

        for (int i = 0; i <= hour ; i++) entries.add(new BarEntry(i, tempValues[i]));
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
        lineChart.getXAxis().setGranularity(1f); //라벨 표시 설정!!!!! 이거 수정수정수정
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
