package com.example.app_anima;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dinuscxj.progressbar.CircleProgressBar;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentHome extends Fragment {

    private TextView tv_calorie, tv_menu, tv_water, tv_temp, tv_bpm, tv_go_jog, tv_run_step, tv_walk_step, tv_rest_time;
    private ListView listView;
    private ScrollView scrollView;

    private LinearLayout appbar, vp_layout, parent_layout;
    private ConstraintLayout dog_running_time;
    private DrawerLayout drawer_ad;

    private ImageButton btn_menu, btn_feedinput, circle_add, circle_minus;

    private final View[] dogContent = new View[8];
    private final String[] dogContentID = {"running_time", "food", "sleep", "weight", "heart", "stress", "water", "body_temperature"};
    private final long DELAY_MS = 500;
    private final long PERIOD_MS = 3000;
    private final int JOG_GOAL = 1000;

    private ArrayList<Drawable> mList;
    private ADScrollAdapter adScrollAdapter;
    private CircleProgressBar circleProgressBar;
    private ViewPager viewPager;
    private Timer timer;

    private boolean load = false;
    private int water_count, walk_step, run_step;
    private String txt_go_jog;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        String packName = Objects.requireNonNull(this.getActivity()).getPackageName();

        tv_calorie = viewGroup.findViewById(R.id.calory);
        tv_menu = viewGroup.findViewById(R.id.tv_menu);
        tv_water = viewGroup.findViewById(R.id.tv_water);
        tv_temp = viewGroup.findViewById(R.id.tv_temp);
        tv_bpm = viewGroup.findViewById(R.id.tv_bpm);
        tv_go_jog = viewGroup.findViewById(R.id.go_jog);
        tv_run_step = viewGroup.findViewById(R.id.tv_run_step);
        tv_walk_step = viewGroup.findViewById(R.id.tv_walk_step);
        tv_rest_time = viewGroup.findViewById(R.id.tv_rest_time);

        listView = viewGroup.findViewById(R.id.drawer_menulist);
        scrollView = viewGroup.findViewById(R.id.sv_main);

        appbar = viewGroup.findViewById(R.id.appbar);
        vp_layout = viewGroup.findViewById(R.id.vp_layout);
        parent_layout = viewGroup.findViewById(R.id.parent_layout);
        dog_running_time = viewGroup.findViewById(R.id.dog_running_time);
        drawer_ad = viewGroup.findViewById(R.id.drawer);

        btn_feedinput = viewGroup.findViewById(R.id.btn_feedinput);
        btn_menu = viewGroup.findViewById(R.id.btn_menu);
        circle_add = viewGroup.findViewById(R.id.circle_add);
        circle_minus = viewGroup.findViewById(R.id.circle_minus);

        viewPager = viewGroup.findViewById(R.id.viewPager);
        circleProgressBar = viewGroup.findViewById(R.id.cpb_circlebar);

        for (int i = 0; i < dogContentID.length; i++) {
            String name = "dog_" + dogContentID[i];
            int id = getResources().getIdentifier(name, "id", packName);
            dogContent[i] = viewGroup.findViewById(id);
        }

        /*Initial Setting*/
        settingData();

        circle_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                water_count += 100;
                tv_water.setText(Integer.toString(water_count));
                PreferenceManager.setInt(getContext(), "water_count", water_count);
            }
        });
        circle_minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (water_count != 0) {
                    water_count -= 100;
                    tv_water.setText(Integer.toString(water_count));
                    PreferenceManager.setInt(getContext(), "water_count", water_count);
                } else {
                    tv_water.setText("0");
                    PreferenceManager.setInt(getContext(), "water_count", 0);
                }
            }
        });


        //drawer 리스트 뷰
        final String[] items = {"블루투스 연결", "로그아웃"};
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        PreferenceManager.clear(getContext());
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                }
                // close drawer.
                DrawerLayout drawer = viewGroup.findViewById(R.id.drawer);
                drawer.closeDrawer(Gravity.LEFT);
            }
        });


        //drawer
        btn_menu.setOnClickListener(new OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View v) {
                if (!drawer_ad.isDrawerOpen(Gravity.LEFT)) {
                    drawer_ad.openDrawer(Gravity.LEFT);
                }
                if (drawer_ad.isDrawerOpen(Gravity.LEFT)) {
                    drawer_ad.closeDrawer(Gravity.LEFT);
                }
            }
        });

        //광고창
        mList = new ArrayList<>();
        mList.add(ResourcesCompat.getDrawable(getResources(), R.drawable.img_ad1, null));
        mList.add(ResourcesCompat.getDrawable(getResources(), R.drawable.img_ad2, null));
        mList.add(ResourcesCompat.getDrawable(getResources(), R.drawable.img_ad3, null));
        mList.add(ResourcesCompat.getDrawable(getResources(), R.drawable.img_ad4, null));
        adScrollAdapter = new ADScrollAdapter(getContext(), mList);
        viewPager.setAdapter(adScrollAdapter);

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            @Override
            public void run() {
                if (load) {
                    if (viewPager.getCurrentItem() == adScrollAdapter.getCount() - 1) {
                        viewPager.setCurrentItem(0, true);
                    } else {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    }
                } else {
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
                if (scrollY >= vp_layout.getBottom()) {
                    appbar.setBackgroundColor(Color.parseColor("#E7D0C8"));
                    tv_menu.setVisibility(View.VISIBLE);
                    btn_menu.setColorFilter(Color.parseColor("#000000"));
                } else {
                    appbar.setBackgroundColor(Color.parseColor("#00E7D0C8"));
                    tv_menu.setVisibility(View.INVISIBLE);
                    btn_menu.setColorFilter(Color.parseColor("#FFFFFF"));
                }
            }
        });

        //사료
        btn_feedinput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final double curCal;
                if (tv_calorie.getText().toString() == null) {
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

                mRadioGroup = view.findViewById(R.id.radiocategory);
                radioButtonFood = view.findViewById(R.id.radiofood);
                radioButtonSnack = view.findViewById(R.id.radiosnack);
                editTextInputFeed = view.findViewById(R.id.input_feed);
                btnPlus100 = view.findViewById(R.id.btn_plus100);
                btnPlus10 = view.findViewById(R.id.btn_plus10);
                btnPlus1 = view.findViewById(R.id.btn_plus1);
                btnMinus100 = view.findViewById(R.id.btn_minus100);
                btnMinus10 = view.findViewById(R.id.btn_minus10);
                btnMinus1 = view.findViewById(R.id.btn_minus1);
                editTextInputCal = view.findViewById(R.id.input_calorie);
                btnApply = view.findViewById(R.id.btn_apply);
                btnCancel = view.findViewById(R.id.btn_cancel);

                RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                    }
                };
                mRadioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);
                radioButtonFood.setChecked(true);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                btnPlus100.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed += 100;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });
                btnPlus10.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed += 10;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });
                btnPlus1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed += 1;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });
                btnMinus100.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed -= 100;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });
                btnMinus10.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed -= 10;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });
                btnMinus1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed -= 1;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });

                btnCancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btnApply.setOnClickListener(new OnClickListener() {
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

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    public void settingData(){
        String rest_time;
        int h, m, s;
        int rest_data = PreferenceManager.getInt(getActivity(), "rest_time");
        h = rest_data / 60 / 60;
        m = (rest_data - 60 * h) / 60;
        s = rest_data - (60 * 60 * h) - (60 * m);
        rest_time = h + "시간" + m + "분" + s + "초";
        System.out.println(rest_data+"  "+rest_time);

        water_count = PreferenceManager.getInt(getContext(), "water_count");
        run_step = PreferenceManager.getInt(getContext(), "run_step");
        walk_step = PreferenceManager.getInt(getContext(), "walk_step");

        tv_water.setText(Integer.toString(water_count));
        tv_temp.setText(String.valueOf(PreferenceManager.getFloat(getContext(), "Temperature")));
        tv_bpm.setText(PreferenceManager.getString(getContext(), "bpm"));
        tv_rest_time.setText(rest_time);

        if ((run_step + walk_step) > 1) {
            txt_go_jog = (run_step + walk_step) + "/"+ JOG_GOAL +"걸음";
            tv_go_jog.setText(txt_go_jog);
            circleProgressBar.setProgress((run_step + walk_step) * 100 / JOG_GOAL);
            dog_running_time.setVisibility(View.VISIBLE);
            int sumStep = run_step + walk_step;
            int run_percent = run_step * 100 / sumStep;
            int walk_percent = 100 - run_percent;
            String run_text = run_percent + "%";
            String walk_text = walk_percent + "%";
            LinearLayout.LayoutParams param_run = (LinearLayout.LayoutParams) tv_run_step.getLayoutParams();
            LinearLayout.LayoutParams param_walk = (LinearLayout.LayoutParams) tv_walk_step.getLayoutParams();
            if (walk_percent > 30 && run_percent > 30) {
                tv_run_step.setText(run_text);
                tv_walk_step.setText(walk_text);
            }else if (walk_percent < run_percent){
                tv_run_step.setText(run_text);
            }else{
                tv_walk_step.setText(walk_text);
            }
            param_run.weight = run_percent;
            param_walk.weight = walk_percent;
            tv_run_step.setLayoutParams(param_run);
            tv_walk_step.setLayoutParams(param_walk);
        }else {
            dog_running_time.setVisibility(View.GONE);
        }


    }
}
