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


    private String CreateTableStatement =  "CREATE TABLE Carer(ID int, FirstName VARCHAR, LastName VARCHAR, PhoneNumber VARCHAR, EmailAddress VARCHAR, Address VARCHAR);";

    public DataBaseHelper(Context context){
        super(context, database_name,null,database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    sqLiteDatabase.execSQL(CreateTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
