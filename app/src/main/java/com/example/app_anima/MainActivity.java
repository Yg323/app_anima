package com.example.app_anima;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentMemo fragmentMemo = new FragmentMemo();
    private FragmentMagazin fragmentMagazin = new FragmentMagazin();

    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋

    String class_name = MyBTService.class.getName();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (requestCode == RESULT_OK) { // '사용'을 눌렀을 때
                    devices = bluetoothAdapter.getBondedDevices();
                    // 페어링 된 디바이스의 크기를 저장
                    int pariedDeviceCount = devices.size();
                    // 페어링 되어있는 장치가 없는 경우
                } else { // '취소'를 눌렀을 때
                    // 여기에 처리 할 코드를 작성하세요.
                }
                break;
            case 0:

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        if (isServiceRunning(class_name)) {
            this.registerReceiver(receiver, new IntentFilter("service"));
        }

        // 블루투스 활성화하기
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        if (bluetoothAdapter == null) { // 디바이스가 블루투스를 지원하지 않을 때
            System.out.println("해당 기기에서 지원하지 않습니다. ");
        } else { // 디바이스가 블루투스를 지원 할 때
            if (bluetoothAdapter.isEnabled()) { // 블루투스가 활성화 상태 (기기에 블루투스가 켜져있음)
                selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
            } else { // 블루투스가 비 활성화 상태 (기기에 블루투스가 꺼져있음)
                // 블루투스를 활성화 하기 위한 다이얼로그 출력
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // 선택한 값이 onActivityResult 함수에서 콜백된다.
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        }

        Calendar cal = Calendar.getInstance();
        int nWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (PreferenceManager.getInt(this, "nweek") != nWeek) {
            PreferenceManager.setInt(this, "water_count", 0);
            PreferenceManager.setInt(this, "run_step", 0);
            PreferenceManager.setInt(this, "walk_step", 0);
            PreferenceManager.setInt(this, "rest_time", 0);
            PreferenceManager.setInt(this, "nweek", nWeek);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    public Boolean isServiceRunning(String class_name){
        // 시스템 내부의 액티비티 상태를 파악하는 ActivityManager객체를 생성한다.
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //  manager.getRunningServices(가져올 서비스 목록 개수) - 현재 시스템에서 동작 중인 모든 서비스 목록을 얻을 수 있다.
        // 리턴값은 List<ActivityManager.RunningServiceInfo>이다. (ActivityManager.RunningServiceInfo의 객체를 담은 List)
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            // ActivityManager.RunningServiceInfo의 객체를 통해 현재 실행중인 서비스의 정보를 가져올 수 있다.
            if (class_name.equals(service.service.getClassName())) {
                return true;
            }
        }
        return  false;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
            if (fragment instanceof FragmentHome) ((FragmentHome) fragment).settingData();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void selectBluetoothDevice() {
        // 이미 페어링 되어있는 블루투스 기기를 찾습니다.
        devices = bluetoothAdapter.getBondedDevices();
        // 페어링 된 디바이스의 크기를 저장
        int pariedDeviceCount = devices.size();
        // 페어링 되어있는 장치가 없는 경우
        if (pariedDeviceCount == 0) {
            // 페어링을 하기위한 함수 호출
            setBluetooth("기기를 먼저 연결해주세요.");
        }
        Intent service = new Intent(MainActivity.this, MyBTService.class);
        startForegroundService(service);
    }

    public void setBluetooth(String bluetoothMessage) {
        // 디바이스를 선택하기 위한 다이얼로그 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(bluetoothMessage)        // 제목 설정
                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivityForResult(intent, 0);
                    }
                })
                .setNegativeButton("앱 종료", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (menuItem.getItemId()) {
                case R.id.home:
                    transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
                    break;
                case R.id.memo:
                    transaction.replace(R.id.frameLayout, fragmentMemo).commitAllowingStateLoss();
                    break;
                case R.id.magazin:
                    transaction.replace(R.id.frameLayout, fragmentMagazin).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }

}