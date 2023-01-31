package com.huce.qlvdcn.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huce.qlvdcn.R;
import com.huce.qlvdcn.activity.CatagoryActivity;
import com.huce.qlvdcn.activity.MainActivity;
import com.huce.qlvdcn.activity.UpdateActivity;
import com.huce.qlvdcn.model.Catagory;

import java.util.ArrayList;
import java.util.List;

public class CatagoryAdapter extends RecyclerView.Adapter<CatagoryAdapter.ViewHolder> {
    List<Catagory> catagories;
    MainActivity mainActivity;

    public static boolean checkClick = false;

    public CatagoryAdapter(List<Catagory> catagories, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.catagories = catagories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_catagory, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Catagory catagory = catagories.get(position);
        holder.catagoryName.setText(catagory.getName());
        String picUrl = "";
        switch (position) {

            case 0: {
                picUrl = "cat_1";
                holder.mainLayout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.catag_background));
                break;
            }
            case 1: {
                picUrl = "cat_2";
                holder.mainLayout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.catag_background));
                break;
            }
            case 2: {
                picUrl = "cat_3";
                holder.mainLayout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.catag_background));
                break;
            }
            case 3: {
                picUrl = "cat_4";
                holder.mainLayout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.catag_background));
                break;
            }
            case 4: {
                picUrl = "cat_5";
                holder.mainLayout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.catag_background));
                break;
            }
        }
        int drawableResourceId = holder.itemView.getContext().getResources().getIdentifier(picUrl, "drawable", holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext()).load(drawableResourceId).into(holder.catagoryPic);

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkClick = true;
                Intent intent = new Intent(mainActivity, CatagoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("object_catagory", catagory);
                intent.putExtras(bundle);
                mainActivity.startActivity(intent);
            }

        });


    }

    @Override
    public int getItemCount() {
        return catagories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView catagoryName;
        ImageView catagoryPic;
        ConstraintLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catagoryName = itemView.findViewById(R.id.catagoryName);
            catagoryPic = itemView.findViewById(R.id.catagoryPic);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
