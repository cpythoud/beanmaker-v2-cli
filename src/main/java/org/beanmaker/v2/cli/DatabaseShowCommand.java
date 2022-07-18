package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

@Command(name = "show", description = "Show the details of a database configuration")
public class DatabaseShowCommand implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "<code>", description = "Code to identify the database configuration")
    String code;

    @ParentCommand
    private DatabaseCommand parent;

    @Override
    public Integer call() {
        // TODO: implement subcommand here ;-)
        return ReturnCode.SUCCESS.code();
    }

}
