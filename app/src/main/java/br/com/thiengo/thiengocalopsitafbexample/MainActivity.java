package br.com.thiengo.thiengocalopsitafbexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import br.com.thiengo.thiengocalopsitafbexample.adapter.UserRecyclerAdapter;
import br.com.thiengo.thiengocalopsitafbexample.adapter.UserViewHolder;
import br.com.thiengo.thiengocalopsitafbexample.domain.User;
import br.com.thiengo.thiengocalopsitafbexample.domain.util.LibraryClass;


public class MainActivity extends AppCompatActivity {

    private Firebase firebase;
    private UserRecyclerAdapter adapter;
    private Firebase.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if( authData == null ){
                    Intent intent = new Intent( MainActivity.this, LoginActivity.class );
                    startActivity( intent );
                    finish();
                }
            }
        };

        firebase = LibraryClass.getFirebase().child("users");
        firebase.addAuthStateListener( authStateListener );
    }


    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        RecyclerView rvUsers = (RecyclerView) findViewById(R.id.rv_users);
        rvUsers.setHasFixedSize( true );
        rvUsers.setLayoutManager( new LinearLayoutManager(this));

        adapter = new UserRecyclerAdapter(
                User.class,
                android.R.layout.two_line_list_item,
                UserViewHolder.class,
                firebase );

        rvUsers.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
        firebase.removeAuthStateListener( authStateListener );
    }


    // MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_update){
            startActivity(new Intent(this, UpdateActivity.class));
        }
        else if(id == R.id.action_update_login){
            startActivity(new Intent(this, UpdateLoginActivity.class));
        }
        else if(id == R.id.action_update_password){
            startActivity(new Intent(this, UpdatePasswordActivity.class));
        }
        else if(id == R.id.action_remove_user){
            startActivity(new Intent(this, RemoveUserActivity.class));
        }
        else if(id == R.id.action_logout){
            firebase.unauth();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
