package br.com.thiengo.thiengocalopsitafbexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import br.com.thiengo.thiengocalopsitafbexample.domain.User;
import br.com.thiengo.thiengocalopsitafbexample.domain.util.LibraryClass;

public class LoginActivity extends CommonActivity {

    private Firebase firebase;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebase = LibraryClass.getFirebase();
        initViews();
        verifyUserLogged();
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
