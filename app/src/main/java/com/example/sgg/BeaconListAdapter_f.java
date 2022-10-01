package com.example.sgg;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.minew.beacon.BeaconValueIndex;
import com.minew.beacon.MinewBeacon;

import java.util.ArrayList;
import java.util.List;


public class BeaconListAdapter_f extends RecyclerView.Adapter<BeaconListAdapter_f.MyViewHolder>
                                    implements OnBeaconClicklistener{

    private List<MinewBeacon> mMinewBeacons = new ArrayList<>();
    OnBeaconClicklistener listener;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.list_beacon_f, null);
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
    public void setOnItemClicklistener(OnBeaconClicklistener listen){
        listener= listen;
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

    public String getItem(int position) {
        MinewBeacon minew;
        minew= mMinewBeacons.get(position);
        return minew.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_MAC).getStringValue();
    }

    @Override
    public void onItemClick(MyViewHolder holder, View view, int position) {
        if (listener != null)
            listener.onItemClick(holder, view, position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView beacon_name, beacon_mac;
        private MinewBeacon mMinewBeacon;


        public MyViewHolder(View itemView) {
            super(itemView);
            beacon_name = (TextView) itemView.findViewById(R.id.beacon_name);
            beacon_mac = (TextView) itemView.findViewById(R.id.beacon_mac);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position= getAdapterPosition();
                    if(listener != null) {
                        listener.onItemClick(MyViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setDataAndUi(MinewBeacon minewBeacon) {
            mMinewBeacon = minewBeacon;
            beacon_name.setText(mMinewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue());
            beacon_mac.setText(mMinewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_MAC).getStringValue());

        }
    }
}
