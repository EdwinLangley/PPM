// This javascript method calls C# by setting the browser 
// to a URL with a custom scheme that is registered in C#.  
// All values are sent to C# as part of the querystring  

function InvokeCSharpWithFormValues(elm) {
    var qs = "";
    var elms = elm.form.elements;

    for (var i = 0; i < elms.length;i++) {
        qs += "&" + elms[i].name + "=" + elms[i].value;
    }

    if (elms.length > 0) 
        qs = qs.substring(1);

    location.href = "hybrid:" + elm.name + "?" + qs;
}

// This javascript method is called from C#

function SetLabelText(text) {
    var elm = document.getElementById('label');
    elm.innerHTML = text;
}
