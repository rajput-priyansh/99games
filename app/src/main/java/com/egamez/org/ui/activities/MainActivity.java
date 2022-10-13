// For Login
package com.egamez.org.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
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
import androidx.cardview.widget.CardView;
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
import com.egamez.org.widget.CustomTextView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.android.volley.Request.Method.GET;

public class MainActivity extends AppCompatActivity implements CountryCodeItemClick {

    TextView signinMain;
    TextView createNewAccount;
    EditText userNameMain, passwordMain;
    TextView resetPassword;
    Boolean doubleBackToExitPressedOnce = false;
    RequestQueue mQueue, dQueue;
    UserLocalStore userLocalStore;
    LoadingDialog loadingDialog;
    SharedPreferences sp;
    String name = "";
    String firstname = "";
    String lastname = "";
    String email = "";
    String fbid = "";
    CustomTextView fb;
    CustomTextView google;
    LoginButton loginButton;
    CallbackManager callbackManager;
    int RC_SIGN_IN = 23;
    CardView googlecv;
    CardView fbcv;
    List<String> spinnerArrayCountryCode;
    List<String> spinnerArrayCountryName;
    List<String> spinnerArrayCountryId;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    Dialog builder;
    OtpView otpView;
    TextView mobileview;
    TextView resend;
    String selectedCode = "";
    String selectedMobile = "";
    String selectedMemberid = "";
    String selectedUsername = "";
    String selectedId = "";
    String selectedEmail = "";
    String selectedApitoken = "";
    String selectedCountryId = "";
    String selectedPromo = "";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private Dialog builderMobile;
    private CountryCodeAdapter mCountryCodeAdapter;
    private TextView countrycode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        spinnerArrayCountryCode = new ArrayList<String>();
        spinnerArrayCountryName = new ArrayList<String>();
        spinnerArrayCountryId = new ArrayList<String>();


        builder = new Dialog(MainActivity.this);
        builder.setContentView(R.layout.mobile_otp_dialog);
        mobileview = (TextView) builder.findViewById(R.id.mobileotptv);
        otpView = (OtpView) builder.findViewById(R.id.otp_view_verifymobile);
        resend = (TextView) builder.findViewById(R.id.resend_verifymobile);


       /* database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");*/

        viewallcountry();

        SharedPreferences.Editor editor = getSharedPreferences("first_time", MODE_PRIVATE).edit();
        editor.putBoolean("f_t", false);
        editor.apply();
        /*
        googlecv = (CardView) findViewById(R.id.googlecv);
        fbcv = (CardView) findViewById(R.id.fbcv);*/

        loginButton = (LoginButton) findViewById(R.id.login_button);
        fb = (CustomTextView) findViewById(R.id.fb);
        google = (CustomTextView) findViewById(R.id.googlesignin);
        //TODO PRI Comment check baner ads enable or not
        /*SharedPreferences sp = getSharedPreferences("SMINFO", MODE_PRIVATE);
        if (TextUtils.equals(sp.getString("fb", "no"), "no")) {
            fb.setVisibility(View.GONE);
        } else {
            fb.setVisibility(View.VISIBLE);
        }
        if (TextUtils.equals(sp.getString("google", "no"), "no")) {
            google.setVisibility(View.GONE);
        } else {
            google.setVisibility(View.VISIBLE);
        }*/
        loadingDialog = new LoadingDialog(this);

        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();
        userLocalStore = new UserLocalStore(this);

        createNewAccount = (TextView) findViewById(R.id.createnewaccount);
        signinMain = (TextView) findViewById(R.id.signin_main);
        userNameMain = (EditText) findViewById(R.id.username_main);
        passwordMain = (EditText) findViewById(R.id.password_main);
        resetPassword = (TextView) findViewById(R.id.resetpassword);

        callbackManager = CallbackManager.Factory.create();

        mAuth = FirebaseAuth.getInstance();

        List<String> permissionNeeds = Arrays.asList("email");
        loginButton.setPermissions(permissionNeeds);

