package com.ascend.www.abkms.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ascend.www.abkms.NewDashboard;
import com.ascend.www.abkms.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ContactUsActivity extends AppCompatActivity {


    ImageView back_btn_toolbar;
    TextView til_text,tv_emailId,tv_mobile_no;
    WebView Webview_AddressLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        back_btn_toolbar = (ImageView)findViewById(R.id.back_btn_toolbar);
        back_btn_toolbar.setVisibility(View.VISIBLE);
        til_text = (TextView)findViewById(R.id.til_text);

        back_btn_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        til_text.setText("Contact us");

        tv_emailId= (TextView)findViewById(R.id.tv_emailId);
        Linkify.addLinks(tv_emailId, Linkify.EMAIL_ADDRESSES);
        tv_emailId.setMovementMethod(LinkMovementMethod.getInstance());

        tv_mobile_no= (TextView)findViewById(R.id.tv_mobile_no);
        Linkify.addLinks(tv_mobile_no, Linkify.PHONE_NUMBERS);
        tv_mobile_no.setMovementMethod(LinkMovementMethod.getInstance());


        Webview_AddressLocation = (WebView)findViewById(R.id.Webview_AddressLocation);
        Webview_AddressLocation.getSettings().setJavaScriptEnabled(true);
        // webView.loadUrl(getAssets().toString()sc_chart.html);
        //Webview_AddressLocation.loadUrl("file:///android_asset/www/web_epf_chart.html");

        loadWebViewDatafinal(Webview_AddressLocation, "file:///android_asset/www/web_map_view.html");


    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebViewDatafinal(WebView webview_addressLocation, String url) {
        WebSettings ws = webview_addressLocation.getSettings();

        ws.setJavaScriptEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        ws.setAllowFileAccess(true);
        ws.setAllowUniversalAccessFromFileURLs(true);
        ws.setBuiltInZoomControls(false);
        ws.setAllowContentAccess(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            try {
                Log.e("WEB_VIEW_JS", "Enabling HTML5-Features");
                Method m1 = WebSettings.class.getMethod("setDomStorageEnabled", new Class[]{Boolean.TYPE});
                m1.invoke(ws, Boolean.TRUE);

                Method m2 = WebSettings.class.getMethod("setDatabaseEnabled", new Class[]{Boolean.TYPE});
                m2.invoke(ws, Boolean.TRUE);

                Method m3 = WebSettings.class.getMethod("setDatabasePath", new Class[]{String.class});
                m3.invoke(ws, "/data/data/" + getApplicationContext().getPackageName() + "/databases/");

                Method m4 = WebSettings.class.getMethod("setAppCacheMaxSize", new Class[]{Long.TYPE});
                m4.invoke(ws, 1024 * 1024 * 8);

                Method m5 = WebSettings.class.getMethod("setAppCachePath", new Class[]{String.class});
                m5.invoke(ws, "/data/data/" + getApplicationContext().getPackageName() + "/cache/");

                Method m6 = WebSettings.class.getMethod("setAppCacheEnabled", new Class[]{Boolean.TYPE});
                m6.invoke(ws, Boolean.TRUE);

                Log.e("WEB_VIEW_JS", "Enabled HTML5-Features");
            } catch (NoSuchMethodException e) {
                Log.e("WEB_VIEW_JS", "Reflection fail", e);
            } catch (InvocationTargetException e) {
                Log.e("WEB_VIEW_JS", "Reflection fail", e);
            } catch (IllegalAccessException e) {
                Log.e("WEB_VIEW_JS", "Reflection fail", e);
            }
        }

        webview_addressLocation.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), NewDashboard.class);
        startActivity(intent);
        overridePendingTransition(R.animator.left_right,R.animator.right_left);
        finish();
    }
}
