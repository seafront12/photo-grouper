package integration.org.seafrtont.photo;

import org.junit.Before;
import org.junit.Test;
import org.seafront.photo.facegrouper.FaceDetector;
import org.seafront.photo.facegrouper.KairosFaceDetector;
import scaffolding.org.seafront.photo.RestfulServer;

import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.file.Files;
import java.util.*;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static scaffolding.org.seafront.photo.RestfulServer.post;

public class FaceDetectorTest {
    public static final String APP_ID = "af733abb";
    public static final String APP_KEY = "b4f59341615b7c050838b9654e23ca80";
    FaceDetector faceDetector;
    private List<String> receivedRequests;
    private Map<String, String> headers;


    @Before
    public void setUp() {
        faceDetector = new KairosFaceDetector("http://localhost:8888", APP_ID, APP_KEY);
        receivedRequests = new ArrayList<>();
        headers = new HashMap<>();
    }

    @Test
    public void enrollWithAppIdAndKey() throws Exception {

        File image = new File(this.getClass().getClassLoader().getResource("alexchen.png").getPath());
        String response = "{\n" +
                "    \"images\": [\n" +
                "    {\n" +
                "        \"time\": 3.43817,\n" +
                "        \"transaction\": {\n" +
                "        \"status\": \"success\",\n" +
                "        \"face_id\": \"685ff4b47a3db8579efd2fa6a7d9293b\",\n" +
                "        \"subject_id\": \"testUser\",\n" +
                "        \"width\": 934,\n" +
                "        \"height\": 934,\n" +
                "        \"topLeftX\": 300,\n" +
                "        \"topLeftY\": 526,\n" +
                "        \"timestamp\": \"1417207442\",\n" +
                "        \"gallery_name\": \"testGallery\"\n" +
                "        },\n" +
                "        \"attributes\": {\n" +
                "            \"gender\": {\n" +
                "                \"type\": \"F\",\n" +
                "                \"confidence\": \"80%\"\n" +
                "            }\n" +
                "        }\n" +
                "    }]\n" +
                "}";
        String base64Pic = Base64.getEncoder().encodeToString(Files.readAllBytes(image.toPath()));
        try (RestfulServer server = new RestfulServer(8888, post("/enroll", headers, receivedRequests, Response.Status.OK, response))) {
            boolean enrolled = faceDetector.enroll("testGallery", "testUser", image);
            assertThat(enrolled, is(true));
            assertThat(receivedRequests.size(), is(1));
            String requestBody = receivedRequests.get(0);
            assertThat(headers.get("app_id"), is(APP_ID));
            assertThat(headers.get("app_key"), is(APP_KEY));
            assertThatJson(requestBody)
                    .node("image").isEqualTo(base64Pic)
                    .node("subject_id").isEqualTo("testUser")
                    .node("gallery_name").isEqualTo("testGallery")
                    .node("selector").isEqualTo("SETPOSE");
        }

    }

    @Test
    public void verifyFaceInImage() throws Exception {
        File image = new File(this.getClass().getClassLoader().getResource("alexchen.png").getPath());
        String response = "{\n" +
                "  \"images\": [\n" +
                "    {\n" +
                "      \"transaction\": {\n" +
                "        \"status\": \"success\",\n" +
                "        \"subject_id\": \"testUser\",\n" +
                "        \"width\": 170,\n" +
                "        \"height\": 287,\n" +
                "        \"topLeftX\": 108,\n" +
                "        \"topLeftY\": 55,\n" +
                "        \"confidence\": 0.88309,\n" +
                "        \"gallery_name\": \"testGallery\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        String base64Pic = Base64.getEncoder().encodeToString(Files.readAllBytes(image.toPath()));
        try(RestfulServer server = new RestfulServer(8888, post("/verify", headers, receivedRequests, Response.Status.OK, response))){
            boolean verified = faceDetector.verify("testGallery", "testUser", image);
            assertThat(verified, is(true));
            assertThat(receivedRequests.size(), is(1));
            String requestBody = receivedRequests.get(0);
            assertThat(headers.get("app_id"), is(APP_ID));
            assertThat(headers.get("app_key"), is(APP_KEY));
            assertThatJson(requestBody)
                    .node("image").isEqualTo(base64Pic)
                    .node("subject_id").isEqualTo("testUser")
                    .node("gallery_name").isEqualTo("testGallery");
        }
    }

    @Test
    public void recognizeFaceInImage() throws Exception {
        File image = new File(this.getClass().getClassLoader().getResource("alexchen.png").getPath());
        String response = "{\n" +
                "    \"images\": [\n" +
                "    {\n" +
                "        \"time\": 2.86091,\n" +
                "        \"transaction\":\n" +
                "        {\n" +
                "            \"status\": \"Complete\",\n" +
                "            \"subject\": \"testUser\",\n" +
                "            \"confidence\": 0.89,\n" +
                "            \"gallery_name\": \"testGallery\"\n" +
                "        },\n" +
                "        \"candidates\": [\n" +
                "        {\n" +
                "          \"subtest1\": \"0.802138030529022\",\n" +
                "          \"enrollment_timestamp\": \"1416850761\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"elizabeth\": \"0.802138030529022\",\n" +
                "          \"enrollment_timestamp\": \"1417207485\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"elizabeth\": \"0.777253568172455\",\n" +
                "          \"enrollment_timestamp\": \"1416518415\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"elizabeth\": \"0.777253568172455\",\n" +
                "          \"enrollment_timestamp\": \"1416431816\"\n" +
                "        }\n" +
                "        ]\n" +
                "    } ]\n" +
                "}";
        String base64Pic = Base64.getEncoder().encodeToString(Files.readAllBytes(image.toPath()));
        try(RestfulServer server = new RestfulServer(8888, post("/recognize", headers, receivedRequests, Response.Status.OK, response))){
            String recognizedUser = faceDetector.recognize("testGallery", image);
            assertThat(recognizedUser, is("testUser"));
            assertThat(receivedRequests.size(), is(1));
            String requestBody = receivedRequests.get(0);
            assertThat(headers.get("app_id"), is(APP_ID));
            assertThat(headers.get("app_key"), is(APP_KEY));
            assertThatJson(requestBody)
                    .node("image").isEqualTo(base64Pic)
                    .node("gallery_name").isEqualTo("testGallery");
        }
    }
}
