

Mavis.Notify("Hello World");

var MavisTalkStatus = Mavis.isReady();

console.log(MavisTalkStatus);

if (MavisTalkStatus === true){
    Mavis.Say("Hi there");
}