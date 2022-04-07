package com.egamez.org.ui.adapters;

import android.app.Activity;
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
import com.egamez.org.models.GameData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendReqAdapter extends RecyclerView.Adapter<FriendReqAdapter.DataObjectHolder>  {
    private static String LOG_TAG = "FriendReqAdapter";
    private static  List<FriendRequest> friendRequestList;
    private Context mContext;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profileImage;
        TextView nameText;
        TextView desText;

        public DataObjectHolder(View itemView) {
            super(itemView);
            profileImage = (ImageView) itemView.findViewById(R.id.profile_image);
            nameText = (TextView) itemView.findViewById(R.id.name_text);
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

    public FriendReqAdapter(Context mainActivity, List<FriendRequest> friendRequestList) {
        this.friendRequestList = friendRequestList;
        mContext=mainActivity;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_req_item, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        FriendRequest friendRequest = friendRequestList.get(position);
        holder.nameText.setText(friendRequest.getName());
        holder.desText.setText(friendRequest.getDes());
        String url = "https://metaclops.in//uploads/game_image/thumb/1000x500_202108171945131676618913__ludo.jpg";
        Picasso.get().load(url).placeholder(R.drawable.refer_and_earn).fit().into(holder.profileImage);
    }

    public void clear() {
        friendRequestList.clear();
       // notifyDataSetChanged();
    }
    public void filterList(List<FriendRequest> filterllist) {
        // below line is to add our filtered
        // list in our course array list.
        friendRequestList = filterllist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return friendRequestList.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }




}
