package com.example.administrator.biodiversityapplication;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import com.example.administrator.biodiversityapplication.LatLong;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;

    //  database
    private static final String DATABASE_NAME = "newdbforbiodiv.db";

    //  record table
    public static final String TABLE_RECORDS = "records";

    //  person table
    private static final String TABLE_PERSON = "person";

    //location table name
    private static final String TABLE_LOC = "location";

    //  records table column names
    public static final String rec_COLUMN_ID = "record_id";
    public static final String rec_COLUMN_PERSON_ID = "person_id";
    public static final String rec_COLUMN_SPECIESNAME = "speciesname";
    public static final String rec_COLUMN_COMMONNAME = "commonname";
    public static final String rec_COLUMN_REMARKS = "remarks";
    public static final String rec_COLUMN_STATUS = "status";
    public static final String rec_COLUMN_IMAGE = "image";
    public static final String rec_COLUMN_DATETIME = "date_time";

    // person table column names
    private static final String per_COLUMN_PERSON_ID = "person_id";
    private static final String per_COLUMN_PERSON_USERNAME = "person_name";
    private static final String per_COLUMN_PERSON_EMAIL = "person_email";
    private static final String per_COLUMN_PERSON_PASSWORD = "person_password";

    //location table column names
    private static final String loc_COLUMN_LOC_ID = "location_id";
    private static final String loc_COLUMN_LOC_RECID = "record_id";
    private static final String loc_COLUMN_LOC_LAT = "latitude";
    private static final String loc_COLUMN_LOC_LONG = "longitude";

    // create person table sql query
    private String CREATE_PERSON_TABLE = "CREATE TABLE " + TABLE_PERSON + "("
            + per_COLUMN_PERSON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            per_COLUMN_PERSON_USERNAME + " TEXT, " +
            per_COLUMN_PERSON_EMAIL + " TEXT, " +
            per_COLUMN_PERSON_PASSWORD + " TEXT" + ")";

    //CREATE TABLE   TABLE_PERSON  (
    //            + per_COLUMN_PERSON_ID +  INTEGER PRIMARY KEY AUTOINCREMENT,  +
    //            per_COLUMN_PERSON_USERNAME +  TEXT,  +
    //            per_COLUMN_PERSON_EMAIL +  TEXT,  +
    //            per_COLUMN_PERSON_PASSWORD +  TEXT" + );

    //create record table sql query
    private String CREATE_RECORD_TABLE = "CREATE TABLE " + TABLE_RECORDS + "(" +
            rec_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            rec_COLUMN_PERSON_ID + " INTEGER ," +
            rec_COLUMN_SPECIESNAME + " TEXT ," +
            rec_COLUMN_COMMONNAME + " TEXT ," +
            rec_COLUMN_REMARKS + " TEXT ," +
            rec_COLUMN_IMAGE + " BLOB , " +
            rec_COLUMN_STATUS + " TEXT , " +
            rec_COLUMN_DATETIME + " TEXT " +

            ");";

    private String CREATE_LOC_TABLE = "CREATE TABLE " + TABLE_LOC + "(" + loc_COLUMN_LOC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + loc_COLUMN_LOC_LAT + " TEXT, " + loc_COLUMN_LOC_LONG + " TEXT, "
            + loc_COLUMN_LOC_RECID + " INTEGER, " + "FOREIGN KEY " + "(" + rec_COLUMN_ID + ")"
            + " REFERENCES " + TABLE_RECORDS + "(" + rec_COLUMN_ID + ")" + ")";


    //We need to pass database information along to superclass
    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PERSON_TABLE);
        db.execSQL(CREATE_RECORD_TABLE);
        db.execSQL(CREATE_LOC_TABLE);
    }
    //Lesson 51
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);

    }

    public Person Authenticate(Person person) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PERSON,// Selecting Table
                new String[]{per_COLUMN_PERSON_ID, per_COLUMN_PERSON_USERNAME, per_COLUMN_PERSON_EMAIL, per_COLUMN_PERSON_PASSWORD},//Selecting columns want to query
                per_COLUMN_PERSON_EMAIL + "=?",
                new String[]{person.email},//Where clause
                null, null, null);

        if (cursor != null && cursor.moveToFirst()&& cursor.getCount()>0) {
            //if cursor has value then in user database there is user associated with this given email
            Person user1 = new Person(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3));

            //Match both passwords check they are same or not
            if (person.password.equalsIgnoreCase(user1.password)) {
                return user1;
            }
        }

        //if user password does not match or there is no record with that email then return @false
        return null;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PERSON,// Selecting Table
                new String[]{per_COLUMN_PERSON_ID, per_COLUMN_PERSON_USERNAME, per_COLUMN_PERSON_EMAIL, per_COLUMN_PERSON_PASSWORD},//Selecting columns want to query
                per_COLUMN_PERSON_EMAIL + "=?",
                new String[]{email},//Where clause
                null, null, null);

        if (cursor != null && cursor.moveToFirst()&& cursor.getCount()>0) {
            //if cursor has value then in user database there is user associated with this given email so return true
            return true;
        }

        //if email does not exist return false
        return false;
    }

    //Add a new row to the database
    public Record addRecord(Record passedRecord){
        Record record = passedRecord;
        ContentValues values = new ContentValues();
        System.out.println("content");
        values.put(rec_COLUMN_PERSON_ID, passedRecord.get_recorderID());
        System.out.println("recorder id");
        values.put(rec_COLUMN_SPECIESNAME, passedRecord.get_speciesName());
        System.out.println("spec");
        values.put(rec_COLUMN_COMMONNAME, passedRecord.get_commonName());
        System.out.println("common");
        values.put(rec_COLUMN_REMARKS, passedRecord.get_remarks());
        System.out.println("remark");
        values.put(rec_COLUMN_IMAGE, passedRecord.get_imageData());
        System.out.println("img");
        values.put(rec_COLUMN_STATUS, passedRecord.get_status());
        System.out.println("stat");
        values.put(rec_COLUMN_DATETIME,passedRecord.get_datetime());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_RECORDS, null, values);
        db.close();

        record.set_id(getRecordIDFromDB(record));
        return record;

    }

    /**
     //     * This method is to create person record
     //     *
     //     * @param person
     //     */
    //using this method we can add persons to person table
    public void addPerson(Person person) {

        //get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //create content values to insert
        ContentValues values = new ContentValues();

        //Put username in  @values
        values.put(per_COLUMN_PERSON_USERNAME, person.userName);

        //Put email in  @values
        values.put(per_COLUMN_PERSON_EMAIL, person.email);

        //Put password in  @values
        values.put(per_COLUMN_PERSON_PASSWORD, person.password);

        // insert row
        long todo_id = db.insert(TABLE_PERSON, null, values);
    }

    //Delete a product from the database
    public void deleteProduct(String productName){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_RECORDS + " WHERE " + rec_COLUMN_SPECIESNAME + "=\"" + productName + "\";");
    }

    public int getPersonIDByEmail(String email){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PERSON + " WHERE " + per_COLUMN_PERSON_EMAIL + "=\"" + email + "\";",null);
        String [] commonNames = null;
        String dbString = "";
        System.out.println("email ---------------> "+email);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // null could happen if we used our empty constructor
            if (cursor.getString(cursor.getColumnIndex("person_id")) != null) {
                dbString += cursor.getString(cursor.getColumnIndex("person_id"));
                dbString += "\n";
            }
            cursor.moveToNext();
        }

        db.close();

        commonNames = dbString.split("\\r?\\n");
        System.out.println("retrieved info: " +commonNames);
        System.out.println("for loop:");
        for(int i=0; i<commonNames.length;i++){
            System.out.println("["+i+"] = "+commonNames[i]);
        }
        System.out.println("for loop done");

        return Integer.parseInt(commonNames[0]);
    }

    // this is goint in record_TextView in the Main activity.
    public String databaseToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_RECORDS + " WHERE 1";// why not leave out the WHERE  clause?

        //Cursor points to a location in your results
        Cursor recordSet = db.rawQuery(query, null);
        //Move to the first row in your results
        recordSet.moveToFirst();

        //Position after the last row means the end of the results
        while (!recordSet.isAfterLast()) {
            // null could happen if we used our empty constructor
            if (recordSet.getString(recordSet.getColumnIndex("productname")) != null) {
                dbString += recordSet.getString(recordSet.getColumnIndex("productname"));
                dbString += "\n";
            }
            recordSet.moveToNext();
        }
        db.close();
        return dbString;
    }

    public int getUserIDByEmail(String email){
        System.out.printf("getUserIDByEmail");
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_PERSON + " WHERE " + per_COLUMN_PERSON_EMAIL + "=\"" + email + "\";";
        Cursor cursor = db.rawQuery(query,null);
        String [] commonNames = null;
        String dbString = "";
        System.out.println("email ---------------> "+email);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // null could happen if we used our empty constructor
            if (cursor.getString(cursor.getColumnIndex(per_COLUMN_PERSON_ID)) != null) {
                dbString += cursor.getString(cursor.getColumnIndex(per_COLUMN_PERSON_ID));
                dbString += "\n";
            }
            cursor.moveToNext();
        }

        commonNames = dbString.split("\\r?\\n");
        System.out.println("retrieved info: " +commonNames);
        System.out.println("for loop:");
        for(int i=0; i<commonNames.length;i++){
            System.out.println("["+i+"] = "+commonNames[i]);
        }
        System.out.println("for loop done");
        return Integer.parseInt(commonNames[0]);

    }

    public String[] getNameRecords(String email, String nameType, String status){
        String [] listOfNames;
        String nameOfColumn = nameType;
        String query;
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        int person_id = getUserIDByEmail(email);


        query = "SELECT " + nameOfColumn + " FROM " + TABLE_RECORDS + " WHERE " + rec_COLUMN_STATUS + "= '"+ status +
                    "' AND " + rec_COLUMN_PERSON_ID + " = '"+ person_id + "';";


        Cursor recordSet = db.rawQuery(query, null);
        recordSet.moveToFirst();
        while (!recordSet.isAfterLast()) {
            if (recordSet.getString(recordSet.getColumnIndex(nameOfColumn)) != null) {
                dbString += recordSet.getString(recordSet.getColumnIndex(nameOfColumn));
                dbString += "\n";
            }
            recordSet.moveToNext();
        }
        db.close();
        listOfNames = dbString.split("\\r?\\n");
        return listOfNames;
    }

    public  ArrayList<Record> getAllRecordsByEmailStatus(String email, String status){
        Record editRecord;
        ArrayList<Record> listOfRecords = new ArrayList<Record>();
        String query;
        SQLiteDatabase db = getWritableDatabase();
        int person_id = getUserIDByEmail(email);

        query = "SELECT * FROM " + TABLE_RECORDS + " WHERE " + rec_COLUMN_STATUS + "= '"+ status +
                "' AND " + rec_COLUMN_PERSON_ID + " = '"+ person_id + "';";

        System.out.println("getAllRecordsByEmailStatus");
        System.out.println("query:--------------------> "+query);

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            if (cursor.getString(cursor.getColumnIndex("speciesname")) != null) {
                listOfRecords.add(new Record(cursor.getInt(cursor.getColumnIndex("person_id")),
                                            cursor.getString(cursor.getColumnIndex("speciesname")),
                                            cursor.getString(cursor.getColumnIndex("commonname")),
                                            cursor.getString(cursor.getColumnIndex("remarks")),
                                            cursor.getBlob(cursor.getColumnIndex("image")),
                                            cursor.getString(cursor.getColumnIndex("status")),
                                            cursor.getString(cursor.getColumnIndex("date_time"))
                                            ));
                editRecord = listOfRecords.get(listOfRecords.size()-1);
                editRecord.set_id(cursor.getInt(cursor.getColumnIndex("record_id")));
                listOfRecords.set(listOfRecords.size() - 1, editRecord);
            }
            cursor.moveToNext();
        }
        db.close();
        return listOfRecords;
    }

    public void updateRecordSave(Record oldRecord, Record newRecord) {
        String query = "UPDATE " +TABLE_RECORDS + " SET " +
                        rec_COLUMN_SPECIESNAME + "= '" + newRecord.get_speciesName() + "', " +
                        rec_COLUMN_COMMONNAME + "= '" + newRecord.get_commonName() + "', " +
                        rec_COLUMN_REMARKS + "= '" + newRecord.get_remarks() +"'" +
                        " WHERE " + rec_COLUMN_STATUS + "= '"+ oldRecord.get_status() +
                        "' AND " + rec_COLUMN_PERSON_ID + " = '"+ oldRecord.get_recorderID() +
                        "' AND " + rec_COLUMN_ID + " = '"+ oldRecord.get_id() +
                        "' AND " + rec_COLUMN_DATETIME + " = '"+ oldRecord.get_datetime() +
                        "';";
        System.out.println("query for update: "+query);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public int getRecordIDFromDB(Record record) {
        int record_id = 0;
        String query;
        SQLiteDatabase db = getWritableDatabase();

        query = "SELECT " + rec_COLUMN_ID + " FROM " + TABLE_RECORDS + " WHERE " +
                rec_COLUMN_PERSON_ID + "= '"+ record.get_recorderID() + "' AND " +
                rec_COLUMN_SPECIESNAME + "= '"+ record.get_speciesName() + "' AND " +
                rec_COLUMN_COMMONNAME + "= '"+ record.get_commonName() + "' AND " +
                rec_COLUMN_REMARKS + "= '"+ record.get_remarks() + "' AND " +
                rec_COLUMN_STATUS + "= '"+ record.get_status() + "';";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(cursor.getColumnIndex("record_id")) != null) {
                System.out.println("-----------------------------------------> IF");
                record_id = cursor.getInt(cursor.getColumnIndex("record_id"));
                System.out.println("-----------------------------------------> recordID: " + record_id);
            }
            cursor.moveToNext();
        }
        db.close();

        return record_id;
    }
    public static String getColumnLocLat() {
        return loc_COLUMN_LOC_LAT;
    }

    public static String getTableLoc() {
        return TABLE_LOC;
    }

    public static String getColumnLocLong() {

        return loc_COLUMN_LOC_LONG;
    }

    public void addLocation (LatLong location){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(loc_COLUMN_LOC_LAT, location.latitude);
        values.put(loc_COLUMN_LOC_LONG, location.longitude);
        values.put(loc_COLUMN_LOC_RECID, location.record_id);
        db.insert(TABLE_LOC, null, values);

        db.close();
    }

    public String getPersonNameByEmail(String email) {

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + per_COLUMN_PERSON_USERNAME + " FROM " + TABLE_PERSON + " WHERE " + per_COLUMN_PERSON_EMAIL + "=\"" + email + "\";",null);
        String [] commonNames = null;
        String dbString = "";
        System.out.println("email ---------------> "+email);
        cursor.moveToFirst();
        dbString = cursor.getString(cursor.getColumnIndex("person_name"));
        db.close();
        return dbString;

    }

    public void updateRecordSubmit(Record oldRecord, Record newRecord){
        String query = "UPDATE " +TABLE_RECORDS + " SET " +
                rec_COLUMN_SPECIESNAME + "= '" + newRecord.get_speciesName() + "', " +
                rec_COLUMN_COMMONNAME + "= '" + newRecord.get_commonName() + "', " +
                rec_COLUMN_REMARKS + "= '" + newRecord.get_remarks()  + "', " +
                rec_COLUMN_STATUS + "= '" + newRecord.get_status() + "'" +
                " WHERE " + rec_COLUMN_PERSON_ID + " = '"+ oldRecord.get_recorderID() +
                "' AND " + rec_COLUMN_ID + " = '"+ oldRecord.get_id() +
                "' AND " + rec_COLUMN_DATETIME + " = '"+ oldRecord.get_datetime() +
                "';";
        System.out.println("query for update: "+query);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public LatLong getLocationByRecordID(int record_id){
        LatLong location = null;

        String query = "SELECT * FROM " + TABLE_LOC + " WHERE " + loc_COLUMN_LOC_RECID + "= '"+ record_id + "';";

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            if (cursor.getString(cursor.getColumnIndex("location_id")) != null) {
//                record.set_id(cursor.getInt(cursor.getColumnIndex("record_id")));
//                record.set
                location = new LatLong(cursor.getInt(cursor.getColumnIndex("location_id")),
                        cursor.getInt(cursor.getColumnIndex("record_id")),
                        cursor.getString(cursor.getColumnIndex("latitude")),
                        cursor.getString(cursor.getColumnIndex("longitude"))
                );
            }
            cursor.moveToNext();
        }
        db.close();

        return location;
    }

    public ArrayList<LatLong> getLatLong(){

        ArrayList<LatLong> listOfLatLongs = new ArrayList<LatLong>();
        String query;
        SQLiteDatabase db = getWritableDatabase();
        //SELECT latitude, longitude FROM location
        //INNER JOIN records
        //ON location.record_id = records.record_id
        //where records.status = 'submitted';

        //or

        //SELECT latitude, longitude as LL
        // FROM location as LOC
        //INNER JOIN records AS REC
        //ON LOC.record_id = REC.record_id
        //where REC.status = 'submitted';

        query = "SELECT " + loc_COLUMN_LOC_LAT + ", " + loc_COLUMN_LOC_LONG +
                " FROM " + TABLE_LOC + " AS LOC "+
                " INNER JOIN " + TABLE_RECORDS + " AS REC "+
                " ON LOC.record_id = REC.record_id " +
                " WHERE REC.status = 'submitted';";

        System.out.println("getLatLong");
        System.out.println("query:--------------------> "+query);

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(cursor.getColumnIndex("latitude")) != null) {
                listOfLatLongs.add(new LatLong(
                        null,
                        null,
                        cursor.getString(cursor.getColumnIndex("latitude")),
                        cursor.getString(cursor.getColumnIndex("longitude"))
                ));
            }
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return listOfLatLongs;
    }
}