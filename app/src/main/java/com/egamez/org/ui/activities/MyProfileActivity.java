//For update profile and change password
package com.egamez.org.ui.activities;


import static android.provider.MediaStore.EXTRA_OUTPUT;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.FileUtils;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.egamez.org.utils.AppSingletoneClass;
import com.egamez.org.utils.VolleyMultipartRequest;
import com.egamez.org.widget.CustomTextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.lyrebirdstudio.aspectratiorecyclerviewlib.aspectratio.model.AspectRatio;
import com.lyrebirdstudio.croppylib.Croppy;
import com.lyrebirdstudio.croppylib.main.CropRequest;
import com.lyrebirdstudio.croppylib.main.CroppyTheme;
import com.lyrebirdstudio.croppylib.main.StorageType;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
import com.egamez.org.R;
import com.egamez.org.events.CountryCodeItemClick;
import com.egamez.org.models.CurrentUser;
import com.egamez.org.ui.adapters.CountryCodeAdapter;
import com.egamez.org.utils.LoadingDialog;
import com.egamez.org.utils.UserLocalStore;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.android.volley.Request.Method.GET;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MyProfileActivity extends AppCompatActivity implements CountryCodeItemClick {

    EditText profileFirstName;
    EditText profileLastName;
    EditText profileUserName;
    EditText profileEmail;
    EditText profileMobileNumber;
    EditText profileDateOfBirth;
    RadioGroup profileGender;
    RadioButton male;
    RadioButton female;
    Button profileSave;
    EditText oldPassword;
    EditText newPassword;
    EditText retypeNewPassword;
    Button profileReset;
    Dialog dialog;
    ImageView back, userProfile;
    TextView verify;
    RequestQueue mQueue, uQueue, rQueue, pQueue;
    LoadingDialog loadingDialog;
    TextView countrycodespinner;
    List<String> spinnerArray;
    List<String> spinnerArrayCountryCode;
    String countryCode = "";
    CurrentUser user;
    UserLocalStore userLocalStore;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    FirebaseAuth mAuth;
    Dialog builder;
    OtpView otpView;
    TextView mobileview;
    TextView resend;
    private Dialog builderMobile;
    private CountryCodeAdapter mCountryCodeAdapter;
    private static int REQUEST_CAMERA = 1, SELECT_FILE = 2, PIC_CROP = 3;
    private File output;
    private Uri resultUri, destinationURI;
    private InputStream iStream;
    private byte[] inputData;
    private Activity mActivity;
    private Uri outputPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mActivity = MyProfileActivity.this;

        SharedPreferences spb = getSharedPreferences("SMINFO", MODE_PRIVATE);
        if (TextUtils.equals(spb.getString("baner", "no"), "yes")) {

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

        builder = new Dialog(MyProfileActivity.this);
        builder.setContentView(R.layout.mobile_otp_dialog);
        mobileview = (TextView) builder.findViewById(R.id.mobileotptv);
        otpView = (OtpView) builder.findViewById(R.id.otp_view_verifymobile);
        resend = (TextView) builder.findViewById(R.id.resend_verifymobile);

        mAuth = FirebaseAuth.getInstance();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("N", "2");
                startActivity(intent);
            }
        });

        verify = (TextView) findViewById(R.id.verifytv);
        profileFirstName = (EditText) findViewById(R.id.profile_firstname);
        profileLastName = (EditText) findViewById(R.id.profile_lastname);
        profileUserName = (EditText) findViewById(R.id.profile_username);
        profileEmail = (EditText) findViewById(R.id.profile_email);
        profileMobileNumber = (EditText) findViewById(R.id.profile_mobilenumber);
        profileDateOfBirth = (EditText) findViewById(R.id.profile_dateofbirth);
        profileGender = (RadioGroup) findViewById(R.id.profile_gender);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        profileSave = (Button) findViewById(R.id.profile_save);
        oldPassword = (EditText) findViewById(R.id.old_password);
        newPassword = (EditText) findViewById(R.id.new_password);
        retypeNewPassword = (EditText) findViewById(R.id.retype_new_password);
        profileReset = (Button) findViewById(R.id.profile_reset);
        countrycodespinner = (TextView) findViewById(R.id.countrycodespinnermyprofile);
        userProfile = (ImageView) findViewById(R.id.userProfile);

        spinnerArray = new ArrayList<String>();
        spinnerArrayCountryCode = new ArrayList<String>();

        countrycodespinner.setText("+91");
        countryCode = "+91";

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageOptionDialog();
            }
        });

        countrycodespinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builderMobile = new Dialog(MyProfileActivity.this);
                builderMobile.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                builderMobile.setCancelable(false);
                WindowManager.LayoutParams wlmp = builderMobile.getWindow().getAttributes();
                builderMobile.getWindow().setAttributes(wlmp);
                builderMobile.setContentView(R.layout.spinner_layout);

                ImageView cancel = builderMobile.findViewById(R.id.spinnercancel);
                LinearLayout ll = builderMobile.findViewById(R.id.spinneritemll);
                EditText edtSearchCountry = builder.findViewById(R.id.edtSearchCountry);
                RecyclerView rvCountryList = builder.findViewById(R.id.rvCountryList);
                rvCountryList.setLayoutManager(new LinearLayoutManager(MyProfileActivity.this));
                mCountryCodeAdapter = new CountryCodeAdapter(MyProfileActivity.this, spinnerArray, spinnerArrayCountryCode, MyProfileActivity.this);
                rvCountryList.setAdapter(mCountryCodeAdapter);
                edtSearchCountry.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        mCountryCodeAdapter.filter(edtSearchCountry.getText().toString());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
