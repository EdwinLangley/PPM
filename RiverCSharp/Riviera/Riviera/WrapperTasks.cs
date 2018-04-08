using System;
using Android.App;
using Android.Webkit;
using RivieraInterfaces;
using RivieraPacks;
using RivieraWeb;
using System.IO;
using System.Collections.Generic;
using System.Linq;
using Java.Lang;

namespace Riviera
{
    public static class WrapperTasks
    {
        static WebView w;
        static RWebClient rwc;

        public static void setUpWebView(ref WebView webView,Activity context){
            w = webView;
            rwc = RWebClient.linkWebView(ref webView);
            webView.Settings.JavaScriptEnabled = true;
            webView.AddJavascriptInterface(new RJSInterface(context), "River");
        }

        public static void loadUrlAndCall(string url,Action whenComplete=null){
            // List<string> url,Action whenComplete
            if (whenComplete != null) rwc.whenReady(whenComplete);
            w.LoadUrl("file:///" + url);
        }

        public static void SyncContentWithRemote(Action callback){
            Console.WriteLine(" >> Syncing");
            int tasksCompleted = 0;
            WebPack.getFileListAsync((string[] fileList) => {
                setUpdateProgress(tasksCompleted, fileList.Length);

                fileList = fileList.Where(c => {
                    return (c[0] != '/' && c[1] != '/');
                }).ToArray();

                foreach (var file in fileList) {
                    Console.WriteLine(" >> Fetching " + file + " ...");

                    string fetch_type = file.Split(':')[0];
                    string fetch_name = file.Split(':')[1];

                    if ( fetch_type == Global.FETCH_TYPE_IMAGE ){
                        if (fetch_name.Length > 0) new WebPackImage(fetch_name, (string fileContent, string fileLocalPath) => {
                            tasksCompleted++;
                            setUpdateProgress(tasksCompleted, fileList.Length);
                            if (tasksCompleted == fileList.Length) callback.Invoke();
                        }).fetch();
                    }
                    else{
                        if (fetch_name.Length > 0) new WebPack(fetch_name, (string fileContent, string fileLocalPath) => {
                            tasksCompleted++;
                            setUpdateProgress(tasksCompleted, fileList.Length);
                            if (tasksCompleted == fileList.Length) callback.Invoke();
                        }).fetch();
                    }
                }
                return false;
            });
        }

        public static void setUpdateProgress(int taskCompletedCount, int taskCount){
            double completionPercent = (((double)taskCompletedCount / (double)taskCount) * 100);
            Console.WriteLine(" >> " + completionPercent);
            w.Post(new Runnable(() =>
            {
                w.EvaluateJavascript("RiverUpdate.updateProgress("+ completionPercent +")",null);
            }));
            Console.WriteLine(" => RiverUpdate.updateProgress(" + completionPercent + ") " + taskCompletedCount + "/" + taskCount);
        }
    }
}
