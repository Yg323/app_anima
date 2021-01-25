package com.example.app_anima;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentMemo fragmentMemo = new FragmentMemo();
    private FragmentMagazin fragmentMagazin = new FragmentMagazin();

    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림
    private Thread workerThread = null; // 문자열 수신에 사용되는 쓰레드
    private byte[] readBuffer; // 수신 된 문자열을 저장하기 위한 버퍼
    private int readBufferPosition; // 버퍼 내 문자 저장 위치

    private boolean stop;
    private int tempCnt, run_step, walk_step, rest_time;
    private float tempSum;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (requestCode == RESULT_OK) { // '사용'을 눌렀을 때
                    selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
                } else { // '취소'를 눌렀을 때
                    // 여기에 처리 할 코드를 작성하세요.
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        Calendar cal = Calendar.getInstance();
        int nWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (PreferenceManager.getInt(this, "nweek") != nWeek) {
            PreferenceManager.setInt(this, "water_count", 0);
            PreferenceManager.setInt(this, "run_step", 0);
            PreferenceManager.setInt(this, "walk_step", 0);
            PreferenceManager.setInt(this, "rest_time", 0);
            PreferenceManager.setInt(this, "nweek", nWeek);
        }

        run_step = PreferenceManager.getInt(this, "run_step");
        walk_step = PreferenceManager.getInt(this, "walk_step");
        rest_time = PreferenceManager.getInt(this, "rest_time");

    }

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
        // 페어링 되어있는 장치가 있는 경우
        else {
            connectDevice("BT04-A");
        }
    }

    public void connectDevice(String deviceName) {
        // 페어링 된 디바이스들을 모두 탐색
        for (BluetoothDevice tempDevice : devices) {
            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if (deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }
        // UUID 생성
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            // 데이터 송,수신 스트림을 얻어옵니다.
            inputStream = bluetoothSocket.getInputStream();
            // 데이터 수신 함수 호출
            receiveData();
            Toast.makeText(getApplicationContext(), "기기가 연결되었습니다.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            finish();  // App 종료
            e.printStackTrace();
        }
    }

    public void receiveData() {
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        final Handler handler = new Handler();
        // 데이터를 수신하기 위한 버퍼를 생성
        readBufferPosition = 0;
        readBuffer = new byte[1024];

        // 데이터를 수신하기 위한 쓰레드 생성
        workerThread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // 데이터 수신 확인
                        int byteAvailable = inputStream.available();
                        // 데이터가 수신 된 경우
                        if (byteAvailable > 0) {
                            // 입력 스트림에서 바이트 단위로 읽어오기
                            byte[] bytes = new byte[byteAvailable];
                            inputStream.read(bytes);
                            // 입력 스트림 바이트를 한 바이트씩 읽어오기
                            for (int i = 0; i < byteAvailable; i++) {
                                byte tempByte = bytes[i];
                                // 개행문자를 기준으로 받음(한줄)
                                if (tempByte == '\n') {
                                    // readBuffer 배열을 encodedBytes로 복사
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    // 인코딩 된 바이트 배열을 문자열로 변환
                                    final String text = new String(encodedBytes, StandardCharsets.US_ASCII);
                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("받아온 데이터", text);
                                            String[] data = text.split("=");
                                            switch (data[0]) {
                                                case "T":
                                                    setTemperature(data[1]);
                                                    break;
                                                case "A":
                                                    setDogLiskDialog(data[1]);
                                                    break;
                                                case "P":
                                                    setHeartRate(data[1]);
                                                    break;
                                                case "V":
                                                    setStep(data[1]);
                                                    break;
                                            }
                                            if (!stop) ((FragmentHome) fragment).settingData();
                                        }
                                    });

                                } // 개행 문자가 아닐 경우
                                else {
                                    readBuffer[readBufferPosition++] = tempByte;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        // 1초마다 받아옴
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        workerThread.start();
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

    public void setTemperature(String data) {
        String[] temp = data.split("��C");
        tempSum += Float.parseFloat(temp[0]);
        tempCnt++;
        if (tempCnt == 5) {
            System.out.println("tempCnt" + tempCnt);
            PreferenceManager.setFloat(this, "Temperature", (float) (Math.round((tempSum) * 20) / 100.0));
            tempSum = 0;
            tempCnt = 0;
        }
    }

    public void setDogLiskDialog(String data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] A = data.split(",");
        float a_x = Float.parseFloat(A[0]);
        float a_x1 = PreferenceManager.getFloat(this, "AX");
        if (a_x1 == 0.0) a_x1 = a_x;
        PreferenceManager.setFloat(this, "AX", a_x);
        if (Math.abs(a_x - a_x1) > 200) {
            builder.setTitle("반려견의 상태를 확인해주세요!")        // 제목 설정
                    .setMessage("다리를 확인해주세요! ")        // 메세지 설정
                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        // 확인 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = builder.create();    // 알림창 객체 생성
            dialog.show();    // 알림창 띄우기
        }
    }

    public void setHeartRate(String data) {
        PreferenceManager.setString(this, "bpm", data);
    }

    public void setStep(String data) {
        String[] V = data.split(",");
        int minus = 0, stepCnt = 0, plus = 0;
        boolean isRunning = false;
        for (String s : V) {
            if (Float.parseFloat(s) < 7) {
                if (Float.parseFloat(s) < 0) minus++;
                stepCnt++;
            } else plus++;
            if (Float.parseFloat(s) < 3) { // 뛰기
                System.out.println("뛴다");
                isRunning = true;
            }
        }
        System.out.println(minus);
        if (minus < 8) {
            if (isRunning) {
                run_step += stepCnt;
                System.out.println("run_step " + run_step);
                PreferenceManager.setInt(this, "run_step", run_step);
            } else {
                walk_step += stepCnt;
                PreferenceManager.setInt(this, "walk_step", walk_step);
            }
        }
        if (plus == 10) {
            rest_time += 1;
            PreferenceManager.setInt(this, "rest_time", rest_time);
        }
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (menuItem.getItemId()) {
                case R.id.home:
                    stop = false;
                    transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
                    break;
                case R.id.memo:
                    stop = true;
                    transaction.replace(R.id.frameLayout, fragmentMemo).commitAllowingStateLoss();
                    break;
                case R.id.magazin:
                    stop = true;
                    transaction.replace(R.id.frameLayout, fragmentMagazin).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }

}