package com.mvnikitin.issuetracker.exception;

public class NotDefinedWorkflowException extends RuntimeException {

    public NotDefinedWorkflowException() {
        super();
    }

    public NotDefinedWorkflowException(String message) {
        super(message);
    }
}
