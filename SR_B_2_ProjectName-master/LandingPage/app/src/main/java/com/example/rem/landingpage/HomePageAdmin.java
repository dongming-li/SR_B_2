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
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HomePageAdmin is the homepage for admin only
 */
public class HomePageAdmin extends AppCompatActivity implements ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener {
    /**
     * Receive the logged in user's email and user ID
     */
    private String receivedEmail, userID;
    /**
     * An URL that fetches user details with android volley
     */
    private String URL = "http://proj-309-sr-b-2.cs.iastate.edu:80/getUser.php";
    /**
     * URL used to get admin message
     */
    private String adminMessageURL = "http://proj-309-sr-b-2.cs.iastate.edu:80/getAdminMessage.php";
    /**
     * URL called to delete admin message
     */
    private String deleteTicketURL = "http://proj-309-sr-b-2.cs.iastate.edu:80/deleteAdminMessage.php";
    /**
     * Variables that store strings to pass around and displaying
     */
    private String getFirstName, getLastName, getEmail, getAdminMessage, getTicketID;
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
        setContentView(R.layout.activity_home_page_admin);
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

        fetchAdminMessage();
        setNavDrawer();

        expListView.setOnGroupClickListener(this);
        expListView.setOnChildClickListener(this);
    }

    /**
     * This method fetches message from admin telling us that issue has been resolved or else
     */
    private void fetchAdminMessage() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, adminMessageURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray array;
                        if (response.equalsIgnoreCase("1")) {

                        } else {
                            try {
                                array = new JSONArray(response);
                                for(int i=0; i<array.length();i++) {
                                    JSONObject individualMessage = array.getJSONObject(i);
                                    getAdminMessage = individualMessage.getString("adminMessage");
                                    getTicketID = individualMessage.getString("ticketID");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(HomePageAdmin.this);
                                    builder.setMessage("From admin regarding your support ticket,\n\n" + getAdminMessage);
                                    builder.setCancelable(true);

                                    builder.setPositiveButton(
                                            "Got it!",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    deleteAdminMessage();
                                                }
                                            }
                                    );
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
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
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userID", userID);
                return params;
            }
        };

        MySingleton.getInstance(HomePageAdmin.this).addToRequestQueue(stringRequest);
    }

    private void setNavDrawer() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL,
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
                        Toast.makeText(HomePageAdmin.this, error.toString(), Toast.LENGTH_LONG).show();
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

        MySingleton.getInstance(HomePageAdmin.this).addToRequestQueue(stringRequest2);
    }

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
        listDataHeader.add("Bug reports");

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
            Intent viewProfileIntent = new Intent(HomePageAdmin.this, ViewProfileV2.class);
            ;
            viewProfileIntent.putExtra("userID", userID);
            viewProfileIntent.putExtra("email", receivedEmail);
            viewProfileIntent.putExtra("viewID", userID);
            startActivity(viewProfileIntent);
            parent.collapseGroup(groupPosition);
        }

        if (childItem == "Edit Profile") {
            Intent editProfileIntent = new Intent(HomePageAdmin.this, edit_screen.class);
            editProfileIntent.putExtra("userID", userID);
            startActivity(editProfileIntent);
            parent.collapseGroup(groupPosition);
        }

        if (childItem == "Your Office") {
            Intent viewOffice = new Intent(HomePageAdmin.this, ViewOfficeV2.class);
            viewOffice.putExtra("userID", userID);
            viewOffice.putExtra("viewID", "59fe1da84ff6a");
            startActivity(viewOffice);
            parent.collapseGroup(groupPosition);
        }

        if (childItem == "Create Office") {
            Intent createOffice = new Intent(HomePageAdmin.this, AddOffice.class);
            startActivity(createOffice);
            parent.collapseGroup(groupPosition);
        }

        if (childItem == "Create Event") {
            Intent createEvent = new Intent(HomePageAdmin.this, InsertEvent.class);
            startActivity(createEvent);
            parent.collapseGroup(groupPosition);
        }
        onBackPressed();
        return false;
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
            onBackPressed();
        }

        if (groupItem == "Bug reports"){
            Intent supportAdmin = new Intent(HomePageAdmin.this, SupportPageAdmin.class);
            supportAdmin.putExtra("email", receivedEmail);
            supportAdmin.putExtra("userID", userID);
            startActivity(supportAdmin);
            onBackPressed();
        }

        if (groupItem == "Support") {
            Intent supportIntent = new Intent(HomePageAdmin.this, SupportPage.class);
            supportIntent.putExtra("email", receivedEmail);
            supportIntent.putExtra("userID", userID);
            startActivity(supportIntent);
            onBackPressed();
        }

        if (groupItem == "Log Out") {

            Intent loginIntent = new Intent(HomePageAdmin.this, LoginPage.class);
            loginIntent.putExtra("email", receivedEmail);
            loginIntent.putExtra("userID", userID);
            startActivity(loginIntent);
            onBackPressed();
        }

        return false;
    }


    public void deleteAdminMessage(){
        StringRequest stringRequest5 = new StringRequest(Request.Method.POST, deleteTicketURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("0")) {

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
                params.put("userID", userID);
                params.put("ticketID", getTicketID);
                return params;
            }
        };
        MySingleton.getInstance(HomePageAdmin.this).addToRequestQueue(stringRequest5);
    }
}
