package com.example.edwin.chatbot1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.History;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.w3c.dom.Text;

import android.view.Menu;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;



public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final boolean TRACE_MODE = false;
    static String botName = "ppm";

    String resourcesPath;
    Bot bot;
    Chat chatSession;
    String textLine;
    EditText editContents;
    ListView messageList;
    ImageView timeImage;
    List<String> messageStrings;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> CarerProperties;
    boolean isDangerDialogOpen = false;
    boolean isFirstRun = false;
    final String key = "MAVISKNOW";
    final Security security = new Security();
    private DataBaseHelper dataBaseHelper;

    public SQLiteDatabase database;

    private Sensor mySensor;
    private SensorManager SM;
    private float[] accelArray = new float[3];
    float xDiff = 0, yDiff = 9.81f , zDiff = 0;
    int accuracyCounter = 0;

    MaxentTagger tagger;

// =====================================================================
// NAME: onCreate
// PURPOSE: This function always runs when the app is started up.
//          It is useful for setting views that will be used globally.
//          Separate threads that are required to run throughout the application
//          are also started here
// =====================================================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setCustomKeybaord();
        messageList= (ListView) findViewById(R.id.message_list);
        timeImage= (ImageView) findViewById(R.id.timeImage);


        messageStrings = new ArrayList<String>();

        setTitle("Mavis Personal Assistant");

        dataBaseHelper = new DataBaseHelper(getApplicationContext());
        database = dataBaseHelper.getReadableDatabase();

        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        try {
            TrainPos();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        //insertData();
        checkTimeThread();
        getCarerInfo();

        runbot();
    }

// =====================================================================
// NAME: onAccuracyChanged
// PURPOSE: Not used. However it is required to be included by android
//          to use the sensors
// =====================================================================


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use
    }

// =====================================================================
// NAME: onSensorChanged
// PURPOSE: Time based trigger caused by the Sensor manager. A normal delay
//          has been set. When this is triggered it compares the data from the
//          sensors last run to gather acceleration in any direction.
//          if this acceleration is greater than 3m/s^2 then this could likely relate
//          to a fall so is noted by the application.
// =====================================================================

    @Override
    public void onSensorChanged(SensorEvent event) {

        xDiff = accelArray[0] - event.values[0];
        yDiff = accelArray[1] - event.values[1];
        zDiff = accelArray[2] - event.values[2];

        if ((Math.abs(xDiff) > 8.0) || (Math.abs(yDiff) > 8.0) || (Math.abs(zDiff) > 8.0)){
            accuracyCounter++;
            if (accuracyCounter > 1) {
                addDangerDialog();
            }
        }

        accelArray[0] = event.values[0];
        accelArray[1] = event.values[1];
        accelArray[2] = event.values[2];

    }

// =====================================================================
// NAME: checkTimeThread
// PURPOSE: Used to change the position of the clock at the top of the screen
// =====================================================================

    private void checkTimeThread(){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Calendar rightNow;
                    while(true) {

                        rightNow = Calendar.getInstance();
                        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
                        switch(currentHour){
                            case 0:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.twelveam);
                                    }
                                });
                                break;
                            case 1:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.oneam);
                                    }
                                });
                            break;
                            case 2:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.twoam);
                                    }
                                });
                            break;
                            case 3:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.threeam);
                                    }
                                });
                            break;
                            case 4:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.fouram);
                                    }
                                });
                            break;
                            case 5:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.fiveam);
                                    }
                                });
                            break;
                            case 6:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.sixam);
                                    }
                                });
                            break;
                            case 7:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.sevenam);
                                    }
                                });
                            break;
                            case 8:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.eightam);
                                    }
                                });
                            break;
                            case 9:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.nineam);
                                    }
                                });
                            break;
                            case 10:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.tenam);
                                    }
                                });
                            break;
                            case 11:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.elevenam);
                                    }
                                });
                            break;
                            case 12:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.twelveam);
                                    }
                                });
                            break;
                            case 13:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.onepm);
                                    }
                                });
                            break;
                            case 14:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.twopm);
                                    }
                                });
                            break;
                            case 15:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.threepm);
                                    }
                                });
                            break;
                            case 16:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.fourpm);
                                    }
                                });
                            break;
                            case 17:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.fivepm);
                                    }
                                });
                            break;
                            case 18:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.sixpm);
                                    }
                                });
                            break;
                            case 19:
                                runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    timeImage.setImageResource(R.drawable.sevenpm);
                                }
                            });
                            case 20:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.eightpm);
                                    }
                                });
                            break;
                            case 21:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.ninepm);
                                    }
                                });
                            break;
                            case 22:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.tenpm);
                                    }
                                });
                            break;
                            case 23:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeImage.setImageResource(R.drawable.elevenpm);
                                    }
                                });
                            break;

                        }

                        Cursor cursor = null;
                        String Query = "SELECT * FROM Med WHERE  ToBeTaken between (SELECT datetime('now')) AND (SELECT datetime('now','+1 hours'));";

                        final ArrayList<String> DrugWarningDetails = new ArrayList<>();

                        cursor = database.rawQuery(Query, null);

                        if (cursor != null && cursor.moveToFirst()) {
                            do {
                                DrugWarningDetails.add(cursor.getString(cursor.getColumnIndex("DrugType")));
                                DrugWarningDetails.add(cursor.getString(cursor.getColumnIndex("ToBeTaken")));
                                DrugWarningDetails.add(cursor.getString(cursor.getColumnIndex("PrescribedBy")));
                                DrugWarningDetails.add(cursor.getString(cursor.getColumnIndex("SurgeryNumber")));

                            } while (cursor.moveToNext());
                            cursor.close();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    final Dialog dialog = new Dialog(MainActivity.this);
                                    dialog.setContentView(R.layout.dialog_enquiredmedicine);

                                    TextView drugTypeTextView = (TextView) dialog.findViewById(R.id.DrugTypeTextView);
                                    TextView freqTextView = (TextView) dialog.findViewById(R.id.FreqTextView);
                                    TextView prescribedByTextView = (TextView) dialog.findViewById(R.id.PrescribedByTextView);

                                    drugTypeTextView.setText(DrugWarningDetails.get(0));
                                    freqTextView.setText(DrugWarningDetails.get(1));
                                    prescribedByTextView.setText(DrugWarningDetails.get(2));


                                    dialog.show();

                                }
                            });

                        }


                        sleep(50000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }

