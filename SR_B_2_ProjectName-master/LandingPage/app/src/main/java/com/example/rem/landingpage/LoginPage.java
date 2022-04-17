package com.example.rem.landingpage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginPage extends AppCompatActivity {

    /**
     * edit text for email and password
     */
    private EditText Email, Password;
    /**
     * string to hold email and password
     */
    private String email, password;
    /**
     * userid set to null initially until it looks for a user
     */
    private String userID = "";
    /**
     * boolean for whether to proceed with valid email or valid password.
     */
    private Boolean proceed_Email, proceed_Password;
    /**
     * textview for redirectregister
     */
    private TextView redirectRegister;

    /**
     * Creates the login page
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Email = (EditText) findViewById(R.id.loginEmail);
        Password = (EditText) findViewById(R.id.loginPassword);

        redirectRegister = (TextView) findViewById(R.id.clickRegister);
        redirectRegister.setVisibility(View.INVISIBLE);

        final Button Login = (Button) findViewById(R.id.loginButton);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //wLogin.setClickable(false);

                // check if user's input is valid
                if (isEmailValid(Email.getText().toString()) && !TextUtils.isEmpty(Email.getText().toString())) {
                    email = Email.getText().toString();
                    proceed_Email = true;
                } else {
                    Email.setError("Invalid email"); // TODO doesn't display until cursor in EditText field
                    // Above bug throughout LoginPage and RegisterPage
                    proceed_Email = false;
                }

                if (isPasswordValid(Password.getText().toString()) && !TextUtils.isEmpty(Password.getText().toString())) {
                    password = Password.getText().toString();
                    proceed_Password = true;
                } else {
                    Password.setError("Invalid password");
                    proceed_Password = false;
                }

                // if user's input is valid, proceed with server request
                if (proceed_Email && proceed_Password) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            "http://proj-309-sr-b-2.cs.iastate.edu:80/login.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    // if email does not exist
                                    if (response.equalsIgnoreCase("Not registered")) {
                                        Toast.makeText(LoginPage.this, "User does not exist, registration needed.", Toast.LENGTH_LONG).show();

                                        Email.setText("");
                                        Password.setText("");

                                        redirectRegister.setVisibility(View.VISIBLE);
                                        redirectRegister.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent registerIntent = new Intent(LoginPage.this, RegisterPage.class);
                                                //registerIntent.putExtra("email", email);
                                                startActivity(registerIntent);
                                            }
                                        });
                                    }

                                    // if password mismatch
                                    else if (response.equalsIgnoreCase("Wrong password")) {
                                        Toast.makeText(LoginPage.this, "Wrong password, try again.", Toast.LENGTH_LONG).show();
                                        Password.setText("");
                                    }

                                    // last condition, if password match
                                    else { // in the case password doesn't match it will return an empty userID
                                        JSONObject user;

                                        try {
                                            user = new JSONObject(response);
                                            userID = user.getString("userID");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(LoginPage.this, e.toString(), Toast.LENGTH_LONG).show(); //TODO remove later
                                        }

                                        Toast.makeText(LoginPage.this, "Successful login", Toast.LENGTH_SHORT).show();

                                        if(email.equals("admin@gmail.com")){
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent homeAdminIntent = new Intent(LoginPage.this, HomePageAdmin.class);
                                                    homeAdminIntent.putExtra("email", email);
                                                    homeAdminIntent.putExtra("userID", userID);
                                                    startActivity(homeAdminIntent);
                                                }
                                            }, 1000);
                                        }
                                        else{
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent homeIntent = new Intent(LoginPage.this, HomePage.class);
                                                    homeIntent.putExtra("email", email);
                                                    homeIntent.putExtra("userID", userID);
                                                    startActivity(homeIntent);
                                                }
                                            }, 1000);
                                        }
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                    Toast.makeText(LoginPage.this, error.toString(), Toast.LENGTH_LONG).show(); //TODO remove later
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("email", email);
                            params.put("password", password);
                            return params;
                        }
                    };
                    MySingleton.getInstance(LoginPage.this).addToRequestQueue(stringRequest);
                }
            }
        });
    }

    private boolean isEmailValid(String email) {
        boolean condition = true;
        int counter = 0;

        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@') {
                counter++;
            }
        }

        if (counter > 1 || counter == 0) {
            condition = false;
        }

        return condition;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
