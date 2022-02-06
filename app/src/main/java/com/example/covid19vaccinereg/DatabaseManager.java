package com.example.covid19vaccinereg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.renderscript.Sampler;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "VaccineDB";
    private static final int DATABASE_VERSION = 1;
    //TABLE FOR ACCOUNT
    private static final String TABLE_ACCOUNT = "account";
    //COLUMN FOR ACCOUNT
    private static final String KEY_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role";
    private static final String COLUMN_IS_VACCINE_REGISTERED = "isvaccineregistered";
    //TABLE FOR VACCINE REGISTRATION
    private static final String TABLE_REGISTER = "registration";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_IDENTIFICATION = "identification";
    private static final String COLUMN_STATE = "state";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_REGISTER_DATE = "registerDate";
    //TABLE FOR NEWS
    private static final String TABLE_NEWS = "news";
    private static final String COLUMN_NEWS_ID = "newsID";
    private static final String COLUMN_NEWS_IMAGE = "newsImage";
    private static final String COLUMN_NEWS_HEADER = "newsHeader";
    private static final String COLUMN_NEWS_LINK =  "newsLink";
    private static final String COLUMN_NEWS_TEXT = "newsContent";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Table for Account
        String Taccount = "CREATE TABLE " + TABLE_ACCOUNT + " ( \n" +
                " " + KEY_ID + " INTEGER NOT NULL CONSTRAINT account_pk PRIMARY KEY AUTOINCREMENT , \n" +
                " " + COLUMN_USERNAME + " varchar(100) NOT NULL, \n" +
                " " + COLUMN_PASSWORD + " varchar(100) NOT NULL, \n" +
                " " + COLUMN_ROLE + " INTEGER(1) NOT NULL, \n" +
                " " + COLUMN_IS_VACCINE_REGISTERED + " INTEGER(1) NOT NULL\n" + ");";

        //Table for Vaccine Registration
        String Tregister = "CREATE TABLE " + TABLE_REGISTER + " ( \n" +
                " " + KEY_ID + " INTEGER NOT NULL CONSTRAINT registration_pk PRIMARY KEY, \n" +
                " " + COLUMN_NAME + " char(100) NOT NULL, \n" +
                " " + COLUMN_AGE + " INTEGER(100) NOT NULL, \n" +
                " " + COLUMN_IDENTIFICATION + " varchar(12) NOT NULL, \n" +
                " " + COLUMN_PHONE + " INTEGER(12) NOT NULL, \n" +
                " " + COLUMN_STATE + " char(100) NOT NULL, \n" +
                " " + COLUMN_EMAIL + " varchar(100) NOT NULL, \n" +
                " " + COLUMN_ADDRESS + " varchar(200) NOT NULL, \n" +
                " " + COLUMN_REGISTER_DATE + " datetime NOT NULL, \n" + "FOREIGN KEY (" + KEY_ID + ") REFERENCES " + TABLE_ACCOUNT + " (" + KEY_ID + "));";

        //Table for News
        String Tnews = "CREATE TABLE " + TABLE_NEWS + " ( \n" +
                " " + COLUMN_NEWS_ID + " INTEGER NOT NULL CONSTRAINT news_pk PRIMARY KEY AUTOINCREMENT , \n" +
                " " + COLUMN_NEWS_HEADER + " varchar(100) NOT NULL, \n" +
                " " + COLUMN_NEWS_IMAGE + " BLOB NOT NULL, \n" +
                " " + COLUMN_NEWS_LINK + " varchar(100) NOT NULL, \n" +
                " " + COLUMN_NEWS_TEXT + " varchar(400) NOT NULL\n" + ");";

        db.execSQL(Taccount);
        db.execSQL(Tregister);
        db.execSQL(Tnews);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String account = "DROP TABLE IF EXISTS " + TABLE_ACCOUNT + ";";
        String register = "DROP TABLE IF EXISTS " + TABLE_REGISTER + ";";
        String news = "DROP TABLE IF EXISTS " + TABLE_NEWS + ";";
        db.execSQL(account);
        db.execSQL(register);
        db.execSQL(news);
        onCreate(db);
    }
    //add new account
    boolean addAccount(String username, String password, int role, int isvaccineregistered) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_ROLE, role);
        contentValues.put(COLUMN_IS_VACCINE_REGISTERED, isvaccineregistered);
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_ACCOUNT, null, contentValues) != 1;
    }

    //add default admin
    boolean addDefaultAdmin()
    {
        ContentValues c = new ContentValues();
        c.put(COLUMN_USERNAME,"Admin");
        c.put(COLUMN_PASSWORD,"1234");
        c.put(COLUMN_ROLE,1);
        c.put(COLUMN_IS_VACCINE_REGISTERED,0);
        SQLiteDatabase db=getWritableDatabase();
        return db.insert(TABLE_ACCOUNT,null,c) != -1;

    }
    //check if username exists in database
    boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM account WHERE username=?", new String[]{username});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    //check if username and password match in database
    boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM account WHERE username=? AND password=?", new String[]{username, password});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    //check if user is an admin
    boolean checkisAdmin(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM account WHERE username=?", new String[]{username});
        if (cursor.moveToFirst()) {
            if (cursor.getInt(cursor.getColumnIndex("role")) == 0)
                return false; // normal user
            else
                return true; // admin user
        } else {
            return false;
        }
    }

    //retrive all accounts except for the currently active account
    Cursor getAllAccounts(String username) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ACCOUNT + " WHERE "+ COLUMN_USERNAME + " !=?", new String[]{username});
    }

    //get all admin accounts except for the current account
    Cursor getAdminsAccounts(String thisUser) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ACCOUNT + " WHERE role=1 AND username!=?", new String[]{thisUser});
    }

    //get all non admin accounts
    Cursor getNonAdminsAccounts() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ACCOUNT + " WHERE role=0", null);
    }

    //get all accounts registered for the vaccine
    Cursor getRegisteredAccounts(String thisUser) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ACCOUNT + " WHERE isvaccineregistered=1 AND username!=?", new String[]{thisUser});
    }

    //get all accounts not registered for the vaccine
    Cursor getUnregisteredAccounts(String thisUser) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ACCOUNT + " WHERE isvaccineregistered=0 AND username!=?", new String[]{thisUser});
    }

    //retrieve the user id for the username passed into the method
    Cursor getUserId(String username) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT id FROM account WHERE username =?", new String[]{username});
    }

    //retrieve vaccine registration information for user
    Cursor getAccountWithVaccRegInfo(String UserId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM registration WHERE id =?", new String[]{UserId});
    }

    //get users information that is searched using Like function except for the current user
    Cursor getSearchedUser(String username,String thisUser) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM account WHERE username LIKE ? AND username !=?", new String[]{username + "%",thisUser});
    }

    //Add user information into registration table after they register for vaccine
    boolean addUserForVaccineReg(int id, String name, int age, String identification, String phone, String state, String email, String address, String date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ID, id);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_AGE, age);
        contentValues.put(COLUMN_IDENTIFICATION, identification);
        contentValues.put(COLUMN_PHONE, phone);
        contentValues.put(COLUMN_STATE, state);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_ADDRESS,address);
        contentValues.put(COLUMN_REGISTER_DATE, date);
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_REGISTER, null, contentValues) != -1;
    }

    //convert user to admin
    boolean makeAdmin(String UserID)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(COLUMN_ROLE,1);
        return db.update(TABLE_ACCOUNT, c, KEY_ID + "=?", new String[]{UserID}) == 1;
    }
    //remove user form admin
    boolean makeNormalUser(String UserID)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(COLUMN_ROLE,0);
        return db.update(TABLE_ACCOUNT, c, KEY_ID + "=?", new String[]{UserID}) == 1;
    }
    //make user registered
    boolean changeToRegistered(int UserID)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(COLUMN_IS_VACCINE_REGISTERED,1);
        return db.update(TABLE_ACCOUNT, c, KEY_ID + "=?", new String[]{String.valueOf(UserID)}) == 1;
    }

    //get account information for username
    Cursor getAccountInfo(String username){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM account WHERE username=?",new String[]{username});
    }

    //retrive all news from database
    Cursor getAllNews()
    {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM news ",null);
    }
    //check if news id matches in database
    boolean checkIfNewsExists(int nId)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM news WHERE newsID=?",new String[]{String.valueOf(nId)});
        return cursor.moveToFirst();
    }

    //retrieve news from the news id
    Cursor getNewsFromId(int nId)
    {
        SQLiteDatabase db = getReadableDatabase();
        return  db.rawQuery("SELECT * FROM news WHERE newsID=?",new String[]{String.valueOf(nId)});
    }
    //add new news
    boolean addNews(String newsHeader, Bitmap newsImageBitmap, String newsLink, String newsText){
        SQLiteDatabase db = getWritableDatabase();
        //convert image into bytearray to store in database
        ByteArrayOutputStream newsImageByteArray = new ByteArrayOutputStream();
        newsImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, newsImageByteArray);
        byte[] newsImageByte = newsImageByteArray.toByteArray();
        ContentValues c = new ContentValues();
        c.put(COLUMN_NEWS_IMAGE,newsImageByte);
        c.put(COLUMN_NEWS_HEADER,newsHeader);
        c.put(COLUMN_NEWS_LINK,newsLink);
        c.put(COLUMN_NEWS_TEXT,newsText);
        return db.insert(TABLE_NEWS, null, c) != -1;
    }

    //update existing news
    boolean updateNews(int nId, String newsHeader, Bitmap newsImageBitmap, String newsLink, String newsText)
    {
        SQLiteDatabase db = getWritableDatabase();
        ByteArrayOutputStream newsImageByteArray = new ByteArrayOutputStream();
        newsImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, newsImageByteArray);
        byte[] newsImageByte = newsImageByteArray.toByteArray();
        ContentValues c = new ContentValues();
        c.put(COLUMN_NEWS_IMAGE,newsImageByte);
        c.put(COLUMN_NEWS_HEADER,newsHeader);
        c.put(COLUMN_NEWS_LINK,newsLink);
        c.put(COLUMN_NEWS_TEXT,newsText);
        return db.update(TABLE_NEWS, c,COLUMN_NEWS_ID +"=?",new String[]{String.valueOf(nId)}) == 1;
    }

    //delete news from database
    boolean deleteNews(int NewsId)
    {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NEWS, COLUMN_NEWS_ID + "=?", new String[]{String.valueOf(NewsId)}) ==1;
    }

    //change username
    boolean updateNewUsername(int id, String username){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(COLUMN_USERNAME,username);
        return db.update(TABLE_ACCOUNT,c,KEY_ID + "=?", new String[]{String.valueOf(id)})==1;
    }

    //chaneg password for user
    boolean updateNewPassword(int id, String password){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(COLUMN_PASSWORD,password);
        return db.update(TABLE_ACCOUNT,c,KEY_ID + "=?", new String[]{String.valueOf(id)})==1;
    }

}