// =====================================================================
// NAME: getCarerInfo
// PURPOSE: queries the database for information about the carer
// =====================================================================

    private void getCarerInfo(){
        Cursor cursor = null;
        String Query ="SELECT * FROM Carer";

        CarerProperties = new ArrayList<>();

        cursor = database.rawQuery(Query,null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                CarerProperties.add(cursor.getString(cursor.getColumnIndex("FirstName")));
                CarerProperties.add(cursor.getString(cursor.getColumnIndex("LastName")));
                CarerProperties.add(cursor.getString(cursor.getColumnIndex("PhoneNumber")));
                CarerProperties.add(cursor.getString(cursor.getColumnIndex("EmailAddress")));
                CarerProperties.add(cursor.getString(cursor.getColumnIndex("Address")));
                CarerProperties.add(cursor.getString(cursor.getColumnIndex("UserName")));
                CarerProperties.add(cursor.getString(cursor.getColumnIndex("EncryptedPassword")));


            } while (cursor.moveToNext());
            cursor.close();
        } else {
            isFirstRun = true;
            messageStrings.add("Mavis: As this is the first time running the application on this device you will need to setup a carer username and password. Try create carer");
            setAdapt();
        }
    }

// =====================================================================
// NAME: insertData
// PURPOSE: Used as a test function to add data to the database
// =====================================================================

    private void insertData(){
        String name = "John";

        ContentValues values = new ContentValues();

        values.put("ID",1);
        values.put("FirstName","Jane");
        values.put("LastName","W");
        values.put("PhoneNumber","77878787");
        values.put("EmailAddress","a@b.com");
        values.put("Address","1 NTU");
        values.put("UserName","Jane");
        values.put("EncryptedPassword","FHDAACFVK");

        long rowId = database.insert("Carer",null,values);
        if (rowId != -1){
            Toast.makeText(MainActivity.this, "Success",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Error",Toast.LENGTH_LONG).show();
        }
    }

// =====================================================================
// NAME: onCreateOptionsMenu
// PURPOSE: Replaces the menu on the actionbar
// =====================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

// =====================================================================
// NAME: onOptionsItemSelected
// PURPOSE: Determines what to do when an option on the action bar has been clicked
// =====================================================================
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);
            final EditText UserNameField = (EditText) mView.findViewById(R.id.UserNameEditText);
            final EditText UserPassword = (EditText) mView.findViewById(R.id.PasswordEditText);
            final TextView warningTextView = (TextView) mView.findViewById(R.id.warningTextView);
            Button LoginButton = (Button) mView.findViewById(R.id.LoginButton);

            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();



            if(isFirstRun == false) {

                LoginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String encrypted = "";

                        if (UserPassword.getText().toString().length() > 7) {
                            encrypted = security.encrypt(UserPassword.getText().toString(), key);
                        }

                        if ((encrypted.equals(CarerProperties.get(6))) && (UserNameField.getText().toString().equals(CarerProperties.get(5)))) {
                            dialog.dismiss();
                            Intent carerPanelIntent = new Intent(MainActivity.this, CarerPanel.class);
                            carerPanelIntent.putExtra("sendFirstName", CarerProperties.get(0));
                            carerPanelIntent.putExtra("sendLastName", CarerProperties.get(1));
                            carerPanelIntent.putExtra("sendPhoneNumber", CarerProperties.get(2));
                            carerPanelIntent.putExtra("sendEmail", CarerProperties.get(3));
                            carerPanelIntent.putExtra("sendAddress", CarerProperties.get(4));
                            startActivity(carerPanelIntent);
                        } else {
                            warningTextView.setVisibility(View.VISIBLE);
                        }
                    }
                });

                dialog.show();
            } else {
                Toast.makeText(MainActivity.this,"You need to make a carer account first", Toast.LENGTH_LONG).show();
            }

        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

// =====================================================================
// NAME: runbot
// PURPOSE: Looks for the AIML files and trains a AIML chatbot from this.
//          Issues with the way android stores files caused us to store the
//          set up files as .zip
// =====================================================================

    public void runbot() {
        resourcesPath = getResourcesPath();
        System.out.println(resourcesPath);
        MagicBooleans.trace_mode = TRACE_MODE;
        File fileExt = new File(getExternalFilesDir(null).getAbsolutePath()+"/bots");

        if(!fileExt.exists())
        {
            ZipFileExtraction extract = new ZipFileExtraction();

            try
            {
                extract.unZipIt(getAssets().open("bots.zip"), getExternalFilesDir(null).getAbsolutePath()+"/");
            } catch (Exception e) { e.printStackTrace(); }
        }

        String path = getExternalFilesDir(null).getAbsolutePath();
        bot = new Bot("ppm",path );//resourcesPath);
        chatSession = new Chat(bot);
        bot.brain.nodeStats();
        textLine = "";
    }

// =====================================================================
// NAME: TrainPos
// PURPOSE: A similar training procedure to the AIML bot is seen here but
//          with a part of speech tagger. We also had to store as a zip file here
// =====================================================================

    public void TrainPos() throws IOException, ClassNotFoundException {
        resourcesPath = getResourcesPath();
        System.out.println(resourcesPath);
        File fileExt = new File(getExternalFilesDir(null).getAbsolutePath());


            ZipFileExtraction extract = new ZipFileExtraction();

            try
            {
                extract.unZipIt(getAssets().open("tag.zip"), getExternalFilesDir(null).getAbsolutePath() + "/");
            } catch (Exception e) { e.printStackTrace(); }

        String path = getExternalFilesDir(null).getAbsolutePath() + "/tag/left3words-wsj-0-18.tagger" ;
        tagger = new MaxentTagger(path);
    }


// =====================================================================
// NAME: setCustomKeybaord
// PURPOSE: As most of the demo will be in a AVD we did not want to waste
//          screen size with the virtual keyboard as we would have a physical
//          one connected. This function turns it off.
// =====================================================================

    public void setCustomKeybaord(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        editContents = (EditText) findViewById(R.id.edittext_chatbox);
        editContents.setInputType(InputType.TYPE_NULL);

        editContents.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            chatSend(editContents);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

    }

// =====================================================================
// NAME: setAdapt
// PURPOSE: Sets an array to update the listview
// =====================================================================


    public void setAdapt() {
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                messageStrings);

        messageList.setAdapter(arrayAdapter);

    }

// =====================================================================
// NAME: getResourcesPath
// PURPOSE: A file path helper for the AIML bot
// =====================================================================

    private static String getResourcesPath() {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        path = path.substring(0, path.length() - 2);
        System.out.println(path);
        String resourcesPath = path + File.separator  + "src" + File.separator + "main" + File.separator + "res";
        return resourcesPath;
    }


// =====================================================================
// NAME: addPhoto
// PURPOSE: This function is responsible for starting the camera intent
// =====================================================================

    private void addPhoto() {
        Intent pickPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(pickPhoto , 0);
    }

// =====================================================================
// NAME: onActivityResult
// PURPOSE: This function is triggered on closing the camera intent
//
// =====================================================================


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data );
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");

        nameMemorydialog(bitmap);

    }

