package com.example.application.unit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class HorizontalPayAdapter extends RecyclerView.Adapter<HorizontalPayAdapter.ViewHolder> {

    onClickListener clickListener;
    Context context;
    ArrayList<String> paymentArrayList;

    public HorizontalPayAdapter(Context context, ArrayList<String> paymentArrayList) {
        this.context = context;
        this.paymentArrayList = paymentArrayList;
    }

    public void setOnClickListener(HorizontalPayAdapter.onClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_horizontal_image_ut,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Glide.with(context)
                .load(paymentArrayList.get(position))
                .into(holder.paymentImage);

        holder.paymentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.itemClicked(view,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return paymentArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView paymentImage;
        public ViewHolder(View itemView) {
            super(itemView);

            paymentImage = itemView.findViewById(R.id.payment_image);
        }
    }

    public interface onClickListener{
        public void itemClicked(View view,int position);
    }
}
