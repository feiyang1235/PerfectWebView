package com.example.bs_develop1.webviewdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.view.KeyEvent.KEYCODE_BACK;

/**
 * Created by bs_develop1 on 2017/8/1.
 */

public class ShowWebView extends Activity {
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    //private Button button;
    private WebView webView;
    private ImageView back;
    private TextView title;
    private ProgressBar progress;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This will not show title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_web_view);
        //Get webview
        webView = findViewById(R.id.webView);
        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
        progress = findViewById(R.id.progress);
        initWebViewSetting();
        if (haveNetworkConnection()) {
            startWebView("http://www.doubleflyer.com/");
        } else {
            progress.setVisibility(View.GONE);
            webView.loadUrl("file:///android_asset/error.html");
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoBack()) webView.goBack();
                else {
                    finish();
                    System.exit(0);
                }
            }
        });
    }

    private void initWebViewSetting() {
        //声明WebSettings子类
        WebSettings webSettings = webView.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
    }


    private void startWebView(String url) {
        //Create new webview Client to show progress dialog
        //When opening a url or click on link
        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            //If you will not use this method url links are opeen in new brower not in webview
            //If url has "tel:245678" , on clicking the number it will directly call to inbuilt calling feature of phone
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(ShowWebView.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            }

            //Show loader on url load
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                switch (errorCode) {
                    case 404:
                        progress.setVisibility(View.GONE);
                        view.loadUrl("file:///android_asset/error.html");
                        break;
                    case 101:
                        progress.setVisibility(View.GONE);
                        view.loadUrl("file:///android_asset/error.html");
                        break;
                    case -2:
                        progress.setVisibility(View.GONE);
                        view.loadUrl("file:///android_asset/error.html");
                        break;
                    default:
                        progress.setVisibility(View.GONE);
                        view.loadUrl("file:///android_asset/error.html");
                        break;
                }
            }

            //处理https请求
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();    //表示等待证书响应
                // handler.cancel();      //表示挂起连接，为默认方式
                // handler.handleMessage(null);    //可做其他处理
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            //加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progress.setVisibility(View.VISIBLE);
                    progress.setProgress(newProgress);
                } else {
                    progress.setVisibility(View.GONE);
                }
            }
            //标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                ShowWebView.this.title.setText(title);
            }
        });
        webView.loadUrl(url);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
//            webView.reload();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}