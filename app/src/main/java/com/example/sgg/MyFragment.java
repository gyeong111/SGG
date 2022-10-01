package com.example.sgg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MyFragment extends Fragment {

    View view;
    String key, nfc;
    Button editbtn, goweb;
    ImageView show_img;
    TextView signup_nfc,show_company,show_name,show_position,show_address,show_phone,show_email, address;
    String data2, data1;
    String[] getData;
    Bitmap bitmap;
    ImageView my, find, add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_my, container, false);

        Bundle bundle= getArguments();
        key= bundle.getString("key");
        nfc= bundle.getString("nfc");

        show_img = view.findViewById(R.id.show_img);
        signup_nfc = view.findViewById(R.id.signup_nfc);
        show_company = view.findViewById(R.id.show_company);
        show_name = view.findViewById(R.id.show_name);
        show_position = view.findViewById(R.id.show_position);
        show_address = view.findViewById(R.id.show_address);
        show_phone = view.findViewById(R.id.show_phone);
        show_email = view.findViewById(R.id.show_email);
        editbtn = view.findViewById(R.id.editbtn);
        address= view.findViewById(R.id.address);
        goweb= view.findViewById(R.id.goweb);
        show_email.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);

        signup_nfc.setText(nfc);

        Thread uThread = new Thread() {
            @Override
            public void run(){
                try{
                    String str;

                    URL url = new URL("http://192.168.0.42:8081/DbConn/Android/image/"+getData[7]);
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

        try {
            String result;
            String nfc = signup_nfc.getText().toString();

            String type = "u";

            Receive_server2 task = new Receive_server2();
            result = task.execute(nfc, type).get();
            getData = result.split("/");

            if (getData[1].equals("0")) {
                address.setText("WEB:   ");
//                show_address.setAutoLinkMask(Linkify.WEB_URLS);
                show_company.setVisibility(View.GONE);
                show_address.setVisibility(View.GONE);
                goweb.setVisibility(View.VISIBLE);
            }

            show_name.setText(getData[0]);
            show_company.setText(getData[1]);
            show_position.setText(getData[2]);
            show_phone.setText(getData[3]);
            show_email.setText(getData[4]);
            show_address.setText(getData[5]);

            uThread.start();
            uThread.join();
            show_img.setImageBitmap(bitmap);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent goinfo = new Intent(getApplicationContext(), UpdateActivity.class);
//                goinfo.putExtra("nfc", nfc);
//                goinfo.putExtra("key", key);
//
//                startActivity(goinfo);
                ((edit)getActivity()).edit(nfc, key);
            }
        });

        //홈페이지 방문
        goweb.setOnClickListener(new View.OnClickListener() {
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
                        ((web)getActivity()).web("http://192.168.0.42:8081/webea/index_form.jsp");
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
                            ((web)getActivity()).web("http://192.168.0.42:8081/webea/show.jsp?no=2020&name=dddd");


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

        return view;
    }
    public interface edit{
        public void edit(String nfc, String key);
    }
    public interface web{
        public void web(String url);
    }
}