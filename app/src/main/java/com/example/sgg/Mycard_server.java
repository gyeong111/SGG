package com.example.sgg;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Mycard_server extends AsyncTask<String, Void, String> {

    String str;
    String receiveMsg, sendMsg;

    @Override
    protected String doInBackground(String... strings) {

        try {
            URL url = new URL("http://192.168.0.42:8081/DbConn/Android/androidDB.jsp");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

            // 전송할 데이터. GET 방식으로 작성
            sendMsg = "nfc="+ strings[0]+ "&name="+strings[1]+ "&company="+strings[2]+ "&position="+ strings[3]+ "&address="+ strings[4]
                    + "&phone="+ strings[5]+ "&email="+ strings[6]+ "&type="+ strings[7]+"&image="+strings[8]+"&beacon="+strings[9];
            //my_key,nfc, name, company, position,type
            osw.write(sendMsg);
            osw.flush();

            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();

                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }

                receiveMsg = buffer.toString();

            } else {
                // 통신 실패
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //jsp로부터 받은 리턴 값
        return receiveMsg;
    }
}