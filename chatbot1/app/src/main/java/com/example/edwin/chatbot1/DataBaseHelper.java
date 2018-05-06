package com.example.edwin.chatbot1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Edwin on 09/04/2018.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String database_name = "Mavis.db";

    private String table_name = "Carer";

    private static final  int database_version = 1;


    private String CreateTableStatement =  "CREATE TABLE Carer(ID int, FirstName VARCHAR, LastName VARCHAR, PhoneNumber VARCHAR, EmailAddress VARCHAR, Address VARCHAR,UserName VARCHAR, EncryptedPassword VARCHAR);";
    private String CreateTableStatement2 =  "CREATE TABLE Contacts(ID INTEGER PRIMARY KEY   AUTOINCREMENT, FirstName VARCHAR, LastName VARCHAR, PhoneNumber VARCHAR, EmailAddress VARCHAR, Address VARCHAR);";
    private String CreateTableStatement3 =  "CREATE TABLE Med(ID INTEGER PRIMARY KEY   AUTOINCREMENT, DrugType VARCHAR, ToBeTaken DATETIME, PrescribedBy VARCHAR, SurgeryNumber VARCHAR);";
    private String CreateTableStatement4 =  "CREATE TABLE Memories(ID INTEGER PRIMARY KEY   AUTOINCREMENT, Name VARCHAR, Picture BLOB);";
    private String CreateTableStatement5 =  "CREATE TABLE AllChat(ID INTEGER PRIMARY KEY   AUTOINCREMENT, Content VARCHAR , atTime DATETIME DEFAULT CURRENT_TIMESTAMP);";

    public DataBaseHelper(Context context){
        super(context, database_name,null,database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    sqLiteDatabase.execSQL(CreateTableStatement);
    sqLiteDatabase.execSQL(CreateTableStatement2);
    sqLiteDatabase.execSQL(CreateTableStatement3);
    sqLiteDatabase.execSQL(CreateTableStatement4);
    sqLiteDatabase.execSQL(CreateTableStatement5);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
