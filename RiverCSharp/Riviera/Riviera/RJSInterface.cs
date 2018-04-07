using Android.Content;
using Android.Webkit;
using Android.Widget;
using Java.Interop;

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
        public void ShowToast()
        {
            Toast.MakeText(context, "Hello from C#", ToastLength.Short).Show();
        }
    }
}
