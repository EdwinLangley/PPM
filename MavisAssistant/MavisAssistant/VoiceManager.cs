using System;
using Android.Speech.Tts;
using Android.Content;
using Android.Media;

using Java.Util;


namespace MavisAssistant
{

    public interface ITextToSpeech{
        void Say(string Text);
    }

    public class VoiceManager : Java.Lang.Object, ITextToSpeech, TextToSpeech.IOnInitListener
    {
        TextToSpeech speaker;
        string sentence;
        Context context;

        public VoiceManager(Context context) { this.context = context; }

        public void Say(string text){
            sentence = text;
            if (speaker == null){
                speaker = new TextToSpeech(context, this,"com.google.android.tts");
            }
            else{
                speaker.Speak(sentence, QueueMode.Flush, null, null);
            }
        }

        public void OnInit(OperationResult status){

            speaker.SetLanguage(Locale.Uk);
            speaker.SetSpeechRate(0.8f);
            speaker.SetPitch(1);

            if (status.Equals(OperationResult.Success)){
                speaker.Speak(sentence,QueueMode.Flush,null,null);
            }

        }

    }
}
