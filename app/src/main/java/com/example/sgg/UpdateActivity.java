package com.example.sgg;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class UpdateActivity extends AppCompatActivity {
    Button save, exit, webupdate;
    ImageView show_img;
    TextView signup_nfc,show_name, address;
    EditText show_company,show_position,show_address,show_phone,show_email;
    String nfc, key;
    String[] getData;
    Bitmap bitmap;
    RadioGroup group;
    AppCompatRadioButton r_card, r_web;

    private final int REQ_CODE_SELECT_IMAGE = 100;
    private String img_path = new String();
    private String serverURL = "http://192.168.0.42:8081/DbConn/Android/androidDB.jsp";
    private Bitmap image_bitmap_copy = null;
    private Bitmap image_bitmap = null;
    private String imageName = null;
    String imgName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        setTitle("편집하기");

        save=findViewById(R.id.save);
        exit= findViewById(R.id.exit);
        show_img=findViewById(R.id.show_img);
        signup_nfc=findViewById(R.id.signup_nfc);
        show_name=findViewById(R.id.show_name);
        show_company=findViewById(R.id.show_company);
        show_position=findViewById(R.id.show_position);
        show_address=findViewById(R.id.show_address);
        show_phone=findViewById(R.id.show_phone);
        show_email=findViewById(R.id.show_email);
        address= findViewById(R.id.address);
        group= findViewById(R.id.group);
        r_card= findViewById(R.id.card);
        r_web= findViewById(R.id.web);
        webupdate= findViewById(R.id.webupdate);


        Intent intent =getIntent();
        nfc=intent.getStringExtra("nfc");
        key= intent.getStringExtra("key");

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

        Thread send_img= new Thread() {
            @Override
            public void run() {
                DoFileUpload(serverURL, img_path);
            }
        };

        try {
            String result;
            String nfc = signup_nfc.getText().toString();

            String type = "u";

            Receive_server2 task = new Receive_server2();
            result = task.execute(nfc, type).get();
            getData = result.split("/");

            if (getData[1].equals("0")){
                group.check(R.id.web);
                ChangeMode(R.id.web);
            }
            else {
                group.check(R.id.card);
                ChangeMode(R.id.card);
            }

            //img는??
//            show_name.setText(getData[0]);
//            show_company.setText(getData[1]);
//            show_position.setText(getData[2]);
//            show_phone.setText(getData[3]);
//            show_email.setText(getData[4]);
//            show_address.setText(getData[5]);

            uThread.start();
            uThread.join();
            show_img.setImageBitmap(bitmap);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //이미지 클릭
        show_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String result;

                    String nfc=signup_nfc.getText().toString();
                    String name=show_name.getText().toString();
                    String company=show_company.getText().toString();
                    String position=show_position.getText().toString();
                    String address=show_address.getText().toString();
                    String phone=show_phone.getText().toString();
                    String email=show_email.getText().toString();
                    String image = imgName;
                    String type="g";

                    if (image.equals(""))
                        image= getData[7];

                    send_img.start();

                    Update_server update= new Update_server();

                    result= update.execute(nfc, name, company, position, address,phone,image,email,type).get();

                    switch (result) {
                        case "r":
                            Toast.makeText(getApplicationContext(), "다시 시도", Toast.LENGTH_SHORT).show();
                            show_name.setText("");
                            break;
                        case "g":
                            Toast.makeText(getApplicationContext(), "변경 완료", Toast.LENGTH_SHORT).show();
                            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("key", key);
                            startActivity(intent);
                            finish();
                            break;
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent= new Intent(getApplicationContext(), MainActivity.class);
//                intent.putExtra("nfc", nfc);
//                intent.putExtra("key", key);
//                startActivity(intent);
                Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("key", key);
                startActivity(intent);
                finish();
            }
        });
        //정보 있으면 수정페이지,,,없으면 생성페이지
        webupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    public void mOnclick(View v) {
        ChangeMode(v.getId());
    }
    private void ChangeMode(int id) {
        switch(id) {
            case R.id.card:
                r_card.setTextColor(Color.parseColor("#ffffff"));
                r_web.setTextColor(Color.parseColor("#3280EC"));
                //레이아웃 명함모드
                if (getData[1].equals("0")) {
                    webupdate.setVisibility(View.GONE);
                    show_address.setVisibility(View.VISIBLE);
                    address.setText("ADDRESS  :");
                    show_name.setText("");
                    show_company.setText("");
                    show_position.setText("");
                    show_phone.setText("");
                    show_email.setText("");
                    show_address.setText("");
                    show_address.setHint("회사주소를 입력하세요");
                    show_company.setVisibility(View.VISIBLE);
                    show_position.setHint("직급을 입력하세요.");
                    show_company.setText("");
                    show_company.setHint("COMPANY");

                }
                else{
                    webupdate.setVisibility(View.GONE);
                    show_address.setVisibility(View.VISIBLE);
                    address.setText("ADDRESS  :");
                    show_company.setVisibility(View.VISIBLE);
                    show_name.setText(getData[0]);
                    show_company.setText(getData[1]);
                    show_position.setText(getData[2]);
                    show_phone.setText(getData[3]);
                    show_email.setText(getData[4]);
                    show_address.setText(getData[5]);
                }
                break;
            case R.id.web:
                r_web.setTextColor(Color.parseColor("#ffffff"));
                r_card.setTextColor(Color.parseColor("#3280EC"));
                //레이아웃 웹사이트 입력 모드
                if (getData[1].equals("0")) {
                    address.setText("WEB  :");
                    show_company.setVisibility(View.GONE);
                    show_address.setVisibility(View.GONE);
                    webupdate.setVisibility(View.VISIBLE);
                    show_name.setText(getData[0]);
                    show_company.setText(getData[1]);
                    show_position.setText(getData[2]);
                    show_phone.setText(getData[3]);
                    show_email.setText(getData[4]);
                    show_address.setText(getData[5]);
                }
                else{
                    webupdate.setVisibility(View.VISIBLE);
                    webupdate.setText("홈페이지 생성");
                    address.setText("WEB  :");
                    show_name.setText("");
                    show_position.setText("");
                    show_phone.setText("");
                    show_email.setText("");
                    show_address.setText("");
                    show_address.setVisibility(View.GONE);
                    show_company.setVisibility(View.GONE);
                    show_company.setText("0");
                    show_position.setHint("직업을 입력하세요");
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Toast.makeText(getBaseContext(), "resultCode : " + data, Toast.LENGTH_SHORT).show();

        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    img_path = getImagePathToUri(data.getData()); //이미지의 URI를 얻어 경로값으로 반환.
                    Toast.makeText(getBaseContext(), "img_path : " + img_path, Toast.LENGTH_SHORT).show();
                    //이미지를 비트맵형식으로 반환
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    //사용자 단말기의 width , height 값 반환
                    int reWidth = (int) (getWindowManager().getDefaultDisplay().getWidth());
                    int reHeight = (int) (getWindowManager().getDefaultDisplay().getHeight());

                    //image_bitmap 으로 받아온 이미지의 사이즈를 임의적으로 조절함. width: 400 , height: 300
                    image_bitmap_copy = Bitmap.createScaledBitmap(image_bitmap, 400, 300, true);  //이미지를 띄울 위젯 ID값
                    show_img.setImageBitmap(image_bitmap_copy);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getImagePathToUri(Uri data) {
        //사용자가 선택한 이미지의 정보를 받아옴
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        //이미지의 경로 값
        String imgPath = cursor.getString(column_index);
        Log.d("test", imgPath);

        //이미지의 이름 값
        imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
        Toast.makeText(UpdateActivity.this, "이미지 이름 : " + imgName, Toast.LENGTH_SHORT).show();
        this.imageName = imgName;

        return imgPath;
    }

    public void DoFileUpload(String apiUrl, String absolutePath) {
        HttpFileUpload(apiUrl, "", absolutePath);
    }

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    public void HttpFileUpload(String urlString, String params, String fileName) {
        try {

            FileInputStream mFileInputStream = new FileInputStream(fileName);
            URL connectUrl = new URL(urlString);
            Log.d("Test", "mFileInputStream  is " + mFileInputStream);

            // HttpURLConnection 통신
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);


            // 이미지전송
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);


            int bytesAvailable = mFileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            byte[] buffer = new byte[bufferSize];
            int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

            Log.d("Test", "image byte is " + bytesRead);

            // read image
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = mFileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            Log.e("Test", "File is written");
            mFileInputStream.close();
            dos.flush();
            // finish upload...

            // get response
            InputStream is = conn.getInputStream();

            StringBuffer b = new StringBuffer();
            for (int ch = 0; (ch = is.read()) != -1; ) {
                b.append((char) ch);
            }
            is.close();
            Log.e("Test", b.toString());


        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
            // TODO: handle exception
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            } else {
                Log.d("checkPermission", "No need to check permissions. SDK version < LoLLIPOP");
            }
        }

    }
}