        //LoginManager.getInstance().setLoginBehavior(LoginBehavior.DIALOG_ONLY);
        loginButton.setLoginBehavior(LoginBehavior.WEB_ONLY);
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object,
                                                            GraphResponse response) {

                                        Log.d("LoginActivity", object.toString());

                                        try {

                                            name = object.getString("name");
                                            firstname = object.getString("first_name");
                                            lastname = object.getString("last_name");
                                            fbid = object.getString("id");
                                            try {
                                                email = object.getString("email");
                                            } catch (Exception e) {
                                                email = "";
                                            }

                                            handleFacebookAccessToken(loginResult.getAccessToken(), firstname, lastname, email, fbid);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields",
                                "first_name,last_name,name,email");
                        request.setParameters(parameters);

                        request.executeAsync();
                    }


                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.cancel), Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT);
                        Log.d("error", error.toString());
                    }
                });


        // Configure Google Sign In

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        SpannableString ss = new SpannableString(getResources().getString(R.string.lbl_don_t_have_an_account_sign_up));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(getApplicationContext(), CreateNewAccount.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {

            }
        };
        ss.setSpan(new StyleSpan(Typeface.BOLD), 22, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan, 22, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Span to set text color to some RGB value
        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.parseColor("#05c9d9"));
        // Set the text color for first 4 characters
        ss.setSpan(fcs, 22, 29, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        createNewAccount.setText(ss);



        SpannableString ss2 = new SpannableString(getResources().getString(R.string.lbl_forgot_password));
        //ss2.setSpan(new StyleSpan(Typeface.BOLD), 0, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(Color.WHITE, 0, 15, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        resetPassword.setText(ss2);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //forgot password
                resetPassword();
            }
        });

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateNewAccount.class));
            }
        });
        signinMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String password = passwordMain.getText().toString();
                String username = userNameMain.getText().toString();

                if (TextUtils.isEmpty(userNameMain.getText().toString())) {
                    userNameMain.setError(getResources().getString(R.string.username_required___));
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordMain.setError(getResources().getString(R.string.password_required___));
                    return;
                }

                //after validate call login api
