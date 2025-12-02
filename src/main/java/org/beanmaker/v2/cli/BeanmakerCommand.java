package org.beanmaker.v2.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;

import picocli.jansi.graalvm.AnsiConsole;

import java.util.Properties;

@Command(
        name = "beanmaker",
        versionProvider = BeanmakerCommand.ManifestVersionProvider.class,
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

    static class ManifestVersionProvider implements IVersionProvider {
        @Override
        public String[] getVersion() throws Exception {
            var properties = new Properties();
            try (var is = getClass().getResourceAsStream("/version-beanmaker-cli.properties")) {
                if (is != null) {
                    properties.load(is);
                }
            }

            String version = properties.getProperty("application.version", "Unknown");
            String date = properties.getProperty("build.date", "Unknown");

            return new String[] {
                    "BeanMaker CLI " + version + " (Built on " + date + ")"
            };
        }
    }

}
