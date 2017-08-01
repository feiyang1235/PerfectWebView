package com.example.bs_develop1.webviewdemo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.view.KeyEvent.KEYCODE_BACK;

/**
 * Created by ermao on 2017/8/1.
 */

public class MainActivity extends AppCompatActivity {
    private MyWebView my_webView;
    private ImageView back;
    private TextView title;
    private ProgressBar progress;
    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: 123");
        setContentView(R.layout.activity_main);
        my_webView = (MyWebView) findViewById(R.id.my_webview);
        title = (TextView) findViewById(R.id.title);
        back = (ImageView) findViewById(R.id.back);
        progress = (ProgressBar) findViewById(R.id.progress);
        my_webView.setTitle(title);
        my_webView.setLoading(progress);
        my_webView.loadUrl("http://www.doubleflyer.com/");
        //如果需要处理错误情况，设置如下监听，如果不设置。则用默认处理方式
//        my_webView.setErrorWebViewListener(new MyWebView.IErrorWebViewListener() {
//            @Override
//            public void onNotNetError() {
//                //这里处理没有网络
//                Log.i(TAG, "onNotNetError: ");
//            }
//
//            @Override
//            public void onOtherError(int errorCode, String description, String failingUrl) {
//                //这里处理非网络错误的情况
//                switch (errorCode){
//                    // Not found
//                    case 404:
//                        break;
//                    // Internal server error
//                    case 500:
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && my_webView.canGoBack()) {
            my_webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void back(View view) {
        if (my_webView.canGoBack()) my_webView.goBack();
        else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //处理webView内存泄漏
        if (my_webView != null) {
            my_webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            my_webView.clearHistory();

            ((ViewGroup) my_webView.getParent()).removeView(my_webView);
            my_webView.destroy();
            my_webView = null;
        }
    }
}
