package org.seafront.photo.kairos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VerifyRequest {
    private final String encodedImage;
    private final String enrollPersonName;
    private final String galleryName;

    public VerifyRequest(String encodedImage, String enrollPersonName, String galleryName) {
        this.galleryName = galleryName;
        this.enrollPersonName = enrollPersonName;
        this.encodedImage = encodedImage;
    }

    @JsonProperty("image")
    public String encodedImage() {
        return encodedImage;
    }

    @JsonProperty("subject_id")
    public String enrollPersonName() {
        return enrollPersonName;
    }

    @JsonProperty("gallery_name")
    public String galleryName() {
        return galleryName;
    }
}
