package com.example.sgg;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MyCardActivity extends AppCompatActivity {
    ImageView show_img;
    TextView signup_nfc, address;
    EditText show_company, show_position, show_address, show_phone, show_email, show_name;
    Button save, makeweb, webcheck;
    String nfc, beacon, key, data1, data2;
    RadioGroup group;
    AppCompatRadioButton r_card, r_web;

    private final int REQ_CODE_SELECT_IMAGE = 100;
    private String img_path = new String();
    private String serverURL = "http://192.168.0.42:8081/DbConn/Android/androidDB.jsp";
    private Bitmap image_bitmap_copy = null;
    private Bitmap image_bitmap = null;
    private String imageName = null;
    String imgName;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_card);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        //??? ?????? ????????????


        signup_nfc = findViewById(R.id.signup_nfc);
        show_name = findViewById(R.id.show_name);
        show_company = findViewById(R.id.show_company);
        show_position = findViewById(R.id.show_position);
        show_address = findViewById(R.id.show_address);
        show_phone = findViewById(R.id.show_phone);
        show_email = findViewById(R.id.show_email);
        save = findViewById(R.id.save);
        show_img = findViewById(R.id.show_img);
        address= findViewById(R.id.address);
        group= findViewById(R.id.group);
        r_card= findViewById(R.id.card);
        r_web= findViewById(R.id.web);
        webcheck=findViewById(R.id.webcheck);
        checkBTPermissions();

        group.check(R.id.card);
        ChangeMode(R.id.card);


        Intent intent = getIntent();
        nfc = intent.getStringExtra("nfc");
        beacon = intent.getStringExtra("beacon");
        key= intent.getStringExtra("key");
        signup_nfc.setText(nfc);

        Thread send_img= new Thread() {
            @Override
            public void run() {
                DoFileUpload(serverURL, img_path);
            }
        };

        //??? ?????? ????????????
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder saveBuilder = new AlertDialog.Builder(MyCardActivity.this)
                        .setTitle("??????")
                        .setMessage("????????????????")
                        .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("??????!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                try {

                                    String result;

                                    //String my_key= key;//???????????? ????????????
                                    String nfc = signup_nfc.getText().toString();
                                    String name = show_name.getText().toString();
                                    String company = show_company.getText().toString();
                                    String position = show_position.getText().toString();
                                    String address = show_address.getText().toString();
                                    String phone = show_phone.getText().toString();
                                    String email = show_email.getText().toString();
                                    String image = imgName;

                                    String type = "m";


                                    //String key; //????????? ????????? ??????X
                                    send_img.start();//????????? ????????? ??????
                                    Mycard_server save = new Mycard_server();
                                    result = save.execute(nfc, name, company, position, address, phone, email, type, image, beacon).get();

                                    switch (result) {
                                        case "r":
                                            Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                                            signup_nfc.setText("");

                                            break;
                                        case "s":
                                            Toast.makeText(getApplicationContext(), "??? ?????? ????????????", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            //????????? ?????????
                                            intent.putExtra("nfc", nfc);
                                            intent.putExtra("key", key);

                                            startActivity(intent);
                                            finish();
                                            break;
                                    }
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                AlertDialog msgDlg = saveBuilder.create();
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
        show_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });

        //???????????? ????????? ??????
        makeweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    webgo receive = new webgo();
                    data2 = receive.execute(key, "mp").get();
                    Log.v("test", data2);
                    data2 = data2.trim();


                    switch (data2) {

                        case "a":
                            Intent intent5 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.0.42:8081/webea/index_form.jsp"));
                            startActivity(intent5);
                            break;

                        case "s":
                            webgo1 receive1 = new webgo1();
                            data1 = receive1.execute(key, "mpp").get();
                            Log.v("test", data1);
//                            http://192.168.0.2:8081/webea/show.jsp?no=222&name=soeuy
                            Intent intent6 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.0.42:8081/webea/show.jsp?" + "key=" + key));
                            startActivity(intent6);
                            break;


                    }


//                    if (data2.equals("a")) {
//                        Intent intent5= new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.0.2:8081/webea/index_form.jsp"));
//                        startActivity(intent5);
//                    }
//
//                    else {
//                        //?????? ??????(?????? ?????????)
//                        webgo1 receive1 = new webgo1();
//                        try {
//                            data1 = receive1.execute(key, "mpp").get();
//
//                            Intent intent5= new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.0.2:8081/webea/show.jsp?no=222&name=soeuy"));
//                            intent5.putExtra("key",key);
//                            startActivity(intent5);
//
//
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //???????????? ???????????? ??????
        webcheck.setOnClickListener(new View.OnClickListener() {
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
                //???????????? ????????????
                address.setText("ADDRESS  :");
                show_address.setHint("??????????????? ???????????????");
                show_position.setHint("????????? ???????????????.");
                show_company.setVisibility(View.VISIBLE);
                show_company.setText("");
                show_company.setHint("COMPANY");
                webcheck.setVisibility(View.GONE);
                break;
            case R.id.web:
                r_web.setTextColor(Color.parseColor("#ffffff"));
                r_card.setTextColor(Color.parseColor("#3280EC"));
                //???????????? ???????????? ?????? ??????
                address.setText("WEB  :");
                webcheck.setVisibility(View.VISIBLE);
                show_address.setVisibility(View.GONE);
                show_company.setVisibility(View.GONE);
                show_company.setText("0");
                show_position.setHint("????????? ???????????????.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Toast.makeText(getBaseContext(), "resultCode : " + data, Toast.LENGTH_SHORT).show();

        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    img_path = getImagePathToUri(data.getData()); //???????????? URI??? ?????? ??????????????? ??????.
                    Toast.makeText(getBaseContext(), "img_path : " + img_path, Toast.LENGTH_SHORT).show();
                    //???????????? ????????????????????? ??????
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    //????????? ???????????? width , height ??? ??????
                    int reWidth = (int) (getWindowManager().getDefaultDisplay().getWidth());
                    int reHeight = (int) (getWindowManager().getDefaultDisplay().getHeight());

                    //image_bitmap ?????? ????????? ???????????? ???????????? ??????????????? ?????????. width: 400 , height: 300
                    image_bitmap_copy = Bitmap.createScaledBitmap(image_bitmap, 400, 300, true);  //???????????? ?????? ?????? ID???
                    show_img.setImageBitmap(image_bitmap_copy);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getImagePathToUri(Uri data) {
        //???????????? ????????? ???????????? ????????? ?????????
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        //???????????? ?????? ???
        String imgPath = cursor.getString(column_index);
        Log.d("test", imgPath);

        //???????????? ?????? ???
        imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
        Toast.makeText(MyCardActivity.this, "????????? ?????? : " + imgName, Toast.LENGTH_SHORT).show();
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

            // HttpURLConnection ??????
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);


            // ???????????????
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