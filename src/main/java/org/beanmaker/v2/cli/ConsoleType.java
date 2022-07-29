package org.beanmaker.v2.cli;

import java.io.PrintStream;

enum ConsoleType {
    DATA(System.out), MESSAGES(System.err);

    static final String COMMAND_STYLE = "bold,underline";

    private final PrintStream out;

    ConsoleType(PrintStream out) {
        this.out = out;
    }

    PrintStream getPrintStream() {
        return out;
    }

    void print(String text) {
        out.print(text);
    }

    void println(String text) {
        out.println(text);
    }

}
