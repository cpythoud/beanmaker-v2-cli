package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

@Command(name = "show", description = "Show all bean/table data")
public class BeanShowCommand implements Callable<Integer> {

    @ParentCommand
    private BeanCommand parent;

    @Override
    public Integer call()  {
        // TODO: implement sub-command
        return ReturnCode.SUCCESS.code();
    }

}
