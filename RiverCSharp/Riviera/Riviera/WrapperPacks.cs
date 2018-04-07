using System.Net;
using System.IO;
using System.Text;
using System;
using Riviera;

namespace RivieraPacks
{
    

    public class WebPack
    {
        private string root = "https://raw.githubusercontent.com/daryl-cecile/RiverRepo/master/";
        private WebClient webClient;
        private Uri url;
        private string fileName;

        public WebPack(string filename,Action<string,string> whenDone=null,bool start=false){

            bool skipWait = false;
            string localPath = Path.Combine(RSupport.ASSETS_DIRECTORY, filename);
                
            if ( File.Exists( localPath ) == true ){
                skipWait = true;
                whenDone?.Invoke(File.ReadAllText(localPath),localPath);
            }

            webClient = new WebClient();

            webClient.Encoding = Encoding.UTF8;

            changeFileName(filename);

            webClient.DownloadStringCompleted += (s, e) => {
                var text = e.Result; // get the downloaded text
                localPath = Path.Combine(RSupport.ASSETS_DIRECTORY, filename);
                Console.WriteLine(" >> Updating " + localPath);
                File.WriteAllText(localPath, text); // writes to local storage
                if (skipWait==false) whenDone?.Invoke(text,localPath);
            };

            if (start==true) fetch();
        }

        public void changeRoot(string newRoot){
            root = newRoot;
        }

        public void changeFileName(string newFileName){
            fileName = newFileName;
        }

        public void fetch(){
            url = new Uri(root + fileName);
            Console.WriteLine(" >> Downloading " + url);
            webClient.DownloadStringAsync(url);
        }

        public static void getFileListAsync(Func<string[],bool> resultHandler){
            WebPack w = new WebPack("FILES.REG", (string result,string localpath) => resultHandler(result.Split('\n')), true);
        }
    }
}
