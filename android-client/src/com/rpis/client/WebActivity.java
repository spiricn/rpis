
package com.rpis.client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class WebActivity extends Activity {
    public static final String EXTRA_KEY_URL = "url";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        mWebClient = new WebClient((ProgressBar) findViewById(R.id.progressBar));

        mWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.clearFormData();
        mWebView.clearHistory();
        mWebView.clearCache(true);

        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                getWindow().setTitle(title); // Set Activity tile to page title.
            }
        });

        mWebView.setWebViewClient(mWebClient);

        String url = getIntent().getExtras().getString(EXTRA_KEY_URL);

        mWebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        mWebView.goBack();
    }

    private WebView mWebView;
    private WebClient mWebClient;
}
