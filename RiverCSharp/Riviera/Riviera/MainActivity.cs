using System;
using System.IO;
using Android.App;
using Android.OS;
using Android.Webkit;
using RivieraPacks;
using Java.Lang;
using Android.Content;
using Android.Runtime;
using Android.Speech;
using Android.Widget;

namespace Riviera
{
    [Activity(Label = "Riviera", MainLauncher = true, Icon = "@drawable/icon")]
    public partial class MainActivity : Activity
    {

        public RComms rComms;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            // Set our view from the "main" layout resource
            SetContentView (Resource.Layout.Main);
            RequestedOrientation = Android.Content.PM.ScreenOrientation.Portrait;

            WebView webView = FindViewById<WebView>(Resource.Id.webView1);

            WebView.SetWebContentsDebuggingEnabled(true);

            rComms = new RComms(this);
            speech = SpeechRecognizer.CreateSpeechRecognizer(this);
            speech.SetRecognitionListener(this);
            recognizerIntent = new Intent(RecognizerIntent.ActionRecognizeSpeech);
            recognizerIntent.PutExtra(RecognizerIntent.ExtraLanguagePreference,"en");
            recognizerIntent.PutExtra(RecognizerIntent.ExtraLanguageModel,RecognizerIntent.LanguageModelFreeForm);
            recognizerIntent.PutExtra(RecognizerIntent.ExtraMaxResults, 3);

            WrapperTasks.setUpWebView(ref webView,this);

            //Fetch first view (update)
            new WebPack("updating.html", (string content, string localPath) => {

                // load fetched view 
                WrapperTasks.loadUrlAndCall(localPath, ()=>{

                    // synchronize local files with remote
                    WrapperTasks.SyncContentWithRemote( ()=>{

                        WrapperTasks.loadUrlAndCall( Global.AssetPath("dashboard.html") );

                    } );
                });
            }).fetch();

        }

		protected override void OnActivityResult(int requestCode, [GeneratedEnum] Result resultCode, Intent data)
		{
            Console.WriteLine(" >> ** ACtivity Res" + requestCode + "," + resultCode + "," + data.ToString());
            if (requestCode == 10)
            {
                if (resultCode == Result.Ok)
                {
                    var matches = data.GetStringArrayListExtra(RecognizerIntent.ExtraResults);
                    Console.WriteLine(" >> ** matches " + string.Join(",",matches));
                    if (matches.Count != 0)
                    {
                        string textInput = matches[0];
                        Toast.MakeText(this, textInput, ToastLength.Long);
                    }
                    else
                        Toast.MakeText(this, "No speech was recognised", ToastLength.Long);
                }
            }

            base.OnActivityResult(requestCode, resultCode, data);
		}
	}

}