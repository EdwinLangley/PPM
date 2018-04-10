package com.example.edwin.chatbot1;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class CarerPanel extends AppCompatActivity {

    String FirstName = "";
    String LastName = "";
    String PhoneNumber = "";
    String EmailAddress = "";
    String Address = "";

    EditText FirstNameEditText;
    EditText LastNameEditText;
    EditText PhoneNumberEditText;
    EditText EmailAddressEditText;
    EditText AddressEditText;

    Button SaveButton;

    private DataBaseHelper dataBaseHelper;

    public SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_panel);
        setTitle("Carer Panel");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        FirstName = intent.getStringExtra("sendFirstName");
        LastName = intent.getStringExtra("sendLastName");
        PhoneNumber = intent.getStringExtra("sendPhoneNumber");
        EmailAddress = intent.getStringExtra("sendEmail");
        Address = intent.getStringExtra("sendAddress");

        dataBaseHelper = new DataBaseHelper(getApplicationContext());
        database = dataBaseHelper.getReadableDatabase();


        FirstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        LastNameEditText = (EditText) findViewById(R.id.lastNameEdittext);
        PhoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEdit);
        EmailAddressEditText = (EditText) findViewById(R.id.emailAddressEdit);
        AddressEditText = (EditText) findViewById(R.id.homeAddressEdit);

        setCarerDisplay();

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    public void setCarerDisplay(){
        Cursor cursor = null;
        String Query ="SELECT * FROM Carer";


        cursor = database.rawQuery(Query,null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                FirstNameEditText.setText(cursor.getString(cursor.getColumnIndex("FirstName")));
                LastNameEditText.setText(cursor.getString(cursor.getColumnIndex("LastName")));
                PhoneNumberEditText.setText(cursor.getString(cursor.getColumnIndex("PhoneNumber")));
                EmailAddressEditText.setText(cursor.getString(cursor.getColumnIndex("EmailAddress")));
                AddressEditText.setText(cursor.getString(cursor.getColumnIndex("Address")));

            } while (cursor.moveToNext());
            cursor.close();
        }

    }

    public void UpdateCarer(){
        Cursor cursor = null;
        String Query ="SELECT * FROM Carer";

        ContentValues values = new ContentValues();

        values.put("FirstName",FirstNameEditText.getText().toString());
        values.put("LastName",LastNameEditText.getText().toString());
        values.put("PhoneNumber",PhoneNumberEditText.getText().toString());
        values.put("EmailAddress",EmailAddressEditText.getText().toString());
        values.put("Address",AddressEditText.getText().toString());

        if(database != null) {

            int numberOfRows = database.update("Carer", values, "ID = 1", null);

            if (numberOfRows > 0) {
                Toast.makeText(CarerPanel.this, "Carer Updated", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CarerPanel.this, "Update Failed", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void updateCarerDetails(View view){
        UpdateCarer();

    }
}
