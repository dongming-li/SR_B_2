package com.example.rem.landingpage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SupportPage is a class that allows users to send feedback, bug reports, message, and etc to admin
 */
public class SupportPage extends AppCompatActivity implements ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener {

    /**
     * A place for user to input message
     */
    private EditText et_messageBody;
    /**
     * Receive the logged in user's email and user ID
     */
    private String receivedEmail, userID;
    /**
     * An URL that fetches user details with android volley
     */
    private String URL = "http://proj-309-sr-b-2.cs.iastate.edu:80/getUser.php";
    /**
     * An URL connects to the database that stores the message or feedback from the user
     */
    private String userMessageURL = "http://proj-309-sr-b-2.cs.iastate.edu:80/insertSupportTicket.php";
    /**
     * Variables that get the user details
     */
    private String getFirstName, getLastName, getEmail;
    /**
     * This variable stores the user's input message
     */
    private String getMessageBody;
    /**
     * This button will submit message to the database
     */
    private Button buttonSubmitMessage;
    /**
     * A adapter for listing the expandablelistview navigation drawer
     */
    private ExpandableListAdapter listAdapter;
    /**
     * Handles the views in the expandablelistview navigation drawer
     */
    private ExpandableListView expListView;
    /**
     * Store a list of main items in the expandablelistview navigation drawer
     */
    private List<String> listDataHeader;
    /**
     * Store the child items in the main item in the expandablelistview navigation drawer
     */
    private HashMap<String, List<String>> listDataChild;

    /**
     * Things or actions that will be done upon opening this page
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.nav_drawer_sub_items);
        // preparing list data
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);

        receivedEmail = getIntent().getStringExtra("email");
        userID = getIntent().getStringExtra("userID");

        setNavDrawer();

        expListView.setOnGroupClickListener(this);
        expListView.setOnChildClickListener(this);

        et_messageBody = (EditText) findViewById(R.id.messageBody);
        buttonSubmitMessage = (Button) findViewById(R.id.buttonSubmitMsg);
        buttonSubmitMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(et_messageBody.getText().toString())) {
                    getMessageBody = et_messageBody.getText().toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(SupportPage.this);
                    builder.setMessage("Have you included all your messages?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Send",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, userMessageURL,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    if (response.equalsIgnoreCase("0")) {
                                                        Toast.makeText(SupportPage.this, "Message sent.", Toast.LENGTH_LONG).show();
                                                        et_messageBody.setText("");
                                                    }
                                                    else{
                                                        Toast.makeText(SupportPage.this, "There might be an issue, try again later.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            },

                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    error.printStackTrace();
                                                    Toast.makeText(SupportPage.this, error.toString(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                    ) {
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<>();
                                            params.put("email", receivedEmail);
                                            params.put("userID", userID);
                                            params.put("message", getMessageBody);
                                            return params;
                                        }
                                    };
                                    MySingleton.getInstance(SupportPage.this).addToRequestQueue(stringRequest2);

                                }
                            }
                    );

                    builder.setNegativeButton(
                            "I want to write more",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }
                    );

                    AlertDialog alert = builder.create();
                    alert.show();

                } else {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(SupportPage.this);
                    builder2.setMessage("Empty message body!");
                    builder2.setCancelable(true);

                    builder2.setPositiveButton(
                            "Write more",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }
                    );

                    AlertDialog alert2 = builder2.create();
                    alert2.show();
                }

            }
        });
    }

    /**
     * Display first name, last name, and profile picture in the navigation drawer
     */
    private void setNavDrawer() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject user;

