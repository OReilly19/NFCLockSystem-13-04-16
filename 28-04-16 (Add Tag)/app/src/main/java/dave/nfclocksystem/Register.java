package dave.nfclocksystem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Register extends Activity implements OnClickListener{

    private EditText mail,user, pass;
    private Button  mRegister;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mail = (EditText)findViewById(R.id.email);
        user = (EditText)findViewById(R.id.username);
        pass = (EditText)findViewById(R.id.password);


        mRegister = (Button)findViewById(R.id.register);
        mRegister.setOnClickListener(this);

    }



        @Override
        public void onClick(View v) {
            new CreateUser().execute();
        }

        class CreateUser extends AsyncTask<String, String, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(Register.this);
                pDialog.setMessage("Creating User...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }

            @Override
            protected String doInBackground(String... args) {

                // Check for success tag
                int success;
                String String_email    = mail.getText().toString();
                String String_username = user.getText().toString();
                String String_password = pass.getText().toString();

                try {
                    // Building Parameters
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("email",String_email));
                    params.add(new BasicNameValuePair("username", String_username));
                    params.add(new BasicNameValuePair("encrypt_password", String_password));

                    Log.d("request!", "starting");

                    //Posting user data to script
                    JSONObject json = jsonParser.makeHttpRequest("http://192.168.1.11/register2.php", "POST", params);

                    Log.d("Registering attempt", json.toString());

                    // json success element
                    success = json.getInt("success");

                    if (success == 1) {
                        Log.d("User Created!", json.toString());
                        finish();
                        return json.getString("message");
                    } else {
                        Log.d("Registering Failure!", json.getString("message"));
                        return json.getString("message");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;

            }

            protected void onPostExecute(String file_url) {
                // dismiss the dialog once product deleted
                pDialog.dismiss();

            }

    }


}