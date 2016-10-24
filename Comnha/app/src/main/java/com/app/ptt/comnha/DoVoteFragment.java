package com.app.ptt.comnha;


import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.app.ptt.comnha.SingletonClasses.DoPost;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class DoVoteFragment extends DialogFragment implements View.OnClickListener, DiscreteSeekBar.OnProgressChangeListener {
    Button btn_vote;
    DiscreteSeekBar mSeekBarGia, mSeekBarVS, mSeekBarPV;
    TextView txt_gia, txt_vs, txt_pv;
    Long gia = (long) 1, vs = (long) 1, pv = (long) 1;

    public DoVoteFragment() {
        // Required empty public constructor
    }

    public static DoVoteFragment newIntance(String title) {
        DoVoteFragment frg = new DoVoteFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frg.setArguments(args);
        return frg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dovote, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anhxa(view);
        String title = getArguments().getString("title", "enter name");
        getDialog().setTitle(title);
//        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void anhxa(View view) {
        txt_gia = (TextView) view.findViewById(R.id.frg_vote_txt_gia);
        txt_vs = (TextView) view.findViewById(R.id.frg_vote_txt_vs);
        txt_pv = (TextView) view.findViewById(R.id.frg_vote_txt_pv);
        txt_vs = (TextView) view.findViewById(R.id.frg_vote_txt_vs);
        txt_pv = (TextView) view.findViewById(R.id.frg_vote_txt_pv);
        mSeekBarGia = (DiscreteSeekBar) view.findViewById(R.id.frg_vote_slide_gia);
        mSeekBarVS = (DiscreteSeekBar) view.findViewById(R.id.frg_vote_slide_vesinh);
        mSeekBarPV = (DiscreteSeekBar) view.findViewById(R.id.frg_vote_slide_phucvu);
        btn_vote = (Button) view.findViewById(R.id.frg_vote_btn_vote);
        btn_vote.setOnClickListener(this);
        txt_pv.setText(getResources().getString(R.string.text_ratepv) + ": " + mSeekBarPV.getMin());
        txt_vs.setText(getResources().getString(R.string.text_ratevs) + ": " + mSeekBarVS.getMin());
        txt_gia.setText(getResources().getString(R.string.text_rategia) + ": " + mSeekBarGia.getMin());
        mSeekBarGia.setOnProgressChangeListener(this);
        mSeekBarPV.setOnProgressChangeListener(this);
        mSeekBarVS.setOnProgressChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DoPost.getInstance().getGia() > 0
                && DoPost.getInstance().getVesinh() > 0
                && DoPost.getInstance().getPhucvu() > 0) {
//            txt_gia.setText(getResources().getString(R.string.text_rategia) + ": " +
//                    DoRate.getInstance().getGia());
//            txt_vs.setText(getResources().getString(R.string.text_ratevs) + ": " +
//                    DoRate.getInstance().getVesinh());
//            txt_pv.setText(getResources().getString(R.string.text_ratepv) + ": " +
//                    DoRate.getInstance().getPhucvu());
            mSeekBarGia.setProgress((int) DoPost.getInstance().getGia());
            mSeekBarVS.setProgress((int) DoPost.getInstance().getVesinh());
            mSeekBarPV.setProgress((int) DoPost.getInstance().getPhucvu());
        } else {
            mSeekBarGia.setProgress((int) DoPost.getInstance().getGia());
            mSeekBarVS.setProgress((int) DoPost.getInstance().getVesinh());
            mSeekBarPV.setProgress((int) DoPost.getInstance().getPhucvu());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frg_vote_btn_vote:
                DoPost.getInstance().setGia(gia);
                DoPost.getInstance().setPhucvu(pv);
                DoPost.getInstance().setVesinh(vs);
                this.dismiss();
                break;
        }
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.frg_vote_slide_gia:
                txt_gia.setText(getResources().getString(R.string.text_rategia) + ": " + String.valueOf(value));
                try {
                    gia = (long) seekBar.getProgress();

                } catch (NullPointerException mess) {
                    gia = (long) 1;
                }
                break;
            case R.id.frg_vote_slide_phucvu:
                txt_pv.setText(getResources().getString(R.string.text_ratepv) + ": " + String.valueOf(value));
                try {
                    pv = (long) seekBar.getProgress();
                } catch (NullPointerException mess) {
                    pv = (long) 1;
                }
                break;
            case R.id.frg_vote_slide_vesinh:
                txt_vs.setText(getResources().getString(R.string.text_ratevs) + ": " + String.valueOf(value));
                try {
                    vs = (long) seekBar.getProgress();
                } catch (NullPointerException mess) {
                    vs = (long) 1;
                }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }
}
