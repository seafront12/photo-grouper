package org.seafront.photo.kairos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnrollRequest {
    private final String encodedImage;
    private final String enrollPersonName;
    private final String galleryName;
    private final String selector = "SETPOSE";
    private final String symmetricFill = "true";

    public EnrollRequest(String encodedImage, String enrollPersonName, String galleryName) {
        this.encodedImage = encodedImage;
        this.enrollPersonName = enrollPersonName;
        this.galleryName = galleryName;
    }

    @JsonProperty("image")
    public String getEncodedImage() {
        return encodedImage;
    }

    @JsonProperty("subject_id")
    public String getEnrollPersonName() {
        return enrollPersonName;
    }

    @JsonProperty("gallery_name")
    public String getGalleryName() {
        return galleryName;
    }

    @JsonProperty("selector")
    public String getSelector() {
        return selector;
    }

    @JsonProperty("symmetricFill")
    public String getSymmetricFill() {
        return symmetricFill;
    }
}
