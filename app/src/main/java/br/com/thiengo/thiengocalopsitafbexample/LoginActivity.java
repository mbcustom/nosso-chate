package br.com.thiengo.thiengocalopsitafbexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Arrays;

import br.com.thiengo.thiengocalopsitafbexample.domain.User;
import br.com.thiengo.thiengocalopsitafbexample.domain.util.LibraryClass;

public class LoginActivity extends CommonActivity {

    private Firebase firebase;
    private User user;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessFacebookLoginData( loginResult.getAccessToken() );
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException error) {
                showSnackbar( error.getMessage() );
            }
        });

        firebase = LibraryClass.getFirebase();
        initViews();
        verifyUserLogged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult( requestCode, resultCode, data );
    }

    private void accessFacebookLoginData(AccessToken accessToken){
        if( accessToken != null ){

            firebase.authWithOAuthToken(
                "facebook",
                accessToken.getToken(),
                new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        user.saveTokenSP( LoginActivity.this, authData.getToken() );
                        user.saveIdSP( LoginActivity.this, authData.getUid() );
                        user.setId( authData.getUid() );
                        user.setName( authData.getProviderData().get("displayName").toString() );
                        //user.setEmail( authData.getProviderData().get("email").toString() );
                        user.saveDB();

                        callMainActivity();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        showSnackbar( firebaseError.getMessage() );
                    }
            });
        }
        else{
            firebase.unauth();
        }
    }




    protected void initViews(){
        email = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
    }

    protected void initUser(){
        user = new User();
        user.setEmail( email.getText().toString() );
        user.setPassword( password.getText().toString() );
        user.generateCryptPassword();
    }

    public void callSignUp(View view){
        Intent intent = new Intent( this, SignUpActivity.class );
        startActivity(intent);
    }

    public void sendLoginData( View view ){
        openProgressBar();
        initUser();
        verifyLogin();
    }

    public void sendLoginFacebookData( View view ){
        LoginManager
            .getInstance()
            .logInWithReadPermissions(
                this,
                Arrays.asList("public_profile", "user_friends", "email")
            );
    }




    private void verifyUserLogged(){
        if( firebase.getAuth() != null ){
            callMainActivity();
        }
        else{
            initUser();

            if( !user.getTokenSP(this).isEmpty() ){
                firebase.authWithPassword(
                    "password",
                    user.getTokenSP(this),
                    new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            user.saveTokenSP( LoginActivity.this, authData.getToken() );
                            user.saveIdSP( LoginActivity.this, authData.getUid() );
                            callMainActivity();
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {}
                    }
                );
            }
        }
    }

    private void callMainActivity(){
        Intent intent = new Intent( this, MainActivity.class );
        startActivity(intent);
        finish();
    }




    private void verifyLogin(){
        firebase.authWithPassword(
            user.getEmail(),
            user.getPassword(),
            new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    user.saveTokenSP( LoginActivity.this, authData.getToken() );
                    user.saveIdSP( LoginActivity.this, authData.getUid() );
                    closeProgressBar();
                    callMainActivity();
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    showSnackbar( firebaseError.getMessage() );
                    closeProgressBar();
                }
            }
        );
    }
}
