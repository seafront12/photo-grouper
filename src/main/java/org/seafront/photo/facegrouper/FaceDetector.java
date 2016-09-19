package org.seafront.photo.facegrouper;

import java.io.File;

public interface FaceDetector {
    boolean enroll(String gallery, String name, File image);
    boolean verify(String gallery, String name, File image);
    String recognize(String gallery, File image);
}
