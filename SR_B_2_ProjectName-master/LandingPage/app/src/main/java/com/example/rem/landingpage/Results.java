package com.example.rem.landingpage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Results extends AppCompatActivity {
    String electionID;
    JSONObject[] candidates = new JSONObject[2];
    TextView winnerView, loserView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        electionID = getIntent().getStringExtra("electionID");

        winnerView = findViewById(R.id.winner);
        loserView = findViewById(R.id.loser);

        getCandidates();
    }

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
                                candidates[i] =  obj;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        compareCandidates();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(Results.this, error.toString(), Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(Results.this).addToRequestQueue(stringRequest);
    }

    protected void compareCandidates() {
        String winner = "", loser = "";
        int winnerNumVotes = 0, loserNumVotes = 0;

        for (int i = 0; i < candidates.length; i++) {
            JSONObject tmp = candidates[i];
            try {
                loser = tmp.getString("userID");
                loserNumVotes = tmp.getInt("numVotes");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (loserNumVotes > winnerNumVotes) {
                String tmpString = winner;
                int tmpVotes = winnerNumVotes;

                winner = loser;
                winnerNumVotes = loserNumVotes;

                loser = tmpString;
                loserNumVotes = tmpVotes;
            }
        }
        String win = winner + " with " + winnerNumVotes + " votes.";
        String lose = loser + " with " + loserNumVotes + " votes.";
        winnerView.setText(win);
        loserView.setText(lose);
    }
}
