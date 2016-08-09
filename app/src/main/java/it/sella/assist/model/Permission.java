package it.sella.assist.model;

/**
 * Created by GodwinRoseSamuel on 22-Jul-16.
 */
public class Permission {

    private String name;
    private int imageId;
    private String title;
    private String description;

    public Permission(String name, int imageId, String title, String description) {
        this.name = name;
        this.imageId = imageId;
        this.title = title;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
