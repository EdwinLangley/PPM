package com.example.edwin.chatbot1;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.History;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.utils.IOUtils;

import android.view.Menu;


public class MainActivity extends AppCompatActivity {

    private static final boolean TRACE_MODE = false;
    static String botName = "super";

    String resourcesPath;
    Bot bot;
    Chat chatSession;
    String textLine;
    EditText editContents;
    ListView messageList;
    List<String> messageStrings;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> CarerProperties;

    private DataBaseHelper dataBaseHelper;

    public SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setCustomKeybaord();
        messageList= (ListView) findViewById(R.id.message_list);


        messageStrings = new ArrayList<String>();

        setTitle("Mavis Personal Assistant");

        dataBaseHelper = new DataBaseHelper(getApplicationContext());
        database = dataBaseHelper.getReadableDatabase();

        getCarerInfo();

        runbot();
    }

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

            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void insertData(){
        String name = "John";

        ContentValues values = new ContentValues();

        values.put("ID",1);
        values.put("FirstName","Jane");
        values.put("LastName","W");
        values.put("PhoneNumber","77878787");
        values.put("EmailAddress","a@b.com");
        values.put("Address","1 NTU");

        long rowId = database.insert("Carer",null,values);
        if (rowId != -1){
            Toast.makeText(MainActivity.this, "Success",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Error",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);
            EditText UserNameField = (EditText) mView.findViewById(R.id.UserNameEditText);
            EditText UserPassword = (EditText) mView.findViewById(R.id.PasswordEditText);
            Button LoginButton = (Button) mView.findViewById(R.id.LoginButton);

            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();

            LoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    Intent carerPanelIntent = new Intent(MainActivity.this, CarerPanel.class);
                    carerPanelIntent.putExtra("sendFirstName",CarerProperties.get(0));
                    carerPanelIntent.putExtra("sendLastName",CarerProperties.get(1));
                    carerPanelIntent.putExtra("sendPhoneNumber",CarerProperties.get(2));
                    carerPanelIntent.putExtra("sendEmail",CarerProperties.get(3));
                    carerPanelIntent.putExtra("sendAddress",CarerProperties.get(4));
                    startActivity(carerPanelIntent);
                }
            });

            dialog.show();

        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }


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
        bot = new Bot("super",path );//resourcesPath);
        chatSession = new Chat(bot);
        bot.brain.nodeStats();
        textLine = "";
    }

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

    public void setAdapt() {
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                messageStrings);

        messageList.setAdapter(arrayAdapter);

    }

    private static String getResourcesPath() {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        path = path.substring(0, path.length() - 2);
        System.out.println(path);
        String resourcesPath = path + File.separator  + "src" + File.separator + "main" + File.separator + "res";
        return resourcesPath;
    }

    public void chatSend(View view) {

        try {

            setHumanResponse();

            if ((textLine == null) || (textLine.length() < 1)) {
                textLine = MagicStrings.null_input;
            }
            if (textLine.equals("q")) {
                System.exit(0);
            } else if (textLine.equals("wq")) {
                bot.writeQuit();
                System.exit(0);
            }else if (textLine.contains("current time")) {
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
            }



            else {
                AIMLFallBack();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public void setHumanResponse() {

        System.out.print("Human : ");
        editContents = (EditText) findViewById(R.id.edittext_chatbox);
        textLine = editContents.getText().toString();
        editContents.setText("");
        messageStrings.add("Human: " + textLine);
    }

    public void timeResponse() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        String response = dateFormat.format(date);
        messageStrings.add("Mavis: " +" The current date and time is:  "+ response);
        setAdapt();

    }

    public void emergency() {
        getCarerInfo();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+CarerProperties.get(2)));
        startActivity(intent);

    }

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



}
