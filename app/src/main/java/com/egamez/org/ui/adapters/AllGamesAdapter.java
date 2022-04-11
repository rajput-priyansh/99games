package com.egamez.org.ui.adapters;

import android.app.Activity;
import android.graphics.Color;
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

public class AllGamesAdapter extends RecyclerView.Adapter<AllGamesAdapter.DataObjectHolder>  {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static List<GameData> gameDataList;
    private Activity mContext;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView gamecardview;
        ImageView gamebaner;
        TextView gamename;
        TextView matchesText;
        ImageView showcaseimage;
        public DataObjectHolder(View itemView) {
            super(itemView);
             gamecardview = (CardView) itemView.findViewById(R.id.gamecardview);
             gamebaner = (ImageView) itemView.findViewById(R.id.gamebanner);
             gamename = (TextView) itemView.findViewById(R.id.gamename);
             matchesText = (TextView) itemView.findViewById(R.id.matches_text);
             showcaseimage = (ImageView) itemView.findViewById(R.id.showcaseimage);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(gameDataList.get(getAdapterPosition()), getAdapterPosition(),v);
        }
    }



    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public AllGamesAdapter(Activity mainActivity, List<GameData> gameDataList) {

        this.gameDataList = gameDataList;
        mContext=mainActivity;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.allgamedata, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        GameData data = gameDataList.get(position);
        holder.matchesText.setText("Matches: " + data.getTotalUpcoming());

        new FancyShowCaseQueue()
                .add(addView(holder.showcaseimage, mContext.getResources().getString(R.string.show_case_1), "1"))
                .show();

        if (data.getGameImage().contains("http://")) {
            data.setGameImage(data.getGameImage().replace("http://", "https://"));
        }
        System.out.println("image>>>>>"+data.getGameImage());
        Picasso.get().load(data.getGameImage()).placeholder(R.drawable.default_battlemania).fit().into(holder.gamebaner);
        holder.gamename.setText(data.getGameName());
    }
    public FancyShowCaseView addView(View view, String title, String n) {
        FancyShowCaseView view1 = new FancyShowCaseView.Builder(mContext)
                .focusOn(view)
                .title(title)
                //.titleGravity(Gravity.BOTTOM)
                .titleSize(20, 1)
                .focusShape(FocusShape.CIRCLE)
                .enableAutoTextPosition()
                .roundRectRadius(10)
                .focusBorderSize(1)
                .showOnce(n)
                .focusBorderColor(mContext.getResources().getColor(R.color.newblue))
                .dismissListener(new DismissListener() {
                    @Override
                    public void onDismiss(String s) {

                    }

                    @Override
                    public void onSkipped(String s) {

                    }
                })
                .build();

        return view1;
    }

    public void clear() {
        gameDataList.clear();
       // notifyDataSetChanged();
    }
    public void addAll(List<GameData> localGamesData) {
        //gameDataList.clear();
        gameDataList.addAll(localGamesData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return gameDataList.size();
    }

    public interface MyClickListener {
        public void onItemClick(GameData gameData,int position, View v);
    }




}
