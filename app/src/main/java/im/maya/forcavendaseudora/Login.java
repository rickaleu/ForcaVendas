package im.maya.forcavendaseudora;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import im.maya.forcavendaseudora.domain.User;
import im.maya.forcavendaseudora.webservice.WebserviceEudora;

public class Login extends Activity {

    private Context context;

    public EditText editLogin;
    public EditText editPassword;
    private Button buttonAccess;
    private ProgressBar loginProgressBar;

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        //===== Analytics Tracker ===================
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-64947111-1");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        //===========================================

        user = new User();
        user.setDeviceType("1");
        user.setDeviceModel(Build.MODEL);
        user.setDeviceOs(Build.VERSION.RELEASE);
        user.setIpAdress(getLocalIpv4Address());

        Log.i("onCreate - DeviceType", user.getDeviceType());
        Log.i("onCreate - DeviceModel", user.getDeviceModel());
        Log.i("onCreate - DeviceOs", user.getDeviceOs());
        Log.i("onCreate - ipAdress", user.getIpAdress());

        //Pega o deviceToken caso ele já esteja no SharedPreferences
        SharedPreferences prefsLogin = context.getSharedPreferences("im.maya.forcavendaseudora", Context.MODE_PRIVATE);


        if (prefsLogin.contains("im.maya.forcavendaseudora.deviceToken")){

            user.setDeviceToken(prefsLogin.getString("im.maya.forcavendaseudora.deviceToken", ""));

        }else {

            user.setDeviceToken("");
        }

        Log.i("onCreate - DeviceToken:", user.getDeviceToken());

