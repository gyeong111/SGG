package com.example.sgg;

import static android.view.View.GONE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class Show_card extends AppCompatActivity {
    String nfc, key, my_key1;
    String[] result;
    String type="c";
    String data2, data1;

    ImageView image;
    TextView name, position, company, address, phone, email, Address;
    ImageButton callimg;
    Bitmap bitmap;
    Button back, website, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_card);

        Intent intent= getIntent();
        nfc= intent.getStringExtra("nfc");
        key= intent.getStringExtra("my_key");
        my_key1=intent.getStringExtra("key");//id값

        image= findViewById(R.id.show_img);
        name= findViewById(R.id.show_name);
        position= findViewById(R.id.show_position);
        company= findViewById(R.id.show_company);
        address= findViewById(R.id.show_address);
        phone= findViewById(R.id.show_phone);
        email= findViewById(R.id.show_email);
        back= findViewById(R.id.back);
        callimg=findViewById(R.id.callimg);
        Address= findViewById(R.id.Address);
        website= findViewById(R.id.website);
        email.setAutoLinkMask(Linkify.EMAIL_ADDRESSES); //이메일 보내기
        delete= findViewById(R.id.delete);

        getData(nfc);
        //이미지img_path= result[0];
        Thread uThread = new Thread() {
            @Override
            public void run(){
                try{
                    String str;

                    URL url = new URL("http://192.168.0.42:8081/DbConn/Android/image/"+result[0]);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    conn.setDoInput(true); //Server 통신에서 입력 가능한 상태로 만듦

                    conn.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)

                    InputStream is = conn.getInputStream(); //inputStream 값 가져오기

                    bitmap = BitmapFactory.decodeStream(is); // Bitmap으로 반환
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        };

        if (result[3].equals("0")) {
            Address.setText("WEB :  ");
            company.setVisibility(GONE);
//            address.setAutoLinkMask(Linkify.WEB_URLS);
            address.setVisibility(GONE);
            website.setVisibility(View.VISIBLE);
        }

        name.setText(result[1]);
        position.setText(result[2]);
        company.setText(result[3]);
        address.setText(result[4]);
        phone.setText(result[5]);
        email.setText(result[6]);

        try {
            uThread.start();
            uThread.join();
            image.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back= new Intent(getApplicationContext(), MainActivity.class);
                back.putExtra("key", my_key1);
                startActivity(back);
                finish();
            }
        });

        //전화걸기
        callimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tt = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + result[5]));
                startActivity(tt);

            }
        });

        //홈페이지 방문하기 버튼 클릭
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    webgo receive = new webgo();
                    data2 = receive.execute(key, "mp").get();
                    Log.v("test", data2);
                    data2=data2.trim();

                    if (data2.equals("a")) {
//                        Intent intent5= new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.0.2:8081/webea/index_form.jsp"));
//                        startActivity(intent5);
                    }

                    else {
                        //정보 있음(원래 페이지)
                        webgo1 receive1 = new webgo1();
                        try {
                            data1 = receive1.execute(key, "mpp").get();

//                            Intent intent5= new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.0.2:8081/webea/show.jsp?no=2020&name=dddd"));
////                            http://192.168.0.2:8081/webea/show.jsp?no=2020&name=dddd
//                            intent5.putExtra("key",key);
//                            startActivity(intent5);
                            Intent intent= new Intent(getApplicationContext(), WebViewActivity.class);
                            intent.putExtra("url", "http://192.168.0.42:8081/webea/show.jsp?no=2020&name=dddd");
                            startActivity(intent);


                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String result;
                    String nfc1=nfc; //nfc값
                    String id=my_key1; //id

                    String type="z";

                    delete_server delete =new delete_server();
                    result=delete.execute(id,nfc1,type).get();


                    switch (result) {
                        case "s": //삭제실패
                            Toast.makeText(Show_card.this, "삭제실패스얌.", Toast.LENGTH_SHORT).show();
                            break;
                        case "e": //

//                            Intent intent1=new Intent(this,MyshowcardActivity.class);
//                            intent1.putExtra("nfc",resultnfc);
//                            startActivity(intent1);
                            Toast.makeText(Show_card.this, "삭제성공스얌.", Toast.LENGTH_SHORT).show();
                            Intent main= new Intent(getApplicationContext(), MainActivity.class);
                            main.putExtra("key", my_key1);
                            startActivity(main);
                            break;
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void getData(String nfc) {
        try {
            ListClick_server receive= new ListClick_server();
            String data= receive.execute(nfc, type).get();
            result= data.split("/");

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }
//    //메뉴 상단바 버튼
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu1, menu);
//        return true;
//    }
//
//    //상단바 클릭코드(내 명함)
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle presses on the action bar items
//        switch (item.getItemId()) {
//            case R.id.delete_btn:
//                //내명함 저장 부분 (만약 정보가 없으면 저장할수있는 폼 /있으면 내명함 보여주기)
//                try {
//                    String result;
//                    String nfc1=nfc; //nfc값
//                    String id=my_key1; //id
//
//                    String type="z";
//
//                    delete_server delete =new delete_server();
//                    result=delete.execute(id,nfc1,type).get();
//
//
//                    switch (result) {
//                        case "s": //삭제실패
//                            Toast.makeText(Show_card.this, "삭제실패스얌.", Toast.LENGTH_SHORT).show();
//                            break;
//                        case "e": //
//
////                            Intent intent1=new Intent(this,MyshowcardActivity.class);
////                            intent1.putExtra("nfc",resultnfc);
////                            startActivity(intent1);
//                            Toast.makeText(Show_card.this, "삭제성공스얌.", Toast.LENGTH_SHORT).show();
//                            Intent main= new Intent(getApplicationContext(), MainActivity2.class);
//                            main.putExtra("key", my_key1);
//                            startActivity(main);
//                            break;
//                    }
//
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//
//
//
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}