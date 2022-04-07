//For register new user
package com.egamez.org.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.egamez.org.R;
import com.egamez.org.events.CountryCodeItemClick;
import com.egamez.org.models.CurrentUser;
import com.egamez.org.ui.adapters.CountryCodeAdapter;
import com.egamez.org.utils.LoadingDialog;
import com.egamez.org.utils.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.android.volley.Request.Method.GET;

public class CreateNewAccount extends AppCompatActivity implements CountryCodeItemClick {

    EditText userNameEt, emailEt, mobileEt, passwordEt, promoCodeEt, confirmPasswordEt;
    Button registerNewAccount;
    RequestQueue mQueue, dQueue;
    LoadingDialog loadingDialog;
    String userName = "";
    String email = "";
    String mobileNumber = "";
    String password = "";
    String confirmPasssword = "";
    String promoCode = "";
    FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    TextView countrycodespinner, signIn;
    String countryCode = "";
    List<String> spinnerArray;
    List<String> spinnerArrayCountryCode;
    private Dialog builder;
    private CountryCodeAdapter mCountryCodeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(this);

        userNameEt = (EditText) findViewById(R.id.register_username);
        emailEt = (EditText) findViewById(R.id.register_email);
        mobileEt = (EditText) findViewById(R.id.register_mobilenumber);
        passwordEt = (EditText) findViewById(R.id.register_password);
        promoCodeEt = (EditText) findViewById(R.id.register_promocode);
        confirmPasswordEt = (EditText) findViewById(R.id.register_confirmpassword);
        registerNewAccount = (Button) findViewById(R.id.registernewaccount);

        countrycodespinner = (TextView) findViewById(R.id.countrycodespinnerregister);

        spinnerArray = new ArrayList<String>();
        spinnerArrayCountryCode = new ArrayList<String>();

        countrycodespinner.setText("+91");
        countryCode = "+91";
        countrycodespinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder = new Dialog(CreateNewAccount.this);
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                builder.setCancelable(false);
                WindowManager.LayoutParams wlmp = builder.getWindow().getAttributes();
                builder.getWindow().setAttributes(wlmp);
                builder.setContentView(R.layout.spinner_layout);

                ImageView cancel = builder.findViewById(R.id.spinnercancel);
                LinearLayout ll = builder.findViewById(R.id.spinneritemll);
                EditText edtSearchCountry = builder.findViewById(R.id.edtSearchCountry);
                RecyclerView rvCountryList = builder.findViewById(R.id.rvCountryList);
                rvCountryList.setLayoutManager(new LinearLayoutManager(CreateNewAccount.this));
                mCountryCodeAdapter = new CountryCodeAdapter(CreateNewAccount.this, spinnerArray, spinnerArrayCountryCode, CreateNewAccount.this);
                rvCountryList.setAdapter(mCountryCodeAdapter);
                edtSearchCountry.setHint("Search...");
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

