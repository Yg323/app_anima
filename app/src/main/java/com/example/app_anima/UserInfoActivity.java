package com.example.app_anima;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity {
    private ImageView imageViewProfile;
    private TextView textViewEmail, textViewName, textViewPetName, textViewSpecie, textViewSex, textViewAge;
    private Button btnSetProfile, btnDelProfile, btnEditInfo, btnChangePassword, btnWithdraw;
    private ImageButton btnBack;

    private String email, name, petName, specie, sex, age, imgpath;

    private File img;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        imageViewProfile = findViewById(R.id.profile_image);
        textViewEmail = findViewById(R.id.tv_email);
        textViewName = findViewById(R.id.tv_name);
        textViewPetName = findViewById(R.id.tv_petname);
        textViewSpecie = findViewById(R.id.tv_petspecie);
        textViewSex = findViewById(R.id.tv_petsex);
        textViewAge = findViewById(R.id.tv_petage);
        btnBack = findViewById(R.id.btn_back);
        btnSetProfile = findViewById(R.id.btn_setimage);
        btnDelProfile = findViewById(R.id.btn_delimage);
        btnEditInfo = findViewById(R.id.btn_editinfo);
        btnChangePassword = findViewById(R.id.btn_changepswd);
        btnWithdraw = findViewById(R.id.btn_withdraw);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        email = PreferenceManager.getString(UserInfoActivity.this, "userEmail");
        name = PreferenceManager.getString(UserInfoActivity.this, "userName");
        petName = PreferenceManager.getString(UserInfoActivity.this, "petName");
        specie = PreferenceManager.getString(UserInfoActivity.this, "petSpecie");
        sex = PreferenceManager.getString(UserInfoActivity.this, "petSex");
        age = PreferenceManager.getString(UserInfoActivity.this, "petAge");

        textViewEmail.setText(email);
        textViewName.setText(name);
        textViewPetName.setText(petName);
        textViewSpecie.setText(specie);
        textViewSex.setText(sex);
        textViewAge.setText(age);

        imgpath = PreferenceManager.getString(this, "profileImg");

        if (imgpath.equals("http://167.179.103.235/null")) {
            imageViewProfile.setImageDrawable(getDrawable(R.drawable.ic_profile));
        } else {
            Glide.with(this).load(imgpath).into(imageViewProfile);
        }

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int permissionResult = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(permissionResult== PackageManager.PERMISSION_DENIED){
                String[] permissions= new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,10);
            }
        }

        btnSetProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 200);
            }
        });

        btnDelProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewProfile.setImageDrawable(getDrawable(R.drawable.ic_profile));
                deleteProfileImage(email);
            }
        });

        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, EditInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                withdrawUser(email);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 10 :
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "외부 메모리 읽기/쓰기 사용 가능", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "외부 메모리 읽기/쓰기 제한", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 200:
                if (resultCode == -1) {
                    //선택한 사진의 경로(Uri)객체 얻어오기
                    Uri uri= data.getData();
                    if(uri!=null){
                        imageViewProfile.setImageURI(uri);
                        img = getRealFileFromUri(uri);
                        setProfileImage(email, img);
                    }
                } else {
                    Toast.makeText(this, "이미지 선택을 하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private File getRealFileFromUri(Uri uri){
        String[] proj= {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= ((Cursor) cursor).getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        File result= new File(cursor.getString(column_index));
        cursor.close();
        return result;
    }

    private void setProfileImage(String email, File img) {
        String URL = "http://167.179.103.235/setProfileImage.php";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", img.getName(), RequestBody.create(MultipartBody.FORM, img))
                .addFormDataPart("email", email)
                .build();
        final Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String output = response.body().string();
                Log.d("ProfileImage Response:", output);
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    String imgPath = jsonObject.getString("profile");
                    PreferenceManager.setString(UserInfoActivity.this, "profileImg", "http://167.179.103.235/"+imgPath);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void deleteProfileImage(String email) {
        String URL = "http://167.179.103.235/deleteProfileImage.php";
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .build();
        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String output = response.body().string();
                Log.d("ProfileImage Response", output);
                boolean success = Boolean.parseBoolean(output);
                if (success) {
                    PreferenceManager.setString(UserInfoActivity.this, "profileImg", "http://167.179.103.235/null");
                } else {
                    Toast.makeText(UserInfoActivity.this, "프로필 사진 삭제 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void withdrawUser(String email) {
        String URL = "http://167.179.103.235/deleteUser.php";
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .build();
        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String output = response.body().string();
                Log.d("Withdraw Response", output);
                boolean success = Boolean.parseBoolean(output);
                if (success) {
                    PreferenceManager.clear(UserInfoActivity.this);
                    Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UserInfoActivity.this, "프로필 사진 삭제 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
