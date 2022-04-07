package com.egamez.org.ui.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.egamez.org.R;
import com.egamez.org.models.GameData;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.DismissListener;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.DataObjectHolder>  {
    private static String LOG_TAG = "AnnouncementAdapter";
    private static List<String> announcmentList;
    private Activity mContext;
   // private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView gameImageView;

        public DataObjectHolder(View itemView) {
            super(itemView);
            gameImageView = (ImageView) itemView.findViewById(R.id.game_image);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //myClickListener.onItemClick(gameDataList.get(getAdapterPosition()), getAdapterPosition(),v);
        }
    }



//    public void setOnItemClickListener(MyClickListener myClickListener) {
//        this.myClickListener = myClickListener;
//    }

    public AnnouncementAdapter(Activity mainActivity, List<String> announcmentList) {
        this.announcmentList = announcmentList;
        mContext=mainActivity;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.announcement_item, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        String url = "https://metaclops.in//uploads/game_image/thumb/1000x500_202108171945131676618913__ludo.jpg";
        Picasso.get().load(url).placeholder(R.drawable.refer_and_earn).fit().into(holder.gameImageView);
    }

    public void clear() {
        announcmentList.clear();
       // notifyDataSetChanged();
    }
    public void addAll(List<String> localGamesData) {
        //gameDataList.clear();
        announcmentList.addAll(localGamesData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return announcmentList.size();
    }

    public interface MyClickListener {
        public void onItemClick(GameData gameData,int position, View v);
    }




}
