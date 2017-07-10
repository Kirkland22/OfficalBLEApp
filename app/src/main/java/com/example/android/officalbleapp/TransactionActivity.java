package com.example.android.officalbleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class TransactionActivity extends Activity {
    private Customer customer;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        queue = Volley.newRequestQueue(this);


        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            customer = (Customer) b.getSerializable("Customer");
        }


    }


    public void onWithdrawalClicked(View view) {
        EditText transactionField = (EditText) TransactionActivity.this.findViewById(R.id.withdrawal_amount);

        String transactionAmount =  transactionField.getText().toString();

        try {
            if (transactionAmount.isEmpty())
            {
                Context context = getApplicationContext();
                CharSequence text = "Please enter an amount to withdraw";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            else if (hasEnoughMoney(transactionAmount)) {

                int balance = Integer.parseInt(customer.getAccountBalance());
                int transAmount = Integer.parseInt(transactionAmount);

                postData(customer.getCustomerName(), transactionAmount);
                completeTransaction(balance,transAmount);
                showToast("Transaction Complete");
                startNewActivity(choiceActivity.class);

            }

            else {
                showToast("Not enough Money");
            }
        } catch (NumberFormatException e) {
            showToast("Please enter a numerical amount");
        }




    }


    public boolean hasEnoughMoney(String amount) {

        int transAmount = Integer.parseInt(amount);
        int balance = Integer.parseInt(customer.getAccountBalance());

        if (transAmount > balance)
        {
            return false;
        }

        else
        {
            return true;
        }

    }

    /*
    @Override
    public void onBackPressed() {
        Context context = getApplicationContext();
        CharSequence text = "CANT GO BACK";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    */

    public void completeTransaction(int balance, int amount) {

        String newBalance = ((Integer)(balance - amount)).toString();
        customer.setAccountBalance(newBalance);

    }


    public void postData(final String name, final String amount) {


        StringRequest sr = new StringRequest(Request.Method.POST, "http://beaconapp-abdallahozaifa.c9users.io:8080/beaconInfo", new Response.Listener<String>() {

            @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error: ", error);

                }

            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("name", name);
                    params.put("amount", amount);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(sr);
          sr.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }



    public void showToast(String toastMessage)
    {
        Context context = getApplicationContext();
        CharSequence text = toastMessage;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void startNewActivity(Class newAct) {

        Intent i = new Intent();
        Bundle b = new Bundle();

        b.putSerializable("Customer", customer);
        i.putExtras(b);
        i.setClass(this, newAct);
        i.putExtra("Customer", customer);
        startActivity(i);
    }


}
