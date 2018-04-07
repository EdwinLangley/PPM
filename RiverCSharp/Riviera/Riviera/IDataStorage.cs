namespace RivieraInterfaces
{
    public interface IDataStorage
    {
        void SaveText(string filename, string text);
        string LoadText(string filename);
    }
}
