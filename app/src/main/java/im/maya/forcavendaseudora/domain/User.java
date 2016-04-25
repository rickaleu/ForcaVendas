package im.maya.forcavendaseudora.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ricardo.sousa on 1/30/15.
 */
public class User implements Parcelable {

    private String username;
    private String password;
    private String deviceToken;
    private String deviceType;
    private String deviceModel;
    private String deviceOs;
    private String ipAdress;

    private String name;
    private boolean ir;
    private String email;
    private String photo;
    private String token;
    private String message;

    public User() {
    }

    public User(String username, String password, String deviceToken, String deviceType, String deviceModel, String deviceOs, String ipAdress, String name, boolean ir, String email, String photo, String token, String message) {
        this.username = username;
        this.password = password;
        this.deviceToken = deviceToken;
        this.deviceType = deviceType;
        this.deviceModel = deviceModel;
        this.deviceOs = deviceOs;
        this.ipAdress = ipAdress;
        this.name = name;
        this.ir = ir;
        this.email = email;
        this.photo = photo;
        this.token = token;
        this.message = message;
    }


    protected User(Parcel in) {
        username = in.readString();
        password = in.readString();
        deviceToken = in.readString();
        deviceType = in.readString();
        deviceModel = in.readString();
        deviceOs = in.readString();
        ipAdress = in.readString();
        name = in.readString();
        ir = in.readByte() != 0;
        email = in.readString();
        photo = in.readString();
        token = in.readString();
        message = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(deviceToken);
        dest.writeString(deviceType);
        dest.writeString(deviceModel);
        dest.writeString(deviceOs);
        dest.writeString(ipAdress);
        dest.writeString(name);
        dest.writeByte((byte) (ir ? 1 : 0));
        dest.writeString(email);
        dest.writeString(photo);
        dest.writeString(token);
        dest.writeString(message);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceOs() {
        return deviceOs;
    }

    public void setDeviceOs(String deviceOs) {
        this.deviceOs = deviceOs;
    }

    public String getIpAdress() {
        return ipAdress;
    }

    public void setIpAdress(String ipAdress) {
        this.ipAdress = ipAdress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIR() {
        return ir;
    }

    public void setIR(Boolean ir) {
        this.ir = ir;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

