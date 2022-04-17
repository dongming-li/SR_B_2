package com.example.rem.landingpage;

import java.util.ArrayList;

/**
 * @author Tyler Beder
 *         This is a class used to fetch data from the server, at this point in time it's more of a
 *         suggestion as to how we want to implement offices on the server end, rather than a functional
 *         part of the project.
 */

public class Office {
    /**
     * An embedded class used to create a direct association between the user profile, and the
     * position name.
     */
    protected class OfficeMember {
        /**
         * The name of the position.
         */
        private String posName;
        /**
         * The user key of the user holding the position.
         */
        private String posHolder;

        /**
         * Constructs an office member object, which contains the name of the position, and the key
         * of the user holding the position.
         *
         * @param posName   The name of the position being created.
         * @param posHolder The key of the user being designated to hold the position.
         */
        private OfficeMember(String posName, String posHolder) {
            this.posHolder = posHolder;
            this.posName = posName;
        }

        /**
         * Gets the name of the position
         *
         * @return the name of the position
         */
        public String getPosName() {
            return posName;
        }

        /**
         * Gets the key of the member holding the position.
         *
         * @return the user key of the user holding the position.
         */
        public String getPosHolder() {
            return posHolder;
        }
    }

    /**
     * Name of the office
     */
    private String officeName;
    /**
     * This is the key of the administrator, which is the only person authorized to add or create
     * positions under this office. This will also be the only person available to add or remove
     * existing officers.
     */
    private String officeAdminKey;
    /**
     * The address of this office.
     */
    private String officeAddress;
    /**
     * The description or summary of this office.
     */
    private String officeDescription;
    /**
     * an array list of office members.
     */
    private ArrayList<OfficeMember> officeMembers = new ArrayList<OfficeMember>();
    /**
     * This is a character that will be passed in and used as a delimiter when getting data or
     * sending new office data to the database.
     */
    private static final char del = 0xc39e;

    /**
     * creates a new office with no initial members, except for the first administrator. The
     * creator will pass an User ID into this constructor to specify the office's administration
     *
     * @param adminKey   a user id to be used as a key for the proposed user to be an administrator
     * @param officeName the name of the office
     * @param address    the address of the office
     * @param offDesc    the description or summary of the office
     * @param memberlist the list of member keys from the server
     * @param posList    the list of positions from the server
     * @throws Exception the number of members and number of positions must be the same
     */
    public Office(String adminKey, String officeName, String address, String offDesc, String memberlist, String posList) throws Exception {
        this.officeName = officeName;
        this.officeAdminKey = adminKey;
        this.officeAddress = address;
        this.officeDescription = offDesc;
        constructFromData(memberlist, posList);
    }
    public Office(String officeName, String address, String offDesc)
    {
        this.officeName =officeName;
        this.officeAddress=address;
        this.officeDescription=offDesc;
    }

    /**
     * This method will be used to construct office data locally. It will parse the
     */
    private void constructFromData(String memKeyData, String posData) throws Exception {
        String[] memKeys = memKeyData.split(String.valueOf(del));
        String[] posNames = posData.split(String.valueOf(del));
        if (memKeys.length != posNames.length)
            throw new Exception();
        for (int dex = 0; dex < memKeys.length; dex++) {
            OfficeMember toAdd = new OfficeMember(memKeys[dex], posNames[dex]);
            officeMembers.add(toAdd);
        }
    }

    /**
     * Queries the database for this user's key just to see if it exists. As stated before with the
     * position, null values can cause problems. This is a helper method that is intended to make
     * failure more traceable when parsing.
     *
     * @param userKey a key of the user who's existence is in question
     * @return true if the user is in the database, false if not
     */
    private boolean userExists(String userKey) {
        //request from  database using key, if the database returns "Could not get user", return
        // false. Otherwise, either an exception is thrown or returns true.
        return true;
    }

    /**
     * Adds a user as a member of this office
     *
     * @param toAdd the key of the proposed member to add.
     * @param pos   the name of the position being added.
     * @throws Exception position name cannot be null. Even though we have not discussed a system for adding or
     *                   parsing the database for this info, a null position is literally asking for problems.
     */
    public void addToOffice(String toAdd, String pos) throws Exception {
        //if the position spot in null
        if (pos == null) {
            throw new Exception();
        }
        OfficeMember temp = new OfficeMember(pos, toAdd);
        officeMembers.add(temp);
    }

    /**
     * This method is used to put the data back together, to store on the server. When we get the
     * data originally, we got it in a string format, and used a delimiter for a character that's
     * rarely used, and parsed the data into an office object; it's like unzipping a jacket. This
     * method "zips" the jacket back up for storing on the database.
     *
     * @return String[0] is the member Keys, and String[1] is the list of positions
     */
    public String[] ServerData() {
        String[] data = new String[2];
        //does the first member
        data[0] = officeMembers.get(0).getPosHolder();
        data[1] = officeMembers.get(0).getPosName();

        for (int dex = 1; dex < officeMembers.size(); dex++) {
            data[0] += (del + officeMembers.get(dex).getPosHolder());
            data[1] += (del + officeMembers.get(dex).getPosName());
        }
        return data;
    }

    /**
     * gets the arraylist of members in this office
     *
     * @return list of members in this office
     */
    public ArrayList<OfficeMember> getOfficeMembers() {
        return officeMembers;
    }

    /**
     * gets the name of the office.
     *
     * @return the name of the office
     */
    public String getOfficeName() {
        return officeName;
    }

    /**
     * gets the key of the administrator
     *
     * @return the admin key
     */
    public String getOfficeAdminKey() {
        return officeAdminKey;
    }

    /**
     * gets the address of the office.
     *
     * @return the office address
     */
    public String getOfficeAddress() {
        return officeAddress;
    }

    /**
     * gets the description of the office
     *
     * @return the office description
     */
    public String getOfficeDescription() {
        return officeDescription;
    }
}