/*
                for (int i = 0; i < spinnerArray.size(); i++) {
                    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    View view = getLayoutInflater().inflate(R.layout.spinner_item_layout, null);
                    view.setLayoutParams(lparams);

                    TextView tv = view.findViewById(R.id.tv);
                    tv.setText(spinnerArrayCountryCode.get(i) + " (" + spinnerArray.get(i) + ")");
                    final int finalI = i;
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final TextView t = (TextView) v;
                            countrycodespinner.setText(spinnerArrayCountryCode.get(finalI));
                            countryCode = spinnerArrayCountryCode.get(finalI);
                            builderMobile.dismiss();
                        }
                    });
                    ll.addView(view);

                }*/
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builderMobile.dismiss();
                    }
                });

                builderMobile.create();
                builderMobile.show();
            }
        });


        userLocalStore = new UserLocalStore(getApplicationContext());
        user = userLocalStore.getLoggedInUser();

        if (TextUtils.equals(user.getPhone().trim(), "")) {
            verify.setVisibility(View.GONE);
        }

        /*all_country api call start*/
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();
        final String url = getResources().getString(R.string.api) + "all_country";

        final JsonObjectRequest request = new JsonObjectRequest(GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("all_country");

                            if (!TextUtils.equals(response.getString("all_country"), "[]")) {
                                //noUpcoming.setVisibility(View.GONE);
                            } else {
                            }
                            JSON_PARSE_DATA_AFTER_WEBCALL(arr);

                            pQueue = Volley.newRequestQueue(getApplicationContext());
                            pQueue.getCache().clear();

                            //my_profile api for get user data
                            String url = getResources().getString(R.string.api) + "my_profile/" + user.getMemberid();

                            final JsonObjectRequest prequest = new JsonObjectRequest(url, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            loadingDialog.dismiss();
                                            Log.d("profile", response.toString());
                                            try {
                                                JSONObject obj = new JSONObject(response.getString("my_profile"));
                                                profileFirstName.setText(obj.getString("first_name"));
                                                profileLastName.setText(obj.getString("last_name"));
                                                if (TextUtils.equals(obj.getString("first_name"), "null")) {
                                                    profileFirstName.setText("");
                                                }
                                                if (TextUtils.equals(obj.getString("last_name"), "null")) {
                                                    profileLastName.setText("");
                                                }
                                                profileUserName.setText(obj.getString("user_name"));
                                                profileEmail.setText(obj.getString("email_id"));
                                                profileMobileNumber.setText(obj.getString("mobile_no"));
                                                profileDateOfBirth.setText(obj.getString("dob"));
                                                if (!obj.isNull("profile_pic") && !obj.getString("profile_pic").equals("")) {
                                                    Picasso.get().load(getString(R.string.image_api) + obj.getString("profile_pic")).placeholder(R.drawable.man).error(R.drawable.man).into(userProfile);
                                                } else {
                                                    Picasso.get().load(R.drawable.man).placeholder(R.drawable.man).error(R.drawable.man).into(userProfile);
                                                }
                                                if (TextUtils.equals(obj.getString("dob"), "null")) {
                                                    profileDateOfBirth.setText("");
                                                }
                                                if (obj.getString("gender").matches("0")) {
                                                    male.setChecked(true);
                                                    female.setChecked(false);
                                                } else if (obj.getString("gender").matches("1")) {
                                                    female.setChecked(true);
                                                    male.setChecked(false);
                                                } else {
                                                    male.setChecked(false);
                                                    female.setChecked(false);
                                                }

                                                countryCode = obj.getString("country_code");
                                                countrycodespinner.setText(countryCode);

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
                                    String token = "Bearer " + user.getToken();
                                    headers.put("Content-Type", "application/json");
                                    headers.put("Authorization", token);
                                    return headers;
                                }
                            };

                            prequest.setShouldCache(false);
                            pQueue.add(prequest);

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
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);
        /*all_game api call end*/
        //viewallcountry();

        dialog = new Dialog(this);


        profileDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.dobpicker);

                final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datepicker);
                Button dob_set = (Button) dialog.findViewById(R.id.dob_set);
                Button dob_cancel = (Button) dialog.findViewById(R.id.dob_cancel);

                dob_set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        String selecteddate = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear();
                        profileDateOfBirth.setText(selecteddate);
                    }
                });
                dob_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        profileMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                SharedPreferences sp = getSharedPreferences("SMINFO", MODE_PRIVATE);
                if (TextUtils.equals(sp.getString("otp", "no"), "no")) {

                } else {
                    if (TextUtils.equals(user.getPhone().trim(), "") && TextUtils.equals(user.getPhone().trim(), charSequence)) {
                        verify.setVisibility(View.GONE);
                    } else {
                        verify.setVisibility(View.VISIBLE);
                    }
                    if (TextUtils.equals(user.getPhone(), charSequence)) {
                        verify.setText(getResources().getString(R.string.verified));
                        verify.setEnabled(false);
                    } else {
                        verify.setText(getResources().getString(R.string.verify));
                        verify.setEnabled(true);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                loadingDialog.dismiss();
                builder.dismiss();
                updateMobile(profileMobileNumber.getText().toString(), countryCode);


            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                e.printStackTrace();
                Log.d("failed", e.getMessage());
                loadingDialog.dismiss();
                Toast.makeText(MyProfileActivity.this, "Something went wrong, Please try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);


                Log.d("codesent", s);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.otp_send_successfully), Toast.LENGTH_SHORT).show();

                openotpdialog(s);
            }

        };

        SharedPreferences sp = getSharedPreferences("SMINFO", MODE_PRIVATE);
        if (TextUtils.equals(sp.getString("otp", "no"), "no")) {
            verify.setVisibility(View.GONE);
        } else {
            verify.setVisibility(View.VISIBLE);
        }
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.equals(user.getMemberid(), "21") && TextUtils.equals(user.getUsername(), "demouser")) {
                    Toast.makeText(MyProfileActivity.this, "You can't update demo profile", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences sp = getSharedPreferences("SMINFO", MODE_PRIVATE);
                if (TextUtils.equals(sp.getString("otp", "no"), "no")) {

                } else {
                    if (profileMobileNumber.getText().toString().trim().length() < 7 || profileMobileNumber.getText().toString().trim().length() > 15) {
                        profileMobileNumber.setError(getResources().getString(R.string.wrong_mobile_number___));
                        return;
                    }
                    checkMobile(profileMobileNumber.getText().toString().trim());
                }

            }
        });


        profileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.equals(user.getMemberid(), "21") && TextUtils.equals(user.getUsername(), "demouser")) {
                    Toast.makeText(MyProfileActivity.this, "You can't update demo profile", Toast.LENGTH_SHORT).show();
                    return;
                }


                final String firstname = profileFirstName.getText().toString();
                final String lastname = profileLastName.getText().toString();
                final String username = profileUserName.getText().toString();
                if (username.contains(" ")) {
                    profileUserName.setError(getResources().getString(R.string.no_space_allowed));
                    return;
                }
                final String email = profileEmail.getText().toString();
                final String mobilenumber = profileMobileNumber.getText().toString();
                if (mobilenumber.length() < 7 || mobilenumber.length() > 15) {
                    profileMobileNumber.setError(getResources().getString(R.string.wrong_mobile_number___));
                    return;
                }
                SharedPreferences sp = getSharedPreferences("SMINFO", MODE_PRIVATE);
                if (TextUtils.equals(sp.getString("otp", "no"), "no")) {

                } else {
                    if (TextUtils.equals(verify.getText().toString(), getResources().getString(R.string.verify))) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_verify_mobile_number), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                String promocode = "";
                String dateofbirth = profileDateOfBirth.getText().toString();
                String gender = "";
                switch (profileGender.getCheckedRadioButtonId()) {
                    case R.id.male:
                        gender = "0";
                        break;
                    case R.id.female:
                        gender = "1";
                        break;
                    default:
                        gender = "-1";
                }

                loadingDialog.show();
