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

public class UpdatePasswordActivity extends AppCompatActivity implements ValueEventListener {
    private Toolbar toolbar;
    private User user;
    private EditText newPassword;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        toolbar.setTitle( getResources().getString(R.string.update_password) );
        newPassword = (EditText) findViewById(R.id.new_password);
        password = (EditText) findViewById(R.id.password);
        user = new User();
        user.contextDataDB( this );
    }

    public void update( View view ){
        user.setNewPassword( newPassword.getText().toString() );
        user.generateCryptNewPassword();
        user.setPassword( password.getText().toString() );
        user.generateCryptPassword();

        Firebase firebase = LibraryClass.getFirebase();
        firebase.changePassword(
            user.getEmail(),
            user.getPassword(),
            user.getNewPassword(),
            new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    Toast.makeText(
                        UpdatePasswordActivity.this,
                        "Senha atualizada com sucesso",
                        Toast.LENGTH_SHORT
                    ).show();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    Toast.makeText(
                        UpdatePasswordActivity.this,
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
