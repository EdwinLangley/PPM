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

            WrapperTasks.setUpWebView(ref webView,this);

            //Fetch first view (update)
            new WebPack("updating.html", (string content, string localPath) => {

                // load fetched view 
                WrapperTasks.loadUrlAndCall("file:///" + localPath, ()=>{

                    // synchronize local files with remote
                    WrapperTasks.SyncContentWithRemote( ()=>{
                        
                        // update complete, fetch and load dashboard view
                        new WebPack("dashboard.html",(string fileContent, string fileLocalPath) => {
                            webView.LoadUrl("file:///" + fileLocalPath);

                            // other tasks are carried out by request from javascript
                        }).fetch();
                    } );
                });
            }).fetch();

        }
    }
}