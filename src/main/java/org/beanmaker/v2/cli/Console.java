package org.beanmaker.v2.cli;

import picocli.CommandLine;

import java.io.PrintStream;

class Console {

    static final String COMMAND_STYLE = "bold,underline";

    private final PrintStream out;

    private Status status;

    Console(ConsoleType consoleType) {
        out = consoleType.getPrintStream();
    }

    Console status(Status status) {
        this.status = status;
        return this;
    }

    Console resetStatus() {
        status = null;
        return this;
    }

    Console printStatus() {
        if (status == null || status.getPrefix() == null)
            return this;

        String statusText = "@|fg(%s),bold %s|@ ".formatted(status.getColor(), status.getPrefix());
        out.print(CommandLine.Help.Ansi.AUTO.string(statusText));
        return this;
    }

    Console print(String text) {
        return print(text, null);
    }

    Console print(String text, String extraStyles) {
        out.print(ansiFormat(text, extraStyles));
        return this;
    }

    Console println(String text) {
        return println(text, null);
    }

    Console println(String text, String extraStyles) {
        out.println(ansiFormat(text, extraStyles));
        return this;
    }

    private String ansiFormat(String text, String extraStyles) {
        if (status == null && extraStyles == null)
            return text;

        var ansiText = new StringBuilder();
        ansiText.append("@|");
        if (status != null)
            ansiText.append("fg(").append(status.getColor()).append("),bold");
        if (extraStyles != null) {
            if (status != null)
                ansiText.append(",");
            ansiText.append(extraStyles);
        }
        ansiText.append(" ").append(text).append("|@");

        return CommandLine.Help.Ansi.AUTO.string(ansiText.toString());
    }

    // * Quickies
    void ok(String message) {
        status = Status.OK;
        println(message);
    }

    void notice(String message) {
        status = Status.NOTICE;
        printStatus().println(message);
    }

    void warning(String message) {
        status = Status.WARNING;
        printStatus().println(message);
    }

    void error(String message) {
        status = Status.ERROR;
        printStatus().println(message);
    }

}
