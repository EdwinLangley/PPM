using System;
using Android.Util;
using Android.Views;

using Android.Content;
using Android.Widget;

using Java.Interop;
using Android.Webkit;

namespace MavisAssistant
{
    public class BindingManager : Java.Lang.Object
    {
        private Context context;
        private VoiceManager vm;

        public BindingManager(Context context){
            this.context = context;
        }

        [Export]
        [JavascriptInterface]
        public void showNotification(string message){
            Toast.MakeText(this.context,message,ToastLength.Long).Show();
        }

        [Export]
        [JavascriptInterface]
        public void talk(string message){
            if (vm == null) vm = new VoiceManager(context);
            vm.Say(message);
        }

      
    }

}
