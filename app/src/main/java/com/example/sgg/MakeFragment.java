package com.example.sgg;

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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MakeFragment extends Fragment implements View.OnClickListener{
    ImageView show_img;
    TextView signup_nfc, address;
    EditText show_company, show_position, show_address, show_phone, show_email, show_name;
    Button save, makeweb, webcheck;
    String nfc, beacon, key, data1, data2;
    RadioGroup group;
    AppCompatRadioButton r_card, r_web;
    RadioButton card, web;

    private final int REQ_CODE_SELECT_IMAGE = 100;
    private String img_path = new String();
    private String serverURL = "http://192.168.0.42:8081/DbConn/Android/androidDB.jsp";
    private Bitmap image_bitmap_copy = null;
    private Bitmap image_bitmap = null;
    private String imageName = null;
    String imgName;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_make, container, false);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        Bundle bundle= getArguments();
        key= bundle.getString("key");
        nfc= bundle.getString("nfc");
        beacon= bundle.getString("beacon");


        signup_nfc = view.findViewById(R.id.signup_nfc);
        show_name = view.findViewById(R.id.show_name);
        show_company = view.findViewById(R.id.show_company);
        show_position = view.findViewById(R.id.show_position);
        show_address = view.findViewById(R.id.show_address);
        show_phone = view.findViewById(R.id.show_phone);
        show_email = view.findViewById(R.id.show_email);
        save = view.findViewById(R.id.save);
        show_img = view.findViewById(R.id.show_img);
        address= view.findViewById(R.id.address);
        group= view.findViewById(R.id.group);
        r_card= view.findViewById(R.id.card);
        r_web= view.findViewById(R.id.web);
        webcheck=view.findViewById(R.id.webcheck);
        makeweb= view.findViewById(R.id.makeweb);
        card= view.findViewById(R.id.card);
        web= view.findViewById(R.id.web);
        checkBTPermissions();

        card.setOnClickListener(this);
        web.setOnClickListener(this);

        group.check(R.id.card);
        ChangeMode(R.id.card);

        signup_nfc.setText(nfc);

        //??? ?????? ????????????
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder saveBuilder = new AlertDialog.Builder(getActivity())
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


                                    ((upload)getActivity()).upload(serverURL, img_path);
                                    Mycard_server save = new Mycard_server();
                                    result = save.execute(nfc, name, company, position, address, phone, email, type, image, beacon).get();

                                    switch (result) {
                                        case "r":
                                            Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();
                                            signup_nfc.setText("");

                                            break;
                                        case "s":
                                            Toast.makeText(getActivity(), "??? ?????? ????????????", Toast.LENGTH_SHORT).show();
//                                            Intent intent = new Intent(getActivity(), MainActivity.class);
//                                            //????????? ?????????
//                                            intent.putExtra("nfc", nfc);
//                                            intent.putExtra("key", key);
//
//                                            startActivity(intent);
                                            ((show)getActivity()).show(nfc, key);
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
//                            Intent intent5 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.0.2:8081/webea/index_form.jsp"));
//                            startActivity(intent5);
                            ((goweb)getActivity()).goweb("http://192.168.0.42:8081/webea/index_form.jsp");
                            break;

                        case "s":
                            webgo1 receive1 = new webgo1();
                            data1 = receive1.execute(key, "mpp").get();
                            Log.v("test", data1);
////                            http://192.168.0.2:8081/webea/show.jsp?no=222&name=soeuy
//                            Intent intent6 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.0.2:8081/webea/show.jsp?" + "key=" + key));
//                            startActivity(intent6);
                            ((goweb)getActivity()).goweb("http://192.168.0.42:8081/webea/show.jsp?" + "key=" + key);
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
        return view;
    }

    @Override
    public void onClick(View view) {
        ChangeMode(view.getId());
    }

    public interface show{
        public void show(String nfc, String key);
    }

    public interface upload {
        public void upload(String apiUrl, String absolutePath);
    }

    public interface goweb {
        public void goweb(String url);
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
                makeweb.setVisibility(View.GONE);
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
                makeweb.setVisibility(View.VISIBLE);
                show_address.setVisibility(View.GONE);
                show_company.setVisibility(View.GONE);
                show_company.setText("0");
                show_position.setHint("????????? ???????????????.");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Toast.makeText(getActivity().getBaseContext(), "resultCode : " + data, Toast.LENGTH_SHORT).show();

        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    img_path = getImagePathToUri(data.getData()); //???????????? URI??? ?????? ??????????????? ??????.
                    Toast.makeText(getActivity().getBaseContext(), "img_path : " + img_path, Toast.LENGTH_SHORT).show();
                    //???????????? ????????????????????? ??????
                    image_bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());

                    //????????? ???????????? width , height ??? ??????
                    int reWidth = (int) (getActivity().getWindowManager().getDefaultDisplay().getWidth());
                    int reHeight = (int) (getActivity().getWindowManager().getDefaultDisplay().getHeight());

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
        Cursor cursor = getActivity().managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        //???????????? ?????? ???
        String imgPath = cursor.getString(column_index);
        Log.d("test", imgPath);

        //???????????? ?????? ???
        imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
        Toast.makeText(getActivity(), "????????? ?????? : " + imgName, Toast.LENGTH_SHORT).show();
        this.imageName = imgName;

        return imgPath;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = getActivity().checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += getActivity().checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            } else {
                Log.d("checkPermission", "No need to check permissions. SDK version < LoLLIPOP");
            }
        }

    }

}