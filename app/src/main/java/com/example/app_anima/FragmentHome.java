package com.example.app_anima;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dinuscxj.progressbar.CircleProgressBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class FragmentHome extends Fragment {

    private ImageButton btn_menu,circle_add,circle_minus;
    private ImageButton btn_feedinput; // 사료 입력 버튼 선언
    private TextView tv_calorie;
    private ScrollView scrollView;
    private LinearLayout appbar, vp_layout;
    private TextView tv_menu,tv_water,tv_temp,go_jog;
    private ListView listView;
    private ArrayList<Drawable> mList;
    private ViewPager viewPager;
    private ADScrollAdapter adScrollAdapter;
    private boolean load = false;
    private Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
    private DrawerLayout drawer;
    private static final String DEFAULT_PATTERN = "%d%%";
    private CircleProgressBar circleProgressBar;
    private int water_count, nweek;
    //블루투스
    private BluetoothSPP bt;
    int cnt=0; float tempSum=0;
    int step;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        btn_feedinput = (ImageButton) viewGroup.findViewById(R.id.btn_feedinput); // 사료 입력 버튼
        tv_calorie = (TextView) viewGroup.findViewById(R.id.calory);
        btn_menu = (ImageButton) viewGroup.findViewById(R.id.btn_menu);
        circle_add = (ImageButton) viewGroup.findViewById(R.id.circle_add);
        circle_minus = (ImageButton) viewGroup.findViewById(R.id.circle_minus);
        scrollView = (ScrollView) viewGroup.findViewById(R.id.sv_main);
        appbar = (LinearLayout) viewGroup.findViewById(R.id.appbar);
        tv_menu = (TextView) viewGroup.findViewById(R.id.tv_menu);
        tv_water = (TextView) viewGroup.findViewById(R.id.tv_water);
        tv_temp = (TextView) viewGroup.findViewById(R.id.tv_temp);
        go_jog = (TextView) viewGroup.findViewById(R.id.go_jog);
        viewPager = (ViewPager) viewGroup.findViewById(R.id.viewPager);
        vp_layout = (LinearLayout) viewGroup.findViewById(R.id.vp_layout);
        circleProgressBar = (CircleProgressBar) viewGroup.findViewById(R.id.cpb_circlebar);
        listView = (ListView) viewGroup.findViewById(R.id.drawer_menulist);

        //물 + 걸음수 초기화
        Calendar cal = Calendar.getInstance();
        nweek = cal.get(Calendar.DAY_OF_WEEK); //요일 구하기
        if(PreferenceManager.getInt(getContext(),"nweek")!=nweek) {
            PreferenceManager.setInt(getContext(), "water_count", 0);
            PreferenceManager.setInt(getContext(),"dog_step",0);
            PreferenceManager.setInt(getContext(),"nweek",nweek);
        }
        water_count = PreferenceManager.getInt(getContext(),"water_count");
        step = PreferenceManager.getInt(getContext(),"dog_step");
        tv_water.setText(Integer.toString(water_count));
        if(step > 1) {
            go_jog.setText(Integer.toString(step)+"/1000걸음");//걸음수 초기값 10000으로 설정해두었는데 바꾸어야함
            circleProgressBar.setProgress(step/10);
        }
        circle_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                water_count+=100;
                tv_water.setText(Integer.toString(water_count));
                PreferenceManager.setInt(getContext(), "water_count", water_count);
            }
        });
        circle_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(water_count!=0) {
                    water_count-=100;
                    tv_water.setText(Integer.toString(water_count));
                    PreferenceManager.setInt(getContext(), "water_count", water_count);
                }
                else{
                    tv_water.setText("0");
                    PreferenceManager.setInt(getContext(), "water_count", 0);
                }
            }
        });

        /*bt = new BluetoothSPP(getContext()); //Initializing

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
        }
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                System.out.println(message);
                String[] array = message.split("=");
                switch (array[0]){
                    case "A" :
                        Log.d("A 값 들어왔음",array[1]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        String[] A = array[1].split(",");
                        float a_x = Float.parseFloat(A[0]);
                        float a_x1 = PreferenceManager.getFloat(getContext(),"AX");
                        if(a_x1==0.0) a_x1=a_x;
                        PreferenceManager.setFloat(getContext(),"AX",a_x);
                        if(Math.abs(a_x-a_x1)>200){
                            builder.setTitle("반려견의 상태를 확인해주세요!")        // 제목 설정
                                    .setMessage("다리를 확인해주세요! ")        // 메세지 설정
                                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                        // 확인 버튼 클릭시 설정
                                        public void onClick(DialogInterface dialog, int whichButton){
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog dialog = builder.create();    // 알림창 객체 생성
                            dialog.show();    // 알림창 띄우기
                        }
                        break;
                    case "V" : //7미만의 숫자가 나올 경우 걸음수 +1
                        Log.d("V 값 들어왔음",array[1]);
                        String[] V = array[1].split(",");
                        int minus =0; int stepCnt=0;
                        for (int i = 0; i < V.length ; i++) {
                            if(Float.parseFloat(V[i])<7){
                                if(Float.parseFloat(V[i])<0) minus++;
                                stepCnt++;
                            }
                        }
                        if(minus<8){
                            step+=stepCnt;
                            PreferenceManager.setInt(getContext(),"dog_step",step);
                            go_jog.setText(Integer.toString(step)+"/1000걸음"); //걸음수 초기값 1000으로 설정해두었는데 바꾸어야함
                            //산책
                            circleProgressBar.setProgress(step/10);
                        }
                        break;
                    case "P" :
                        Log.d("P 값 들어왔음",array[1]);
                        break;
                    case "T" :
                        Log.d("온도 값 들어왔음",array[1]);
                        String[] temp = array[1].split("°C");
                        tempSum+=Float.parseFloat(temp[0]);
                        cnt++;
                        Log.d("cnt값이당", String.valueOf(cnt));

                        if(cnt==5){
                            PreferenceManager.setFloat(getContext(),"Temperature", (float) (Math.round((tempSum) * 20) / 100.0));
                            Log.d("tempSum", String.valueOf(Math.round((tempSum) * 20) / 100.0));
                            tv_temp.setText(String.valueOf(PreferenceManager.getFloat(getContext(),"Temperature")));
                            tempSum=0;cnt=0;
                        }
                        break;
                }
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Log.d("연결됐을때","Connected to " + name + "\n" + address);
            }

            public void onDeviceDisconnected() { //연결해제
                Log.d("연결해제","Connection lost");
            }

            public void onDeviceConnectionFailed() { //연결실패
                Log.d("연결실패","연결실패");
            }
        });



        //drawer 리스트 뷰
        final String[] items = {"블루투스 연결"};
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, items) ;
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                switch (position){
                    case 0:
                        if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                            bt.disconnect();
                        } else {
                            Intent intent = new Intent(getContext(), DeviceList.class);
                            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                        }
                        break;
                }
                // close drawer.
                DrawerLayout drawer = (DrawerLayout) viewGroup.findViewById(R.id.drawer) ;
                drawer.closeDrawer(Gravity.LEFT) ;
            }
        });



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
        });*/

        //광고창
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

        //사료
        btn_feedinput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final double curCal;
                if (tv_calorie.getText().toString().equals(null)) {
                    curCal = 0;
                } else {
                    curCal = Double.parseDouble(tv_calorie.getText().toString());
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_food, null, false);
                builder.setView(view);

                final RadioGroup mRadioGroup;
                final RadioButton radioButtonFood, radioButtonSnack;
                final EditText editTextInputFeed;
                final Button btnPlus100, btnPlus10, btnPlus1, btnMinus100, btnMinus10, btnMinus1;
                final EditText editTextInputCal;
                final Button btnApply, btnCancel;
                final boolean isFood;

                mRadioGroup = (RadioGroup) view.findViewById(R.id.radiocategory);
                radioButtonFood = (RadioButton) view.findViewById(R.id.radiofood);
                radioButtonSnack = (RadioButton) view.findViewById(R.id.radiosnack);
                editTextInputFeed = (EditText) view.findViewById(R.id.input_feed);
                btnPlus100 = (Button) view.findViewById(R.id.btn_plus100);
                btnPlus10 = (Button) view.findViewById(R.id.btn_plus10);
                btnPlus1 = (Button) view.findViewById(R.id.btn_plus1);
                btnMinus100 = (Button) view.findViewById(R.id.btn_minus100);
                btnMinus10 = (Button) view.findViewById(R.id.btn_minus10);
                btnMinus1 = (Button) view.findViewById(R.id.btn_minus1);
                editTextInputCal = (EditText) view.findViewById(R.id.input_calorie);
                btnApply = (Button) view.findViewById(R.id.btn_apply);
                btnCancel = (Button) view.findViewById(R.id.btn_cancel);

                RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                    }
                };
                mRadioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);
                radioButtonFood.setChecked(true);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                btnPlus100.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed += 100;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });
                btnPlus10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed += 10;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });
                btnPlus1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed += 1;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });
                btnMinus100.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed -= 100;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });
                btnMinus10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed -= 10;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });
                btnMinus1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed -= 1;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btnApply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int checkedRadio = mRadioGroup.getCheckedRadioButtonId();
                        double inputCalorie = Double.parseDouble(editTextInputCal.getText().toString());
                        double calculatedCalorie = inputCalorie / 100;
                        double inputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        double finalFeed = inputFeed * calculatedCalorie;
                        tv_calorie.setText(String.format("%.2f", finalFeed + curCal));
                        alertDialog.dismiss();
                    }
                });
            }
        });



        return viewGroup;
    }

    /*@Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    public CharSequence format(int progress, int max) {
        return String.format(DEFAULT_PATTERN, (int) ((float) progress / (float) max * 100));
    }
    //블루투스
    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
            }
        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(getContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                Log.d("블루투스","Bluetooth was not enabled.");
            }
        }
    }
*/
}
