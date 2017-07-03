package com.example.android.officalbleapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Kirkland on 7/3/17.
 */

public class Customer implements Serializable{

    public static Parcelable Creator;
    private String customerName;
    private int accountNumber;
    private int accountBalance;
    private int transactionAmount;
    private boolean isHighRoller;


    public Customer(String customerName,int accountNumber,int accountBalance,int transactionAmount, boolean isHighRoller) {
        this.customerName = customerName;
        this.accountNumber = accountNumber;
        this.accountBalance = accountBalance;
        this.transactionAmount = transactionAmount;
        this.isHighRoller = isHighRoller;
    }


    public int getAccountNumber() {
        return accountNumber;
    }

    public int getTransactionAmount() {
        return transactionAmount;
    }

    public String getCustomerName() {
        return customerName;
    }

    @Override
    public String toString() {
        return "Name: " + customerName + "\n" + "Account Number: " + accountNumber + "\n";
    }

}
