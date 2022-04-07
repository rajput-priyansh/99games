//for show data in result fragment
package com.egamez.org.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.egamez.org.R;
import com.egamez.org.models.LotteryData;
import com.egamez.org.ui.activities.SelectedLotteryActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LotteryResultAdapter extends RecyclerView.Adapter<LotteryResultAdapter.MyViewHolder> {

    private Context mContext;
    private List<LotteryData> mData;


    public LotteryResultAdapter(Context mContext, List<LotteryData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.lottery_result_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {



        SharedPreferences sp = mContext.getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
       // String selectedcurrency = sp.getString("currency", "₹");
        SpannableStringBuilder builder = new SpannableStringBuilder();


        final LotteryData data = mData.get(position);

        if(!TextUtils.equals(data.getlImage(),"")){
            Picasso.get().load(Uri.parse(data.getlImage())).placeholder(R.drawable.lucky_draw).fit().into(holder.limageview);
        }

        builder.append(Html.fromHtml(mContext.getResources().getString(R.string.won_prize__)+" "))
                .append(" ", new ImageSpan(mContext, R.drawable.resize_coin1618,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")
                .append(Html.fromHtml(""+data.getlPrize()));
        holder.winprize.setText(builder);

        holder.title.setText(data.getlTitla()+" - "+mContext.getResources().getString(R.string.lottery)+" #"+data.getlId());
        holder.time.setText(mContext.getResources().getString(R.string.draw_on)+" : "+data.getlTime().substring(0,11));
        holder.wonby.setText(mContext.getResources().getString(R.string.won_by)+" : "+data.getlWonBy());


        holder.lotteryresultcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, SelectedLotteryActivity.class);
                intent.putExtra("FROM","RESULT");
                intent.putExtra("LID",data.getlId());
                intent.putExtra("TITLE",data.getlTitla());
                intent.putExtra("BANER",data.getlImage());
                intent.putExtra("TIME",data.getlTime());
                intent.putExtra("ENTRYFEE",data.getLfee());
                intent.putExtra("PRIZE",data.getlPrize());
                intent.putExtra("WONBY",data.getlWonBy());
                intent.putExtra("ABOUT",data.getlRule());
                intent.putExtra("JOINMEMBER",data.getlJoinedMember());
                mContext.startActivity(intent);
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

        CardView lotteryresultcv;
        ImageView limageview;
        TextView winprize;
        TextView title;
        TextView time;
        TextView wonby;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            lotteryresultcv=(CardView)itemView.findViewById(R.id.lotteryresultcardview);
            limageview=(ImageView)itemView.findViewById(R.id.lrimageview);
            winprize = (TextView) itemView.findViewById(R.id.lrwonprize);
            title = (TextView) itemView.findViewById(R.id.lrtitle);
            time = (TextView) itemView.findViewById(R.id.lrtime);
            wonby = (TextView) itemView.findViewById(R.id.lrwonby);
        }
    }
}
