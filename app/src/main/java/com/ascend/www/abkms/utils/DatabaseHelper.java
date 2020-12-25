/*
package com.ascend.www.abkms.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;


*/
/**
 * Created by ravi on 15/03/18.
 *//*


public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "indiana";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CLIENTS = "clients";
    private static final String TABLE_PLANTS = "plant";
    private static final String TABLE_LOCATIONS = "location";
    private static final String KEY_ID = "id";
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_CLIENT_NAME = "client_name";
    private static final String KEY_PLANT_ID = "plant_id";
    private static final String KEY_PLANT_NAME = "plant_name";
    private static final String KEY_LOCATION_ID = "location_id";
    private static final String KEY_LOCATION_NAME = "location_name";

    */
/*CREATE TABLE students ( id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone_number TEXT......);*//*


    private static final String CREATE_TABLE_CLIENTS = "CREATE TABLE "
            + TABLE_CLIENTS + "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," +  KEY_CLIENT_NAME + " TEXT );";

    private static final String CREATE_TABLE_PLANTS = "CREATE TABLE "
            + TABLE_PLANTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_CLIENT_ID + " TEXT," + KEY_PLANT_NAME + " TEXT );";

    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE "
            + TABLE_LOCATIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CLIENT_ID + " TEXT," + KEY_LOCATION_NAME + " TEXT );";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.d("table", CREATE_TABLE_CLIENTS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_CLIENTS);
        db.execSQL(CREATE_TABLE_PLANTS);
        db.execSQL(CREATE_TABLE_LOCATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_CLIENTS + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_PLANTS + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_LOCATIONS + "'");
        onCreate(db);
    }

    public void UpdateMasters(){

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_CLIENTS + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_PLANTS + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_LOCATIONS + "'");
        onCreate(db);


    }

    public void addClients(String client_id, String client_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        //adding user name in users table
        ContentValues values_Clients = new ContentValues();
        values_Clients.put(KEY_CLIENT_NAME, client_name);
         db.insert(TABLE_CLIENTS, null, values_Clients);
        //long id = db.insertWithOnConflict(TABLE_CLIENTS, null, values_Clients, SQLiteDatabase.CONFLICT_IGNORE);


    }

    public void addPlant(String client_id,String plant_id,String plant_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        //adding user name in users table

        //adding user plant in users_plants table
        ContentValues valuesPlants = new ContentValues();
        valuesPlants.put(KEY_CLIENT_ID, client_id);
        valuesPlants.put(KEY_PLANT_NAME, plant_name);

        db.insert(TABLE_PLANTS, null, valuesPlants);

        //long id = db.insertWithOnConflict(TABLE_PLANTS, null, valuesPlants, SQLiteDatabase.CONFLICT_IGNORE);


    }

    public void addLocation(String client_id,  String location_id, String location_name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valuesLocation = new ContentValues();
        valuesLocation.put(KEY_CLIENT_ID, client_id);
        valuesLocation.put(KEY_LOCATION_NAME, location_name);

        db.insert(TABLE_LOCATIONS, null, valuesLocation);

        //long id = db.insertWithOnConflict(TABLE_LOCATIONS, null, valuesLocation, SQLiteDatabase.CONFLICT_IGNORE);

    }

    public ArrayList<ClientModel> getAllClients() {
        ArrayList<ClientModel> clientModelArrayList = new ArrayList<ClientModel>();

        String selectQuery = "SELECT  * FROM " + TABLE_CLIENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ClientModel clientModel = new ClientModel();
                clientModel.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                clientModel.setClient_name(c.getString(c.getColumnIndex(KEY_CLIENT_NAME)));



                // adding to Students list
                clientModelArrayList.add(clientModel);
            } while (c.moveToNext());
        }
        return clientModelArrayList;
    }


    public ArrayList<ClientModel> getAllPlants(String ClientId) {
        ArrayList<ClientModel> plantModelArrayList = new ArrayList<ClientModel>();

        String selectPlantQuery = "SELECT  * FROM " + TABLE_PLANTS +" WHERE "+KEY_CLIENT_ID+" = "+ ClientId;
        Log.d("oppp",selectPlantQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cPlant = db.rawQuery(selectPlantQuery, null);
        // looping through all rows and adding to list
        if (cPlant.moveToFirst()) {
            do {
                ClientModel plantModel = new ClientModel();
                plantModel.setId(cPlant.getInt(cPlant.getColumnIndex(KEY_ID)));
                plantModel.setPlant_name(cPlant.getString(cPlant.getColumnIndex(KEY_PLANT_NAME)));
                plantModel.setClient_id(cPlant.getString(cPlant.getColumnIndex(KEY_CLIENT_ID)));
                //getting plant hobby where id = id from plant_hobby table




                // adding to Students list
                plantModelArrayList.add(plantModel);
            } while (cPlant.moveToNext());
        }
        return plantModelArrayList;
    }


    public ArrayList<ClientModel> getAllLocations(String ClientId) {
        ArrayList<ClientModel> LocationModelArrayList = new ArrayList<ClientModel>();

        String selectLocationQuery = "SELECT  * FROM " + TABLE_LOCATIONS +" WHERE "+KEY_CLIENT_ID+" = "+ ClientId;
        Log.d("oppp",selectLocationQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cLocation = db.rawQuery(selectLocationQuery, null);
        // looping through all rows and adding to list
        if (cLocation.moveToFirst()) {
            do {
                ClientModel LocationModel = new ClientModel();
                LocationModel.setId(cLocation.getInt(cLocation.getColumnIndex(KEY_ID)));
                LocationModel.setPlant_name(cLocation.getString(cLocation.getColumnIndex(KEY_LOCATION_NAME)));
                LocationModel.setClient_id(cLocation.getString(cLocation.getColumnIndex(KEY_CLIENT_ID)));
                //getting Location hobby where id = id from Location_hobby table




                // adding to Students list
                LocationModelArrayList.add(LocationModel);
            } while (cLocation.moveToNext());
        }
        return LocationModelArrayList;
    }

    public void updateUser(int id, String client_id, String client_name,String plant_id,String plant_name, String location_id, String location_name) {
        SQLiteDatabase db = this.getWritableDatabase();

        // updating name in users table
        ContentValues values = new ContentValues();
        values.put(KEY_CLIENT_NAME, client_name);

        db.update(TABLE_CLIENTS, values, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        // updating hobby in users_hobby table
        ContentValues valuesHobby = new ContentValues();
        valuesHobby.put(KEY_PLANT_NAME, plant_name);
        valuesHobby.put(KEY_CLIENT_ID, client_id);
        db.update(TABLE_PLANTS, valuesHobby, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        // updating city in users_city table
        ContentValues valuesCity = new ContentValues();
        valuesHobby.put(KEY_LOCATION_NAME, plant_name);
        valuesHobby.put(KEY_CLIENT_ID, client_id);
        db.update(TABLE_LOCATIONS, valuesCity, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteUSer(int id) {

        // delete row in students table based on id
        SQLiteDatabase db = this.getWritableDatabase();

        //deleting from users table
        db.delete(TABLE_CLIENTS, KEY_ID + " = ?",new String[]{String.valueOf(id)});

        //deleting from users_hobby table
        db.delete(TABLE_PLANTS, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        //deleting from users_city table
        db.delete(TABLE_LOCATIONS, KEY_ID + " = ?",new String[]{String.valueOf(id)});
    }

}
*/
