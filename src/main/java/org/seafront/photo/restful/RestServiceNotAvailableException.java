package org.seafront.photo.restful;

import com.google.common.base.Joiner;

public class RestServiceNotAvailableException extends RuntimeException {
    public RestServiceNotAvailableException(String[] serviceBases, String servicePath) {
        super(String.format("Service Not Available. Base: %s, Path: %s", Joiner.on(',').join(serviceBases), servicePath));
    }
}