                        try {
                            user = new JSONObject(response);
                            getFirstName = user.getString("fName");
                            getLastName = user.getString("lName");
                            getEmail = user.getString("email");

                            ImageView profilePictureView = (ImageView) findViewById(R.id.profpic);
                            Picasso.with(getApplicationContext()).load("http://proj-309-sr-b-2.cs.iastate.edu/ProfPics/" + getFirstName + ".png").resize(200, 200).into(profilePictureView);

                            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                            View userName = navigationView.getHeaderView(0);
                            TextView nav_drawer_user = userName.findViewById(R.id.nav_user);
                            nav_drawer_user.setText(getFirstName + " " + getLastName + "\n" + getEmail);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(SupportPage.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", receivedEmail);
                params.put("userID", userID);
                return params;
            }
        };
        MySingleton.getInstance(SupportPage.this).addToRequestQueue(stringRequest);
    }

    /**
     * This method is used to setup the items in the navigation drawer
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Home");
        listDataHeader.add("User Profile");
        listDataHeader.add("Office Profile");
        listDataHeader.add("Event");
        listDataHeader.add("Support");
        listDataHeader.add("Log Out");

        // Adding child data
        List<String> user = new ArrayList<String>();
        user.add("Your Profile");
        user.add("Edit Profile");

        List<String> office = new ArrayList<String>();
        office.add("All Offices");
        office.add("Your Office");
        office.add("Create Office");

        List<String> event = new ArrayList<String>();
        event.add("Show All Events");
        event.add("Create Event");

        listDataChild.put(listDataHeader.get(1), user); // Header, Child data
        listDataChild.put(listDataHeader.get(2), office);
        listDataChild.put(listDataHeader.get(3), event);
    }

    /**
     * This method will go back to the previous page one page at a time
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    /**
     * Callback method to be invoked when a child in this expandable list has
     * been clicked.
     *
     * @param parent        The ExpandableListView where the click happened
     * @param v             The view within the expandable list/ListView that was clicked
     * @param groupPosition The group position that contains the child that
     *                      was clicked
     * @param childPosition The child position within the group
     * @param id            The row id of the child that was clicked
     * @return True if the click was handled
     */
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        String childItem = (String) listAdapter.getChild(groupPosition, childPosition);
        if (childItem == "Your Profile") {
            Intent viewProfileIntent = new Intent(SupportPage.this, ViewProfileV2.class);
            ;
            viewProfileIntent.putExtra("userID", userID);
            startActivity(viewProfileIntent);
            parent.collapseGroup(groupPosition);
        }

        if (childItem == "Edit Profile") {
            Intent editProfileIntent = new Intent(SupportPage.this, edit_screen.class);
            editProfileIntent.putExtra("userID", userID);
            startActivity(editProfileIntent);
            parent.collapseGroup(groupPosition);
        }

        if (childItem == "Your Office") {
            Intent viewOffice = new Intent(SupportPage.this, ViewOfficeV2.class);
            viewOffice.putExtra("userID", userID);
            viewOffice.putExtra("viewID", "59fe1da84ff6a");
            startActivity(viewOffice);
            parent.collapseGroup(groupPosition);
        }

        if (childItem == "Create Office") {
            Intent createOffice = new Intent(SupportPage.this, AddOffice.class);
            startActivity(createOffice);
            parent.collapseGroup(groupPosition);
        }

        if (childItem == "Create Event") {
            Intent createEvent = new Intent(SupportPage.this, InsertEvent.class);
            startActivity(createEvent);
            parent.collapseGroup(groupPosition);
        }
        onBackPressed();
        return true;
    }

    /**
     * Callback method to be invoked when a group in this expandable list has
     * been clicked.
     *
     * @param parent        The ExpandableListConnector where the click happened
     * @param v             The view within the expandable list/ListView that was clicked
     * @param groupPosition The group position that was clicked
     * @param id            The row id of the group that was clicked
     * @return True if the click was handled
     */
    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        String groupItem = (String) listAdapter.getGroup(groupPosition);

        if (groupItem == "Home") {
            if(receivedEmail.equals("admin@gmail.com")){
                Intent homeAdminIntent = new Intent(SupportPage.this, HomePageAdmin.class);
                homeAdminIntent.putExtra("email", receivedEmail);
                homeAdminIntent.putExtra("userID", userID);
                startActivity(homeAdminIntent);
                onBackPressed();
            }
            else{
                Intent homeIntent = new Intent(SupportPage.this, HomePage.class);
                homeIntent.putExtra("email", receivedEmail);
                homeIntent.putExtra("userID", userID);
                startActivity(homeIntent);
                onBackPressed();
            }
        }

        if (groupItem == "Support") {
            onBackPressed();
        }

        if (groupItem == "Log Out") {
            Intent loginIntent = new Intent(SupportPage.this, LoginPage.class);
            startActivity(loginIntent);
            onBackPressed();
        }

        return false;
    }

}
