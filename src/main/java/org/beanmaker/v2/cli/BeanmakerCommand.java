package org.beanmaker.v2.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import picocli.jansi.graalvm.AnsiConsole;

@Command(
        name = "beanmaker",
        version = "BeanMaker CLI 1.0",
        mixinStandardHelpOptions = true,
        subcommands = { DatabaseCommand.class, InitCommand.class, ProjectCommand.class, CommandLine.HelpCommand.class }
)
public class BeanmakerCommand {

    public static void main(String[] args) {
        int exitCode;
        try (AnsiConsole ansi = AnsiConsole.windowsInstall()) {
            exitCode = new CommandLine(new BeanmakerCommand()).execute(args);
        }
        System.exit(exitCode);
    }

}
