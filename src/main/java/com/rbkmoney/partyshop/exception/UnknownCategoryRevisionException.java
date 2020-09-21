package com.rbkmoney.partyshop.exception;

public class UnknownCategoryRevisionException extends RuntimeException {
    public UnknownCategoryRevisionException() {
    }

    public UnknownCategoryRevisionException(String message) {
        super(message);
    }

    public UnknownCategoryRevisionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownCategoryRevisionException(Throwable cause) {
        super(cause);
    }

    public UnknownCategoryRevisionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
