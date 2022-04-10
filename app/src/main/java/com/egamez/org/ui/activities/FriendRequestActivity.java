//For showing selected game's tournament or matchall_play_match
package com.egamez.org.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egamez.org.R;
import com.egamez.org.models.FriendRequest;
import com.egamez.org.models.GameData;
import com.egamez.org.ui.adapters.AllGamesAdapter;
import com.egamez.org.ui.adapters.FriendReqAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FriendRequestActivity extends AppCompatActivity {

    private RecyclerView friendsRecyclerView;
    private SearchView searchView;
    private RecyclerView.LayoutManager layoutManager;
    private FriendReqAdapter friendReqAdapter;
    private List<FriendRequest> friendList = new ArrayList<>();
    private ImageView backImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);
       friendList =getData();
    initView();
    }

    private void initView() {
        searchView= (SearchView) findViewById(R.id.search_view);
        friendsRecyclerView = (RecyclerView) findViewById(R.id.friends_recycler);
        backImage=(ImageView) findViewById(R.id.backfromselectedgame);
        friendsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        friendsRecyclerView.setLayoutManager(layoutManager);
        friendReqAdapter = new FriendReqAdapter(this,friendList);
        friendsRecyclerView.setAdapter(friendReqAdapter);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()) {
                    List<FriendRequest> filterList = new ArrayList<>();
                    for (FriendRequest friendRequest : friendList) {
                        if (friendRequest.getName().toLowerCase().contains(newText.toLowerCase())) {
                            filterList.add(friendRequest);
                        }
                    }
                    if (!filterList.isEmpty()) {
                        friendReqAdapter.filterList(filterList);
                    }
                }else{
                    friendReqAdapter.filterList(friendList);
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((FriendReqAdapter) friendReqAdapter).setOnItemClickListener(new FriendReqAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private List<FriendRequest> getData(){
        List<FriendRequest> friendRequestList = new ArrayList<>();
        friendRequestList.add(new FriendRequest("Krishan","9999999999"));
        friendRequestList.add(new FriendRequest("Kartik","9999999998"));
        friendRequestList.add(new FriendRequest("Tarun","9999999997"));
        friendRequestList.add(new FriendRequest("Anup","9999999996"));
        friendRequestList.add(new FriendRequest("Arpit","9999999995"));
        friendRequestList.add(new FriendRequest("Mayank","9999999994"));
        friendRequestList.add(new FriendRequest("Alok","9999999993"));
        friendRequestList.add(new FriendRequest("Tushar","9999999992"));
        friendRequestList.add(new FriendRequest("Vivek","9999999991"));
        friendRequestList.add(new FriendRequest("Pramod","9999999990"));
        return  friendRequestList;
    }
}
