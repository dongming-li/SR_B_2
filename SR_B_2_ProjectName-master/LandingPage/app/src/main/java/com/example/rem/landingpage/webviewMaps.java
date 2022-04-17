package com.example.rem.landingpage;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.webkit.WebView;
import android.app.ActionBar;


/**
* Enables javascript to run within an android screen, reaches out to the server, then loads the webpage based on the specified URL.
*/
public class webviewMaps extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

//Get a reference to your WebView//
        WebView webView = (WebView) findViewById(R.id.webview);



//Specify the URL you want to display//
        webView.loadUrl("http://proj-309-sr-b-2.cs.iastate.edu/MyVote411.html");

        //enable Javascript
        webView.getSettings().setJavaScriptEnabled(true);

    }
}
