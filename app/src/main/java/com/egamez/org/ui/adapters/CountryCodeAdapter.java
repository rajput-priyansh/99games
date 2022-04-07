//For show data in my statics
package com.egamez.org.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egamez.org.R;
import com.egamez.org.events.CountryCodeItemClick;
import com.egamez.org.models.MyStatisticsData;
import com.egamez.org.ui.activities.CreateNewAccount;

import java.util.ArrayList;
import java.util.List;

public class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.MyViewHolder> {

    private final List<String> spinnerArray;
    private List<String> spinnerArraySearch;
    private final List<String> spinnerArrayCountryCode;
    private List<String> spinnerArrayCountryCodeSearch;
    private final CountryCodeItemClick mClick;
    private Context mContext;

    public CountryCodeAdapter(Activity mContext, List<String> spinnerArray, List<String> spinnerArrayCountryCode, CountryCodeItemClick mClick) {
        this.mClick = mClick;
        this.mContext = mContext;
        this.spinnerArraySearch = new ArrayList<String>();
        this.spinnerArraySearch.addAll(spinnerArray);
        this.spinnerArrayCountryCodeSearch = new ArrayList<String>();
        this.spinnerArrayCountryCodeSearch.addAll(spinnerArrayCountryCode);
        this.spinnerArray = spinnerArray;
        this.spinnerArrayCountryCode = spinnerArrayCountryCode;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.spinner_item_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        spinnerArray.get(position);
        holder.tv.setText(spinnerArrayCountryCode.get(position) + " (" + spinnerArray.get(position) + ")");
        holder.tv.setTag(position);
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (int) view.getTag();
                mClick.onCountryCodeItemClick(view, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return spinnerArray.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase();
        spinnerArray.clear();
        spinnerArrayCountryCode.clear();
        if (charText.length() == 0) {
            spinnerArray.addAll(spinnerArraySearch);
            spinnerArrayCountryCode.addAll(spinnerArrayCountryCodeSearch);
        } else {
            for (int i = 0; i < spinnerArraySearch.size(); i++) {
                if (spinnerArraySearch.get(i).toLowerCase().contains(charText) || spinnerArrayCountryCodeSearch.get(i).toLowerCase().contains(charText)) {
                    spinnerArray.add(spinnerArraySearch.get(i));
                    spinnerArrayCountryCode.add(spinnerArrayCountryCodeSearch.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }
}
