using System;
using System.IO;

namespace Riviera
{
    public static class Global
    {

        public static string AssetPath(string assetName){
            return Path.Combine(ASSETS_DIRECTORY, assetName);
        }

        public static string WORKING_DIRECTORY{
            get {
                return Environment.GetFolderPath(Environment.SpecialFolder.Personal);
            }
        }

        public static string ASSETS_DIRECTORY{
            get{
                Directory.CreateDirectory(Path.Combine(WORKING_DIRECTORY, "river_app"));
                return Path.Combine(WORKING_DIRECTORY, "river_app");
            }
        }

        public static string PERSONAL_DIRECTORY
        {
            get
            {
                Directory.CreateDirectory(Path.Combine(WORKING_DIRECTORY, "river_user"));
                return Path.Combine(WORKING_DIRECTORY, "river_user");
            }
        }

        public static readonly string FETCH_TYPE_IMAGE = "imgx";
        public static readonly string FETCH_TYPE_FNAME = "";
    }
}
