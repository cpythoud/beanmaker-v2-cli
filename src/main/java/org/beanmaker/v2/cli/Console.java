package org.beanmaker.v2.cli;

import picocli.CommandLine.Help.Ansi;

import java.io.PrintStream;

enum Console {
    INSTANCE;

    private static final PrintStream OUT = System.err;

    static final String COMMAND_STYLE = "bold,underline";

    Console print(Status status, String message) {
        OUT.print(ansiFormat(status, message, false, null));
        return this;
    }

    Console print(Status status, String message, boolean showPrefix) {
        OUT.print(ansiFormat(status, message, showPrefix, null));
        return this;
    }

    Console print(Status status, String message, String extraStyles) {
        OUT.print(ansiFormat(status, message, false, extraStyles));
        return this;
    }

    Console println(Status status, String message) {
        OUT.println(ansiFormat(status, message, false, null));
        return this;
    }

    Console println(Status status, String message, boolean showPrefix) {
        OUT.println(ansiFormat(status, message, showPrefix, null));
        return this;
    }

    Console println(Status status, String message, String extraStyles) {
        OUT.println(ansiFormat(status, message, false, extraStyles));
        return this;
    }

    private String ansiFormat(Status status, String message, boolean showPrefix, String extraStyles) {
        String prefix = status.getPrefix();
        String color = status.getColor();

        var ansiText = new StringBuilder();
        if (prefix != null && showPrefix)
            ansiText.append("@|fg(%s),bold %s|@ ".formatted(color, prefix));
        ansiText.append("@|fg(").append(color).append(")");
        if (extraStyles != null)
            ansiText.append(",").append(extraStyles);
        ansiText.append(" ").append(message).append("|@");

        return Ansi.AUTO.string(ansiText.toString());
    }

}
