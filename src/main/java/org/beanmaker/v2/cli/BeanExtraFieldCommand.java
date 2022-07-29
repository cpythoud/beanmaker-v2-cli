package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "extra-field", description = "Manage extra non-database-based field")
public class BeanExtraFieldCommand implements Callable<Integer> {

    @Option(names = "--java-type", paramLabel = "<java-type>", description = "change java type")
    String javaType;

    @Option(names = "--initialization-code", paramLabel = "<code>", description = "specify field initialization code")
    String initializationCode;

    @Option(names = "--final", description = "mark field as final")  // TODO: check --no-final in PicoCLI documentation
    boolean isFinal;

    @Option(names = "--required-import", paramLabel = "<import>", description = "specify a required import")
    String[] imports;

    @Parameters(index = "0", paramLabel = "<java-fieldname>", description = "name of extra field (can type only beginning of field name)")
    String dbName;

    @ParentCommand
    private BeanCommand parent;

    @Override
    public Integer call()  {
        // TODO: implement sub-command
        return ReturnCode.SUCCESS.code();
    }

}
