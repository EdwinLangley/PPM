using System;
using System.Collections.Generic;
using Android.Runtime;
using Android.Webkit;
using Java.Lang;
using Riviera;

namespace RivieraWeb
{
    public class RJSCallback : Java.Lang.Object, IValueCallback
    {
        private Action<object> _callback;
        public RJSCallback(Action<object> callback=null){
            _callback = callback;
        }

        public void OnReceiveValue(Java.Lang.Object value)
        {
            if (_callback != null) _callback(value);
        }
    }

    public class RWebClient : WebViewClient
    {
        public static WebView linkedWebView;
        public List<Action> tasklist = new List<Action>();

        public static RWebClient linkWebView(ref WebView webView){
            RWebClient rWebClientInstance = new RWebClient();
            RWebClient.linkedWebView = webView;
            WebChromeClient webChromeClient = new WebChromeClient();
            webView.SetWebChromeClient(webChromeClient);
            webView.SetWebViewClient(rWebClientInstance);
            webView.ClearCache(true);
            return rWebClientInstance;
        }

        public static void ExecuteJS(string script,Action<object> callback=null){
            linkedWebView.Post(new Runnable(() =>
            {
                linkedWebView.EvaluateJavascript(script, new RJSCallback(callback));
            }));
        }

        public static void TriggerCallback(string identifier){
            ExecuteJS(" document.dispatchEvent(new CustomEvent('"+identifier+"', { bubbles : true })) ");
        }

        public static void RespondSaying(string message){
            ExecuteJS("RiverRemote.response_speak(`"+message+"`)");
        }

        public static void UserSaid(string message){
            ExecuteJS("RiverRemote.user_speak(`" + message + "`)");
        }

        public static void UpdateUI_StopListening(){
            MainActivity.isListening = false;
            ExecuteJS("RiverRemote.stop_listening()");
        }

        public void whenReady(Action doTask){
            tasklist.Add(doTask);
        }

		public override void OnPageFinished(WebView view, string url)
		{
			base.OnPageFinished(view, url);

            foreach (Action task in tasklist)
            {
                task();
            }
            tasklist.Clear();
        }
	}
}
