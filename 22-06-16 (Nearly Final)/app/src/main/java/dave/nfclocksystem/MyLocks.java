package dave.nfclocksystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MyLocks extends ActionBarActivity implements OnClickListener{
    private Button mAddKey, mDeleteUser, mUnlockDoor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_locks);


        mAddKey = (Button) findViewById(R.id.addKey);
        mDeleteUser = (Button) findViewById(R.id.deleteUser);
        mUnlockDoor = (Button) findViewById(R.id.unlockDoor);

        //Register Listeners
        mAddKey.setOnClickListener(this);
        mDeleteUser.setOnClickListener(this);
        mUnlockDoor.setOnClickListener(this);

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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.addKey:
                Intent i = new Intent(this, ReadTag.class);
                startActivity(i);
                break;

           /* case R.id.deleteUser:
                i = new Intent(this, Messages.class);
                startActivity(i);
                break;*/

            case R.id.unlockDoor:
                i = new Intent(this, UnlockDoor.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}