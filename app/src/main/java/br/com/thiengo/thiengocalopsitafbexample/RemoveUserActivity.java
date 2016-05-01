package br.com.thiengo.thiengocalopsitafbexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import br.com.thiengo.thiengocalopsitafbexample.domain.User;
import br.com.thiengo.thiengocalopsitafbexample.domain.util.LibraryClass;

public class RemoveUserActivity extends AppCompatActivity implements ValueEventListener {

    private Toolbar toolbar;
    private User user;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_user);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        toolbar.setTitle( getResources().getString(R.string.remove_user) );
        password = (EditText) findViewById(R.id.password);

        user = new User();
        user.contextDataDB( this );
    }

    public void update( View view ){
        user.setPassword( password.getText().toString() );
        user.generateCryptPassword();

        Firebase firebase = LibraryClass.getFirebase();
        firebase.removeUser(
            user.getEmail(),
            user.getPassword(),
            new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    user.retrieveIdSP( RemoveUserActivity.this );
                    user.removeDB();
                    Toast.makeText(
                        RemoveUserActivity.this,
                        "Conta removida com sucesso",
                        Toast.LENGTH_SHORT
                    ).show();
                    finish();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    Toast.makeText(
                        RemoveUserActivity.this,
                        firebaseError.getMessage(),
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }
        );
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue( User.class );
        user.setEmail( u.getEmail() );
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {}
}
