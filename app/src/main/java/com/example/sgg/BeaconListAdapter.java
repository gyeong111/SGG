package com.example.sgg;


import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.minew.beacon.BeaconValueIndex;
import com.minew.beacon.MinewBeacon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class BeaconListAdapter extends RecyclerView.Adapter<BeaconListAdapter.MyViewHolder>{

    private List<MinewBeacon> mMinewBeacons = new ArrayList<>();
    View view;
    String f_key;
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = View.inflate(parent.getContext(), R.layout.list_beacon, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setDataAndUi(mMinewBeacons.get(position));
    }

    @Override
    public int getItemCount() {
        if (mMinewBeacons != null) {
            return mMinewBeacons.size();
        }
        return 0;
    }

    public void setData(List<MinewBeacon> minewBeacons) {
        this.mMinewBeacons = minewBeacons;

//        notifyItemRangeChanged(0,minewBeacons.size());
        notifyDataSetChanged();

    }

    public void setItems(List<MinewBeacon> newItems) {
//        validateItems(newItems);


        int startPosition = 0;
        int preSize = 0;
        if (this.mMinewBeacons != null) {
            preSize = this.mMinewBeacons.size();

        }
        if (preSize > 0) {
            this.mMinewBeacons.clear();
            notifyItemRangeRemoved(startPosition, preSize);
        }
        this.mMinewBeacons.addAll(newItems);
        notifyItemRangeChanged(startPosition, newItems.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private MinewBeacon mMinewBeacon;
        private final TextView name;
        private final TextView position;
        private final TextView company;

        String Name= "";
        String Company= "";
        String Position= "";
        String NFC= "";

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.b_name);
            position= (TextView) itemView.findViewById(R.id.b_position);
            company= itemView.findViewById(R.id.b_company);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });
        }

        public void setDataAndUi(MinewBeacon minewBeacon) {
            String type="b";

            mMinewBeacon = minewBeacon;
            String mac= mMinewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_MAC).getStringValue();
            String id= ((MainActivity)MainActivity.mContext).a;

            String data= "";
            try {
                Beacon_server receive= new Beacon_server();
                data = receive.execute(mac,id, type).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            if(data.equals("x")){
                
            }
            else {
                String[] result;
                result= data.split("/");


                if (result[1].equals("0")) {
                    Name= result[0];
                    Company= result[2];
                    Position="";
                }
                else{
                    Name= result[0];
                    Company=result[1];
                    Position= result[2];
                }

                name.setText(Name);
                company.setText(Company);
                position.setText(Position);
            }


        }
    }
}
