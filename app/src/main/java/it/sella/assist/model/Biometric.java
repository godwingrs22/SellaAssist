package it.sella.assist.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GodwinRoseSamuel on 03-Aug-16.
 */
public class Biometric implements Parcelable {

    private String date;

    public Biometric(Parcel in) {
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Biometric> CREATOR = new Creator<Biometric>() {
        @Override
        public Biometric createFromParcel(Parcel in) {
            return new Biometric(in);
        }

        @Override
        public Biometric[] newArray(int size) {
            return new Biometric[size];
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Biometric{" +
                "date='" + date + '\'' +
                '}';
    }
}