// =====================================================================
// NAME: nameMemorydialog
// PURPOSE: This function is responsible for starting a dialog that allows the
//          user to label the memorory.
// =====================================================================

    private void nameMemorydialog(final Bitmap bitmap) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_newmemoryphoto);

        final ImageView fromCamera = (ImageView) dialog.findViewById(R.id.fromCameraview);
        final EditText nameOfMemory = (EditText) dialog.findViewById(R.id.MemoryNameField);
        Button button = (Button) dialog.findViewById(R.id.submitmemorybutton);

        fromCamera.setImageBitmap(bitmap);

        dialog.show();

        button.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {
                if((bitmap != null) && (!nameOfMemory.getText().toString().isEmpty()) ) {

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] img = bos.toByteArray();

                    if(addPhotoMemoryToDb(nameOfMemory.getText().toString() , img)){
                        dialog.dismiss();
                    } else {
                    }
                }
            }
        });


    }

// =====================================================================
// NAME: addPhotoMemoryToDb
// PURPOSE: Will proceed to add the memory from the name memory dialog to
//          the database
// =====================================================================


    private boolean addPhotoMemoryToDb(String nameOfMemory, byte[] img) {
        ContentValues values = new ContentValues();
        values.put("Name", nameOfMemory);
        values.put("Picture", img);

        long rowId = database.insert("Memories",null,values);
        if (rowId != -1){
            Toast.makeText(MainActivity.this, "Success",Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(MainActivity.this, "Error",Toast.LENGTH_LONG).show();
            return false;
        }

    }


// =====================================================================
// NAME: chatSend
// PURPOSE: Probably the most important function of the application.
//          Determines what functions are to be run dependant on content of
//          the string entered into the message box. It will either do this
//          by looking for words contained within the string or it will use
//          a point of speech tagger to try and determine where the data lies
//          within the sentence.
// =====================================================================

    public void chatSend(View view) {

        try {

            setHumanResponse();

            addChatToDatabase(textLine);

            String tagged = tagger.tagString(textLine);

            ArrayList<String> NLPWordArrayList = new ArrayList<String>();
            for (String word : tagged.split(" ")) {
                NLPWordArrayList.add(word);
            }

            if ((textLine == null) || (textLine.length() < 1)) {
                textLine = MagicStrings.null_input;
            }
            if (textLine.equals("q")) {
                System.exit(0);
            } else if (textLine.equals("wq")) {
                bot.writeQuit();
                System.exit(0);
            } else if (textLine.contains("current time")) {
                timeResponse();
            } else if (textLine.contains("emergency") || textLine.contains("Emergency")) {
                emergency();
            } else if (textLine.contains("carer") && textLine.contains("first name")) {
                carerDetailsResponse("FN");
            } else if (textLine.contains("carer") && textLine.contains("last name")) {
                carerDetailsResponse("LN");
            } else if (textLine.contains("carer") && textLine.contains("phone")) {
                carerDetailsResponse("PN");
            } else if (textLine.contains("carer") && textLine.contains("email")) {
                carerDetailsResponse("EA");
            } else if (textLine.contains("carer") && textLine.contains("address")) {
                carerDetailsResponse("A");
            } else if (textLine.contains("add") && textLine.contains("contact")) {
                ArrayList<String> arguments = new ArrayList<>();

                for (String word : NLPWordArrayList) {
                    if (word.contains("/NNP")) {
                        arguments.add(word.substring(0, word.length() - 4));
                    }
                }
                if (arguments.size() == 0){
                    addContactDialog();
                } else if(arguments.size() == 1){
                    addContactDialog(arguments.get(0));
                } else if(arguments.size() == 2){
                    addContactDialog(arguments.get(0),arguments.get(1));
                }

            } else if (textLine.contains("update") && textLine.contains("contact")) {
                ArrayList<String> arguments = new ArrayList<>();

                for (String word : NLPWordArrayList) {
                    if (word.contains("/NNP")) {
                        arguments.add(word.substring(0, word.length() - 4));
                    }
                }
                if (arguments.size() == 0){
                    updateContactDialogNoArgs();
                } else if(arguments.size() == 1){
                    updateContactDialog(arguments.get(0));
                } else if(arguments.size() == 2){
                    updateContactDialog(arguments.get(0),arguments.get(1));
                }

            } else if (textLine.contains("add") && textLine.contains("medication")) {
                addPrescriptionDialog();
            } else if (textLine.contains("calendar")) {
                addCalendarDialog();
            } else if (textLine.contains("delete contact")) {
                ArrayList<String> arguments = new ArrayList<>();

                for (String word : NLPWordArrayList) {
                    if (word.contains("/NNP")) {
                        arguments.add(word.substring(0, word.length() - 4));
                    }
                }
                deleteContact(arguments.get(0), arguments.get(1));

            } else if ((textLine.contains("medication")) && (textLine.contains("have"))) {
                ArrayList<String> arguments = new ArrayList<>();

                for (String word : NLPWordArrayList) {
                    if ((word.contains("/NNP")) && (!word.contains("Dr"))) {
                        arguments.add(word.substring(0, word.length() - 4));
                    }
                }
                addMedicineDialog(arguments.get(0));
            } else if ((textLine.contains("show")) && (textLine.contains("number"))) {
                ArrayList<String> arguments = new ArrayList<>();

                for (String word : NLPWordArrayList) {
                    if ((word.contains("/NNP")) && (!word.contains("Dr"))) {
                        arguments.add(word.substring(0, word.length() - 4));
                    }
                }
                if(arguments.size() == 1){
                    showContactNumber(arguments.get(0));
                } else if(arguments.size() == 2){
                    showContactNumber(arguments.get(0),arguments.get(1));
                }

            } else if ((textLine.contains("show")) && (textLine.contains("email"))) {
                ArrayList<String> arguments = new ArrayList<>();

                for (String word : NLPWordArrayList) {
                    if ((word.contains("/NNP")) && (!word.contains("Dr"))) {
                        arguments.add(word.substring(0, word.length() - 4));
                    }
                }
                if(arguments.size() == 1){
                    showContactEmail(arguments.get(0));
                } else if(arguments.size() == 2){
                    showContactEmail(arguments.get(0),arguments.get(1));
                }

            } else if ((textLine.contains("show")) && (textLine.contains("address"))) {
                ArrayList<String> arguments = new ArrayList<>();

                for (String word : NLPWordArrayList) {
                    if ((word.contains("/NNP")) && (!word.contains("Dr"))) {
                        arguments.add(word.substring(0, word.length() - 4));
                    }
                }
                if(arguments.size() == 1){
                    showContactAddress(arguments.get(0));
                } else if(arguments.size() == 2){
                    showContactAddress(arguments.get(0),arguments.get(1));
                }

            }else if ((textLine.contains("delete")) && (textLine.contains("prescription"))) {
                ArrayList<String> arguments = new ArrayList<>();

                for (String word : NLPWordArrayList) {
                    if ((word.contains("/NNP")) && (!word.contains("Dr"))) {
                        arguments.add(word.substring(0, word.length() - 4));
                    }
                }
                if(arguments.size() == 1){
                    deletePrescription(arguments.get(0));
                }

            } else if (textLine.contains("create") && textLine.contains("carer")) {
                addCarerLogin();
            } else if (textLine.contains("add") && textLine.contains("photo")) {
                addPhoto();
            }else if (textLine.contains("view") && textLine.contains("memories")) {
                showAllMemories();
            }else if (textLine.contains("export")) {
                startExportDialog();
            }else {
                AIMLFallBack();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// =====================================================================
// NAME: showContactNumber
// PURPOSE: Diaplays the relevant contact details
//
// =====================================================================

    private void showContactNumber(String first) {
        Cursor cursor = null;
        String Query ="SELECT * FROM Contacts WHERE FirstName = '" + first + "'";

        ArrayList<String> changeNameResults = new ArrayList<String>();

        cursor = database.rawQuery(Query,null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("PhoneNumber")));
            } while (cursor.moveToNext());
            cursor.close();

            if(changeNameResults.size() == 1){
                messageStrings.add("Mavis: " + first + "s phone number is " + changeNameResults.get(0));
            } else {
                messageStrings.add("Mavis: There were too many people with this first name");
            }

        } else {
            messageStrings.add("Mavis: I couldn't find this information");
            setAdapt();
        }


    }

// =====================================================================
// NAME: showContactNumber
// PURPOSE: Diaplays the relevant contact details
//
// =====================================================================

    private void showContactNumber(String first, String second) {
        Cursor cursor = null;
        String Query ="SELECT * FROM Contacts WHERE FirstName = '" + first + "' AND LastName = '" + second  + "'";

        ArrayList<String> changeNameResults = new ArrayList<String>();

        cursor = database.rawQuery(Query,null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("PhoneNumber")));
            } while (cursor.moveToNext());
            cursor.close();

            if(changeNameResults.size() == 1){
                messageStrings.add("Mavis: " + first + " " + second+ "s phone number is " + changeNameResults.get(0));
            }

        } else {
            messageStrings.add("Mavis: I couldn't find this information");
            setAdapt();
        }

    }

