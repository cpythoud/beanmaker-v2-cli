package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "import", description = "Import a database configuration")
public class DatabaseImportCommand implements Callable<Integer> {

    @Option(names = "--input-file", paramLabel = "<file>", description = "import data from this file (stdin if not specified")
    Path inputFile;

    @ParentCommand
    private DatabaseCommand parent;

    @Override
    public Integer call() {
        // TODO: implement subcommand here ;-)
        return ReturnCode.SUCCESS.code();
    }

}
