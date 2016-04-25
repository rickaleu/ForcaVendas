package im.maya.forcavendaseudora;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import im.maya.forcavendaseudora.response.Communication;
import im.maya.forcavendaseudora.webservice.WebserviceEudora;


public class FeedDetail extends ActionBarActivity {

    private Context context;
    private boolean validToken;
    private WebView feedMain;
    private ProgressBar progress;
    private String token;

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //===== Analytics Tracker ===================
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-64947111-1");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        //===========================================

        context = this;

        loadComponents();
        loadActions();

    }

    @Override
    public void onResume() {
        super.onResume();

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        Tracker tracker = analytics.newTracker("UA-64947111-1");

        // All subsequent hits will be send with screen name = "main screen"
        tracker.setScreenName("Detalhe do Comunicado");

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("FeedDetail")
                .setLabel("Enviar")
                .build());

        validateToken();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intentHome = new Intent(context, Home.class);
                startActivity(intentHome);
                finish();
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }


    public void loadComponents(){

        progress = (ProgressBar) findViewById(R.id.feed_progress);
        feedMain = (WebView) findViewById(R.id.feed_container_webview);
    }


    public void loadActions(){

        Intent intent = getIntent();
        String s = intent.getStringExtra("link");

        feedMain.getSettings().setJavaScriptEnabled(true);

        feedMain.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progress.setVisibility(View.INVISIBLE);
                feedMain.setVisibility(View.VISIBLE);
            }
        });

        feedMain.loadUrl(s);

        String idBulletin = intent.getStringExtra("id");
        Log.i("BULLETIN SERVIÇO", String.valueOf(idBulletin));

        //passando o valor da variável via parâmetro
        readed(idBulletin);

    }


    public void readed(String idBulletin){

        //Sharepreferences
        SharedPreferences prefsReaded = context.getSharedPreferences("im.maya.forcavendaseudora", Context.MODE_PRIVATE);
        token = prefsReaded.getString("im.maya.forcavendaseudora.user.Token", "");

        //Create Request params
        RequestParams params = new RequestParams();
        params.put("Token", token);
        params.put("BulletinId", Integer.parseInt(idBulletin));

        //Post logout
        WebserviceEudora readed = new WebserviceEudora();

        readed.postReaded(params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    //if retorno diferente de nulo
                    if (response != null) {

                        Boolean readed = response.getBoolean("Object");

//                        //Sharepreferences
//                        SharedPreferences prefsReaded = context.getSharedPreferences("im.maya.forcavendaseudora", Context.MODE_PRIVATE);
//                        prefsReaded.edit().putBoolean("im.maya.forcavendaseudora.user.readed", readed).apply();

                        Communication communicationObject = new Communication();
                        communicationObject.setReaded(readed);

                    }

                } catch (Exception ex) {
                    Log.e("Parametros", ex.getMessage());

                } finally {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(context, "Houve falha na comunicação. Tente novamente", Toast.LENGTH_LONG).show();

            }
        });
    }


    public void validateToken(){

        //Create Request params
        RequestParams params = new RequestParams();
        params.put("Token", token);

        Log.i("Param Set - Token", String.valueOf(token));

        //Post logout
        WebserviceEudora config = new WebserviceEudora();

        config.postValidateToken(params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    //Recebemos o retorno de sucesso
                    //Toast.makeText(context, "Retorno" + response, Toast.LENGTH_LONG).show();

                    //if retorno diferente de nulo
                    if (response != null) {

                        Integer code = response.getInt("Code");
                        Log.i("Response Set - Code", String.valueOf(code));

                        validToken = response.getBoolean("Object");
                        Log.i("Response Set - Object", String.valueOf(validToken));

                        if (!validToken) {

                            //Mandar para a tela de Login.
                            Intent intent = new Intent(context, Login.class);
                            startActivity(intent);

                            context.getSharedPreferences("im.maya.forcavendaseudora.user", 0).edit().clear().commit();
                        }
                    }

                } catch (Exception ex) {
                    Log.e("Parametros", ex.getMessage());

                } finally {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }
        });

    }

}
