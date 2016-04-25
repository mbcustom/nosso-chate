package br.com.thiengo.thiengocalopsitafbexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import br.com.thiengo.thiengocalopsitafbexample.domain.User;

public class UpdateActivity extends AppCompatActivity implements ValueEventListener, Firebase.CompletionListener {

    private Toolbar toolbar;
    private User user;
    private AutoCompleteTextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        toolbar.setTitle( getResources().getString(R.string.update_profile) );
        name = (AutoCompleteTextView) findViewById(R.id.name);
        user = new User();
        user.contextDataDB( this );
    }

    public void update( View view ){
        user.retrieveIdSP( UpdateActivity.this );
        user.setName( name.getText().toString() );
        user.updateDB( UpdateActivity.this );
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue( User.class );
        name.setText( u.getName() );
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {}

    @Override
    public void onComplete(FirebaseError firebaseError, Firebase firebase) {

        if( firebaseError != null ){
            Toast.makeText( this, "Falhou: "+firebaseError.getMessage(), Toast.LENGTH_LONG ).show();
        }
        else{
            Toast.makeText( this, "Atualização realizada com sucesso.", Toast.LENGTH_SHORT ).show();
        }
    }
}
