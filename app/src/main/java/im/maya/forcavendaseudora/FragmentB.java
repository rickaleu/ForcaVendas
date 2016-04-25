package im.maya.forcavendaseudora;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import im.maya.forcavendaseudora.webservice.WebserviceEudora;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentB extends android.support.v4.app.Fragment {


    public String tokenResale;
    private WebView resaleMain;
    public boolean validToken;

    public ProgressBar resaleProgressBar;

    public static GoogleAnalytics analytics;
    public static Tracker tracker;


    public FragmentB() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_resale, container, false);

        //===== Analytics Tracker ===================
        analytics = GoogleAnalytics.getInstance(getActivity());
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-64947111-1");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        //===========================================

        //Sharepreferences
        SharedPreferences prefsResale = getActivity().getSharedPreferences("im.maya.forcavendaseudora", Context.MODE_PRIVATE);
        tokenResale = prefsResale.getString("im.maya.forcavendaseudora.user.Token", "");

        CookieManager.getInstance().setAcceptCookie(true);
//        CookieManager.getInstance().hasCookies();


        resaleMain = (WebView) rootView.findViewById(R.id.resale_container_webview);
        resaleProgressBar = (ProgressBar) rootView.findViewById(R.id.resale_progress);


        String url = "http://fv.eudoracloud.com/ferramentas/intencoesRevenda?Token=" + tokenResale;

        Log.i("STRING_URL_TOKEN", String.valueOf(url));


        resaleMain.getSettings().setJavaScriptEnabled(true);

        resaleMain.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                resaleProgressBar.setVisibility(View.VISIBLE);
                resaleMain.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                resaleProgressBar.setVisibility(View.INVISIBLE);
                resaleMain.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }

                // Otherwise allow the OS to handle things like tel, mailto, etc.
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });

        resaleMain.loadUrl(url);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        CookieSyncManager.getInstance().stopSync();

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(getActivity());
        Tracker tracker = analytics.newTracker("UA-64947111-1");

        // All subsequent hits will be send with screen name = "main screen"
        tracker.setScreenName("Intensão de revendas");

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("FragmentResale")
                .setLabel("Enviar")
                .build());

        validateToken();
    }

    @Override
    public void onPause() {
        super.onPause();

        CookieSyncManager.getInstance().sync();
    }

    public void validateToken(){

        //Create Request params
        RequestParams params = new RequestParams();
        params.put("Token", tokenResale);

        Log.i("Param Set - Token FB", String.valueOf(tokenResale));

        //Post ValidToken
        WebserviceEudora config = new WebserviceEudora();

        config.postValidateToken(params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    //Recebemos o retorno de sucesso
                    //Toast.makeText(context, "Retorno" + response, Toast.LENGTH_LONG).show();

                    //if retorno diferente de nulo
                    if (response != null) {

                        validToken = response.getBoolean("Object");
                        Log.i("Response Set - Obj FB", String.valueOf(validToken));

                        if (!validToken) {

                            //Mandar para a tela de Login.
                            Intent intent = new Intent(getActivity(), Login.class);
                            startActivity(intent);

                            getActivity().getSharedPreferences("im.maya.forcavendaseudora.user", 0).edit().clear().commit();
                        }
                    }

                } catch (Exception ex) {
                    Log.e("Parametros FB", ex.getMessage());

                } finally {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

//                Toast.makeText(getActivity(), "Houve falha na comunicação. Por favor, encerre o aplicativo e tente novamente", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
