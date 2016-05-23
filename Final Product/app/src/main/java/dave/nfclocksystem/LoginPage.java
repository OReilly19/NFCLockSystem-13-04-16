package dave.nfclocksystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoginPage extends ActionBarActivity implements OnClickListener {

    private EditText user, pass;
    private Button mSubmit, mRegister;


    //Process Dialog
    private ProgressDialog pDialog;

    //Json Parser
    JSONParser jsonParser = new JSONParser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //Setup User Input Field
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);

        //Setup Buttons
        mSubmit = (Button) findViewById(R.id.login);
        mRegister = (Button) findViewById(R.id.register);

        //Register Listeners
        mSubmit.setOnClickListener(this);
        mRegister.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onClick(View v){
        // TODO Auto-generated method stub
        if(isOnline()){
            switch (v.getId()) {
                case R.id.login:
                    new AttemptLogin().execute();
                    break;

            case R.id.register:
                Intent i = new Intent(this, Register.class);
                startActivity(i);
                break;
                default:
                    break;
            }
        }
        else{
            Toast.makeText(LoginPage.this, "Please ensure device is connected to the internet", Toast.LENGTH_LONG).show();

        }
    }



    class AttemptLogin extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginPage.this);
            pDialog.setMessage("Getting User Credentials");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... args) {
            String String_username = user.getText().toString();
            String String_password = pass.getText().toString();
            int success;


            try{

                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", String_username));
                params.add(new BasicNameValuePair("password", String_password));

                Log.d("request!", "starting");
                JSONObject json = jsonParser.makeHttpRequest("http://52.18.47.165/login2.php", "POST", params);

                Log.d("Login attempt", json.toString());
                success = json.getInt("success");

                if(success == 1){
                    SharedPreferences pref = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.putString("username", String_username);
                    editor.apply();
                    Log.d("Login Successful!", json.toString());
                    Intent i = new Intent(getApplicationContext(), Homepage.class);
                    startActivity(i);
                    finish();
                    return json.getString("message");
                }
                else{
                    return json.getString("message");
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url){
            pDialog.dismiss();
            Toast.makeText(LoginPage.this, file_url, Toast.LENGTH_LONG).show();

        }

    }
}
