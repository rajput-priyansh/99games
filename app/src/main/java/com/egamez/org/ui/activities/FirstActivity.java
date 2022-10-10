//For Splash screen
package com.egamez.org.ui.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.egamez.org.utils.AppSingletoneClass;
import com.egamez.org.utils.VolleyMultipartRequest;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.egamez.org.R;
import com.egamez.org.models.CurrentUser;
import com.egamez.org.utils.LoadingDialog;
import com.egamez.org.utils.UserLocalStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class FirstActivity extends AppCompatActivity {

    private static final String TAG = "99egames";
    String versionName = null;
    int versionCode = 0;
    String latestVersionName = null;
    int latestVersionCode = 0;
    String forceUpdate = "No";
    RequestQueue vQueue, dQueue;
    UserLocalStore userLocalStore;
    CurrentUser user;
    LoadingDialog loadingDialog;
    String downloadUrl = "";
    int waitTime = 0;
    String memberStatus = "";
    String forceLogout = "No";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        printHashKey(this);

        if (!user.getMemberid().isEmpty()) {

            //dashboard api call for currency info
            String durl = getResources().getString(R.string.api) + "dashboard/" + user.getMemberid();
            Map<String, String> headerParam = new HashMap<>();
            CurrentUser user = userLocalStore.getLoggedInUser();
            String credentials = user.getUsername() + ":" + user.getPassword();
            String auth = "Basic "
                    + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            String token = "Bearer " + user.getToken();
            headerParam.put("Content-Type", "application/json");
            headerParam.put("Authorization", token);


            VolleyMultipartRequest stringRequest = new VolleyMultipartRequest(Request.Method.GET, headerParam, durl, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse networkResponse) {
                    loadingDialog.dismiss();
                    try {
                        String responseString = new String(networkResponse.data);
                        JSONObject response = new JSONObject(responseString);

                        JSONObject obj = new JSONObject(response.getString("web_config"));
                        JSONObject memobj = new JSONObject(response.getString("member"));
                        memberStatus = memobj.getString("member_status");

                        if (TextUtils.equals(memberStatus, "0")) {
                            if (!user.getUsername().equals("") && !user.getPassword().equals("")) {
                                userLocalStore.clearUserData();
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.your_account_is_blocked_by_admin), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finishAffinity();
                            }
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loadingDialog.dismiss();
                    String err = error + "";
                    Log.e("**VolleyError", "error" + error.getMessage());
                }
            }) {
                @NonNull
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    Log.d("UIUtils.TAG", "SAVE_NEW_LEAD_GENRATION Params==>" + params);
                    return params;
                }

                public Map<String, DataPart> getByteData() {
                    Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                    Log.d("UIUtils.TAG", "SAVE_NEW_LEAD_GENRATION Params==>" + params);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppSingletoneClass.getInstance(FirstActivity.this).addToRequestQueue(stringRequest, "update_myprofile");

           /* dQueue = Volley.newRequestQueue(getApplicationContext());
            dQueue.getCache().clear();

            String durl = getResources().getString(R.string.api) + "dashboard/" + user.getMemberid();

            final JsonObjectRequest drequest = new JsonObjectRequest(durl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                JSONObject obj = new JSONObject(response.getString("web_config"));
                                JSONObject memobj = new JSONObject(response.getString("member"));
                                memberStatus = memobj.getString("member_status");

                                if (TextUtils.equals(memberStatus, "0")) {
                                    if (!user.getUsername().equals("") && !user.getPassword().equals("")) {
                                        userLocalStore.clearUserData();
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.your_account_is_blocked_by_admin), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finishAffinity();
                                    }
                                    return;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("**VolleyErrorfirst", "error" + error.getMessage());
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
            dQueue.add(drequest);*/
        }

        // call version api for check latest version
        vQueue = Volley.newRequestQueue(this);
        vQueue.getCache().clear();

        String vurl = getResources().getString(R.string.api) + "version/android";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, vurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("version", response.toString());
                try {
                    latestVersionName = response.getString("version");
                    latestVersionCode = Integer.parseInt(response.getString("version"));
                    downloadUrl = response.getString("url");
                    forceUpdate = response.getString("force_update");
                    forceLogout = response.getString("force_logged_out");

                    SharedPreferences sp = getSharedPreferences("SMINFO", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("fb", response.getString("fb_login"));
                    editor.putString("google", response.getString("google_login"));
                    editor.putString("otp", response.getString("firebase_otp"));
                    editor.putString("baner", response.getString("banner_ads_show"));
                    editor.apply();

                    try {
                        versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                        versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                    } catch (PackageManager.NameNotFoundException e) {

                    }

                    if (!(versionCode < latestVersionCode)) {
                        loadingDialog.dismiss();
                        final Handler tipsHanlder = new Handler();
                        Runnable tipsRunnable = new Runnable() {
                            @Override
                            public void run() {
                                tipsHanlder.postDelayed(this, 1000);

                                if (TextUtils.equals(String.valueOf(waitTime), "1")) {

                                    if (!user.getUsername().equals("") && !user.getMemberid().equals("")) {

                                        if (TextUtils.equals(memberStatus, "1")) {

                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class).putExtra("N", "0"));
                                            finishAffinity();
                                        }

                                    } else {
                                        SharedPreferences prefs = getSharedPreferences("first_time", MODE_PRIVATE);
                                        Boolean ft = prefs.getBoolean("f_t", true);
                                        if (ft) {
                                            startActivity(new Intent(getApplicationContext(), IndroductionActivity.class));
                                            finishAffinity();
                                        } else {
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finishAffinity();
                                        }
                                    }
                                }
                                waitTime++;
                            }
                        };
                        tipsHanlder.post(tipsRunnable);

                    } else {
                        if (TextUtils.equals(forceUpdate, "Yes")) {
                            if (TextUtils.equals(forceLogout, "Yes")) {
                                logoutall();
                            }
                            loadingDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), AppUpdateActivity.class));
                            finishAffinity();
                        } else {
                            loadingDialog.dismiss();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                            builder.setCancelable(false);
                            builder.setTitle(getResources().getString(R.string.app_name));
                            builder.setMessage(getResources().getString(R.string.new_update_available));
                            builder.setNegativeButton(getResources().getString(R.string.later), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (!user.getUsername().equals("") && !user.getPassword().equals("")) {

                                        if (TextUtils.equals(memberStatus, "1")) {
                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class).putExtra("N", "0"));
                                            finishAffinity();
                                        }
                                    } else {
                                        SharedPreferences prefs = getSharedPreferences("first_time", MODE_PRIVATE);
                                        Boolean ft = prefs.getBoolean("f_t", true);
                                        if (ft) {
                                            startActivity(new Intent(getApplicationContext(), IndroductionActivity.class));
                                            finishAffinity();
                                        } else {
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finishAffinity();
                                        }
                                    }
                                }
                            });
                            builder.setPositiveButton(getResources().getString(R.string.update_now), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if (TextUtils.equals(forceLogout, "Yes")) {
                                        logoutall();
                                    }
                                    startActivity(new Intent(getApplicationContext(), AppUpdateActivity.class));
                                    finishAffinity();
                                }
                            });

                            builder.create();
                            builder.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    try {
                        Log.d("um msg", response.getString("message"));
                        if (TextUtils.equals(response.getString("message"), "<h1>Under Maintenance</h1>")) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                            builder.setCancelable(false);
                            builder.setTitle(getResources().getString(R.string.announcement));
                            builder.setMessage(getResources().getString(R.string.currently_app_server_is_under_maintanance));

                            builder.setPositiveButton(getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finishAffinity();
                                }
                            });

                            builder.create();
                            builder.show();
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hide progress
                loadingDialog.dismiss();
                NetworkResponse response = error.networkResponse;
                String errorMsg = "";
                if (response != null && response.data != null) {
                    errorMsg = new String(response.data);
                }
                Log.e("errorversion", error.toString());
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finishAffinity();
            }
        });
        request.setShouldCache(false);
        vQueue.add(request);
    }

    private void logoutall() {
        userLocalStore.clearUserData();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignIn.getClient(getApplicationContext(), gso).signOut();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            loadingDialog.dismiss();
        } catch (Exception e) {

        }
    }


    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }


}
