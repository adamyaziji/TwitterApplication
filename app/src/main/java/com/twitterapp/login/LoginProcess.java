package com.twitterapp.login;

import android.util.Log;
import android.view.View;

/**
 * Created by Dominic on 10-Apr-16.
 */
public class LoginProcess implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            new TokenGet().execute();
            Log.d("create", "request token asynctask)");

        }
}

