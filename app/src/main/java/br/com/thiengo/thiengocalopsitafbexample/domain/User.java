package br.com.thiengo.thiengocalopsitafbexample.domain;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import br.com.thiengo.thiengocalopsitafbexample.domain.util.CryptWithMD5;
import br.com.thiengo.thiengocalopsitafbexample.domain.util.LibraryClass;


public class User {
    public static String PROVIDER = "br.com.thiengo.thiengocalopsitafbexample.domain.User.PROVIDER";
    public static String ID = "br.com.thiengo.thiengocalopsitafbexample.domain.User.ID";


    private String id;
    private String name;
    private String email;
    private String password;
    private String newPassword;


    public User(){}



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void saveIdSP(Context context, String token ){
        LibraryClass.saveSP( context, ID, token );
    }

    public void retrieveIdSP(Context context ){
        this.id = LibraryClass.getSP( context, ID );
    }

    public boolean isSocialNetworkLogged( Context context ){
        String token = getProviderSP( context );
        return( token.contains("facebook") || token.contains("google") || token.contains("twitter") || token.contains("github") );
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void setNameInMap( Map<String, Object> map ) {
        if( getName() != null ){
            map.put( "name", getName() );
        }
    }

    public void setNameIfNull(String name) {
        if( this.name == null ){
            this.name = name;
        }
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private void setEmailInMap( Map<String, Object> map ) {
        if( getEmail() != null ){
            map.put( "email", getEmail() );
        }
    }

    public void setEmailIfNull(String email) {
        if( this.email == null ){
            this.email = email;
        }

    }


    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void generateCryptPassword() {
        password = CryptWithMD5.cryptWithMD5(password);
    }


    @Exclude
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void generateCryptNewPassword() {
        newPassword = CryptWithMD5.cryptWithMD5(newPassword);
    }



    public void saveProviderSP(Context context, String token ){
        LibraryClass.saveSP( context, PROVIDER, token );
    }
    public String getProviderSP(Context context ){
        return( LibraryClass.getSP( context, PROVIDER) );
    }



    public void saveDB( DatabaseReference.CompletionListener... completionListener ){
        DatabaseReference firebase = LibraryClass.getFirebase().child("users").child( getId() );

        if( completionListener.length == 0 ){
            firebase.setValue(this);
        }
        else{
            firebase.setValue(this, completionListener[0]);
        }
    }

    public void updateDB( DatabaseReference.CompletionListener... completionListener ){

        DatabaseReference firebase = LibraryClass.getFirebase().child("users").child( getId() );

        Map<String, Object> map = new HashMap<>();
        setNameInMap(map);
        setEmailInMap(map);

        if( map.isEmpty() ){
            return;
        }

        if( completionListener.length > 0 ){
            firebase.updateChildren(map, completionListener[0]);
        }
        else{
            firebase.updateChildren(map);
        }
    }

    public void removeDB(){
        DatabaseReference firebase = LibraryClass.getFirebase().child("users").child( getId() );
        firebase.setValue(null);
    }

    public void contextDataDB( Context context ){
        retrieveIdSP( context );
        DatabaseReference firebase = LibraryClass.getFirebase().child("users").child( getId() );

        firebase.addListenerForSingleValueEvent( (ValueEventListener) context );
    }
}