        loadComponents();
        loadActions();


    }

    @Override
    protected void onResume() {
        super.onResume();

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        Tracker tracker = analytics.newTracker("UA-64947111-1");

        // All subsequent hits will be send with screen name = "main screen"
        tracker.setScreenName("Login");

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Login")
                .setLabel("Enviar")
                .build());
    }



    public void loadComponents(){

        Typeface editFont = Typeface.createFromAsset(getAssets(), "fonts/opensanslight.ttf");
        editLogin = (EditText) findViewById(R.id.login_edit_login);
        editPassword = (EditText) findViewById(R.id.login_edit_password);
        buttonAccess = (Button) findViewById(R.id.login_button_enter);
        editLogin.setTypeface(editFont);
        editPassword.setTypeface(editFont);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/radikal.otf");
        buttonAccess.setTypeface(font);

        loginProgressBar = (ProgressBar) findViewById(R.id.login_progress);

    }

    public void loadActions(){

        buttonAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editLogin.getText().toString().equals("")){

                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    Toast.makeText(context, "Campo login vazio. Por favor, preencha com seu código ou e-mail.", Toast.LENGTH_SHORT).show();
                    editLogin.setText("");
                    editLogin.requestFocus();

                    return;
                }
                else if(editPassword.getText().toString().equals("")){

                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    Toast.makeText(context, "Campo senha vazio. Por favor, preencha com sua senha.", Toast.LENGTH_SHORT).show();
                    editPassword.setText("");
                    editPassword.requestFocus();

                    return;
                }
                else{
                    loginProgressBar.setVisibility(View.VISIBLE);
                    validateLogin();
                }
            }
        });

    }

    public void validateLogin(){

        if(!(editLogin.getText().toString().equals("") && editPassword.getText().toString().equals(""))) {

            //setando os valores nos métodos do user (parametros de retorno)
            user.setUsername(editLogin.getText().toString());
            user.setPassword(editPassword.getText().toString());


            //Create Request params
            RequestParams params = new RequestParams();
            params.put("Username", user.getUsername());
            params.put("Password", user.getPassword());
            params.put("DeviceToken", user.getDeviceToken());
            params.put("DeviceType", user.getDeviceType());
            params.put("DeviceModel", user.getDeviceModel().replace(" ", "%20"));
            params.put("DeviceOs", user.getDeviceOs());
            params.put("Ip", user.getIpAdress());
            //====================


            Log.i("param - Username", user.getUsername());
            Log.i("param - Password", user.getPassword());
            Log.i("param - DeviceToken", user.getDeviceToken());
            Log.i("param - DeviceType", user.getDeviceType());
            Log.i("param - DeviceModel", user.getDeviceModel());
            Log.i("param - DeviceOs", user.getDeviceOs());
            Log.i("param - IpAdress", user.getIpAdress());

            //Post login
            WebserviceEudora login = new WebserviceEudora();

            login.postLogin(params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        //Gravamos as demais informações retornadas, no model user

                        //if retorno diferente de nulo
                        if (response != null) {

                            Integer code = response.getInt("Code");

                            JSONObject obj = response.getJSONObject("Object");

                            user.setName(response.getJSONObject("Object").getString("Name"));
                            user.setEmail(response.getJSONObject("Object").getString("Email"));
                            user.setPhoto(response.getJSONObject("Object").getString("PhotoUrl"));
                            user.setToken(response.getJSONObject("Object").getString("Token"));
                            user.setIR(response.getJSONObject("Object").getBoolean("IR"));

                            Log.i("Response", String.valueOf(obj));


                            //Sharepreferences
                            SharedPreferences prefsLogin = context.getSharedPreferences("im.maya.forcavendaseudora", Context.MODE_PRIVATE);
                            //Parâmetros de envio
                            prefsLogin.edit().putString("im.maya.forcavendaseudora.user.username", user.getUsername()).apply();
                            prefsLogin.edit().putString("im.maya.forcavendaseudora.user.deviceType", user.getDeviceType()).apply();
                            prefsLogin.edit().putString("im.maya.forcavendaseudora.user.deviceModel", user.getDeviceModel()).apply();
                            prefsLogin.edit().putString("im.maya.forcavendaseudora.user.deviceOs", user.getDeviceOs()).apply();
                            prefsLogin.edit().putString("im.maya.forcavendaseudora.user.ip", user.getIpAdress()).apply();
                            //Parâmetros de retorno
                            prefsLogin.edit().putString("im.maya.forcavendaseudora.user.name", user.getName()).apply();
                            prefsLogin.edit().putString("im.maya.forcavendaseudora.user.email", user.getEmail()).apply();
                            prefsLogin.edit().putString("im.maya.forcavendaseudora.user.photoUrl", user.getPhoto()).apply();
                            prefsLogin.edit().putString("im.maya.forcavendaseudora.user.Token", user.getToken()).apply();
                            prefsLogin.edit().putBoolean("im.maya.forcavendaseudora.user.IR", user.getIR()).apply();


                            //mandar para a tela de comunicados.
                            Intent intent = new Intent(context, Home.class);
                            startActivity(intent);

                        }

                    } catch (Exception ex) {
                        Log.e("Parametros", ex.getMessage());

                        //Mensagem de Erro no Login
                        String messageError = null;
                        try {
                            messageError = response.getString("Message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("TAG", messageError);

                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                        Toast.makeText(context, messageError, Toast.LENGTH_SHORT).show();
                        editLogin.setText("");
                        editPassword.setText("");
                        editLogin.requestFocus();


                    } finally {
                        loginProgressBar.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    Toast.makeText(context, "Houve falha na comunicação. Tente novamente", Toast.LENGTH_SHORT).show();
                    editLogin.setText("");
                    editPassword.setText("");
                    editLogin.requestFocus();

                    loginProgressBar.setVisibility(View.INVISIBLE);
                }

            });
        }
    }


    public static String getLocalIpv4Address(){
        try {
            String ipv4;
            List<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            if(nilist.size() > 0){
                for (NetworkInterface ni: nilist){
                    List<InetAddress>  ialist = Collections.list(ni.getInetAddresses());
                    if(ialist.size()>0){
                        for (InetAddress address: ialist){
                            if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = address.getHostAddress())){
                                return ipv4;
                            }
                        }
                    }
                }
            }

        } catch (Exception ex) {

        }
        return "";
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
