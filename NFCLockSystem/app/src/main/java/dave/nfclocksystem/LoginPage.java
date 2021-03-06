package dave.nfclocksystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoginPage extends ActionBarActivity implements OnClickListener {

    private EditText user, pass;
    private Button mSubmit, mRegister, mReset;
    public static String appuser;


    //Process Dialog
    private ProgressDialog pDialog;

    //Json Parser
    JSONParser jsonParser = new JSONParser();

    //RPi IP
    //private static final String RPi_IP = "http://192.168.1.11";

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
        mReset = (Button) findViewById(R.id.resetPassword);

        //Register Listeners
        mSubmit.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mReset.setOnClickListener(this);

/*
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
*/
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
/*
            case R.id.resetPassword:
                Intent intent = new Intent(this, ResetPassword.class);
                startActivity(intent);
                break;
*/
                default:
                    break;
            }
        }
    }

    class AttemptLogin extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginPage.this);
            pDialog.setMessage("Sending Data to Database...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            String String_username = user.getText().toString();
            String String_password = pass.getText().toString();
            appuser=String_username;
            int success;


            try{

                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", String_username));
                params.add(new BasicNameValuePair("password", String_password));

                Log.d("request!", "starting");
                JSONObject json = jsonParser.makeHttpRequest("http://192.168.1.11/login2.php", "POST", params);

                Log.d("Login attempt", json.toString());
                success = json.getInt("success");

                if(success == 1){
                    Log.d("Login Successful!", json.toString());
                    Intent i = new Intent(getApplicationContext(), Homepage.class);
                    startActivity(i);
                    finish();
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url){
            pDialog.dismiss();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login_page, container, false);
            return rootView;
        }
    }
}
