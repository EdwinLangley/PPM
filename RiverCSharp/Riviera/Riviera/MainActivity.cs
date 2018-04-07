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

            new WebPack("updating.html", (string content, string localPath) => {
                
                WrapperTasks.loadUrlAndCall("file:///" + localPath, ()=>{
                    WrapperTasks.SyncContentWithRemote( ()=>{
                        // update complete
                        new WebPack("dashboard.html",(string fileContent, string fileLocalPath) => {
                            webView.LoadUrl("file:///" + fileLocalPath);
                        }).fetch();
                    } );
                });
            }).fetch();

        }
    }
}