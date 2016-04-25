package im.maya.forcavendaseudora.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ricardosousa on 8/27/15.
 */
public class ReturnService {

    @SerializedName("Code")
    private int code;

    @SerializedName("Message")
    private String message;

    @SerializedName("Object")
    private ObjectReturn objectReturn;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ObjectReturn getObjectReturn() {
        return objectReturn;
    }

    public void setObjectReturn(ObjectReturn objectReturn) {
        this.objectReturn = objectReturn;
    }

}
