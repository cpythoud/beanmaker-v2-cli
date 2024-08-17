package org.beanmaker.v2.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import picocli.jansi.graalvm.AnsiConsole;

@Command(
        name = "beanmaker",
        version = "BeanMaker CLI 2.2-SNAPSHOT",
        mixinStandardHelpOptions = true,
        subcommands = {
                BeanCommand.class,
                DatabaseCommand.class,
                GenerateCodeCommand.class,
                InitCommand.class,
                ProjectCommand.class,
                RegenerateAllCommand.class,
                TableCommand.class,
                TableListCommand.class,
                CommandLine.HelpCommand.class
        }
)
public class BeanmakerCommand {

    public static void main(String[] args) {
        int exitCode;
        try (AnsiConsole ignored = AnsiConsole.windowsInstall()) {
            var cmd = new CommandLine(new BeanmakerCommand());
            cmd.setCaseInsensitiveEnumValuesAllowed(true);
            exitCode = cmd.execute(args);
        }
        System.exit(exitCode);
    }

}
