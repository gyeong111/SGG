package com.example.sgg;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;


public class AddFragment extends Fragment {
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    final static String TAG= "nfc_test";

    TextView show_name,show_company,show_position,show_address,show_phone,show_email, address;
    Button save, go_main, goweb;
    LinearLayout shoshow;
    ImageView show_img;
    TextView signup_nfc;
    String[] getData;
    String beacon;
    // public int a;
    String key, nfc;
    Bitmap bitmap;
    Thread uThread;
    String data2, data1;

    AlertDialog.Builder msgBuilder;
    AlertDialog msgDlg;

    public static AddFragment newInstance() {
        return new AddFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_add, container, false);
        Bundle bundle= getArguments();
        key= bundle.getString("key");
        nfc= bundle.getString("nfc");

        getActivity().setTitle("상대방명함 받아오기");
//        nfcAdapter= NfcAdapter.getDefaultAdapter(getActivity());
//
//        if (nfcAdapter==null){
//            Toast.makeText(getActivity(), "NO NFC", Toast.LENGTH_SHORT).show();
//            getActivity().finish();
//        }
//        pendingIntent= PendingIntent.getActivity(getActivity(), 0,
//                new Intent(getActivity(), getActivity().getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);

        show_img= view.findViewById(R.id.show_img);
        show_name= view.findViewById(R.id.show_name);
        show_company= view.findViewById(R.id.show_company);
        show_email= view.findViewById(R.id.show_email);
        show_phone= view.findViewById(R.id.show_phone);
        show_address= view.findViewById(R.id.show_address);
        show_position= view.findViewById(R.id.show_position);
        save= view.findViewById(R.id.save);
        signup_nfc= view.findViewById(R.id.signup_nfc);
        shoshow=view.findViewById(R.id.shoshow);
        address= view.findViewById(R.id.Address);
        goweb= view.findViewById(R.id.goweb);

        signup_nfc.setText(nfc);

        uThread = new Thread() {
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
        go();

//--상대방명함 저장부분  mykey(내 아이디),상대방 nfc,name ,company
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //확인다이얼로그창
                AlertDialog.Builder saveBuilder=new AlertDialog.Builder(getActivity())
                        .setTitle("저장하나요?")
                        .setMessage("")
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                try {

                                    String result;

                                    String my_key= key;//내아이디 가져오기
                                    String nfc= signup_nfc.getText().toString();
                                    String name= show_name.getText().toString();
                                    String company= show_company.getText().toString();
                                    String position= show_position.getText().toString();

                                    String type= "p";


                                    //String key; //데베에 하나더 추가X

                                    Mycard save_server= new Mycard();
                                    result= save_server.execute(my_key,nfc, name, company, position,type, beacon).get();

                                    switch (result) {
                                        case "r":
                                            Toast.makeText(getActivity(), "이미있는 명함입니다", Toast.LENGTH_SHORT).show();
                                            signup_nfc.setText("");
                                            ((MainActivity)getActivity()).switchFragment(1, my_key);
                                            break;
                                        case "s":
                                            Toast.makeText(getActivity(), "저장을 성공했습니다", Toast.LENGTH_SHORT).show();
                                            ((MainActivity)getActivity()).switchFragment(1, my_key);
//                                            save.setVisibility(View.GONE);
//                                            Intent intent= new Intent(getApplicationContext(),ShowcardActivity.class); //->저장된거 보는걸로 넘어가기(엑티비티생성해야함)
//                                            //정보들 보내기
//                                            intent.putExtra("nfc",nfc);
//                                            intent.putExtra("key",key);
//
//                                            startActivity(intent);
//                                            break;
                                    }
                                } catch (ExecutionException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                AlertDialog msgDlg=saveBuilder.create();
                msgDlg.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        msgDlg.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3280EC"));
                        msgDlg.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#3280EC"));
                    }
                });
                msgDlg.show();

            }
        });
//        go_main.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent back= new Intent(getApplicationContext(), MainActivity2.class);
////                back.putExtra("key", key);
////                startActivity(back);
////                ((MainActivity)getActivity()).switchFragment(new MainFragment());
//            }
//        });
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

                            ((aweb)getActivity()).aweb("http://192.168.0.42:8081/webea/show.jsp?no=2020&name=dddd");

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
    public interface aweb{
        public void aweb(String url);
    }
    //정보가져오기
    private void go() {
        try {
            String result;
            String nfc = signup_nfc.getText().toString();
            String type = "u";

            Receive_server2 task = new Receive_server2();
            result = task.execute(nfc, type).get();

            if (result.equals("x")) {
                AlertDialog.Builder adBuilder=new AlertDialog.Builder(getActivity())
                        .setTitle("경고")
                        .setMessage("등록되지 않은 사용자입니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ((MainActivity)getActivity()).switchFragment(1, key);
                            }
                        });

                AlertDialog aDlg=adBuilder.create();
                aDlg.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override public void onShow(DialogInterface arg0) {
                        aDlg.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3280EC"));
                    }});

                aDlg.show();
            }
            else {
                shoshow.setVisibility(View.VISIBLE);
                getData= result.split("/");

                if (getData[1].equals("0")) {
                    shoshow.setVisibility(View.VISIBLE);
                    address.setText("WEB : ");
//                    show_address.setAutoLinkMask(Linkify.WEB_URLS);
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
                beacon= getData[6];

                uThread.start();
                uThread.join();
                show_img.setImageBitmap(bitmap);
            }



        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

//    private String toReversedHex(byte[] bytes) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < bytes.length; ++i) {
//            if (i > 0) {
//                sb.append(" ");
//            }
//            int b = bytes[i] & 0xff;
//            if (b < 0x10)
//                sb.append('0');
//            sb.append(Integer.toHexString(b));
//        }
//        return sb.toString();
//    }
//    public String readTag(MifareUltralight mifareUlTag) {
//
//        try {
//            mifareUlTag.connect();
//            byte[] payload = mifareUlTag.readPages(4);
//            return new String(payload, Charset.forName("US-ASCII"));
//        } catch (IOException e) {
//            Log.e(TAG, "IOException while reading MifareUltralight message...", e);
//        } finally {
//            if (mifareUlTag != null) {
//                try {
//
//                    mifareUlTag.close();
//                }
//                catch (IOException e) {
//                    Log.e(TAG, "Error closing tag...", e);
//                }
//            }
//        }
//        return null;
//    }
}