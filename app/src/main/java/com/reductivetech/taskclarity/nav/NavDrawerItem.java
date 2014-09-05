package com.reductivetech.taskclarity.nav;

public class NavDrawerItem {

    private String _title;
    private int _icon;

    public NavDrawerItem(String title, int icon) {
        _title = title;
        _icon = icon;
    }

    public String getTitle(){
        return _title;
    }

    public int getIcon(){
        return _icon;
    }

}