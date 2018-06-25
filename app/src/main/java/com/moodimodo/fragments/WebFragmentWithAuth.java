package com.moodimodo.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.fragments.QuantimodoWebFragment;
import com.quantimodo.tools.sdk.AuthHelper;
import com.quantimodo.tools.sdk.request.NoNetworkConnection;

import javax.inject.Inject;

public abstract class WebFragmentWithAuth extends QuantimodoWebFragment {

    @Inject
    AuthHelper mAuthHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QTools.getInstance().inject(this);
    }

    protected String getToken(){
        if (mAuthHelper.isLoggedIn()){
            try {
                return mAuthHelper.getAuthTokenWithRefresh();
            } catch (NoNetworkConnection noNetworkConnection) {

            }
        }
        return null;
    }


    @Override
    protected void onWebViewCreated(final WebView wv) {
        new AsyncTask<Void,Void,String>() {

            @Override
            protected String doInBackground(Void... params) {
                return getToken();
            }

            @Override
            protected void onPostExecute(String s) {
                if (s != null){
                    wv.loadUrl(String.format(getArguments().getString(ARG_URL),s));
                } else {
                    Toast.makeText(getActivity(), "No auth", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
