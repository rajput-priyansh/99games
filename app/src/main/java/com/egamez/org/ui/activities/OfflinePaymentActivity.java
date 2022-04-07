//for offline payment
package com.egamez.org.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.egamez.org.R;
import com.egamez.org.utils.URLImageParser;

public class OfflinePaymentActivity extends AppCompatActivity {

    ImageView back;
    TextView paymentDesc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_payment);

        back = (ImageView) findViewById(R.id.backfromoffline);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddMoneyActivity.class);
                startActivity(intent);
            }
        });

        Intent intent=getIntent();
        String payDesc=intent.getStringExtra("paymentdesc");
        paymentDesc=(TextView)findViewById(R.id.payment_desc);
        Log.d("paydesc",payDesc);

        paymentDesc.setText(Html.fromHtml(payDesc,new URLImageParser(paymentDesc,this),null));
    }
}
