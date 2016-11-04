package com.app.ptt.comnha;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PickDistrictDialogFragment extends DialogFragment {
    ListView mListView;

    public interface OnPickDistricListener {
        void onPickDistrict(String district);
    }

    OnPickDistricListener onPickDistricListener;

    public PickDistrictDialogFragment() {
        // Required empty public constructor
    }

    private int whatprovince;

    public void setWhatprovince(int whatprovince) {
        this.whatprovince = whatprovince;
    }

    private ArrayAdapter<CharSequence> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pick_district_dialog, container, false);
        anhxa(view);
        return view;
    }

    private void anhxa(View view) {
        mListView = (ListView) view.findViewById(R.id.frg_pickDistrict_listV);
        switch (whatprovince) {
            case 1:
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.hanoi, android.R.layout.simple_list_item_1);
                break;
            case 2:
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.hochiminh, android.R.layout.simple_list_item_1);
                break;
        }
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onPickDistricListener.onPickDistrict(adapter.getItem(position).toString());
                dismiss();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(getResources().getString(R.string.text_pickDistrict));
    }

    public void setOnPickDistricListener(OnPickDistricListener listener) {
        onPickDistricListener = listener;
    }
}
