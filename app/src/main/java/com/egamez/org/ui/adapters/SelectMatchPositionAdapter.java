//For show checkbox checked or unchecked in select position page
package com.egamez.org.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egamez.org.R;
import com.egamez.org.models.JoinSingleMatchData;

import java.util.ArrayList;
import java.util.List;

public class SelectMatchPositionAdapter extends RecyclerView.Adapter<SelectMatchPositionAdapter.MyViewHolder> {

    final public List<String> checkBoxList = new ArrayList<String>();
    private Context mContext;
    private List<JoinSingleMatchData> mData;

    public SelectMatchPositionAdapter(Context mContext, List<JoinSingleMatchData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.selectmatchposition_data, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final JoinSingleMatchData data = mData.get(position);


        if (!data.getUsername().trim().matches("") && !data.getPubgid().trim().matches("")) {
            holder.checkBox.setChecked(true);
            holder.checkBox.setEnabled(false);
            holder.checkTv.setEnabled(true);
        }
        holder.checkTv.setText(data.getPosition());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    // JoinSingleMatchData sdata=mList.get(finalK);
                    checkBoxList.add("{'team': '" + data.getTeam() + "','position':'" + data.getPosition() + "'}");
                } else {
                    checkBoxList.remove("{'team': '" + data.getTeam() + "','position':'" + data.getPosition() + "'}");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
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

        CheckBox checkBox;
        TextView checkTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            checkTv = (TextView) itemView.findViewById(R.id.checktv);

        }
    }
}
