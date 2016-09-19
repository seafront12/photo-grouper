package org.seafront.photo.restful;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static javax.ws.rs.client.Entity.entity;

public class RestfulClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestfulClient.class);

    private final String serviceBase;
    private static final int TIMEOUT = 800;
    private final Client client;

    private int lastWorkingServiceBaseIdx = 0;


    public RestfulClient(String serviceBase, String appId, String appKey) {
        this.serviceBase = serviceBase;
        if (this.serviceBase == null || serviceBase.isEmpty()) {
            throw new IllegalArgumentException("No Valid serviceBase configured.");
        }

        client = TrustAllSsl.trustAllSslClientBuilder().build();
        client.register(new Authenticator(appId, appKey));
    }

    public Response get(String servicePath) throws URISyntaxException, MalformedURLException {
        URL baseUrl = new URL(serviceBase);
        String url = new URI(baseUrl.getProtocol(), baseUrl.getUserInfo(), baseUrl.getHost(),
                baseUrl.getPort(), baseUrl.getPath() + servicePath, baseUrl.getQuery(), null).toString();
        LOGGER.info("Calling URL GET: {}", url);
        return client.target(url).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
    }

    public <I> Response  post(String servicePath, I postEntity) throws MalformedURLException, URISyntaxException {
        URL baseUrl = new URL(serviceBase);
        String url = new URI(baseUrl.getProtocol(), baseUrl.getUserInfo(), baseUrl.getHost(),
                baseUrl.getPort(), baseUrl.getPath() + servicePath, baseUrl.getQuery(), null).toString();
        LOGGER.info("Calling URL POST: {}", url);
        return client.target(url).request().accept(MediaType.APPLICATION_JSON_TYPE).post(entity(postEntity, MediaType.APPLICATION_JSON_TYPE));
    }

    public <I, T> T post(String servicePath, Class<T> type, I postBody, T defaultValue) {
        Response response = null;
        try {
            response = this.post(servicePath, postBody);
            if (response.getStatus() == 200) {
                return response.readEntity(type);
            } else {
                LOGGER.warn("Calling {} get status code: {}, body: {}", servicePath, response.getStatus(),
                        response.readEntity(String.class));
            }
        } catch (RestServiceNotAvailableException e) {
            LOGGER.error("Service not available: {}", servicePath, e);
        } catch (ProcessingException e) {
            LOGGER.error("Failed to process message in response of ()", servicePath, e);
        } catch (Exception e) {
            if (response != null) {
                String message = response.readEntity(String.class);
                LOGGER.error("Call returned: {} ", message, e);
            }
            LOGGER.error("Unknown error when call: {} ", servicePath, e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return defaultValue;
    }

    public <T> T get(String servicePath, Class<T> type, T defaultValue) {
        Response response = null;
        try {
            response = this.get(servicePath);
            if (response.getStatus() == 200) {
                return response.readEntity(type);
            } else {
                LOGGER.warn("Calling {} get status code: {}, body: {}", servicePath, response.getStatus(),
                        response.readEntity(String.class));
            }
        } catch (RestServiceNotAvailableException e) {
            LOGGER.error("Service not available: {}", servicePath, e);
        } catch (ProcessingException e) {
            LOGGER.error("Failed to process message in response of ()", servicePath, e);
        } catch (Exception e) {
            if (response != null) {
                String message = response.readEntity(String.class);
                LOGGER.error("Call returned: {} ", message, e);
            }
            LOGGER.error("Unknown error when call: {} ", servicePath, e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return defaultValue;
    }


    public class Authenticator implements ClientRequestFilter {

        private final String appId;
        private final String appKey;

        public Authenticator(String appId, String appKey) {
            this.appId = appId;
            this.appKey = appKey;
        }

        public void filter(ClientRequestContext requestContext) throws IOException {
            MultivaluedMap<String, Object> headers = requestContext.getHeaders();
            headers.add("app_id", appId);
            headers.add("app_key", appKey);
        }

    }

}
