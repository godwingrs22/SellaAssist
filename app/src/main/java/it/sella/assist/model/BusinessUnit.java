package it.sella.assist.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GodwinRoseSamuel on 05-Aug-16.
 */
public class BusinessUnit implements Parcelable {

    private int id;
    private String name;
    private String ownerId;

    public BusinessUnit(Parcel in) {
        id = in.readInt();
        name = in.readString();
        ownerId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(ownerId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BusinessUnit> CREATOR = new Creator<BusinessUnit>() {
        @Override
        public BusinessUnit createFromParcel(Parcel in) {
            return new BusinessUnit(in);
        }

        @Override
        public BusinessUnit[] newArray(int size) {
            return new BusinessUnit[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "BusinessUnit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ownerId='" + ownerId + '\'' +
                '}';
    }
}
