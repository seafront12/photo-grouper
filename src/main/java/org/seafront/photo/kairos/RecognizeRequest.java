package org.seafront.photo.kairos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecognizeRequest {
    private final String encodedImage;
    private final String galleryName;

    public RecognizeRequest(String encodedImage, String galleryName) {
        this.galleryName = galleryName;
        this.encodedImage = encodedImage;
    }

    @JsonProperty("image")
    public String encodedImage() {
        return encodedImage;
    }

    @JsonProperty("gallery_name")
    public String galleryName() {
        return galleryName;
    }
}
