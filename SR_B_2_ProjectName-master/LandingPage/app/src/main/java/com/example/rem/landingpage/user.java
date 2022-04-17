package com.example.rem.landingpage;

/**
 * @author Tyler Beder
 */

public class user {
    /**
     * Stores the first name of the user that is currently logged in
     */
    private String fname;
    /**
     * Stores the last name of the user that is currently logged in
     */
    private String lname;
    /**
     * Stores the email of the user that is currently logged in
     */
    private String email;
    /**
     * Stores the user ID of the user that is currently logged in
     */
    private String userID;
    /**
     * Stores the short summary/description of the user that is currently logged in
     */
    private String desc;
    /**
     * Stores the party affiliation of the user that is currently logged in
     */
    private String party;

    /**
     * Creates a user and saves this data in a temporary space so that it doesn't have to get the
     * data from php every time. As of right now(further information permitting), at most one user
     * should be constructed at a time.
     * @param userID
     * user ID of the user currently logged in
     * @param fname
     * first name of the user currently logged in
     * @param lname
     * last name of the user currently logged in
     * @param email
     * email of the person logged in
     * @param description
     * short summary/description of the person logged in
     * @param party
     * party affiliation of the person logged in
     */
    public user(String userID, String fname, String lname, String email, String description, String party)
    {
        this.userID=userID;
        this.fname=fname;
        this.lname=lname;
        this.email=email;
        this.desc=description;
        this.party=party;
    }
    /**
     * Creates a user and saves this data in a temporary space so that it doesn't have to get the
     * data from php every time. As of right now(further information permitting), at most one user
     * should be constructed at a time.
     * @param data
     * a String array, intended to be returned from the server.
     */
    public user(String[] data)
    {
        //it would be highly advised to include some error handling. I(Tyler Beder) wrote this, and
        // even I know that if pieces outside of this are not working properly, that this is prone
        //to failure.
        this.userID=data[0];
        this.fname=data[1];
        this.lname=data[2];
        this.email=data[3];
        //this.password=data[4];
        this.desc=data[5];
        this.party=data[4];
    }
    public user(String data)
    {
        String[] sdata=data.split(",");
        this.userID=sdata[0];
        this.fname=sdata[1];
        this.lname=sdata[2];
        this.email=sdata[3];
        //this.password=data[4];
        this.desc=sdata[5];
        this.party=sdata[4];

    }
    /**
     * Gets the first name of the user that is currently logged in
     * @return first name
     */
    public String getFname() {
        return fname;
    }
    /**
     * Gets the last name of the user that is currently logged in
     * @return last name
     */
    public String getLname() {
        return lname;
    }
    /**
     * Gets the email address of the user that is currently logged in
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the password of the user that is currently logged in
     * @return password
     */
    //public String getPassword() {
        //return password;
    //}
    /**
     * Gets the User ID of the user that is currently logged in
     * @return UserID
     */
    public String getUserID() {
        return userID;
    }
    /**
     * Gets the short summary/description of the user that is currently logged in
     * @return short summary/description
     */
    public String getDesc() {
        return desc;
    }
    /**
     * Gets the party affiliation of the user that is currently logged in
     * @return party
     */
    public String getParty() {
        return party;
    }
    /**
     *
     */
    public String getFullName()
    {
        return fname+" "+lname;
    }

}
