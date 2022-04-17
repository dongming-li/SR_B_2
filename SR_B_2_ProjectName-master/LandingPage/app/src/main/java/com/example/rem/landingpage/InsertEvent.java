package com.example.rem.landingpage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
* InsertEvent that extends AppCapatActivity creates a new page where you enter event information.
* This class accepts user input and then sends it to the server via volley.
*/
public class InsertEvent extends AppCompatActivity {
    /**
    * edit text variable for description and event titles
    */
    private EditText et_eventTitle, et_eventDescription;
    /**
     * private string to hold title and descriptions
     */
    private String s_eventTitle, s_eventDescription;
    /**
     * boolean for whether or not to proceed from title to description
     */
    private Boolean proceed_eventTitle, proceed_eventDescription;
    /**
     * button to press to create event
      */
    private Button buttonCreateEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        et_eventTitle = (EditText) findViewById(R.id.eventTitle);
        et_eventDescription = (EditText) findViewById(R.id.eventDescription);

        buttonCreateEvent = (Button) findViewById(R.id.buttonCreateEvent);
        buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(et_eventTitle.getText().toString())) {
                    s_eventTitle = et_eventTitle.getText().toString();
                    proceed_eventTitle = true;
                } else {
                    et_eventTitle.setError("Invalid event title");
                    proceed_eventTitle = false;
                }

                if (!TextUtils.isEmpty(et_eventDescription.getText().toString())) {
                    s_eventDescription = et_eventDescription.getText().toString();
                    proceed_eventDescription = true;
                } else {
                    et_eventDescription.setError("Invalid office name");
                    proceed_eventDescription = false;
                }

                if (proceed_eventTitle && proceed_eventDescription) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            "http://proj-309-sr-b-2.cs.iastate.edu:80/insertEvent.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    // if email exists already
                                    if (response.equalsIgnoreCase("Event exists")) {
                                        Toast.makeText(InsertEvent.this, "Same event title exists!", Toast.LENGTH_LONG).show();

                                        et_eventTitle.setText(""); // reset email and password fields
                                        et_eventDescription.setText("");
                                    } else {
                                        JSONObject event;

                                        try {
                                            event = new JSONObject(response);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(InsertEvent.this, e.toString(), Toast.LENGTH_LONG).show(); //TODO remove later
                                        }

                                        Toast.makeText(InsertEvent.this, "Event created successful!.", Toast.LENGTH_SHORT).show();

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent homeIntent = new Intent(InsertEvent.this, HomePage.class);
                                                startActivity(homeIntent);
                                            }
                                        }, 5000);

                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    volleyError.printStackTrace();
                                    Toast.makeText(InsertEvent.this, volleyError.toString(), Toast.LENGTH_LONG).show(); //TODO remove later
                                }
                            }
                    ) {
                        /**
                        *
                        */
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("eventTitle", s_eventTitle);
                            params.put("eventDescription", s_eventDescription);
                            return params;
                        }
                    };
                    MySingleton.getInstance(InsertEvent.this).addToRequestQueue(stringRequest);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
