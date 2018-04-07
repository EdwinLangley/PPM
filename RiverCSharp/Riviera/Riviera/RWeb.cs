using System;
using System.Collections.Generic;
using Android.Webkit;

namespace RivieraWeb
{
    public class RWebClient : WebViewClient
    {
        public WebView linkedWebView;
        public List<Action> tasklist = new List<Action>();

        public static RWebClient linkWebView(ref WebView webView){
            RWebClient rWebClientInstance = new RWebClient();
            rWebClientInstance.linkedWebView = webView;
            webView.SetWebViewClient(rWebClientInstance);
            return rWebClientInstance;
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
