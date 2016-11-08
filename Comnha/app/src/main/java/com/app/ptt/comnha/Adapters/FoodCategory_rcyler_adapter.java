package com.app.ptt.comnha.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.ptt.comnha.FireBase.FoodCategory;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 11/8/2016.
 */

public class FoodCategory_rcyler_adapter extends RecyclerView.Adapter<FoodCategory_rcyler_adapter.ViewHolder> {
    ArrayList<FoodCategory> foodCategories;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtCateName;

        public ViewHolder(View itemView) {
            super(itemView);
            txtCateName = (TextView) itemView.findViewById(R.id.item_rcyler_foodcate_txtcateNameV);
        }
    }

    public FoodCategory_rcyler_adapter(ArrayList<FoodCategory> list) {
        foodCategories = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.
                from(parent.getContext()).inflate(R.layout.item_rcyler_foodcatefory, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtCateName.setText(foodCategories.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return foodCategories.size();
    }
}
