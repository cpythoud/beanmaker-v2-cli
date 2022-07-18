package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

@Command(name = "test", description = "Test database connection")
public class DatabaseTestCommand implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "<code>", description = "Code of database configuration to test")
    String code;

    @ParentCommand
    private DatabaseCommand parent;

    @Override
    public Integer call() {
        // TODO: implement subcommand here ;-)
        return ReturnCode.SUCCESS.code();
    }

}
