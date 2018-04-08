using System;
using System.IO;
using Android.App;
using Android.OS;
using Android.Webkit;
using RivieraPacks;

namespace Riviera
{
    [Activity(Label = "Riviera", MainLauncher = true, Icon = "@drawable/icon")]
    public class MainActivity : Activity
    {
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            // Set our view from the "main" layout resource
            SetContentView (Resource.Layout.Main);

            WebView webView = FindViewById<WebView>(Resource.Id.webView1);

            WebView.SetWebContentsDebuggingEnabled(true);

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
    }
}