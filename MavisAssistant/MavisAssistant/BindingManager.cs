using System;
using Android.Util;
using Android.Webkit;

namespace MavisAssistant
{
    public class BindingManager: Java.Lang.Object
    {

        WebView webView;

        public BindingManager(WebView webView)
        {
            this.webView = webView;
        }

        void Run()
        {
            this.webView.EvaluateJavascript("document.body.querySelector('h1').innerHTML += ' z'; return 0", new JavascriptResult((string v) =>
            {
                Log.Debug("abc","");
            }));
        }
    }
    
    public class JavascriptResult : Java.Lang.Object, IValueCallback
    {
        Action<string> valueHandler;

        public JavascriptResult(Action<string> onValue){
            this.valueHandler = onValue;       
        }

        public void OnReceiveValue(Java.Lang.Object result)
        {
            string json = ((Java.Lang.String)result).ToString();
            this.valueHandler(json);
        }
    }
}
