package com.quantimodo.android.sdk;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Helper class
 */
public class Quantimodo {

    /**
     * Checks if QM app is installed
     * @param context Context
     * @return true if QM app is installed
     */
    public static boolean isInstalled(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo("com.quantimodo.android", PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Create intent , with link to QM app
     * @return intent
     */
    public static Intent getInstallIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.quantimodo.android"));
        return intent;
    }

    /**
     * Create intent, to launch login process with QM app
     * @param context Context
     * @return intent
     */
    public static Intent getLoginIntent(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.quantimodo.android");
        intent.setFlags(intent.getFlags() & (~Intent.FLAG_ACTIVITY_NEW_TASK)); // Open sign in activity in same task
        intent.setAction("com.quantimodo.android.ACTION_SIGN_IN");
        intent.putExtra("callingApp", context.getPackageName());
        return intent;
    }

    /**
     * Get accounts , this method requires the caller to hold the permission
     * {@link android.Manifest.permission#GET_ACCOUNTS}.
     * @param context Context
     * @return accounts
     */
    public static Account[] getAccounts(Context context) {
        return AccountManager.get(context).getAccountsByType("com.quantimodo");
    }

    /**
     * Get default account, this method requires the caller to hold the permission
     * {@link android.Manifest.permission#GET_ACCOUNTS}.
     * @param context Context
     * @return Account
     */
    public static Account getAccount(Context context) {
        Account[] accounts = getAccounts(context);
        if (accounts.length > 0) {
            return accounts[0];
        } else {
            return null;
        }
    }

    /**
     * Get default account, this method requires the caller to hold the permission
     * {@link android.Manifest.permission#GET_ACCOUNTS}.
     * @param context Context
     * @param name Name of account
     * @return Account
     */
    public static Account getAccount(Context context, String name) {
        Account[] accounts = getAccounts(context);
        for (Account account : accounts) {
            if (account.name.equals(name)) {
                return account;
            }
        }
        return null;
    }
}
