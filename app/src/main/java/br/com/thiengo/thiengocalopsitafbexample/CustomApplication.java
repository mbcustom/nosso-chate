package br.com.thiengo.thiengocalopsitafbexample;

import android.app.Application;

import com.firebase.client.Firebase;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

/**
 * Created by viniciusthiengo on 3/14/16.
 */
public class CustomApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "wZrZCM8CRkJBYdPq8Pj8Ax4ME";
    private static final String TWITTER_SECRET = "82DDDDGtC3QOXWmZbo5Y0aIEfpO387RzrXXmK7CtEwEkznh5BN";


    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        Firebase.setAndroidContext(this);
    }
}