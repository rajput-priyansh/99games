package com.egamez.org.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egamez.org.R;
import com.egamez.org.models.TimeSlotBean;
import com.egamez.org.models.TournamentData;
import com.egamez.org.ui.activities.SelectedTournamentActivity;

import java.util.ArrayList;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

    ArrayList<TournamentData.AllplayMatch.UniqueCodeMatch> mArrayList;
    Activity mActivity;
    TournamentData.AllplayMatch.UniqueCodeMatch mBean;

    public TimeSlotAdapter(List<TournamentData.AllplayMatch.UniqueCodeMatch> mArrayList, Activity mActivity) {
        this.mArrayList = new ArrayList<>();
        this.mArrayList.addAll(mArrayList);
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public TimeSlotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_time_slot, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotAdapter.ViewHolder holder, int position) {
        mBean = mArrayList.get(position);
        holder.rbSelectTimeSlot.setText(mBean.getMatchTime());
        if (mBean.getSelected()) {
            holder.rbSelectTimeSlot.setChecked(true);
        } else {
            holder.rbSelectTimeSlot.setChecked(false);
        }

        if (mBean.getJoinStatus()) {
            holder.rbSelectTimeSlot.setChecked(true);
            holder.rbSelectTimeSlot.setEnabled(false);
            holder.txtViewMatchDetails.setVisibility(View.VISIBLE);
        } else {
            if (mBean.getSelected()) {
                holder.rbSelectTimeSlot.setChecked(true);
            } else {
                holder.rbSelectTimeSlot.setChecked(false);
            }
            holder.rbSelectTimeSlot.setEnabled(true);
            holder.txtViewMatchDetails.setVisibility(View.GONE);
        }
        holder.txtViewMatchDetails.setTag(position);
        holder.txtViewMatchDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (int) view.getTag();
                Intent intent = new Intent(mActivity, SelectedTournamentActivity.class);
                intent.putExtra("M_ID", mArrayList.get(pos).getmId() + "");
                intent.putExtra("FROM", "PLAY");
                intent.putExtra("BANER", mArrayList.get(pos).getMatchBanner() + "");
                intent.putExtra("GAME_NAME", mArrayList.get(pos).getGameName() + "");
                mActivity.startActivity(intent);
            }
        });
        holder.rbSelectTimeSlot.setTag(position);
        holder.rbSelectTimeSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (int) view.getTag();
                for (int i = 0; i < mArrayList.size(); i++) {
                    if (i == pos) {
                        if (mArrayList.get(pos).getSelected()) {
                            mArrayList.get(pos).setSelected(false);
                        } else {
                            mArrayList.get(pos).setSelected(true);
                        }
                    } else {
                        mArrayList.get(i).setSelected(false);
                    }
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RadioButton rbSelectTimeSlot;
        TextView txtViewMatchDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            rbSelectTimeSlot = (RadioButton) itemView.findViewById(R.id.rbSelectTimeSlot);
            txtViewMatchDetails = (TextView) itemView.findViewById(R.id.txtViewMatchDetails);
        }
    }
}
