//For show data in upcoming tab
package com.egamez.org.ui.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.egamez.org.R;
import com.egamez.org.events.JoinMatchSelectTimeClick;
import com.egamez.org.models.TournamentData;
import com.egamez.org.ui.activities.SelectMatchPositionActivity;
import com.egamez.org.ui.activities.SelectedTournamentActivity;
import com.egamez.org.utils.LoadingDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.DismissListener;

import static java.lang.Integer.parseInt;

public class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.MyViewHolder> {

    private final JoinMatchSelectTimeClick mTimeSlotClick;
    LoadingDialog loadingDialog;
    RequestQueue mQueue;
    private Context mContext;
    private List<TournamentData.AllplayMatch> mData;

    public TournamentAdapter(Context mContext, List<TournamentData.AllplayMatch> mData, JoinMatchSelectTimeClick mTimeSlotClick) {
        this.mContext = mContext;
        this.mData = mData;
        this.mTimeSlotClick = mTimeSlotClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.tournament_data, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final TournamentData.AllplayMatch tournamentData = mData.get(position);

        loadingDialog = new LoadingDialog(mContext);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        if (TextUtils.equals(tournamentData.getPinMatch(), "1")) {
            holder.pincv.setVisibility(View.VISIBLE);
        } else if (TextUtils.equals(tournamentData.getPinMatch(), "mymatch")) {
            holder.pincv.setVisibility(View.GONE);
        } else {
            holder.pincv.setVisibility(View.GONE);
        }

        holder.tvGameName.setText(tournamentData.getGameName());

        /*date sh */

        holder.tvMatchTitleandNumber.setText(tournamentData.getMatchName() + " - " + mContext.getResources().getString(R.string.match) + " #" + tournamentData.getmId());
        String newdate = tournamentData.getMatchTime();
        String arr[] = newdate.split(" ", 2);
        String firstWord = arr[0];   //the
        String theRest = arr[1];     //quick brown fox

        holder.tvDate.setText(firstWord);
        holder.tvTime.setText(theRest);

        /*prize pool sh*/
        String prize = tournamentData.getWinPrize()+"";
        holder.tvTotalWin.setText(prize);

        /*original*/
//                builder.append(Html.fromHtml(mContext.getResources().getString(R.string.PRIZE_POOL) + "<br>"))
//                .append(" ", new ImageSpan(mContext, R.drawable.resize_coin1618, ImageSpan.ALIGN_BASELINE), 0)
//                .append(" ")
//                .append(Html.fromHtml("<b>" + tournamentData.getWinprize()));
//        holder.tvTotalWin.setText(builder);

        /*PerKill sh */
        holder.tvPerKill.setText(tournamentData.getPerKill()+"");

        /*original*/
        /*builder = new SpannableStringBuilder();
        builder.append(Html.fromHtml(mContext.getResources().getString(R.string.PER_KILL) + "<br>"))
                .append(" ", new ImageSpan(mContext, R.drawable.resize_coin1618, ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")
                .append(Html.fromHtml("<b>" + tournamentData.getPerkill()));
        holder.tvPerKill.setText(builder);*/

        builder = new SpannableStringBuilder();
        builder.append(Html.fromHtml(""))
                .append(" ", new ImageSpan(mContext, R.drawable.resize_coin1618, ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")
                .append(Html.fromHtml("<b>" + tournamentData.getEntryFee()));
        holder.tvEntryFee.setText(Html.fromHtml("<b>" + tournamentData.getEntryFee()));

        holder.tvType.setText(tournamentData.getType());
        holder.tvVersion.setText(tournamentData.getVersion());
        holder.tvMap.setText(tournamentData.getMap());
        holder.roomIdPassll.setVisibility(View.GONE);
       /* if (TextUtils.equals(tournamentData.getRoomId(), "") || TextUtils.equals(tournamentData.getRoomPassword(), "") || !TextUtils.equals(tournamentData.getJoinstatus(), "true")) {
            holder.roomIdPassll.setVisibility(View.GONE);
        } else {
            holder.roomIdPassll.setVisibility(View.GONE);
        }*/


        holder.tvTotalWin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimeSlotClick.onItemWinningPrizeClick(view,position);
//                showallprize(tournamentData.getMatchname(), tournamentData.getMid(), tournamentData.getPrizedescreption());
            }
        });

