package com.example.sgg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    EditText login_id, login_pw;
    Button login,sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("");

        login_id= findViewById(R.id.login_id);
        login_pw= findViewById(R.id.login_pw);
        login= findViewById(R.id.login);
        sign_up= findViewById(R.id.sign_up);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String result;

                    String id= login_id.getText().toString();
                    String pw= login_pw.getText().toString();
                    String type= "l";

                    Login_server login= new Login_server();

                    result= login.execute(id, pw, type).get();

                    switch (result) {
                        case "s":
                            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                            SaveSharedPreference.setUserId(LoginActivity.this, id);

                            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("key",id);
                            intent.putExtra("type","m");
                            startActivity(intent);
                            finish();
                            break;
                        case "e":
                            Toast.makeText(getApplicationContext(), "비밀번호 오류", Toast.LENGTH_SHORT).show();
                            break;
                        case "n":
                            Toast.makeText(getApplicationContext(), "가입하지 않은 id", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }


        });
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), Sign_up.class);
                startActivity(intent);
                finish();
            }
        });
    }
}