package im.maya.forcavendaseudora.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ricardosousa on 8/27/15.
 */
public class ObjectReturn {

    @SerializedName("Total")
    private int total;
    @SerializedName("List")
    private List<Communication> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Communication> getList() {
        return list;
    }

    public void setList(List<Communication> list) {
        this.list = list;
    }
}
