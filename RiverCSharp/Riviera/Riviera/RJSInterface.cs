using Android.Content;
using Android.Webkit;
using Android.Widget;
using Java.Interop;
using Riviera;
using Android.App;
using RivieraManagers;
using RivieraWeb;

namespace RivieraInterfaces
{
    class RJSInterface : Java.Lang.Object
    {
        Context context;

        public RJSInterface(Context context)
        {
            this.context = context;
        }

        [Export]
        [JavascriptInterface]
        public void ShowToast(string content)
        {
            Toast.MakeText(context, content, ToastLength.Short).Show();
        }

        [Export]
        [JavascriptInterface]
        public void ShowAlert(string title, string content){
            AlertDialog.Builder ab = new AlertDialog.Builder(context);
            ab.SetTitle(title);
            ab.SetMessage(content);
            ab.Show();
        }

        [Export]
        [JavascriptInterface]
        public string RegisterTask(string identifier){
            RTaskManager.RTask task = new RTaskManager.RTask();
            task.identifier = identifier;
            RTaskManager.taskList.Add(task);
            return task.identifier;
        }

        [Export]
        [JavascriptInterface]
        public string SyncWithRemote(string taskIdentifier){
            RTaskManager.RTask task = RTaskManager.taskList.Find((RTaskManager.RTask obj) => obj.identifier == taskIdentifier);
            WrapperTasks.SyncContentWithRemote(task.Complete);
            return task.identifier;
        }

    }
}
