package com.example.adam.twitterapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;



public class MainActivity extends AppCompatActivity {

    String CONSUMER_KEY = "Y5RKiazZ8HwqPLwCYzNvB3LHL";
    String CONSUMER_SECRET = "WSuZncU2WCI8tBTGOl5WpGKd4Src4nXsHZFydgjfIxsSXQmwbE";
    Twitter twitter;
    AccessToken accessToken;
    String oauth_url,oauth_verifier,profile_url;
    Dialog auth_dialog;
    WebView web;
    RequestToken requestToken;
    SharedPreferences pref;
    ProgressDialog progress;
    Context context;
    Button b_Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

        b_Login = (Button)findViewById(R.id.bLogin);
        b_Login.setOnClickListener(new LoginProcess());
        context = this;

    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private class LoginProcess implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            new TokenGet().execute();
            Log.d("create", "request token asynctask)");

        }
    }

    private class TokenGet extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... params) {

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
                auth_dialog = new Dialog(context);
                auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                auth_dialog.setContentView(R.layout.auth_webview);
                web = (WebView) auth_dialog.findViewById(R.id.webv);
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
                            Toast.makeText(context, "Sorry !, Permission Denied",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                auth_dialog.show();
                auth_dialog.setCancelable(true);

            } else {
                Toast.makeText(context, "Sorry !, Network Error or Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AccessTokenGet extends AsyncTask<String, String, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(context);
            progress.setMessage("Fetching Data ...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();

        }

        @Override
        protected Boolean doInBackground(String... args) {

            try {
                accessToken = twitter.getOAuthAccessToken(requestToken, oauth_verifier);
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("ACCESS_TOKEN", accessToken.getToken());
                edit.putString("ACCESS_TOKEN_SECRET", accessToken.getTokenSecret());
                User user = twitter.showUser(accessToken.getUserId());
                profile_url = user.getOriginalProfileImageURL();
                edit.putString("NAME", user.getName());
                edit.putString("IMAGE_URL", user.getOriginalProfileImageURL());

                edit.commit();


            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();


            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            if(response){
                progress.hide();


                /**
                 * Create Intent to go to profile page!!!
                 */

            }
        }






    }
}
