package com.app.ptt.comnha.Service;

import com.app.ptt.comnha.Modules.DirectionFinder;
import com.app.ptt.comnha.Modules.DirectionFinderListener;
import com.app.ptt.comnha.Modules.Route;

import java.io.UnsupportedEncodingException;

/**
 * Created by cuong on 11/4/2016.
 */

public class LoadJson implements DirectionFinderListener {
    String a;
    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(Route route) {

    }

    @Override
    public void onDistanceFinderSuccess(String a) {
        this.a=a;
    }
    public String returnDistance(){
        return a;
    }

    public void layKhoangCach(String a, String b){
        try {
            new DirectionFinder(this, a, b,"",2).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
