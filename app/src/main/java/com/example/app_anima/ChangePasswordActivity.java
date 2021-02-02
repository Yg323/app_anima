package com.example.app_anima;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText editTextCurPswd, editTextNewPswd, editTextNewPswdChk;
    private ImageButton btnBack;
    private Button btnApply;

    private String curPswd, newPswd, newPswdChk, email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepswd);

        editTextCurPswd = findViewById(R.id.et_curpswd);
        editTextNewPswd = findViewById(R.id.et_pswd);
        editTextNewPswdChk = findViewById(R.id.et_chkpswd);
        btnBack = findViewById(R.id.btn_back);
        btnApply = findViewById(R.id.btn_apply);

        email = PreferenceManager.getString(this, "userEmail");
        curPswd = PreferenceManager.getString(this, "userPassword");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editTextNewPswd.setVisibility(View.GONE);
        editTextNewPswdChk.setVisibility(View.GONE);
        btnApply.setText("확인");
        btnApply.setOnClickListener(checkPassword);
    }

    View.OnClickListener checkPassword = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String inputPswd = editTextCurPswd.getText().toString();
            if (inputPswd.equals("")) {
                AlertDialog builder = new AlertDialog.Builder(ChangePasswordActivity.this)
                        .setMessage("현재 비밀번호를 입력하세요!")
                        .setPositiveButton("확인", null)
                        .show();
            } else if (inputPswd.equals(curPswd)) {
                editTextCurPswd.setEnabled(false);
                editTextNewPswd.setVisibility(View.VISIBLE);
                editTextNewPswdChk.setVisibility(View.VISIBLE);
                btnApply.setText("변경");
                btnApply.setOnClickListener(newPassword);
            } else {
                AlertDialog builder = new AlertDialog.Builder(ChangePasswordActivity.this)
                        .setMessage("비밀번호가 틀렸습니다!")
                        .setPositiveButton("확인", null)
                        .show();
            }
        }
    };

    View.OnClickListener newPassword = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            newPswd = editTextNewPswd.getText().toString();
            newPswdChk = editTextNewPswdChk.getText().toString();
            if (newPswd.equals("")) {
                AlertDialog builder = new AlertDialog.Builder(ChangePasswordActivity.this)
                        .setMessage("새 비밀번호를 입력하세요!")
                        .setPositiveButton("확인", null)
                        .show();
            } else if (newPswdChk.equals("")) {
                AlertDialog builder = new AlertDialog.Builder(ChangePasswordActivity.this)
                        .setMessage("새 비밀번호 확인을 입력하세요!")
                        .setPositiveButton("확인", null)
                        .show();
            } else if (newPswdChk.equals(newPswd)) {
                ChangePasswordRequest request = new ChangePasswordRequest(email, newPswd, listener);
                RequestQueue requestQueue = Volley.newRequestQueue(ChangePasswordActivity.this);
                requestQueue.add(request);
            } else {
                AlertDialog builder = new AlertDialog.Builder(ChangePasswordActivity.this)
                        .setMessage("새 비밀번호 확인이 다릅니다!")
                        .setPositiveButton("확인", null)
                        .show();
            }
        }
    };

    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                Log.d("Change password Result", response);
                boolean success = Boolean.parseBoolean(response);
                if (success) {
                    Toast.makeText(ChangePasswordActivity.this, "비밀번호 변경 완료", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    AlertDialog builder = new AlertDialog.Builder(ChangePasswordActivity.this)
                            .setMessage("비밀번호 변경 실패")
                            .setPositiveButton("확인", null)
                            .show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialog builder = new AlertDialog.Builder(ChangePasswordActivity.this)
                        .setMessage("ERROR!")
                        .setPositiveButton("확인", null)
                        .show();
            }
        }
    };
}

class ChangePasswordRequest extends StringRequest {
    private final static String URL = "http://167.179.103.235/updatePswd.php";
    private Map<String, String> map;

    public ChangePasswordRequest(String email, String pswd, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("email", email);
        map.put("pswd", pswd);
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }
}