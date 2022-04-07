//For show data in my statics
package com.egamez.org.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egamez.org.R;
import com.egamez.org.models.MyStatisticsData;

import java.util.List;

public class MyStatisticsAdapter extends RecyclerView.Adapter<MyStatisticsAdapter.MyViewHolder> {

    private Context mContext;
    private List<MyStatisticsData> mData;

    public MyStatisticsAdapter(Context mContext, List<MyStatisticsData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.mystatisticsdata, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        int count = mData.size() - position;
        holder.noMyStatistics.setText(String.valueOf(count));
        MyStatisticsData data = mData.get(position);
        holder.titleMyStatistics.setText(data.getMatchname() + " - "+mContext.getResources().getString(R.string.match)+" #" + data.getMid());
        holder.timeMystatistics.setText(data.getMatchtime());
        holder.paid.setText(data.getPaid());
        holder.won.setText(data.getWon());
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
        TextView noMyStatistics;
        TextView titleMyStatistics;
        TextView timeMystatistics;
        TextView paid;
        TextView won;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            noMyStatistics = (TextView) itemView.findViewById(R.id.no_mystatistics);
            titleMyStatistics = (TextView) itemView.findViewById(R.id.title_mystatistics);
            timeMystatistics = (TextView) itemView.findViewById(R.id.time_mystatistics);
            paid = (TextView) itemView.findViewById(R.id.paid);
            won = (TextView) itemView.findViewById(R.id.won);
        }
    }
}
