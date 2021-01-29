package com.example.app_anima;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button btnLogin, btnSignUp, btnFindPassword;
    private String email, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.et_email);
        editTextPassword = findViewById(R.id.et_pswd);
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_signup);
        btnFindPassword = findViewById(R.id.btn_findpw);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                if (checkNull(email, password)) {
                    LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(loginRequest);
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        btnFindPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private Boolean checkNull(String email, String password) {
        if (email.equals("")) {
            AlertDialog builder = new AlertDialog.Builder(LoginActivity.this)
                    .setMessage("이메일을 입력하세요!")
                    .setPositiveButton("확인", null)
                    .show();
            return false;
        } else if (password.equals("")) {
            AlertDialog builder = new AlertDialog.Builder(LoginActivity.this)
                    .setMessage("비밀번호를 입력하세요!")
                    .setPositiveButton("확인", null)
                    .show();
            return false;
        }
        return true;
    }

    Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Log.d("Login Result", response);
                boolean success = jsonObject.getBoolean("success");

                if (success) {
                    String userEmail = jsonObject.getString("email");
                    String userPassword = jsonObject.getString("pswd");
                    String userName = jsonObject.getString("name");

                    PreferenceManager.setString(LoginActivity.this, "userEmail", userEmail);
                    PreferenceManager.setString(LoginActivity.this, "userPassword", userPassword);
                    PreferenceManager.setString(LoginActivity.this, "userName", userName);
                    PreferenceManager.setBoolean(LoginActivity.this, "autoLogin", true);

                    Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    AlertDialog builder = new AlertDialog.Builder(LoginActivity.this)
                            .setMessage("로그인 실패\n이메일과 비밀번호를 확인하세요.")
                            .setPositiveButton("확인", null)
                            .show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                AlertDialog builder = new AlertDialog.Builder(LoginActivity.this)
                        .setMessage("404 BAD REQUEST")
                        .setPositiveButton("확인", null)
                        .show();
            }
        }
    };
}

class LoginRequest extends StringRequest {
    private final static String URL = "http://167.179.103.235/login.php";
    private Map<String, String> map;

    public LoginRequest(String email, String password, Response.Listener<String> listener) {
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
