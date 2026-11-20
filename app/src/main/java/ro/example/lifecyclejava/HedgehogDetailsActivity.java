package ro.example.lifecyclejava;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class HedgehogDetailsActivity extends LifecycleLoggingActivity {
    public static final String EXTRA_DETAILS_URL = "ro.example.lifecyclejava.EXTRA_DETAILS_URL";
    public static final String EXTRA_PAGE_TITLE = "ro.example.lifecyclejava.EXTRA_PAGE_TITLE";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hedgehog_details);

        TextView title = findViewById(R.id.text_hedgehog_web_title);
        String pageTitle = getIntent().getStringExtra(EXTRA_PAGE_TITLE);
        if (pageTitle != null) {
            title.setText(pageTitle);
        }

        String pageUrl = getIntent().getStringExtra(EXTRA_DETAILS_URL);
        Uri uri = pageUrl == null ? null : Uri.parse(pageUrl);
        if (uri == null || !"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) {
            Toast.makeText(this, R.string.web_page_unavailable, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        webView = findViewById(R.id.webview_hedgehog_details);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(pageUrl);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
        }
        super.onDestroy();
    }
}
