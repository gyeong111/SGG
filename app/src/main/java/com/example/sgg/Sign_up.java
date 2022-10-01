package com.example.sgg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

public class Sign_up extends AppCompatActivity {
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    final static String TAG= "nfc_test";

    EditText signup_id, signup_pw, signup_nfc, signup_name;
    Button submit, check, find_beacon;
    String beacon_mac;
    TextView beacon;

    public static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nfcAdapter= NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter==null){
            Toast.makeText(this, "NO NFC", Toast.LENGTH_SHORT).show();
            finish();
        }
        pendingIntent= PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);

        signup_id= findViewById(R.id.signup_id);
        signup_pw= findViewById(R.id.signup_pw);
        signup_nfc= findViewById(R.id.signup_nfc);
        signup_name= findViewById(R.id.signup_name);
        submit= findViewById(R.id.submit);
        check= findViewById(R.id.check);
        find_beacon= findViewById(R.id.find_beacon);
        beacon= findViewById(R.id.beacon);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String result;

                    String id= signup_id.getText().toString();
                    String pw= signup_pw.getText().toString();
                    String nfc= signup_nfc.getText().toString();
                    String name= signup_name.getText().toString();
                    String beacon= beacon_mac;
                    String type= "s";

                    Signup_server login= new Signup_server();

                    result= login.execute(id, pw, type, name, nfc, beacon).get();

                    switch (result) {
                        case "r":
                            Toast.makeText(getApplicationContext(), "다시 시도", Toast.LENGTH_SHORT).show();
                            signup_id.setText("");
                            break;
                        case "s":
                            Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();
                            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
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

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String result;

                    String id= signup_id.getText().toString();
                    String pw= signup_pw.getText().toString();
                    String type= "l";

                    Login_server login= new Login_server();

                    result= login.execute(id, pw, type).get();

                    switch (result) {
                        case "s":
                            Toast.makeText(getApplicationContext(), "사용불가", Toast.LENGTH_SHORT).show();
                            signup_id.setText("");
                            break;
                        case "e":
                            Toast.makeText(getApplicationContext(), "사용불가", Toast.LENGTH_SHORT).show();
                            signup_id.setText("");
                            break;
                        case "n":
                            Toast.makeText(getApplicationContext(), "사용할 수 있는 ID", Toast.LENGTH_SHORT).show();
                            submit.setEnabled(true);
                            break;
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        //내 비콘 등록 버튼
        find_beacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent find= new Intent(getApplicationContext(), Find_beacon.class);
                startActivityForResult(find, REQUEST_CODE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            beacon_mac = data.getExtras().getString("beacon");
            beacon.setText(beacon_mac);
        }
    }

    //NFC설정
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
    Fragment fragment;
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

            signup_nfc.setText(toReversedHex(ID));
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
}