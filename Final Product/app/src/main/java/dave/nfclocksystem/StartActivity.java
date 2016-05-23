package dave.nfclocksystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class StartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = this.getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "");
        if (username.length()>0){
            Intent i = new Intent(this, Homepage.class);
            startActivity(i);
        }
        else{
            Intent i = new Intent(this, LoginPage.class);
            startActivity(i);
        }
    }
}
