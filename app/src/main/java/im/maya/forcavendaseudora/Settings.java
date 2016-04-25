package im.maya.forcavendaseudora;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import im.maya.forcavendaseudora.webservice.WebserviceEudora;


public class Settings extends ActionBarActivity {

    private Context context;
    private CheckBox pushEnable;
    private TextView exit;
    private ProgressBar settingsProgressBar;

    public String token;
    public boolean ppush;
    public boolean status;
    public boolean push;
    private boolean validToken;

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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

        //Sharepreferences
        SharedPreferences prefsSettings = context.getSharedPreferences("im.maya.forcavendaseudora", Context.MODE_PRIVATE);
        token = prefsSettings.getString("im.maya.forcavendaseudora.user.Token", "");


        loadComponents();
        requestGetConfig();
        loadActions();

    }

    @Override
    public void onResume() {
        super.onResume();

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        Tracker tracker = analytics.newTracker("UA-64947111-1");

        // All subsequent hits will be send with screen name = "main screen"
        tracker.setScreenName("Configurações");

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Settings")
                .setLabel("Enviar")
                .build());

        validateToken();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void loadComponents(){

        pushEnable = (CheckBox) findViewById(R.id.settings_check_push);
        exit = (TextView) findViewById(R.id.settings_title_exit);
        settingsProgressBar = (ProgressBar) findViewById(R.id.settings_progress);

    }

    public void loadActions(){

        pushEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                validatePush();

            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logout();
            }
        });

    }

    public void logout(){

        settingsProgressBar.setVisibility(View.VISIBLE);

        //Create Request params
        RequestParams params = new RequestParams();
        params.put("Token", token);

        //Post logout
        WebserviceEudora logout = new WebserviceEudora();

        logout.postLogout(params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    //Recebemos o retorno de sucesso
                    //Toast.makeText(context, "Retorno" + response, Toast.LENGTH_LONG).show();

                    //if retorno diferente de nulo
                    if (response != null) {

                        Integer code = response.getInt("Code");

                        status = response.getBoolean("Object");

                        if(status){

                            SharedPreferences settings = context.getSharedPreferences("im.maya.forcavendaseudora", Context.MODE_PRIVATE);
                            settings.edit().clear().commit();


                            //Mandar para a tela de Login.
                            Intent intent  = new Intent(getBaseContext(), Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }
                        else{

                            //Mensagem de Erro no Login
                            new AlertDialog.Builder(context)
                                    .setMessage("Desculpe, houve um erro de servidor.")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                        }
                                    })
                                    .show();
                        }
                    }

                } catch (Exception ex) {
                    Log.e("Parametros", ex.getMessage());

                } finally {
                    settingsProgressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(context, "Houve falha na comunicação. Tente novamente", Toast.LENGTH_LONG).show();

                settingsProgressBar.setVisibility(View.INVISIBLE);

            }
        });


    }

    public boolean validatePush(){

        if (pushEnable.isChecked()){

            push = true;
            requestSetConfig();
        }
        else {

            push = false;
            requestSetConfig();

        }

        return true;
    }


    //==| Serviço que recupera o status do checkbox push |==
    public void requestGetConfig(){

        //Create Request params
        RequestParams params = new RequestParams();
        params.put("Token", token);

        Log.i("Param Get - Token", String.valueOf(token));

        //Post logout
        WebserviceEudora config = new WebserviceEudora();

        config.postGetConfig(params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    //Recebemos o retorno de sucesso
                    //Toast.makeText(context, "Retorno" + response, Toast.LENGTH_LONG).show();

                    //if retorno diferente de nulo
                    if (response != null) {

                        Integer code = response.getInt("Code");

                        push = response.getBoolean("Object");

                        Log.i("Response Get - Push", String.valueOf(push));


                        //Sharepreferences
                        SharedPreferences prefsSettings = context.getSharedPreferences("im.maya.forcavendaseudora", Context.MODE_PRIVATE);
                        prefsSettings.edit().putBoolean("im.maya.forcavendaseudora.user.Push", push).apply();
                    }

                } catch (Exception ex) {
                    Log.e("Parametros", ex.getMessage());

                } finally {
                }

                SharedPreferences prefsSettings = context.getSharedPreferences("im.maya.forcavendaseudora", Context.MODE_PRIVATE);
                ppush = prefsSettings.getBoolean("im.maya.forcavendaseudora.user.Push", push);

                pushEnable.setChecked(ppush);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(context, "Houve falha na comunicação. Tente novamente", Toast.LENGTH_LONG).show();

            }
        });
    }
    //======================================================

    //==| Serviço que manda o status do checkbox do push |==
    public void requestSetConfig(){

        //Create Request params
        RequestParams params = new RequestParams();
        params.put("Token", token);
        params.put("Push", push);

        Log.i("Param Set - Token", String.valueOf(token));
        Log.i("Param Set - Push", String.valueOf(push));

        //Post logout
        WebserviceEudora config = new WebserviceEudora();

        config.postSetConfig(params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    //Recebemos o retorno de sucesso
                    //Toast.makeText(context, "Retorno" + response, Toast.LENGTH_LONG).show();

                    //if retorno diferente de nulo
                    if (response != null) {

                        Integer code = response.getInt("Code");

                        push = response.getBoolean("Object");

                        Log.i("Response Set - Push", String.valueOf(push));

                        //Sharepreferences
                        SharedPreferences prefsSettings = context.getSharedPreferences("im.maya.forcavendaseudora", Context.MODE_PRIVATE);
                        prefsSettings.edit().putBoolean("im.maya.forcavendaseudora.user.Push", push).apply();

                    }

                } catch (Exception ex) {
                    Log.e("Parametros", ex.getMessage());

                } finally {
                }

                SharedPreferences prefsSettings = context.getSharedPreferences("im.maya.forcavendaseudora", Context.MODE_PRIVATE);
                ppush = prefsSettings.getBoolean("im.maya.forcavendaseudora.user.Push", push);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(context, "Houve falha na comunicação. Tente novamente", Toast.LENGTH_LONG).show();

            }
        });
    }
    //======================================================

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
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

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
