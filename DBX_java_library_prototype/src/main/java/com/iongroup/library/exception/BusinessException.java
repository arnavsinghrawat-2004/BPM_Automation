package com.iongroup.library.exception;

/**
 * Represents a controlled business failure.
 *
 * Use this for:
 * - Validation errors
 * - Rule violations
 * - Expected business rejections
 *
 * This is NOT for technical failures.
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String message) {
        super(message);
        this.errorCode = null;
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Optional error code for BPMN mapping / audit
     */
    public String getErrorCode() {
        return errorCode;
    }
}
