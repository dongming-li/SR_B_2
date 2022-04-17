package com.example.rem.landingpage;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
* Allows users to add office and register candidates for election.
*/
public class AddOffice extends AppCompatActivity {
    private EditText ET_description, ET_office_name, ET_office_address, ET_officeID;
    private String s_description, s_office_name, s_office_address, s_officeID, receivedEmail;
    private Integer privilege = 2; //2 for admin, 1 for manager, 0 for normal user, to be used in the future???

    private Boolean proceed_description, proceed_office_name, proceed_office_address, proceed_officeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_office);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ET_description = (EditText) findViewById(R.id.editDescription);
        ET_office_name = (EditText) findViewById(R.id.editOfficeName);
        ET_office_address = (EditText) findViewById(R.id.editOfficeAddress);
        //ET_officeID = (EditText) findViewById(R.id.editOfficeID);

        //Email.setText(getIntent().getStringExtra("email")); // if coming from LoginPage, get email

        receivedEmail = getIntent().getStringExtra("email");

        Button createButton = (Button) findViewById(R.id.buttonAddOffice);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check if user's input is valid
                if (!TextUtils.isEmpty(ET_description.getText().toString())) {
                    s_description = ET_description.getText().toString();
                    proceed_description = true;
                } else {
                    ET_description.setError("Invalid description"); // TODO doesn't display until cursor in EditText field
                    // Above bug throughout LoginPage and RegisterPage
                    proceed_description = false;
                }

                if (!TextUtils.isEmpty(ET_office_name.getText().toString())) {
                    s_office_name = ET_office_name.getText().toString();
                    proceed_office_name = true;
                } else {
                    ET_office_name.setError("Invalid office name");
                    proceed_office_name = false;
                }

                if (!TextUtils.isEmpty(ET_office_address.getText().toString())) {
                    s_office_address = ET_office_address.getText().toString();
                    proceed_office_address = true;
                } else {
                    ET_office_address.setError("Invalid office address");
                    proceed_office_address = false;
                }

                // if user's input is valid, proceed with server request
                if (proceed_description && proceed_office_name && proceed_office_address) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            "http://proj-309-sr-b-2.cs.iastate.edu:80/insertOffice.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    // if email exists already
                                    if (response.equalsIgnoreCase("Office already exists")) {
                                        Toast.makeText(AddOffice.this, "Email is already registered, try another.", Toast.LENGTH_LONG).show();

                                        ET_description.setText(""); // reset email and password fields
                                        ET_office_name.setText("");
                                        ET_office_address.setText("");
                                    }

                                    // if server could not register user
                                    else if (response.equalsIgnoreCase("Did not register user")) {
                                        Toast.makeText(AddOffice.this, "Could not register user, try again.", Toast.LENGTH_LONG).show();

                                        ET_description.setText(""); // reset email and password fields
                                        ET_office_name.setText("");
                                        ET_office_address.setText("");
                                    }

                                    // last condition, if unregistered user
                                    else { // in the case password doesn't match it will return an empty userID
//                                        String description = "";
//                                        String office_name = "";
//                                        String office_address = "";
                                        JSONObject office;

                                        try {
                                            office = new JSONObject(response);
//                                            description = office.getString("s_description");
//                                            office_name = office.getString("s_office_name");
//                                            office_address = office.getString("s_office_address");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(AddOffice.this, e.toString(), Toast.LENGTH_LONG).show(); //TODO remove later
                                        }

                                        Toast.makeText(AddOffice.this, "Office registered", Toast.LENGTH_SHORT).show();

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent viewOfficeIntent = new Intent(AddOffice.this, ViewOfficeV2.class);
                                                //    loginIntent.putExtra("email", email);
                                                //    loginIntent.putExtra("userID", userID);
                                                startActivity(viewOfficeIntent);
                                            }
                                        }, 6000);

                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    volleyError.printStackTrace();
                                    Toast.makeText(AddOffice.this, volleyError.toString(), Toast.LENGTH_LONG).show(); //TODO remove later
                                }
                            }
                    )
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("description", s_description);
                            params.put("name", s_office_name);
                            params.put("address", s_office_address);
                            return params;
                        }
                    };
                    MySingleton.getInstance(AddOffice.this).addToRequestQueue(stringRequest);
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