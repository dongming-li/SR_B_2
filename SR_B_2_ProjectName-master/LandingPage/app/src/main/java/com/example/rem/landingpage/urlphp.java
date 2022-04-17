package com.example.rem.landingpage;

/**
 * @author Tyler Beder
 *
 * This class file is a simple way of getting our URL names. This class represents good modular
 * design, as we should only have to change the URL, if we were to move to a new php file for
 * getting the smae information.
 */

public class urlphp {
    private static final String root="http://proj-309-sr-b-2.cs.iastate.edu:80/";
    
    //lets us simply fetch the user data
    public static final String getUserURL=root+"getUser.php";
    //lets us update information on a user
    public static final String updateUserURL=root+"updateUser.php";
    //lets a user log a user in
    public static final String loginURL=root+"login.php";
    //registers a user in our database
    public static final String registerURL=root+"register.php";
    //gets Office Data
    public static final String getOfficeURL=root+"getOffice.php";
    //creates a new office
    public static final String insertOfficeURL=root+"insertOffice.php";
    //updates info on an already existing office
    public static final String updateOfficeURL=root+"updateOffice.php";
    //adds a user to an office
    public static final String insertMemberURL=root+"insertOfficeUser.php";

    public static final String getOfficeMembersURL=root+"getOfficeUsers.php";

    public static final String updateOfficeMemberURL=root+"updateOfficeUser.php";



}
