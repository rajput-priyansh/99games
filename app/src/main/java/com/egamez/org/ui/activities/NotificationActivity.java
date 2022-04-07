//For showing selected game's tournament or matchall_play_match
package com.egamez.org.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egamez.org.R;
import com.egamez.org.models.FriendRequest;
import com.egamez.org.ui.adapters.FriendReqAdapter;
import com.egamez.org.ui.adapters.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView notificationRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private NotificationAdapter notificationAdapter;
    private List<String> notificationList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        notificationList =getData();
    initView();
    }

    private void initView() {
        notificationRecyclerView = (RecyclerView) findViewById(R.id.notification_recycler);
        notificationRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        notificationRecyclerView.setLayoutManager(layoutManager);
        notificationAdapter = new NotificationAdapter(this,notificationList);
        notificationRecyclerView.setAdapter(notificationAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((NotificationAdapter) notificationAdapter).setOnItemClickListener(new NotificationAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private List<String> getData(){
        List<String> notificationList = new ArrayList<>();
        notificationList.add("Kartik has invited You to Join Contest IN BGMI at 9PM.");
        notificationList.add("Kartik has invited You to Join Contest IN Carrom Pool at 10PM.");
        notificationList.add("Mayank has invited You to Join Contest IN BGMI at 9PM.");
        notificationList.add("Anup has invited You to Join Contest IN Carrom at 10PM.");
        return  notificationList;
    }
}
