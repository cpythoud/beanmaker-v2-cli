package org.beanmaker.v2.cli;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "add-extra-field", aliases = { "aef" }, description = "Create extra non-database-based field")
class BeanAddExtraFieldCommand implements Callable<Integer> {

    @Option(names = { "--jt", "--java-type" }, paramLabel = "<java-type>", required = true, description = "change java type")
    String javaType;

    @Option(names = { "--ic", "--initialization-code" }, paramLabel = "<java expression>", description = "specify field initialization code")
    String initializationCode;

    @ArgGroup()
    BeanEditExtraFieldCommand.IsFinal isFinal;

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
        // TODO: check gen-config exists

        // TODO: check project-config exists

        // TODO: check we have a current table

        // TODO: load current table data (config. + Columns), error if not available (i.e. no previous config file create with table exists)

        // TODO: retrieve field information (either from config file or Columns)

        // TODO: process options (if changes from Column suggestion, record in config, if not don't record and remove record if one exist)
        // TODO: if required, save new configuration


        return ReturnCode.SUCCESS.code();
    }

}
