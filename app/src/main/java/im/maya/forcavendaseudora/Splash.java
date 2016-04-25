package im.maya.forcavendaseudora;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


public class Splash extends Activity {

    private static int SPLASH_TIME_OUT = 2000;

    //Context
    private Context context;

    private String tokenCache;
    private String tokenDifferent = "0";


    //======| Device token |======
    String SENDER_ID = "48199381107";
    GoogleCloudMessaging gcm;
    String registrationId;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //============================


    //=========| Main |===========
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = this;

        //Verifica se já existe o sharedPreferences
        SharedPreferences prefsSplash = context.getSharedPreferences("im.maya.forcavendaseudora", Context.MODE_PRIVATE);
        tokenCache = prefsSplash.getString("im.maya.forcavendaseudora.user.Token", "");

        Log.i("TOKEN CACHE", tokenCache);

        if (!prefsSplash.contains("im.maya.forcavendaseudora.deviceToken")) {

            //Chamamos o serviço do google para gerar o device token ou chamamos o delay para abrir a tela.
            Log.i("Comunicados Eudora", "Não possui o devicetoken no sharedPreferences!");

            if (checkPlayServices()) {

                gcm = GoogleCloudMessaging.getInstance(context);
                Log.i("GCM", String.valueOf(gcm));
                registerInBackground();

            } else {
                Log.i("Comunicados Eudora", "No valid Google Play Services APK found.");
            }
        } else{

            //Já existe a chave salva no sharedPreferences.
            Log.i("Comunicados Eudora", "Já possui o devicetoken no sharedPreferences!");

            //Chama a tela com delay.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(tokenCache.isEmpty() && tokenCache != tokenDifferent) {
                        callLoginIntent();
                    }
                    else{
                        callHomeIntent();
                    }

                }
            }, SPLASH_TIME_OUT);
        }
    }

    //==================================



    //===========| Methods |============

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();

            } else {
                Log.i("Comunicados Eudora", "This device is not supported.");
                finish();
            }
            return false;
        }

        return true;
    }


    private void registerInBackground() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                        Log.i("GCM RegisBack", String.valueOf(gcm));
                    }
                    registrationId = gcm.register(SENDER_ID);
                    Log.i("RegID SENDERID", String.valueOf(registrationId));

                    //Sharepreferences
                    SharedPreferences prefsSplash = context.getSharedPreferences("im.maya.forcavendaseudora", Context.MODE_PRIVATE);
                    prefsSplash.edit().putString("im.maya.forcavendaseudora.deviceToken", registrationId).apply();


                    callLoginIntent();

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                    callLoginIntent();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }


    //Send to login activity
    private  void  callLoginIntent(){

        Intent intent = new Intent(context, Login.class);
        context.startActivity(intent);
        finish();

    }

    //Send to home activity
    private  void  callHomeIntent(){

        Intent intent = new Intent(context, Home.class);
        context.startActivity(intent);
        finish();

    }

    //==================================


}





