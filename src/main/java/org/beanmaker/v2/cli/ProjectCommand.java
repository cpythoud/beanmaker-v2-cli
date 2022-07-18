package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "project", description = "Manage project parameters")
class ProjectCommand implements Callable<Integer> {

    @Option(names = { "-n", "--name" }, paramLabel = "<name>", description = "change project name")
    String name;

    @Option(names = "--description", paramLabel = "<description>", description = "set short description of the project")
    String description;

    @Option(names = { "--db", "--database"}, paramLabel = "<database code>", description = "specify database to connect to")
    String database;

    @Option(names = "--default-package", paramLabel = "<package name>", description = "set default package for generated beans")
    String defaultPackage;

    @Option(names = "--gen-source-dir", paramLabel = "<path to source dir>", description = "set where to save generated source files")
    String genSourceDir;


    @Override
    public Integer call() {
        // TODO: implement subcommand here ;-)
        return ReturnCode.SUCCESS.code();
    }

}
