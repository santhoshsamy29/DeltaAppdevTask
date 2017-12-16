package com.example.application.unit;

import android.content.Context;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    Context context;
    ArrayList<Store> storesArrayList;
    OnClickListener onClickListener;
    boolean isCancelled = false;
    int currentPosition =-1;

    public StoreAdapter(Context context, ArrayList<Store> storesArrayList) {
        this.context = context;
        this.storesArrayList = storesArrayList;
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_store_set,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        new CountDownTimer(60000 * storesArrayList.get(position).getTimer(), 1) {

            public void onTick(long millisUntilFinished) {
                if(isCancelled && position == currentPosition){
                    cancel();
                }else{
                    holder.timerTv.setText("00 : " +String.format("%02d : %02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            ((millisUntilFinished/1000)%60)));
                }

            }
            public void onFinish() {
                holder.timerTv.setText("00:00:00");
            }
        }.start();

        holder.storeNameTv.setText(storesArrayList.get(position).getName().substring(0,1).toUpperCase()
                + storesArrayList.get(position).getName().substring(1) );
        holder.storeNameTv.setPaintFlags(holder.storeNameTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.prodNameTv.setText(storesArrayList.get(position).getProdName().substring(0,1).toUpperCase()
                + storesArrayList.get(position).getProdName().substring(1));
        holder.prodNameTv.setPaintFlags(holder.prodNameTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.landmarkTv.setText(storesArrayList.get(position).getLandmark().substring(0,1).toUpperCase()
                + storesArrayList.get(position).getLandmark().substring(1));

        holder.gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.itemClicked(view,position);
            }
        });

        holder.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCancelled = true;
                currentPosition = position;
                onClickListener.itemClicked(view,position);
            }
        });

        holder.prodNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.itemClicked(view,position);
            }
        });

        holder.storeNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.itemClicked(view,position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return storesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageButton sendButton,gpsButton;
        TextView storeNameTv,prodNameTv,landmarkTv,timerTv;
        public ViewHolder(View itemView) {
            super(itemView);

            sendButton = itemView.findViewById(R.id.send_button);
            gpsButton = itemView.findViewById(R.id.gps_button);
            storeNameTv = itemView.findViewById(R.id.store_name_tv);
            prodNameTv = itemView.findViewById(R.id.productName_tv);
            landmarkTv = itemView.findViewById(R.id.landmark_tv);
            timerTv = itemView.findViewById(R.id.store_timer_tv);
        }
    }

    public interface OnClickListener{
        void itemClicked(View view,int position);
    }
}
