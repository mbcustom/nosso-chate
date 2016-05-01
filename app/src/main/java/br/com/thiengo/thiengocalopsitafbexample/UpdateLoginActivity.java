package br.com.thiengo.thiengocalopsitafbexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import br.com.thiengo.thiengocalopsitafbexample.domain.User;
import br.com.thiengo.thiengocalopsitafbexample.domain.util.LibraryClass;

public class UpdateLoginActivity extends AppCompatActivity implements ValueEventListener {
    private Toolbar toolbar;
    private User user;
    private AutoCompleteTextView newEmail;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        toolbar.setTitle( getResources().getString(R.string.update_login) );
        newEmail = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        user = new User();
        user.contextDataDB( this );
    }

    public void update( View view ){

        user.setPassword( password.getText().toString() );
        user.generateCryptPassword();

        Firebase firebase = LibraryClass.getFirebase();
        firebase.changeEmail(
            user.getEmail(),
            user.getPassword(),
            newEmail.getText().toString(),
            new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                user.setEmail( newEmail.getText().toString() );
                user.updateDB();
                Toast.makeText(
                    UpdateLoginActivity.this,
                    "Email de login atualizado com sucesso",
                    Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(
                    UpdateLoginActivity.this,
                    firebaseError.getMessage(),
                    Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue( User.class );
        newEmail.setText( u.getEmail() );
        user.setEmail( u.getEmail() );
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {}
}
