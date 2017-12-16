package com.example.application.unit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.application.unit.Product;

import java.util.ArrayList;

public class HorizontalImagesAdapter extends RecyclerView.Adapter<HorizontalImagesAdapter.ViewHolder>{

    onClickListener clickListener;
    Context context;
    ArrayList<Product> products;
    public HorizontalImagesAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    public void setOnClickListener(HorizontalImagesAdapter.onClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_horizontal_image,null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Glide.with(context)
                .load(products.get(position).getImg_url())
                .into(holder.horizontalImage);
        //holder.horizontalImage.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),drawables[position]));
        //holder.horizontalImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        /*holder.imageCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                products.remove(position);
                notifyDataSetChanged();
                productDetailsAdapter.notifyDataSetChanged();
            }
        });*/

        holder.imageCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.itemClicked(view,position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView horizontalImage;
        ImageButton imageCloseButton,imageHeartButton;

        public ViewHolder(View itemView) {
            super(itemView);

            horizontalImage = itemView.findViewById(R.id.horizontal_images);
            imageCloseButton = itemView.findViewById(R.id.image_close_button);
            imageHeartButton = itemView.findViewById(R.id.image_heart_button);
        }
    }

    public interface onClickListener{
        public void itemClicked(View view,int position);
    }
}