//For after successfully join match or tournament
package com.egamez.org.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.egamez.org.R;

public class SuccessJoinActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_join);

        TextView nameid = (TextView) findViewById(R.id.successjoinmatchnameid);
        Button home = (Button) findViewById(R.id.joinsuccesshome);

        Intent intent = getIntent();
        String matchid = intent.getStringExtra("MATCH_ID");
        String matchname = intent.getStringExtra("MATCH_NAME");

        nameid.setText(matchname + " - "+getResources().getString(R.string.match)+" #" + matchid);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SelectedGameActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), SelectedGameActivity.class));
    }
}
