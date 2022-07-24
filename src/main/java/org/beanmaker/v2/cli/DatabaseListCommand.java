package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

@Command(name = "list", description = "List available database configurations")
public class DatabaseListCommand implements Callable<Integer> {

    @Parameters(index = "0", defaultValue = "*", paramLabel = "<filter>", description = "Wildcard filter (optional)")
    String filter;

    @ParentCommand
    private DatabaseCommand parent;

    @Override
    public Integer call() {
        // TODO: implement full list and list filtered by wildcards here
        // TODO: do internet search for java tools and libraries to work with wildcards
        return ReturnCode.SUCCESS.code();
    }

}
