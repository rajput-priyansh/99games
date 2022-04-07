//for show order in list
package com.egamez.org.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableStringBuilder;
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
import com.egamez.org.models.OrderData;
import com.egamez.org.ui.activities.SingleOrderActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public  class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    private Context mContext;
    private List<OrderData> mData;

    public OrderAdapter(Context mContext, List<OrderData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.order_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        OrderData data = mData.get(position);
        if (!data.getpImage().equals("")) {
            Picasso.get().load(Uri.parse(data.getpImage())).placeholder(R.mipmap.app_logo).fit().into(holder.iv);
        } else {
            holder.iv.setImageDrawable(mContext.getDrawable(R.mipmap.app_logo));
        }
        holder.ordername.setText(data.getOrderNo());
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(Html.fromHtml(mContext.getResources().getString(R.string.price__)+" "))
                .append(" ", new ImageSpan(mContext, R.drawable.resize_coin,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")
                .append(Html.fromHtml(data.getpPrice()));
        holder.price.setText(builder);

        holder.status.setText(mContext.getResources().getString(R.string.status__)+" "+data.getOrderStatus().substring(0, 1).toUpperCase() + data.getOrderStatus().substring(1).toLowerCase());

        holder.date.setText((mContext.getResources().getString(R.string.date__)+" "+data.getCreatedDate()));

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(mContext, SingleOrderActivity.class);
                intent.putExtra("ordername",data.getOrderNo());
                intent.putExtra("pname",data.getpName());
                intent.putExtra("price",data.getpPrice());
                intent.putExtra("image",data.getpImage());
                intent.putExtra("uname",data.getUserName());
                intent.putExtra("add",data.getAddress());
                intent.putExtra("status",data.getOrderStatus().substring(0, 1).toUpperCase() + data.getOrderStatus().substring(1).toLowerCase());
                intent.putExtra("tracklink",data.getCourierLink());
                intent.putExtra("additional",data.getAdditional());
                intent.putExtra("date",data.getCreatedDate());
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

        CardView cv;
        ImageView iv;
        TextView ordername;
        TextView price;
        TextView status;
        TextView date;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cv=(CardView)itemView.findViewById(R.id.ordercv);
            iv=(ImageView)itemView.findViewById(R.id.pimage);
            ordername=(TextView)itemView.findViewById(R.id.ordername);
            price=(TextView)itemView.findViewById(R.id.price);
            status=(TextView)itemView.findViewById(R.id.status);
            date=(TextView)itemView.findViewById(R.id.date);

        }
    }

}
