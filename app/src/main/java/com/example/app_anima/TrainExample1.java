package com.example.app_anima;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TrainExample1 extends AppCompatActivity {
    private ImageButton btnBack;
    private TextView trainTitle, trainContent;
    private WebView trainVideo;
    private WebSettings mWebSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainex1);

        btnBack = (ImageButton) findViewById(R.id.btn_back);
        trainTitle = (TextView) findViewById(R.id.traintitle);
        trainContent = (TextView) findViewById(R.id.traincontent);
        trainVideo = (WebView) findViewById(R.id.trainvideo);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        trainTitle.setText("테스트 제목");
        trainContent.setText("테스트 내용");

        trainVideo.setWebViewClient(new WebViewClient()); // 클릭시 새창 안뜨게
        mWebSettings = trainVideo.getSettings(); //세부 세팅 등록
        mWebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스크립트 허용 여부
        mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(true); // 화면 확대 축소 허용 여부
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

        trainVideo.loadUrl("https://m.site.naver.com/qrcode/view.nhn?v=0wdpw"); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
    }
}
