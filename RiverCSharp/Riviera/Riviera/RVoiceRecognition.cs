// RVoiceRecognition.cs
// /Users/darylcecile/Desktop/PPM.nosync/RiverCSharp/Riviera/Riviera
// Daryl Cecile Copyright 2018
// 08/04/2018

// http://www.truiton.com/2014/06/android-speech-recognition-without-dialog-custom-activity/

using System;
using System.Collections.Generic;
using Android.Content;
using Android.Content.PM;
using Android.OS;
using Android.Runtime;
using Android.Speech;
using Android.Widget;
using Java.Util;
using RivieraWeb;

namespace Riviera
{
    public partial class MainActivity : IRecognitionListener
    {

        public static int REQUEST_RECORD_PERMISSION = 100;
        public SpeechRecognizer speech = null;
        private Intent recognizerIntent;
        public static bool isListening = false;

		public override void OnRequestPermissionsResult(int requestCode, string[] permissions, [GeneratedEnum] Permission[] grantResults)
		{
			base.OnRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == REQUEST_RECORD_PERMISSION){
                
                if (grantResults.Length > 0 && grantResults[0] == Permission.Granted)
                {
                    speech.StartListening(recognizerIntent);
                }
                else
                {
                    Toast.MakeText(this, "Permission Denied!", ToastLength.Long).Show();
                }

            }
		}

		public void OnBeginningOfSpeech()
        {
            //throw new NotImplementedException();
        }

        public void OnBufferReceived(byte[] buffer)
        {
            //throw new NotImplementedException();
        }

        public void OnEndOfSpeech()
        {
            //throw new NotImplementedException();
        }

        public void OnError([GeneratedEnum] SpeechRecognizerError error)
        {
            //throw new NotImplementedException();
        }

        public void OnEvent(int eventType, Bundle @params)
        {
            //throw new NotImplementedException();
        }

        public void OnPartialResults(Bundle partialResults)
        {
            //throw new NotImplementedException();
        }

        public void OnReadyForSpeech(Bundle @params)
        {
            //throw new NotImplementedException();
        }

        public void OnResults(Bundle results)
        {
            var matches = results.GetStringArrayList(SpeechRecognizer.ResultsRecognition);
            String text = matches[0];
            RWebClient.UserSaid(text);
            rComms.processInput(text);
            RWebClient.UpdateUI_StopListening();
        }

        public void OnRmsChanged(float rmsdB)
        {
            // HACK: use for waveform
            //throw new NotImplementedException();
        }
    }
}
