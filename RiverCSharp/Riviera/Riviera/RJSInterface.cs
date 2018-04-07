using Android.Content;
using Android.Webkit;
using Android.Widget;
using Java.Interop;
using System.Linq;
using Riviera;

namespace RivieraInterfaces
{
    class RJSInterface : Java.Lang.Object
    {
        Context context;

        public readonly string[] TasksAllowed = {  };

        public RJSInterface(Context context)
        {
            this.context = context;
        }

        [Export]
        [JavascriptInterface]
        public void ShowToast()
        {
            Toast.MakeText(context, "Hello from C#", ToastLength.Short).Show();
        }

        [Export]
        [JavascriptInterface]
        public bool RequestTask(string taskName,string[] parameters){

            if ( TasksAllowed.Contains(taskName) ){
                // TODO run tasks
                return true;
            }
            else{
                return false;
            }
        }
    }
}
