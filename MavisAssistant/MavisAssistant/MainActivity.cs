using Android.App;
using Android.Widget;
using Android.OS;
using Android.Webkit;

namespace MavisAssistant
{
    [Activity(Label = "mavis assistant", MainLauncher = true)]
    public class MainActivity : Activity
    {
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.Main);

            WebView localWebView = FindViewById<WebView>(Resource.Id.LocalWebView);
            localWebView.SetWebViewClient(new WebViewClient()); // stops request going to Web Browser

            localWebView.Settings.JavaScriptEnabled = true;

            localWebView.AddJavascriptInterface(new BindingManager(localWebView), "Mavis");
    
            localWebView.LoadUrl("file:///android_asset/app.html");
        }
    }
}

