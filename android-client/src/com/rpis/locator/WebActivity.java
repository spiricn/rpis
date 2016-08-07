
package com.rpis.locator;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebActivity extends Activity {
    public static final String EXTRA_KEY_URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        
        mWebView = (WebView)findViewById(R.id.webView1);
        
        String url = getIntent().getExtras().getString(EXTRA_KEY_URL);
        
        mWebView.loadUrl(url);
    }

    private WebView mWebView;
}
