using System;
using Android.App;
using Android.Content;
using Android.Runtime;
using Android.Views;
using Android.Webkit;
using Android.Widget;
using Android.OS;

namespace Mavis
{
    [Activity(Label = "mavis", MainLauncher = true, Icon = "@mipmap/icon")]
    public class MainActivity : BaseActivity
    {

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState,()=>{

                // Set our view from the "main" layout resource
                SetContentView(Resource.Layout.Main);

                var webView = FindViewById<WebView>(Resource.Id.webView);
                webView.Settings.JavaScriptEnabled = true;

                // Use subclassed WebViewClient to intercept hybrid native calls
                webView.SetWebViewClient(new BaseWebView());

                // Render the view from the type generated from RazorView.cshtml
                var model = new Model1() { Text = "Text goes here" };
                var template = new RazorView() { Model = model };
                var page = template.GenerateString();

                // Load the rendered HTML into the view with a base URL 
                // that points to the root of the bundled Assets folder
                webView.LoadDataWithBaseURL("file:///android_asset/", page, "text/html", "UTF-8", null);
                
            });

        }


    }
}
