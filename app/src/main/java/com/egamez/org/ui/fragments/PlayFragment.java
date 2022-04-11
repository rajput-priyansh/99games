//For Play tab at selected home page
package com.egamez.org.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
import com.egamez.org.models.HowToPlayBean;
import com.egamez.org.ui.activities.FriendRequestActivity;
import com.egamez.org.ui.activities.MyProfileActivity;
import com.egamez.org.ui.activities.MyStatisticsActivity;
import com.egamez.org.ui.activities.NotificationActivity;
import com.egamez.org.ui.adapters.AllGamesAdapter;
import com.egamez.org.ui.adapters.AnnouncementAdapter;
import com.egamez.org.ui.adapters.HowToPlayAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.egamez.org.R;
import com.egamez.org.models.BanerData;
import com.egamez.org.models.CurrentUser;
import com.egamez.org.models.GameData;
import com.egamez.org.ui.activities.AnnouncementActivity;
import com.egamez.org.ui.activities.MainActivity;
import com.egamez.org.ui.activities.MyWalletActivity;
import com.egamez.org.ui.activities.SelectedGameActivity;
import com.egamez.org.ui.adapters.TestFragmentAdapter;
import com.egamez.org.utils.KKViewPager;
import com.egamez.org.utils.LoadingDialog;
import com.egamez.org.utils.UserLocalStore;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.DismissListener;

import static com.android.volley.Request.Method.GET;
import static com.facebook.FacebookSdk.getApplicationContext;

public class PlayFragment extends Fragment {

    CardView balance;
    RequestQueue mQueue, dQueue, sQueue;
    TextView balInPlay;
    UserLocalStore userLocalStore;
    TextView noUpcoming,usernameText,balanceText;
    LoadingDialog loadingDialog;
    ShimmerFrameLayout shimer;
    CurrentUser user;
    RecyclerView allGameRecyclerView,announcementRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private AllGamesAdapter allGamesAdapter;
    List<GameData> allGamesList =new ArrayList<>();
    private AnnouncementAdapter announcementAdapter;
    private RecyclerView.LayoutManager annLayoutManager;
    List<String> announList =new ArrayList<>();
    SwipeRefreshLayout pullToRefresh;
    TextView announcement, txtTipsAndTricks, txtTournamentTitle;
    CardView announcecv, cvHowToPlay;
    RecyclerView rvYoutubeLinks;
    ImageView imgCloseYoutubeLinkDialog,friendImage,notificationImage,staticsImage;
    RelativeLayout rlTutorialListDialogOverlay;
    LinearLayout profileLayout;
    KKViewPager mPager;

