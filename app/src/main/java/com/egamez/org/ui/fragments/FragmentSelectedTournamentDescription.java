//For show description in selected tournament or match in tab layout
package com.egamez.org.ui.fragments;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.egamez.org.R;
import com.egamez.org.models.CurrentUser;
import com.egamez.org.utils.LoadingDialog;
import com.egamez.org.utils.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FragmentSelectedTournamentDescription extends Fragment {

    TextView matchTitleAndNumber;
    TextView team;
    TextView entryFee;
    TextView mode;
    TextView matchType;
    TextView map;
    TextView matchSchedule;
    TextView winningPrize;
    TextView perKill;
    TextView about;
    TextView sponser;
    CardView rData;
    LinearLayout roomDetail;
    TextView roomId;
    TextView roomPass;
    Button playNow;
    LinearLayout joinedll;
    LinearLayout sponsorll;
    String mIds,matchNames,matchTimes,winPrizes,perKills,entryFees,type,version,maps,matchTypes,matchDescs,noOfPlayers,numberOfPosition,memberId,matchUrl,roomIds,roomPassword,matchSponsers;
    String join_status=null;
    String packagename="";
    RequestQueue mQueue;
    LoadingDialog loadingDialog;
    SwipeRefreshLayout pullToRefresh;
    String from="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_selectedtournament_description, container, false);

        loadingDialog=new LoadingDialog(getActivity());
        Intent intent = getActivity().getIntent();
        from = intent.getStringExtra("FROM");
        final String mid=intent.getStringExtra("M_ID");

        pullToRefresh=(SwipeRefreshLayout)root.findViewById(R.id.pullToRefreshselecttournament);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                refresh();
                pullToRefresh.setRefreshing(false);
            }
        });
        matchTitleAndNumber=(TextView)root.findViewById(R.id.matchtitleandnumber);
        team=(TextView)root.findViewById(R.id.team);
        entryFee=(TextView)root.findViewById(R.id.entryfee);
        mode=(TextView)root.findViewById(R.id.mode);
        matchType=(TextView)root.findViewById(R.id.matchtype);
        map=(TextView)root.findViewById(R.id.map);
        matchSchedule=(TextView)root.findViewById(R.id.matchschedule);
        winningPrize=(TextView)root.findViewById(R.id.winningprize);
        perKill=(TextView)root.findViewById(R.id.perkill);
        about=(TextView)root.findViewById(R.id.about);
        sponser=(TextView)root.findViewById(R.id.sponsor);
        rData=(CardView) root.findViewById(R.id.registereddata);
        roomDetail=(LinearLayout) root.findViewById(R.id.roomdetailll);
        roomId=(TextView)root.findViewById(R.id.roomid);
        roomPass=(TextView)root.findViewById(R.id.roompass);
        playNow=(Button)root.findViewById(R.id.playnow);
        joinedll=(LinearLayout)root.findViewById(R.id.joinedll);
        sponsorll=(LinearLayout)root.findViewById(R.id.sponsorll);
        rData.setVisibility(View.GONE);

        //single match api call for description of that match
        mQueue = Volley.newRequestQueue(getActivity());
        mQueue.getCache().clear();
        final UserLocalStore userLocalStore = new UserLocalStore(getActivity());
        final CurrentUser user = userLocalStore.getLoggedInUser();

        String url = getResources().getString(R.string.api)+"single_match/"+mid+"/"+user.getMemberid();

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject obj=response.getJSONObject("match");

                            mIds=obj.getString("m_id");
                            matchNames=obj.getString("match_name");
                            matchTimes=obj.getString("match_time");
                            winPrizes=obj.getString("win_prize");
                            perKills=obj.getString("per_kill");
                            entryFees=obj.getString("entry_fee");
                            type=obj.getString("type");
                            version=obj.getString("version");
                            maps=obj.getString("MAP");
                            matchTypes=obj.getString("match_type");
                            matchDescs=obj.getString("match_desc");
                            noOfPlayers=obj.getString("no_of_player");
                            numberOfPosition=obj.getString("number_of_position");
                            memberId=obj.getString("member_id");
                            matchUrl=obj.getString("match_url");
                            roomIds=obj.getString("room_id");
                            roomPassword=obj.getString("room_password");
                            join_status=obj.getString("join_status");
                            matchSponsers=obj.getString("match_sponsor");

                            packagename=obj.getString("package_name");
                            matchTitleAndNumber.setText(matchNames+" - Match #"+mIds);
                            team.setText(" "+type);
                            entryFee.setText(entryFees);

                            mode.setText(" "+version);
                            if (matchTypes.matches("1")){
                                matchType.setText(" "+getActivity().getResources().getString(R.string.PAID));
                            }else {
                                matchType.setText(" "+getActivity().getResources().getString(R.string.FREE));
                            }
                            map.setText(" "+maps);
                            matchSchedule.setText(" "+matchTimes);
                            winningPrize.setText(winPrizes);

                            perKill.setText(perKills);

                            about.setText(Html.fromHtml(matchDescs));
                            about.setMovementMethod(LinkMovementMethod.getInstance());

                            if(TextUtils.equals(matchSponsers,"")){
                                sponsorll.setVisibility(View.GONE);
                            }else {
                                sponsorll.setVisibility(View.VISIBLE);
                                sponser.setText(Html.fromHtml(matchSponsers));
                                sponser.setMovementMethod(LinkMovementMethod.getInstance());

                            }

                            //if room id and password updated from admin side and you joined that match then display id and pass
                            roomDetail.setVisibility(View.GONE);
                            if(TextUtils.equals(roomIds,"") || TextUtils.equals(roomPassword,"") || !TextUtils.equals(join_status,"true")){
                                roomDetail.setVisibility(View.GONE);
                            }else {
                                roomDetail.setVisibility(View.VISIBLE);
                                byte[] dataid = Base64.decode(roomIds.substring(1,roomIds.length()-1), Base64.DEFAULT);
                                roomIds= new String(dataid, "UTF-8");
                                roomId.setText(roomIds);
                                byte[] datapass = Base64.decode(roomPassword.substring(1,roomPassword.length()-1), Base64.DEFAULT);
                                roomPassword= new String(datapass, "UTF-8");
                                roomPass.setText(roomPassword);
                                roomId.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText(getActivity().getResources().getString(R.string.room_id), roomIds);
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.room_id_copied_successfully), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                roomPass.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText(getActivity().getResources().getString(R.string.room_password), roomPassword);
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.room_password_copied_successfully), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                // for open game
                                playNow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        openApplication(getActivity(),packagename);
                                    }
                                });
                            }
                            JSONArray joinarr = response.getJSONArray("join_position");

                            // after join you will see room detail
                            if(join_status.matches("true")==true){
                                rData.setVisibility(View.VISIBLE);
                                JSON_PARSE_DATA_AFTER_WEBCALL_JOIN(joinarr);
                            }else {

                            }
                        } catch (JSONException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("**VolleyError", "error" + error.getMessage());
            }
        }){
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
                String token="Bearer "+user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);

        return root;
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL_JOIN(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                View view = getLayoutInflater().inflate(R.layout.joined_member_data, null);
                final TextView r_team=(TextView)view.findViewById(R.id.registeredteam);
                final TextView r_position=(TextView)view.findViewById(R.id.registeredposition);
                final TextView r_pubgname=(TextView)view.findViewById(R.id.registeredpubgname);

                if (TextUtils.equals(from,"LIVE")){
                    r_pubgname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    r_pubgname.setClickable(false);
                    r_pubgname.setEnabled(false);
                }

                r_team.setText(getActivity().getResources().getString(R.string.team_)+json.getString("team"));
                r_position.setText(json.getString("position"));
                r_pubgname.setText(json.getString("pubg_id"));

                final JSONObject finalJson = json;
                r_pubgname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        final Dialog builder =new Dialog(getActivity());

                        builder.setContentView(R.layout.add_player_details_data);
                        TextView title=builder.findViewById(R.id.main_title);
                        final EditText newplayername=builder.findViewById(R.id.newplayername);

                        LinearLayout add_player_ll=builder.findViewById(R.id.add_player_ll);
                        Button newok=builder.findViewById(R.id.newok);
                        Button newcancel=(Button)builder.findViewById(R.id.newcancel);

                        title.setText(getActivity().getResources().getString(R.string.edit_playername));

                        newok.setText(getActivity().getResources().getString(R.string.save));
                        add_player_ll.setVisibility(View.GONE);


                        newplayername.setText(r_pubgname.getText().toString());

                        newok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(TextUtils.isEmpty(newplayername.getText().toString().trim())){
                                    newplayername.setError(getActivity().getResources().getString(R.string.please_enter_player_name));
                                    return;
                                }

                                RequestQueue jQueue = Volley.newRequestQueue(getContext());
                                jQueue.getCache().clear();

                                String jurl = getResources().getString(R.string.api)+"change_player_name";


                                final UserLocalStore userLocalStore = new UserLocalStore(getActivity());
                                CurrentUser user = userLocalStore.getLoggedInUser();


                                HashMap<String, String> params = new HashMap<String, String>();


                                params.put("member_id", user.getMemberid());
                                params.put("match_id", mIds);
                                try {
                                    params.put("match_join_member_id", finalJson.getString("match_join_member_id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                params.put("pubg_id", newplayername.getText().toString());
                              /*  params.put("team", r_team.getText().toString().trim());
                                params.put("position", r_position.getText().toString().trim());*/




                                Log.d(jurl,new JSONObject(params).toString());

                                final JsonObjectRequest jrequest = new JsonObjectRequest(jurl, new JSONObject(params),
                                        new Response.Listener<JSONObject>() {

                                            @Override
                                            public void onResponse(JSONObject response) {

                                                Log.d("Edit name ",response.toString());
                                                loadingDialog.dismiss();


                                                try {

                                                    if(response.getString("status").matches("true")){
                                                        builder.dismiss();
                                                        getActivity().finish();
                                                        getActivity().overridePendingTransition( 0, 0);
                                                        startActivity(getActivity().getIntent());
                                                        getActivity().overridePendingTransition( 0, 0);


                                                    }else {
                                                        Toast.makeText(getActivity(), Html.fromHtml(response.getString("message")), Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                                //Log.d("Volley",response.toString());
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Log.e("error " + String.valueOf(error.getNetworkTimeMs()), String.valueOf(error));



                                    }
                                }){
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
                                        String token="Bearer "+user.getToken();
                                        headers.put("Content-Type", "application/json");
                                        headers.put("Authorization", token);
                                        return headers;
                                    }
                                };

                                jrequest.setShouldCache(false);
                                jrequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 0));
                                jQueue.add(jrequest);
                            }
                        });
                        newcancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                builder.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
                joinedll.addView(view);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void openApplication(Context context, String packageN) {
        Intent i = context.getPackageManager().getLaunchIntentForPackage(packageN);

        if (i != null) {
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
        } else {
            try {
                Intent intent =new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageN));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            catch (android.content.ActivityNotFoundException anfe) {
                Intent intent =new Intent(Intent.ACTION_VIEW,Uri.parse("Uri.parse(\"http://play.google.com/store/apps/details?id=\"" + packageN));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

    public void refresh(){
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}
