package dave.nfclocksystem;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static dave.nfclocksystem.Homepage.appuser;


public class ReadTag extends ActionBarActivity implements View.OnClickListener {

    private Button maddThisKey;
    private EditText user;
    private String nfcTagID;
    private int decTag;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // list of the different NFC technologies available. Used to distinguish between NFC types.
    private final String[][] tagsList = new String[][] {
            new String[] {
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(),
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_tag);
        user=(EditText) findViewById(R.id.username);
        maddThisKey = (Button)findViewById(R.id.addThisKey);
        maddThisKey.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_read_tag, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.tagsList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disabling foreground dispatch:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            nfcTagID=ByteArrayToDecString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
            ((TextView)findViewById(R.id.tag)).setText(
                    "NFC Tag is:\n" +
                            nfcTagID);
        }
    }

    public String ByteArrayToDecString(byte [] inarray) {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";
        String temp="";

        for(j = 0 ; j < inarray.length ; j++)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }

        if(out.length() % 2 == 0){
            while(out.length() > 0) {
                String nextChunk = out.substring(0,2);
                decTag=Integer.parseInt(nextChunk, 16);
                temp=temp+Integer.toString(decTag);
                out = out.substring(2,out.length());
            }
        }
        return temp;
    }

    @Override
    public void onClick(View v) {
        new addTag().execute();
    }

    class addTag extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReadTag.this);
            pDialog.setMessage("Adding new key");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            String String_username = user.getText().toString();

                try {
                    // Building Parameters
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("currentUser",appuser));
                    params.add(new BasicNameValuePair("username", String_username));
                    params.add(new BasicNameValuePair("tagID", nfcTagID));

                    Log.d("request!", "starting");

                    //Posting user data to script
                    JSONObject json = jsonParser.makeHttpRequest("http://52.18.47.165/addTag.php", "POST", params);

                    Log.d("Attempt to send started", json.toString());

                    // json success element
                    success = json.getInt("success");

                    if (success == 1) {
                        Log.d("User info updated", json.toString());
                        finish();
                        return json.getString("message");
                    } else {
                        Log.d("Failed to update user", json.getString("message"));
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
            Toast.makeText(ReadTag.this, file_url, Toast.LENGTH_LONG).show();


        }

    }
}