package com.example.app_anima;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class TrainingActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private TextView trainTitle, trainContent;
    private VideoView trainVideo;
    private MediaController controller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        btnBack = findViewById(R.id.btn_back);
        trainTitle = findViewById(R.id.traintitle);
        trainContent = findViewById(R.id.traincontent);
        trainVideo = findViewById(R.id.trainvideo);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        trainContent.setMovementMethod(new ScrollingMovementMethod());

        int getId = getIntent().getIntExtra("id", 0);
        String getTitle = getIntent().getStringExtra("title");

        trainTitle.setText(getTitle);

        // Uri videoUri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.trainvideo_+getId);
        String path = "android.resource://"+getPackageName()+"/raw/trainvideo_"+getId;
        trainVideo.setVideoPath(path);

        trainVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                controller = new MediaController(TrainingActivity.this);

                trainVideo.setMediaController(controller);
                controller.setAnchorView(trainVideo);
            }
        });

        InputStream inputStream = getResources().openRawResource(R.raw.traintext_1);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                trainContent.append(line);
                Log.d("MyTag", line);
                trainContent.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
