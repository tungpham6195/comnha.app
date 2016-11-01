package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.ptt.comnha.FireBase.ThucDon;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 10/30/2016.
 */

public class Thucdon_rcyler_adapter extends RecyclerView.Adapter<Thucdon_rcyler_adapter.ViewHolder> {
    ArrayList<ThucDon> thucDonList;
    Activity activity;

    public Thucdon_rcyler_adapter(ArrayList<ThucDon> thucDonList, Activity activity) {
        this.thucDonList = thucDonList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcyler_thucdon, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txt_gia.setText(thucDonList.get(position).getGia() + "đ");
        holder.txt_tenMon.setText(thucDonList.get(position).getTenmon());
    }

    @Override
    public int getItemCount() {
        return thucDonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_tenMon, txt_gia;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_gia = (TextView) itemView.findViewById(R.id.item_rcyler_thucdon_txtGia);
            txt_tenMon = (TextView) itemView.findViewById(R.id.item_rcyler_thucdon_txttenMon);
        }
    }
}
