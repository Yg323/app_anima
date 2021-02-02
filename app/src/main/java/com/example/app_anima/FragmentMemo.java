package com.example.app_anima;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FragmentMemo extends Fragment {
    ViewGroup viewGroup;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private Button btn_day, btn_week;
    private TextView tv_memo_food;

    private Calendar cal;
    private BarChart runChart, waterChart;
    private LineChart lineChart;
    private final String[] timeLabelList = new String[]{"AM12", "AM1", "AM2", "AM3", "AM4", "AM5", "AM6", "AM7", "AM8", "AM9", "AM10", "AM11",
            "PM12", "PM1", "PM2", "PM3", "PM4", "PM5", "PM6", "PM7", "PM8", "PM9", "PM10", "PM11", "AM12"};

    private final String[] waterDays = new String[]{"-", "-", "-", "-", "-", "-", "-"}, weekStepDays = new String[]{"-", "-", "-", "-", "-", "-", "-"};
    private int[] stepSums = new int[24], waterSums = new int[7], weekSteps = new int[7];
    private float[] tempValues = new float[24];
    private int hour;
    private String email;
    private String time;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_memo, container, false);
        runChart = viewGroup.findViewById(R.id.barChart);
        waterChart = viewGroup.findViewById(R.id.barChartWater);
        lineChart = viewGroup.findViewById(R.id.lineChart);
        tv_memo_food = viewGroup.findViewById(R.id.tv_memo_food);
        btn_day = viewGroup.findViewById(R.id.btn_day);
        btn_week = viewGroup.findViewById(R.id.btn_week);

        email = PreferenceManager.getString(getContext(), "userEmail");
        time = PreferenceManager.getString(getContext(), "date");
        cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR);
        dbHelper = new DBHelper(getContext());

        processingStep();
        processingTemp();
        processingWater();

        drawDayRunChart();
        drawLineChart();
        settingFood();

        btn_day.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawDayRunChart();
            }
        });

        btn_week.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                processingWeekStep();
            }
        });

        return viewGroup;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void processingStep() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(Calendar.getInstance().getTime());

        dbHelper = new DBHelper(getContext());
        db = dbHelper.getReadableDatabase();

        for (int i = 0; i < hour + 1; i++) {
            String newDate;
            int stepSum = 0;

            if (i < 10) newDate = date + " 0" + i;
            else newDate = date + " " + i;

            Cursor cursor = db.rawQuery("SELECT stepcnt FROM steptable WHERE writedate LIKE '" + newDate + "%'", null);
            while (cursor.moveToNext()) {
                int stepCnt = cursor.getInt(0);
                stepSum += stepCnt;
            }
            stepSums[i] = stepSum;
        }

        db.close();

    }

    public void processingTemp() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(Calendar.getInstance().getTime());

        dbHelper = new DBHelper(getContext());
        db = dbHelper.getReadableDatabase();

        for (int i = 0; i <= hour + 1; i++) {
            String newDate;
            int cnt = 0;
            float tempSum = (float) 0.00;
            if (i < 10) newDate = date + " 0" + i;
            else newDate = date + " " + i;

            Cursor cursor = db.rawQuery("SELECT tempvalue FROM temptable WHERE writedate LIKE '" + newDate + "%'", null);
            while (cursor.moveToNext()) {
                float tempValue = cursor.getFloat(0);
                cnt++;
                tempSum += tempValue;
            }
            if (cnt == 0) {
                tempValues[i] = (float) 27.0;
            } else {
                tempValues[i] = tempSum / cnt;
            }

        }
        db.close();

    }

    public void processingWeekStep() {
        StepRequest stepRequest = new StepRequest(email, stepResponseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(stepRequest);
        weekSteps[6] = PreferenceManager.getInt(getContext(), "run_step") + PreferenceManager.getInt(getContext(), "walk_step");
        weekStepDays[6] = getDateDay(time);
    }

    public void processingWater() {
        Log.d("processingWater", "실행");
        WaterRequest waterRequest = new WaterRequest(email, waterResponseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(waterRequest);
        waterSums[6] = PreferenceManager.getInt(getContext(), "water_count");
        waterDays[6] = getDateDay(time);
    }

    public void drawDayRunChart() {

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
        runChart.setVisibleXRangeMaximum(25); //x축 표시 범위
        runChart.setHighlightPerTapEnabled(false); //하이라이트 없애기
        runChart.setTouchEnabled(false); // 그래프 터치햇을때
        runChart.setDoubleTapToZoomEnabled(false); // 더블 탭으로 확대/축소 불가능하게
        runChart.getLegend().setEnabled(false); //차트 범례 설정

        XAxis xAxis = runChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //x축을 아래에 두기
        xAxis.setDrawAxisLine(false); // 축(축선)을 따라 선(축선)을 그려야하는 경우
        xAxis.setValueFormatter(new IndexAxisValueFormatter(timeLabelList)); //라벨 설정
        xAxis.setDrawGridLines(false); // no grid lines
        xAxis.setGranularity(1f); //라벨 표시 설정

        YAxis left = runChart.getAxisLeft();
        left.setDrawLabels(false); // 왼쪽 y축 값 보이지 않게
        left.setDrawAxisLine(false); // line X
        left.setDrawGridLines(false); // no grid lines
        runChart.getAxisRight().setEnabled(false); // no right axis

        runChart.invalidate();
    }

    public void drawWeekRunChart() {

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 7; i++) entries.add(new BarEntry(i, weekSteps[i]));

        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColors(Color.parseColor("#86E57F"));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth((float) 0.5);//막대그래프 너비 설정

        runChart.setData(barData);
        runChart.setFitBars(true);

        runChart.getDescription().setEnabled(false); //차트옆에 표기된 description 안보이게 설정
        runChart.setPinchZoom(false); //줌인아웃 설정
        runChart.setDrawGridBackground(false); //격자구조 여부
        runChart.setVisibleXRangeMaximum(25); //x축 표시 범위
        runChart.setHighlightPerTapEnabled(false); //하이라이트 없애기
        runChart.setTouchEnabled(false); // 그래프 터치햇을때
        runChart.setDoubleTapToZoomEnabled(false); // 더블 탭으로 확대/축소 불가능하게
        runChart.getLegend().setEnabled(false); //차트 범례 설정

        XAxis xAxis = runChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //x축을 아래에 두기
        xAxis.setDrawAxisLine(false); // 축(축선)을 따라 선(축선)을 그려야하는 경우
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weekStepDays)); //라벨 설정
        xAxis.setDrawGridLines(false); // no grid lines
        xAxis.setGranularity(1f); //라벨 표시 설정

        YAxis left = runChart.getAxisLeft();
        left.setDrawLabels(false); // 왼쪽 y축 값 보이지 않게
        left.setDrawAxisLine(false); // line X
        left.setDrawGridLines(false); // no grid lines
        runChart.getAxisRight().setEnabled(false); // no right axis
        runChart.invalidate();
    }

    public void drawLineChart() {
        ArrayList<Entry> entries = new ArrayList<>();

        for (int i = 0; i <= hour; i++) entries.add(new BarEntry(i, tempValues[i]));
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
        lineChart.getXAxis().setGranularity(1f); //라벨 표시 설정
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

        for (int i = 0; i < 7; i++) entries.add(new BarEntry(i, waterSums[i]));

        BarDataSet barDataSet = new BarDataSet(entries, " ");
        barDataSet.setColors(Color.parseColor("#1070DE"));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth((float) 0.5);//막대그래프 너비 설정

        waterChart.setData(barData);
        waterChart.setFitBars(true);

        waterChart.getDescription().setEnabled(false); //차트옆에 표기된 description 안보이게 설정
        waterChart.setPinchZoom(false); //줌인아웃 설정
        waterChart.setDrawGridBackground(false); //격자구조 여부
        waterChart.setTouchEnabled(false); // 그래프 터치햇을때
        waterChart.setDoubleTapToZoomEnabled(false); // 더블 탭으로 확대/축소 불가능하게

        XAxis xAxis = waterChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //x축을 아래에 두기
        xAxis.setValueFormatter(new IndexAxisValueFormatter(waterDays)); //라벨 설정
        xAxis.setDrawAxisLine(false); // 축(축선)을 따라 선(축선)을 그려야하는 경우
        xAxis.setDrawGridLines(false); // no grid lines

        YAxis left = waterChart.getAxisLeft();
        left.setDrawLabels(false); // 왼쪽 y축 값 보이지 않게
        left.setDrawAxisLine(false); // line X
        left.setDrawGridLines(false); // no grid lines
        left.setDrawZeroLine(true); // draw a zero line
        waterChart.getAxisRight().setEnabled(false); // no right axis

        waterChart.getLegend().setEnabled(false); // 차트 범례 설정
        waterChart.invalidate();
    }

    public void settingFood() {
        GetFoodRequest getFoodRequest = new GetFoodRequest(email, time, foodResponseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(getFoodRequest);
    }

    Response.Listener<String> stepResponseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Log.d("Step Result", response);
                boolean success = jsonObject.getBoolean("success");
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                if (success) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        String time = jObject.getString("time");
                        weekStepDays[5 - i] = getDateDay(time);
                        weekSteps[5 - i] = jObject.getInt("cnt_step");
                    }
                    drawWeekRunChart();

                } else {
                    Log.d("서버 전송", "실패");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setMessage("404 BAD REQUEST")
                        .setPositiveButton("확인", null)
                        .show();
            }
        }
    };

    Response.Listener<String> waterResponseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {

                Log.d("waterResponseListener", "실행");

                JSONObject jsonObject = new JSONObject(response);
                Log.d("Water Result", response);
                boolean success = jsonObject.getBoolean("success");
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                if (success) {
                    Log.d("water 성공", "실행");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        String time = jObject.getString("time");
                        waterDays[5 - i] = getDateDay(time);
                        waterSums[5 - i] = jObject.getInt("cnt_water");
                    }
                    drawWaterChart();

                } else {
                    Log.d("water 실패", "실행");
                }
            } catch (Exception e) {
                e.printStackTrace();
                androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setMessage("404 BAD REQUEST")
                        .setPositiveButton("확인", null)
                        .show();
            }
        }
    };

    Response.Listener<String> foodResponseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {

                Log.d("foodResponseListener", "실행");

                JSONObject jsonObject = new JSONObject(response);
                Log.d("Food Result", response);
                boolean success = jsonObject.getBoolean("success");
                boolean null_check = jsonObject.getBoolean("null");

                if (success) {
                    Log.d("Food 성공", "실행");
                    if (null_check) {
                        tv_memo_food.setText("아직 섭취한 사료가 없습니다!");
                    } else {
                        StringBuilder txt_food = new StringBuilder();

                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            int foodType = jObject.getInt("food_type");
                            double kcal = jObject.getDouble("kcal");
                            if (foodType == 0) {
                                txt_food.append("사료         " + kcal + "kcal\n");
                            } else {
                                txt_food.append("간식         " + kcal + "kcal\n");
                            }
                        }
                        tv_memo_food.setText(txt_food);

                    }


                    drawWaterChart();

                } else {
                    Log.d("Food 실패", "실행");
                }
            } catch (Exception e) {
                e.printStackTrace();
                androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setMessage("404 BAD REQUEST")
                        .setPositiveButton("확인", null)
                        .show();
            }
        }
    };

    public String getDateDay(String currentDate) {

        String dayOfWeek = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);
        Date nDate = null;
        try {
            nDate = dateFormat.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nDate);
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                dayOfWeek = "일";
                break;
            case 2:
                dayOfWeek = "월";
                break;
            case 3:
                dayOfWeek = "화";
                break;
            case 4:
                dayOfWeek = "수";
                break;
            case 5:
                dayOfWeek = "목";
                break;
            case 6:
                dayOfWeek = "금";
                break;
            case 7:
                dayOfWeek = "토";
                break;
        }

        Log.d("getDateDay", "date: " + currentDate + ", " + dayOfWeek + "요일");
        return dayOfWeek;
    }
}

class WaterRequest extends StringRequest {
    private final static String URL = "http://167.179.103.235/getWater.php";
    private Map<String, String> map;

    public WaterRequest(String email, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("email", email);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}

class StepRequest extends StringRequest {
    private final static String URL = "http://167.179.103.235/getStep.php";
    private Map<String, String> map;

    public StepRequest(String email, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("email", email);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}

class GetFoodRequest extends StringRequest {
    private final static String URL = "http://167.179.103.235/getFood.php";
    private Map<String, String> map;

    public GetFoodRequest(String email, String today, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("email", email);
        map.put("today", today);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
