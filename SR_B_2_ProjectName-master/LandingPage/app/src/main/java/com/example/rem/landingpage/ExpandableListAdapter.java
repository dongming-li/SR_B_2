//When adding a group item, check getChildrenCount() and add an icon
// if things above are ignored, app will crash
//
//
//
//
package com.example.rem.landingpage;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * This class is the adapter for the expandablelistview
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
    /**
     * Context variable
     */
    private Context _context;
    /**
     * Store a list of main items in the expandablelistview navigation drawer
     */
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    /**
     * Store the child items in the main item in the expandablelistview navigation drawer
     */
    private HashMap<String, List<String>> _listDataChild;
    /**
     * Assign icon for group in expandablelistview navigation drawer
     */
    private List<Integer> groupIcon;
    /**
     * Assign icon for child item of group in expandablelistview navigation drawer
     */
    private List<Integer> childIcon;


    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    /**
     * Gets the data associated with the given child within the given group.
     * @param groupPosition
     * @param childPosititon
     * @return
     */
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    /**
     * Gets the ID for the given child within the given group
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Gets a View that displays the data for the given child within the given group.
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);
        childIcon = new ArrayList<Integer>();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);

        ImageView childView = (ImageView) convertView.findViewById(R.id.childIcon);
        childView.setImageResource(R.drawable.ic_arrow);
        txtListChild.setText(childText);
        return convertView;
    }

    /**
     * Gets the number of children in a specified group.
     * @param groupPosition
     * @return
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        if(groupPosition == 0 || groupPosition == 4 || groupPosition == 5 || groupPosition == 6 || groupPosition == 7)
        {
            return 0;
        }
        else {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        }
    }

    /**
     * Gets the data associated with the given group.
     * @param groupPosition
     * @return
     */
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    /**
     * Gets the number of groups.
     * @return
     */
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    /**
     * Gets the ID for the group at the given position.
     * @param groupPosition
     * @return
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Gets a View that displays the given group.
     * @param groupPosition
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        groupIcon = new ArrayList<Integer>();
        groupIcon.add(R.drawable.ic_menu_homepage);
        groupIcon.add(R.drawable.ic_menu_user);
        groupIcon.add(R.drawable.ic_menu_office);
        groupIcon.add(R.drawable.ic_menu_event);
        groupIcon.add(R.drawable.ic_menu_support);
        groupIcon.add(R.drawable.ic_menu_logout);
        groupIcon.add(R.drawable.ic_menu_bug);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.NORMAL);
        lblListHeader.setText(headerTitle);

        ImageView groupView = (ImageView) convertView.findViewById(R.id.groupIcon);
        int groupId = this.groupIcon.get(groupPosition);
        groupView.setImageResource(groupId);

        return convertView;
    }

    /**
     * Indicates whether the child and group IDs are stable across changes to the underlying data.
     * @return
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Whether the child at the specified position is selectable.
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
