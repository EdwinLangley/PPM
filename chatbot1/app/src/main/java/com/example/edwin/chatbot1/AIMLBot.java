import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.History;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.utils.IOUtils;

/**
  Initialises AIML Bot resources and provides interface for responses
  Uses constructor as intialisation point also known as RAII
**/
public class AIMLBot{
   private static final boolean TRACE_MODE = false;
   private Bot bot;
   private Chat chatSession;
  
  /**
    Initialises the bot with the relavant AIML files
  **/
  public AIMLBot() {
        MagicBooleans.trace_mode = TRACE_MODE;
        File fileExt = new File(getExternalFilesDir(null).getAbsolutePath() + "/bots");
        //Unzip AIML assets and grab path
        if(!fileExt.exists())
        {
            ZipFileExtraction extract = new ZipFileExtraction();

            try
            {
                extract.unZipIt(getAssets().open("bots.zip"), getExternalFilesDir(null).getAbsolutePath()+"/");
            } catch (Exception e) { e.printStackTrace(); }
        }

        String path = getExternalFilesDir(null).getAbsolutePath();
    
        //Load in bot and chatSession
        bot = new Bot("super", path);
        chatSession = new Chat(bot);
        bot.brain.nodeStats();
        textLine = "";
  }
  
  public String getResponse(String userInput){
    
        if (MagicBooleans.trace_mode)
            System.out.println("STATE=" + userInput + ":THAT=" + ((History) chatSession.thatHistory.get(0)).get(0) + ":TOPIC=" + chatSession.predicates.get("topic"));
        String response = chatSession.multisentenceRespond(userInput);
        while (response.contains("&lt;"))
            response = response.replace("&lt;", "<");
        while (response.contains("&gt;"))
            response = response.replace("&gt;", ">");
        messageStrings.add("Mavis: " + response);
    
    return "Mavis: " + response;
  }
}