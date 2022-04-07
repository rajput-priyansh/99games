package com.egamez.org.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.egamez.org.R;
import com.egamez.org.models.FriendRequest;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.DataObjectHolder>  {
    private static String LOG_TAG = "NotificationAdapter";
    private static  List<String> notificationList;
    private Context mContext;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profileImage;
        TextView titleText;
        TextView desText;

        public DataObjectHolder(View itemView) {
            super(itemView);
            profileImage = (ImageView) itemView.findViewById(R.id.profile_image);
            titleText = (TextView) itemView.findViewById(R.id.title_text);
            desText = (TextView) itemView.findViewById(R.id.des_text);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(),v);
        }
    }



    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public NotificationAdapter(Context mainActivity, List<String> notificationList) {
        this.notificationList = notificationList;
        mContext=mainActivity;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.titleText.setText(notificationList.get(position));
        //holder.desText.setText(friendRequest.getDes());
        String url = "https://metaclops.in//uploads/game_image/thumb/1000x500_202108171945131676618913__ludo.jpg";
        Picasso.get().load(url).placeholder(R.drawable.refer_and_earn).fit().into(holder.profileImage);
    }

    public void clear() {
        notificationList.clear();
       // notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }




}
