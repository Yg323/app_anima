package com.example.app_anima;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditInfoActivity extends AppCompatActivity {
    private TextView textViewEmail;
    private EditText editTextName, editTextPetName, editTextSpecie, editTextSex, editTextAge;
    private ImageButton btnBack;
    private Button btnModify;

    private String email, name, petName, specie, sex, age;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editinfo);

        textViewEmail = findViewById(R.id.tv_email);
        editTextName = findViewById(R.id.et_name);
        editTextPetName = findViewById(R.id.et_petname);
        editTextSpecie = findViewById(R.id.et_petspecie);
        editTextSex = findViewById(R.id.et_petsex);
        editTextAge = findViewById(R.id.et_petage);
        btnBack = findViewById(R.id.btn_back);
        btnModify = findViewById(R.id.btn_modify);

        email = PreferenceManager.getString(this, "userEmail");
        name = PreferenceManager.getString(this, "userName");
        petName = PreferenceManager.getString(this, "petName");
        specie = PreferenceManager.getString(this, "petSpecie");
        sex = PreferenceManager.getString(this, "petSex");
        age = PreferenceManager.getString(this, "petAge");

        textViewEmail.setText(email);
        editTextName.setText(name);
        editTextPetName.setText(petName);
        editTextSpecie.setText(specie);
        editTextSex.setText(sex);
        editTextAge.setText(age);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = editTextName.getText().toString();
                petName = editTextPetName.getText().toString();
                specie = editTextSpecie.getText().toString();
                sex = editTextSex.getText().toString();
                age = editTextAge.getText().toString();

                if (checkNull(name, petName, specie, sex, age)) {
                    EditUserRequest editUserRequest = new EditUserRequest(email, name, petName, specie, sex, age, responseListener);
                    RequestQueue requestQueue = Volley.newRequestQueue(EditInfoActivity.this);
                    requestQueue.add(editUserRequest);
                }
            }
        });
    }

    private boolean checkNull(String name, String petName, String specie, String sex, String age) {
        if (name.equals("")) {
            AlertDialog builder = new AlertDialog.Builder(EditInfoActivity.this)
                    .setMessage("이름을 입력하세요!")
                    .setPositiveButton("확인", null)
                    .show();
            return false;
        } else if (petName.equals("")) {
            AlertDialog builder = new AlertDialog.Builder(EditInfoActivity.this)
                    .setMessage("반려견 이름을 입력하세요!")
                    .setPositiveButton("확인", null)
                    .show();
            return false;
        } else if (specie.equals("")) {
            AlertDialog builder = new AlertDialog.Builder(EditInfoActivity.this)
                    .setMessage("반려견 종을 입력하세요!")
                    .setPositiveButton("확인", null)
                    .show();
            return false;
        } else if (sex.equals("")) {
            AlertDialog builder = new AlertDialog.Builder(EditInfoActivity.this)
                    .setMessage("반려견 성별을 입력하세요!")
                    .setPositiveButton("확인", null)
                    .show();
            return false;
        } else if (age.equals("")) {
            AlertDialog builder = new AlertDialog.Builder(EditInfoActivity.this)
                    .setMessage("반려견 나이를 입력하세요!")
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
                Log.d("Modify Result", response);
                boolean success = Boolean.parseBoolean(response);
                if (success) {
                    PreferenceManager.setString(EditInfoActivity.this, "userName", name);
                    PreferenceManager.setString(EditInfoActivity.this, "petName", petName);
                    PreferenceManager.setString(EditInfoActivity.this, "petSpecie", specie);
                    PreferenceManager.setString(EditInfoActivity.this, "petSex", sex);
                    PreferenceManager.setString(EditInfoActivity.this, "petAge", age);
                    Intent intent = new Intent(EditInfoActivity.this, UserInfoActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    AlertDialog builder = new AlertDialog.Builder(EditInfoActivity.this)
                            .setMessage("회원가입 실패")
                            .setPositiveButton("확인", null)
                            .show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialog builder = new AlertDialog.Builder(EditInfoActivity.this)
                        .setMessage("ERROR")
                        .setPositiveButton("확인", null)
                        .show();
            }
        }
    };
}

class EditUserRequest extends StringRequest {
    private final static String URL = "http://167.179.103.235/updateUser.php";
    private Map<String, String> map;

    public EditUserRequest(String email, String name, String petName, String specie, String sex, String age, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("email", email);
        map.put("name", name);
        map.put("petname", petName);
        map.put("specie", specie);
        map.put("sex", sex);
        map.put("age", age);
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }
}
