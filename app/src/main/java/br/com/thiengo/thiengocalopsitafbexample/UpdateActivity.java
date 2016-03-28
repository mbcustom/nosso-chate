package br.com.thiengo.thiengocalopsitafbexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;

import br.com.thiengo.thiengocalopsitafbexample.domain.User;

public class UpdateActivity extends AppCompatActivity {

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
        toolbar.setTitle( getResources().getString(R.string.atualizar) );
        name = (AutoCompleteTextView) findViewById(R.id.name);
        user = new User();
    }


    public void update( View view ){
        user.setName( name.getText().toString() );
    }
}
