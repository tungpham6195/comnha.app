package com.app.ptt.comnha;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.app.ptt.comnha.Modules.Route;

import java.util.ArrayList;

public class MapFragment extends Fragment {
    private AutoCompleteTextView acText;
    private ArrayList<Route> list;
    private ArrayList<String> listName;
    public void getMethod(ArrayList<Route> list){
        this.list=list;
        listName=new ArrayList<String>();
        for(Route a: list){
            listName.add(a.startAddress.toString());
            Log.i("LOG",a.startAddress.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map,container,false);
        acText=(AutoCompleteTextView) view.findViewById(R.id.acText);
        ArrayAdapter arrayAdapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,listName);
        acText.setAdapter(arrayAdapter);
        acText.setThreshold(1);
        return view;
    }

}
