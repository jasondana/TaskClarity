package com.reductivetech.taskclarity.page;

public class TaskListItem {

    private String _title;
    private String _value;

    public TaskListItem(String title, String value) {
        _title = title;
        _value = value;
    }

    public String getTitle() {
        return _title;
    }

    public String getValue() {
        return _value;
    }
}
