package ro.example.lifecyclejava;

/** Datele unui element din galeria foto cu arici. */
public class HedgehogGalleryItem {
    private final String title;
    private final String description;
    private final String imageUrl;
    private final String imageSourceUrl;
    private final String attribution;
    private final String licenseUrl;
    private final String detailsUrl;

    public HedgehogGalleryItem(String title, String description, String imageUrl,
                               String imageSourceUrl, String attribution,
                               String licenseUrl, String detailsUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.imageSourceUrl = imageSourceUrl;
        this.attribution = attribution;
        this.licenseUrl = licenseUrl;
        this.detailsUrl = detailsUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getImageSourceUrl() {
        return imageSourceUrl;
    }

    public String getAttribution() {
        return attribution;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public String getDetailsUrl() {
        return detailsUrl;
    }
}
