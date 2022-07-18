package org.beanmaker.v2.cli;

import picocli.CommandLine;

public enum ReturnCode {
    SUCCESS(CommandLine.ExitCode.OK),
    SYSTEM_ERROR(CommandLine.ExitCode.SOFTWARE),
    USER_ERROR(CommandLine.ExitCode.USAGE);

    ReturnCode(int exitCode) {
        this.exitCode = exitCode;
    }

    private final int exitCode;

    int code() {
        return exitCode;
    }

}
