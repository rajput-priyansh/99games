//For withdraw money from app wallet
package com.egamez.org.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.egamez.org.R;
import com.egamez.org.models.CurrentUser;
import com.egamez.org.utils.CustomTypefaceSpan;
import com.egamez.org.utils.LoadingDialog;
import com.egamez.org.utils.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WithdrawMoneyActivity extends AppCompatActivity {

    ImageView back;
    String amount;
    int amountInt = 0;
    RequestQueue mQueue;
    LoadingDialog loadingDialog;
    TextView withdrawTitle;
    RadioGroup withdrawOption;
    RadioButton paytm;
    RadioButton phonepe;
    RadioButton googlepay;
    String withdrawMethod = "";
    RadioButton rdbtn;
    JSONArray array;
    EditText paytm_number;
    EditText withdraw_amount;
    String selectedField = "";
    TextInputLayout til;
    TextView withdrawNote;
    String point = "";
    int pointInt = 0;
    String selectedCurrencySymbol = "";
    String minWithdraw="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_money);

        back = (ImageView) findViewById(R.id.backfromwithdrawmoney);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyWalletActivity.class);
                intent.putExtra("N","2");
                startActivity(intent);
            }
        });

        til = (TextInputLayout) findViewById(R.id.numbertil);

        loadingDialog = new LoadingDialog(WithdrawMoneyActivity.this);
        loadingDialog.show();
        paytm_number = (EditText) findViewById(R.id.paytm_number);
        withdraw_amount = (EditText) findViewById(R.id.withdraw_amount);
        final Button withdraw_btn = (Button) findViewById(R.id.withdraw_btn);
        withdrawTitle = (TextView) findViewById(R.id.withdrawtitle);
        withdrawOption = (RadioGroup) findViewById(R.id.withdraw_option);
        withdrawNote = (TextView) findViewById(R.id.withdraw_note);

        mQueue = Volley.newRequestQueue(WithdrawMoneyActivity.this);
        mQueue.getCache().clear();

        final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
        String url = getResources().getString(R.string.api) + "withdraw_method";

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();
                        try {
                            JSONArray arr = response.getJSONArray("withdraw_method");
                            minWithdraw=response.getString("min_withdrawal");

                            array = arr;
                            if (!TextUtils.equals(response.getString("withdraw_method"), "[]")) {
                                JSON_PARSE_DATA_AFTER_WEBCALL(arr);
                            } else {
                                Toast.makeText(WithdrawMoneyActivity.this, getResources().getString(R.string.withdraw_method_not_available), Toast.LENGTH_SHORT).show();
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


        withdrawOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                rdbtn = (RadioButton) findViewById(checkedId);
                try {
                    withdrawMethod = rdbtn.getText().toString();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = null;
                        try {
                            json = array.getJSONObject(i);

                            if (TextUtils.equals(json.getString("withdraw_method"), withdrawMethod)) {
                                paytm_number.setText("");
                                selectedCurrencySymbol=json.getString("currency_symbol");
                                point=json.getString("withdraw_method_currency_point");
                                pointInt=Integer.parseInt(point);
                                if (withdraw_amount.getText().toString().trim().length() > 0) {

                                    withdrawNote.setText(TextUtils.concat(getResources().getString(R.string.you_will_get_)," ",selectedCurrencySymbol,String.format("%.2f",Double.parseDouble(withdraw_amount.getText().toString().trim())/(double) pointInt)));
                                    if (TextUtils.equals(selectedCurrencySymbol, "₹")) {
                                        Typeface font = Typeface.DEFAULT;
                                        SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.Rs));
                                        SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                        withdrawNote.setText(TextUtils.concat(Html.fromHtml(getString(R.string.you_will_get_))," ", SS, Html.fromHtml(String.format("%.2f",Double.parseDouble(withdraw_amount.getText().toString().trim())/(double)pointInt))));
                                    }

                                } else {
                                    withdrawNote.setText("");
                                }


                                selectedField = json.getString("withdraw_method_field");

                                if (TextUtils.equals(json.getString("withdraw_method_field"), "mobile no")) {
                                    til.setHint(getResources().getString(R.string.mobile_number));
                                    paytm_number.setInputType(InputType.TYPE_CLASS_PHONE);
                                } else if (TextUtils.equals(json.getString("withdraw_method_field"), "email")) {
                                    til.setHint(getResources().getString(R.string.email));
                                    paytm_number.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                } else if (TextUtils.equals(json.getString("withdraw_method_field"), "UPI ID")) {
                                    til.setHint(getResources().getString(R.string.upi_id));
                                    paytm_number.setInputType(InputType.TYPE_CLASS_TEXT);
                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                withdrawTitle.setText(getResources().getString(R.string.withdraw_to_) + " "+withdrawMethod);
            }
        });

        withdraw_btn.setEnabled(false);
        withdraw_btn.setBackgroundColor(getResources().getColor(R.color.newdisablegreen));
        paytm_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                if (TextUtils.equals(selectedField, "mobile no")) {
                    if (charSequence.length() < 7 || charSequence.length() > 15) {
                        withdraw_btn.setEnabled(false);
                        withdraw_btn.setBackgroundColor(getResources().getColor(R.color.newdisablegreen));
                    } else {
                        if (!TextUtils.isEmpty(withdraw_amount.getText().toString())) {
                            withdraw_btn.setEnabled(true);
                            withdraw_btn.setBackgroundColor(getResources().getColor(R.color.newgreen));
                        }
                    }
                } else {
                    withdraw_btn.setEnabled(true);
                    withdraw_btn.setBackgroundColor(getResources().getColor(R.color.newgreen));
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        withdraw_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    if (!TextUtils.isEmpty(paytm_number.getText().toString()))
                        withdraw_btn.setEnabled(true);
                    withdraw_btn.setBackgroundColor(getResources().getColor(R.color.newgreen));

                    withdrawNote.setText(TextUtils.concat(getResources().getString(R.string.you_will_get_)," ",selectedCurrencySymbol,String.format("%.2f",Double.parseDouble(String.valueOf(charSequence))/(double)pointInt)));
                    if (TextUtils.equals(selectedCurrencySymbol, "₹")) {
                        Typeface font = Typeface.DEFAULT;
                        SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.Rs));
                        SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        withdrawNote.setText(TextUtils.concat(Html.fromHtml(getString(R.string.you_will_get_))," ", SS, Html.fromHtml(String.format("%.2f",Double.parseDouble(String.valueOf(charSequence))/(double) pointInt))));
                    }

                } else {
                    withdrawNote.setText("");
                    withdraw_btn.setEnabled(false);
                    withdraw_btn.setBackgroundColor(getResources().getColor(R.color.newdisablegreen));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        withdraw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String paytmnumber = paytm_number.getText().toString().trim();

                amount = withdraw_amount.getText().toString().trim();
                try {
                    amountInt = Integer.parseInt(amount);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (TextUtils.equals(selectedField, "mobile no")) {
                    if (TextUtils.equals(paytmnumber, "")) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_enter_mobile_number), Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (TextUtils.equals(selectedField, "email")) {
                    if (TextUtils.equals(paytmnumber, "")) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(paytmnumber).matches()) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.wrong_email_address___), Toast.LENGTH_SHORT).show();
                        return;
                    }

                } else if (TextUtils.equals(selectedField, "UPI ID")) {
                    if (TextUtils.equals(paytmnumber, "")) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_enter_upi_id), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                int minWithdrawInt=Integer.parseInt(minWithdraw);
                if (amountInt < minWithdrawInt) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawMoneyActivity.this);
                    builder.setTitle(getString(R.string.error));
                    SpannableStringBuilder builderp = new SpannableStringBuilder();
                    builderp.append(Html.fromHtml(getString(R.string.enter_minmum)+" "))
                            .append(" ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin,ImageSpan.ALIGN_BASELINE), 0)
                            .append(" ")
                            .append(Html.fromHtml(minWithdraw)+".");
                    builder.setMessage( builderp);
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                    builder.create();
                    return;
                }

                /*withdraw api call start*/
                loadingDialog.show();
                mQueue = Volley.newRequestQueue(WithdrawMoneyActivity.this);
                mQueue.getCache().clear();

                final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                final CurrentUser user = userLocalStore.getLoggedInUser();
                String url = getResources().getString(R.string.api) + "withdraw";

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("submit", "withdraw");
                hashMap.put("amount", withdraw_amount.getText().toString().trim());
                hashMap.put("pyatmnumber", paytm_number.getText().toString().trim());
                hashMap.put("withdraw_method", withdrawMethod);
                hashMap.put("member_id", user.getMemberid());

                final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(hashMap),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                loadingDialog.dismiss();
                                try {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawMoneyActivity.this);
                                    if (TextUtils.equals(response.getString("status"), "true")) {
                                        builder.setTitle(getResources().getString(R.string.success));
                                    } else {
                                        builder.setTitle(getResources().getString(R.string.error));
                                    }
                                    builder.setMessage(response.getString("message"));
                                    builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    });
                                    builder.show();
                                    builder.create();
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
                request.setShouldCache(false);
                mQueue.add(request);
                /*withdraw api call end*/

                paytm_number.setText("");
                withdraw_amount.setText("");
                withdraw_btn.setEnabled(false);
            }
        });
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        withdrawOption.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                rdbtn = new RadioButton(this);
                rdbtn.setId(i);
                rdbtn.setText(json.getString("withdraw_method"));
                if (i == 0) {
                    selectedCurrencySymbol=json.getString("currency_symbol");
                    point=json.getString("withdraw_method_currency_point");
                    pointInt=Integer.parseInt(point);
                    rdbtn.setChecked(true);
                    withdrawMethod = rdbtn.getText().toString();
                    withdrawTitle.setText(getResources().getString(R.string.withdraw_to_) + " "+withdrawMethod);
                    selectedField = json.getString("withdraw_method_field");
                    if (TextUtils.equals(json.getString("withdraw_method_field"), "mobile no")) {
                        til.setHint(getResources().getString(R.string.mobile_number));
                        paytm_number.setInputType(InputType.TYPE_CLASS_PHONE);
                    } else if (TextUtils.equals(json.getString("withdraw_method_field"), "email")) {
                        til.setHint(getResources().getString(R.string.email));
                        paytm_number.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    } else if (TextUtils.equals(json.getString("withdraw_method_field"), "UPI ID")) {
                        til.setHint(getResources().getString(R.string.upi_id));
                        paytm_number.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                }
                withdrawOption.addView(rdbtn);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
