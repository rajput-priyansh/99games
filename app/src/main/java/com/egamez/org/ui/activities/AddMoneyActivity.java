//For add money to app wallet
package com.egamez.org.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gocashfree.cashfreesdk.CFPaymentService;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.instamojo.android.Instamojo;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.egamez.org.R;
import com.egamez.org.models.CurrentUser;
import com.egamez.org.utils.CustomTypefaceSpan;
import com.egamez.org.utils.LoadingDialog;
import com.egamez.org.utils.UserLocalStore;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class AddMoneyActivity extends AppCompatActivity implements Instamojo.InstamojoPaymentCallback, PaymentResultWithDataListener {

    private static final int TEZ_REQUEST_CODE = 123;
    private static final String GOOGLE_TEZ_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    String amount;
    int amountInt = 0;
    float amountFloat = 0;
    int minAmoubtInt = 0;
    ImageView back;
    RequestQueue mQueue, rQueue, pQueue, psQueue;
    UserLocalStore userLocalStore;
    String custId = "";
    String finalTamount = "";
    LoadingDialog loadingDialog;
    int PAYPAL_REQUEST_CODE = 1234;
    int STRIPE_REQUEST_CODE = 50000;
    PayPalConfiguration config;
    String payment = "";
    String minAmount = "";
    String modeStatus = "";
    String paymentDescrption = "";
    String secretKey = "";
    Card card;
    LinearLayout paystackll;
    TextView cardNumber;
    TextView CVV;
    TextView expMonth;
    TextView expYear;
    TextView paystacktestnote, txtStripeNote;
    Checkout checkout;
    String receipt = "";
    String depositId = "";
    String cfAppid = "";
    String upi_id = "";
    TextInputLayout parentaddmoney;
    RadioGroup addmoneyOption;
    RadioButton rdbtn;
    JSONArray array;
    TextView addNote;
    String point = "";
    long pointInt = 0;
    String selectedCurrency = "";
    String selectedCurrencySymbol = "";
    private LinearLayout llStripePaymentContainer;
    private CardInputWidget cardInputWidget;

    private int REQUEST_CODE_LOCATION = 100;
    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;
    private static final String BACKEND_URL = "http://metaclops.in/api/public/";
    private PaymentMethodCreateParams params;
    String stripePublishableKey;

    @Override
    protected void onDestroy() {
        //only for paypal
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = null;
                try {
                    json = array.getJSONObject(i);
                    if (TextUtils.equals(json.getString("payment_name"), "PayPal")) {
                        stopService(new Intent(AddMoneyActivity.this, PayPalService.class));
                    }
                } catch (JSONException e) {

                }
            }
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        loadingDialog = new LoadingDialog(AddMoneyActivity.this);
        loadingDialog.show();

        addmoneyOption = (RadioGroup) findViewById(R.id.addmoney_option);
        addNote = (TextView) findViewById(R.id.add_note);
        paystackll = (LinearLayout) findViewById(R.id.paystackll);
        cardNumber = (TextView) findViewById(R.id.add_amount_cardnumber);
        CVV = (TextView) findViewById(R.id.add_amount_cvv);
        expMonth = (TextView) findViewById(R.id.add_amount_expmonth);
        expYear = (TextView) findViewById(R.id.add_amount_expyear);
        paystacktestnote = (TextView) findViewById(R.id.paystacktestnote);
        llStripePaymentContainer = (LinearLayout) findViewById(R.id.llStripePaymentContainer);
        cardInputWidget = (CardInputWidget) findViewById(R.id.cardInputWidget);
        txtStripeNote = (TextView) findViewById(R.id.txtStripeNote);

        txtStripeNote.setVisibility(View.GONE);

        // payment start
        // get payment gateway info in payment api
        pQueue = Volley.newRequestQueue(getApplicationContext());
        pQueue.getCache().clear();

        String url = getResources().getString(R.string.api) + "payment";
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();
                        try {
                            minAmount = response.getString("min_addmoney");
                            JSONArray arr = response.getJSONArray("payment");
                            if (!TextUtils.equals(response.getString("payment"), "[]")) {
                                array = arr;
                                JSON_PARSE_DATA_AFTER_WEBCALL(arr);
                            } else {
                                Toast.makeText(AddMoneyActivity.this, getString(R.string.payment_method_not_available), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MyWalletActivity.class));
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
                userLocalStore = new UserLocalStore(getApplicationContext());
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
        pQueue.add(request);
        //payment end

        back = (ImageView) findViewById(R.id.backfromaddmoney);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyWalletActivity.class);
                startActivity(intent);
            }
        });


        userLocalStore = new UserLocalStore(getApplicationContext());
        final CurrentUser user = userLocalStore.getLoggedInUser();
        custId = user.getMemberid();


        final EditText addamountedit = (EditText) findViewById(R.id.add_amount_edit);
        final Button addamountbtn = (Button) findViewById(R.id.add_amount_btn);
        parentaddmoney = (TextInputLayout) findViewById(R.id.parentaddmoney);

        addamountbtn.setEnabled(false);
        addamountbtn.setBackgroundColor(getResources().getColor(R.color.newdisablegreen));
        addamountedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    addamountbtn.setEnabled(true);
                    addamountbtn.setBackgroundColor(getResources().getColor(R.color.newgreen));
                    if (TextUtils.equals(payment, "PayStack")) {
                        addNote.setText(getResources().getString(R.string.you_will_pay_) + " " + selectedCurrencySymbol + String.valueOf(Integer.parseInt(String.valueOf(charSequence)) / pointInt));
                        if (TextUtils.equals(selectedCurrencySymbol, "₹")) {
                            Typeface font = Typeface.DEFAULT;
                            SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.Rs));
                            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            addNote.setText(TextUtils.concat(getResources().getString(R.string.you_will_pay_), " ", SS, String.valueOf(Integer.parseInt(String.valueOf(charSequence)) / pointInt)));
                        }
                    } else {
                        addNote.setText(getResources().getString(R.string.you_will_pay_) + " " + selectedCurrencySymbol + String.format("%.2f", Double.parseDouble(String.valueOf(charSequence)) / (double) pointInt));
                        if (TextUtils.equals(selectedCurrencySymbol, "₹")) {
                            Typeface font = Typeface.DEFAULT;
                            SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.Rs));
                            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            addNote.setText(TextUtils.concat(getResources().getString(R.string.you_will_pay_), " ", SS, String.format("%.2f", Double.parseDouble(String.valueOf(charSequence)) / (double) pointInt)));
                        }
                    }
                } else {
                    addNote.setText("");
                    addamountbtn.setEnabled(false);
                    addamountbtn.setBackgroundColor(getResources().getColor(R.color.newdisablegreen));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addamountedit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    /* =============== Stipr Initialization start=============*/
                   /* MediaType mediaType = MediaType.get("application/json; charset=utf-8");
                    String mJson = "{"
                            + "\"currency\":\"usd\","
                            + "\"items\":["
                            + "{\"id\":\"photo_subscription\", \"amount\":\"" + amount + "\"}"
                            + "]"
                            + "}";
                    RequestBody body = RequestBody.create(mJson, mediaType);
                    okhttp3.Request mRequest = new okhttp3.Request.Builder()
                            .url(BACKEND_URL + "create-payment-intent.php")
                            .post(body)
                            .build();
                    httpClient.newCall(mRequest)
                            .enqueue(new PayCallback(AddMoneyActivity.this));*/
                    /*=============Stripe Initialization ends===================*/
                }
            }
        });

        addmoneyOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                try {
                    rdbtn = (RadioButton) findViewById(checkedId);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = null;
                        try {
                            json = array.getJSONObject(i);
                            payment = rdbtn.getText().toString();
                            if (TextUtils.equals(json.getString("payment_name"), rdbtn.getText().toString())) {
                                payment = rdbtn.getText().toString();
                                modeStatus = json.getString("payment_status");

                                selectedCurrency = json.getString("currency_code");
                                selectedCurrencySymbol = json.getString("currency_symbol");
                                point = json.getString("currency_point");
                                pointInt = Integer.parseInt(point);

                                if (TextUtils.equals(payment, "PayStack")) {
                                    paystackll.setVisibility(View.VISIBLE);
                                    if (TextUtils.equals(json.getString("payment_status"), "Test")) {
                                        paystacktestnote.setVisibility(View.VISIBLE);
                                    } else {
                                        paystacktestnote.setVisibility(View.GONE);
                                    }
                                    llStripePaymentContainer.setVisibility(View.GONE);
                                } else if (TextUtils.equals(payment, "Stripe")) {
                                    llStripePaymentContainer.setVisibility(View.VISIBLE);
                                } else {
                                    paystackll.setVisibility(View.GONE);
                                    paystacktestnote.setVisibility(View.GONE);
                                    llStripePaymentContainer.setVisibility(View.GONE);
                                }

                                if (addamountedit.getText().toString().trim().length() > 0) {
                                    if (TextUtils.equals(payment, "PayStack")) {
                                        addNote.setText(getResources().getString(R.string.you_will_pay_) + " " + selectedCurrencySymbol + String.valueOf(Integer.parseInt(addamountedit.getText().toString().trim()) / pointInt));
                                        if (TextUtils.equals(selectedCurrencySymbol, "₹")) {
                                            Typeface font = Typeface.DEFAULT;
                                            SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.Rs));
                                            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                            addNote.setText(TextUtils.concat(getResources().getString(R.string.you_will_pay_), " ", SS, String.valueOf(Integer.parseInt(addamountedit.getText().toString().trim()) / pointInt)));
                                        }
                                    } else {
                                        addNote.setText(getResources().getString(R.string.you_will_pay_) + " " + selectedCurrencySymbol + String.format("%.2f", Double.parseDouble(addamountedit.getText().toString().trim()) / (double) pointInt));
                                        if (TextUtils.equals(selectedCurrencySymbol, "₹")) {
                                            Typeface font = Typeface.DEFAULT;
                                            SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.Rs));
                                            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                            addNote.setText(TextUtils.concat(getResources().getString(R.string.you_will_pay_), " ", SS, String.format("%.2f", Double.parseDouble(addamountedit.getText().toString().trim()) / (double) pointInt)));
                                        }
                                    }

                                } else {
                                    addNote.setText("");

                                }

                            } else if (TextUtils.equals(payment, "Stripe")) {
                                llStripePaymentContainer.setVisibility(View.VISIBLE);
                                if (TextUtils.equals(modeStatus, "Test")) {
                                    txtStripeNote.setVisibility(View.VISIBLE);
                                } else {
                                    txtStripeNote.setVisibility(View.VISIBLE);
                                }
                                    /*if (TextUtils.equals(json.getString("payment_status"), "Test")) {
                                        paystacktestnote.setVisibility(View.VISIBLE);
                                    } else {
                                        paystacktestnote.setVisibility(View.GONE);
                                    }*/
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        addamountbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = addamountedit.getText().toString().trim();
                try {
                    amountInt = Integer.parseInt(amount);
                    minAmoubtInt = Integer.parseInt(minAmount);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (amountInt < minAmoubtInt) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(AddMoneyActivity.this);
                    builder.setTitle(getString(R.string.error));
                    builder.setMessage(getString(R.string.enter_minmum) + minAmount);
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                    builder.create();
                } else {

                    amountFloat = Float.parseFloat(String.format("%.2f", Double.parseDouble(String.valueOf(amountInt)) / (double) pointInt));

                    if (TextUtils.equals(payment, "PayPal")) {
                        // if payment gateway select paypal
                        PaypalPayment(String.valueOf(amountFloat));
                    } else if (TextUtils.equals(payment, "Stripe")) {
//                        llStripePaymentContainer.setVisibility(View.VISIBLE);

                        if (addamountedit.getText().toString().equals("")) {
                            Toast.makeText(AddMoneyActivity.this, "Please enter amount first", Toast.LENGTH_SHORT).show();
                        } else {
                            /* =============== Stripe Initialization start=============*/
                            MediaType mediaType = MediaType.get("application/json; charset=utf-8");
                            String mJson = "{"
                                    + "\"currency\":\"usd\","
                                    + "\"items\":["
                                    + "{\"id\":\"photo_subscription\", \"amount\":\"" + amount + "\"}"
                                    + "]"
                                    + "}";
                            RequestBody body = RequestBody.create(mJson, mediaType);
                            okhttp3.Request mRequest = new okhttp3.Request.Builder()
                                    .url(BACKEND_URL + "create-payment-intent.php")
                                    .post(body)
                                    .build();
                            httpClient.newCall(mRequest)
                                    .enqueue(new PayCallback(AddMoneyActivity.this));
                            /*=============Stripe Initialization ends===================*/
                        }
                    } else if (TextUtils.equals(payment, "PayTm")) {
                        // if payment gateway select paytm
                        loadingDialog.show();
                        //add_money for paytm start
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = getResources().getString(R.string.api) + "add_money";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("payment_name", payment);
                        params.put("MID", "");
                        params.put("ORDER_ID", "");
                        params.put("CUST_ID", custId);
                        params.put("INDUSTRY_TYPE_ID", "");
                        params.put("CHANNEL_ID", "WAP");
                        params.put("TXN_AMOUNT", amount);
                        params.put("WEBSITE", "");
                        params.put("CALLBACK_URL", getResources().getString(R.string.api) + "verifyChecksum");
                        Log.d(url, new JSONObject(params).toString());

                        final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                        Log.d("addmoney", response.toString());
                                        loadingDialog.dismiss();

                                        try {
                                            if (TextUtils.equals(response.getString("status"), "false")) {
                                                Toast.makeText(AddMoneyActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                            } else {
                                                JSONObject obj = new JSONObject(response.getString("message"));
                                                String varifyurl = getResources().getString(R.string.api) + "verifyChecksum";

                                                HashMap<String, String> paramMap = new HashMap<String, String>();
                                                paramMap.put("MID", obj.getString("MID"));
                                                paramMap.put("ORDER_ID", obj.getString("ORDER_ID"));
                                                paramMap.put("TXN_AMOUNT", stripZeros(String.valueOf(amountFloat)));
                                                //Log.d("amount", String.valueOf(Float.parseFloat(String.valueOf(amountInt))/(float) pointInt));
                                                paramMap.put("WEBSITE", obj.getString("WEBSITE"));
                                                paramMap.put("INDUSTRY_TYPE_ID", obj.getString("INDUSTRY_TYPE_ID"));
                                                paramMap.put("CUST_ID", custId);
                                                paramMap.put("CALLBACK_URL", varifyurl);
                                                paramMap.put("CHANNEL_ID", "WAP");
                                                paramMap.put("CHECKSUMHASH", obj.getString("CHECKSUMHASH"));
                                                PaytmPay(paramMap);
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
                        request.setShouldCache(false);
                        mQueue.add(request);
                        //add_money for paytm end
                    } else if (TextUtils.equals(payment, "Offline")) {

                        loadingDialog.show();
                        //add_money for paytm start
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = getResources().getString(R.string.api) + "add_money";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("payment_name", payment);
                        params.put("CUST_ID", custId);
                        params.put("TXN_AMOUNT", amount);

                        final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        Log.d("response", response.toString());
                                        loadingDialog.dismiss();
                                        try {
                                            if (TextUtils.equals(response.getString("status"), "false")) {
                                                Toast.makeText(AddMoneyActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Intent intent = new Intent(getApplicationContext(), OfflinePaymentActivity.class);
                                                intent.putExtra("paymentdesc", paymentDescrption);
                                                startActivity(intent);

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
                        request.setShouldCache(false);
                        mQueue.add(request);

                    } else if (TextUtils.equals(payment, "PayStack")) {

                        if (TextUtils.equals(user.getEmail().trim(), "")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.please_update_profile_with_email), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                            return;
                        }
                        if (TextUtils.equals(cardNumber.getText().toString().trim(), "")) {
                            cardNumber.setError(getString(R.string.please_enter_card_number));
                            return;
                        }
                        if (TextUtils.equals(CVV.getText().toString().trim(), "")) {
                            CVV.setError(getString(R.string.please_enter_cvv));
                            return;
                        }
                        if (TextUtils.equals(expMonth.getText().toString().trim(), "")) {
                            expMonth.setError(getString(R.string.please_enter_expiry_month));
                            return;
                        }
                        if (TextUtils.equals(expYear.getText().toString().trim(), "")) {
                            expYear.setError(getString(R.string.please_enter_expiry_year));
                            return;
                        }

                        String cardnumber = cardNumber.getText().toString().trim();
                        int expirymonth = Integer.parseInt(expMonth.getText().toString().trim()); //any month in the future
                        int expiryyear = Integer.parseInt(expYear.getText().toString().trim()); // any year in the future. '2018' would work also!
                        String cvv = CVV.getText().toString().trim();  // cvv of the test card

                        card = new Card(cardnumber, expirymonth, expiryyear, cvv);

                        if (card.isValid()) {
                            //Toast.makeText(AddMoneyActivity.this, "charged", Toast.LENGTH_SHORT).show();
                            // charge card
                        } else {
                            //Toast.makeText(AddMoneyActivity.this, "not charged", Toast.LENGTH_SHORT).show();
                            //do something
                        }

                        Charge charge = new Charge();
                        charge.setAmount((int) amountFloat);
                        CurrentUser user = userLocalStore.getLoggedInUser();
                        charge.setEmail(user.getEmail());
                        charge.setCard(card); //sets the card to charge
                        performCharge(charge);

                    } else if (TextUtils.equals(payment, "Instamojo")) {

                        loadingDialog.show();
                        //add_money for instamojo start
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = getResources().getString(R.string.api) + "add_money";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("payment_name", payment);
                        params.put("CUST_ID", custId);
                        params.put("TXN_AMOUNT", amount);

                        Log.d(url, new JSONObject(params).toString());

                        final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("addmoney", response.toString());
                                        loadingDialog.dismiss();
                                        try {
                                            Log.d(response.getString("order_id"), response.toString());
                                            if (TextUtils.equals(response.getString("status"), "false")) {
                                                Toast.makeText(AddMoneyActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                            } else {
                                                depositId = response.getString("deposit_id");
                                                initiateInstamojoSDKPayment(response.getString("order_id"));
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
                        request.setShouldCache(false);
                        mQueue.add(request);
                        //add_money for instamojo end

                    } else if (TextUtils.equals(payment, "Razorpay")) {

                        loadingDialog.show();
                        //add_money for razorpay start
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = getResources().getString(R.string.api) + "add_money";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("CUST_ID", custId);
                        params.put("payment_name", payment);
                        final int newamount = 100 * amountInt;
                        params.put("TXN_AMOUNT", String.valueOf(newamount));

                        Log.d(url, new JSONObject(params).toString());

                        final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                        Log.d("addmoney", response.toString());
                                        loadingDialog.dismiss();

                                        try {
                                            if (TextUtils.equals(response.getString("status"), "false")) {
                                                Toast.makeText(AddMoneyActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                            } else {
                                                receipt = response.getString("receipt");
                                                startRazorpayPayment(response.getString("key_id"), response.getString("order_id"), response.getString("currency"), String.valueOf(newamount));
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
                        request.setShouldCache(false);
                        mQueue.add(request);
                        //add_money for razorpay end

                    } else if (TextUtils.equals(payment, "Cashfree")) {

                        if (TextUtils.equals(user.getEmail().trim(), "") && TextUtils.equals(user.getPhone().trim(), "")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.please_update_profile_with_email_and_mobile_number), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                            return;
                        }

                        if (TextUtils.equals(user.getEmail().trim(), "")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.please_update_profile_with_email), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                            return;
                        }
                        if (TextUtils.equals(user.getPhone().trim(), "")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.please_update_profile_with_mobile_number), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                            return;
                        }

                        loadingDialog.show();
                        //add_money for cashfree start
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = getResources().getString(R.string.api) + "add_money";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("payment_name", payment);
                        params.put("CUST_ID", custId);
                        params.put("TXN_AMOUNT", amount);

                        Log.d(url, new JSONObject(params).toString());

                        final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        loadingDialog.dismiss();
                                        Log.d("cashfree add money ", response.toString());
                                        try {
                                            if (TextUtils.equals(response.getString("status"), "false")) {
                                                Toast.makeText(AddMoneyActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                            } else {
                                                JSONObject obj = new JSONObject(response.getString("message"));
                                                HashMap<String, String> params = new HashMap<String, String>();
                                                params.put("appId", cfAppid);
                                                params.put("orderId", obj.getString("order_id"));
                                                params.put("orderAmount", String.valueOf(amountFloat));
                                                params.put("customerPhone", user.getPhone());
                                                params.put("customerEmail", user.getEmail());
                                                CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
                                                cfPaymentService.setOrientation(0);
                                                //Log.d("abcdefghj",params.toString());
                                                if (TextUtils.equals(modeStatus, "Test")) {
                                                    cfPaymentService.doPayment(AddMoneyActivity.this, params, obj.getString("cftoken"), "TEST");
                                                } else {
                                                    cfPaymentService.doPayment(AddMoneyActivity.this, params, obj.getString("cftoken"), "PROD");
                                                }
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
                        request.setShouldCache(false);
                        mQueue.add(request);
                        //add_money for cashfree end

                    } else if (TextUtils.equals(payment, "Google Pay")) {

                        loadingDialog.show();
                        //add_money for google pay start
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = getResources().getString(R.string.api) + "add_money";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("payment_name", payment);
                        params.put("CUST_ID", custId);
                        params.put("TXN_AMOUNT", amount);

                        Log.d(url, new JSONObject(params).toString());

                        final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("addmoney", response.toString());
                                        loadingDialog.dismiss();
                                        try {

                                            if (TextUtils.equals(response.getString("status"), "false")) {
                                                Toast.makeText(AddMoneyActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                            } else {
                                                String gtid = response.getString("transection_no");
                                                String goid = response.getString("order_id");
                                                Uri uri =
                                                        new Uri.Builder()
                                                                .scheme("upi")
                                                                .authority("pay")
                                                                .appendQueryParameter("pa", upi_id)
                                                                .appendQueryParameter("pn", getResources().getString(R.string.app_name))
                                                                .appendQueryParameter("tn", "Transaction for " + gtid)
                                                                .appendQueryParameter("am", String.valueOf(amountFloat))
                                                                .appendQueryParameter("cu", "INR")
                                                                .build();
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(uri);
                                                intent.setPackage(GOOGLE_TEZ_PACKAGE_NAME);
                                                try {
                                                    SharedPreferences sharedPreferences = getSharedPreferences("gpay", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("amount", String.valueOf(amountFloat));
                                                    editor.putString("tid", gtid);
                                                    editor.putString("oid", goid);
                                                    editor.apply();

                                                    startActivityForResult(intent, TEZ_REQUEST_CODE);
                                                } catch (Exception e) {

                                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AddMoneyActivity.this);
                                                    builder1.setMessage(getResources().getString(R.string.google_pay_not_installed));
                                                    builder1.setTitle(getResources().getString(R.string.error));
                                                    builder1.setCancelable(false);
                                                    builder1.setPositiveButton(
                                                            getResources().getString(R.string.ok),
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {

                                                                }
                                                            }).create().show();
                                                }
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
                        request.setShouldCache(false);
                        mQueue.add(request);
                        //add_money for google pay end
                    }
                }

            }
        });
    }

    /*=======================================   Stripe Payment Flow Start ================================*/

    private void displayAlert(@NonNull String title,
                              @Nullable String message,
                              boolean restartDemo) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        if (restartDemo) {
            builder.setPositiveButton("Restart demo",
                    (DialogInterface dialog, int index) -> {
                        CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
                        cardInputWidget.clear();
//                        startCheckout();
                    });
        } else {
            builder.setPositiveButton("Ok", null);
        }
        builder.create().show();
    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }*/

    private void onPaymentSuccess(@NonNull final okhttp3.Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );

        Log.d(getClass().getName(), "FailureResponseFf==>" + responseMap.toString());
        // The response from the server includes the Stripe publishable key and
        // PaymentIntent details.
        // For added security, our sample app gets the publishable key from the server
        paymentIntentClientSecret = responseMap.get("clientSecret");

        stripePublishableKey = responseMap.get("publishableKey");

        // Configure the SDK with your Stripe publishable key so that it can make requests to the Stripe API
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull(stripePublishableKey)
        );

        CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
        if (params != null) {
            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
            stripe.confirmPayment(AddMoneyActivity.this, confirmParams);
        }
    }

    private static final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<AddMoneyActivity> activityRef;

        PayCallback(@NonNull AddMoneyActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final AddMoneyActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            Log.d(getClass().getName(), "FailureResponseF==>" + e.toString());
            activity.runOnUiThread(() ->

                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull final okhttp3.Response response)
                throws IOException {
            final AddMoneyActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            if (!response.isSuccessful()) {
                Log.d(getClass().getName(), "FailureResponseS==>" + response.toString());
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                Log.d(getClass().getName(), "FailureResponseSS==>" + response.toString());
                activity.onPaymentSuccess(response);
            }
        }
    }

    private class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<AddMoneyActivity> activityRef;

        PaymentResultCallback(@NonNull AddMoneyActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final AddMoneyActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
               /* activity.displayAlert(
                        "Payment completed",
                        gson.toJson(paymentIntent),
                        true
                );*/
                Log.d(getClass().getName(), "Success_Response ====>" + gson.toJson(paymentIntent));
                StripePaymentPay(paymentIntent);
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).message,
                        false
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final AddMoneyActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString(), false);
        }
    }

    public void StripePaymentPay(PaymentIntent s) {
        rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.getCache().clear();
        userLocalStore = new UserLocalStore(getApplicationContext());
        //  call paytm_response  api after paytm gateway response
        loadingDialog.show();
        String rurl = getResources().getString(R.string.api) + "stripe_response";

        HashMap<String, String> rparams = new HashMap<String, String>();
        rparams.put("member_id", custId);
        if (s.getStatus() == PaymentIntent.Status.Succeeded) {
            rparams.put("state", "approved");
        } else {
            rparams.put("state", "not_approved");
        }
        rparams.put("amount", amount);
        rparams.put("id", s.getCreated() + "");

        String finalTid = s.getId();
        finalTamount = amount;

        final JsonObjectRequest rrequest = new JsonObjectRequest(rurl, new JSONObject(rparams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            loadingDialog.dismiss();
                            if (TextUtils.equals(response.getString("status"), "true")) {
                                Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TID", finalTid);
                                intent.putExtra("TAMOUNT", finalTamount);
                                startActivity(intent);

                            } else {
                                Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TID", finalTid);
                                intent.putExtra("TAMOUNT", finalTamount);
                                startActivity(intent);
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
        rrequest.setShouldCache(false);
        rQueue.add(rrequest);
    }

    /*=======================================   Stripe Payment Flow End ================================*/

    public void PaytmPay(Map<String, String> paramMap) {
        PaytmPGService Service = null;

        // check test or production?
        if (TextUtils.equals(modeStatus, "Test")) {
            Service = PaytmPGService.getStagingService("");
            Log.d("status", "test");
        } else {
            Service = PaytmPGService.getProductionService();
            Log.d("status", "production");
        }
        Log.d("status", modeStatus);

        PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);
        Service.initialize(Order, null);
        Service.startPaymentTransaction(AddMoneyActivity.this, true, true, new PaytmPaymentTransactionCallback() {

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {
                Toast.makeText(getApplicationContext(), " UI Error Occur. ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTransactionResponse(Bundle inResponse) {
                Log.d("paytm main response", inResponse.toString() + "-----------------------------------------------------------------");

                rQueue = Volley.newRequestQueue(getApplicationContext());
                rQueue.getCache().clear();
                userLocalStore = new UserLocalStore(getApplicationContext());
                //  call paytm_response  api after paytm gateway response
                loadingDialog.show();
                String rurl = getResources().getString(R.string.api) + "paytm_response";

                HashMap<String, String> rparams = new HashMap<String, String>();
                rparams.put("order_id", inResponse.getString("ORDERID"));
                rparams.put("reason", inResponse.getString("RESPMSG"));
                rparams.put("amount", amount);
                rparams.put("banktransectionno", inResponse.getString("TXNID"));

                if (TextUtils.equals(inResponse.getString("STATUS"), "TXN_SUCCESS")) {
                    rparams.put("status", "1");
                } else {
                    rparams.put("status", "2");
                }
                final String finalTid = inResponse.getString("TXNID");
                finalTamount = inResponse.getString("TXNAMOUNT");

                final JsonObjectRequest rrequest = new JsonObjectRequest(rurl, new JSONObject(rparams),
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    loadingDialog.dismiss();
                                    if (TextUtils.equals(response.getString("status"), "true")) {
                                        Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                        intent.putExtra("selected", selectedCurrencySymbol);
                                        intent.putExtra("TID", finalTid);
                                        intent.putExtra("TAMOUNT", finalTamount);
                                        startActivity(intent);

                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                        intent.putExtra("selected", selectedCurrencySymbol);
                                        intent.putExtra("TID", finalTid);
                                        intent.putExtra("TAMOUNT", finalTamount);
                                        startActivity(intent);
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
                rrequest.setShouldCache(false);
                rQueue.add(rrequest);
            }

            @Override
            public void networkNotAvailable() {
                Log.d("LOG", "UI Error Occur.");
                Toast.makeText(getApplicationContext(), " UI Error Occur. ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {
                Log.d("LOG", "UI Error Occur.");
                Toast.makeText(getApplicationContext(), " Severside Error " + inErrorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode,
                                              String inErrorMessage, String inFailingUrl) {
                Log.d("LOG", inErrorMessage);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Log.d("LOG", "Back");
            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.payment_transaction_failed), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void PaypalPayment(String amount) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), selectedCurrency, getResources().getString(R.string.pay_for_) + getResources().getString(R.string.app_name) + " " + getResources().getString(R.string._wallet), PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(AddMoneyActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    public void performCharge(Charge charge) {
        //create a Charge object
        loadingDialog.show();

        PaystackSdk.chargeCard(AddMoneyActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(final Transaction transaction) {

                psQueue = Volley.newRequestQueue(getApplicationContext());
                psQueue.getCache().clear();
                String url = getResources().getString(R.string.api) + "paystack_response";
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("reference", transaction.getReference());
                params.put("amount", amount);

                Log.d("paystack resposne data", new JSONObject(params).toString());

                final JsonObjectRequest psrequest = new JsonObjectRequest(url, new JSONObject(params),
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                //Log.d("add_money",response.toString());
                                loadingDialog.dismiss();
                                try {
                                    if (TextUtils.equals(response.getString("status"), "true")) {
                                        Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                        intent.putExtra("selected", selectedCurrencySymbol);
                                        intent.putExtra("TID", transaction.getReference());
                                        intent.putExtra("TAMOUNT", String.valueOf((int) amountFloat));
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                        intent.putExtra("selected", selectedCurrencySymbol);
                                        intent.putExtra("TID", transaction.getReference());
                                        intent.putExtra("TAMOUNT", String.valueOf((int) amountFloat));
                                        startActivity(intent);
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
                psrequest.setShouldCache(false);
                psQueue.add(psrequest);

                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                Log.d("beforevalidate", transaction.getReference());
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                loadingDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                intent.putExtra("TID", transaction.getReference());
                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                startActivity(intent);
                //handle error here
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("paypal", "in activity result");

        if (requestCode == PAYPAL_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if ((confirmation != null)) {
                    JSONObject paymentdetails = confirmation.toJSONObject();
                    try {
                        final JSONObject responsedetails = (JSONObject) paymentdetails.get("response");

                        //  call paypal_response  api after paypal gateway response
                        loadingDialog.show();
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = getResources().getString(R.string.api) + "paypal_response";

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("member_id", custId);
                        params.put("id", responsedetails.getString("id"));
                        params.put("amount", amount);
                        params.put("state", responsedetails.getString("state"));
                        final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                        loadingDialog.dismiss();
                                        try {
                                            if (TextUtils.equals(response.getString("status"), "true")) {
                                                Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                                intent.putExtra("selected", selectedCurrencySymbol);
                                                intent.putExtra("TID", responsedetails.getString("id"));
                                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                                startActivity(intent);
                                            } else {
                                                Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                                intent.putExtra("selected", selectedCurrencySymbol);
                                                intent.putExtra("TID", responsedetails.getString("id"));
                                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                                startActivity(intent);
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
                        request.setShouldCache(false);
                        mQueue.add(request);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.cancel), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STRIPE_REQUEST_CODE) {
            // Handle the result of stripe.confirmPayment
            stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
        } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid), Toast.LENGTH_SHORT).show();
        } else if (requestCode == CFPaymentService.REQ_CODE) {
            //Log.d("cashfree", "ReqCode : " + CFPaymentService.REQ_CODE);
            // Log.d("cashfree", "API Response : ");
            //Prints all extras. Replace with app logic.
            loadingDialog.show();
            if (data != null) {
                Bundle bundle = data.getExtras();
                //Log.d("cashfree",bundle.toString());
                final JSONObject json = new JSONObject();
                if (bundle != null) {
                    for (String key : bundle.keySet()) {
                        if (bundle.getString(key) != null) {
                            Log.d("TAG", key + " : " + bundle.getString(key));
                            try {
                                json.put(key, JSONObject.wrap(bundle.get(key)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Log.d("final result cashfree", json.toString());

                    mQueue = Volley.newRequestQueue(getApplicationContext());
                    mQueue.getCache().clear();

                    String url = getResources().getString(R.string.api) + "cashfree_response";

                    final JsonObjectRequest request = new JsonObjectRequest(url, json,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {

                                    Log.d("cashfree_response", response.toString());

                                    loadingDialog.dismiss();
                                    try {
                                        if (TextUtils.equals(response.getString("status"), "true")) {
                                            Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                            intent.putExtra("selected", selectedCurrencySymbol);
                                            intent.putExtra("TID", json.getString("referenceId"));
                                            intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                            intent.putExtra("selected", selectedCurrencySymbol);
                                            try {
                                                intent.putExtra("TID", json.getString("referenceId"));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                            startActivity(intent);
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
                    request.setShouldCache(false);
                    mQueue.add(request);
                }
            }
        } else if (requestCode == TEZ_REQUEST_CODE) {
            // Process based on the data in response.
            if (resultCode == RESULT_OK) {
                Log.d("google pay result ok", data.toString() + "------" + data.getStringExtra("Status") + " " + data.getStringExtra("response"));
                Toast.makeText(getApplicationContext(), data.getStringExtra("Status"), Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences("gpay", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (TextUtils.equals(data.getStringExtra("Status"), "SUCCESS")) {
                    googlePayResponse("true", sharedPreferences.getString("amount", "0"), sharedPreferences.getString("tid", ""), sharedPreferences.getString("oid", ""));
                } else {
                    googlePayResponse("false", sharedPreferences.getString("amount", "0"), sharedPreferences.getString("tid", ""), sharedPreferences.getString("oid", ""));
                }

                editor.clear();
            } else {

                try {
                    Toast.makeText(getApplicationContext(), data.getStringExtra("Status"), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {

                }
            }
        }
    }

    private void initiateInstamojoSDKPayment(String orderID) {
        Instamojo.getInstance().initiatePayment(this, orderID, AddMoneyActivity.this);
    }

    @Override

    public void onInstamojoPaymentComplete(String orderID, String transactionID, String paymentID, String paymentStatus) {
        //instamojo
        Log.d("Instamojo success", "Payment complete. Order ID: " + orderID + ", Transaction ID: " + transactionID
                + ", Payment ID:" + paymentID + ", Status: " + paymentStatus);
        InstamojoResponse(paymentStatus, paymentID);
    }

    @Override
    public void onPaymentCancelled() {
        //instamojo
        Log.d("Instamojo cancel", "Payment cancelled");
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.payment_cancelled), Toast.LENGTH_SHORT).show();
        InstamojoResponse("cancel", "");
    }

    @Override
    public void onInitiatePaymentFailure(String errorMessage) {
        //instamojo
        Log.d("Instamojo fail", "Initiate payment failed " + errorMessage);
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void InstamojoResponse(String status, final String payid) {
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        loadingDialog.show();
        String url = getResources().getString(R.string.api) + "instamojo_response";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("status", status);
        params.put("amount", String.valueOf(amount));
        params.put("member_id", custId);
        params.put("order_id", depositId);
        params.put("payment_id", payid);

        Log.d(url, new JSONObject(params).toString());
        final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();
                        try {
                            if (TextUtils.equals(response.getString("status"), "true")) {
                                Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TID", payid);
                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TID", payid);
                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                startActivity(intent);
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
        request.setShouldCache(false);
        mQueue.add(request);
    }

    public void startRazorpayPayment(String key_id, String order_id, String currency, String amount) {
        checkout.setKeyID(key_id);
        checkout.setImage(R.mipmap.app_logo);
        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();
            options.put("name", getResources().getString(R.string.app_name));
            //options.put("description", "Reference No. #123456");
            //options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", order_id);
            options.put("currency", currency);
            options.put("amount", amount);

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("Razorpay", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        //Log.d("final razor pay"+s,paymentData.toString());
        //razorpay
        //Log.d(s,paymentData.getOrderId()+"------"+paymentData.getData()+"------"+paymentData.getPaymentId()+"------"+paymentData.getExternalWallet()+"------"+paymentData.getSignature());
        try {
            Checkout.clearUserData(getApplicationContext());
            RazorpayResponse("true", paymentData.getOrderId(), paymentData.getPaymentId(), paymentData.getSignature());
        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(),"Toast success",Toast.LENGTH_SHORT).show();
            Checkout.clearUserData(getApplicationContext());
            RazorpayResponse("true", paymentData.getOrderId(), paymentData.getPaymentId(), paymentData.getSignature());
        }


    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        //razorpay

        try {
            Checkout.clearUserData(getApplicationContext());
            Log.d(String.valueOf(i), s + "------" + paymentData.getData() + "------" + paymentData.getPaymentId() + "------" + paymentData.getSignature());
            if (TextUtils.equals(paymentData.getPaymentId(), null) && TextUtils.equals(paymentData.getPaymentId(), null)) {
                startActivity(new Intent(getApplicationContext(), TransactionFailActivity.class));
                loadingDialog.dismiss();
            } else {
                RazorpayResponse("false", paymentData.getOrderId(), paymentData.getPaymentId(), paymentData.getSignature());
            }
        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(),"Toast error",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), TransactionFailActivity.class));
            loadingDialog.dismiss();
        }
    }

    public void RazorpayResponse(String status, String orderid, final String payid, String signature) {
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        loadingDialog.show();
        String url = getResources().getString(R.string.api) + "razorpay_response";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("status", status);
        params.put("amount", amount);
        params.put("member_id", custId);
        params.put("razorpay_order_id", orderid);
        params.put("razorpay_payment_id", payid);
        params.put("razorpay_signature", signature);
        params.put("receipt", receipt);

        Log.d(url, new JSONObject(params).toString());
        final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        loadingDialog.dismiss();
                        try {
                            if (TextUtils.equals(response.getString("status"), "true")) {
                                Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TID", payid);
                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TID", payid);
                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                startActivity(intent);
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
        request.setShouldCache(false);
        mQueue.add(request);
    }

    public void googlePayResponse(String status, String amount, final String tid, String oid) {

        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        loadingDialog.show();
        String url = getResources().getString(R.string.api) + "googlepay_response";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("status", status);
        params.put("amount", amount);
        params.put("member_id", custId);
        params.put("transaction_id", tid);
        params.put("order_id", oid);


        Log.d(url, new JSONObject(params).toString());
        final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("googlepay resp", response.toString());
                        loadingDialog.dismiss();
                        try {
                            if (TextUtils.equals(response.getString("status"), "true")) {
                                Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TID", tid);
                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                intent.putExtra("TID", tid);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse response = error.networkResponse;
                String errorString = new String(response.data);

                Log.e("**VolleyError", "error" + errorString);
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

        addmoneyOption.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                rdbtn = new RadioButton(this);
                rdbtn.setId(i);
                rdbtn.setText(json.getString("payment_name"));
                if (i == 0) {
                    rdbtn.setChecked(true);

                    modeStatus = json.getString("payment_status");
                    payment = json.getString("payment_name");
                    selectedCurrency = json.getString("currency_code");
                    selectedCurrencySymbol = json.getString("currency_symbol");
                    point = json.getString("currency_point");
                    pointInt = Integer.parseInt(point);

                    parentaddmoney.setHint(getString(R.string.amount_bracket));

                    if (TextUtils.equals(rdbtn.getText().toString(), "PayStack")) {
                        paystackll.setVisibility(View.VISIBLE);
                        if (TextUtils.equals(json.getString("payment_status"), "Test")) {
                            paystacktestnote.setVisibility(View.VISIBLE);
                        } else {
                            paystacktestnote.setVisibility(View.GONE);
                        }
                    } else {
                        paystackll.setVisibility(View.GONE);
                        paystacktestnote.setVisibility(View.GONE);
                    }
                }

                if (TextUtils.equals(json.getString("payment_name"), "PayPal")) {
                    if (TextUtils.equals(json.getString("payment_status"), "Sandbox")) {
                        Log.d("paypall", "sandbox" + json.getString("client_id"));
                        config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                                .clientId(json.getString("client_id"));
                        Intent intent = new Intent(AddMoneyActivity.this, PayPalService.class);
                        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                        startService(intent);
                    } else {
                        // Log.d("paypall", "production");
                        config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                                .clientId(json.getString("client_id"));
                        Intent intent = new Intent(AddMoneyActivity.this, PayPalService.class);
                        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                        startService(intent);
                    }
                } else if (TextUtils.equals(json.getString("payment_name"), "PayStack")) {
                    if (TextUtils.equals(json.getString("payment_status"), "Test")) {
                        // paystacktestnote.setVisibility(View.VISIBLE);
                    }

                    PaystackSdk.initialize(getApplicationContext());
                    PaystackSdk.setPublicKey(json.getString("public_key"));
                    secretKey = json.getString("secret_key");

                } else if (TextUtils.equals(json.getString("payment_name"), "Instamojo")) {
                    if (TextUtils.equals(json.getString("payment_status"), "Test")) {
                        Instamojo.getInstance().initialize(AddMoneyActivity.this, Instamojo.Environment.TEST);
                    } else {
                        Instamojo.getInstance().initialize(AddMoneyActivity.this, Instamojo.Environment.PRODUCTION);
                    }
                } else if (TextUtils.equals(json.getString("payment_name"), "Razorpay")) {

                    checkout = new Checkout();
                    Checkout.preload(getApplicationContext());

                } else if (TextUtils.equals(json.getString("payment_name"), "Cashfree")) {
                    cfAppid = json.getString("app_id");
                } else if (TextUtils.equals(json.getString("payment_name"), "Google Pay")) {
                    upi_id = json.getString("upi_id");
                } else if (TextUtils.equals(json.getString("payment_name"), "Offline")) {
                    paymentDescrption = json.getString("payment_description");
                } else if (TextUtils.equals(json.getString("payment_name"), "Stripe")) {
                    paymentDescrption = json.getString("payment_status");
                    stripePublishableKey = json.getString("publishable_key");
                }
                addmoneyOption.addView(rdbtn);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

       /* rdbtn = new RadioButton(this);
        rdbtn.setId(array.length());
        rdbtn.setText("Stripe");
        addmoneyOption.addView(rdbtn);*/
    }

    public static String stripZeros(String number) {
        return new BigDecimal(number).stripTrailingZeros().toPlainString();
    }
}
