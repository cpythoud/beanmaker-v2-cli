package org.beanmaker.v2.cli;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "extra-field", description = "Manage extra non-database-based field")
public class BeanExtraFieldCommand implements Callable<Integer> {

    @Option(names = { "--jt", "--java-type" }, paramLabel = "<java-type>", description = "change java type")
    String javaType;

    @Option(names = { "--ic", "--initialization-code" }, paramLabel = "<java expression>", description = "specify field initialization code")
    String initializationCode;

    @ArgGroup()
    IsFinal isFinal;

    static class IsFinal {
        @Option(names = { "-f", "--final" }, description = "mark field as final")
        boolean isFinal;

        @Option(names = { "--nf", "--not-final" }, description = "mark field as not final")
        boolean notFinal;
    }

    @Option(names = { "--ci", "--clear--imports" }, description = "mark field as final")
    boolean clearImports;

    @Option(names = { "--ri", "--required-import" }, paramLabel = "<import>", description = "specify a required import")
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
