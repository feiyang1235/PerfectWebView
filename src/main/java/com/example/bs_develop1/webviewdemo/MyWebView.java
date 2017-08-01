package com.example.bs_develop1.webviewdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by ermao on 2017/8/1.
 */

public class MyWebView extends WebView {
    private TextView title;
    private ProgressBar loading;
    private ProgressDialog loadingDialog;
    private Context context;
    private IErrorWebViewListener mListener;
    private static final String TAG = "MyWebView";
    public TextView getMyTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public ProgressBar getLoading() {
        return loading;
    }

    public void setLoading(ProgressBar loading) {
        this.loading = loading;
    }

    public ProgressDialog getLoadingDialog() {
        return loadingDialog;
    }

    public void setLoadingDialog(ProgressDialog loadingDialog) {
        this.loadingDialog = loadingDialog;
    }

    public MyWebView(Context context) {
        this(context, null);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    private void init() {
        initWebViewSetting();
        setWebViewClient(new MyWebViewClient());
        setWebChromeClient(new MyWebChromeClient());
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
    }

    public class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                context.startActivity(intent);
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (loadingDialog != null) {
                // in standard case YourActivity.this
//                progressDialog = new ProgressDialog(ShowWebView.this);
                loadingDialog.setMessage("Loading...");
                loadingDialog.show();
            }
        }

        //Show loader on url load
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        public void onPageFinished(WebView view, String url) {
            try {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.hide();
//                    progressDialog = null;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.i(TAG, "onOtherError errorCode: "+errorCode);
            Log.i(TAG, "onOtherError description: "+description);
            Log.i(TAG, "onOtherError failingUrl: "+failingUrl);
            /** -8  net::ERR_CONNECTION_TIMED_OUT   http://www.google.com/
             *  -2   net::ERR_NAME_NOT_RESOLVED    http://www2.google.com/
             * */
            if (loading != null) {
                loading.setVisibility(View.GONE);
            }
            if (mListener != null){
                if (errorCode!=-2) {
                    mListener.onOtherError(errorCode, description, failingUrl);
                }
                else {
                    mListener.onNotNetError();
                }
            }
            else
                switch (errorCode) {
                    case 404:
                        if (loading != null) {
                            loading.setVisibility(View.GONE);
                            view.loadUrl("file:///android_asset/error.html"); //代表各项错误需要处理的事
                        }
                        break;
                    case 101:
                        if (loading != null) {
                            loading.setVisibility(View.GONE);
                            view.loadUrl("file:///android_asset/error.html");
                        }
                        break;
                    //disconnected
                    case -2:
                        if (loading != null) {
                            loading.setVisibility(View.GONE);
                            view.loadUrl("file:///android_asset/error.html");
                        }
                        break;
                    default:
                        if (loading != null) {
                            loading.setVisibility(View.GONE);
                            view.loadUrl("file:///android_asset/error.html");
                        }
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
    }

    public class MyWebChromeClient extends WebChromeClient {
        //加载进度
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (loading == null) return;
            if (newProgress < 100) {
                loading.setVisibility(View.VISIBLE);
                loading.setProgress(newProgress);
            } else {
                loading.setVisibility(View.GONE);
            }
        }

        //标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (MyWebView.this.title != null)
                MyWebView.this.title.setText(title);
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    @Override
    public void loadUrl(String url) {
        if (haveNetworkConnection())
            super.loadUrl(url);
        else {
            if (loading != null) loading.setVisibility(View.GONE);
            if (mListener != null) mListener.onNotNetError();
            else super.loadUrl("file:///android_asset/error.html"); //加载assets事先放置的html页面
        }
    }

    private void initWebViewSetting() {
        //声明WebSettings子类
        WebSettings webSettings = this.getSettings();
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

    @Override
    public void destroy() {
        super.destroy();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
        title = null;
        loading = null;
    }

    public void setErrorWebViewListener(IErrorWebViewListener listener) {
        mListener = listener;
    }

    public interface IErrorWebViewListener {
        void onNotNetError();

        void onOtherError(int errorCode, String description, String failingUrl);
    }
}
