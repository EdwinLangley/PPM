package net.darylcecile.mavis;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends Activity {

    private MavisBindingInterface MBI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set visual defaults
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor( Color.BLACK );

        // set main activity
        setContentView(R.layout.activity_main);

        MBI = new MavisBindingInterface(this);

        // load MavisWebUI
        final WebView webView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface( MBI , "Mavis");

        webView.loadUrl("file:///android_asset/index.html");

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        // end the tts service
        MBI.destroy();
    }
}
