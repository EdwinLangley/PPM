using Android.App;
using Android.Widget;
using Android.OS;
using Android.Webkit;
using Android.Content.PM;

namespace MavisAssistant
{
    [Activity(Label = "mavis assistant", MainLauncher = true, Theme = "@android:style/Theme.NoTitleBar", ConfigurationChanges = ConfigChanges.Orientation | ConfigChanges.ScreenSize)]
    public class MainActivity : Activity
    {
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.Main);

            WebView localWebView = FindViewById<WebView>(Resource.Id.LocalWebView);

            localWebView.SetWebViewClient(new MavisWebViewClient()); // stops request going to Web Browser

            localWebView.Settings.JavaScriptEnabled = true;

            localWebView.AddJavascriptInterface(new BindingManager( Application.Context ), "Mavis");

            localWebView.LoadUrl("file:///android_asset/app.html");
        }
    }

    internal class MavisWebViewClient : WebViewClient
    {
        public override bool ShouldOverrideUrlLoading(WebView view, IWebResourceRequest request)
        {
            view.LoadUrl(request.Url.ToString());
            return true;  
        }
    }
}

