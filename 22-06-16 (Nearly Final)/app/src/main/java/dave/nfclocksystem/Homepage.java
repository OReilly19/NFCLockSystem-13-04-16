package dave.nfclocksystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class Homepage extends ActionBarActivity implements OnClickListener {


    private Button mAddKey, mUnlockDoor, mMyLocks, mMessages, mLogs;
    public static String appuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        appuser = prefs.getString("username", "");
        if(appuser.trim().length()==0){
            Intent i = new Intent(this, LoginPage.class);
            startActivity(i);
        }
        else{
            setContentView(R.layout.homepage);

            mAddKey = (Button) findViewById(R.id.addKey);
            mUnlockDoor = (Button) findViewById(R.id.unlockDoor);
            //mMyLocks = (Button) findViewById(R.id.mylocks);
            mMessages = (Button) findViewById(R.id.messages);
            mLogs = (Button) findViewById(R.id.logs);

            //Register Listeners
            mAddKey.setOnClickListener(this);
            mUnlockDoor.setOnClickListener(this);
            mMessages.setOnClickListener(this);
            mLogs.setOnClickListener(this);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_homepage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            startActivity(new Intent(this, Homepage.class));
            return true;
        }
        if (id==R.id.myLocksMenu){
            startActivity(new Intent(this, MyLocks.class));
        }

        if(id == R.id.messagesMenu){
            startActivity(new Intent(this, Messages.class));
        }

        if(id == R.id.logsMenu){
            startActivity(new Intent(this, Logs.class));
        }
        if(id == R.id.Logout){
            SharedPreferences preferences =getSharedPreferences("PREFS",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.unlockDoor:
                    Intent i = new Intent(this, UnlockDoor.class);
                    startActivity(i);
                    break;

                case R.id.messages:
                    i = new Intent(this, Messages.class);
                    startActivity(i);
                    break;

                 case R.id.logs:
                    i = new Intent(this, Logs.class);
                    startActivity(i);
                    break;

                case R.id.addKey:
                    i = new Intent(this, ReadTag.class);
                    startActivity(i);
                    break;

                default:
                    break;

            }
        }
}
