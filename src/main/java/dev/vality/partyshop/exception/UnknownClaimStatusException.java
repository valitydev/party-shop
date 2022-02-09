package dev.vality.partyshop.exception;

public class UnknownClaimStatusException extends RuntimeException {
    public UnknownClaimStatusException() {
    }

    public UnknownClaimStatusException(String message) {
        super(message);
    }

    public UnknownClaimStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownClaimStatusException(Throwable cause) {
        super(cause);
    }

    public UnknownClaimStatusException(String message,
                                       Throwable cause,
                                       boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
