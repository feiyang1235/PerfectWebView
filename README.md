 
# PerfectWebView
easy to use webview

# just use MyWebView replace your Webview. 
if you want to add loading progressbar,just use 
#### my_webview.setLoading(ProgressBar pb)
but if you want to add loadingDialog instead of loading progressbar,just use 
#### my_webview.setLoadingDialog(ProgressDialog pd)
if you want to set webview title,use
#### my_webview.setMyTitle(TextView tv)
if you want to handle error situation,just like 
'''my_webView.setErrorWebViewListener(new MyWebView.IErrorWebViewListener() {
            @Override
            public void onNotNetError() {
                Log.i(TAG, "onNotNetError: ");
            }

            @Override
            public void onOtherError(int errorCode, String description, String failingUrl) {
                switch (errorCode){
                    // Not found
                    case ERROR_URL_NAME:
                        break;
                    // Internal server error
                    case ERROR_NAME_SOLVE:
                        break;
                    default:
                        break;
                }
            }
        });'''
