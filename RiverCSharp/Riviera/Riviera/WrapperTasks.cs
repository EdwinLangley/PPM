using System;
using Android.App;
using Android.Webkit;
using RivieraInterfaces;
using RivieraPacks;
using RivieraWeb;

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

        public static void loadUrlAndCall(string url,Action whenComplete){
            rwc.whenReady(whenComplete);
            w.LoadUrl(url);
        }

        public static void SyncContentWithRemote(Action callback){
            Console.WriteLine(" >> Syncing");
            int tasksCompleted = 0;
            WebPack.getFileListAsync((string[] fileList) => {
                tasksCompleted++;
                setUpdateProgress(tasksCompleted, fileList.Length);
                foreach (var file in fileList) {
                    Console.WriteLine(" >> Fetching " + file + " ...");
                    tasksCompleted++;
                    if (file.Length > 0) new WebPack(file,(string fileContent,string fileLocalPath)=>{
                        if (tasksCompleted == fileList.Length){
                            callback.Invoke();
                        }
                    }).fetch();
                    setUpdateProgress(tasksCompleted, fileList.Length);
                }
                return false;
            });
        }

        public static void setUpdateProgress(int taskCompletedCount, int taskCount){
            double completionPercent = (((double)taskCompletedCount / (double)taskCount) * 100);
            Console.WriteLine(" >> " + completionPercent);
            w.EvaluateJavascript("RiverUpdate.updateProgress("+ completionPercent +")",null);
            Console.WriteLine(" => RiverUpdate.updateProgress(" + completionPercent + ") " + taskCompletedCount + "/" + taskCount);
        }
    }
}
