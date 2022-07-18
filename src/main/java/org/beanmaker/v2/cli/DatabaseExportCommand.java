package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "export", description = "Export a database configuration")
class DatabaseExportCommand implements Callable<Integer> {

    @Option(names = "--output-file", paramLabel = "<file>", description = "file to store the exported configuration in")
    Path outputFile;

    @Parameters(index = "0", paramLabel = "<code>", description = "Code to uniquely identify the database configuration")
    String code;

    @ParentCommand
    private DatabaseCommand parent;

    @Override
    public Integer call() {
        // TODO: implement subcommand here ;-)
        return ReturnCode.SUCCESS.code();
    }

}
