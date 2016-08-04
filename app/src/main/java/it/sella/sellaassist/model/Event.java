package it.sella.sellaassist.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GodwinRoseSamuel on 27-Jul-16.
 */
public class Event implements Parcelable {
    private int id;
    private String title;
    private long startTimestamp;
    private long endTimestamp;
    private String bannerImage;
    private String createdByName;
    private String createdByProfileImage;
    private long createdByTimestamp;
    private String address;
    private int interested;
    private boolean remainder;
    private String description;

    public Event(Parcel in) {
        id = in.readInt();
        title = in.readString();
        startTimestamp = in.readLong();
        endTimestamp = in.readLong();
        bannerImage = in.readString();
        createdByName = in.readString();
        createdByProfileImage = in.readString();
        createdByTimestamp = in.readLong();
        address = in.readString();
        interested = in.readInt();
        remainder = in.readByte() != 0;
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeLong(startTimestamp);
        dest.writeLong(endTimestamp);
        dest.writeString(bannerImage);
        dest.writeString(createdByName);
        dest.writeString(createdByProfileImage);
        dest.writeLong(createdByTimestamp);
        dest.writeString(address);
        dest.writeInt(interested);
        dest.writeByte((byte) (remainder ? 1 : 0));
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

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

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getCreatedByProfileImage() {
        return createdByProfileImage;
    }

    public void setCreatedByProfileImage(String createdByProfileImage) {
        this.createdByProfileImage = createdByProfileImage;
    }

    public long getCreatedByTimestamp() {
        return createdByTimestamp;
    }

    public void setCreatedByTimestamp(long createdByTimestamp) {
        this.createdByTimestamp = createdByTimestamp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getInterested() {
        return interested;
    }

    public void setInterested(int interested) {
        this.interested = interested;
    }

    public boolean isRemainder() {
        return remainder;
    }

    public void setRemainder(boolean remainder) {
        this.remainder = remainder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                ", bannerImage='" + bannerImage + '\'' +
                ", createdByName='" + createdByName + '\'' +
                ", createdByProfileImage='" + createdByProfileImage + '\'' +
                ", createdByTimestamp=" + createdByTimestamp +
                ", address='" + address + '\'' +
                ", interested=" + interested +
                ", remainder=" + remainder +
                ", description='" + description + '\'' +
                '}';
    }
}