// =====================================================================
// NAME: showContactEmail
// PURPOSE: Diaplays the relevant contact details
//
// =====================================================================

    private void showContactEmail(String first) {
        Cursor cursor = null;
        String Query ="SELECT * FROM Contacts WHERE FirstName = '" + first + "'";

        ArrayList<String> changeNameResults = new ArrayList<String>();

        cursor = database.rawQuery(Query,null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("EmailAddress")));
            } while (cursor.moveToNext());
            cursor.close();

            if(changeNameResults.size() == 1){
                messageStrings.add("Mavis: " + first + "s Email is " + changeNameResults.get(0));
            } else {
                messageStrings.add("Mavis: There were too many people with this first name");
            }

        } else {
            messageStrings.add("Mavis: I couldn't find this information");
            setAdapt();
        }


    }

// =====================================================================
// NAME: showContactEmail
// PURPOSE: Diaplays the relevant contact details
//
// =====================================================================

    private void showContactEmail(String first, String second) {
        Cursor cursor = null;
        String Query ="SELECT * FROM Contacts WHERE FirstName = '" + first + "' AND LastName = '" + second  + "'";

        ArrayList<String> changeNameResults = new ArrayList<String>();

        cursor = database.rawQuery(Query,null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("EmailAddress")));
            } while (cursor.moveToNext());
            cursor.close();

            if(changeNameResults.size() == 1){
                messageStrings.add("Mavis: " + first + " " + second+ "s Email is " + changeNameResults.get(0));
            }

        } else {
            messageStrings.add("Mavis: I couldn't find this information");
            setAdapt();
        }

    }

// =====================================================================
// NAME: showContactAddress
// PURPOSE: Diaplays the relevant contact details
//
// =====================================================================

    private void showContactAddress(String first) {
        Cursor cursor = null;
        String Query ="SELECT * FROM Contacts WHERE FirstName = '" + first + "'";

        ArrayList<String> changeNameResults = new ArrayList<String>();

        cursor = database.rawQuery(Query,null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("Address")));
            } while (cursor.moveToNext());
            cursor.close();

            if(changeNameResults.size() == 1){
                messageStrings.add("Mavis: " + first + "s address is " + changeNameResults.get(0));
            } else {
                messageStrings.add("Mavis: There were too many people with this first name");
            }

        } else {
            messageStrings.add("Mavis: I couldn't find this information");
            setAdapt();
        }


    }

// =====================================================================
// NAME: showContactAddress
// PURPOSE: Diaplays the relevant contact details
//
// =====================================================================

    private void showContactAddress(String first, String second) {
        Cursor cursor = null;
        String Query ="SELECT * FROM Contacts WHERE FirstName = '" + first + "' AND LastName = '" + second  + "'";

        ArrayList<String> changeNameResults = new ArrayList<String>();

        cursor = database.rawQuery(Query,null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("Address")));
            } while (cursor.moveToNext());
            cursor.close();

            if(changeNameResults.size() == 1){
                messageStrings.add("Mavis: " + first + " " + second+ "s address is " + changeNameResults.get(0));
            }

        } else {
            messageStrings.add("Mavis: I couldn't find this information");
            setAdapt();
        }

    }

// =====================================================================
// NAME: updateContactDialog
// PURPOSE: Adds the current request from the user to the database
// =====================================================================

    private void updateContactDialog(final String first) {

        Cursor cursor = null;
        String Query ="SELECT * FROM Contacts WHERE FirstName = '" + first + "'";

        ArrayList<String> changeNameResults = new ArrayList<String>();

        cursor = database.rawQuery(Query,null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("FirstName")));
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("LastName")));
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("PhoneNumber")));
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("EmailAddress")));
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("Address")));
            } while (cursor.moveToNext());
            cursor.close();
        } else {

        }

        if (changeNameResults.size() == 5) {

            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialog_addcontact);

            final EditText FirstNameField = (EditText) dialog.findViewById(R.id.FirstNameEditText);
            final EditText LastNameField = (EditText) dialog.findViewById(R.id.LastNameEditText);
            final EditText PhoneField = (EditText) dialog.findViewById(R.id.PhoneEditText);
            final EditText EmailField = (EditText) dialog.findViewById(R.id.EmailEditText);
            final EditText AddressField = (EditText) dialog.findViewById(R.id.AddressEditText);
            final Button AddButton = (Button) dialog.findViewById(R.id.AddContactButton);
            final TextView title = (TextView) dialog.findViewById(R.id.AddContactTitle);

            title.setText("Edit Contact " + first);

            FirstNameField.setText(changeNameResults.get(0));
            LastNameField.setText(changeNameResults.get(1));
            PhoneField.setText(changeNameResults.get(2));
            EmailField.setText(changeNameResults.get(3));
            AddressField.setText(changeNameResults.get(4));



            FirstNameField.setFocusable(false);

            AddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues args = new ContentValues();
                    args.put("LastName",LastNameField.getText().toString());
                    args.put("PhoneNumber",PhoneField.getText().toString());
                    args.put("EmailAddress",EmailField.getText().toString());
                    args.put("Address",AddressField.getText().toString());

                    long rowId = database.update("Contacts", args, "FirstName = '" + first + "'",null);
                    if (rowId != -1){
                        Toast.makeText(MainActivity.this, "Entry Updated",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "Error Updating",Toast.LENGTH_LONG).show();
                    }
                }
            });

            dialog.show();


        } else if(changeNameResults.size() > 5){
            messageStrings.add("Mavis: There was more than one person in your contacts with this first name");
            setAdapt();
        } else if(changeNameResults.size() < 5){
            messageStrings.add("Mavis: There is nobody in your contacts with this first name");
            setAdapt();
        }

    }

