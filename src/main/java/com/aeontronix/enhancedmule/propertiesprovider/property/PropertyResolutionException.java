package com.aeontronix.enhancedmule.propertiesprovider.property;

public class PropertyResolutionException extends RuntimeException {
    public PropertyResolutionException() {
    }

    public PropertyResolutionException(String message) {
        super(message);
    }

    public PropertyResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertyResolutionException(Throwable cause) {
        super(cause);
    }

    public PropertyResolutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