//                String uurl = getResources().getString(R.string.api) + "update_myprofile";
                String uurl = getResources().getString(R.string.api) + "update_myprofile_v1";

                String finalGender = gender;

               /* Map<String, String> headerParam = new HashMap<>();
                CurrentUser user = userLocalStore.getLoggedInUser();
                String credentials = user.getUsername() + ":" + user.getPassword();
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                String token = "Bearer " + user.getToken();
                headerParam.put("Content-Type", "application/json");
                headerParam.put("Authorization", token);


                VolleyMultipartRequest stringRequest = new VolleyMultipartRequest(Request.Method.POST, headerParam, uurl, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse networkResponse) {
                        loadingDialog.dismiss();
                        try {
                            String responseString = new String(networkResponse.data);
                            JSONObject response = new JSONObject(responseString);
                            if (response.getString("status").matches("true")) {
                                Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                                userLocalStore = new UserLocalStore(getApplicationContext());
                                final CurrentUser user = userLocalStore.getLoggedInUser();
                                CurrentUser cUser = new CurrentUser(user.getMemberid(), username, user.getPassword(), email, mobilenumber, user.getToken());
                                UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                userLocalStore.storeUserData(cUser);
                            } else {
                                Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                        params.put("member_id", user.getMemberid());
                        params.put("first_name", firstname);
                        params.put("last_name", lastname);
                        params.put("user_name", username);
                        params.put("mobile_no", mobilenumber);
                        params.put("email_id", email);
                        params.put("dob", dateofbirth);
                        params.put("gender", finalGender);
                        params.put("submit", "save");
                        params.put("member_pass", user.getPassword());
                        params.put("country_code", countryCode);
                        Log.d("UIUtils.TAG", "SAVE_NEW_LEAD_GENRATION Params==>" + params);
                        return params;
                    }

                    public Map<String, DataPart> getByteData() {
                        Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                        if (inputData != null) {
                            params.put("profile_pic", new VolleyMultipartRequest.DataPart("profile.jpg", inputData, "image/jpeg"));
                        }
                        Log.d("UIUtils.TAG", "SAVE_NEW_LEAD_GENRATION Params==>" + params);
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppSingletoneClass.getInstance(mActivity).addToRequestQueue(stringRequest, "update_myprofile");*/
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    //your codes here

                }

                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body;
                /*params.put("member_id", user.getMemberid());
                        params.put("first_name", firstname);
                        params.put("last_name", lastname);
                        params.put("user_name", username);
                        params.put("mobile_no", mobilenumber);
                        params.put("email_id", email);
                        params.put("dob", dateofbirth);
                        params.put("gender", finalGender);
                        params.put("submit", "save");
                        params.put("member_pass", user.getPassword());
                        params.put("country_code", countryCode);*/
                if (inputData != null) {
                    body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("country_code", countryCode)
                            .addFormDataPart("dob", dateofbirth)
                            .addFormDataPart("last_name", lastname)
                            .addFormDataPart("member_pass", user.getPassword())
                            .addFormDataPart("submit", "save")
                            .addFormDataPart("gender", finalGender)
                            .addFormDataPart("mobile_no", mobilenumber)
                            .addFormDataPart("user_name", username)
                            .addFormDataPart("email_id", email)
                            .addFormDataPart("first_name", firstname)
                            .addFormDataPart("member_id", user.getMemberid())
                            .addFormDataPart("profile_pic", "profile_pic.jpg", RequestBody.create(MediaType.parse("application/octet-stream"), inputData))
                            .build();
                } else {
                    body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("country_code", countryCode)
                            .addFormDataPart("dob", dateofbirth)
                            .addFormDataPart("last_name", lastname)
                            .addFormDataPart("member_pass", user.getPassword())
                            .addFormDataPart("submit", "save")
                            .addFormDataPart("gender", finalGender)
                            .addFormDataPart("mobile_no", mobilenumber)
                            .addFormDataPart("user_name", username)
                            .addFormDataPart("email_id", email)
                            .addFormDataPart("first_name", firstname)
                            .addFormDataPart("member_id", user.getMemberid())
                            .addFormDataPart("profile_pic", "")
                            .build();
                }

                CurrentUser user = userLocalStore.getLoggedInUser();
                String credentials = user.getUsername() + ":" + user.getPassword();
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                String token = "Bearer " + user.getToken();
                Request request = new Request.Builder()
                        .url("https://metaclops.in/api/update_myprofile_v1")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", token)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        JSONObject responseObj = null;
                        try {
                            responseObj = new JSONObject(response.body().string());

                            if (responseObj.getString("status").matches("true")) {
                                loadingDialog.dismiss();
                                JSONObject finalResponseObj = responseObj;
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            Toast.makeText(MyProfileActivity.this, finalResponseObj.getString("message"), Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                userLocalStore = new UserLocalStore(getApplicationContext());
                                final CurrentUser user = userLocalStore.getLoggedInUser();
                                CurrentUser cUser = new CurrentUser(user.getMemberid(), username, user.getPassword(), email, mobilenumber, user.getToken());
                                UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                userLocalStore.storeUserData(cUser);
                            } else {
                                JSONObject finalResponseObj1 = responseObj;
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            Toast.makeText(MyProfileActivity.this, finalResponseObj1.getString("message"), Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });/*.execute();*/
            }
        });
        profileReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.equals(user.getMemberid(), "21") && TextUtils.equals(user.getUsername(), "demouser")) {
                    Toast.makeText(MyProfileActivity.this, "You can't update demo profile", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String old_pass = oldPassword.getText().toString();
                final String new_pass = newPassword.getText().toString().trim();
                final String retype_new_pass = newPassword.getText().toString().trim();
                if (TextUtils.isEmpty(old_pass)) {
                    oldPassword.setError(getResources().getString(R.string.enter_password));
                    return;
                }
                if (TextUtils.isEmpty(new_pass)) {
                    newPassword.setError(getResources().getString(R.string.enter_new_password));
                    return;
                }
                if (TextUtils.isEmpty(retype_new_pass)) {
                    retypeNewPassword.setError(getResources().getString(R.string.retype_new_password));
                    return;
                }
                if (!TextUtils.equals(new_pass, retype_new_pass)) {
                    retypeNewPassword.setError(getResources().getString(R.string.password_not_matched___));
                    return;
                }
                loadingDialog.show();
                rQueue = Volley.newRequestQueue(getApplicationContext());
                rQueue.getCache().clear();

                //update_myprofile api call after validate all password field for password reset
                String rurl = getResources().getString(R.string.api) + "update_myprofile";

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("oldpass", old_pass);
                params.put("newpass", new_pass);
                params.put("confpass", retype_new_pass);
                params.put("submit", "reset");
                params.put("member_id", user.getMemberid());

                final JsonObjectRequest rrequest = new JsonObjectRequest(rurl, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                loadingDialog.dismiss();
                                try {
                                    if (response.getString("status").matches("true")) {
                                        Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                        userLocalStore = new UserLocalStore(getApplicationContext());
                                        final CurrentUser user = userLocalStore.getLoggedInUser();
                                        CurrentUser cUser = new CurrentUser(user.getMemberid(), user.getUsername(), retype_new_pass, user.getEmail(), user.getPhone(), user.getToken());
                                        UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                        userLocalStore.storeUserData(cUser);

                                    } else {
                                        Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                    oldPassword.setText("");
                                    newPassword.setText("");
                                    retypeNewPassword.setText("");
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

                rrequest.setShouldCache(false);
                rQueue.add(rrequest);
            }
        });


    }

    /*Image Picker*/
    private void showImageOptionDialog() {
        REQUEST_CAMERA = 1;
        SELECT_FILE = 2;
        PIC_CROP = 3;

        final Dialog mDialog = new Dialog(mActivity);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        mDialog.setContentView(R.layout.custom_image_picker_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;

        mDialog.getWindow().setAttributes(lp);
        final CustomTextView imgGallery = (CustomTextView) mDialog.findViewById(R.id.chooseGallery);
        final CustomTextView imgCamera = (CustomTextView) mDialog.findViewById(R.id.captureImage);
        final CustomTextView cancleImage = (CustomTextView) mDialog.findViewById(R.id.cancleImage);

        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_FILE);
                mDialog.dismiss();
            }
        });

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                /*output = new File(mActivity.getExternalCacheDir(), String.valueOf(System.currentTimeMillis()) + ".jpg");
                outputPath = Uri.fromFile(output);
                intent.putExtra(EXTRA_OUTPUT, outputPath);*/
                startActivityForResult(intent, REQUEST_CAMERA);

                mDialog.dismiss();
            }
        });

        cancleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2021) {

            resultUri = data.getData();
            Log.d("LogUtils.TAG", "resultUri ===> " + resultUri);
//            Picasso.get().load(resultUri).placeholder(R.drawable.dummy).error(R.drawable.dummy).into(imgProfile);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis;
            try {
                fis = new FileInputStream(new File(resultUri.getPath()));
                byte[] buf = new byte[1024];
                int n;
                while (-1 != (n = fis.read(buf)))
                    baos.write(buf, 0, n);
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte[] bbytes = baos.toByteArray();

            try {
                iStream = mActivity.getContentResolver().openInputStream(resultUri);
                userProfile.setImageURI(resultUri);
                inputData = bbytes;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_OK && (requestCode == SELECT_FILE || requestCode == REQUEST_CAMERA)) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }




        } else {
            Log.d("LogUtils.TAG", "error while image crop");
//            final Throwable cropError = UCrop.getError(data);
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024000;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap thumbnail = null;
        try {
            CropRequest cropRequest = new CropRequest.Auto(data.getData(), 2021, StorageType.EXTERNAL, Collections.singletonList(AspectRatio.ASPECT_FREE), new CroppyTheme(R.color.colorPrimary));
            Croppy.INSTANCE.start(mActivity, cropRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onCaptureImageResult(Intent data) {
        try {
            Bitmap bitmap;
            if (data.getData() == null) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            }

            CropRequest cropRequest = new CropRequest.Auto(getImageUri(mActivity, bitmap), 2021, StorageType.CACHE, Collections.singletonList(AspectRatio.ASPECT_FREE), new CroppyTheme(R.color.colorPrimary));
            Croppy.INSTANCE.start(mActivity, cropRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Image Picker*/
    public Uri getImageUri(Context inContext, Bitmap inImage) {

        /*ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

        Uri yourUri = Uri.fromFile(destination);
        destinationURI = yourUri;*/


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onCountryCodeItemClick(View mView, int position) {
        countrycodespinner.setText(spinnerArrayCountryCode.get(position));
        countryCode = spinnerArrayCountryCode.get(position);
        builderMobile.dismiss();
        mCountryCodeAdapter.filter("");
    }


    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                spinnerArray.add(json.getString("country_name"));
                spinnerArrayCountryCode.add(json.getString("p_code"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkMobile(final String mobilenumber) {

        loadingDialog.show();

        //register api call for new user

        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();


        final String URL = getResources().getString(R.string.api) + "checkMobileNumber";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("mobile_no", mobilenumber);

        Log.d(URL, new JSONObject(params).toString());

        JsonObjectRequest request_json = new JsonObjectRequest(com.android.volley.Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        Log.d("send otp", response.toString());
                        try {
                            String status = response.getString("status");

                            if (TextUtils.equals(status, "true")) {

                                PhoneAuthOptions options =
                                        PhoneAuthOptions.newBuilder(mAuth)
                                                .setPhoneNumber(countryCode + mobilenumber)       // Phone number to verify
                                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                                .setActivity(MyProfileActivity.this)                 // Activity (for callback binding)
                                                .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
                                                .build();
                                PhoneAuthProvider.verifyPhoneNumber(options);


                            } else {
                                loadingDialog.dismiss();
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    Log.d("erorostring ", errorString);
                }
            }
        });

        request_json.setShouldCache(false);
        mQueue.add(request_json);
    }

    public void openotpdialog(final String fotp) {


        ///////////////////////countdown////////////////////////

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                resend.setText(String.valueOf(millisUntilFinished / 1000));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                resend.setText(getResources().getString(R.string.resend));
                resend.setClickable(true);
                resend.setEnabled(true);
            }

        }.start();

        ////////////////////////countdown///////////////////////
        mobileview.setText(getResources().getString(R.string.we_will_sent_you_otp_in_) + countryCode + profileMobileNumber.getText().toString().trim());

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                loadingDialog.show();
                checkMobile(profileMobileNumber.getText().toString().trim());
            }
        });

        otpView.setShowSoftInputOnFocus(true);
        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {


                loadingDialog.show();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(fotp, otp);
                signInWithPhoneAuthCredential(credential);

            }
        });

        builder.show();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            builder.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");

                            updateMobile(profileMobileNumber.getText().toString(), countryCode);


                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.wrong_otp), Toast.LENGTH_SHORT).show();
                                otpView.setText("");
                                loadingDialog.dismiss();
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    public void updateMobile(String mobile, String code) {


        loadingDialog.show();

        //call  update_mobile_no
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        final String URL = getResources().getString(R.string.api) + "update_mobile_no";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("member_id", user.getMemberid());
        params.put("mobile_no", mobile);
        params.put("country_code", code);
        params.put("country_id", "");

        Log.d(URL, new JSONObject(params).toString());
        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("update mobile response", response.toString());
                        loadingDialog.dismiss();

                        try {
                            String status = response.getString("status");

                            if (TextUtils.equals(status, "true")) {

                                if (response.getString("status").matches("true")) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.mobile_number_successfully_verified), Toast.LENGTH_SHORT).show();
                                    verify.setEnabled(false);
                                    verify.setText(getResources().getString(R.string.verified));
                                    userLocalStore = new UserLocalStore(getApplicationContext());
                                    final CurrentUser user = userLocalStore.getLoggedInUser();
                                    CurrentUser cUser = new CurrentUser(user.getMemberid(), user.getUsername(), user.getPassword(), user.getEmail(), mobile, user.getToken());
                                    UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                    userLocalStore.storeUserData(cUser);

                                } else {
                                    Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                }


                            } else {

                            }
                        } catch (JSONException e) {
                            loadingDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                //Log.d("login fb error response", error.getMessage());
            }
        });
        request_json.setShouldCache(false);
        mQueue.add(request_json);


    }
}
