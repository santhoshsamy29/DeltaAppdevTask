package com.example.application.unit;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{

    ClickListener clickListener;
    Context context;
    ArrayList<String> names;
    CountDownTimer countDownTimer;

    public CustomAdapter(Context context, ArrayList<String> names) {
        this.context = context;
        this.names = names;
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_set,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        countDownTimer = new CountDownTimer(600000, 1) {

            public void onTick(long millisUntilFinished) {
                holder.timerTv.setText("00 : " +String.format("%02d : %02d",TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        ((millisUntilFinished/1000)%60)));
            }

            public void onFinish() {
                countDownTimer.cancel();
            }
        }.start();

        holder.nameTv.setText(names.get(position));

        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.itemClicked(view,position);
            }
        });
        holder.locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.itemClicked(view,position);
            }
        });
        holder.newActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.itemClicked(view,position);
            }
        });
        holder.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.itemClicked(view,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv,timerTv;
        ImageButton callButton,locationButton,newActivityButton;
        Button okButton;

        public ViewHolder(View itemView) {
            super(itemView);
            okButton = itemView.findViewById(R.id.ok_button);
            nameTv = itemView.findViewById(R.id.name_tv);
            timerTv = itemView.findViewById(R.id.timer_tv);
            callButton = itemView.findViewById(R.id.call);
            locationButton = itemView.findViewById(R.id.location);
            newActivityButton = itemView.findViewById(R.id.new_activity);
        }
    }

    public interface ClickListener{
        public void itemClicked(View view,int position);
    }
}
