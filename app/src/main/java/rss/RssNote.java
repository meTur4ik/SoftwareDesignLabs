package rss;

public class RssNote {
    // TODO TODO TODO TODO TODO TOTODODO
    private String title;
    private String description;
    private String imageUri;
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RssNote() {
    }

    public RssNote(String title) {
        this.title = title;
    }

    public RssNote(String title, String description, String imageUri, String link) {
        this.title = title;
        this.description = description;
        this.imageUri = imageUri;
        this.link = link;
    }
}
