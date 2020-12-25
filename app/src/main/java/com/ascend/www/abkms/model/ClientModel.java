package com.ascend.www.abkms.model;

public class ClientModel {




    public static final String TABLE_NAME = "client";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CLIENT_NAME = "client_name";
    public static final String COLUMN_CLIENT_ID = "client_id";
    public static final String COLUMN_PLANT_ID = "plant_id";
    public static final String COLUMN_PLANT_NAME = "plant_name";
    public static final String COLUMN_LOCATION_ID = "locaion_id";
    public static final String COLUMN_LOCATION_NAME = "locaion_name";

    private int id;
    private String client_id;
    private String client_name;
    private String plant_name;
    private String plant_id;
    private String location_name;
    private String location_id;

    public String getPlant_name() {
        return plant_name;
    }

    public void setPlant_name(String plant_name) {
        this.plant_name = plant_name;
    }

    public String getPlant_id() {
        return plant_id;
    }

    public void setPlant_id(String plant_id) {
        this.plant_id = plant_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_CLIENT_ID + " TEXT,"+ COLUMN_CLIENT_NAME + " TEXT" + ")";

    public ClientModel() {
    }


    public ClientModel(int id, String client_id, String client_name) {
        this.id = id;
        this.client_id = client_id;
        this.client_name = client_name;
    }



}
