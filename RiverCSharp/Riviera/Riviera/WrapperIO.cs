using System;
using System.IO;
using RivieraInterfaces;

namespace RiverIOController
{
    public class SaveAndLoad : IDataStorage
    {
        public void SaveText(string filename, string text)
        {
            var documentsPath = Environment.GetFolderPath(Environment.SpecialFolder.Personal);
            Directory.CreateDirectory(Path.Combine(documentsPath, "river_app"));
            var filePath = Path.Combine(documentsPath,"river_app", filename);
            File.WriteAllText(filePath, text);
        }
        public string LoadText(string filename)
        {
            var documentsPath = Environment.GetFolderPath(Environment.SpecialFolder.Personal);
            Directory.CreateDirectory(Path.Combine(documentsPath, "river_app"));
            var filePath = Path.Combine(documentsPath,"river_app", filename);
            return File.ReadAllText(filePath);
        }
    }
}