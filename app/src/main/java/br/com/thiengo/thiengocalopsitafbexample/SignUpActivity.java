package br.com.thiengo.thiengocalopsitafbexample;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import br.com.thiengo.thiengocalopsitafbexample.domain.User;
import br.com.thiengo.thiengocalopsitafbexample.domain.util.LibraryClass;

public class SignUpActivity extends CommonActivity {

    private Firebase firebase;
    private User user;
    private AutoCompleteTextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebase = LibraryClass.getFirebase();
        initViews();
    }

    protected void initViews(){
        name = (AutoCompleteTextView) findViewById(R.id.name);
        email = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.sign_up_progress);
    }

    protected void initUser(){
        user = new User();
        user.setName( name.getText().toString() );
        user.setEmail( email.getText().toString() );
        user.setPassword( password.getText().toString() );
        user.generateCryptPassword();
    }

    public void sendSignUpData( View view ){
        openProgressBar();
        initUser();
        saveUser();
    }

    private void saveUser(){
        firebase.createUser(
            user.getEmail(),
            user.getPassword(),
            new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> stringObjectMap) {
                    user.setId( stringObjectMap.get("uid").toString() );
                    user.saveDB();
                    firebase.unauth();

                    showToast( "Conta criada com sucesso!" );
                    closeProgressBar();
                    finish();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    showSnackbar( firebaseError.getMessage() );
                    closeProgressBar();
                }
            }
        );
    }
}
