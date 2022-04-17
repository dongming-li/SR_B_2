package com.example.rem.landingpage;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * This class is used to create and store elections.
 * Elections are created by office administrators and viewed by office members.
 * Office members can vote in these elections.
 *
 * @author Henry Saling
 */
public class Election extends AppCompatActivity {
    /**
     * variables to be stored in db
     * corresponds to office and user inputted position title
     * electionID is generated and then returned via server
     */
    private String electionID, officeID, posTitle;
    /**
     * candidates in the election
     */
    private String[] candidates =  new String[2]; //TODO dynamically allocate
    /**
     * ui
     */
    private EditText editTextPosTitle, candidate1, candidate2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election);

        editTextPosTitle = findViewById(R.id.editTextPosTitle); candidate1 = findViewById(R.id.candidate1);
        candidate2 = findViewById(R.id.candidate2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton insertElection = findViewById(R.id.insertElection);
        insertElection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO get from edit texts
                candidates[0] = candidate1.getText().toString();
                candidates[1] = candidate2.getText().toString();
                //
                posTitle = editTextPosTitle.getText().toString();
                officeID = getIntent().getStringExtra("officeID");
                insertElection();
                Toast.makeText(Election.this, "Election successfully created.", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * inserts election to db via server
     */
    protected void insertElection() {
        // insert election
        StringRequest insertElectionRequest = new StringRequest(Request.Method.POST,
                "http://proj-309-sr-b-2.cs.iastate.edu:80/insertElection.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            electionID = obj.getString("electionID");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int i = 0; i < candidates.length; i++) {
                            insertCandidate(candidates[i]);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(Election.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("officeID", officeID);
                params.put("posTitle", posTitle);
                return params;
            }
        };
        MySingleton.getInstance(Election.this).addToRequestQueue(insertElectionRequest);
    }

    /**
     * stores candidates in db via server
     *
     * @param userID
     */
    protected void insertCandidate(final String userID) {
        // insert candidate
        StringRequest insertCandidateRequest = new StringRequest(Request.Method.POST,
                "http://proj-309-sr-b-2.cs.iastate.edu:80/insertCandidate.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //TODO check
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(Election.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("electionID", electionID);
                params.put("userID", userID);
                return params;
            }
        };
        MySingleton.getInstance(Election.this).addToRequestQueue(insertCandidateRequest);
    }
}