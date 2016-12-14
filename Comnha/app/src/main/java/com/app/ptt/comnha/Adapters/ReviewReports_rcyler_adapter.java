package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 10/4/2016.
 */

public class ReviewReports_rcyler_adapter extends RecyclerView.Adapter<ReviewReports_rcyler_adapter.ViewHolder> {
    ArrayList<Post> list;
    Activity activity;
    LayoutInflater inflater;
    int previuosPosition = 0;
    onClickPopUpListener onClickPopUpListener;

    public interface onClickPopUpListener {
        void onClick(boolean isAccept, Post post);
    }

    public void setOnClickPopUpListener(onClickPopUpListener listener) {
        this.onClickPopUpListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_un, txt_postdate, txt_tittle,
                txt_content, txt_time, txt_tenquan, txt_diachi;
        public ImageView img_user, img_popup;

        public ViewHolder(View view) {
            super(view);
            txt_time = (TextView) view.findViewById(R.id.reviewreport_txtv_time);
            txt_un = (TextView) view.findViewById(R.id.reviewreport_txtv_username);
            txt_tittle = (TextView) view.findViewById(R.id.reviewreport_txtv_tittle);
            txt_postdate = (TextView) view.findViewById(R.id.reviewreport_txtv_postdate);
            txt_content = (TextView) view.findViewById(R.id.reviewreport_txtv_content);
            txt_tenquan = (TextView) view.findViewById(R.id.reviewreport_txtv_tenquan);
            txt_diachi = (TextView) view.findViewById(R.id.reviewreport_txtv_diachi);
            img_user = (ImageView) view.findViewById(R.id.reviewreport_img_user);
            img_popup = (ImageView) view.findViewById(R.id.reviewreport_imgPopup);
        }
    }

    public ReviewReports_rcyler_adapter(ArrayList<Post> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcyler_reviewreport, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txt_tittle.setText(list.get(position).getTitle());
        holder.txt_content.setText(list.get(position).getContent());
        holder.txt_postdate.setText(list.get(position).getDate());
        holder.txt_time.setText(list.get(position).getTime());
        holder.txt_un.setText(list.get(position).getUsername());
        holder.txt_tenquan.setText(list.get(position).getLocaName());
        holder.txt_diachi.setText(list.get(position).getDiachi());
        holder.img_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(holder.img_popup.getContext(), holder.img_popup, Gravity.START);
                popupMenu.getMenuInflater().inflate(R.menu.option_menu_report, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_accept:
                                new AlertDialog.Builder(holder.img_popup.getContext())
                                        .setMessage("Bạn chấp chận report này???")
                                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                onClickPopUpListener.onClick(true,
                                                        list.get(position));
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
                                new AlertDialog.Builder(holder.img_popup.getContext())
                                        .setMessage("Bạn muốn từ chối report này???")
                                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                onClickPopUpListener.onClick(false,
                                                        list.get(position));
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
//        if (position > previuosPosition) {
//            AnimationUtils.animateItemRcylerV(holder, true);
//
//        } else {
//            AnimationUtils.animateItemRcylerV(holder, false);
//
//        }
//        previuosPosition = position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
