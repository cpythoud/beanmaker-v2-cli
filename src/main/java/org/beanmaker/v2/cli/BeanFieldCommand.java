package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "field", description = "Manage field data")
class BeanFieldCommand implements Callable<Integer> {

    @Option(names = "--java-type", paramLabel = "<java-type>", description = "change java type")
    String javaType;

    @Option(names = "--java-name", paramLabel = "<java-name>", description = "change java field name")
    String server;

    @Option(names = "--required", description = "mark field as required")  // TODO: check --no-required in PicoCLI documentation
    boolean required;

    @Option(names = "--unique", description = "mark field as unique")  // TODO: check --no-required in PicoCLI documentation
    boolean unique;

    @Option(names = "--associated-bean-class", paramLabel = "<associated-bean-class>", description = "bean class associated to id_field")
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
