// File: C:\kasir\android\app\src\main\java\com\kasir\app\MainActivity.java

package com.kasir.app;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onBridgeReady() {
        super.onBridgeReady();
        
        WebView webView = getBridge().getWebView();
        if (webView != null) {
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.addJavascriptInterface(new JavaScriptInterface(this, webView), "Android");
            Log.d("MainActivity", "JavaScriptInterface berhasil dipasang.");
        } else {
            Log.e("MainActivity", "WebView tidak ditemukan. JavaScriptInterface gagal dipasang.");
        }
    }
}
