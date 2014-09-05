package com.reductivetech.taskclarity.settings;

import android.accounts.Account;
import android.app.ListActivity;
import android.os.Bundle;

import android.widget.ArrayAdapter;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.reductivetech.taskclarity.R;

public class AccountsActivity extends ListActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleAccountManager googleAccountManager = new GoogleAccountManager(this);
        Account[] accounts = googleAccountManager.getAccounts();

        setListAdapter(new ArrayAdapter<Account>(this, R.layout.accounts, accounts));
    }
}