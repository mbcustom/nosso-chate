package br.com.thiengo.thiengocalopsitafbexample;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.Arrays;

import br.com.thiengo.thiengocalopsitafbexample.domain.User;
import br.com.thiengo.thiengocalopsitafbexample.domain.util.LibraryClass;


public class LoginActivity extends CommonActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN_GOOGLE = 7859;

    private Firebase firebase;
    private User user;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // FACEBOOK
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


        // GOOGLE SIGN IN
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("")
                .requestEmail()
                .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        firebase = LibraryClass.getFirebase();
        initViews();
        verifyUserLogged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == RC_SIGN_IN_GOOGLE
                && resultCode == RESULT_OK ){

            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent( data );
            GoogleSignInAccount account = googleSignInResult.getSignInAccount();

            Log.i("LOG", "Id: "+account.getId());
            Log.i("LOG", "Email: "+account.getEmail());
            Log.i("LOG", "Token: "+account.getIdToken());
            syncGoogleSignInToken( account );
        }
        else{
            callbackManager.onActivityResult( requestCode, resultCode, data );
        }
    }




    private void accessFacebookLoginData(AccessToken accessToken){
        accessLoginData(
            "facebook",
            (accessToken != null ? accessToken.getToken() : null)
        );
    }

    private void accessGoogleLoginData(String accessToken){
        accessLoginData(
            "google",
            accessToken
        );
    }

    private void accessLoginData( String provider, String token ){
        if( token != null ){

            firebase.authWithOAuthToken(
                provider,
                token,
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

    private void syncGoogleSignInToken( GoogleSignInAccount googleSignInAccount ){
        AsyncTask<GoogleSignInAccount, Void, String> task = new AsyncTask<GoogleSignInAccount, Void, String>() {
            @Override
            protected String doInBackground(GoogleSignInAccount... params) {
                GoogleSignInAccount gsa = params[0];
                String scope = "oauth2:profile email";
                String token = null;

                try {
                    token = GoogleAuthUtil.getToken( LoginActivity.this, gsa.getEmail(), scope );
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                }

                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                super.onPostExecute(token);

                if( token != null ){
                    accessGoogleLoginData( token );
                }
                else{
                    showSnackbar("Google login falhou, tente novamente");
                }
            }
        };

        task.execute(googleSignInAccount);
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

    public void sendLoginGoogleData( View view ){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showSnackbar( connectionResult.getErrorMessage() );
    }
}
