package br.com.thiengo.thiengocalopsitafbexample.listener;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import br.com.thiengo.thiengocalopsitafbexample.domain.User;


public class CustomValueEventListener implements ValueEventListener {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        for( DataSnapshot d : dataSnapshot.getChildren() ){
            User u = d.getValue( User.class );

            Log.i("log", "Name: "+u.getName());
            Log.i("log", "Email: "+u.getEmail());
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {}
}
