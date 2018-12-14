package layout;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.asus_user.labs.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebPreviewFragment extends Fragment {

    View webPreviewFragment;
    public WebPreviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        webPreviewFragment = inflater.inflate(R.layout.fragment_web_preview, container, false);
        String myValue = this.getArguments().getString("message");
        WebView webView = webPreviewFragment.findViewById(R.id.rssWebView);
        // включаем поддержку JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });
        // указываем страницу загрузки
        webView.loadUrl(myValue);
        return webPreviewFragment;
    }
}
