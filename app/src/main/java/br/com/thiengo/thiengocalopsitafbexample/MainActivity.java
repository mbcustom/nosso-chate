package br.com.thiengo.thiengocalopsitafbexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import br.com.thiengo.thiengocalopsitafbexample.adapter.UserRecyclerAdapter;
import br.com.thiengo.thiengocalopsitafbexample.adapter.UserViewHolder;
import br.com.thiengo.thiengocalopsitafbexample.domain.User;
import br.com.thiengo.thiengocalopsitafbexample.domain.util.LibraryClass;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Firebase firebase;
    private UserRecyclerAdapter adapter;
    private Firebase.AuthStateListener authStateListener;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* PARA GOOGLE LOGOUT */
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("")
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

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
        User user = new User();

        if( user.isSocialNetworkLogged( this ) ){
            getMenuInflater().inflate(R.menu.menu_social_network_logged, menu);
        }
        else{
            getMenuInflater().inflate(R.menu.menu, menu);
        }
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

            LoginManager.getInstance().logOut();

            if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ){
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            }

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}
}
