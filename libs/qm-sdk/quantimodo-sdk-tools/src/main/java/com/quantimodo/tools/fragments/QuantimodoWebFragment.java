package com.quantimodo.tools.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.R;
import com.quantimodo.tools.ToolsPrefs;

import javax.inject.Inject;

public class QuantimodoWebFragment extends Fragment {
    /**
     * Common URLs.
     */
    public static final String URL_ANALYZE = "analyze";
    public static final String URL_CORRELATE = "correlate";
    public static final String URL_IMPORT_DATA = "connect";
    public static final String URL_TRACK_EXERCISE = "track-exercise";
    public static final String URL_TRACK_SLEEP = "track-sleep";
    public static final String URL_TRACK_VITALS = "track-vital-signs";
    public static final String URL_VARIABLES = "embeddable/?plugin=search-variables&accessToken=";
    public static final String URL_COMMON_PREDICTORS = "embeddable/?plugin=search-relationships&commonOrUser=common&accessToken=";
    public static final String URL_YOUR_PREDICTORS = "embeddable/?plugin=search-relationships&commonOrUser=user&accessToken=";

    /**
     * Argument field names.
     */
    public static final String ARG_URL = "url";

    /**
     * The URL to load in the WebView.
     */
    private String mUrl;

    @Inject
    ToolsPrefs mToolsPrefs;

    /**
     * Views.
     */
    protected ProgressBar pbWebViewLoading;
    protected WebView wvContent;

    /**
     * Create a new instance of WebFragment
     *
     * @param url The URL to display.
     * @return The created instance.
     */
    public static QuantimodoWebFragment newInstance(String url) {
        QuantimodoWebFragment fragment = new QuantimodoWebFragment();

        Bundle arguments = new Bundle();
        arguments.putString(ARG_URL, url);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QTools.getInstance().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        mUrl = mToolsPrefs.getApiUrl().concat(arguments.getString(ARG_URL));

        View rootView = inflater.inflate(R.layout.qmt_f_quantimodo_web, container, false);

        pbWebViewLoading = (ProgressBar) rootView.findViewById(R.id.pbWebViewLoading);
        wvContent = (WebView) rootView.findViewById(R.id.wvContent);

        pbWebViewLoading.setMax(100);
        pbWebViewLoading.setProgress(10);   // Give a fake sense of progress

        wvContent.setWebViewClient(new QuantimodoWebViewClient());
        wvContent.setWebChromeClient(new MyWebChromeClient());
        wvContent.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvContent.getSettings().setJavaScriptEnabled(true);
        wvContent.getSettings().setAllowFileAccess(true);
        wvContent.getSettings().setDomStorageEnabled(true);
        onWebViewCreated(wvContent);

        return rootView;
    }

    public ToolsPrefs getToolsPrefs() {
        return mToolsPrefs;
    }

    protected void onWebViewCreated(WebView wv){
        wv.loadUrl(mUrl);
    }

    /**
     * @return The URL this fragment loads in its WebView.
     */
    public String getUrl() {
        return mUrl;
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress > 99) {
                pbWebViewLoading.setVisibility(View.GONE);
            } else {
                if (pbWebViewLoading.getVisibility() != View.VISIBLE) {
                    pbWebViewLoading.setVisibility(View.VISIBLE);
                }

                pbWebViewLoading.setProgress(newProgress);
            }

            super.onProgressChanged(view, newProgress);
        }
    }

    public class QuantimodoWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.endsWith(".mp4")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "video/*");
                view.getContext().startActivity(intent);
                return true;
            } else {
                return super.shouldOverrideUrlLoading(view, url);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // Less than ideal because the content still shows briefly, but better than nothing.
            view.loadUrl(
                    "javascript:(function() { " +
                            "document.getElementById('banner').style.display = 'none'; " +
                            "document.getElementById('infobar').style.display = 'none'; " +
                            "document.getElementById('hellobar').style.display = 'none'; " +
                            "document.getElementById('wpadminbar').style.display = 'none'; " +
                            "document.getElementById('site-wide-cta').style.display = 'none'; " +
                            "document.getElementById('footer').style.display = 'none'; " +
                            "})()"
            );
        }
    }
}
