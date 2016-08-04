package it.sella.sellaassist.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GodwinRoseSamuel on 16-Jan-16.
 */
public class User implements Parcelable {
    private String gbsID;
    private String name;
    private String password;
    private String profilePic;
    private String deviceId;
    private boolean isLoggedIn;
    private String businessUnit;

    public User(Parcel in) {
        gbsID = in.readString();
        name = in.readString();
        password = in.readString();
        profilePic = in.readString();
        deviceId = in.readString();
        isLoggedIn = in.readByte() != 0;
        businessUnit = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gbsID);
        dest.writeString(name);
        dest.writeString(password);
        dest.writeString(profilePic);
        dest.writeString(deviceId);
        dest.writeByte((byte) (isLoggedIn ? 1 : 0));
        dest.writeString(businessUnit);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getGbsID() {
        return gbsID;
    }

    public void setGbsID(String gbsID) {
        this.gbsID = gbsID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    @Override
    public String toString() {
        return "User{" +
                "gbsID='" + gbsID + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", isLoggedIn=" + isLoggedIn +
                ", businessUnit='" + businessUnit + '\'' +
                '}';
    }
}
