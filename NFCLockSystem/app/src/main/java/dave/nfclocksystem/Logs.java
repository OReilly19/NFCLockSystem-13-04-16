package dave.nfclocksystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static dave.nfclocksystem.LoginPage.appuser;


public class Logs extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logs);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LogsFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logs, menu);
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

    public static class LogsFragment extends Fragment {

        ProgressDialog pDialog;
        JSONParser jsonParser = new JSONParser();

        int success;
        public static List<String> listMyLogs;
        public static List<String> emp_usernames;
/*        public static List<String> emp_username;
        public static List<String> lock_id;
        public static List<String> lock_name;
        public static List<String> status;
        public static List<String> time;
*/        public static String sender;

        ArrayAdapter<String> logsReceived;

        public LogsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.logs_fragment, container, false);
            Context context = getActivity();
            listMyLogs=new ArrayList<>();
            emp_usernames=new ArrayList<>();

            new getLogs(context,rootView).execute();

            return rootView;
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            if (v.getId()==R.id.listview_MessageInbox) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                menu.setHeaderTitle(emp_usernames.get(info.position));
                sender = emp_usernames.get(info.position);

 /*               String[] menuItems = getResources().getStringArray(R.array.messageReply);
                for (int i = 0; i<menuItems.length; i++) {
                    menu.add(Menu.NONE, i, i, menuItems[i]);
                }*/
            }
        }

  /*      @Override
        public boolean onContextItemSelected(MenuItem item) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            int menuItemIndex = item.getItemId();
            String[] menuItems = getResources().getStringArray(R.array.messageReply);
            if (menuItemIndex==1){

                Intent intent = new Intent(getActivity(),CreateMessage.class);
                startActivity(intent);
            }

            return true;
        }
*/

        class getLogs extends AsyncTask<String,String,String> {

            private Context mContext;
            private View rootView;

            public getLogs(Context context, View rootView){
                this.mContext=context;
                this.rootView=rootView;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Getting logs...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }


            @Override
            protected String doInBackground(String... args){

                try{

                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("username",appuser));

                    JSONObject jsonObject=jsonParser
                            .makeHttpRequest("http://192.168.1.11/getLogs.php", "POST", params);

                    Log.d("Fill list attempt", jsonObject.toString());

                    success = jsonObject.getInt("success");

                    if(success == 1 ) {
                        JSONArray array = jsonObject.getJSONArray("posts");
                        Log.d("Array Contents", array.toString());
                        int len = array.length();
                        String len2 = Integer.toString(len);
                        Log.d("Array Length", len2);
                        for (int i = 0; i < array.length(); i++) {

                            JSONObject a = array.getJSONObject(i);
                            Log.d("Test of A", a.toString());
                            String emp_user = a.getString("username");
                            emp_usernames.add(emp_user);
                            String emp_id = a.getString("employee_id");
                            String lock_id= a.getString("lock_id");
                            String lock_name = a.getString("lock_name");
                            String status = a.getString("status");
                            String time = a.getString("time");
                            emp_user = "Employee Username: " + emp_user;
                            emp_id= "Employee ID: " + emp_id;
                            lock_id = "Lock ID: " + lock_id;
                            lock_name = "Lock Name: " + lock_name;
                            status = "Status: " + status;
                            time = "Time: " + time;

                            String str4 =emp_user + "\n\n" + emp_id + "\n\n" + lock_id + "\n\n" + lock_name + "\n\n" + status + "\n\n" + time;
                            Log.d("Str2", emp_user);


                            listMyLogs.add(str4);
                        }

                        return jsonObject.getString("message");
                    }
                    else{
                        Log.d("Failed to update list", jsonObject.getString("message"));
                        return jsonObject.getString("message");
                    }

                }
                catch (JSONException e){
                    e.printStackTrace();
                }


                return null;
            }


            protected void onPostExecute(String file_url){
                pDialog.dismiss();

                if (success == 1) {
                    Collections.reverse(listMyLogs);
                    Collections.reverse(emp_usernames);
                    logsReceived = new ArrayAdapter<String>(
                            getActivity(),
                            R.layout.sample_logs,
                            R.id.list_logs,
                            listMyLogs);

                    ListView listView = (ListView)rootView.findViewById(R.id.listview_logs);
                    listView.setAdapter(logsReceived);
                    registerForContextMenu(listView);
                }
            }

        }
    }
}
