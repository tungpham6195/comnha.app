package com.app.ptt.comnha.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.ptt.comnha.FireBase.Report;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 11/26/2016.
 */

public class Report_listview_adapter extends BaseAdapter {
    ArrayList<Report> reports;
    OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void OnItemClick(Report report);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public Report_listview_adapter(ArrayList<Report> reports) {
        this.reports = reports;
    }

    @Override
    public int getCount() {
        return reports.size();
    }

    @Override
    public Object getItem(int position) {
        return reports.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_report_store, null);
            holder = new ViewHolder();
            holder.old_txt_name = (TextView) convertView.findViewById(R.id.item_reportstore_txttenquan);
            holder.old_txt_diachi = (TextView) convertView.findViewById(R.id.item_reportstore_txtdiachi);
            holder.old_txt_gio = (TextView) convertView.findViewById(R.id.item_reportstore_txtgio);
            holder.old_txt_sdt = (TextView) convertView.findViewById(R.id.item_reportstore_txtsdt);
            holder.old_txt_gia = (TextView) convertView.findViewById(R.id.item_reportstore_txtgia);
            holder.txt_name = (TextView) convertView.findViewById(R.id.item_reportstore_txt_newtenquan);
            holder.txt_diachi = (TextView) convertView.findViewById(R.id.item_reportstore_newtxtdiachi);
            holder.txt_gio = (TextView) convertView.findViewById(R.id.item_reportstore_txtnewgio);
            holder.txt_sdt = (TextView) convertView.findViewById(R.id.item_reportstore_txt_newsdt);
            holder.txt_gia = (TextView) convertView.findViewById(R.id.item_reportstore_txt_newgia);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.old_txt_name.setText("Tên cũ: " + reports.get(position).getOld_name());
        holder.old_txt_diachi.setText("Địa chỉ cũ: " + reports.get(position).getOld_address());
        holder.old_txt_gio.setText("Giờ cũ: " + reports.get(position).getOld_timestart()
                + " - " + reports.get(position).getOld_timeend());
        holder.old_txt_sdt.setText("SĐT cũ: " + reports.get(position).getOld_sdt());
        holder.old_txt_gia.setText("Giá cũ: " + reports.get(position).getOld_giamin()
                + " - " + reports.get(position).getOld_giamax());
        holder.txt_name.setText("Tên mới: " + reports.get(position).getOld_name());
        holder.txt_diachi.setText("Địa chỉ mới: " + reports.get(position).getOld_name());
        holder.txt_gio.setText("Giờ mới: " + reports.get(position).getTimestart()
                + " - " + reports.get(position).getTimeend());
        holder.txt_sdt.setText("SĐT mới: " + reports.get(position).getOld_name());
        holder.txt_gia.setText("Giá mới: " + reports.get(position).getGiamin()
                + " - " + reports.get(position).getGiamax());
        return convertView;
    }

    static class ViewHolder {
        TextView txt_name, txt_diachi, txt_gio, txt_sdt, txt_gia;
        TextView old_txt_name, old_txt_diachi, old_txt_gio, old_txt_sdt, old_txt_gia;
    }
}
