package com.egamez.org.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.egamez.org.R;

public class FragmentSelectedLotteryDescription extends Fragment {

    TextView titletv;
    TextView timetv;
    TextView prizetv;
    TextView feetv;
    TextView abouttv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_selectedlottery_description, container, false);

        titletv=(TextView)root.findViewById(R.id.lotterytitledesc);
        timetv=(TextView)root.findViewById(R.id.lotterytimedesc);
        prizetv=(TextView)root.findViewById(R.id.lotteryplayfordesc);
        feetv=(TextView)root.findViewById(R.id.lotteryfeedesc);
        abouttv=(TextView)root.findViewById(R.id.aboutlottery);

        Intent intent=getActivity().getIntent();
        String title=intent.getStringExtra("TITLE");
        String lid=intent.getStringExtra("LID");
        String time=intent.getStringExtra("TIME");
        String prize=intent.getStringExtra("PRIZE");
        String entryfee=intent.getStringExtra("ENTRYFEE");
        String about=intent.getStringExtra("ABOUT");

        titletv.setText(title+" "+getActivity().getResources().getString(R.string.lottery)+" #"+lid);
        timetv.setText(getActivity().getResources().getString(R.string.result_on)+"\n"+time);
        prizetv.setText(getActivity().getResources().getString(R.string.play_for)+"\n"+prize);
        feetv.setText(getActivity().getResources().getString(R.string.fees)+"\n"+entryfee);
        abouttv.setText(Html.fromHtml(about));

        return root;
    }
}
