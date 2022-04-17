package com.example.rem.landingpage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
 * Page where users register their email and make an account in the app.
 */
public class RegisterPage extends AppCompatActivity {
    /**
     * edit text for user properties
     */
    private EditText FirstName, LastName, Email, Password;
    /**
     * string that contains user properties
     */
    private String fName, lName, email, password;
    /**
     * booleans for user properties to decide if they are valid or not
     */
    private Boolean proceed_FirstName, proceed_LastName, proceed_Email, proceed_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        FirstName = (EditText) findViewById(R.id.editTextFirstName);
        LastName = (EditText) findViewById(R.id.editTextLastName);
        Email = (EditText) findViewById(R.id.editTextEmail);
        Password = (EditText) findViewById(R.id.editTextPassword);

        Email.setText(getIntent().getStringExtra("email")); // if coming from LoginPage, get email

        Button InsertButton = (Button) findViewById(R.id.ButtonInsert);
        InsertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            //check if user's input is valid
            if (!TextUtils.isEmpty(FirstName.getText().toString()) && isAdminFirstName(FirstName.getText().toString())) {
                fName = FirstName.getText().toString();
                proceed_FirstName = true;
            }
            else {
                FirstName.setError("Invalid first name"); // TODO doesn't display until cursor in EditText field
                                                          // Above bug throughout LoginPage and RegisterPage
                proceed_FirstName = false;
            }

            if (!TextUtils.isEmpty(LastName.getText().toString()) && isAdminLastName(LastName.getText().toString())) {
                lName = LastName.getText().toString();
                proceed_LastName = true;
            }
            else {
                LastName.setError("Invalid last name");
                proceed_LastName = false;
            }

            if (isEmailValid(Email.getText().toString()) && !TextUtils.isEmpty(Email.getText().toString())) {
                email = Email.getText().toString();
                proceed_Email = true;
            } else {
                Email.setError("Invalid email");
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

            if (proceed_FirstName && proceed_LastName && proceed_Email && proceed_Password) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    "http://proj-309-sr-b-2.cs.iastate.edu:80/register.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // if email exists already
                            if (response.equalsIgnoreCase("Email exists")) {
                                Toast.makeText(RegisterPage.this, "Email is already registered, try another.", Toast.LENGTH_LONG).show();

                                Email.setText(""); // reset email and password fields
                                Password.setText("");
                            }

                            // if server could not register user
                            else if (response.equalsIgnoreCase("Did not register user")) {
                                Toast.makeText(RegisterPage.this, "Could not register user, try again.", Toast.LENGTH_LONG).show();

                                Email.setText(""); // reset email and password fields
                                Password.setText("");
                            }

                            // last condition, if registered user
                            else { // in the case password doesn't match it will return an empty userID
                                String userID = "";
                                JSONObject user;

                                try {
                                    user = new JSONObject(response);
                                    userID = user.getString("userID");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(RegisterPage.this, e.toString(), Toast.LENGTH_LONG).show(); //TODO remove later
                                }

                                Toast.makeText(RegisterPage.this, "Successful registration", Toast.LENGTH_SHORT).show();

                                Toast.makeText(RegisterPage.this, "Redirecting to login page", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent loginIntent = new Intent(RegisterPage.this, LoginPage.class);
                                    //    loginIntent.putExtra("email", email);
                                    //    loginIntent.putExtra("userID", userID);
                                        startActivity(loginIntent);
                                    }
                                }, 3000);

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            volleyError.printStackTrace();
                            Toast.makeText(RegisterPage.this, volleyError.toString(), Toast.LENGTH_LONG).show(); //TODO remove later
                        }
                    }
                )
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("fName", fName);
                        params.put("lName", lName);
                        params.put("email", email);
                        params.put("password", password);
                        return params;
                    }
                };
                MySingleton.getInstance(RegisterPage.this).addToRequestQueue(stringRequest);
            }
            }
        });
    }

    /**
     * checks if email is valid
     * @param email
     * @return boolean
     */
    private boolean isEmailValid(String email) {
        boolean condition = true;
        int counter = 0;

        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@') {
                counter++;
            }
        }

        if(email.equals("admin")){
            condition = false;
        }

        if (counter > 1 || counter == 0) {
            condition = false;
        }

        return condition;
    }

    /**
     * Makes sure user can't register as admin
     * @param FirstName
     * @return boolean
     */
    private boolean isAdminFirstName(String FirstName){
        boolean condition = true;
        if(FirstName.equals("admin")){
            condition = false;
        }
        return condition;
    }

    /**
     * makes sure admin isnt the users last name
     * @param LastName
     * @return boolean
     */
    private boolean isAdminLastName(String LastName){
        boolean condition = true;
        if(LastName.equals("admin")){
            condition = false;
        }
        return condition;
    }

    /**
     * ensures that the user has a long enough password.
     * @param password
     * @return boolean
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}