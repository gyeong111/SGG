package com.example.sgg;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sgg.databinding.ActivityMainBinding;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements MainFragment.show, MyFragment.edit, MakeFragment.show, MakeFragment.upload, MakeFragment.goweb, MyFragment.web
        ,AddFragment.aweb{

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    final static String TAG= "nfc_test";

    public static Context mContext;
    public String a;

    private ActivityMainBinding binding;
    private BottomNavigationView navView;
    Menu menu;
    Fragment my, find, home, add, more, make, alert;
    String key, result;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);
        menu= navView.getMenu();

        nfcAdapter= NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter==null){
            Toast.makeText(this, "NO NFC", Toast.LENGTH_SHORT).show();
            finish();
        }
        pendingIntent= PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);

        intent= getIntent();
        key= intent.getStringExtra("key");
        a= key;
        mContext=this;

        my= new MyFragment();
        find= new FindFragment();
        home= new MainFragment();
        add= new AddFragment();
        make= new MakeFragment();
        alert= new AlertFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, home).commit();
        menu.findItem(R.id.nav_home).setIcon(R.drawable.ic_home_select);
        Bundle bundle= new Bundle();
        bundle.putString("key", key);
        home.setArguments(bundle);
        navView= findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_my:
                        item.setIcon(R.drawable.ic_my_select);
                        menu.findItem(R.id.nav_find).setIcon(R.drawable.ic_find);
                        menu.findItem(R.id.nav_home).setIcon(R.drawable.ic_home);
                        menu.findItem(R.id.nav_add).setIcon(R.drawable.ic_add);
                        try {
                            String type="q";

                            Exisit_server exist =new Exisit_server();
                            result=exist.execute(key,type).get();
                            String[] re= new String[3];
                            re= result.split("/");

                            if (re[0].equals("e")){//내명함
                                String nfc= re[1];

//                                Intent intent1=new Intent(this,MyshowcardActivity.class);
//                                intent1.putExtra("key",key);
//                                intent1.putExtra("nfc", nfc);
//
//                                startActivity(intent1);
                                Bundle bundle= new Bundle();
                                bundle.putString("key", key);
                                bundle.putString("nfc", nfc);
                                my.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, my).commitAllowingStateLoss();
                            }
                            else {//명함추가
                                String nfc= re[0];
                                String beacon= re[1];

//                                Intent intent=new Intent(getApplicationContext(),MyCardActivity.class);
//                                intent.putExtra("key", key);
//                                intent.putExtra("nfc", nfc);
//                                intent.putExtra("beacon", beacon);
//                                startActivity(intent);
                                Bundle bundle= new Bundle();
                                bundle.putString("key", key);
                                bundle.putString("nfc", nfc);
                                bundle.putString("beacon", beacon);
                                make.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, make).commit();
                            }

                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        return true;

                    case R.id.nav_find:
                        item.setIcon(R.drawable.ic_find_select);
                        menu.findItem(R.id.nav_my).setIcon(R.drawable.ic_my);
                        menu.findItem(R.id.nav_home).setIcon(R.drawable.ic_home);
                        menu.findItem(R.id.nav_add).setIcon(R.drawable.ic_add);
                        Bundle bundlef= new Bundle();
                        bundlef.putString("key", key);
                        find.setArguments(bundlef);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, find).commit();
                        return true;
                    case R.id.nav_home:
                        item.setIcon(R.drawable.ic_home_select);
                        menu.findItem(R.id.nav_find).setIcon(R.drawable.ic_find);
                        menu.findItem(R.id.nav_my).setIcon(R.drawable.ic_my);
                        menu.findItem(R.id.nav_add).setIcon(R.drawable.ic_add);

                        Bundle bundle= new Bundle();
                        bundle.putString("key", key);
                        home.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, home).commit();

                        return true;
                    case R.id.nav_add:
                        item.setIcon(R.drawable.ic_add_select);
                        menu.findItem(R.id.nav_find).setIcon(R.drawable.ic_find);
                        menu.findItem(R.id.nav_home).setIcon(R.drawable.ic_home);
                        menu.findItem(R.id.nav_my).setIcon(R.drawable.ic_my);
                        Bundle bundle_= new Bundle();
                        bundle_.putString("key", key);
                        alert.setArguments(bundle_);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, alert).commit();
                        return true;
                    case R.id.nav_more:
                        SaveSharedPreference.clearUserId(getApplicationContext());
                        Intent intent= new Intent(getApplicationContext(), First.class);
                        startActivity(intent);
                        return true;
                    default: return true;
                }
            }
        });
    }
    public void switchFragment(int i, String key) {
        switch (i){
            case 1:
                menu.findItem(R.id.nav_home).setIcon(R.drawable.ic_home_select);
                menu.findItem(R.id.nav_find).setIcon(R.drawable.ic_find);
                menu.findItem(R.id.nav_my).setIcon(R.drawable.ic_my);
                menu.findItem(R.id.nav_add).setIcon(R.drawable.ic_add);
                Bundle bundle= new Bundle();
                bundle.putString("key", key);
                home.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, home).commit();
                break;
        }
    }
    //리스트에서 클릭한 항목 띄우기
    @Override
    public void show(String nfc, String key, String my_key) {
        Intent intent= new Intent(this, Show_card.class);
        intent.putExtra("nfc", nfc);
        intent.putExtra("key", key);//id값
        intent.putExtra("my_key",my_key);
        startActivity(intent);
    }

    @Override
    public void edit(String nfc, String key) {
        Intent goinfo = new Intent(getApplicationContext(), UpdateActivity.class);
        goinfo.putExtra("nfc", nfc);
        goinfo.putExtra("key", key);

        startActivity(goinfo);
        finish();
    }

    @Override
    public void show(String nfc, String key) {
        menu.findItem(R.id.nav_my).setIcon(R.drawable.ic_my_select);
        menu.findItem(R.id.nav_find).setIcon(R.drawable.ic_find);
        menu.findItem(R.id.nav_home).setIcon(R.drawable.ic_home);
        menu.findItem(R.id.nav_add).setIcon(R.drawable.ic_add);
        Bundle bundle1= new Bundle();
        bundle1.putString("key", key);
        bundle1.putString("nfc", nfc);
        my.setArguments(bundle1);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, my).commit();
    }
    @Override
    public void goweb(String url) {
        Intent intent= new Intent(this, WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
    @Override
    public void web(String url) {
        Intent intent= new Intent(this, WebViewActivity.class);
        intent.putExtra("url",url);
        startActivity(intent);
    }
    @Override
    public void aweb(String url) {
        Intent intent= new Intent(this, WebViewActivity.class);
        intent.putExtra("url",url);
        startActivity(intent);
    }
    @Override
    public void upload(String apiUrl, String absolutePath) {
        DoFileUpload(apiUrl, absolutePath);
    }
//NFC

    @Override
    protected void onResume() {
       super.onResume();
       assert  nfcAdapter != null;

      nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        String action= intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
            Tag tag= (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            assert tag != null;

            byte[] ID= tag.getId();
            MifareUltralight mifareUlTag = MifareUltralight.get(tag);
            readTag(mifareUlTag);

//            msgDlg.dismiss();
//
//            signup_nfc.setText(toReversedHex(ID));
//            go();
            Bundle bundle= new Bundle();
            bundle.putString("key", key);
            bundle.putString("nfc", toReversedHex(ID));
            add.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, add).commit();
        }
    }
    private String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }
    public String readTag(MifareUltralight mifareUlTag) {

        try {
            mifareUlTag.connect();
            byte[] payload = mifareUlTag.readPages(4);
            return new String(payload, Charset.forName("US-ASCII"));
        } catch (IOException e) {
            Log.e(TAG, "IOException while reading MifareUltralight message...", e);
        } finally {
            if (mifareUlTag != null) {
                try {

                    mifareUlTag.close();
                }
                catch (IOException e) {
                    Log.e(TAG, "Error closing tag...", e);
                }
            }
        }
        return null;
    }

//사진 업로드
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



}