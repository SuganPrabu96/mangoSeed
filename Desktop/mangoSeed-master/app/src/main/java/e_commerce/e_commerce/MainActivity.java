package e_commerce.e_commerce;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

import util.ConnectionDetector;

public class MainActivity extends ActionBarActivity{

    public static ConnectionDetector internetConnection;
    ConnectivityManager conman;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        internetConnection = new ConnectionDetector(getApplicationContext());

        if(true){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }, 2000);
        } else if (internetConnection.isConnectingToInternet() == false) {

            AlertDialog.Builder b1 = new AlertDialog.Builder(MainActivity.this);
            b1.setTitle("No connection");
            b1.setMessage("No internet connection available. Please try again.");
            final AlertDialog a = b1.create();
            a.show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    a.dismiss();
                }
            }, 2000);
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    }*/
}
