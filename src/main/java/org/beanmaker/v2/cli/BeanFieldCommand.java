package org.beanmaker.v2.cli;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "field", description = "Manage field data")
class BeanFieldCommand implements Callable<Integer> {

    @Option(names = { "--jt", "--java-type" }, paramLabel = "<java-type>", description = "change java type")
    String javaType;

    @Option(names = { "--jn", "--java-name" }, paramLabel = "<java-name>", description = "change java field name")
    String server;

    @ArgGroup()
    Required required;

    static class Required {
        @Option(names = { "-r", "--required" }, description = "mark field as required")
        boolean required;

        @Option(names = { "--nr", "--not-required" }, description = "mark field as not required")
        boolean notRequired;
    }

    @ArgGroup()
    Unique unique;

    static class Unique {
        @Option(names = { "-u", "--unique" }, description = "mark field as unique")
        boolean unique;

        @Option(names = { "--nu", "--not-unique" }, description = "mark field as not unique")
        boolean notUnique;
    }

    @Option(names = { "--abc", "--associated-bean-class" }, paramLabel = "<associated-bean-class>", description = "bean class associated to id_field")
    String associatedBeanClass;

    @Parameters(index = "0", paramLabel = "<db-name>", description = "name of field in database (can type only beginning of field name)")
    String dbName;

    @ParentCommand
    private BeanCommand parent;

    @Override
    public Integer call()  {
        // TODO: implement sub-command
        return ReturnCode.SUCCESS.code();
    }

}