// =====================================================================
// NAME: updateContactDialog
// PURPOSE: Provides a dialog that allows the user to change the details
//          of a contact when both tht first and last name are provided
// =====================================================================

    private void updateContactDialog(final String first, final String last) {
        Cursor cursor = null;
        String Query ="SELECT * FROM Contacts WHERE FirstName = '" + first + "'" + " AND LastName = '" + last + "'";

        ArrayList<String> changeNameResults = new ArrayList<String>();

        cursor = database.rawQuery(Query,null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("FirstName")));
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("LastName")));
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("PhoneNumber")));
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("EmailAddress")));
                changeNameResults.add(cursor.getString(cursor.getColumnIndex("Address")));
            } while (cursor.moveToNext());
            cursor.close();
        } else {

        }

        if (changeNameResults.size() == 5) {

            final Dialog TwoVarDialog = new Dialog(MainActivity.this);
            TwoVarDialog.setContentView(R.layout.dialog_addcontact);

            final EditText FirstNameField = (EditText) TwoVarDialog.findViewById(R.id.FirstNameEditText);
            final EditText LastNameField = (EditText) TwoVarDialog.findViewById(R.id.LastNameEditText);
            final EditText PhoneField = (EditText) TwoVarDialog.findViewById(R.id.PhoneEditText);
            final EditText EmailField = (EditText) TwoVarDialog.findViewById(R.id.EmailEditText);
            final EditText AddressField = (EditText) TwoVarDialog.findViewById(R.id.AddressEditText);
            final Button AddButton = (Button) TwoVarDialog.findViewById(R.id.AddContactButton);
            final TextView title = (TextView) TwoVarDialog.findViewById(R.id.AddContactTitle);

            title.setText("Edit Contact " + first);

            FirstNameField.setText(changeNameResults.get(0));
            LastNameField.setText(changeNameResults.get(1));
            PhoneField.setText(changeNameResults.get(2));
            EmailField.setText(changeNameResults.get(3));
            AddressField.setText(changeNameResults.get(4));



            FirstNameField.setFocusable(false);
            LastNameField.setFocusable(false);

            AddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues args = new ContentValues();
                    args.put("PhoneNumber",PhoneField.getText().toString());
                    args.put("EmailAddress",EmailField.getText().toString());
                    args.put("Address",AddressField.getText().toString());

                    long rowId = database.update("Contacts", args, "FirstName = '" + first + "' AND LastName = '" + last + "'" ,null);
                    if (rowId != -1){
                        Toast.makeText(MainActivity.this, "Entry Updated",Toast.LENGTH_LONG).show();
                        TwoVarDialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "Error Updating",Toast.LENGTH_LONG).show();
                    }
                }
            });

            TwoVarDialog.show();


        } else if(changeNameResults.size() > 5){
            messageStrings.add("Mavis: There was more than one person in your contacts with this first name");
            setAdapt();
        } else if(changeNameResults.size() < 5){
            messageStrings.add("Mavis: There is nobody in your contacts with this first name");
            setAdapt();
        }

    }

// =====================================================================
// NAME: updateContactDialogNoArgs
// PURPOSE: Provides a list of all contacts when the user does not
//          name one
// =====================================================================

    private void updateContactDialogNoArgs() {
        final Dialog allContactsDialog = new Dialog(MainActivity.this);
        allContactsDialog.setContentView(R.layout.dialog_allmemories);

        TextView allContactsTitle = (TextView) allContactsDialog.findViewById(R.id.MemoriesTitle);

        allContactsTitle.setText("All contacts");

        final ListView listView = (ListView) allContactsDialog.findViewById(R.id.MemoriesList);

        final ArrayList<String> MemoryNames = getAllContactsFromDB();

        final ArrayList<String> firstNames = new ArrayList<String>();
        final ArrayList<String> lastNames = new ArrayList<String>();
        ArrayList<String> comboNames = new ArrayList<String>();

        for (int i = 0; i < MemoryNames.size(); i = i+2){
            firstNames.add(MemoryNames.get(i));
            lastNames.add(MemoryNames.get(i+1));
            comboNames.add(MemoryNames.get(i) + " " + MemoryNames.get(i+1));
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                comboNames );

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                updateContactDialog(firstNames.get(i),lastNames.get(i));
            }
        });

        allContactsDialog.show();


    }

// =====================================================================
// NAME: getAllContactsFromDB
// PURPOSE: Gets all of the users names from the database
//
// =====================================================================

    private ArrayList<String> getAllContactsFromDB() {

        Cursor cursor = null;
        String Query ="SELECT * FROM Contacts";

        ArrayList<String> ContactProps = new ArrayList<>();

        cursor = database.rawQuery(Query,null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ContactProps.add(cursor.getString(cursor.getColumnIndex("FirstName")));
                ContactProps.add(cursor.getString(cursor.getColumnIndex("LastName")));
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Toast.makeText(MainActivity.this, "Try adding some contacts first", Toast.LENGTH_SHORT).show();
        }
        return ContactProps;
    }

// =====================================================================
// NAME: startExportDialog
// PURPOSE: Starts a dialog that allows the user to decide the timeframe they wish
//          to export to file.
// =====================================================================

    private void startExportDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_exportchat);

        final EditText fromDate = (EditText) dialog.findViewById(R.id.FromDate);
        final EditText todate = (EditText) dialog.findViewById(R.id.ToDateField);
        Button exportButton = (Button) dialog.findViewById(R.id.ExportButton);

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fromDateString = fromDate.getText().toString();
                String toDateString = todate.getText().toString();


                Cursor cursor = null;
                String Query = "select * from AllChat where `atTime` >= '" + fromDateString + "' and `atTime` <= '" + toDateString + "'";

                String sentences = "";

                cursor = database.rawQuery(Query, null);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        sentences+= " " + cursor.getString(cursor.getColumnIndex("Content"));
                    } while (cursor.moveToNext());
                    cursor.close();
                } else {
                    Toast.makeText(MainActivity.this, "There were no entries found", Toast.LENGTH_LONG).show();
                }
                if (!sentences.isEmpty()){
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/appLogs";
                    File file = new File(path+"/savedFile.txt");
                    writeToFile(sentences,MainActivity.this );
                    dialog.dismiss();
                }
            }
        });



        dialog.show();


    }

