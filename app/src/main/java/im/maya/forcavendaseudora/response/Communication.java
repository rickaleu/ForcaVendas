package im.maya.forcavendaseudora.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ricardosousa on 8/27/15.
 */
public class Communication {

    @SerializedName("Id")
    private int id;

    @SerializedName("Title")
    private String title;

    @SerializedName("Date")
    private String date;

    @SerializedName("Description")
    private String descricao;

    @SerializedName("Link")
    private String link;

    @SerializedName("Readed")
    private Boolean readed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

}
