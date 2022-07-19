package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.concurrent.Callable;

import static org.beanmaker.v2.cli.BeanmakerCommand.PROJECT_CONFIG_FILE;

@Command(name = "init", description = "Initialize a new project")
class InitCommand implements Callable<Integer> {

    @Option(
            names = { "-n", "--name" },
            defaultValue =  "unnamed",
            paramLabel = "<name>",
            description = "project name (default: ${DEFAULT-VALUE})"
    )
    String name;

    @Option(names = "--description", paramLabel = "<description>", description = "short description of the project")
    String description;

    @Option(names = { "--db", "--database"}, required = true, paramLabel = "<database code>", description = "database to connect to")
    String database;

    @Option(names = "--default-package", required = true, paramLabel = "<package name>", description = "default package for generated beans")
    String defaultPackage;

    @Option(
            names = "--gen-source-dir",
            defaultValue = "src/main/java",
            paramLabel = "<path to source dir>",
            description = "set where to save generated source files (default: ${DEFAULT-VALUE})"
    )
    String genSourceDir;

    @Override
    public Integer call() throws IOException {
        var configFile = Path.of(PROJECT_CONFIG_FILE);
        if (Files.exists(configFile)) {
            // TODO: introduce ANSI stuff
            System.err.println(PROJECT_CONFIG_FILE + " already exists. Please use 'project' command to modify it.");
            return ReturnCode.USER_ERROR.code();
        }

        Files.writeString(configFile, createIntialConfig());

        return ReturnCode.SUCCESS.code();
    }

    private String createIntialConfig() {
        // TODO: verify parameters? i.e., database code

        var config = new StringBuilder();  // * The configuration is so simple, we don't bother with an XML library for now
        config.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<project>\n");

        addProperty(config, "name", name);
        if (description != null)
            addProperty(config, "description", description);
        addProperty(config, "database", database);
        addProperty(config, "default-package", defaultPackage);
        addProperty(config, "gen-source-dir", genSourceDir);

        config.append("</project>\n");
        return config.toString();
    }

    private void addProperty(StringBuilder config, String name, String value) {
        config.append("    <").append(name).append(">").append(value).append("</").append(name).append(">\n");
    }

}
