package org.seafront.photo.facegrouper;

import org.seafront.photo.kairos.EnrollRequest;
import org.seafront.photo.kairos.KairosResponse;
import org.seafront.photo.kairos.RecognizeRequest;
import org.seafront.photo.kairos.VerifyRequest;
import org.seafront.photo.restful.RestfulClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class KairosFaceDetector implements FaceDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(KairosFaceDetector.class);
    private final RestfulClient client;

    @Inject
    public KairosFaceDetector(String serviceUrl, String appId, String appKey) {
        client = new RestfulClient(serviceUrl, appId, appKey);
    }

    @Override
    public boolean enroll(String gallery, String name, File image) {

        try {
            EnrollRequest request = new EnrollRequest(encodeBase64(image), name, gallery);
            KairosResponse response = client.post("/enroll", KairosResponse.class, request, new KairosResponse());
            LOGGER.info("Enroll result for {}@{} of {}: {}", name, gallery, image.getName(), response.getTransactionStatus());
            return "success".equals(response.getTransactionStatus());
        } catch (Exception e) {
            LOGGER.error("Failed to enroll", e);
        }
        return false;
    }

    @Override
    public boolean verify(String gallery, String name, File image) {
        try {
            VerifyRequest request = new VerifyRequest(encodeBase64(image), name, gallery);
            KairosResponse response = client.post("/verify", KairosResponse.class, request, new KairosResponse());
            LOGGER.info("Verify result for {}@{} of {}: {}", name, gallery, image.getName(), response.getTransactionStatus());
            return "success".equals(response.getTransactionStatus());
        } catch (Exception e) {
            LOGGER.error("Failed to verify");
        }
        return false;
    }

    @Override
    public String recognize(String gallery, File image) {
        try {
            RecognizeRequest recognizeRequest = new RecognizeRequest(encodeBase64(image), gallery);
            KairosResponse response = client.post("/recognize", KairosResponse.class, recognizeRequest, new KairosResponse());
            LOGGER.info("Recognize {} in gallery {} of {} is {} with confidence {}", response.getTransactionStatus(), gallery, image.getName(), response.getRecognizedUser(), response.getRecognizeConfidence());
            return response.getRecognizedUser();
        } catch (Exception e) {
            LOGGER.error("Failed to recognize");
        }
        return "unknown";
    }

    private String encodeBase64(File image) throws IOException {
        return Base64.getEncoder().encodeToString(Files.readAllBytes(image.toPath()));
    }

}
