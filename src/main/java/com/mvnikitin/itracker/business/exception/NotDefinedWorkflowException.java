package com.mvnikitin.itracker.business.exception;

public class NotDefinedWorkflowException extends RuntimeException {

    public NotDefinedWorkflowException() {
        super();
    }

    public NotDefinedWorkflowException(String message) {
        super(message);
    }
}