        if (!tournamentData.getMatchBanner().equals("")) {
            Picasso.get().load(Uri.parse(tournamentData.getMatchBanner())).placeholder(R.drawable.default_battlemania).fit().into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
            //holder.imageView.setImageDrawable(mContext.getDrawable(R.drawable.default_battlemania));
        }
        holder.tounamentCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(mContext, SelectedTournamentActivity.class);
                intent.putExtra("M_ID", tournamentData.getmId());
                intent.putExtra("FROM", "PLAY");
                intent.putExtra("BANER", tournamentData.getMatchBanner());
                intent.putExtra("GAME_NAME", tournamentData.getGameName());
                mContext.startActivity(intent);*/
            }
        });

        if ((tournamentData.getNoOfPlayer()) == (tournamentData.getNumberOfPosition()) || (tournamentData.getNoOfPlayer()) >= (tournamentData.getNumberOfPosition())) {
            holder.joinCardView.setEnabled(false);
            holder.remainTotal.setText(tournamentData.getNumberOfPosition() + "/" + tournamentData.getNumberOfPosition());
            holder.join.setText(mContext.getResources().getString(R.string.MATCH_FULL));
            holder.join.setEnabled(false);
            holder.join.setTextColor(Color.WHITE);
            holder.tvEntryFee.setBackgroundColor(mContext.getResources().getColor(R.color.black));
            holder.joinLl.setBackgroundColor(mContext.getResources().getColor(R.color.black));
            holder.join.setBackgroundColor(mContext.getResources().getColor(R.color.black));
            holder.llJoinButtonWrap.setBackground(mContext.getResources().getDrawable(R.drawable.btn_black_round_bottom));

        } else {
            holder.remainTotal.setText(String.valueOf(tournamentData.getNoOfPlayer()) + "/" + tournamentData.getNumberOfPosition());
        }
        /*if (tournamentData.get().matches("true")) {
            holder.join.setText(mContext.getResources().getString(R.string.JOINED));
            holder.tvEntryFee.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.joinLl.setBackgroundColor(mContext.getResources().getColor(R.color.newdisablegreen));
            holder.join.setBackgroundColor(mContext.getResources().getColor(R.color.newdisablegreen));
            holder.llJoinButtonWrap.setBackground(mContext.getResources().getDrawable(R.drawable.btn_blue_round_bottom));
        } else {*/
            holder.join.setEnabled(true);
//        }

//        Log.d("getGamename==>", tournamentData.getGamename());
//        Log.d("total1==>", tournamentData.getNumberofposition());
//        Log.d("total2==>", tournamentData.getNoofplayer());

        int remaining = (tournamentData.getNumberOfPosition()) - (tournamentData.getNoOfPlayer());
