package c8102ea2a569.service2tomcat.exception;

import java.io.Serializable;

public class ServiceUnavailableException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    public ServiceUnavailableException(String message) {
        super(message);
    }
}
