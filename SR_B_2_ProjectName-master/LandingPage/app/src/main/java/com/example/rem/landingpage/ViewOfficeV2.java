package com.example.rem.landingpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewOfficeV2 extends AppCompatActivity {
    /**
     * used to get keys for the currently logged in user and the office bieng viewed
     */
    private Bundle extras;
    /**
     * the key of the person currently logged in. In order to stay logged in while using the app,
     * this key needs to be passed to every activity, even if it's not being used
     */
    private String CUR_USER_KEY;
    /**
     * the key of the office displayed
     */
    private String OFFICE_VIEW_KEY;
    /**
     * A TextView used to display the name of the office.
     */
    private TextView officeName;
    /**
     * A TextView used to display the description/summary of the office.
     */
    private TextView offDesc;
    /**
     * A TextView used to display the address of the office.
     */
    private TextView offAdd;
    /**
     * The layout of the office page. This is defined to programmatically create elements on the
     * display.
     */
    private LinearLayout LL;
    private ArrayList<user> officeMembers=new ArrayList<user>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_office_v2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setTitle("Office");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        extras = getIntent().getExtras();
        CUR_USER_KEY = extras.getString("userID");
        OFFICE_VIEW_KEY = extras.getString("viewID");
        OFFICE_VIEW_KEY = "59fe1da84ff6a";  //TODO:HARDCODED FOR TESTING PURPOSES
        //bind to xml layout
        LL = (LinearLayout) findViewById(R.id.offLayout);
        //bind to TextView created in xml
        officeName = (TextView) findViewById(R.id.OfficeName);
        offDesc = (TextView) findViewById(R.id.OfficeDesc);
        offAdd = (TextView) findViewById(R.id.OfficeAdd);

        //requests data from the database, turns it into a JSON object, and creates an office.
        StringRequest strReq = new StringRequest(Request.Method.POST, urlphp.getOfficeURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String ServerResponse) {
                try {
                    JSONObject obj = new JSONObject(ServerResponse);
                    officeName.setText(String.valueOf(obj.getString("name")));
                    offDesc.setText(String.valueOf(obj.getString("description")));
                    offAdd.setText(String.valueOf(obj.getString("address")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("Error", volleyError.toString());
                        volleyError.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("officeID", OFFICE_VIEW_KEY);
                return params;
            }
        };
        MySingleton.getInstance(ViewOfficeV2.this).addToRequestQueue(strReq);


        //gets member data
        StringRequest memberReq = new StringRequest(Request.Method.POST, urlphp.getOfficeMembersURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String ServerResponse) {
                try {
                    JSONArray jarr = new JSONArray(ServerResponse);
                    for (int i = 0; i < jarr.length(); i++) {
                        JSONObject obj = jarr.getJSONObject(i);
                        final String id = obj.getString("userID");
                        StringRequest stringReq = new StringRequest(Request.Method.POST, urlphp.getUserURL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                try {
                                    JSONObject jobj = new JSONObject(response);
                                    user cur = new user(jobj.getString("userID"), jobj.getString("fName"), jobj.getString("lName"),
                                            jobj.getString("email"), jobj.getString("description"), jobj.getString("party"));

                                    officeMembers.add(cur);
                                    TextView tmp = new TextView(ViewOfficeV2.this);
                                    tmp.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.MATCH_PARENT));
                                    tmp.setText(officeMembers.get(officeMembers.size() - 1).getFullName());
                                    LL.addView(tmp);
                                    
                                } catch (JSONException ex) {
                                    Log.d("JSONException", ex.toString());
                                    ex.printStackTrace();
                                }
                            }
                        },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        Log.d("Error", volleyError.toString());
                                        volleyError.printStackTrace();
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("userID", id);
                                return params;
                            }
                        };
                        MySingleton.getInstance(ViewOfficeV2.this).addToRequestQueue(stringReq);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("Error", volleyError.toString());
                        volleyError.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("officeID", OFFICE_VIEW_KEY);
                return params;
            }
        };
        MySingleton.getInstance(ViewOfficeV2.this).addToRequestQueue(memberReq);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent electionIntent = new Intent(ViewOfficeV2.this, Election.class);
                electionIntent.putExtra("officeID", OFFICE_VIEW_KEY);
                startActivity(electionIntent);
            }
        });

        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent voteIntent = new Intent(ViewOfficeV2.this, Vote.class);
                voteIntent.putExtra("officeID", OFFICE_VIEW_KEY);
                startActivity(voteIntent);
            }
        });
    }

    private boolean adminIsCurUser() {

        return true;
    }


}
