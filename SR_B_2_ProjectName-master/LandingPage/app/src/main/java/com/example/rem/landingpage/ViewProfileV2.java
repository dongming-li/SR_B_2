package com.example.rem.landingpage;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewProfileV2 extends AppCompatActivity {

    private Bundle extras;
    //private final String USER_KEY=extras.getString("USER_KEY");
    private String CUR_USER_KEY;
    private String VIEW_KEY;
    private String userEmail;
    private Button deleteAccount, editAccount;
    private String delUserURL = "http://proj-309-sr-b-2.cs.iastate.edu:80/delUser.php";
    private String receivedEmail, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_v2);

        //sets toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        receivedEmail = getIntent().getStringExtra("email");
        userID = getIntent().getStringExtra("userID");

        deleteAccount = findViewById(R.id.deleteAccount);


        //saves the USER_KEY for the next activity;
        extras = getIntent().getExtras();
        CUR_USER_KEY = extras.getString("userID");
        VIEW_KEY = extras.getString("viewID");

//links TextViews to xml
        final TextView fname = (TextView) findViewById(R.id.fName);
        final TextView lname = (TextView) findViewById(R.id.lname);
        final TextView pA = (TextView) findViewById(R.id.partyAffil);
        final TextView descr = (TextView) findViewById(R.id.description);

        //Floating action button used for sending an email to them.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        final LinearLayout pL = (LinearLayout) findViewById(R.id.proLayout);
        if(ownerIsCurUser()) {
            Button b = new Button(ViewProfileV2.this);
            b.setText("Edit");
            pL.addView(b);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ViewProfileV2.this, edit_screen.class);
                    i.putExtra("userID", CUR_USER_KEY);
                    startActivity(i);
                }
            });
        }

        //get data from server

        StringRequest strReq = new StringRequest(Request.Method.POST, urlphp.getUserURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String ServerResponse) {
                try {
                    JSONObject obj = new JSONObject(ServerResponse);
                    //user curUser = new user(
                    //      obj.getString("userID"), obj.getString("fName"), obj.getString("lName"),
                    //     obj.getString("email"), obj.getString("description"), obj.getString("party"));
                    fname.setText(String.valueOf(obj.getString("fName")));
                    lname.setText(String.valueOf(obj.getString("lName")));
                    pA.setText(String.valueOf(obj.getString("party")));
                    descr.setText(String.valueOf(obj.getString("description")));

                    userEmail=String.valueOf(obj.getString("email"));

                    ImageView profilePictureView = (ImageView) findViewById(R.id.profpic);
                    Picasso.with(getApplicationContext()).load( "http://proj-309-sr-b-2.cs.iastate.edu/ProfPics/"+String.valueOf(obj.getString("fname"))+".png"). resize(200, 200).into(profilePictureView);

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
        MySingleton.getInstance(ViewProfileV2.this).addToRequestQueue(strReq);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                //Intent emailIntent = new Intent(Intent.ACTION_SEND);
                Log.i("Send email", "");

                String[] TO= {userEmail.equals(null)?"":userEmail};
                String[] CC = {""};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "YourVote: ");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    Log.i("Finished sending email.", "");
                    emailIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP );
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ViewProfileV2.this, "There is no email client installed.", Toast.LENGTH_LONG).show();
                }

            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder3 = new AlertDialog.Builder(ViewProfileV2.this);
                builder3.setMessage("Delete account? :(");
                builder3.setCancelable(true);

                builder3.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeAccount();
                            }
                        }
                );

                builder3.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }
                );

                AlertDialog alert3 = builder3.create();
                alert3.show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_profile_v2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean ownerIsCurUser()
    {
        return (CUR_USER_KEY.equals(VIEW_KEY));
    }

    private void removeAccount() {
        StringRequest stringRequest4 = new StringRequest(Request.Method.POST, delUserURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("User deleted")) {
                            AlertDialog.Builder builder3 = new AlertDialog.Builder(ViewProfileV2.this);
                            builder3.setMessage("Account deleted. Return to login page or register page?");
                            builder3.setCancelable(true);

                            builder3.setPositiveButton(
                                    "Login",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent loginIntent = new Intent(ViewProfileV2.this, LoginPage.class);
                                            startActivity(loginIntent);
                                        }
                                    }
                            );

                            builder3.setNegativeButton(
                                    "Register",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent registerIntent = new Intent(ViewProfileV2.this, RegisterPage.class);
                                            startActivity(registerIntent);
                                        }
                                    }
                            );

                            AlertDialog alert3 = builder3.create();
                            alert3.show();
                        } else {
                            AlertDialog.Builder builder4 = new AlertDialog.Builder(ViewProfileV2.this);
                            builder4.setMessage("Failed to delete account!");
                            builder4.setCancelable(true);

                            builder4.setPositiveButton(
                                    "Try again",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            removeAccount();
                                        }
                                    }
                            ).setNeutralButton(
                                    "Contact support",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent supportIntent = new Intent(ViewProfileV2.this, SupportPage.class);
                                            supportIntent.putExtra("email", receivedEmail);
                                            supportIntent.putExtra("userID", userID);
                                            startActivity(supportIntent);
                                        }
                                    }
                            );

                            builder4.setNegativeButton(
                                    "Cancel",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }

                            );
                            AlertDialog alert4 = builder4.create();
                            alert4.show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(ViewProfileV2.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userID", userID);
                return params;
            }
        };
        MySingleton.getInstance(ViewProfileV2.this).addToRequestQueue(stringRequest4);
    }

}