// =====================================================================
// NAME: writeToFile
// PURPOSE: When the user decides to export the chat to file this is
//          started
// =====================================================================

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("ChatOutput.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Toast.makeText(MainActivity.this,"Save to file was successful",Toast.LENGTH_LONG).show();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
// =====================================================================
// NAME: addChatToDatabase
// PURPOSE: Adds the current request to the database for later sentiment
//          analysis
// =====================================================================

    private void addChatToDatabase(String sentence) {


        ContentValues values = new ContentValues();

        values.put("Content",sentence);

        long rowId = database.insert("AllChat",null,values);
        if (rowId != -1){
//            Toast.makeText(MainActivity.this, "Success",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Error",Toast.LENGTH_LONG).show();
        }


    }

// =====================================================================
// NAME: showAllMemories
// PURPOSE: Links to the later function "getAllMemoriesFromDB", provides
//          a dialog to show all of the memories if the user doesn't say one
// =====================================================================

    private void showAllMemories() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_allmemories);

        final ListView listView = (ListView) dialog.findViewById(R.id.MemoriesList);

        final ArrayList<String> MemoryNames = getAllMemoryNamesFromDb();

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                MemoryNames );

        listView.setAdapter(arrayAdapter);

        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                final String selectedText = MemoryNames.get(position);
                if(!selectedText.isEmpty()){
                    byte[] image = getOneMemoryData(selectedText);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image , 0, image .length);

                    final Dialog innerDialog = new Dialog(MainActivity.this);
                    innerDialog.setContentView(R.layout.dialog_displaymemory);

                    ImageView imageView = (ImageView) innerDialog.findViewById(R.id.MemImageView);
                    Button editButton = (Button) innerDialog.findViewById(R.id.editMemoryButton);
                    final Button deleteButton = (Button) innerDialog.findViewById(R.id.deleteMemory);

                    imageView.setImageBitmap(bitmap);
                    TextView singleMemory = (TextView) innerDialog.findViewById(R.id.singleMemoryTitle);
                    singleMemory.setText("Your Memory: " + selectedText);

                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog EditDialog = new Dialog(MainActivity.this);
                            EditDialog.setContentView(R.layout.dialog_editname);

                            Button editNameButton = (Button) EditDialog.findViewById(R.id.saveNewNameButton);
                            final EditText newNameField = (EditText) EditDialog.findViewById(R.id.newNameEditText);



                            editNameButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final String newMemoryName = newNameField.getText().toString();
                                    if(!newMemoryName.isEmpty()) {
                                        editAMemoryName(selectedText, newMemoryName);
                                        EditDialog.dismiss();
                                        innerDialog.dismiss();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(MainActivity.this, "There was nothing in the EditText", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                            EditDialog.show();
                        }
                    });

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog deleteDialog = new Dialog(MainActivity.this);
                            deleteDialog.setContentView(R.layout.dialog_confirm);

                            Button yesConfirmButton = (Button) deleteDialog.findViewById(R.id.yesButton);
                            Button noConfirmButton = (Button) deleteDialog.findViewById(R.id.noButton);

                            yesConfirmButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    deleteAMemory(selectedText);
                                    Toast.makeText(MainActivity.this, "The memory has been deleted from the database", Toast.LENGTH_LONG).show();
                                    deleteDialog.dismiss();
                                    innerDialog.dismiss();
                                    dialog.dismiss();
                                }
                            });

                            noConfirmButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(MainActivity.this,"Okay, no worries!", Toast.LENGTH_LONG).show();
                                    deleteDialog.dismiss();
                                }
                            });

                            deleteDialog.show();
                        }
                    });

                    innerDialog.show();

                }
            }
        });
    }

// =====================================================================
// NAME: deleteAMemory
// PURPOSE: allows the user to delete a memory from that database
//
// =====================================================================

    public void deleteAMemory(String memName) {

        long rowId = database.delete("Memories", "Name =" + "'" + memName + "'",null );
        if (rowId != -1){
            Toast.makeText(MainActivity.this, "Entry Deleted",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Error Deleting",Toast.LENGTH_LONG).show();
        }



    }

// =====================================================================
// NAME: editAMemoryName
// PURPOSE: Allows the user to change the name of a memory
// =====================================================================

    public void editAMemoryName(String memName, String newMemName) {

        ContentValues args = new ContentValues();
        args.put("Name",newMemName);

        long rowId = database.update("Memories", args, "Name =" + "'" + memName + "'",null);
        if (rowId != -1){
            Toast.makeText(MainActivity.this, "Entry Updated",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Error Updating",Toast.LENGTH_LONG).show();
        }



    }

// =====================================================================
// NAME: getAllMemoryNamesFromDb
// PURPOSE: takes all of the memory names from the database so they can
//          be displayed in a listview
// =====================================================================

    private ArrayList<String> getAllMemoryNamesFromDb() {
        Cursor cursor = null;
        String Query ="SELECT Name FROM Memories";

        ArrayList<String> MemoryNames = new ArrayList<>();

        cursor = database.rawQuery(Query,null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                MemoryNames.add(cursor.getString(cursor.getColumnIndex("Name")));
            } while (cursor.moveToNext());
            cursor.close();
        } else {

        }

        return MemoryNames;

    }

// =====================================================================
// NAME: getOneMemoryData
// PURPOSE: gets the data associated with a memeory such as the photo
//
// =====================================================================

    private byte[] getOneMemoryData(String Name) {
        Cursor cursor = null;
        String Query ="SELECT * FROM Memories WHERE Name = '" + Name + "'";

        byte [] image = null;

        cursor = database.rawQuery(Query,null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                image = cursor.getBlob(cursor.getColumnIndex("Picture"));
            } while (cursor.moveToNext());
            cursor.close();
        } else {

        }

        return image;

    }

// =====================================================================
// NAME: AIMLFallBack
// PURPOSE: None of the other responses were appropriate for this application
//          the application will now rely on a AIML response
// =====================================================================

    public void AIMLFallBack() {

        String request = textLine;
        if (MagicBooleans.trace_mode)
            System.out.println("STATE=" + request + ":THAT=" + ((History) chatSession.thatHistory.get(0)).get(0) + ":TOPIC=" + chatSession.predicates.get("topic"));
        String response = chatSession.multisentenceRespond(request);
        while (response.contains("&lt;"))
            response = response.replace("&lt;", "<");
        while (response.contains("&gt;"))
            response = response.replace("&gt;", ">");
        messageStrings.add("Mavis: " + response);
        setAdapt();
    }

// =====================================================================
// NAME: setHumanResponse
// PURPOSE: Records what was said by the user so the listview looks like a conversation
// =====================================================================

    public void setHumanResponse() {

        System.out.print("Human : ");
        editContents = (EditText) findViewById(R.id.edittext_chatbox);
        textLine = editContents.getText().toString();
        editContents.setText("");
        messageStrings.add("Me: " + textLine);
        setAdapt();
    }

// =====================================================================
// NAME: timeResponse
// PURPOSE: Responds with the time if it is asked for.
// =====================================================================

    public void timeResponse() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        String response = dateFormat.format(date);
        messageStrings.add("Mavis: " +" The current date and time is:  "+ response);
        setAdapt();

    }

