package it.sella.assist.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GodwinRoseSamuel on 24-Jul-16.
 */
public class BiometricInfo implements Parcelable {

    private String date;
    private long timestamp;
    private String location;
    private boolean sent;

    public BiometricInfo(Parcel in) {
        date = in.readString();
        timestamp = in.readLong();
        location = in.readString();
        sent = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeLong(timestamp);
        dest.writeString(location);
        dest.writeByte((byte) (sent ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BiometricInfo> CREATOR = new Creator<BiometricInfo>() {
        @Override
        public BiometricInfo createFromParcel(Parcel in) {
            return new BiometricInfo(in);
        }

        @Override
        public BiometricInfo[] newArray(int size) {
            return new BiometricInfo[size];
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    @Override
    public String toString() {
        return "BiometricInfo{" +
                "date='" + date + '\'' +
                ", timestamp=" + timestamp +
                ", location='" + location + '\'' +
                ", sent=" + sent +
                '}';
    }
}
