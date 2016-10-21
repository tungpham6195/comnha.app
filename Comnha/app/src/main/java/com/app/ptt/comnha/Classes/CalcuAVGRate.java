package com.app.ptt.comnha.Classes;

import com.app.ptt.comnha.FireBase.Post;

import java.util.ArrayList;

/**
 * Created by PTT on 10/10/2016.
 */

public class CalcuAVGRate {

    ArrayList<Post> list;

    public CalcuAVGRate(ArrayList<Post> list) {
        this.list = list;
    }

    public ArrayList<Long> calcu() {
        long giasum = 0, vsinhsum = 0, phucvusum = 0;
        long size = list.size();
        for (Post item : list) {
            giasum += item.getGia();
            vsinhsum += item.getVesinh();
            phucvusum += item.getPhucvu();
        }
        ArrayList<Long> endlist = new ArrayList<Long>();
        endlist.add(0, giasum / size);
        endlist.add(1, vsinhsum / size);
        endlist.add(2, phucvusum / size);
        return endlist;
    }
}