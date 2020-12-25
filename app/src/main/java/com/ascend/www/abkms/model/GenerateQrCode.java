package com.ascend.www.abkms.model;

/**
 * Created by ravi on 20/02/18.
 */

public class GenerateQrCode {
    public static final String TABLE_NAME = "qr_code";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CLIENT_NAME = "client_name";
    public static final String COLUMN_CLIENT_ID = "client_id";
    public static final String COLUMN_PLANT_NAME = "plant_name";
    public static final String COLUMN_PLANT_ID = "plant_id";
    public static final String COLUMN_LOCATION_NAME = "location_name";
    public static final String COLUMN_LOCATION_ID = "location_id";
    public static final String COLUMN_SERVICE_TYPE = "service_type";
    public static final String COLUMN_SERVICE_ID = "client_name";
    public static final String COLUMN_IGBT_NUM = "igbt_num";
    public static final String COLUMN_POWER_CARD_NUM = "power_card_num";
    public static final String COLUMN_QR_CODE_NUM = "qr_code_num";
    public static final String COLUMN_QR_CODE_IMG = "qr_code_img";

    private int id;

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

    public String getPlant_id() {
        return plant_id;
    }

    public void setPlant_id(String plant_id) {
        this.plant_id = plant_id;
    }

    public String getPlant_name() {
        return plant_name;
    }

    public void setPlant_name(String plant_name) {
        this.plant_name = plant_name;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getIgbt_num() {
        return igbt_num;
    }

    public void setIgbt_num(String igbt_num) {
        this.igbt_num = igbt_num;
    }

    public String getPower_card_num() {
        return power_card_num;
    }

    public void setPower_card_num(String power_card_num) {
        this.power_card_num = power_card_num;
    }

    public String getQr_code_num() {
        return qr_code_num;
    }

    public void setQr_code_num(String qr_code_num) {
        this.qr_code_num = qr_code_num;
    }

    public String getQr_code_img() {
        return qr_code_img;
    }

    public void setQr_code_img(String qr_code_img) {
        this.qr_code_img = qr_code_img;
    }

    private String client_id;
    private String client_name;
    private String plant_id;
    private String plant_name;
    private String location_id;
    private String location_name;
    private String service_type;
    private String service_id;
    private String igbt_num;
    private String power_card_num;
    private String qr_code_num;
    private String qr_code_img;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_CLIENT_NAME + " TEXT,"+ COLUMN_CLIENT_ID + " TEXT," + COLUMN_PLANT_NAME + " TEXT,"
                    + COLUMN_PLANT_ID + " TEXT," + COLUMN_LOCATION_NAME + " TEXT," + COLUMN_LOCATION_ID + " TEXT,"
                    + COLUMN_SERVICE_TYPE + " TEXT," + COLUMN_SERVICE_ID + " TEXT," + COLUMN_IGBT_NUM + " TEXT,"
                    + COLUMN_POWER_CARD_NUM + " TEXT," + COLUMN_QR_CODE_NUM + " TEXT," + COLUMN_QR_CODE_IMG + " BLOB"
                    + ")";

    public GenerateQrCode() {
    }

    public GenerateQrCode(int id, String client_id, String client_name, String plant_id,String plant_name, String location_id, String location_name,
                          String service_type,String service_id, String igbt_num, String power_card_num, String qr_code_num, String qr_code_img) {
        this.id = id;
        this.client_id = client_id;
        this.client_name = client_name;
        this.plant_id = plant_id;
        this.plant_name = plant_name;
        this.location_id = location_id;
        this.location_name = location_name;
        this.service_type = service_type;
        this.service_id = service_id;
        this.igbt_num = igbt_num;
        this.power_card_num = power_card_num;
        this.qr_code_num = qr_code_num;
        this.qr_code_img = qr_code_img;


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
