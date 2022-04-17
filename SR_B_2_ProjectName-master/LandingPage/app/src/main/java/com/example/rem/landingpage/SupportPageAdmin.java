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
 * SupportPageAdmin is a class for admin only and it allows admin to read users' feedback or message and reply to the user.
 */
public class SupportPageAdmin extends AppCompatActivity implements ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {

    /**
     * A variable for user to store admin message
     */
    private EditText et_replyUserMessage;
    /**
     * Receive the logged in user's email and user ID
     */
    private String receivedEmail, userID;
    /**
     * An URL that fetches user details with android volley
     */
    private String URL = "http://proj-309-sr-b-2.cs.iastate.edu:80/getUser.php";
    /**
     * Used to fetch users' support tickets
     */
    private String fetchUserMessageURL = "http://proj-309-sr-b-2.cs.iastate.edu:80/getSupportTickets.php";
    /**
     * Used to store admin message
     */
    private String insertAdminMessageURL = "http://proj-309-sr-b-2.cs.iastate.edu:80/insertAdminMessage.php";
    /**
     * Called to delete solved tickets
     */
    private String deleteTicketURL = "http://proj-309-sr-b-2.cs.iastate.edu:80/deleteSupportTicket.php";
    /**
     * Variables to store data for navigation drawer and strings
     */
    private String getFirstName, getLastName, getEmail, getUserMessage, getUserID, getReplyMessage, getUserEmail, getTicketID, globalUserID, globalTicketID;
    /**
     * Submit and send message to user
     */
    private Button buttonReplyMessage;
    /**
     * Display the details of the user that submit the ticket
     */
    private TextView displayUserID, displayUserEmail, displayUserMessage;
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
        setContentView(R.layout.activity_support_page_admin);

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

        displayUserID = findViewById(R.id.userID);
        displayUserEmail = findViewById(R.id.userEmail);
        displayUserMessage = findViewById(R.id.showUserMessage);

        setNavDrawer();

        expListView.setOnGroupClickListener(this);
        expListView.setOnChildClickListener(this);

        getData();

