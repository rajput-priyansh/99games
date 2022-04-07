//for buy any product or place order
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;

public class ProductOrderActivity extends AppCompatActivity {

    ImageView back;
    TextView title;
    TextView current;
    TextView total;
    EditText name;
    EditText address;
    EditText additional;
    Button cancel;
    Button buy;
    LoadingDialog loadingDialog;
    UserLocalStore userLocalStore;
    CurrentUser user;
    RequestQueue dQueue,mQueue;
    String id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_order);

        //check baner ads enable or not
        SharedPreferences spb=getSharedPreferences("SMINFO",MODE_PRIVATE);
        if(TextUtils.equals(spb.getString("baner","no"),"yes")) {

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

        loadingDialog=new LoadingDialog(this);
        userLocalStore=new UserLocalStore(this);
        user=userLocalStore.getLoggedInUser();

        back=(ImageView)findViewById(R.id.backfromorder);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        title=(TextView)findViewById(R.id.ordertitle);
        current=(TextView)findViewById(R.id.ordercurrent);
        total=(TextView)findViewById(R.id.ordertotal);
        name=(EditText)findViewById(R.id.orderfullname);
        address=(EditText)findViewById(R.id.orderadd);
        additional=(EditText)findViewById(R.id.orderadditionnal);
        cancel=(Button)findViewById(R.id.ordercancel);
        buy=(Button)findViewById(R.id.orderbuy);

        SharedPreferences sp = getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
        final String selectedcurrency = sp.getString("currency", "₹");

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        title.setText(intent.getStringExtra("name"));

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(Html.fromHtml(getResources().getString(R.string.total_payable_amount__)))
                .append(" ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")
                .append(Html.fromHtml("<b>"+intent.getStringExtra("price")));
        total.setText(builder);

        //dashboard api call
        dQueue = Volley.newRequestQueue(getApplicationContext());
        dQueue.getCache().clear();

        String durl = getResources().getString(R.string.api) + "dashboard/" + user.getMemberid();

        final JsonObjectRequest drequest = new JsonObjectRequest(durl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();
                        //Log.d("respons",response.toString());

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

                            SpannableStringBuilder builder = new SpannableStringBuilder();
                            builder.append(Html.fromHtml(getResources().getString(R.string.your_current_balance__)))
                                    .append(" ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                                    .append(" ")
                                    .append(Html.fromHtml("<b>"+totalmoney));
                            current.setText(builder);

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

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.equals(name.getText().toString().trim(),"")){
                    Toast.makeText(ProductOrderActivity.this, getResources().getString(R.string.please_enter_your_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.equals(address.getText().toString().trim(),"")){
                    Toast.makeText(ProductOrderActivity.this, getResources().getString(R.string.please_enter_your_address), Toast.LENGTH_SHORT).show();
                    return;
                }
                /*product order api call start*/
                loadingDialog.show();
                mQueue = Volley.newRequestQueue(getApplicationContext());
                mQueue.getCache().clear();
                String surl = getResources().getString(R.string.api) + "product_order";
                final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());

                JSONObject addobj=new JSONObject();
                JSONObject postobj=new JSONObject();
                try {
                    addobj.put("name",name.getText().toString().trim());
                    addobj.put("address",address.getText().toString().trim());
                    addobj.put("add_info",additional.getText().toString().trim());
                    postobj.put("submit","order");
                    postobj.put("product_id",id);
                    postobj.put("member_id",user.getMemberid());
                    postobj.put("shipping_address",addobj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final JsonObjectRequest srequest = new JsonObjectRequest(POST, surl,  postobj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                loadingDialog.dismiss();

                                try {
                                    String status=response.getString("status");
                                    if(TextUtils.equals(status,"true")){
                                        Intent intent =new Intent(getApplicationContext(),SuccessOrderActivity.class);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(ProductOrderActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("**VolleyError", "error" + error.getMessage());
                    }
                })
                {
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
                srequest.setShouldCache(false);
                mQueue.add(srequest);

                /*product order api call end*/
            }
        });




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}