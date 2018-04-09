// RComms.cs
// /Users/darylcecile/Desktop/PPM.nosync/RiverCSharp/Riviera/Riviera
// Daryl Cecile Copyright 2018
// 08/04/2018
using System;
using Android;
using Android.App;
using Android.Content;
using Android.Speech;
using Android.Views;
using Android.Widget;
using Java.Lang;
using RivieraWeb;

namespace Riviera
{
    public class RComms
    {
        private Intent voiceIntent;
        private Activity activity;
        private Action call_back;

        public const int VOICE = 10;
        public bool isRecording = false;

        public RComms(Activity a)
        {
            activity = a;
        }

        public void Listen(Action callback){
            activity.RequestPermissions(new string[] { Manifest.Permission.RecordAudio },
                                MainActivity.REQUEST_RECORD_PERMISSION);
            call_back = callback;
        }

        public void processInput(string input){
            // TODO process voice recognition input
            RWebClient.RespondSaying("Heard user say " + input);
        }

        public void StopListening(){
            View v = activity.FindViewById(Resource.Id.webView1);
            v.Post(new Runnable (()=>{
                ((MainActivity)activity).speech.StopListening();
            }));
            call_back();
        }
    }
}
