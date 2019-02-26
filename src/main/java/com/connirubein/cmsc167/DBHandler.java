package com.connirubein.cmsc167;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;

    //  database
    private static final String DATABASE_NAME = "cmsc167_db.db";

    //  person table
    private static final String TABLE_PERSON = "person";

    // person table column names
    private static final String per_COLUMN_PERSON_ID = "person_id";
    private static final String per_COLUMN_PERSON_USERNAME = "person_name";
    private static final String per_COLUMN_PERSON_PASSWORD = "person_password";

    // create person table sql query
    private String CREATE_PERSON_TABLE = "CREATE TABLE " + TABLE_PERSON + "("
            + per_COLUMN_PERSON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            per_COLUMN_PERSON_USERNAME + " TEXT, " +
            per_COLUMN_PERSON_PASSWORD + " TEXT" + ")";


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
    }
    //Lesson 51
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);

    }

    public boolean isUsernameExists(String username) {
        System.out.println("hello world");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PERSON,// Selecting Table
                new String[]{per_COLUMN_PERSON_ID, per_COLUMN_PERSON_USERNAME, per_COLUMN_PERSON_PASSWORD},//Selecting columns want to query
                per_COLUMN_PERSON_USERNAME + "=?",
                new String[]{username},//Where clause
                null, null, null);

        System.out.println("hello world 2");

        if (cursor != null && cursor.moveToFirst()&& cursor.getCount()>0) {
            //if cursor has value then in user database there is user associated with this given email so return true
            System.out.println("TRUEEEE");
            return true;
        }

        //if email does not exist return false
        System.out.println("FALSEEEE");
        return false;
    }

    public Person Authenticate(Person person) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PERSON,// Selecting Table
                new String[]{per_COLUMN_PERSON_ID, per_COLUMN_PERSON_USERNAME, per_COLUMN_PERSON_PASSWORD},//Selecting columns want to query
                per_COLUMN_PERSON_USERNAME + "=?",
                new String[]{person.userName},//Where clause
                null, null, null);

        if (cursor != null && cursor.moveToFirst()&& cursor.getCount()>0) {
            //if cursor has value then in user database there is user associated with this given email
            Person user1 = new Person(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));

            //Match both passwords check they are same or not
            if (person.password.equalsIgnoreCase(user1.password)) {
                return user1;
            }
        }

        //if user password does not match or there is no record with that email then return @false
        return null;
    }

    public void addPerson(Person person) {

        //get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //create content values to insert
        ContentValues values = new ContentValues();

        //Put username in  @values
        values.put(per_COLUMN_PERSON_USERNAME, person.userName);

        //Put password in  @values
        values.put(per_COLUMN_PERSON_PASSWORD, person.password);

        // insert row
        long todo_id = db.insert(TABLE_PERSON, null, values);
    }

    public String[] getAllNames(){
        String [] listOfNames;
        String query;
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();


        query = "SELECT " + per_COLUMN_PERSON_USERNAME + " FROM " + TABLE_PERSON + ";";


        Cursor recordSet = db.rawQuery(query, null);
        recordSet.moveToFirst();
        while (!recordSet.isAfterLast()) {
            if (recordSet.getString(recordSet.getColumnIndex(per_COLUMN_PERSON_USERNAME)) != null) {
                dbString += recordSet.getString(recordSet.getColumnIndex(per_COLUMN_PERSON_USERNAME));
                dbString += "\n";
            }
            recordSet.moveToNext();
        }
        db.close();
        listOfNames = dbString.split("\\r?\\n");
        return listOfNames;
    }

    }