    List<BanerData> banerData;
    List<HowToPlayBean> mHowToPlayArrayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for(int i=1;i<10;i++){
            announList.add(""+i);
        }
    }

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.play_home, container, false);

        pullToRefresh = (SwipeRefreshLayout) root.findViewById(R.id.pullToRefreshplay);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //pullToRefresh.setRefreshing(true);
                //pullToRefresh.setRefreshing(false);
                refresh();

            }
        });
        usernameText = (TextView) root.findViewById(R.id.username_text);
        balanceText= (TextView) root.findViewById(R.id.balance_text);
        mPager = (KKViewPager) root.findViewById(R.id.kk_pager);

        banerData = new ArrayList<>();

        loadingDialog = new LoadingDialog(getContext());
        shimer = (ShimmerFrameLayout) root.findViewById(R.id.shimmerplay);


        userLocalStore = new UserLocalStore(getContext());
        txtTipsAndTricks = root.findViewById(R.id.txtTipsAndTricks);
        txtTournamentTitle = root.findViewById(R.id.txtTournamentTitle);
        txtTipsAndTricks.setSelected(true);
        txtTournamentTitle.setSelected(true);
        user = userLocalStore.getLoggedInUser();
        if(user !=null){
            usernameText.setText("Username:"+user.getUsername());
            balanceText.setText("Balance:0");
        }
        profileLayout = (LinearLayout) root.findViewById(R.id.profile_layout);
        friendImage = (ImageView)root.findViewById(R.id.friend_image);
        staticsImage = (ImageView)root.findViewById(R.id.statics_image);

        notificationImage = (ImageView)root.findViewById(R.id.notification_image);
        allGameRecyclerView = (RecyclerView) root.findViewById(R.id.allgamell_recycler);
        allGameRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(),  LinearLayoutManager.HORIZONTAL, false);
        allGameRecyclerView.setLayoutManager(mLayoutManager);
        allGamesAdapter = new AllGamesAdapter(getActivity(),allGamesList);
        allGameRecyclerView.setAdapter(allGamesAdapter);

        announcementRecyclerView = (RecyclerView) root.findViewById(R.id.announcement_recycler);
        announcementRecyclerView.setHasFixedSize(true);
        annLayoutManager = new LinearLayoutManager(getActivity());
        announcementRecyclerView.setLayoutManager(annLayoutManager);
        announcementAdapter = new AnnouncementAdapter(getActivity(),announList);
        announcementRecyclerView.setAdapter(announcementAdapter);

        balInPlay = (TextView) root.findViewById(R.id.balinplay);
        balance = (CardView) root.findViewById(R.id.balanceinplay);
        rvYoutubeLinks = (RecyclerView) root.findViewById(R.id.rvYoutubeLinks);
        rvYoutubeLinks.setLayoutManager(new LinearLayoutManager(getActivity()));
        imgCloseYoutubeLinkDialog = (ImageView) root.findViewById(R.id.imgCloseYoutubeLinkDialog);
        rlTutorialListDialogOverlay = (RelativeLayout) root.findViewById(R.id.rlTutorialListDialogOverlay);
        cvHowToPlay = (CardView) root.findViewById(R.id.cvHowToPlay);
        friendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendRequestActivity.class);
                startActivity(intent);
            }
        });
        staticsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyStatisticsActivity.class);
                startActivity(intent);
            }
        });
        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                startActivity(intent);
            }
        });
        notificationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });
        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MyWalletActivity.class);
                intent.putExtra("FROM", "PLAY");
                startActivity(intent);
            }
        });

        announcement = (TextView) root.findViewById(R.id.announce);
        announcecv = (CardView) root.findViewById(R.id.announccv);
        announcecv.setVisibility(View.GONE);
        announcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AnnouncementActivity.class));
            }
        });

        cvHowToPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlTutorialListDialogOverlay.setVisibility(View.VISIBLE);
            }
        });

        imgCloseYoutubeLinkDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlTutorialListDialogOverlay.setVisibility(View.GONE);
            }
        });

        Announcement();

        viewallslider();

        mQueue = Volley.newRequestQueue(getContext());
        mQueue.getCache().clear();
        noUpcoming = (TextView) root.findViewById(R.id.noupcominginplay);

        /*dashboard start*/
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        dQueue = Volley.newRequestQueue(getContext());
        String durl = getResources().getString(R.string.api) + "dashboard/" + user.getMemberid();
        final JsonObjectRequest drequest = new JsonObjectRequest(durl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject memobj = new JSONObject(response.getString("member"));


                            if (TextUtils.equals(memobj.getString("member_status"), "0")) {
                                if (!user.getUsername().equals("") && !user.getPassword().equals("")) {

                                    userLocalStore.clearUserData();
                                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.your_account_is_blocked_by_admin), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getActivity(), MainActivity.class));

                                }

                                return;
                            }

                            String winmoney = memobj.getString("wallet_balance");
                            String joinmoney = memobj.getString("join_money");
                            if (TextUtils.equals(winmoney, "null")) {
                                winmoney = "0";
                            }
                            if (TextUtils.equals(joinmoney, "null")) {
                                joinmoney = "0";
                            }
                            String totalmoney = String.valueOf(Integer.parseInt(winmoney) + Integer.parseInt(joinmoney));

                            SharedPreferences sp = getActivity().getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
                            String selectedcurrency = sp.getString("currency", "₹");

                            balInPlay.setText(totalmoney);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
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

        drequest.setShouldCache(false);
        dQueue.add(drequest);

        /*dashboard end*/
        viewallgame();

        return root;
    }

    public void viewallgame() {
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        /*all_game api call start*/
        mQueue = Volley.newRequestQueue(getContext());
        mQueue.getCache().clear();
        String url = getResources().getString(R.string.api) + "all_game";
        final UserLocalStore userLocalStore = new UserLocalStore(getContext());

        final JsonObjectRequest request = new JsonObjectRequest(GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("all_game");

                            if (!TextUtils.equals(response.getString("all_game"), "[]")) {
                                noUpcoming.setVisibility(View.GONE);
                            } else {
                                noUpcoming.setVisibility(View.VISIBLE);
                            }
                            JSON_PARSE_DATA_AFTER_WEBCALL(arr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingDialog.dismiss();
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
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                String token = "Bearer " + user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);
        /*all_game api call end*/

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AllGamesAdapter) allGamesAdapter).setOnItemClickListener(new AllGamesAdapter.MyClickListener() {
            @Override
            public void onItemClick(GameData gameData, int position, View v) {
                Intent intent = new Intent(getActivity(), SelectedGameActivity.class);
                SharedPreferences sp = getActivity().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("gametitle", gameData.getGameName());
                editor.putString("gameid", gameData.getGameId());
                editor.apply();
                startActivity(intent);
            }
        });
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                //Log.d("respopnse",json.toString());
                final GameData data = new GameData(json.getString("game_id"), json.getString("game_name"), json.getString("game_image"), json.getString("status"), json.getString("total_upcoming_match"));
                allGamesList.add(data);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        allGamesAdapter.addAll(allGamesList);
    }

    public void refresh() {
        getActivity().finish();
        getActivity().overridePendingTransition(0, 0);
        getActivity().startActivity(getActivity().getIntent());
        getActivity().overridePendingTransition(0, 0);
    }

    public void Announcement() {

        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        //for announcement api
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();
        String url = getResources().getString(R.string.api) + "announcement";
        final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());

        final JsonObjectRequest request = new JsonObjectRequest(GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("announcement");

                            if (!TextUtils.equals(response.getString("announcement"), "[]")) {
                                announcecv.setVisibility(View.VISIBLE);
                            } else {
                                announcecv.setVisibility(View.GONE);
                                announcement.setText(Html.fromHtml("<b>" + getActivity().getResources().getString(R.string.announcement) + "</b><br>" + getActivity().getResources().getString(R.string.no_announcement_available)));
                            }
                            JSON_PARSE_DATA_AFTER_WEBCALLannounccement(arr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        loadingDialog.dismiss();
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


    }

    public void viewallslider() {

        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        /*slider api call start*/
        sQueue = Volley.newRequestQueue(getActivity());
        sQueue.getCache().clear();
        String surl = getResources().getString(R.string.api) + "slider";
        final UserLocalStore userLocalStore = new UserLocalStore(getContext());

        final JsonObjectRequest srequest = new JsonObjectRequest(GET, surl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("slider", response.toString());
                            JSONArray arr = response.getJSONArray("slider");

                            if (TextUtils.equals(response.getString("slider"), "[]")) {
                                mPager.setVisibility(View.GONE);
                            } else {
//                                mPager.setVisibility(View.VISIBLE);
                                mPager.setVisibility(View.GONE);
                            }

                            JSON_PARSE_DATA_AFTER_WEBCALLslider(arr);
                            JSONArray mLinksJsonArray = response.getJSONArray("app_tutorial");
                            mHowToPlayArrayList = new ArrayList<>();
                            for (int i = 0; i < mLinksJsonArray.length(); i++) {
                                JSONObject json = null;
                                try {
                                    json = mLinksJsonArray.getJSONObject(i);
                                    HowToPlayBean mHowToPlayBean = new HowToPlayBean();
                                    mHowToPlayBean.setYoutube_link_id(json.getString("youtube_link_id"));
                                    mHowToPlayBean.setYoutube_link(json.getString("youtube_link"));
                                    mHowToPlayBean.setYoutube_link_title(json.getString("youtube_link_title"));
                                    mHowToPlayBean.setDate_created(json.getString("date_created"));
                                    mHowToPlayArrayList.add(mHowToPlayBean);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            rvYoutubeLinks.setAdapter(new HowToPlayAdapter(mHowToPlayArrayList, getActivity()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingDialog.dismiss();
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
        srequest.setShouldCache(false);
        sQueue.add(srequest);
        /*slider api call end*/

    }

    public void JSON_PARSE_DATA_AFTER_WEBCALLslider(JSONArray array) {

        banerData.clear();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                BanerData data = new BanerData(json.getString("slider_id"), json.getString("slider_title"), json.getString("slider_image"), json.getString("slider_link_type"), json.getString("slider_link"), json.getString("link_id"), json.getString("game_name"));
                banerData.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mPager.setAdapter(new TestFragmentAdapter(requireActivity().getSupportFragmentManager(),
                getActivity(), banerData));
        mPager.setPageMargin(40);
        mPager.setAnimationEnabled(true);
        mPager.setFadeEnabled(true);
        mPager.setFadeFactor(0.6f);
        if (banerData.size() >= 3) {
            mPager.setCurrentItem(1);
        }

    }

    public void JSON_PARSE_DATA_AFTER_WEBCALLannounccement(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                announcement.setText(Html.fromHtml("<b>" + getActivity().getResources().getString(R.string.announcement) + "</b><br>" + json.getString("announcement_desc")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public FancyShowCaseView addView(View view, String title, String n) {
        FancyShowCaseView view1 = new FancyShowCaseView.Builder(getActivity())
                .focusOn(view)
                .title(title)
                //.titleGravity(Gravity.BOTTOM)
                .titleSize(20, 1)
                .focusShape(FocusShape.CIRCLE)
                .enableAutoTextPosition()
                .roundRectRadius(10)
                .focusBorderSize(1)
                .showOnce(n)
                .focusBorderColor(getActivity().getResources().getColor(R.color.newblue))
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
