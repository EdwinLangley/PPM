package net.darylcecile.mavis;

import android.animation.TypeConverter;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class VoiceController implements TextToSpeech.OnInitListener {

    private String sentence = "";

    private TextToSpeech tts;

    public boolean ready = false;

    VoiceController(Context c){
        tts = new TextToSpeech(c, this);
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            tts.setLanguage(Locale.UK);
            ready = true;
        }else{
            ready = false;
        }
    }

    public void Say(String text){
        sentence = text;

        if(ready) {
            Log.i("STAT","Saying "+text);
            tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null,null);
        }
        else{
            Log.e("STAT","cant talk");
        }

    }

    // Free up resources
    public void destroy(){
        tts.shutdown();
    }

}
