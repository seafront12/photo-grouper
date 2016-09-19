package scaffolding.org.seafront.photo;

import spark.Spark;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.OK;

public class RestfulServer implements AutoCloseable {
    public static Operation post(String uri, Response.Status status) {
        return new Post(uri, status);
    }

    public static Operation post(String uri, Map<String, String> headers, List<String> requests, Response.Status status, String responseBody) {
        return new Post(uri, headers, requests, status, responseBody);
    }

    public static Operation get(String uri, String response) {
        return new Get(uri, response);
    }

    public static Operation head(String uri, Response.Status status) {
        return new Head(uri, status);
    }

    public static Operation get(String uri, String response, Response.Status status) {
        return new Get(uri, response, status);
    }

    public RestfulServer(int port, Operation... operations) {
        Spark.port(port);
        for (Operation operation : operations) {
            operation.setup();
        }
    }

    public void close() throws Exception {
        System.out.println("Closing Spark on " + Thread.currentThread().getName());
        Spark.stop();
    }

    public interface Operation {
        void setup();
    }

    public static final class Post implements Operation {
        private String uri;
        private Map<String, String>  headers;
        private Response.Status status;
        private String responseBody = "";
        private List<String> requests;

        public Post(String uri, Response.Status status) {
            this.uri = uri;
            this.status = status;
        }

        public Post(String uri, Map<String, String> headers, List<String> requests, Response.Status status, String responseBody) {
            this.uri = uri;
            this.headers = headers;
            this.status = status;
            this.responseBody = responseBody;
            this.requests = requests;
        }

        public void setup() {
            Spark.post(uri, (req, res) -> {
                if (requests != null) {
                    requests.add(req.body());
                }
                if (headers != null) {
                    req.headers().forEach(h -> headers.put(h, req.headers(h)));
                }
                res.status(status.getStatusCode());
                res.type("application/json");
                return responseBody;
            });
        }
    }

    public static final class Get implements Operation {
        private Response.Status status = OK;
        private String uri;
        private final String response;

        public Get(String uri, String response) {
            this.uri = uri;
            this.response = response;
        }

        public Get(String uri, String response, Response.Status status) {
            this.uri = uri;
            this.response = response;
            this.status = status;
        }

        public void setup() {
            Spark.get(uri, (req, res) -> {
                res.status(status.getStatusCode());
                res.type("application/json");
                res.body(response);
                return response;
            });
        }
    }

    public static final class Head implements Operation {
        private Response.Status status = OK;
        private String uri;

        public Head(String uri, Response.Status status) {
            this.uri = uri;
            this.status = status;
        }

        public void setup() {
            Spark.head(uri, (req, res) -> {
                res.status(status.getStatusCode());
                return "";
            });
        }
    }
}
