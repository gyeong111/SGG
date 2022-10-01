package com.example.sgg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AlertFragment extends Fragment {
    AlertDialog.Builder msgBuilder;
    AlertDialog msgDlg;
    String key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_alert, container, false);
        Bundle bundle= getArguments();
        key= bundle.getString("key");

        showDialognfc();

        return view;
    }
    public void showDialognfc() {

        msgBuilder=new AlertDialog.Builder(getActivity())
                .setTitle("알림")
                .setMessage("nfc태그해주세요")
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent= new Intent(getActivity(), MainActivity2.class);
//                        intent.putExtra("key", key);
//                        startActivity(intent);
                        ((MainActivity)getActivity()).switchFragment(1, key);
                    }
                });

        msgDlg=msgBuilder.create();
        msgDlg.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface arg0) {
                msgDlg.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#3280EC"));
            }});

        msgDlg.show();
    }
}