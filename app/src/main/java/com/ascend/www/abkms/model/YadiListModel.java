package com.ascend.www.abkms.model;

public class YadiListModel {

    String id,filter_by,list_name,actual_name,list_count;

    public YadiListModel() {
    }

    public YadiListModel(String filter_by, String list_name, String actual_name, String list_count) {
        this.id = id;
        this.filter_by = filter_by;
        this.list_name = list_name;
        this.actual_name = actual_name;
        this.list_count = list_count;


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilter_by() {
        return filter_by;
    }

    public void setFilter_by(String filter_by) {
        this.filter_by = filter_by;
    }

    public String getList_name() {
        return list_name;
    }

    public void setList_name(String list_name) {
        this.list_name = list_name;
    }

    public String getActual_name() {
        return actual_name;
    }

    public void setActual_name(String actual_name) {
        this.actual_name = actual_name;
    }

    public String getList_count() {
        return list_count;
    }

    public void setList_count(String list_count) {
        this.list_count = list_count;
    }
}
