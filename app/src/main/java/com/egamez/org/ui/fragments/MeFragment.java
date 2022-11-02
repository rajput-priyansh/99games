//For Me tab at home page
package com.egamez.org.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.egamez.org.widget.CustomTextView;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;
import com.egamez.org.R;
import com.egamez.org.models.CurrentUser;
import com.egamez.org.ui.activities.AboutusActivity;
import com.egamez.org.ui.activities.AnnouncementActivity;
import com.egamez.org.ui.activities.CustomerSupportActivity;
import com.egamez.org.ui.activities.HowtoActivity;
import com.egamez.org.ui.activities.LeaderboardActivity;
import com.egamez.org.ui.activities.MainActivity;
import com.egamez.org.ui.activities.MyOrderActivity;
import com.egamez.org.ui.activities.MyProfileActivity;
import com.egamez.org.ui.activities.MyReferralsActivity;
import com.egamez.org.ui.activities.MyRewardedActivity;
import com.egamez.org.ui.activities.MyStatisticsActivity;
import com.egamez.org.ui.activities.MyWalletActivity;
import com.egamez.org.ui.activities.SelectedGameActivity;
import com.egamez.org.ui.activities.TermsandConditionActivity;
import com.egamez.org.ui.activities.TopPlayerActivity;
import com.egamez.org.utils.LoadingDialog;
import com.egamez.org.utils.UserLocalStore;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MeFragment extends Fragment {

    CustomTextView userName;
    TextView playCoin;
    TextView myWallet;
    TextView myProfile;
    TextView aboutUs;
    TextView customerSupport;
    TextView logOut;
    TextView shareApp;
    TextView winning;
    TextView myStatistics;
    TextView topPlayer;
    TextView matchesPlayed;
    TextView totalKilled;
    TextView amountWon;
    TextView appVersion;
    TextView appTutorial;
    TextView myReff;
    TextView myRewards;
    TextView leaderboard;
    TextView termAndCondition;
    TextView mymatches;
    TextView myOrder;
    TextView announcetv;
    LinearLayout staticResult;
    RequestQueue mQueue, vQueue;
    UserLocalStore userLocalStore;
    LoadingDialog loadingDialog;
    String userNameforlogout = "";
    SharedPreferences sp;
    String shareBody = "";
    Switch notification;
    TextView pushtext;
    ImageView imgUserProfile, imgShareProfile;
    private String msg;


    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {


        loadingDialog = new LoadingDialog(getContext());
        sp = getActivity().getSharedPreferences("tabinfo", Context.MODE_PRIVATE);
        String selectedtab = sp.getString("selectedtab", "");
        if (TextUtils.equals(selectedtab, "2")) {
            loadingDialog.show();
        }
        View root = inflater.inflate(R.layout.me_home, container, false);

//        userName = (CustomTextView) root.findViewById(R.id.username_text);
//        playCoin = (TextView) root.findViewById(R.id.playcoin);
        myWallet = (TextView) root.findViewById(R.id.mywallet);
        myProfile = (TextView) root.findViewById(R.id.myprofile);
        aboutUs = (TextView) root.findViewById(R.id.aboutus);
        customerSupport = (TextView) root.findViewById(R.id.customersupport);
        logOut = (TextView) root.findViewById(R.id.logout);
        appTutorial = (TextView) root.findViewById(R.id.howto);
        shareApp = (TextView) root.findViewById(R.id.shareapp);
        winning = (TextView) root.findViewById(R.id.winning);
        myStatistics = (TextView) root.findViewById(R.id.mystatisics);
        topPlayer = (TextView) root.findViewById(R.id.topplayer);
        myReff = (TextView) root.findViewById(R.id.myreff);
        myRewards = (TextView) root.findViewById(R.id.myreward);
        leaderboard = (TextView) root.findViewById(R.id.leaderboard);
        termAndCondition = (TextView) root.findViewById(R.id.tandc);
//        staticResult = (LinearLayout) root.findViewById(R.id.staticsresult);
//        matchesPlayed = (TextView) root.findViewById(R.id.matchesplayed);
//        totalKilled = (TextView) root.findViewById(R.id.totalkilled);
//        amountWon = (TextView) root.findViewById(R.id.amountwon);
//        appVersion = (TextView) root.findViewById(R.id.appversion);
        mymatches = (TextView) root.findViewById(R.id.mymatch);
        myOrder = (TextView) root.findViewById(R.id.myorder);
        announcetv = (TextView) root.findViewById(R.id.announcetv);
        pushtext = (TextView) root.findViewById(R.id.pushtext);
//        imgUserProfile = (ImageView) root.findViewById(R.id.imgUserProfile);
//        imgShareProfile = (ImageView) root.findViewById(R.id.imgShareProfile);

        notification = (Switch) root.findViewById(R.id.notification);

        SharedPreferences sp = getActivity().getSharedPreferences("Notification", Context.MODE_PRIVATE);
        final String switchstatus = sp.getString("switch", "on");
        if (TextUtils.equals(switchstatus, "on")) {
            notification.setChecked(true);
            OneSignal.disablePush(false);
        } else {
            notification.setChecked(false);
            OneSignal.disablePush(true);
        }


        announcetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AnnouncementActivity.class));
            }
        });

        /*imgShareProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (msg != null && !msg.equals("")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(Intent.createChooser(intent, "Share"));
                }
            }
        });*/

        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences sp = getActivity().getSharedPreferences("Notification", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                // Toast.makeText(getContext(), String.valueOf(isChecked), Toast.LENGTH_SHORT).show();
                if (isChecked) {
                    OneSignal.disablePush(false);
                    editor.putString("switch", "on");
                } else {
                    OneSignal.disablePush(true);
                    editor.putString("switch", "off");
                }
                editor.apply();
            }
        });

        userLocalStore = new UserLocalStore(getContext());
        final CurrentUser user = userLocalStore.getLoggedInUser();
        userNameforlogout = user.getUsername();

        //dashboard api call start
        mQueue = Volley.newRequestQueue(getContext());
        mQueue.getCache().clear();
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        String url = getResources().getString(R.string.api) + "dashboard/" + user.getMemberid();

        /*final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("DashboardAPIResponse", response + "");
                            JSONObject obj = new JSONObject(response.getString("web_config"));
                            shareBody = obj.getString("share_description");
                            JSONObject memobj = new JSONObject(response.getString("member"));

                            String winmoney = memobj.getString("wallet_balance");
                            String joinmoney = memobj.getString("join_money");
                            if (TextUtils.equals(winmoney, "null")) {
                                winmoney = "0";
                            }
                            if (TextUtils.equals(joinmoney, "null")) {
                                joinmoney = "0";
                            }
                            String totalmoney = String.valueOf(Integer.parseInt(winmoney) + Integer.parseInt(joinmoney));

                            userName.setText(memobj.getString("user_name"));
                            playCoin.setText(totalmoney);
                            if (!memobj.isNull("profile_pic") && !memobj.getString("profile_pic").equals("")) {
                                Picasso.get().load(getString(R.string.image_api) + memobj.getString("profile_pic")).placeholder(R.drawable.man).error(R.drawable.man).into(imgUserProfile);
                            } else {
                                Picasso.get().load(R.drawable.man).placeholder(R.drawable.man).error(R.drawable.man).into(imgUserProfile);
                            }

                            JSONObject totplayobj = new JSONObject(response.getString("tot_match_play"));
                            if (TextUtils.equals(totplayobj.getString("total_match"), "null")) {
                                matchesPlayed.setText("0");
                            } else {
                                matchesPlayed.setText(totplayobj.getString("total_match"));
                            }
                            JSONObject totkillobj = new JSONObject(response.getString("tot_kill"));

                            if (TextUtils.equals(totkillobj.getString("total_kill"), "null")) {
                                totalKilled.setText("0");
                            } else {
                                totalKilled.setText(totkillobj.getString("total_kill"));
                            }

                            JSONObject totwinobj = new JSONObject(response.getString("tot_win"));

                            if (TextUtils.equals(totwinobj.getString("total_win"), "null")) {
                                amountWon.setText("0");
                            } else {
                                amountWon.setText(totwinobj.getString("total_win"));
                            }


                            msg = "Hey, I Am Playing on metaclops.in and here's Some of My Stats. Join us.\n" +
                                    "Name : "+memobj.getString("user_name")+"\n" +
                                    "matches : "+matchesPlayed.getText().toString()+"\n" +
                                    "Total Kills : "+totalKilled.getText().toString()+"\n" +
                                    "Amount Won : "+amountWon.getText().toString()+"\n" +
                                    "You Can Join me on this esports Platform Via https://play.google.com/store/apps/details?id=com.egamez.org";
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
        mQueue.add(request);*/
        //dashboard api call end

        mymatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectedGameActivity.class);
                SharedPreferences sp = getActivity().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("gametitle", "My Matches");
                editor.putString("gameid", "not");
                editor.apply();
                startActivity(intent);
            }
        });

        myOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyOrderActivity.class);
                startActivity(intent);
            }
        });
        /*staticResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyStatisticsActivity.class));
            }
        });*/

        myWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyWalletActivity.class));
            }
        });

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyProfileActivity.class));
            }
        });

        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody + getActivity().getResources().getString(R.string.referral_code) + " : " + user.getUsername());
                startActivity(Intent.createChooser(sharingIntent, getActivity().getResources().getString(R.string.share_using)));
            }
        });

        myStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyStatisticsActivity.class));
            }
        });

        topPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), TopPlayerActivity.class));
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AboutusActivity.class));
            }
        });

        customerSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CustomerSupportActivity.class));
            }
        });

        myReff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyReferralsActivity.class));
            }
        });
        myRewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyRewardedActivity.class));
            }
        });

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LeaderboardActivity.class));
            }
        });

        termAndCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TermsandConditionActivity.class));
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutall();
            }
        });

        appTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HowtoActivity.class));
            }
        });
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        /* version api call start*/
        vQueue = Volley.newRequestQueue(getContext());
        vQueue.getCache().clear();
        String vurl = getResources().getString(R.string.api) + "version/android";
        JsonObjectRequest vrequest = new JsonObjectRequest(Request.Method.GET, vurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /*try {
                    appVersion.setText(getActivity().getResources().getString(R.string.version) + " : " + response.getString("version"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                loadingDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
                loadingDialog.dismiss();
            }
        });

        vrequest.setShouldCache(false);
        vQueue.add(vrequest);
        /*version api call end*/

        return root;
    }

    private void logoutall() {

        userLocalStore.clearUserData();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignIn.getClient(getContext(), gso).signOut();

        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.log_out_successfully), Toast.LENGTH_SHORT).show();

        startActivity(new Intent(getActivity(), MainActivity.class));
    }
}
