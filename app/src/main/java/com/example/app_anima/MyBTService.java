package com.example.app_anima;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

public class MyBTService extends Service {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice = null; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림
    private Thread workerThread = null; // 문자열 수신에 사용되는 쓰레드
    private byte[] readBuffer; // 수신 된 문자열을 저장하기 위한 버퍼
    private int readBufferPosition; // 버퍼 내 문자 저장 위치

    private int tempCnt, run_step, walk_step, rest_time;
    private float tempSum;
    boolean thread_running = true;

    private final String SERVICE_ID = "기기와 통신";
    private final String DOG_LEG_ID = "반려견 상태";
    private final String DEVICE_NAME = "BT04-A";

    @Override
    public void onCreate() {
        Log.d("서비스 온 크리에이트 호출", "호출");
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        devices = bluetoothAdapter.getBondedDevices();
        dbHelper = new DBHelper(this);
        connectDevice(DEVICE_NAME);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("서비스 스타트 커맨드 호출", "호출");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, SERVICE_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        startForeground(1, notification);
        run_step = PreferenceManager.getInt(this, "run_step");
        walk_step = PreferenceManager.getInt(this, "walk_step");
        rest_time = PreferenceManager.getInt(this, "rest_time");
        receiveData();
        return super.onStartCommand(intent, flags, startId);
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
            //receiveData();
            Toast.makeText(getApplicationContext(), "기기가 연결되었습니다.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void receiveData() {
        // 데이터를 수신하기 위한 버퍼를 생성
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        // 데이터를 수신하기 위한 쓰레드 생성
        workerThread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                while (!workerThread.isInterrupted() && thread_running) {
                    try {
                        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        devices = bluetoothAdapter.getBondedDevices();
                        if (!bluetoothAdapter.isEnabled()) {
                            thread_running = false;
                            stopSelf();
                        }
                        boolean isConnected = false;
                        for (BluetoothDevice tempDevice : devices) {
                            if (tempDevice.getName().equals(DEVICE_NAME)) {
                                isConnected = true;
                                break;
                            }
                        }
                        if (!isConnected) {
                            thread_running = false;
                            stopSelf();
                        }
                        // 데이터 수신 확인
                        int byteAvailable = inputStream.available();
                        // 데이터가 수신 된 경우
                        Log.d("통신", byteAvailable + "중");
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
                                    /*handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                        }
                                    });*/
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
                                } // 개행 문자가 아닐 경우
                                else {
                                    readBuffer[readBufferPosition++] = tempByte;
                                }
                            }
                            Intent updateIntent = new Intent("service");
                            sendBroadcast(updateIntent);
                        }
                        try {
                            Thread.sleep(1000);
                            if (!thread_running) workerThread.interrupt();
                        } catch (InterruptedException e) {
                            Log.d("스레드 종료", "인터럽트 발생");
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        workerThread.start();
    }

    public void setTemperature(String data) {
        String[] temp = data.split("��C");
        tempSum += Float.parseFloat(temp[0]);
        tempCnt++;
        if (tempCnt == 5) {
            System.out.println("tempCnt" + tempCnt);
            float new_temp = (float) (Math.round((tempSum) * 20) / 100.0);
            PreferenceManager.setFloat(this, "Temperature", new_temp);
            if (new_temp != 0) {
                db = dbHelper.getReadableDatabase();
                db.execSQL("INSERT INTO temptable('tempvalue') VALUES ('" + new_temp + "');");
                db.close();
            }

            tempSum = 0;
            tempCnt = 0;
        }
    }

    public void setDogLiskDialog(String data) {
        String[] A = data.split(",");
        float a_x = Float.parseFloat(A[0]);
        float a_x1 = PreferenceManager.getFloat(this, "AX");
        if (a_x1 == 0.0) a_x1 = a_x;
        PreferenceManager.setFloat(this, "AX", a_x);
        if (Math.abs(a_x - a_x1) > 200) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel serviceChannel = new NotificationChannel(DOG_LEG_ID, DOG_LEG_ID, NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager manager = getSystemService(NotificationManager.class);
                assert manager != null;
                manager.createNotificationChannel(serviceChannel);
                Intent notificationIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, notificationIntent, 0);
                Notification notification = new NotificationCompat.Builder(this, DOG_LEG_ID)
                        .setContentTitle("반려견의 상태를 확인해주세요.")
                        .setContentText("다리를 확인해주세요.")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build();
                manager.notify(9999, notification);
            }

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
        if (minus < 8) {
            if (isRunning) {
                run_step += stepCnt;
                System.out.println("run_step " + run_step);
                PreferenceManager.setInt(this, "run_step", run_step);
            } else {
                walk_step += stepCnt;
                PreferenceManager.setInt(this, "walk_step", walk_step);
            }
            if (stepCnt != 0) {
                db = dbHelper.getReadableDatabase();
                db.execSQL("INSERT INTO steptable('stepcnt') VALUES ('" + stepCnt + "');");
                db.close();
            }

        }
        if (plus == 10) {
            rest_time += 1;
            PreferenceManager.setInt(this, "rest_time", rest_time);
        }
    }

    @Override
    public void onDestroy() {
        Log.d("온 디스트로이 호출", "호출");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(SERVICE_ID, SERVICE_ID, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);
        }
    }
}

