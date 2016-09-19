package org.seafront.photo.kairos;

import java.util.List;

public class KairosResponse {
    List<Image> images;

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getTransactionStatus(){
        if(images.size()==0 || images.get(0).getTransaction() == null){
            return "unknown";
        }
        return images.get(0).getTransaction().getStatus();
    }

    public String getRecognizedUser(){
        if(images.size() == 0 || images.get(0).getTransaction() == null){
            return "unknown";
        }
        return images.get(0).getTransaction().getSubject();
    }

    public double getRecognizeConfidence() {
        if(images.size() == 0 || images.get(0).getTransaction() == null){
            return 0d;
        }
        return images.get(0).getTransaction().getConfidence();
    }
}