        et_replyUserMessage = (EditText) findViewById(R.id.replyUserTextBox);
        buttonReplyMessage = (Button) findViewById(R.id.buttonReplyUser);
        buttonReplyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(et_replyUserMessage.getText().toString())) {
                    getReplyMessage = et_replyUserMessage.getText().toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(SupportPageAdmin.this);
                    builder.setMessage("Send to user?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Send",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    replyUser();
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
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(SupportPageAdmin.this);
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
            Intent viewProfileIntent = new Intent(SupportPageAdmin.this, ViewProfileV2.class);
            ;
            viewProfileIntent.putExtra("userID", userID);
            startActivity(viewProfileIntent);
            parent.collapseGroup(groupPosition);
        }

        if (childItem == "Edit Profile") {
            Intent editProfileIntent = new Intent(SupportPageAdmin.this, edit_screen.class);
            editProfileIntent.putExtra("userID", userID);
            startActivity(editProfileIntent);
            parent.collapseGroup(groupPosition);
        }

        if (childItem == "Your Office") {
            Intent viewOffice = new Intent(SupportPageAdmin.this, ViewOfficeV2.class);
            viewOffice.putExtra("userID", userID);
            viewOffice.putExtra("viewID", "59fe1da84ff6a");
            startActivity(viewOffice);
            parent.collapseGroup(groupPosition);
        }

        if (childItem == "Create Office") {
            Intent createOffice = new Intent(SupportPageAdmin.this, AddOffice.class);
            startActivity(createOffice);
            parent.collapseGroup(groupPosition);
        }

        if (childItem == "Create Event") {
            Intent createEvent = new Intent(SupportPageAdmin.this, InsertEvent.class);
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
                Intent homeAdminIntent = new Intent(SupportPageAdmin.this, HomePageAdmin.class);
                homeAdminIntent.putExtra("email", receivedEmail);
                homeAdminIntent.putExtra("userID", userID);
                startActivity(homeAdminIntent);
                onBackPressed();
            }
            else{
                Intent homeIntent = new Intent(SupportPageAdmin.this, HomePage.class);
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
            Intent loginIntent = new Intent(SupportPageAdmin.this, LoginPage.class);
            startActivity(loginIntent);
            onBackPressed();
        }

        return false;
    }

    /**
     * This method will get user's message from the database
     */
    public void getData() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, fetchUserMessageURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("1")) {
                            AlertDialog.Builder builder3 = new AlertDialog.Builder(SupportPageAdmin.this);
                            builder3.setMessage("There is no message!");
                            builder3.setCancelable(true);

                            builder3.setPositiveButton(
                                    "Huuray!!!",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onBackPressed();
                                        }
                                    }
                            );

                            AlertDialog alert3 = builder3.create();
                            alert3.show();

                        } else {
                            JSONObject jsonObject;

                            try {
                                jsonObject = new JSONObject(response);

                                getTicketID = jsonObject.getString("ticketID");
                                getUserID = jsonObject.getString("userID");
                                getUserEmail = jsonObject.getString("email");
                                getUserMessage = jsonObject.getString("message");

                                globalUserID = getUserID;
                                globalTicketID = getTicketID;

                                displayUserID.setText(getUserID);
                                displayUserEmail.setText(getUserEmail);
                                displayUserMessage.setText(getUserMessage);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        MySingleton.getInstance(SupportPageAdmin.this).addToRequestQueue(stringRequest2);
    }

    /**
     * Display first name, last name, and profile picture in the navigation drawer
     */
    public void setNavDrawer(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject;

                        try {
                            jsonObject = new JSONObject(response);
                            getFirstName = jsonObject.getString("fName");
                            getLastName = jsonObject.getString("lName");
                            getEmail = jsonObject.getString("email");


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
                        Toast.makeText(SupportPageAdmin.this, error.toString(), Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(SupportPageAdmin.this).addToRequestQueue(stringRequest);
    }

    /**
     * Send admin message to user
     */
    public void replyUser(){
        StringRequest stringRequest3 = new StringRequest(Request.Method.POST, insertAdminMessageURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("0")) {
                            displayUserID.setText("");
                            displayUserMessage.setText("");
                            displayUserEmail.setText("");
                            et_replyUserMessage.setText("");

                            AlertDialog.Builder builder4 = new AlertDialog.Builder(SupportPageAdmin.this);
                            builder4.setMessage("Message sent!");
                            builder4.setCancelable(true);

                            builder4.setPositiveButton(
                                    "Get another ticket?",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            getData();
                                        }
                                    }
                            );

                            builder4.setNegativeButton(
                                    "No",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onBackPressed();
                                        }
                                    }
                            );

                            AlertDialog alert4 = builder4.create();
                            alert4.show();

                            deleteTicket();
                        } else {
                            Toast.makeText(SupportPageAdmin.this, "There might be an issue, fix it", Toast.LENGTH_LONG).show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userID", globalUserID);
                params.put("adminMessage", getReplyMessage);
                params.put("ticketID", globalTicketID);
                return params;
            }
        };
        MySingleton.getInstance(SupportPageAdmin.this).addToRequestQueue(stringRequest3);
    }

    /**
     *
     * The method will delete the user's support ticket after the admin replies
     */
    public void deleteTicket(){
        StringRequest stringRequest4 = new StringRequest(Request.Method.POST, deleteTicketURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("0")) {
                            Toast.makeText(SupportPageAdmin.this, "Ticket deleted.", Toast.LENGTH_LONG).show();
                        } else {
                            getData();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ticketID", globalTicketID);
                return params;
            }
        };
        MySingleton.getInstance(SupportPageAdmin.this).addToRequestQueue(stringRequest4);
    }
}
