package com.example.android.officalbleapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class choiceActivity extends Activity {
private Customer customer;
private TextView tCustomerName;
private TextView tCustomerBalance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            customer = (Customer) b.getSerializable("Customer");
        }
        setContentView(R.layout.activity_choice);

        tCustomerName = (TextView) findViewById(R.id.customer_name);
        tCustomerBalance = (TextView) findViewById(R.id.customer_balance);

        tCustomerName.setText(customer.getCustomerName());
        tCustomerBalance.setText(customer.getAccountBalance());
    }



    public void onVestibuleClicked(View view) {
        Intent i = new Intent();
        Bundle b = new Bundle();

        b.putSerializable("Customer",customer);
        i.putExtras(b);
        i.setClass(this,RangingActivity.class);

        i.putExtra("Customer",customer);
        startActivity(i);

    }

    public void onTransactionClicked(View view) {
        Intent i = new Intent();
        Bundle b = new Bundle();

        b.putSerializable("Customer",customer);
        i.putExtras(b);
        i.setClass(this,TransactionActivity.class);

        i.putExtra("Customer",customer);
        startActivity(i);
        finish();

    }

    public void onBranchClicked (View view) {
        Intent intent = new Intent(choiceActivity.this, LocalBranchActivity.class);
        startActivity(intent);
    }

    public void onLogoutClicked(View view) {
        Intent i = new Intent();
        i.setClass(this,LoginActivity.class);
        startActivity(i);
        showToast("You are now logged out");
        finish();


    }



    public void showToast(String toastMessage)
    {
        Context context = getApplicationContext();
        CharSequence text = toastMessage;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
