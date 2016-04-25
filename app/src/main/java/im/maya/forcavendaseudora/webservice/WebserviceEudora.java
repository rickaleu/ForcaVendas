package im.maya.forcavendaseudora.webservice;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by ricardo.sousa on 3/5/15.
 */
public class WebserviceEudora {

    private static String BASE_URL = "http://fv.eudoracloud.com/";

    private static String STRING_PATH_ACCOUNT = BASE_URL + "service/api/account/";
    private static String STRING_PATH_BULLETIN = BASE_URL + "service/api/bulletin/";

    private static AsyncHttpClient client = new AsyncHttpClient();



    //Post Login
    public static void postLogin(RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler){
        client.post(STRING_PATH_ACCOUNT + "login", params, asyncHttpResponseHandler);
    }

    public static void postLogout(RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler){
        client.post(STRING_PATH_ACCOUNT + "logout", params, asyncHttpResponseHandler);
    }

    public static void postValidateToken(RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler){
        client.post(STRING_PATH_ACCOUNT + "validateToken", params, asyncHttpResponseHandler);
    }

    public static void postSetConfig(RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler){
        client.post(STRING_PATH_ACCOUNT + "setConfig", params, asyncHttpResponseHandler);
    }

    public static void postGetConfig(RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler){
        client.post(STRING_PATH_ACCOUNT + "getConfig", params, asyncHttpResponseHandler);
    }

    //Post List
//    public static void postList(RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler){
//        client.post(STRING_PATH_BULLETIN + "list", params, asyncHttpResponseHandler);
//    }

    public static void postReaded(RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler){
        client.post(STRING_PATH_BULLETIN + "readed", params, asyncHttpResponseHandler);
    }

}
