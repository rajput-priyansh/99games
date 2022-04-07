//for lottery
package com.egamez.org.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.tabs.TabLayout;
import com.egamez.org.R;
import com.egamez.org.models.CurrentUser;
import com.egamez.org.ui.adapters.TabAdapter;
import com.egamez.org.ui.fragments.OngoingLotteryFragment;
import com.egamez.org.ui.fragments.ResultLotteryFragment;
import com.egamez.org.utils.UserLocalStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LotteryActivity extends AppCompatActivity {

    int n = 0;
    ImageView back;
    CardView balance;
    TextView balInPlay;
    UserLocalStore userLocalStore;
    CurrentUser user;
    RequestQueue dQueue;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);

        SharedPreferences sp=getSharedPreferences("SMINFO",MODE_PRIVATE);
        if(TextUtils.equals(sp.getString("baner","no"),"yes")) {

            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    mAdView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    // Code to be executed when an ad request fails.
                    mAdView.setVisibility(View.GONE);
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }
            });
        }

        viewPager = (ViewPager) findViewById(R.id.viewPagerlottry);
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutlottery);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new OngoingLotteryFragment(), getResources().getString(R.string.ONGOING));
        adapter.addFragment(new ResultLotteryFragment(), getResources().getString(R.string.RESULTS));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(getResources().getColor(R.color.deselected_color), getResources().getColor(R.color.white));

        try {
            Intent intent = getIntent();
            String N = intent.getStringExtra("N");
            n = Integer.parseInt(N);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        viewPager.setCurrentItem(n);
        back = (ImageView) findViewById(R.id.backfromlottery);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("N", "0");
                startActivity(intent);
            }
        });

        userLocalStore = new UserLocalStore(getApplicationContext());
        user = userLocalStore.getLoggedInUser();

        balInPlay = (TextView) findViewById(R.id.balinlottery);
        balance = (CardView) findViewById(R.id.balanceinlattery);
        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tabLayout.getSelectedTabPosition()==0){
                    Intent intent=new Intent(getApplicationContext(), MyWalletActivity.class);
                    intent.putExtra("FROM","ONGOING");
                    startActivity(intent);
                }else if(tabLayout.getSelectedTabPosition()==1){
                    Intent intent=new Intent(getApplicationContext(), MyWalletActivity.class);
                    intent.putExtra("FROM","UPCOMING");
                    startActivity(intent);
                }else if(tabLayout.getSelectedTabPosition()==2){
                    Intent intent=new Intent(getApplicationContext(), MyWalletActivity.class);
                    intent.putExtra("FROM","RESULT");
                    startActivity(intent);
                }
            }
        });


        //dashboard api call
        dQueue = Volley.newRequestQueue(getApplicationContext());
        dQueue.getCache().clear();

        String durl = getResources().getString(R.string.api) + "dashboard/" + user.getMemberid();

        final JsonObjectRequest drequest = new JsonObjectRequest(durl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
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
                            balInPlay.setText(totalmoney);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                String token="Bearer "+user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        drequest.setShouldCache(false);
        dQueue.add(drequest);



    }
}