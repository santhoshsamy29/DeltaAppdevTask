package com.example.application.pokedex;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyHolder> {

    Context context;
    ArrayList<HistoryPokemon> poke_arr;

    public HistoryAdapter(Context context, ArrayList<HistoryPokemon> tpoke_arr) {
        this.context = context;
        this.poke_arr = tpoke_arr;

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_history,parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.name.setText(poke_arr.get(position).name);
        Log.d("SAN","SPRITE : " + poke_arr.get(position).image);
        if(poke_arr.get(position).image == "null"){
            Glide.with(context)
                    .load(R.drawable.pokeball)
                    .into(holder.img);

        }else{
            Glide.with(context)
                    .load(poke_arr.get(position).image)
                    .into(holder.img);
        }

    }

    @Override
    public int getItemCount() {
        return poke_arr.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView img;
        TextView name;

        public MyHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.history_name);
            img = (ImageView) itemView.findViewById(R.id.history_img);
        }
    }
}
