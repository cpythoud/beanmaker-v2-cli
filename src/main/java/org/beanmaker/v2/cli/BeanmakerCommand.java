package org.beanmaker.v2.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "beanmaker",
        version = "BeanMaker CLI 1.0",
        mixinStandardHelpOptions = true,
        subcommands = { DatabaseCommand.class, InitCommand.class, ProjectCommand.class, CommandLine.HelpCommand.class }
)
public class BeanmakerCommand {

    static final String ASSETS_CONFIG_FILE = "beanmaker-assets.xml";
    static final String PROJECT_CONFIG_FILE = "beanmaker.xml";

    public static void main(String[] args) {
        int exitCode = new CommandLine(new BeanmakerCommand()).execute(args);
        System.exit(exitCode);
    }

}
