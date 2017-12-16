package com.example.application.unit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class HorizontalImageAdapterUt extends RecyclerView.Adapter<HorizontalImageAdapterUt.ViewHolder>{

    Context context;
    ArrayList<Product> products;
    public HorizontalImageAdapterUt(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
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
                .load(products.get(position).getImg_url())
                .into(holder.horizontalImage);
        holder.imageName.setText(products.get(position).getProdName());

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView horizontalImage;
        TextView imageName;

        public ViewHolder(View itemView) {
            super(itemView);

            horizontalImage = itemView.findViewById(R.id.payment_image);
            imageName = itemView.findViewById(R.id.img_name_tv);
        }
    }

}