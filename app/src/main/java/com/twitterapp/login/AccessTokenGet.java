package com.twitterapp.login;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;

class AccessTokenGet extends AsyncTask<String, String, Boolean> {


    private Twitter twitter;
    private SharedPreferences pref;
    private ProgressDialog progress;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new ProgressDialog(MainActivity.getContext());
        progress.setMessage("Fetching Data ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();

    }

    @Override
    protected Boolean doInBackground(String... args) {
        try {
            twitter = MainActivity.getTwitter();
            AccessToken accessToken = twitter.getOAuthAccessToken(TokenGet.getRequestToken(), TokenGet.getOauth_verifier());
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("ACCESS_TOKEN", accessToken.getToken());
            edit.putString("ACCESS_TOKEN_SECRET", accessToken.getTokenSecret());
            User user = twitter.showUser(accessToken.getUserId());
            String profile_url = user.getOriginalProfileImageURL();
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