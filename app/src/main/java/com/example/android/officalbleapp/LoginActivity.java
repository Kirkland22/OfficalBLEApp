package com.example.android.officalbleapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconManager;


public class LoginActivity extends Activity {
    protected static final String TAG = "LoginActivity";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String SEAN_USERNAME = "sean";
    private static final String SEAN_PASSWORD = "p";
    private static final String BRENDON_USERNAME = "brendon";
    private static final String BRENDON_PASSWORD = "p";
    private static final String HOZAIFA_USERNAME = "hozaifa";
    private static final String HOZAIFA_PASSWORD = "p";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Log.e("Loading","Looging in");
        verifyBluetooth();
        //logToDisplay("Welcome to Chase");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }
        }
    }

    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public void onSignInClicked(View view) {
        EditText eUsername = (EditText) LoginActivity.this.findViewById(R.id.login_username);
        EditText ePassword = (EditText) LoginActivity.this.findViewById(R.id.login_password);

        Login(eUsername,ePassword);
        eUsername.setText("");
        ePassword.setText("");
    }



    public void Login(EditText eUsername,  EditText ePassword) {

        if(eUsername.getText().toString().equals(SEAN_USERNAME) && ePassword.getText().toString().equals(SEAN_PASSWORD)) {
            Customer Sean  = new Customer("Sean Kirkland",0001,"345",20,false);
            Intent i = new Intent();
            Bundle b = new Bundle();

            b.putSerializable("Customer",Sean);
            i.putExtras(b);
            i.setClass(this,navigationTest.class);

            //Intent myIntent = new Intent(this, choiceActivity.class);
            //startActivity(myIntent);
            i.putExtra("Customer",Sean);
            startActivity(i);
            finish();
        }

        else if(eUsername.getText().toString().equals(BRENDON_USERNAME) && ePassword.getText().toString().equals(BRENDON_PASSWORD)) {
            Customer brendon  = new Customer("Brendon James",0002,"1345",-20,false);
            Intent i = new Intent();
            Bundle b = new Bundle();

            b.putSerializable("Customer",brendon);
            i.putExtras(b);
            i.setClass(this,navigationTest.class);
            //Intent myIntent = new Intent(this, RangingActivity.class);
            //myIntent.putExtra("Customer",Sean);
            startActivity(i);
            finish();

        }

        else if(eUsername.getText().toString().equals(HOZAIFA_USERNAME) && ePassword.getText().toString().equals(HOZAIFA_PASSWORD)) {
            Customer hozaifa  = new Customer("Hozaifa Adballa",0003,"100345",20,true);
            Intent i = new Intent();
            Bundle b = new Bundle();

            b.putSerializable("Customer",hozaifa);
            i.putExtras(b);
            i.setClass(this,navigationTest.class);
            startActivity(i);
            finish();
        }

        else
        {
            showToast("Username or Password incorrect");
    }
    }


    public void showToast(String toastMessage)
    {
        Context context = getApplicationContext();
        CharSequence text = toastMessage;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /*
    @Override
    public void onResume() {
        super.onResume();
        ((BeaconReferenceApplication) this.getApplicationContext()).setLoginActivity(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BeaconReferenceApplication) this.getApplicationContext()).setLoginActivity(null);
    }
    */


    /*
    public void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                EditText editText = (EditText)LoginActivity.this
                        .findViewById(R.id.monitoringText);
                editText.append(line+"\n");
            }
        });
    }
    */

}
