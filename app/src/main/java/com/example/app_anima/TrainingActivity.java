package com.example.app_anima;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TrainingActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private TextView trainTitle, trainContent;
    private WebView trainVideo;
    private WebSettings mWebSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        trainTitle = (TextView) findViewById(R.id.traintitle);
        trainContent = (TextView) findViewById(R.id.traincontent);
        trainVideo = (WebView) findViewById(R.id.trainvideo);

        final int getIndex = getIntent().getIntExtra("index", 1);

        trainContent.setMovementMethod(new ScrollingMovementMethod());
        trainContent.setScrollY(0);

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

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        switch (getIndex) {
            case 0:
                trainTitle.setText("어깨 관절 - 이두근, 삼두근");
                trainContent.setText("어깨관절 유연성 개선, 관절 가동 개선\n" +
                        "반려견을 한쪽으로 눞히거나 서 있는 상태에서 어깨 관절 부위를 스트레칭 하는 운동입니다.\n" +
                        "1) 스트레칭을 하려는 방향을 위로 향하게 옆으로 눕히고, 어깨와 팔꿈치, 발목 관절을 일자로 만듭니다.\n" +
                        "2) 어깨관절 앞으로 코 방향으로 앞발목을 부드럽게 올려줍니다. 이때 어깨관절의 통증이 느끼거나 어깨관절의 움직임에 이상이 있다면 중지해야 합니다. 통증이 없거나 불편해하지 않는 다면 동작은 최대한 늘린 상태에서 15~20초간 유지합니다.\n" +
                        "3) 견관절을 앞쪽으로 늘린 후에 다리를 몸통으로 내려서 중립 자세를 잠시 취한 후 뒤로 당겨서 굽히는 자세를 취하게 됩니다. 이 동작도 15~20초간 유지하고 3~5회 반복해줍니다.");
                trainVideo.loadUrl("https://m.site.naver.com/qrcode/view.nhn?v=0wdpw"); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
                break;
            case 1:
                trainTitle.setText("골반 – 허벅지 근육, 허벅지 인대");
                trainContent.setText("골반 가동성 개선, 넙다리 근육의 유연성 개선\n" +
                        "반려견을 한쪽으로 눕히거나 서 있는 상태에서 골반과 주변 근육 및 인대를 스트레칭 하는 운동입니다.\n" +
                        "1) 스트레치 하려는 방향을 위로 향하게 옆으로 눕히고, 뒷다리를 발목을 잡고 중립 자세를 취합니다. 발목을 잡을 때는 손가락에 힘을 풀고 편안하게 유지시켜야 합니다.\n" +
                        "2) 중립 자세에서 뒷다리를 뒤쪽으로 최대한 펴줍니다. 이 자세를 15~20초 정도 유지하며, 스트레칭의 효과는 골반 관절의 가동성을 개선시켜주고 허벅지 근육의 유연성을 증가시켜 줍니다.\n" +
                        "3) 골반을 눕히는 동작으로 중립 자세에서 무릎을 곧게 편 상태에서 부드럽게 배 쪽으로 당겨줍니다. 이 자세는 골반의 가동성 개선과 허벅지 뒤쪽 근육의 유연성을 개선시켜 줍니다.");
                trainVideo.loadUrl("https://m.site.naver.com/qrcode/view.nhn?v=0wdpE"); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
                break;
            case 2:
                trainTitle.setText("스트레칭 – 허리 근육, 엉덩이 근육");
                trainContent.setText("엉덩이 주변 근육, 허리 근육의 유연성 개선\n" +
                        "엉덩이 주변의 중요 근육을 스트레칭 하는 운동으로 눕거나 서 있는 상태에서 실시합니다.\n" +
                        "1) 스트레칭을 하고자 하는 방향을 위로 향하게 눕히고 뒷발목을 곧게 편 상태에서 중립 자세를 취합니다.\n" +
                        "2) 무릎관절과 발목관절을 편 상태로 유지하고 뒷다리를 최대한 뒤로 펴 줍니다.\n" +
                        "3) 뒷다리를 최대한 편 상태에서 뒷다리를 안쪽을 돌려줍니다. 뒤로 편 상태에서 안쪽으로 돌려줄 때 근육 긴장도가 유지되어야 합니다. 만약 허리 근육이나 엉덩이 근육 손상이 있는 환자라면 안쪽으로 돌려줄 때 통증을 나타냅니다.");
                trainVideo.loadUrl("https://m.site.naver.com/qrcode/view.nhn?v=0wdpQ"); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
                break;
            case 3:
                trainTitle.setText("쿠키 스트레칭 – 어깨부터 뒷발목까지");
                trainContent.setText("체중부하, 근육의 유연성 개선. 척추 가동성 개선\n" +
                        "쿠키 스트레칭은 반려견이 좋아하는 간식을 이용한 운동 방법입니다.\n" +
                        "한 세트는 어깨, 갈비뼈, 엉덩이, 뒷발목까지 4단계로 각 단계별로 5~10초 정도 유지하면서 한번에 진행을 합니다.\n" +
                        "1) 어깨\n" +
                        "한 손으로 배 아래를 가볍게 받쳐주고 치료하고자 하는 어깨쪽 방향을 향해 쿠키로 유인합니다. 이때 사지의 다리는 그대로 유지한 채로 스트레칭을 하는 게 좋습니다.\n" +
                        "2) 갈비뼈 \n" +
                        "어깨에서 쿠키를 먹게 되면 바로 갈비뼈 쪽으로 더 내려오게 됩니다.\n" +
                        "3) 골반 관절\n" +
                        "이번에는 갈비뼈보다 더 뒤쪽인 고관절쪽으로 유도합니다.\n" +
                        "4) 발목 관절\n" +
                        "마지막으로 엉덩이 아래쪽 방향으로 무릎뼈를 지나 발목 관절 쪽으로 유도하여 5~10초 정도 스트레칭 자시를 유지합니다.");
                trainVideo.loadUrl("https://m.site.naver.com/qrcode/view.nhn?v=0wdq0"); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
                break;
            case 4:
                trainTitle.setText("뒷다리 체중 이동");
                trainContent.setText("뒷다리 체중 부하, 균형 감각\n" +
                        "반려견이 서 있는 자세에서 앞다리는 고정하고 뒷다리를 보호자가 좌우로 밀어주어 움직이게 하여 체중 부하를 하는 운동입니다.\n" +
                        "한 세트는 뒷다리를 좌우로 움직이는 것을 1회로 하여 총 5~10회 반복합니다.\n" +
                        "1) 체중 이동 준비를 위해 사지로 지탱하여 서 있게 합니다.\n" +
                        "2) 좌에서 우로 뒷다리를 움직이도록 좌측을 밀거나 자극을 줍니다.\n" +
                        "3) 뒷다리의 좌측을 자극해 주면 우측으로 움직이게 되고 이때 앞다리는 고정되어야 합니다.\n" +
                        "4) 뒷다리가 우측으로 움직이면 다음에 좌측으로 움직이도록 해서 반복해 줍니다.");
                trainVideo.loadUrl("https://m.site.naver.com/qrcode/view.nhn?v=0wdh3"); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
        }
    }
}