//        Log.d("remaining==>", remaining + "===");

        holder.remainingtotal.setText(remaining + " Players remaining");
        holder.totalPlayersJoined.setText(tournamentData.getNoOfPlayer() + " Players joined");

        holder.progressBar.setMax((tournamentData.getNumberOfPosition()));
        holder.progressBar.setProgress((tournamentData.getNoOfPlayer()));
        int currentprogressinpercetege = ((tournamentData.getNoOfPlayer()) * 100) / (tournamentData.getNumberOfPosition());

        if (currentprogressinpercetege >= 75 && currentprogressinpercetege < 95) {
            holder.progressBar.getProgressDrawable().setColorFilter(mContext.getResources().getColor(R.color.neworange), PorterDuff.Mode.SRC_IN);
        } else if (currentprogressinpercetege >= 95) {
            holder.progressBar.getProgressDrawable().setColorFilter(mContext.getResources().getColor(R.color.newred), PorterDuff.Mode.SRC_IN);
        }

        new FancyShowCaseQueue()
                .add(addView(holder.joinCardView, mContext.getResources().getString(R.string.show_case_3), "3"))
                .show();
        holder.joinCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (tournamentData.getJoinstatus().matches("true")) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.you_are_already_joined), Toast.LENGTH_SHORT).show();
                } else {*/
                    mTimeSlotClick.onItemTimeSlotClick(view, position);
                    /*Intent intent = new Intent(mContext, SelectMatchPositionActivity.class);
                    intent.putExtra("MATCH_ID", mData.get(position).getMid());
                    intent.putExtra("GAME_NAME", mData.get(position).getGamename());
                    intent.putExtra("MATCH_NAME", mData.get(position).getMatchname());
                    intent.putExtra("TYPE", mData.get(position).getType());
                    intent.putExtra("TOTAL", mData.get(position).getNumberofposition());
                    intent.putExtra("JOIN_STATUS", mData.get(position).getJoinstatus());
                    mContext.startActivity(intent);*/
//                }
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

    public void showallprize(String matchname, String matchnumber, String prizedesc) {

        final Dialog builder = new Dialog(mContext);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlmp = builder.getWindow().getAttributes();
        wlmp.gravity = Gravity.BOTTOM;
        wlmp.y = 100; // bottom margin
        builder.getWindow().setAttributes(wlmp);
        builder.setContentView(R.layout.showallprizelayout);

        TextView title_number = (TextView) builder.findViewById(R.id.title_number);
        TextView winner = (TextView) builder.findViewById(R.id.winner);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            winner.setText(Html.fromHtml(prizedesc, Html.FROM_HTML_MODE_COMPACT));
        } else {
            winner.setText(Html.fromHtml(prizedesc));
        }

        title_number.setText(matchname + " - " + mContext.getResources().getString(R.string.match) + " #" + matchnumber);
        ImageView cancel = (ImageView) builder.findViewById(R.id.cancelprize);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
        builder.show();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvMatchTitleandNumber;
        TextView tvTime, tvDate;
        TextView tvTotalWin;
        TextView tvPerKill;
        TextView tvEntryFee;
        TextView tvType;
        TextView tvVersion, remainingtotal, totalPlayersJoined;
        TextView tvMap;
        ImageView imageView;
        LinearLayout tounamentCardView, llJoinButtonWrap;
        TextView remainTotal;
        TextView join;
        CardView joinCardView;
        ProgressBar progressBar;
        LinearLayout roomIdPassll, joinLl;
        TextView rId, tvGameName;
        ImageView pincv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMatchTitleandNumber = (TextView) itemView.findViewById(R.id.tvmatchtitleandnumber);
            tvTime = (TextView) itemView.findViewById(R.id.tvTimes);
            tvTotalWin = (TextView) itemView.findViewById(R.id.tvtotalwin);
            tvPerKill = (TextView) itemView.findViewById(R.id.tvperkill);
            tvEntryFee = (TextView) itemView.findViewById(R.id.tventryfee);
            tvType = (TextView) itemView.findViewById(R.id.tvtype);
            tvVersion = (TextView) itemView.findViewById(R.id.tvversion);
            tvMap = (TextView) itemView.findViewById(R.id.tvmap);
            remainTotal = (TextView) itemView.findViewById(R.id.remainingtotal);
            join = (TextView) itemView.findViewById(R.id.join);
            joinCardView = (CardView) itemView.findViewById(R.id.joincardview);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
            imageView = (ImageView) itemView.findViewById(R.id.imageview);
            tounamentCardView = (LinearLayout) itemView.findViewById(R.id.tournamentcardview);
            roomIdPassll = (LinearLayout) itemView.findViewById(R.id.roomidpassll);
            rId = (TextView) itemView.findViewById(R.id.rid);
            joinLl = (LinearLayout) itemView.findViewById(R.id.joinll);
            pincv = (ImageView) itemView.findViewById(R.id.piniv);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            remainingtotal = (TextView) itemView.findViewById(R.id.remainingtotal);
            totalPlayersJoined = (TextView) itemView.findViewById(R.id.totalPlayersJoined);
            tvGameName = (TextView) itemView.findViewById(R.id.tvGameName);
            llJoinButtonWrap = (LinearLayout) itemView.findViewById(R.id.llJoinButtonWrap);
        }
    }

    public FancyShowCaseView addView(View view, String title, String n) {
        FancyShowCaseView view1 = new FancyShowCaseView.Builder((Activity) mContext)
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
}

