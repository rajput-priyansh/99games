//For onesignal, check internet availability and font family
package com.egamez.org.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.onesignal.OneSignal;
import com.egamez.org.R;
import com.egamez.org.ui.activities.NoInternetActivity;
import com.stripe.android.PaymentConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MyApp extends Application {

    private static Context mContext;
    boolean internet = false;
    RequestQueue mQueue;
    JsonObjectRequest request;

    public static Context getContext() {
        return mContext;
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_a9ifu3p7HnwrKXU2SYU4aeuN"
        );
        //for change font family
        FontsOverride.setDefaultFont(this, "MONOSPACE", "font/Poppins-Regular.otf");



        //for one signal notification
        mQueue = Volley.newRequestQueue(this);
        mQueue.getCache().clear();

        String url = getResources().getString(R.string.api) + "one_signal_app";

        request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("onesignal notification",response.toString()+"------------------------------------------------------");
                try {
                    if(TextUtils.equals(response.getString("one_signal_notification"),"1")){
                        //one signal notification on
                        OneSignal.initWithContext(mContext);
                        OneSignal.setAppId(response.getString("one_signal_app_id"));
                        //OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.DEBUG);
                    }else {
                        //one signal notification off

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("errorONESIGNAL",    error.toString());
                if (error instanceof TimeoutError){
                    request.setShouldCache(false);
                    mQueue.add(request);
                }
            }
        });
        request.setShouldCache(false);
        mQueue.add(request);

        //check internet availability
        final Handler tipsHanlder = new Handler();
        Runnable tipsRunnable = new Runnable() {
            @Override
            public void run() {
                tipsHanlder.postDelayed(this, 1000);

                if (isAppRunning(getApplicationContext(), getPackageName())) {

                    // App is running
                    if (isAppOnForeground(getApplicationContext(), getPackageName())) {

                        //run in foreground
                        if (!isNetworkAvailable()) {
                            if (internet == false) {
                                internet = true;
                                Intent intent = new Intent(getApplicationContext(), NoInternetActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } else {
                            internet = false;
                            //internet available
                        }
                    } else {
                        //run in background
                    }
                } else {
                    // App is not running
                }
            }
        };
        tipsHanlder.post(tipsRunnable);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isAppOnForeground(Context context, String appPackageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = appPackageName;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
