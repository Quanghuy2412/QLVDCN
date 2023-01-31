package com.huce.qlvdcn.adapter;

import static com.huce.qlvdcn.adapter.CatagoryAdapter.checkClick;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huce.qlvdcn.R;
import com.huce.qlvdcn.activity.CatagoryActivity;
import com.huce.qlvdcn.activity.MainActivity;
import com.huce.qlvdcn.activity.UpdateActivity;
import com.huce.qlvdcn.model.Item;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
    MainActivity mainActivity;
    CatagoryActivity catagoryActivity;
    List<Item> itemList;

    public ItemAdapter(MainActivity mainActivity, CatagoryActivity catagoryActivity, List<Item> itemList) {
        this.mainActivity = mainActivity;
        this.catagoryActivity = catagoryActivity;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.MyViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.tvItemTitle.setText(item.getTitle());
        holder.tvItemPos.setText(item.getPosition());
        holder.tvItemDate.setText(item.getTime());
        holder.tvItemQuantity.setText(item.getQuantity());

        if (checkClick) {
            //holder.imgDelete.setVisibility(View.GONE);
            Glide.with(catagoryActivity).load(item.getImage()).error(R.drawable.background_null_image).into(holder.imgItemImage);
        } else {
            Glide.with(mainActivity).load(item.getImage()).error(R.drawable.background_null_image).into(holder.imgItemImage);
        }

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkClick) {
                    catagoryActivity.deleteItem(item);
                } else {
                    mainActivity.deleteItem(item);
                }
            }
        });

        holder.ItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkClick) {
                    Intent intent = new Intent(catagoryActivity, UpdateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("object_item", item);
                    intent.putExtras(bundle);
                    catagoryActivity.startActivity(intent);
                    checkClick = false;
                } else {
                    Intent intent = new Intent(mainActivity, UpdateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("object_item", item);
                    intent.putExtras(bundle);
                    mainActivity.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemTitle, tvItemPos, tvItemDate, tvItemQuantity;
        ImageView imgDelete;
        CircleImageView imgItemImage;
        ConstraintLayout ItemLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ItemLayout = itemView.findViewById(R.id.ItemLayout);
            imgItemImage = itemView.findViewById(R.id.imgItemImage);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemPos = itemView.findViewById(R.id.tvItemPos);
            tvItemDate = itemView.findViewById(R.id.tvItemDate);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
        }
    }

    private ClickListener mclickListener;

    public interface ClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(ClickListener clickListener) {
        mclickListener = clickListener;
    }
}

