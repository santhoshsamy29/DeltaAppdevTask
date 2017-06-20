package com.example.application.deltatasktwo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    Context context;
    ArrayList<CardView> img_list;

    MainActivity.OnClickListener OnClickListener;

    public void setOnClickListener(MainActivity.OnClickListener OnClickListener){
        this.OnClickListener = OnClickListener;
    }

    public ImageAdapter(Context context,ArrayList<CardView> img_list) {
        this.context = context;
        this.img_list = img_list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_image,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.caption_tv.setText(img_list.get(position).caption);
        holder.img.setImageURI(img_list.get(position).image);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickListener.onClick(img_list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return img_list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView caption_tv;
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            caption_tv = (TextView) itemView.findViewById(R.id.caption_tv);
            img = (ImageView) itemView.findViewById(R.id.img);
        }
    }
}
