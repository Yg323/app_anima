package com.example.app_anima;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class FindPasswordActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private Button btnApply;
    private EditText editTextEmail, editTextAuthCode, editTextPassword, editTextPasswordCheck;
    private String email, authCode, inputCode, password, passwordCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpswd);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitDiskReads().permitDiskWrites().permitNetwork().build());

        btnBack = findViewById(R.id.btn_back);
        btnApply = findViewById(R.id.btn_apply);
        editTextEmail = findViewById(R.id.et_email);
        editTextAuthCode = findViewById(R.id.et_authcode);
        editTextPassword = findViewById(R.id.et_pswd);
        editTextPasswordCheck = findViewById(R.id.et_chkpswd);

        editTextAuthCode.setVisibility(View.GONE);
        editTextPassword.setVisibility(View.GONE);
        editTextPasswordCheck.setVisibility(View.GONE);
        btnApply.setText("인증번호 받기");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnApply.setOnClickListener(sendMail);
    }

    View.OnClickListener sendMail = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            email = editTextEmail.getText().toString();
            MailSend mailSend = new MailSend();
            authCode = MailSend.getAuthCode();
            if (email.equals("")) {
                AlertDialog builder = new AlertDialog.Builder(FindPasswordActivity.this)
                        .setMessage("이메일을 입력하세요!")
                        .setPositiveButton("확인", null)
                        .show();
            } else {
                mailSend.sendMail(getApplicationContext(), email);
                btnApply.setText("인증");
                btnApply.setOnClickListener(certifying);
                editTextEmail.setEnabled(false);
                editTextAuthCode.setVisibility(View.VISIBLE);
            }
        }
    };

    View.OnClickListener certifying = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            inputCode = editTextAuthCode.getText().toString();
            if (inputCode.equals("")) {
                AlertDialog builder = new AlertDialog.Builder(FindPasswordActivity.this)
                        .setMessage("인증코드를 입력하세요!")
                        .setPositiveButton("확인", null)
                        .show();
            } else {
                if (inputCode.equals(authCode)) {
                    AlertDialog builder = new AlertDialog.Builder(FindPasswordActivity.this)
                            .setMessage("인증되었습니다.")
                            .setPositiveButton("확인", null)
                            .show();
                    editTextAuthCode.setVisibility(View.GONE);
                    editTextPassword.setVisibility(View.VISIBLE);
                    editTextPasswordCheck.setVisibility(View.VISIBLE);
                    btnApply.setText("변경");
                    btnApply.setOnClickListener(changePswd);
                } else {
                    AlertDialog builder = new AlertDialog.Builder(FindPasswordActivity.this)
                            .setMessage("인증코드가 틀렸습니다.")
                            .setPositiveButton("확인", null)
                            .show();
                    editTextAuthCode.setText(null);
                    btnApply.setText("인증벋호 받기");
                    btnApply.setOnClickListener(sendMail);
                }
            }
        }
    };

    View.OnClickListener changePswd = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            password = editTextPassword.getText().toString();
            passwordCheck = editTextPasswordCheck.getText().toString();
            if (password.equals("")) {
                AlertDialog builder = new AlertDialog.Builder(FindPasswordActivity.this)
                        .setMessage("비밀번호를 입력하세요!")
                        .setPositiveButton("확인", null)
                        .show();
            } else if (passwordCheck.equals("")) {
                AlertDialog builder = new AlertDialog.Builder(FindPasswordActivity.this)
                        .setMessage("비밀번호 확인을 입력하세요!")
                        .setPositiveButton("확인", null)
                        .show();
            } else {
                ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(email, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(FindPasswordActivity.this);
                queue.add(resetPasswordRequest);
            }
        }
    };

    Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                Log.d("Reset Password Result", response);
                boolean success = Boolean.parseBoolean(response);

                if (success) {
                    Toast.makeText(FindPasswordActivity.this, "비밀번호 변경 완료", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FindPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    AlertDialog builder = new AlertDialog.Builder(FindPasswordActivity.this)
                            .setMessage("비밀번호 초기화 실패")
                            .setPositiveButton("확인", null)
                            .show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialog builder = new AlertDialog.Builder(FindPasswordActivity.this)
                        .setMessage("ERROR!")
                        .setPositiveButton("확인", null)
                        .show();
            }
        }
    };
}

class ResetPasswordRequest extends StringRequest {
    private final static String URL = "http://167.179.103.235/resetpswd.php";
    private Map<String, String> map;

    public ResetPasswordRequest(String email, String password, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("email", email);
        map.put("pswd", password);
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }
}
