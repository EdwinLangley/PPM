using System;
using Android.App;
using Android.Views;
using Android.Webkit;

namespace Mavis
{
    public class Core
    {
        public Core()
        {
        }
    }

    public class BaseActivity : Activity{

        protected virtual void OnCreate(Android.OS.Bundle savedInstanceState, Action action)
        {
            base.OnCreate(savedInstanceState);

            // IMPORTANT_NOTE need this for now > for fullscreen view 
            RequestWindowFeature(WindowFeatures.NoTitle);
            Window.SetFlags(WindowManagerFlags.Fullscreen, WindowManagerFlags.Fullscreen);

            action();
        }

    }

    public class BaseWebView : WebViewClient{
        
        public override bool ShouldOverrideUrlLoading(WebView webView, IWebResourceRequest request)
        {

            // If the URL is not our own custom scheme, just let the webView load the URL as usual
            var scheme = "hybrid:";

            var url = request.Url.ToString();

            if (!url.StartsWith(scheme, StringComparison.CurrentCulture))
                return false;

            // This handler will treat everything between the protocol and "?"
            // as the method name.  The querystring has all of the parameters.
            var resources = url.Substring(scheme.Length).Split('?');
            var method = resources[0];
            var parameters = System.Web.HttpUtility.ParseQueryString(resources[1]);

            if (method == "UpdateLabel")
            {
                var textbox = parameters["textbox"];

                // Add some text to our string here so that we know something
                // happened on the native part of the round trip.
                var prepended = $"C# says: {textbox}";

                // Build some javascript using the C#-modified result
                executeJavascript(webView, $"SetLabelText('{prepended}');" );
            }

            return true;

        }


        public void executeJavascript(WebView webView, String statement)
        {
            webView.LoadUrl("javascript:" + statement);
        }

    }
}
