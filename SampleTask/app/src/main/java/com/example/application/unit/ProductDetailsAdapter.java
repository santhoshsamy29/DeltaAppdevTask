package com.example.application.unit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.application.unit.Product;

import java.util.ArrayList;

public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.ViewHolder> {

    Context context;
    ArrayList<Product> productList;
    private int currentPosition = -1;
    Boolean expandLayout = false;
    Boolean collapseLayout = false;

    public ProductDetailsAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_product_detail,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.productName.setText(productList.get(position).getProdName());
        holder.productPrice.setText("Price : " + String.valueOf(productList.get(position).getPrice()));
        holder.productColor.setText("Color : " + productList.get(position).getColor());
        holder.productSize.setText("Size : " + String.valueOf(productList.get(position).getSize()));
        holder.productQuantity.setText("Quantity : " + String.valueOf(productList.get(position).getQuantity()));
        holder.productStoreName.setText("Store Name : " + productList.get(position).getStoreName());
        holder.productTransactionId.setText("Trans. Id : " + String.valueOf(productList.get(position).getTransactionId()));

        if (currentPosition == position && expandLayout) {

            Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);

            holder.hidingLayout.setVisibility(View.VISIBLE);
            holder.hidingLayout.startAnimation(slideDown);
            expandLayout = false;
        }

        if (currentPosition == position && collapseLayout) {

            Animation slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);

            holder.hidingLayout.startAnimation(slideUp);
            holder.hidingLayout.setVisibility(View.GONE);
            collapseLayout = false;
        }

        holder.showProductDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentPosition = position;
                expandLayout = true;
                holder.showProductDesc.setVisibility(View.INVISIBLE);
                holder.hideProductDesc.setVisibility(View.VISIBLE);
                notifyDataSetChanged();

            }
        });


        holder.hideProductDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentPosition = position;
                collapseLayout = true;
                holder.hideProductDesc.setVisibility(View.INVISIBLE);
                holder.showProductDesc.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView productName,productPrice,productColor,productSize,productQuantity,productStoreName,productTransactionId;
        ImageButton showProductDesc,hideProductDesc;
        LinearLayout hidingLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name_tv);
            productPrice = itemView.findViewById(R.id.product_price_tv);
            productColor = itemView.findViewById(R.id.product_color_tv);
            productSize = itemView.findViewById(R.id.product_size_tv);
            productQuantity = itemView.findViewById(R.id.product_quantity_tv);
            productStoreName = itemView.findViewById(R.id.product_storename_tv);
            productTransactionId = itemView.findViewById(R.id.product_transactionId_tv);
            showProductDesc = itemView.findViewById(R.id.show_desc_button);
            hideProductDesc = itemView.findViewById(R.id.hide_desc_button);

            hidingLayout = itemView.findViewById(R.id.hiding_layout);
        }
    }
}
