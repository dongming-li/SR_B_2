package com.example.rem.landingpage;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * This class is used for voting in elections.
 * Office members can vote in the election once.
 * Office administrators can view the results.
 *
 * @author Henry Saling
 */
public class Vote extends AppCompatActivity {

    /**
     * variables to be stored and retrieved from db
     * corresponds to office
     * winner identifies which candidate won
     */
    String officeID, electionID, numVotes, winner;
    /**
     * candidates of election
     */
    String[] candidates = new String[2];
    /**
     * ui
     */
    TextView candidate1, candidate2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        candidate1 = findViewById(R.id.candidate1);
        candidate2 = findViewById(R.id.candidate2);

        officeID = getIntent().getStringExtra("officeID");
        electionID = "5a285c3a35755"; //TODO replace hard code

        getCandidates();

        FloatingActionButton fab1 = findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                winner = (String) candidate1.getText();
                updateCandidateVotes(winner);
                Intent intent = new Intent(Vote.this, Results.class);
                intent.putExtra("electionID", electionID);
                startActivity(intent);
            }
        });

        FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                winner = (String) candidate2.getText();
                updateCandidateVotes(winner);
                Intent intent = new Intent(Vote.this, Results.class);
                intent.putExtra("electionID", electionID);
                startActivity(intent);
            }
        });
    }

    /**
     * retrieves the candidates from db via server
     */
    protected void getCandidates() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://proj-309-sr-b-2.cs.iastate.edu:80/getCandidates.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray array = null;
                        try {
                            array = new JSONArray(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int i = 0; i < array.length(); ++i) {
                            try {
                                JSONObject obj = array.getJSONObject(i);
                                candidates[i] = obj.getString("userID");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        candidate1.setText(candidates[0]);
                        candidate2.setText(candidates[1]);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(Vote.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("electionID", electionID);
                return params;
            }
        };
        MySingleton.getInstance(Vote.this).addToRequestQueue(stringRequest);
    }

    /**
     * updates the number of votes a candidate has
     *
     * @param winner
     */
    protected void updateCandidateVotes(final String winner) {
        StringRequest getCandidateRequest = new StringRequest(Request.Method.POST,
                "http://proj-309-sr-b-2.cs.iastate.edu:80/getCandidate.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Integer tmp = obj.getInt("numVotes");
                            tmp++;
                            numVotes = tmp.toString();
                            updateVotes(winner);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(Vote.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userID", winner);
                params.put("electionID", electionID);
                return params;
            }
        };
        MySingleton.getInstance(Vote.this).addToRequestQueue(getCandidateRequest);
    }

    /**
     * helper method for updateCandidateVotes
     *
     * @param winner
     */
    protected void updateVotes(final String winner) {
        StringRequest updateCandidateRequest = new StringRequest(Request.Method.POST,
                "http://proj-309-sr-b-2.cs.iastate.edu:80/updateCandidate.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(Vote.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userID", winner);
                params.put("electionID", electionID);
                params.put("numVotes", numVotes);
                return params;
            }
        };
        MySingleton.getInstance(Vote.this).addToRequestQueue(updateCandidateRequest);
    }
}
