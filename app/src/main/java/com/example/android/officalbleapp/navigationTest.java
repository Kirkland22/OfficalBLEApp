package com.example.android.officalbleapp;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import android.content.res.Configuration;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.RangedBeacon;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class navigationTest extends AppCompatActivity implements BeaconConsumer {
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private Customer customer;
    private TextView tCustomerName;
    private TextView tCustomerBalance;
    RequestQueue queue;
    private boolean hasNeverBeenInQueue = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_navigation_test);
        getSerializedObject();
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        tCustomerName = (TextView) findViewById(R.id.customer_name);
        tCustomerBalance = (TextView) findViewById(R.id.customer_balance);
        tCustomerName.setText(customer.getCustomerName());
        tCustomerBalance.setText(customer.getAccountBalance());

        /*Changes the sampling rate of Beacon:
        Faster Sampling = less accurate measurement
        Slower Sampling = more accurate measurement
         */
        RangedBeacon.setSampleExpirationMilliseconds(500);

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void addDrawerItems() {
        String[] osArray = { "Unlock Vestibule", "ATM Transaction", "Request Service", "Log out"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0 ) {
                    startIntent();
                }

                    else if(position == 1) {
                    Intent i1 = new Intent();
                    Bundle b1 = new Bundle();

                    b1.putSerializable("Customer", customer);
                    i1.putExtras(b1);
                    i1.setClass(parent.getContext(), TransactionActivity.class);

                    i1.putExtra("Customer", customer);
                    startActivity(i1);
                }


                else if (position == 2) {

                    startRanging();
                }


                else if (position == 3) {
                    Intent i3 = new Intent();
                    i3.setClass(parent.getContext(), LoginActivity.class);
                    startActivity(i3);
                    showToast("You are now logged out");
                    finish();
                }




            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_test, menu);
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
            //Toast.makeText(navigationTest.this, "Settings", Toast.LENGTH_SHORT).show();
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }


    @Override
    public void onBeaconServiceConnect() {

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    //EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                    Beacon firstBeacon = beacons.iterator().next();
                    Log.i("Beacon ID3", "Beacon Minor: " + firstBeacon.getId3());
                    if(firstBeacon.getId3().toInt() == 3 && hasNeverBeenInQueue){
                        Integer account = customer.getAccountNumber();
                        postData(customer.getCustomerName(),account.toString());
                        hasNeverBeenInQueue = false;
                    }

                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("44444444-4444-4444-4444-44444444BEAC", null, null, null));
        } catch (RemoteException e) {   }
    }


    public void showToast(String toastMessage) {
        Context context = getApplicationContext();
        CharSequence text = toastMessage;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //Sends the customer information to server for queue. Server: /queue
    private void postData(final String name, final String accNum) {
        StringRequest sr = new StringRequest(Request.Method.POST,"http://beaconapp-abdallahozaifa.c9users.io:8080/queue", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e("IDK",response);
                displayQueueNumber(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("account", accNum);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
        sr.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void displayQueueNumber(String data) {

        try {
            JSONObject reader = new JSONObject(data);
            String queueNumber = reader.getString("queueNum");
            String waitTime = reader.getString("waitTime");
            String message = "Queue number:" + queueNumber + " \nEstimated wait time: " + waitTime + " minutes";
            sendNotification(message);



        } catch (Exception e) {

        }
    }

    private void sendNotification(String notificationText) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("ChaseWay IoT")
                        .setSmallIcon(R.drawable.ic_launcher).setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationText));


        /*TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, choiceActivity.class));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);*/
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());



    }


    private void startIntent() {
        Intent i = new Intent();
        Bundle b = new Bundle();

        b.putSerializable("Customer", customer);
        i.putExtras(b);
        i.setClass(this, RangingActivity.class);
        i.putExtra("Customer", customer);
        startActivity(i);

    }
    private void getSerializedObject() {
        Bundle b = this.getIntent().getExtras();
        if (b != null)
            customer = (Customer)b.getSerializable("Customer");

    }

    private void startRanging() {
        beaconManager.bind(this);
    }
}