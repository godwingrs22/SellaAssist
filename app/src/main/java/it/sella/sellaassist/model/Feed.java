package it.sella.sellaassist.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GodwinRoseSamuel on 23-Jul-16.
 */
public class Feed implements Parcelable {
    private int id;
    private String createdByName;
    private String profileImage;
    private String startTimestamp;
    private String itArea;
    private boolean isImportant;
    private String message;
    private String image;
    private String url;

    public Feed(Parcel in) {
        id = in.readInt();
        createdByName = in.readString();
        profileImage = in.readString();
        startTimestamp = in.readString();
        itArea = in.readString();
        isImportant = in.readByte() != 0;
        message = in.readString();
        image = in.readString();
        url = in.readString();
    }

    public static final Creator<Feed> CREATOR = new Creator<Feed>() {
        @Override
        public Feed createFromParcel(Parcel in) {
            return new Feed(in);
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(String startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public String getItArea() {
        return itArea;
    }

    public void setItArea(String itArea) {
        this.itArea = itArea;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(createdByName);
        dest.writeString(profileImage);
        dest.writeString(startTimestamp);
        dest.writeString(itArea);
        dest.writeByte((byte) (isImportant ? 1 : 0));
        dest.writeString(message);
        dest.writeString(image);
        dest.writeString(url);
    }

    @Override
    public String toString() {
        return "Feed{" +
                "id=" + id +
                ", createdByName='" + createdByName + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", startTimestamp='" + startTimestamp + '\'' +
                ", itArea='" + itArea + '\'' +
                ", isImportant=" + isImportant +
                ", message='" + message + '\'' +
                ", image='" + image + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}