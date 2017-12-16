package com.example.application.unit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class HorizontalNumbersAdapter extends RecyclerView.Adapter<HorizontalNumbersAdapter.ViewHolder>{

    Context context;
    ArrayList<Integer> numberArrayList;
    HorizontalNumbersAdapter.onClickListener clickListener;
    int currentPosition = -1;
    ArrayList<Boolean> click = new ArrayList<>();;

    public void setOnClickListener(HorizontalNumbersAdapter.onClickListener clickListener){
        this.clickListener = clickListener;
    }

    public HorizontalNumbersAdapter(Context context, ArrayList<Integer> numberArrayList) {
        this.context = context;
        this.numberArrayList = numberArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_number,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        for(int i=0 ; i<this.numberArrayList.size() ; i++){
            click.add(false);
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.number.setText(numberArrayList.get(position).toString());

        if(currentPosition == position && click.get(position)){
            holder.quantityNumber.setBackgroundResource(R.drawable.selected_round_corner);
        }
        if(currentPosition == position && !click.get(position)){
            holder.quantityNumber.setBackgroundResource(R.drawable.round_corner);
        }

        holder.quantityNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentPosition = position;

                if(click.get(currentPosition)){
                    click.set(currentPosition,false);
                } else if(!click.get(currentPosition)) {
                    click.set(currentPosition,true);
                }
                notifyDataSetChanged();

                clickListener.itemClicked(view,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return numberArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout quantityNumber;
        TextView number;
        public ViewHolder(View itemView) {
            super(itemView);

            quantityNumber = itemView.findViewById(R.id.quantity_number);
            number = itemView.findViewById(R.id.quantity_number_tv);
        }
    }

    public interface onClickListener{
        public void itemClicked(View view,int position);
    }
}