//                loginuser(username, password, "login");
                startActivity(new Intent(getApplicationContext(), HomeActivity.class).putExtra("N", "0"));
            }
        });

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                loadingDialog.dismiss();
                /*Toast.makeText(getApplicationContext(), getResources().getString(R.string.mobile_number_successfully_verified), Toast.LENGTH_SHORT).show();*/
                updateMobile(selectedMemberid, selectedUsername, selectedId, selectedEmail, selectedApitoken, selectedCode, selectedMobile, selectedCountryId, selectedPromo);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                e.printStackTrace();
                Log.d("failed", e.getMessage());
                loadingDialog.dismiss();
                Toast.makeText(MainActivity.this, "Something went wrong, Please try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);


                loadingDialog.dismiss();
                Log.d("codesent", s);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.otp_send_successfully), Toast.LENGTH_SHORT).show();

                openotpdialog(s);
            }

        };


    }

    public void resetPassword() {

        final Dialog builder = new Dialog(MainActivity.this);
        builder.setContentView(R.layout.resetpassword);
        final EditText fpemail = builder.findViewById(R.id.fpemail);
        final TextView fpsendemail = (TextView) builder.findViewById(R.id.fpsendemail);
        TextView fpcancel = (TextView) builder.findViewById(R.id.fpcancel);

        fpsendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(fpemail.getText().toString())) {
                    fpemail.setError("Enter Email or Mobile No.");
                } else {
                    loadingDialog.show();
                    builder.dismiss();

                    //call forgotpassword api
                    mQueue = Volley.newRequestQueue(getApplicationContext());

                    String url = getResources().getString(R.string.api) + "sendOTP";

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("email_mobile", fpemail.getText().toString().trim());

                    final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    loadingDialog.dismiss();

                                    Log.d("fpresponse", response.toString());
                                    try {
                                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                        if (TextUtils.equals(response.getString("status"), "true")) {
                                            Intent intent = new Intent(MainActivity.this, FpOtpVerificationActivity.class);
                                            intent.putExtra("EP", fpemail.getText().toString().trim());
                                            intent.putExtra("MID", response.getString("member_id"));
                                            intent.putExtra("OTP", response.getString("otp"));
                                            startActivity(intent);
                                        } else {

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
                    mQueue.add(request);
                }
            }
        });

        fpcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
        builder.show();
    }

    public void loginuser(final String user_name, final String password, final String submit) {

        loadingDialog.show();
        //call login api
        final String URL = getResources().getString(R.string.api) + "login";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_name", user_name);
        params.put("password", password);
        params.put("submit", submit);


        Log.d(URL, new JSONObject(params).toString());
        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("login response", response.toString());

                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            final JSONObject msgobj = response.getJSONObject("message");
                            final String token = msgobj.getString("api_token");
                            if (TextUtils.equals(status, "true")) {


                                JSONObject obj = new JSONObject(message);
                                String member_id = obj.getString("member_id");
                                String email = obj.getString("email_id");
                                String phone = obj.getString("mobile_no");
                                CurrentUser cUser = new CurrentUser(member_id, user_name, password, email, phone, token);
                                final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                userLocalStore.storeUserData(cUser);

                                loadingDialog.dismiss();
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.login_successfully), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class).putExtra("N", "0"));


                            } else {

                                loadingDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            loadingDialog.dismiss();

                            try {
                                String status = response.getString("status");
                                String message = response.getString("message");
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());

            }
        });
        request_json.setShouldCache(false);
        mQueue.add(request_json);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1500);
    }

    private void handleFacebookAccessToken(AccessToken token, final String fname, final String lname, String email, String fbid) {


        RegisterFB(fname, lname, fname, email, "", fbid, "fb_login");

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

    public void RegisterFB(final String first_name, final String last_name, final String user_name, final String email, final String phone, final String fbid, final String submit) {

        final Profile profile = Profile.getCurrentProfile();
        loadingDialog.show();
        //call login api
        final String URL = getResources().getString(R.string.api) + "registerFB";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("first_name", first_name);
        params.put("last_name", last_name);
        params.put("user_name", first_name);
        params.put("email_id", email);
        params.put("mobile_no", phone);
        params.put("fb_id", fbid);
        params.put("submit", submit);


        Log.d(URL, new JSONObject(params).toString());
        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("fb_login_response", response.toString());


                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            if (TextUtils.equals(status, "true")) {


                                if (TextUtils.equals(response.getJSONObject("message").getString("mobile_no"), "")) {
                                    // member id. username, gid,email,phone,apitoken
                                    loadingDialog.dismiss();
                                    final String member_id = response.getJSONObject("message").getString("member_id");

                                    openMobileDialog(member_id, response.getJSONObject("message").getString("user_name"), fbid, response.getJSONObject("message").getString("email_id"), response.getJSONObject("message").getString("api_token"), response.getJSONObject("message").getString("new_user"));
                                } else {
                                    String member_id = response.getJSONObject("message").getString("member_id");

                                    CurrentUser cUser = new CurrentUser(member_id, response.getJSONObject("message").getString("user_name"), fbid, email, phone, response.getJSONObject("message").getString("api_token"));
                                    final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                    userLocalStore.storeUserData(cUser);

                                    loadingDialog.dismiss();
                                    Toast.makeText(MainActivity.this, getResources().getString(R.string.login_successfully), Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class).putExtra("N", "0"));

                                }
                                //JSONObject obj = new JSONObject(message);


                            } else {
                                if (profile != null) {
                                    // user has logged in
                                    LoginManager.getInstance().logOut();

                                } else {
                                    // user has not logged in
                                }

                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
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

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        loadingDialog.show();
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                loadingDialog.dismiss();
                // Google Sign In failed, update UI appropriately
                Log.w("google failed", "Google sign in failed", e);
                // ...
            }
        } else {
            if (responseCode == RESULT_OK) {
                callbackManager.onActivityResult(requestCode, responseCode, data);
            } else {
                loadingDialog.dismiss();
            }


        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("google succesws", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d("google name", user.getDisplayName());
                            Log.d("google email", user.getEmail());
                            Log.d("google uuid", user.getUid());
                            String[] name = user.getDisplayName().split("\\s+");

                            Log.d("google firstname", name[0]);
                            //Log.d("google lastname", name[1]);
                            String p_ = "";
                            try {
                                p_ = user.getPhoneNumber();
                                Log.d("google phone number", user.getPhoneNumber());
                            } catch (Exception e) {
                                p_ = "";
                            }
                            if (TextUtils.equals(p_, null)) {
                                p_ = "";
                            }
                            if (name.length == 1) {
                                RegisterGoogle(name[0], "", user.getDisplayName(), user.getEmail(), p_, user.getUid(), "google_login");
                            } else {
                                RegisterGoogle(name[0], name[1], user.getDisplayName(), user.getEmail(), p_, user.getUid(), "google_login");
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("google err final", "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }

    public void RegisterGoogle(final String first_name, final String last_name, final String user_name, final String email, final String phone, final String gid, final String submit) {

        final Profile profile = Profile.getCurrentProfile();
        loadingDialog.show();
        //call login api
        final String URL = getResources().getString(R.string.api) + "registerGoogle";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("first_name", first_name);
        params.put("last_name", last_name);
        params.put("user_name", first_name);
        params.put("email_id", email);
        params.put("mobile_no", phone);
        params.put("g_id", gid);
        params.put("submit", submit);


        Log.d(URL, new JSONObject(params).toString());
        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("google response", response.toString());

                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            if (TextUtils.equals(status, "true")) {


                                //JSONObject obj = new JSONObject(message);

                                if (TextUtils.equals(response.getJSONObject("message").getString("mobile_no"), "")) {
                                    // member id. username, gid,email,phone,apitoken
                                    loadingDialog.dismiss();
                                    final String member_id = response.getJSONObject("message").getString("member_id");

                                    openMobileDialog(member_id, response.getJSONObject("message").getString("user_name"), gid, response.getJSONObject("message").getString("email_id"), response.getJSONObject("message").getString("api_token"), response.getJSONObject("message").getString("new_user"));
                                } else {
                                    String member_id = response.getJSONObject("message").getString("member_id");

                                    CurrentUser cUser = new CurrentUser(member_id, response.getJSONObject("message").getString("user_name"), gid, email, phone, response.getJSONObject("message").getString("api_token"));
                                    final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                    userLocalStore.storeUserData(cUser);

                                    loadingDialog.dismiss();
                                    Toast.makeText(MainActivity.this, getResources().getString(R.string.login_successfully), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class).putExtra("N", "0"));

                                }
                            } else {
                                if (profile != null) {
                                    // user has logged in
                                    LoginManager.getInstance().logOut();

                                } else {
                                    // user has not logged in
                                }

                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
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

    public void onClick(View v) {
        if (v == fb) {
            loginButton.performClick();
        }
    }


    public void openMobileDialog(final String memberid, final String username, final String id, final String email, final String apitoken, String newuser) {
        // member id. username, gid,email,phone,apitoken


        final Dialog builder = new Dialog(MainActivity.this);
        builder.setContentView(R.layout.enter_mobile_dialog);
        countrycode = (TextView) builder.findViewById(R.id.countrycodespinnerem);
        final TextView mobilenumber = (TextView) builder.findViewById(R.id.em_mobilenumber);
        final TextView promocode = (TextView) builder.findViewById(R.id.first_promocode);
        TextInputLayout promocodetil = (TextInputLayout) builder.findViewById(R.id.firstpromoTil);
        Button submit = (Button) builder.findViewById(R.id.em_submit);

        if (TextUtils.equals(newuser, "Yes")) {
            promocodetil.setVisibility(View.VISIBLE);
        } else {
            promocodetil.setVisibility(View.GONE);
        }

        countrycode.setText("+91");
        selectedCode = "+91";
        for (int i = 0; i < spinnerArrayCountryCode.size(); i++) {
            if (spinnerArrayCountryCode.get(i).equalsIgnoreCase("+91")) {
                selectedCountryId = spinnerArrayCountryId.get(i);
            }
        }


        countrycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builderMobile = new Dialog(MainActivity.this);
                builderMobile.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                builderMobile.setCancelable(false);
                WindowManager.LayoutParams wlmp = builderMobile.getWindow().getAttributes();
                builderMobile.getWindow().setAttributes(wlmp);
                builderMobile.setContentView(R.layout.spinner_layout);

                ImageView cancel = builderMobile.findViewById(R.id.spinnercancel);
                LinearLayout ll = builderMobile.findViewById(R.id.spinneritemll);
                EditText edtSearchCountry = builderMobile.findViewById(R.id.edtSearchCountry);
                RecyclerView rvCountryList = builderMobile.findViewById(R.id.rvCountryList);
                rvCountryList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                mCountryCodeAdapter = new CountryCodeAdapter(MainActivity.this, spinnerArrayCountryName, spinnerArrayCountryCode, MainActivity.this);
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

                for (int i = 0; i < spinnerArrayCountryCode.size(); i++) {
                    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    View view = getLayoutInflater().inflate(R.layout.spinner_item_layout, null);
                    view.setLayoutParams(lparams);

                    TextView tv = view.findViewById(R.id.tv);
                    tv.setText(spinnerArrayCountryCode.get(i) + " (" + spinnerArrayCountryName.get(i) + ")");
                    final int finalI = i;
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final TextView t = (TextView) v;
                            countrycode.setText(spinnerArrayCountryCode.get(finalI));
                            selectedCode = spinnerArrayCountryCode.get(finalI);
                            selectedCountryId = spinnerArrayCountryId.get(finalI);


                            builder.dismiss();
                        }
                    });
                    ll.addView(view);

                }
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.equals(selectedCode.trim(), "")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_select_country_code), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(mobilenumber.getText().toString().trim())) {
                    mobilenumber.setError(getResources().getString(R.string.mobile_number_required___));
                    return;
                }
                if (mobilenumber.getText().toString().trim().length() < 7 || mobilenumber.getText().toString().trim().length() > 15) {
                    mobilenumber.setError(getResources().getString(R.string.wrong_mobile_number___));
                    return;
                }


                loadingDialog.show();

                SharedPreferences sp = getSharedPreferences("SMINFO", MODE_PRIVATE);
                if (TextUtils.equals(sp.getString("otp", "no"), "no")) {
                    // member id. username, gid,email,apitoken

                    //no otp
                    builder.dismiss();
                    updateMobile(memberid, username, id, email, apitoken, selectedCode, mobilenumber.getText().toString().trim(), selectedCountryId, selectedPromo);


                } else {

                    //send otp


                    selectedMobile = mobilenumber.getText().toString().trim();
                    selectedPromo = promocode.getText().toString().trim();
                    selectedMemberid = memberid;
                    selectedUsername = username;
                    selectedId = id;
                    selectedEmail = email;
                    selectedApitoken = apitoken;

                    mQueue = Volley.newRequestQueue(getApplicationContext());
                    mQueue.getCache().clear();


                    final String URL = getResources().getString(R.string.api) + "checkMobileNumber";
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("mobile_no", selectedMobile);
                    params.put("promo_code", selectedPromo);


                    Log.d(URL, new JSONObject(params).toString());

                    JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {


                                    Log.d("send otp", response.toString());
                                    try {
                                        String status = response.getString("status");

                                        if (TextUtils.equals(status, "true")) {
                                            builder.dismiss();
                                            PhoneAuthOptions options =
                                                    PhoneAuthOptions.newBuilder(mAuth)
                                                            .setPhoneNumber(selectedCode + selectedMobile)       // Phone number to verify
                                                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                                            .setActivity(MainActivity.this)                 // Activity (for callback binding)
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
            }
        });

        builder.create();

        try {
            builder.show();
        } catch (Exception e) {

        }


    }

    @Override
    public void onCountryCodeItemClick(View mView, int position) {
        countrycode.setText(spinnerArrayCountryCode.get(position));
        selectedCode = spinnerArrayCountryCode.get(position);
        selectedCountryId = spinnerArrayCountryId.get(position);

        builderMobile.dismiss();
        mCountryCodeAdapter.filter("");
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
        });
        request.setShouldCache(false);
        mQueue.add(request);


    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                spinnerArrayCountryCode.add(json.getString("p_code"));
                spinnerArrayCountryName.add(json.getString("country_name"));
                spinnerArrayCountryId.add(json.getString("country_id"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateMobile(final String memberid, final String username, final String id, final String email, final String apitoken, String code, final String mobile, final String countryId, String promo) {

        final Profile profile = Profile.getCurrentProfile();
        loadingDialog.show();
        //call login api
        final String URL = getResources().getString(R.string.api) + "update_mobile_no";


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("member_id", memberid);
        params.put("mobile_no", mobile);
        params.put("country_code", code);
        params.put("country_id", countryId);
        params.put("promo_code", promo);


        Log.d(URL, new JSONObject(params).toString());
        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("update mobile response", response.toString());

                        try {
                            String status = response.getString("status");

                            if (TextUtils.equals(status, "true")) {

                                final CurrentUser cUser = new CurrentUser(memberid, username, id, email, mobile, apitoken);
                                loadingDialog.dismiss();
                                final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                userLocalStore.storeUserData(cUser);
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.login_successfully), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class).putExtra("N", "0"));


                            } else {
                                if (profile != null) {
                                    // user has logged in
                                    LoginManager.getInstance().logOut();

                                } else {
                                    // user has not logged in
                                }
                                String message = response.getString("message");

                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
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
        mobileview.setText(getResources().getString(R.string.we_will_sent_you_otp_in_) + selectedCode + selectedMobile);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                loadingDialog.show();
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(selectedCode + selectedMobile)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(MainActivity.this)                 // Activity (for callback binding)
                                .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
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
        builder.create();


        builder.show();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            builder.dismiss();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.mobile_number_successfully_verified), Toast.LENGTH_SHORT).show();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");


                            updateMobile(selectedMemberid, selectedUsername, selectedId, selectedEmail, selectedApitoken, selectedCode, selectedMobile, selectedCountryId, selectedPromo);


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

}
