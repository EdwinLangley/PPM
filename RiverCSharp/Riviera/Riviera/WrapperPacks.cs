using System.Net;
using System.IO;
using System.Text;
using System;
using Riviera;

namespace RivieraPacks
{
    

    public class WebPack
    {
        protected string root = "https://raw.githubusercontent.com/daryl-cecile/RiverRepo/master/";
        protected WebClient webClient;
        protected Uri url;
        protected string fileName;
        protected string localPath;
        protected bool waitSkipped;
        protected Action<string,string> whenReceived;

        public WebPack(string filename,Action<string,string> whenDone=null,bool start=false){

            bool skipWait = false;
            string localpath = Path.Combine(Globals.ASSETS_DIRECTORY, filename);
                
            if ( File.Exists( localPath ) == true ){
                skipWait = true;
                whenDone?.Invoke(File.ReadAllText(localPath),localPath);
            }

            webClient = new WebClient();

            webClient.Encoding = Encoding.UTF8;

            changeFileName(filename);

            localPath = localpath;
            waitSkipped = skipWait;
            whenReceived = whenDone;

            if (start==true) {
                fetch(); 
            }
        }

        protected virtual void linkEvent(bool skipWait,string localPath,string filename,Action<string,string> whenDone=null){
            webClient.DownloadStringCompleted += (s, e) => {
                var text = e.Result; // get the downloaded text
                localPath = Path.Combine(Globals.ASSETS_DIRECTORY, filename);
                Console.WriteLine(" >> Updating " + localPath);
                File.WriteAllText(localPath, text); // writes to local storage
                if (skipWait == false) whenDone?.Invoke(text, localPath);
            };
        }

        public void changeRoot(string newRoot){
            root = newRoot;
        }

        public void changeFileName(string newFileName){
            fileName = newFileName;
        }

        public virtual void fetch(){
            linkEvent(waitSkipped, localPath, fileName, whenReceived);

            url = new Uri(root + fileName);
            Console.WriteLine(" >> Downloading " + url);
            webClient.DownloadStringAsync(url);
        }

        public static void getFileListAsync(Func<string[],bool> resultHandler){
            WebPack w = new WebPack("FILES.REG", (string result,string localpath) => resultHandler(result.Split('\n')), true);
        }
    }

    public class WebPackImage : WebPack
    {
        public WebPackImage(string filename, Action<string, string> whenDone = null, bool start = false) : base(filename, whenDone, false)
        {
            if (start == true) {
                fetch();
            }
        }

        override protected void linkEvent(bool skipWait, string localPath, string filename, Action<string, string> whenDone = null)
        {
            webClient.DownloadDataCompleted += (s, e) => {
                var bytes = e.Result; // get the downloaded text
                localPath = Path.Combine(Globals.ASSETS_DIRECTORY, filename);
                Console.WriteLine(" >> Updating " + localPath);
                File.WriteAllBytes(localPath, bytes); // writes to local storage
                if (skipWait == false) whenDone?.Invoke("", localPath);
            };
        }

        override public void fetch()
        {
            linkEvent(waitSkipped, localPath, fileName, whenReceived);

            url = new Uri(root + fileName);
            Console.WriteLine(" >> Downloading " + url);
            webClient.DownloadDataAsync(url);
        }
    }
}
