# PerfectWebView
easy to use webview

#just use MyWebView replace your Webview. 
##if you want to add loading progressbar,just add '''my_webview.setLoading(ProgressBar pb)''',but if you want to add loadingDialog instead of loading progressbar,just use '''my_webview.setLoadingDialog(ProgressDialog pd)''' replace
##'''my_webview.setMyTitle(TextView tv)''' can set webview title on this component
##if you want to handle error situation,just like 
'''my_webView.setErrorWebViewListener(new MyWebView.IErrorWebViewListener() {
            @Override
            public void onNotNetError() {
                Log.i(TAG, "onNotNetError: ");
            }

            @Override
            public void onOtherError(int errorCode, String description, String failingUrl) {
                switch (errorCode){
                    // Not found
                    case 404:
                        break;
                    // Internal server error
                    case 500:
                        break;
                    default:
                        break;
                }
            }
        });'''
