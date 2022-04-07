//for show product in list
package com.egamez.org.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.egamez.org.R;
import com.egamez.org.models.ProductData;
import com.egamez.org.ui.activities.SingleProductActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private Context mContext;
    private List<ProductData> mData;

    public ProductAdapter(Context mContext, List<ProductData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.product_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ProductData data = mData.get(position);
        if (!data.getpImage().equals("")) {
            Picasso.get().load(Uri.parse(data.getpImage())).placeholder(R.mipmap.app_logo).fit().into(holder.imageView);
        } else {

            holder.imageView.setImageDrawable(mContext.getDrawable(R.mipmap.app_logo));
        }

        holder.name.setText(data.getpName());
        holder.aprice.setText(data.getPaPrice());
        holder.aprice.setPaintFlags(  holder.aprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.sprice.setText(data.getPsPrice());

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, SingleProductActivity.class);
                intent.putExtra("pid",data.getpId());
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
        ImageView imageView;
        TextView name;
        TextView aprice;
        TextView sprice;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cv=(CardView)itemView.findViewById(R.id.pcv);
            imageView=(ImageView)itemView.findViewById(R.id.pimage);
            name=(TextView)itemView.findViewById(R.id.pname);
            aprice=(TextView)itemView.findViewById(R.id.paprice);
            sprice=(TextView)itemView.findViewById(R.id.psprice);
        }
    }
}
