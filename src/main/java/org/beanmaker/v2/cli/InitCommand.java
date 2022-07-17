package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "init", description = "Initialize a new project")
public class InitCommand implements Runnable {

    @Option(names = { "-n", "--name" }, defaultValue =  "unnamed project", paramLabel = "<name>", description = "project name")
    String name;

    @Option(names = "--description", paramLabel = "<description>", description = "short description of the project")
    String description;

    @Option(names = { "--db", "--database"}, required = true, paramLabel = "<database code>", description = "database to connect to")
    String database;

    @Option(names = "--default-package", required = true, paramLabel = "<package name>", description = "default package for generated beans")
    String defaultPackage;

    @Option(names = "--gen-source-dir", defaultValue = "src/main/java", paramLabel = "<path to source dir>", description = "set where to save generated source files")
    String genSourceDir;


    @Override
    public void run() {
        // TODO: implement sub-command here ;-)
    }

}
