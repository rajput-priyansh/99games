package com.egamez.org.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egamez.org.R;
import com.egamez.org.models.HowToPlayBean;
import com.egamez.org.models.TournamentData;
import com.egamez.org.ui.activities.SelectedTournamentActivity;

import java.util.ArrayList;
import java.util.List;

public class HowToPlayAdapter extends RecyclerView.Adapter<HowToPlayAdapter.ViewHolder> {

    ArrayList<HowToPlayBean> mArrayList;
    Activity mActivity;
    HowToPlayBean mBean;

    public HowToPlayAdapter(List<HowToPlayBean> mArrayList, Activity mActivity) {
        this.mArrayList = new ArrayList<>();
        this.mArrayList.addAll(mArrayList);
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public HowToPlayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_youtube_link, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HowToPlayAdapter.ViewHolder holder, int position) {
        mBean = mArrayList.get(position);
        holder.txtYoutubeLink.setText(mBean.getYoutube_link_title());
        holder.txtYoutubeLink.setTag(position);
        holder.txtYoutubeLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (int) view.getTag();
                mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mArrayList.get(pos).getYoutube_link())));
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

        TextView txtYoutubeLink;

        public ViewHolder(View itemView) {
            super(itemView);
            txtYoutubeLink = (TextView) itemView.findViewById(R.id.txtYoutubeLink);
        }
    }
}
