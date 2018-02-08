package net.darylcecile.mavis;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class VoiceController implements TextToSpeech.OnInitListener {

    private String sentence = "";

    private TextToSpeech tts;

    private boolean ready = false;

    private boolean allowed = false;

    VoiceController(Context c){
        tts = new TextToSpeech(c, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if ( status == TextToSpeech.SUCCESS ){
                    tts.setLanguage(Locale.UK);
                    Log.d("STAT","Set Lang");
                }
                else{
                    Log.e("STAT","Failed to init voice");
                }

            }
        });
    }

    public boolean isAllowed(){
        return allowed;
    }

    public void allow(boolean allowed){
        this.allowed = allowed;
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            tts.setLanguage(Locale.US);
            ready = true;
        }else{
            ready = false;
        }
    }

    public void Say(String text){
        sentence = text;

        if(ready && allowed) {
            Log.i("STAT","Saying "+text);
            tts.speak(sentence, TextToSpeech.QUEUE_ADD, null,null);
        }
        else{
            Log.i("STAT","cant talk");
        }

    }

    // Free up resources
    public void destroy(){
        tts.shutdown();
    }

}
