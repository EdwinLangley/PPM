using System;
using Android.Util;
using Android.Views;

using Android.Content;
using Android.Widget;

using Java.Interop;
using Android.Webkit;
using Xamarin.Forms;

namespace MavisAssistant
{
    public class BindingManager : Java.Lang.Object
    {
        private Context context;

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
            Console.WriteLine("### TALKING {0}",message);

            DependencyService.Get<VoiceManager>().Say(message);
        }

      
    }

}
