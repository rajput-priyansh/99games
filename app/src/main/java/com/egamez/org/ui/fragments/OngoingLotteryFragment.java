package com.egamez.org.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.egamez.org.R;
import com.egamez.org.models.CurrentUser;
import com.egamez.org.models.LotteryData;
import com.egamez.org.ui.adapters.LotteryAdapter;
import com.egamez.org.utils.LoadingDialog;
import com.egamez.org.utils.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OngoingLotteryFragment extends Fragment {


    RecyclerView recyclerView;
    LotteryAdapter myAdapter;
    List<LotteryData> mData;
    RequestQueue mQueue;
    LoadingDialog loadingDialog;
    SwipeRefreshLayout pullToRefresh;
    UserLocalStore userLocalStore;
    CurrentUser user;
    TextView noOngoing;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_ongoing_lottery,container,false);

        pullToRefresh = (SwipeRefreshLayout) view.findViewById(R.id.pullToRefreshinongoinglottery);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                refresh();
                pullToRefresh.setRefreshing(false);
            }
        });

        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.show();

        noOngoing=(TextView)view.findViewById(R.id.noongoinglottery);
        recyclerView = (RecyclerView) view.findViewById(R.id.rvinongoinglottery);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(false);
        layoutManager.setReverseLayout(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mData = new ArrayList<>();

        userLocalStore=new UserLocalStore(getContext());
        user=userLocalStore.getLoggedInUser();

        mQueue = Volley.newRequestQueue(getContext());
        mQueue.getCache().clear();

        // for top player
        String url = getResources().getString(R.string.api) + "lottery/"+user.getMemberid()+"/ongoing";

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingDialog.dismiss();

                Log.d("response",response.toString());
                JSONObject player = null;
                try {
                    JSONArray arr = response.getJSONArray("ongoing");

                    if (!TextUtils.equals(response.getString("ongoing"), "[]")) {
                        noOngoing.setVisibility(View.GONE);
                    } else {
                        noOngoing.setVisibility(View.VISIBLE);
                    }
                    JSON_PARSE_DATA_AFTER_WEBCALL(arr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", "error" + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                UserLocalStore userLocalStore = new UserLocalStore(getContext());
                CurrentUser user = userLocalStore.getLoggedInUser();
                String credentials = user.getUsername() + ":" + user.getPassword();
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                String token="Bearer "+user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);

        return view;
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                LotteryData data = new LotteryData(json.getString("lottery_id"), json.getString("lottery_title"), json.getString("lottery_image"), json.getString("lottery_time"), json.getString("lottery_rules"),json.getString("lottery_fees"),json.getString("lottery_prize"),json.getString("lottery_size"),json.getString("total_joined"),json.getString("join_status"),json.getString("won_by"),json.getString("join_member"));
                mData.add(data);
                myAdapter = new LotteryAdapter(getActivity(), mData);
                myAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(myAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void refresh() {
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}
