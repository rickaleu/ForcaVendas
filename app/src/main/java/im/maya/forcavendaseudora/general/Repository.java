package im.maya.forcavendaseudora.general;

import im.maya.forcavendaseudora.request.BaseRequest;
import im.maya.forcavendaseudora.response.ReturnService;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by ricardosousa on 8/27/15.
 */
public interface Repository {

    @POST("/list")
    void getLista(@Body BaseRequest request, Callback<ReturnService> cb);
}
