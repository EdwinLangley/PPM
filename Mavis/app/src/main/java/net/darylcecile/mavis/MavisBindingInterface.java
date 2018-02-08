package net.darylcecile.mavis;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class MavisBindingInterface {

    private Context context;
    private VoiceController speaker;

    MavisBindingInterface(Context c){
        this.context = c;
        speaker = new VoiceController(c);
    }

    @JavascriptInterface
    public void Notify(String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public void Say(String text){
        speaker.Say(text);
    }


    // Free up resources
    public void destroy(){
        speaker.destroy();
    }
}
