//For show logins user's referral list
package com.egamez.org.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
import com.egamez.org.R;
import com.egamez.org.models.CurrentUser;
import com.egamez.org.utils.LoadingDialog;
import com.egamez.org.utils.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyReferralsActivity extends AppCompatActivity {

    RequestQueue mQueue, dQueue;
    UserLocalStore userLocalStore;
    CurrentUser user;
    LinearLayout refLl;
    TextView refTv;
    ImageView back;
    LoadingDialog loadingDialog;
    String from ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_referrals);

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

        loadingDialog = new LoadingDialog(MyReferralsActivity.this);
        loadingDialog.show();

        Intent intent=getIntent();
        from=intent.getStringExtra("FROM");
        back = (ImageView) findViewById(R.id.backfrommyreff);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.equals(from,"REFERNEARN")){
                    Intent intent = new Intent(getApplicationContext(), ReferandEarnActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("N", "2");
                    startActivity(intent);
                }
            }
        });

        final TextView refnumber = (TextView) findViewById(R.id.refnumber);
        final TextView earnings = (TextView) findViewById(R.id.earnings);
        refTv = (TextView) findViewById(R.id.reftv);
        refLl = (LinearLayout) findViewById(R.id.refll);

        userLocalStore = new UserLocalStore(getApplicationContext());
        user = userLocalStore.getLoggedInUser();

        //For list of all referrals
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        String url = getResources().getString(R.string.api) + "my_refrrrals/" + user.getMemberid();

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject totrefobj = new JSONObject(response.getString("tot_referrals"));
                            refnumber.setText(Html.fromHtml(getResources().getString(R.string.referrals)+"<br><b>" + totrefobj.getString("total_ref")));

                            JSONObject totearnobj = new JSONObject(response.getString("tot_earnings"));

                            SharedPreferences sp = getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
                            //String selectedcurrency = sp.getString("currency", "₹");
                            SpannableStringBuilder builder = new SpannableStringBuilder();

                            if ((TextUtils.equals(totearnobj.getString("total_earning"), "null"))) {
                                builder = new SpannableStringBuilder();
                                builder.append(Html.fromHtml(getResources().getString(R.string.earnings)+"<br><b>"))
                                        .append(" ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                                        .append(" ")
                                        .append(Html.fromHtml(""+"<b>0</b>"));
                                earnings.setText(builder);
                            } else {
                                builder = new SpannableStringBuilder();
                                builder.append(Html.fromHtml(getResources().getString(R.string.earnings)+"<br><b>"))
                                        .append(" ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                                        .append(" ")
                                        .append(Html.fromHtml("<b>"+ totearnobj.getString("total_earning")));
                                earnings.setText(builder);
                            }
                            JSONArray arr = response.getJSONArray("my_referrals");
                            if (!TextUtils.equals(response.getString("my_referrals"), "[]")) {
                                refTv.setVisibility(View.GONE);
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
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                String token="Bearer "+user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };

        request.setShouldCache(false);
        mQueue.add(request);

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
                            String totalmoney = String.valueOf(Integer.parseInt(winmoney) + Integer.parseInt(joinmoney));
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
    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                View view = getLayoutInflater().inflate(R.layout.referral_data, null);
                TextView rdate = (TextView) view.findViewById(R.id.rdate);
                TextView rplayername = (TextView) view.findViewById(R.id.rplayername);
                TextView rstatus = (TextView) view.findViewById(R.id.rstatus);

                rdate.setText(json.getString("date"));
                rplayername.setText(json.getString("user_name"));
                rstatus.setText(json.getString("status"));
                if (TextUtils.equals(json.getString("status"), "Rewarded")) {
                    rstatus.setTextColor(getResources().getColor(R.color.newgreen));
                } else {
                    rstatus.setTextColor(getResources().getColor(R.color.newblack));
                }
                refLl.addView(view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(TextUtils.equals(from,"REFERNEARN")){
            Intent intent = new Intent(getApplicationContext(), ReferandEarnActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("N", "2");
            startActivity(intent);
        }
    }
}
