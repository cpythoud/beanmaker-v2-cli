package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

@Command(name = "remove", aliases = { "rm" }, description = "Remove database configuration(s)")
public class DatabaseRemoveCommand implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "<code or filter>", description = "code of configuration to delete or wildcard filter for multiple configurations")
    String codeOrFilter;

    @ParentCommand
    private DatabaseCommand parent;

    @Override
    public Integer call() {
        // TODO: implement subcommand here ;-)
        return ReturnCode.SUCCESS.code();
    }

}
