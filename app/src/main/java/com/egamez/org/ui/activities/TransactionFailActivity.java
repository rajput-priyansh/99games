// For after failed transaction
package com.egamez.org.ui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.egamez.org.R;
import com.egamez.org.utils.CustomTypefaceSpan;

public class TransactionFailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_fail);

        Button home = (Button) findViewById(R.id.failthome);
        TextView failtid = (TextView) findViewById(R.id.failtid);
        TextView failtamount = (TextView) findViewById(R.id.failtamount);

        Intent intent = getIntent();
        String tid = intent.getStringExtra("TID");
        String tamount = intent.getStringExtra("TAMOUNT");
        String selectedcurrency=intent.getStringExtra("selected");

        failtid.setText(getResources().getString(R.string.transaction_id)+" : #" + tid);
        failtamount.setText(getResources().getString(R.string.transaction_amount_is)+" : " + selectedcurrency + tamount);
        if (TextUtils.equals(selectedcurrency, "₹")) {
            Typeface font = Typeface.DEFAULT_BOLD;
            SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.Rs));
            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            failtamount.setText(TextUtils.concat(getResources().getString(R.string.transaction_amount_is)+" : ", SS, tamount));
        }
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyWalletActivity.class));
            }
        });
    }
}
