package org.beanmaker.v2.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "beanmaker",
        version = "BeanMaker CLI 1.0",
        subcommands = { InitCommand.class, ProjectCommand.class, CommandLine.HelpCommand.class }
)
public class BeanmakerCommand {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new BeanmakerCommand()).execute(args);
        System.exit(exitCode);
    }

}