// =====================================================================
// NAME: emergency
// PURPOSE: Opens the dialer with the number of the carer
// =====================================================================

    public void emergency() {
        getCarerInfo();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+CarerProperties.get(2)));
        startActivity(intent);

    }

// =====================================================================
// NAME: carerDetailsResponse
// PURPOSE: Replies with appropriate information about the carer
// =====================================================================

    public void carerDetailsResponse(String flag) {
        getCarerInfo();
        if (flag.equals("FN")){
            messageStrings.add("Mavis: Your carer is  " + CarerProperties.get(0));
            setAdapt();
        } else if(flag.equals("LN")) {
            messageStrings.add("Mavis: Your carers second name is " + CarerProperties.get(1));
            setAdapt();
        } else if(flag.equals("PN")) {
            messageStrings.add("Mavis: Your carers phone number is " + CarerProperties.get(2));
            setAdapt();
        } else if(flag.equals("EA")) {
            messageStrings.add("Mavis: Your carers Email address is " + CarerProperties.get(3));
            setAdapt();
        } else if(flag.equals("A")) {
            messageStrings.add("Mavis: Your carers address is " + CarerProperties.get(4));
            setAdapt();
        }

    }

// =====================================================================
// NAME: addContactDialog
// PURPOSE: Will open a new dialog that allows the user to add information
//          about a contact and save it to the database.
// =====================================================================

    public void addContactDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_addcontact, null);
        mBuilder.setView(mView);

        final EditText FirstNameField = (EditText) mView.findViewById(R.id.FirstNameEditText);
        final EditText LastNameField = (EditText) mView.findViewById(R.id.LastNameEditText);
        final EditText PhoneField = (EditText) mView.findViewById(R.id.PhoneEditText);
        final EditText EmailField = (EditText) mView.findViewById(R.id.EmailEditText);
        final EditText AddressField = (EditText) mView.findViewById(R.id.AddressEditText);
        final Button AddButton = (Button) mView.findViewById(R.id.AddContactButton);

        final AlertDialog dialog = mBuilder.create();

        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_LONG).show();
                String FirstName = FirstNameField.getText().toString();
                String LastName = LastNameField.getText().toString();
                String Phone = PhoneField.getText().toString();
                String Email = EmailField.getText().toString();
                String Address = AddressField.getText().toString();


                insertNewContact(FirstName,LastName,Phone,Email,Address);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

// =====================================================================
// NAME: addContactDialog
// PURPOSE: Will open a new dialog that allows the user to add information
//          about a contact and save it to the database. Gathers context
//          surroudning the first name of the contact
// =====================================================================

    public void addContactDialog(String firstname) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_addcontact, null);
        mBuilder.setView(mView);

        final EditText FirstNameField = (EditText) mView.findViewById(R.id.FirstNameEditText);
        final EditText LastNameField = (EditText) mView.findViewById(R.id.LastNameEditText);
        final EditText PhoneField = (EditText) mView.findViewById(R.id.PhoneEditText);
        final EditText EmailField = (EditText) mView.findViewById(R.id.EmailEditText);
        final EditText AddressField = (EditText) mView.findViewById(R.id.AddressEditText);
        final Button AddButton = (Button) mView.findViewById(R.id.AddContactButton);

        FirstNameField.setText(firstname);

        final AlertDialog dialog = mBuilder.create();

        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_LONG).show();
                String FirstName = FirstNameField.getText().toString();
                String LastName = LastNameField.getText().toString();
                String Phone = PhoneField.getText().toString();
                String Email = EmailField.getText().toString();
                String Address = AddressField.getText().toString();


                insertNewContact(FirstName,LastName,Phone,Email,Address);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

// =====================================================================
// NAME: addCarerLogin
// PURPOSE: used to make a Carer login on first run
// =====================================================================

    public void addCarerLogin() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_createcarer, null);
        mBuilder.setView(mView);

        final EditText UserNameField = (EditText) mView.findViewById(R.id.userNameField);
        final EditText firstPassword = (EditText) mView.findViewById(R.id.passwordField);
        final EditText confirmPassword = (EditText) mView.findViewById(R.id.confirmPasswordField);
        final Button saveButton = (Button) mView.findViewById(R.id.saveButton);

        final AlertDialog dialog = mBuilder.create();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = UserNameField.getText().toString();
                String retrievedFirstPassword = firstPassword.getText().toString();
                String retrievedSecondPassword = confirmPassword.getText().toString();



                if (retrievedFirstPassword.equals(retrievedSecondPassword)){
                    String encrypted = "";
                    if (retrievedFirstPassword.length() > 8) {
                        encrypted = security.encrypt(retrievedFirstPassword, key);
                        insertNewCarer(userName,encrypted);
                        isFirstRun = false;
                        dialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "Please make your password over 7 characters long", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }


            }
        });

        dialog.show();
    }

// =====================================================================
// NAME: addContactDialog
// PURPOSE: Will open a new dialog that allows the user to add information
//          about a contact and save it to the database. Gathers context
//          surroudning the first and last name of the contact
// =====================================================================

    public void addContactDialog(String firstname, String lastname) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_addcontact, null);
        mBuilder.setView(mView);

        final EditText FirstNameField = (EditText) mView.findViewById(R.id.FirstNameEditText);
        final EditText LastNameField = (EditText) mView.findViewById(R.id.LastNameEditText);
        final EditText PhoneField = (EditText) mView.findViewById(R.id.PhoneEditText);
        final EditText EmailField = (EditText) mView.findViewById(R.id.EmailEditText);
        final EditText AddressField = (EditText) mView.findViewById(R.id.AddressEditText);
        final Button AddButton = (Button) mView.findViewById(R.id.AddContactButton);

        FirstNameField.setText(firstname);
        LastNameField.setText(lastname);

        final AlertDialog dialog = mBuilder.create();

        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_LONG).show();
                String FirstName = FirstNameField.getText().toString();
                String LastName = LastNameField.getText().toString();
                String Phone = PhoneField.getText().toString();
                String Email = EmailField.getText().toString();
                String Address = AddressField.getText().toString();


                insertNewContact(FirstName,LastName,Phone,Email,Address);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

// =====================================================================
// NAME: addPrescriptionDialog
// PURPOSE: Will open a new dialog that allows the user to add information
//          about medication and save it to the database.
// =====================================================================

    public void addPrescriptionDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_addprescription, null);
        mBuilder.setView(mView);

        final EditText DrugTypeField = (EditText) mView.findViewById(R.id.DrugTypeEdit);
        final EditText ToBeTakenField = (EditText) mView.findViewById(R.id.ToBeTakenEdit);
        final EditText PrescribedByField = (EditText) mView.findViewById(R.id.PrescribedByEdit);
        final EditText SurgeryNumberField = (EditText) mView.findViewById(R.id.SurgeryNumberEdit);
        final Button AddButton = (Button) mView.findViewById(R.id.AddPrescriptionButton);

        final AlertDialog dialog = mBuilder.create();

        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_LONG).show();
                String DrugType = DrugTypeField.getText().toString();
                String ToBeTaken = ToBeTakenField.getText().toString();
                String PrescribedBy = PrescribedByField.getText().toString();
                String SurgeryNumber = SurgeryNumberField.getText().toString();


                insertNewPrescription(DrugType,ToBeTaken,PrescribedBy,SurgeryNumber);
                dialog.dismiss();
            }
        });

        dialog.show();

    }


