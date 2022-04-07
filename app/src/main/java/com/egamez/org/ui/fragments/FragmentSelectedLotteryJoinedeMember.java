package com.egamez.org.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.egamez.org.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragmentSelectedLotteryJoinedeMember extends Fragment {

    LinearLayout ll;
    TextView noMember;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_selectedlottery_joinedmember, container, false);

        ll = (LinearLayout) root.findViewById(R.id.lllottery);
        noMember = (TextView) root.findViewById(R.id.nomemberlottery);

        Intent intent = getActivity().getIntent();
        final String wonby = intent.getStringExtra("WONBY");
        final String joinedmember = intent.getStringExtra("JOINMEMBER");

        JSONArray arr = null;
        try {
            arr = new JSONArray(joinedmember);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.equals(arr.toString(), "[]")) {
            noMember.setVisibility(View.VISIBLE);
        } else {
            noMember.setVisibility(View.GONE);
            JSON_PARSE_DATA_AFTER_WEBCALL(arr,wonby);
        }

        return root;
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array,String wonby) {
        int count = 1;
        for (int i = 0; i < array.length(); i++) {

            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                View view = getLayoutInflater().inflate(R.layout.mystatisticsdata, null);
                TextView tvno = (TextView) view.findViewById(R.id.no_mystatistics);
                TextView tvname = (TextView) view.findViewById(R.id.title_mystatistics);
                TextView tv1 = (TextView) view.findViewById(R.id.time_mystatistics);
                TextView tv2 = (TextView) view.findViewById(R.id.paid);
                TextView tv3 = (TextView) view.findViewById(R.id.won);
                tv1.setVisibility(View.GONE);
                tv2.setVisibility(View.GONE);
                tv3.setVisibility(View.GONE);
                tvno.setText("  " + String.valueOf(count) + ".   ");
                if(TextUtils.equals(json.getString("user_name"),wonby)){
                    tvname.setText(json.getString("user_name")+" ("+getActivity().getResources().getString(R.string.winner)+")");
                }else {
                    tvname.setText(json.getString("user_name"));
                }

                ll.addView(view);
                count++;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
