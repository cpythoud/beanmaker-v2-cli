package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "init", description = "Initialize a new project")
public class InitCommand implements Callable<Integer> {

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
    public Integer call() {
        // TODO: implement subcommand here ;-)
        return ReturnCode.SUCCESS.code();
    }

}
