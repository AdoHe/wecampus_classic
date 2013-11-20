package com.westudio.wecampus.ui.about;

import android.os.Bundle;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Created by nankonami on 13-10-4.
 * Terms of Use Activity
 */
public class TermsOfUseActivity extends SherlockFragmentActivity {

    private static final String URL = "http://wecampus.net/terms.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Update the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebView webView = new WebView(this);
        webView.loadUrl(URL);
        setContentView(webView);
    }
}
