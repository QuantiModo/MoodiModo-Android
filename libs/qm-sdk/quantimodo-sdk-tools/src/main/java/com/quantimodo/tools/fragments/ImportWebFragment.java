package com.quantimodo.tools.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.sdk.AuthHelper;
import com.quantimodo.tools.sdk.request.NoNetworkConnection;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class ImportWebFragment extends QuantimodoWebFragment {

    public static final String API_CONNECT_URL = "api/v1/connect/mobile";

    @Inject
    AuthHelper mAuthHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QTools.getInstance().inject(this);
    }

    public static ImportWebFragment newInstance(){
        ImportWebFragment importWebFragment = new ImportWebFragment();

        Bundle arguments = new Bundle();
        arguments.putString(ARG_URL, API_CONNECT_URL);
        importWebFragment.setArguments(arguments);
        return importWebFragment;
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
                    Map<String,String> headers = new HashMap<>();
                    headers.put("Authorization","Bearer " + s);
                    wv.loadUrl(getToolsPrefs().getApiUrl() + API_CONNECT_URL,headers);
                } else {
                    Toast.makeText(getActivity(), "No auth", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
