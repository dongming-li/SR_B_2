package com.example.rem.landingpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
 * @author Tyler Beder
 *         This is the profile editing screen. It's used for by users to edit their own information.
 */
public class edit_screen extends AppCompatActivity {
    /**
     * A Bundle used to get the id's from activity to activity.
     */
    private Bundle x;
    /**
     * The id of the user that is currently logged in.
     */
    private String CUR_USER_KEY;
    /**
     * An editText field used to view and change the user's first name
     */
    private EditText firstName;
    /**
     * An editText field used to view and change the user's last name
     */
    private EditText lastName;
    /**
     * An editText field used to view and change the user's email
     */
    private EditText ETemail;

    private EditText persSum;
    /**
     * An editText field used to view and change the user's party affiliation
     */
    private EditText partyA;
    /**
     * An editText field used to view and change the user's password
     */
    private EditText ETpassword;
    /**
     * A button used to say that the user is finished editing their information. Upon clicking,
     * this will redirect them to ViewProfile, so that they can sse theyr profile the way others see
     * their profile.
     */
    private Button done;
    /**
     * A button used to change the profile picture.
     */
    private Button changeProf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_screen);

        firstName = (EditText) findViewById(R.id.editFirstName);
        lastName = (EditText) findViewById(R.id.editLastName);

        persSum = (EditText) findViewById(R.id.editPersSum);
        ETpassword = (EditText) findViewById(R.id.editPass);
        partyA = (EditText) findViewById(R.id.editPartyA);
        ETemail = (EditText) findViewById(R.id.editEmail);
        done = (Button) findViewById(R.id.fin);
        changeProf= (Button)findViewById(R.id.button);
        x = getIntent().getExtras();
        CUR_USER_KEY = x.getString("userID");

        //Gets pre-existing dat from the server
        StringRequest strReq = new StringRequest(Request.Method.POST, urlphp.getUserURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String ServerResponse) {
                try {
                    JSONObject obj = new JSONObject(ServerResponse);
                    user curUser = new user(
                            obj.getString("userID"), obj.getString("fName"), obj.getString("lName"),
                            obj.getString("email"), obj.getString("description"), obj.getString("party"));

                    firstName.setText(String.valueOf(curUser.getFname()), TextView.BufferType.EDITABLE);
                    lastName.setText(String.valueOf(curUser.getLname()), TextView.BufferType.EDITABLE);
                    //don't show old password.
                    partyA.setText(String.valueOf(curUser.getParty()), TextView.BufferType.EDITABLE);
                    persSum.setText(String.valueOf(curUser.getDesc()), TextView.BufferType.EDITABLE);
                } catch (JSONException e) {
                    Log.d("JSONException",e.toString());
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
                params.put("userID", CUR_USER_KEY);
                return params;
            }
        };
        //reqQu.add(strReq);
        MySingleton.getInstance(edit_screen.this).addToRequestQueue(strReq);
        firstName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String str = firstName.getText().toString();
                firstName.setText(str, TextView.BufferType.EDITABLE);
            }
        });
        lastName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String str = lastName.getText().toString();
                lastName.setText(str, TextView.BufferType.EDITABLE);
            }
        });
        persSum.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String str = persSum.getText().toString();
                persSum.setText(str, TextView.BufferType.EDITABLE);
            }
        });
        ETpassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String str = ETpassword.getText().toString();
                ETpassword.setText(str, TextView.BufferType.EDITABLE);

            }
        });
        partyA.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String str = partyA.getText().toString();
                partyA.setText(str, TextView.BufferType.EDITABLE);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Calls on server to update the database
                StringRequest strSend = new StringRequest(Request.Method.POST, urlphp.updateUserURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        Log.d("Response", ServerResponse);
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
                        params.put("userID", CUR_USER_KEY);
                        params.put("fName", firstName.getText().toString());
                        params.put("lName", lastName.getText().toString());
                        params.put("party", partyA.getText().toString());
                        params.put("description", persSum.getText().toString());
                        return params;
                    }
                };
                //reqQu.add(strSend);
                MySingleton.getInstance(edit_screen.this).addToRequestQueue(strSend);
                //CREATE NEW USER OBJECT WITH NEW INFORMATION
                Intent i = new Intent(edit_screen.this, ViewProfileV2.class);
                i.putExtra("userID", CUR_USER_KEY);
                i.putExtra("viewID", CUR_USER_KEY);
                startActivity(i);
                finish();
            }
        });
        changeProf.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //TODO: TYLER, add functionality here
            }
        });
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void attemptEdit() {
        // Reset errors.
        ETemail.setError(null);
        ETpassword.setError(null);

        // Store values at the time of the login attempt.
        String email = ETemail.getText().toString();
        String password = ETpassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            ETpassword.setError("Incorrect Password");
            focusView = ETpassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            ETemail.setError("Incorrect Email");
            focusView = ETemail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            ETemail.setError("Invalid Email");
            focusView = ETemail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

        }
    }
}

