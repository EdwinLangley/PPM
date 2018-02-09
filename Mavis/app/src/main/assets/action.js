

Mavis.Notify("Hello World");


let talkHandler = ()=>{
    let MavisTalkStatus = Mavis.isReady();
    if (MavisTalkStatus === true){
        Mavis.Say("Hi there");
    }
    else{
        setTimeout(talkHandler,1000);
    }
};

setTimeout(talkHandler,1000);