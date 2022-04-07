//For show data in top player list
package com.egamez.org.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egamez.org.R;
import com.egamez.org.models.TopplayerData;

import java.util.List;

public class TopplayerAdapter extends RecyclerView.Adapter<TopplayerAdapter.MyViewHolder> {

    private Context mContext;
    private List<TopplayerData> mData;

    public TopplayerAdapter(Context mContext, List<TopplayerData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.topplayer_data, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TopplayerData topplayerData = mData.get(position);
        if (TextUtils.equals(topplayerData.getGamename(), "")) {
            holder.headerLl.setVisibility(View.GONE);
        } else {
            holder.headerLl.setVisibility(View.VISIBLE);
            holder.gameHeader.setText(topplayerData.getGamename());
        }
        holder.pubgId.setText(topplayerData.getUsername());
        SharedPreferences sp = mContext.getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
        //String selectedcurrency = sp.getString("currency", "₹");

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(Html.fromHtml(""))
                .append(" ", new ImageSpan(mContext, R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")
                .append(Html.fromHtml(""+topplayerData.getWinnning()));
        holder.winning.setText(builder);
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

        TextView pubgId;
        TextView winning;
        TextView gameHeader;
        LinearLayout headerLl;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            pubgId = (TextView) itemView.findViewById(R.id.pubg_id);
            winning = (TextView) itemView.findViewById(R.id.winning);
            gameHeader = (TextView) itemView.findViewById(R.id.gameheader);
            headerLl = (LinearLayout) itemView.findViewById(R.id.headerll);

        }
    }
}
