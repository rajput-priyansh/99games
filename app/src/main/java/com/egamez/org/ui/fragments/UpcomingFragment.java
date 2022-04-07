//For Upcoming tab at selected game page
package com.egamez.org.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.egamez.org.R;
import com.egamez.org.events.JoinMatchSelectTimeClick;
import com.egamez.org.models.CurrentUser;
import com.egamez.org.models.TimeSlotBean;
import com.egamez.org.models.TournamentData;
import com.egamez.org.ui.activities.SelectMatchPositionActivity;
import com.egamez.org.ui.adapters.TimeSlotAdapter;
import com.egamez.org.ui.adapters.TournamentAdapter;
import com.egamez.org.utils.LoadingDialog;
import com.egamez.org.utils.UserLocalStore;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.GET;

public class UpcomingFragment extends Fragment implements JoinMatchSelectTimeClick {

    RecyclerView recyclerView;
    TournamentAdapter myAdapter;
    List<TournamentData.AllplayMatch> mData;
    RequestQueue mQueue;
    UserLocalStore userLocalStore;
    TextView noUpcoming;
    LoadingDialog loadingDialog;
    ShimmerFrameLayout shimer;
    SwipeRefreshLayout pullToRefresh;
    CurrentUser user;
    public LinearLayout llTimeSlotContainer;
    private ImageView imgCloseTimeSlotDialog;
    private RelativeLayout rlTimeSlotDialogOverlay;
    private TextView txtNextFromTimeSlot;
    public RecyclerView rvTimeSlot;
    private RelativeLayout rlDialogOverLay;
    private LinearLayout llWinnerListContainer;
    private ImageView imgCloseWinnerListDialog;
    private TextView title_number, winner, matchType, txtMatchDate, txtGameTypeName, txtGameMatchType, txtGameMatchName, txtGameMatchDate;
    private String matchid, gamename;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.upcoming_home, container, false);

        loadingDialog = new LoadingDialog(getContext());
        shimer = (ShimmerFrameLayout) root.findViewById(R.id.shimmerplay);

        pullToRefresh = (SwipeRefreshLayout) root.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                refresh();
                pullToRefresh.setRefreshing(false);
            }
        });

        userLocalStore = new UserLocalStore(getContext());
        user = userLocalStore.getLoggedInUser();
        SharedPreferences sp = getActivity().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        matchid = sp.getString("gameid", "");
        gamename = sp.getString("gametitle", "");

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(false);
        layoutManager.setReverseLayout(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        rlTimeSlotDialogOverlay = (RelativeLayout) getActivity().findViewById(R.id.rlTimeSlotDialogOverlay);
        imgCloseTimeSlotDialog = (ImageView) getActivity().findViewById(R.id.imgCloseTimeSlotDialog);
        txtNextFromTimeSlot = (TextView) getActivity().findViewById(R.id.txtNextFromTimeSlot);
        rvTimeSlot = (RecyclerView) getActivity().findViewById(R.id.rvTimeSlot);
        rvTimeSlot.setLayoutManager(new LinearLayoutManager(getActivity()));
        llTimeSlotContainer = (LinearLayout) getActivity().findViewById(R.id.llTimeSlotContainer);
        rlTimeSlotDialogOverlay.setVisibility(View.GONE);
        txtGameTypeName = getActivity().findViewById(R.id.txtGameTypeName);
        txtGameMatchType = getActivity().findViewById(R.id.txtGameMatchType);
        txtGameMatchName = getActivity().findViewById(R.id.txtGameMatchName);
        txtGameMatchDate = getActivity().findViewById(R.id.txtGameMatchDate);

        rlDialogOverLay = getActivity().findViewById(R.id.rlDialogOverLay);
        llWinnerListContainer = getActivity().findViewById(R.id.llWinnerListContainer);
        imgCloseWinnerListDialog = getActivity().findViewById(R.id.imgCloseWinnerListDialog);
        title_number = getActivity().findViewById(R.id.title_number);
        matchType = getActivity().findViewById(R.id.matchType);
        txtMatchDate = getActivity().findViewById(R.id.txtMatchDate);
        winner = getActivity().findViewById(R.id.winner);
        rlDialogOverLay.setVisibility(View.GONE);

        mData = new ArrayList<>();

        noUpcoming = (TextView) root.findViewById(R.id.noupcoming);
        viewtournament(matchid, user.getMemberid(), gamename);

        return root;
    }

    public void viewtournament(final String matchid, String memberid, final String gamename) {

        /*all_play_match api call start*/
        mQueue = Volley.newRequestQueue(getContext());
        mQueue.getCache().clear();

        String url = null;
        if (TextUtils.equals(matchid, "not")) {
            url = getResources().getString(R.string.api) + "my_match/" + memberid;
        } else {
//            url = getResources().getString(R.string.api) + "all_play_match/" + matchid + "/" + memberid;
            url = getResources().getString(R.string.api) + "all_play_match_new/" + matchid + "/" + memberid;
        }


        final UserLocalStore userLocalStore = new UserLocalStore(getContext());

        final JsonObjectRequest request = new JsonObjectRequest(GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (TextUtils.equals(matchid, "not")) {
                                JSONArray arr = response.getJSONArray("my_match");
                                noUpcoming.setVisibility(View.GONE);
                                if (!TextUtils.equals(response.getString("my_match"), "[]")) {
                                    noUpcoming.setVisibility(View.GONE);
                                } else {
                                    noUpcoming.setVisibility(View.VISIBLE);
                                }
                                JSON_PARSE_DATA_AFTER_WEBCALL(matchid, arr, gamename);
                            } else {
                                JSONArray arr = response.getJSONArray("allplay_match");
                                noUpcoming.setVisibility(View.GONE);
                                if (!TextUtils.equals(response.getString("allplay_match"), "[]")) {
                                    noUpcoming.setVisibility(View.GONE);
                                } else {
                                    noUpcoming.setVisibility(View.VISIBLE);
                                }
                                JSON_PARSE_DATA_AFTER_WEBCALL(matchid, arr, gamename);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingDialog.dismiss();
                        shimer.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("**VolleyError", "error" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                CurrentUser user = userLocalStore.getLoggedInUser();
                String credentials = user.getUsername() + ":" + user.getPassword();
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                String token = "Bearer " + user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);
        /*all_play_match api call end*/
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(String matchid, JSONArray array, String gamename) {


        int count = 0;
        if (TextUtils.equals(matchid, "not")) {
            /*for (int i = 0; i < array.length(); i++) {
                JSONObject json = null;
                try {
                    json = array.getJSONObject(i);

                    Log.d("upcoming", json.toString());
                    if (TextUtils.equals(matchid, "not")) {

                        if (TextUtils.equals(json.getString("match_status"), "1")) {
                            count++;
                            TournamentData data = new TournamentData(json.getString("m_id"), json.getString("match_banner"), json.getString("match_name"), json.getString("m_time"), json.getString("match_time"), json.getString("win_prize"), json.getString("per_kill"), json.getString("entry_fee"), json.getString("type"), json.getString("version"), json.getString("MAP"), json.getString("no_of_player"), json.getString("member_id"), json.getString("match_type"), json.getString("number_of_position"), json.getString("room_id"), json.getString("room_password"), json.getString("join_status"), gamename, json.getString("prize_description"), "mymatch");
                            mData.add(data);
                            myAdapter = new TournamentAdapter(getContext(), mData, UpcomingFragment.this);
                            myAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(myAdapter);

                        }
                    } else {
                        TournamentData data = new TournamentData(json.getString("m_id"), json.getString("match_banner"), json.getString("match_name"), json.getString("m_time"), json.getString("match_time"), json.getString("win_prize"), json.getString("per_kill"), json.getString("entry_fee"), json.getString("type"), json.getString("version"), json.getString("MAP"), json.getString("no_of_player"), json.getString("member_id"), json.getString("match_type"), json.getString("number_of_position"), json.getString("room_id"), json.getString("room_password"), json.getString("join_status"), gamename, json.getString("prize_description"), json.getString("pin_match"));
                        mData.add(data);
                        myAdapter = new TournamentAdapter(getContext(), mData, UpcomingFragment.this);
                        myAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(myAdapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }*/
        } else {
            mData = new ArrayList<TournamentData.AllplayMatch>();
            Gson gson = new Gson();
            mData.addAll(Arrays.asList(gson.fromJson(String.valueOf(array), TournamentData.AllplayMatch[].class)));

            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).setGameName(gamename);
                for (int j = 0; j < mData.get(i).getUniqueCodeMatches().size(); j++) {
                    mData.get(i).getUniqueCodeMatches().get(j).setSelected(false);
                    mData.get(i).getUniqueCodeMatches().get(j).setGameName(gamename);
                }
            }

            myAdapter = new TournamentAdapter(getContext(), mData, UpcomingFragment.this);
            myAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(myAdapter);
        }


        if (TextUtils.equals(matchid, "not") && count == 0) {
            noUpcoming.setVisibility(View.VISIBLE);
        }
    }

    public void refresh() {
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onItemTimeSlotClick(View mView, int position) {
        rlTimeSlotDialogOverlay.setVisibility(View.VISIBLE);
        Animation bottomUp = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_up);

        txtGameTypeName.setText(gamename);
        txtGameMatchType.setText(mData.get(position).getType() + "");
        txtGameMatchName.setText(mData.get(position).getMatchName());
        txtGameMatchDate.setText(mData.get(position).getMatchTime());

        llTimeSlotContainer.startAnimation(bottomUp);
        imgCloseTimeSlotDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeTimeSlotDialog();
            }
        });
        rlTimeSlotDialogOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        TimeSlotAdapter timeSlotAdapter = new TimeSlotAdapter(mData.get(position).getUniqueCodeMatches(), getActivity());
        rvTimeSlot.setAdapter(timeSlotAdapter);

        txtNextFromTimeSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < mData.get(position).getUniqueCodeMatches().size(); i++) {
                    if (mData.get(position).getUniqueCodeMatches().get(i).getSelected() && !mData.get(position).getUniqueCodeMatches().get(i).getJoinStatus()) {
                        Intent intent = new Intent(getActivity(), SelectMatchPositionActivity.class);
                        intent.putExtra("MATCH_ID", mData.get(position).getUniqueCodeMatches().get(i).getmId() + "");
                        intent.putExtra("GAME_NAME", gamename + "");
                        intent.putExtra("MATCH_NAME", mData.get(position).getUniqueCodeMatches().get(i).getMatchName() + "");
                        intent.putExtra("TYPE", mData.get(position).getUniqueCodeMatches().get(i).getType() + "");
                        intent.putExtra("TOTAL", mData.get(position).getUniqueCodeMatches().get(i).getNumberOfPosition() + "");
                        intent.putExtra("JOIN_STATUS", mData.get(position).getUniqueCodeMatches().get(i).getJoinStatus() + "");
                        getActivity().startActivity(intent);
                    }
                }
            }
        });


    }

    @Override
    public void onItemWinningPrizeClick(View mView, int position) {
        rlDialogOverLay.setVisibility(View.VISIBLE);
        Animation bottomUp = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_up);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            winner.setText(Html.fromHtml(mData.get(position).getPrizeDescription(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            winner.setText(Html.fromHtml(mData.get(position).getPrizeDescription()));
        }

        matchType.setText(mData.get(position).getType());
        txtMatchDate.setText(mData.get(position).getMatchTime());
        title_number.setText(mData.get(position).getMatchName());

        llWinnerListContainer.startAnimation(bottomUp);
        imgCloseWinnerListDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeWinningPrizeDialog();
            }
        });
        rlDialogOverLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void closeTimeSlotDialog() {
        Animation bottomDown = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_down);
        llTimeSlotContainer.startAnimation(bottomDown);
        rlTimeSlotDialogOverlay.setVisibility(View.GONE);
    }

    private void closeWinningPrizeDialog() {
        Animation bottomDown = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_down);
        llWinnerListContainer.startAnimation(bottomDown);
        rlDialogOverLay.setVisibility(View.GONE);
    }
}
