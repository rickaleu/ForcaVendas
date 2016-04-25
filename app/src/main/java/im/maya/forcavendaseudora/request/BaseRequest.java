package im.maya.forcavendaseudora.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ricardosousa on 8/27/15.
 */

public class BaseRequest {

    @SerializedName("Token")
    private String token;
    @SerializedName("Page")
    private int page;
    @SerializedName("Amount")
    private int amount;

    public BaseRequest(String token, int page, int amount) {
        this.token = token;
        this.page = page;
        this.amount = amount;
    }

}
