package com.app.ptt.comnha.Adapters;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
    OnClickPopupMenuListener onClickPopupMenuListener;

    public interface OnClickPopupMenuListener {
        void onCLickPopupMenu(Report report, boolean isAccept);
    }

    public void setOnClickPopupMenuListener(OnClickPopupMenuListener listener) {
        onClickPopupMenuListener = listener;
    }

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
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
            holder.img_option = (ImageView) convertView.findViewById(R.id.item_reportstore_img_option);
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
        holder.txt_name.setText("Tên mới: " + reports.get(position).getName());
        holder.txt_diachi.setText("Địa chỉ mới: " + reports.get(position).getAddress());
        holder.txt_gio.setText("Giờ mới: " + reports.get(position).getTimestart()
                + " - " + reports.get(position).getTimeend());
        holder.txt_sdt.setText("SĐT mới: " + reports.get(position).getSdt());
        holder.txt_gia.setText("Giá mới: " + reports.get(position).getGiamin()
                + " - " + reports.get(position).getGiamax());
        holder.img_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(holder.img_option.getContext(), holder.img_option,
                        Gravity.START | Gravity.TOP);
                popupMenu.getMenuInflater().inflate(R.menu.option_menu_report, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_accept:
                                new AlertDialog.Builder(holder.img_option.getContext())
                                        .setMessage("Bạn chấp chận report này???")
                                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                onClickPopupMenuListener.onCLickPopupMenu(
                                                        reports.get(position), true);
                                            }
                                        })
                                        .setNegativeButton("Trở lại", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                                return true;
                            case R.id.action_deny:
                                new AlertDialog.Builder(holder.img_option.getContext())
                                        .setMessage("Bạn muốn từ chối report này???")
                                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                onClickPopupMenuListener.onCLickPopupMenu(
                                                        reports.get(position), false);
                                            }
                                        })
                                        .setNegativeButton("Trở lại", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView txt_name, txt_diachi, txt_gio, txt_sdt, txt_gia;
        TextView old_txt_name, old_txt_diachi, old_txt_gio, old_txt_sdt, old_txt_gia;
        ImageView img_option;
    }
}
