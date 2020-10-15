package app.snapmate.facebook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;


public class PrivacyPolicy extends AppCompatActivity {

    private  WebView mywebview;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
         mywebview = (WebView) findViewById(R.id.webView);
         mywebview.getSettings().setJavaScriptEnabled(true);
        String fileName = "privacypolicy.html";
        mywebview.loadUrl("file:///android_asset/" + fileName);
    }
}