                /*for (int i = 0; i < spinnerArray.size(); i++) {
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
                            //countryidString=spinnerArrayid.get(finalI);
                            countryCode = spinnerArrayCountryCode.get(finalI);
                            builder.dismiss();
                        }
                    });
                    if (spinnerArray.get(i).contains("India")) {
                        tv.performClick();
                    }
                    ll.addView(view);
                }*/

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                        mCountryCodeAdapter.filter("");
                    }
                });

                builder.create();
                builder.show();
            }
        });

        viewallcountry();

        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        userNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        promoCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {

                } else {
                    registerNewAccount.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        signIn = (TextView) findViewById(R.id.signin);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        registerNewAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                userName = userNameEt.getText().toString().trim();
                email = emailEt.getText().toString().trim();
                mobileNumber = mobileEt.getText().toString().trim();
                password = passwordEt.getText().toString().trim();
                confirmPasssword = passwordEt.getText().toString().trim();
                promoCode = promoCodeEt.getText().toString().trim();

                if (TextUtils.isEmpty(userName)) {
                    userNameEt.setError(getResources().getString(R.string.username_required___));
                    return;
                }
                if (userName.contains(" ")) {
                    userNameEt.setError(getResources().getString(R.string.no_space_allowed));
                    return;
                }
                if (TextUtils.equals(countryCode.trim(), "")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_select_country_code), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(mobileNumber)) {
                    mobileEt.setError(getResources().getString(R.string.mobile_number_required___));
                    return;
                }
                if (mobileNumber.length() < 7 || mobileNumber.length() > 15) {
                    mobileEt.setError(getResources().getString(R.string.wrong_mobile_number___));
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    emailEt.setError(getResources().getString(R.string.email_required___));
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEt.setError(getResources().getString(R.string.wrong_email_address___));
                    return;
                }


                if (TextUtils.isEmpty(password)) {
                    passwordEt.setError(getResources().getString(R.string.password_required___));
                    return;
                }
                if (TextUtils.isEmpty(confirmPasssword)) {
                    confirmPasswordEt.setError(getResources().getString(R.string.retype_your_password___));
                    return;
                }

                if (!TextUtils.equals(password, confirmPasssword)) {
                    confirmPasswordEt.setError(getResources().getString(R.string.password_not_matched___));
                    return;
                }

                SharedPreferences sp = getSharedPreferences("SMINFO", MODE_PRIVATE);
                if (TextUtils.equals(sp.getString("otp", "no"), "no")) {

                    registeruser(promoCode, userName, mobileNumber, email, password, confirmPasssword, "register");

                } else {
                    //callback method for phone number verification
                    mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            signInWithPhoneAuthCredential(phoneAuthCredential);

                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            e.printStackTrace();
                            Log.d("failed", e.getMessage());
                            loadingDialog.dismiss();
                            Toast.makeText(CreateNewAccount.this, "Something went wrong, Please try again", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(s, forceResendingToken);

                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.otp_send_successfully), Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), OtpVerifyActivity.class);
                            intent.putExtra("USER_NAME", userName);
                            intent.putExtra("MOBILE_NO", mobileNumber);
                            intent.putExtra("EMAIL_ID", email);
                            intent.putExtra("PASS", password);
                            intent.putExtra("CPASS", confirmPasssword);
                            intent.putExtra("PROMO_CODE", promoCode);
                            intent.putExtra("API_OTP", s);
                            intent.putExtra("COUNTRY_CODE", countryCode);
                            startActivity(intent);
                        }
                    };
                    sendotp(promoCode, userName, mobileNumber, email, password, confirmPasssword, "register");
                }

            }
        });
    }

    @Override
    public void onCountryCodeItemClick(View mView, int position) {
        countrycodespinner.setText(spinnerArrayCountryCode.get(position));
        countryCode = spinnerArrayCountryCode.get(position);
        builder.dismiss();
        mCountryCodeAdapter.filter("");
    }

    public void sendotp(final String promoCode, final String userNameEt, final String mobile_no, final String emailId, final String password, final String cpassword, final String submit) {

        loadingDialog.show();

        //checkMember api call for new user
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        final String URL = getResources().getString(R.string.api) + "checkMember";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("promo_code", promoCode);
        params.put("user_name", userNameEt);
        params.put("mobile_no", mobile_no);
        params.put("country_code", countryCode);
        params.put("email_id", emailId);
        params.put("password", password);
        params.put("cpassword", cpassword);
        params.put("submit", submit);

        Log.d(URL, new JSONObject(params).toString());

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        Log.d("send otp", response.toString());
                        try {
                            String status = response.getString("status");

                            if (TextUtils.equals(status, "true")) {

                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.otp_send_successfully), Toast.LENGTH_SHORT).show();

                                PhoneAuthOptions options =
                                        PhoneAuthOptions.newBuilder(mAuth)
                                                .setPhoneNumber(countryCode + mobile_no)       // Phone number to verify
                                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                                .setActivity(CreateNewAccount.this)                 // Activity (for callback binding)
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

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            registeruser(promoCode, userName, mobileNumber, email, password, confirmPasssword, "register");

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    public void registeruser(final String promoCode, final String userNameEt, final String mobile_no, final String emailId, final String password, final String cPassword, final String submit) {

        loadingDialog.show();

        //register api call for new user
        final String URL = getResources().getString(R.string.api) + "registrationAcc";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("promo_code", promoCode);
        params.put("user_name", userNameEt);
        params.put("mobile_no", mobile_no);
        params.put("email_id", emailId);
        params.put("password", password);
        params.put("cpassword", cPassword);
        params.put("country_code", countryCode);
        params.put("submit", submit);
        Log.d(URL, new JSONObject(params).toString());

        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        Log.d("create", response.toString());
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            if (TextUtils.equals(status, "true")) {
                                final String token = response.getString("api_token");
                                final String member_id = response.getString("member_id");


                                CurrentUser cUser = new CurrentUser(member_id, userNameEt, password, emailId, mobile_no, token);
                                final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                userLocalStore.storeUserData(cUser);
                                loadingDialog.dismiss();
                                Toast.makeText(CreateNewAccount.this, getResources().getString(R.string.registration_successfully), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class).putExtra("N", "0"));


                            } else {
                                mAuth.signOut();
                                loadingDialog.dismiss();
                                Toast.makeText(CreateNewAccount.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mAuth.signOut();
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        request_json.setShouldCache(false);
        mQueue.add(request_json);
    }

    public void viewallcountry() {

        /*all_country api call start*/
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();
        String url = getResources().getString(R.string.api) + "all_country";
        final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());

        final JsonObjectRequest request = new JsonObjectRequest(GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();
                        try {
                            JSONArray arr = response.getJSONArray("all_country");

                            if (!TextUtils.equals(response.getString("all_country"), "[]")) {
                                //noUpcoming.setVisibility(View.GONE);
                            } else {
                                //noUpcoming.setVisibility(View.VISIBLE);
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
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);
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

}
