package com.example.edwin.chatbot1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.History;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.utils.IOUtils;


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





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageList= (ListView) findViewById(R.id.message_list);

        messageStrings = new ArrayList<String>();

        runbot();

    }

    public void runbot() {
        resourcesPath = getResourcesPath();
        System.out.println(resourcesPath);
        MagicBooleans.trace_mode = TRACE_MODE;
        bot = new Bot("super", "" );//resourcesPath);
        chatSession = new Chat(bot);
        bot.brain.nodeStats();
        textLine = "";
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
        String resourcesPath = path + File.separator + "src" + File.separator + "main" + File.separator + "res";
        return resourcesPath;
    }

    public void chatSend(View view) {



        try {

            //while (true) {
            System.out.print("Human : ");
            editContents = (EditText) findViewById(R.id.edittext_chatbox);
            textLine = editContents.getText().toString();
            editContents.setText("");
            messageStrings.add("Human: " + textLine);
            if ((textLine == null) || (textLine.length() < 1))
                textLine = MagicStrings.null_input;
            if (textLine.equals("q")) {
                System.exit(0);
            } else if (textLine.equals("wq")) {
                bot.writeQuit();
                System.exit(0);
            } else {
                String request = textLine;
                if (MagicBooleans.trace_mode)
                    System.out.println("STATE=" + request + ":THAT=" + ((History) chatSession.thatHistory.get(0)).get(0) + ":TOPIC=" + chatSession.predicates.get("topic"));
                String response = chatSession.multisentenceRespond(request);
                while (response.contains("&lt;"))
                    response = response.replace("&lt;", "<");
                while (response.contains("&gt;"))
                    response = response.replace("&gt;", ">");
                messageStrings.add("Robot : " + response);
                setAdapt();
            }
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