// =====================================================================
// NAME: addDangerDialog
// PURPOSE: Will open a new dialog that allows the user to confirm if they are
//          in danger
// =====================================================================


    public void addDangerDialog() {

        if (isDangerDialogOpen == false) {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_emergency, null);
            mBuilder.setView(mView);

            final Button yesButton = (Button) mView.findViewById(R.id.yesButton);
            final Button noButton = (Button) mView.findViewById(R.id.noButton);


            final AlertDialog dialog = mBuilder.create();


            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(final DialogInterface arg0) {
                    isDangerDialogOpen = false;
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    emergency();
                    dialog.dismiss();
                }
            });

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
            isDangerDialogOpen = true;
        }
    }

// =====================================================================
// NAME:    addCalendarDialog
// PURPOSE: Displays a Calendar
// =====================================================================

    public void addCalendarDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_calendar, null);
        mBuilder.setView(mView);


        final AlertDialog dialog = mBuilder.create();

        dialog.show();

    }

// =====================================================================
// NAME: addMedicineDialog
// PURPOSE: Will open a new dialog that allows the user to add information
//          about medication and save it to the database.
// =====================================================================

    public void addMedicineDialog(String DrName) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_enquiredmedicine, null);
        mBuilder.setView(mView);


        final AlertDialog dialog = mBuilder.create();


        ArrayList<String> DrugProperties;

        Cursor cursor = null;
        String Query ="SELECT * FROM Med WHERE PrescribedBy = ? ";


        DrugProperties = new ArrayList<>();

        TextView drugTextView = (TextView) mView.findViewById(R.id.DrugTypeTextView);
        TextView freqTextView = (TextView) mView.findViewById(R.id.FreqTextView);
        TextView DrTextView = (TextView) mView.findViewById(R.id.PrescribedByTextView);

        cursor = database.rawQuery(Query, new String[]{DrName});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                dialog.show();
                drugTextView.setText("Drug Type: " + cursor.getString(cursor.getColumnIndex("DrugType")));
                freqTextView.setText("You should take: " + cursor.getString(cursor.getColumnIndex("ToBeTaken")));
                DrTextView.setText("Prescribed By: " + DrName);//cursor.getString(cursor.getColumnIndex("PrescribedBy")));

            } while (cursor.moveToNext());
            cursor.close();
        } else {
            messageStrings.add("Mavis: Sorry, you didn't have anything from this doctor");
            setAdapt();
        }

    }

// =====================================================================
// NAME: insertNewContact
// PURPOSE: Database function for inserting contacts
// =====================================================================

    public void insertNewContact(String FirstName, String LastName, String Phone,String Email, String Address) {

        ContentValues values = new ContentValues();

        values.put("FirstName",FirstName);
        values.put("LastName",LastName);
        values.put("PhoneNumber",Phone);
        values.put("EmailAddress",Email);
        values.put("Address",Address);

        long rowId = database.insert("Contacts",null,values);
        if (rowId != -1){
            Toast.makeText(MainActivity.this, "Success",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Error",Toast.LENGTH_LONG).show();
        }


    }

// =====================================================================
// NAME: insertNewCarer
// PURPOSE: Adds a new carer for start up
// =====================================================================

    public void insertNewCarer(String username, String password) {

        if(isFirstRun == true) {

            ContentValues values = new ContentValues();


            values.put("UserName", username);
            values.put("EncryptedPassword", password);
            values.put("ID",1);

            long rowId = database.insert("Carer", null, values);
            if (rowId != -1) {
                Toast.makeText(MainActivity.this, "Success, add info in carer panel", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        }  else {
            Toast.makeText(MainActivity.this, "Account already exists", Toast.LENGTH_LONG).show();
        }

        getCarerInfo();


    }

// =====================================================================
// NAME: insertNewPrescription
// PURPOSE: Database function for inserting prescriptions
// =====================================================================

    public void insertNewPrescription(String DrugType, String ToBeTaken, String PrescribedBy,String SurgeryNumber) {

        ContentValues values = new ContentValues();

        values.put("DrugType",DrugType);
        values.put("ToBeTaken",ToBeTaken);
        values.put("PrescribedBy",PrescribedBy);
        values.put("SurgeryNumber",SurgeryNumber);

        long rowId = database.insert("Med",null,values);
        if (rowId != -1){
            Toast.makeText(MainActivity.this, "Success",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Error",Toast.LENGTH_LONG).show();
        }


    }

// =====================================================================
// NAME: deleteContact
// PURPOSE: Database function for deleting contacts
// =====================================================================

    public void deleteContact(String FirstName, String LastName) {

        long rowId = database.delete("Contacts", "FirstName =" + FirstName + " AND LastName =" + LastName,null );
        if (rowId != -1){
            Toast.makeText(MainActivity.this, "Success",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Error",Toast.LENGTH_LONG).show();
        }


    }

// =====================================================================
// NAME: deletePrescription
// PURPOSE: Database function for deleting prescriptions
// =====================================================================

    public void deletePrescription(final String DrugName) {


        Cursor cursor = null;
        String Query = "SELECT * FROM Med WHERE DrugType = '" + DrugName + "'";

        CarerProperties = new ArrayList<>();

        cursor = database.rawQuery(Query, null);

        if (cursor != null && cursor.moveToFirst()) {

            getCarerInfo();

            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialog_login);

            TextView approval = (TextView) dialog.findViewById(R.id.CarerLoginTitle);
            final TextView warningTextView = (TextView) dialog.findViewById(R.id.warningTextView);
            final EditText UserNameField = (EditText) dialog.findViewById(R.id.UserNameEditText);
            final EditText UserPassword = (EditText) dialog.findViewById(R.id.PasswordEditText);
            Button loginButton = (Button) dialog.findViewById(R.id.LoginButton);


            approval.setText("Approval Needed For This Action");


            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String encrypted = "";

                    if (UserPassword.getText().toString().length() > 7) {
                        encrypted = security.encrypt(UserPassword.getText().toString(), key);
                    }

                    if ((encrypted.equals(CarerProperties.get(6))) && (UserNameField.getText().toString().equals(CarerProperties.get(5)))) {
                        dialog.dismiss();

                        long rowId = database.delete("Med", "DrugType= '" + DrugName + "'", null);
                        if (rowId != -1) {
                            Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        warningTextView.setVisibility(View.VISIBLE);
                    }

                }
            });

            dialog.show();


        } else {
            messageStrings.add("Mavis: Sorry, this didn't exist");
            setAdapt();
        }
    }






}
