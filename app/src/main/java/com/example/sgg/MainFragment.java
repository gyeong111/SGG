package com.example.sgg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainFragment extends Fragment {
    public static final int REQUEST_CODE = 100;
    public static final int REQUEST_CODE2 = 103;

    ListView listView;
    String key, resultnfc;
    String n_type="n";
    //    String[] result;
    String A;
    ArrayList<Save_user> userDataView;
    String result;

    List<String> Name;
    List<String> Company;
    List<String> Position;
    List<String> NFC;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_main, container, false);
        // Inflate the layout for this fragment
        Bundle bundle= getArguments();
        key= bundle.getString("key");

        userDataView= new ArrayList<>();
        Name= new ArrayList<>();
        Company= new ArrayList<>();
        Position= new ArrayList<>();
        NFC= new ArrayList<>();

        listView= view.findViewById(R.id.listview);

        getList(key);

        if (A=="s") {
            for (int i=0; i<Name.size(); i++) {
                Save_user userData= new Save_user();
                userData.name= Name.get(i);
                userData.company= Company.get(i);
                userData.position= Position.get(i);

                userDataView.add(userData);
            }
        }
        else {
            Save_user userData= new Save_user();
            userData.name= Name.get(0);
            userData.company= Company.get(0);
            userData.position= Position.get(0);

            userDataView.add(userData);
        }
        ListAdapter adapter= new CustomListView(userDataView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent show= new Intent(getActivity(), Show_card.class);
//                show.putExtra("nfc",NFC.get(i));
//                show.putExtra("key",key);//id값
//                show.putExtra("my_key",resultnfc);
//
//                startActivityForResult(show, REQUEST_CODE);
                ((show)getActivity()).show(NFC.get(i), key, resultnfc);
            }
        });
        return view;
    }

    public void getList(String my_key) {
        String data="";
        try {
            Receive_server receive = new Receive_server();
            data = receive.execute(my_key, n_type).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (data.equals("a")) {
            A = "n";

            Name.add("저장된 정보 없음");
            Company.add("");
            Position.add("");
            NFC.add("");

        }
        else {
            A = "s";
            String[] result;

            result = data.split("/");

            for (int i = 0; i < result.length; i += 4) {
                Name.add(result[i]);
            }
            for (int i = 1; i < result.length; i += 4) {
                if (result[i].equals("0"))
                    Company.add(result[i+1]); //회사대신 직업?이름
                else
                    Company.add(result[i]);
            }
            for (int i = 2; i < result.length; i += 4) {
                if (result[i-1].equals("0"))
                    Position.add(""); //웹명함인 경우 비우기
                else
                    Position.add(result[i]);
            }
            for (int i = 3; i < result.length; i += 4) {
                NFC.add(result[i]);
            }

        }
    }
    public interface show {
        public void show(String nfc, String key, String my_key);
    }
}