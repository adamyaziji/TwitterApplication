package com.twitterapp.login;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

/**
 * Created by Dominic on 10-Apr-16.
 */
class TokenGet extends AsyncTask<String,String,String> {
    String oauth_url;
    Twitter twitter;

    public static String getOauth_verifier() {
        return oauth_verifier;
    }

    static String oauth_verifier;

    public static RequestToken getRequestToken() {
        return requestToken;
    }

    private static RequestToken requestToken;

    @Override
    protected String doInBackground(String... params) {
        twitter = MainActivity.getTwitter();
        Log.d("create", "request token asynctask)");
        try {
            requestToken = twitter.getOAuthRequestToken();
            oauth_url = requestToken.getAuthorizationURL();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return oauth_url;
    }

    protected void onPostExecute(String oauth_url) {
        if (oauth_url != null) {
            Log.e("URL", oauth_url);
            final Dialog auth_dialog = new Dialog(MainActivity.getContext());
            auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            auth_dialog.setContentView(R.layout.auth_webview);
            WebView web = (WebView) auth_dialog.findViewById(R.id.webv);
            web.getSettings().setJavaScriptEnabled(true);
            web.loadUrl(oauth_url);
            web.setWebViewClient(new WebViewClient() {
                boolean authComplete = false;

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (url.contains("oauth_verifier") && authComplete == false) {
                        authComplete = true;
                        Log.e("Url", url);
                        Uri uri = Uri.parse(url);
                        oauth_verifier = uri.getQueryParameter("oauth_verifier");
                        auth_dialog.dismiss();
                        new AccessTokenGet().execute();
                    } else if (url.contains("denied")) {
                        auth_dialog.dismiss();
                        Toast.makeText(MainActivity.getContext(), "Sorry !, Permission Denied",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            auth_dialog.show();
            auth_dialog.setCancelable(true);

        } else {
            Toast.makeText(MainActivity.getContext(), "Sorry !, Network Error or Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }
}