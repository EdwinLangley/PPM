using System;
using Android.App;
using Android.Content;
using Android.Speech.Tts;
using Android.Util;
using Java.Util;

using Xamarin.Forms;

[assembly: Xamarin.Forms.Dependency(typeof(MavisAssistant.VoiceManager))]
namespace MavisAssistant
{

    public interface ITextToSpeech{
        void Say(string Text);
    }

    public class VoiceManager : Java.Lang.Object, ITextToSpeech, TextToSpeech.IOnInitListener
    {
        TextToSpeech speaker;
        Context context;

        public VoiceManager() { 
            this.context = Android.App.Application.Context;
        }

        public void Say(string text){
            if (speaker == null){
                speaker = new TextToSpeech(context, this);
            }
            else{

                speaker.SetLanguage(Locale.Uk);
                speaker.SetSpeechRate(0.8f);
                speaker.SetPitch(1);

                speaker.Speak(text, QueueMode.Flush, null, "Mavis");
            }
        }


        public void OnInit(OperationResult status)
        {
            if (status.Equals(OperationResult.Success))
            {
                speaker.Speak("Good morning", QueueMode.Flush, null, null);
            }
            else{
                Console.WriteLine("### Failed to initialize");
                Log.Error("SPEECH","Failed to init");
            }
        }

    }
}
