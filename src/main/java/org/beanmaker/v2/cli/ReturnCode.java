package org.beanmaker.v2.cli;

public enum ReturnCode {
    SUCCESS(0), SYSTEM_ERROR(1), USER_ERROR(2);

    ReturnCode(int exitCode) {
        this.exitCode = exitCode;
    }

    private final int exitCode;

    int code() {
        return exitCode;
    }

}
