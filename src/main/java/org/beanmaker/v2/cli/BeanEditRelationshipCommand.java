package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "edit-relationship", description = "Manage a relationship with an other table")
class BeanEditRelationshipCommand implements Callable<Integer> {

    @Option(names = { "--jt", "--java-type" }, paramLabel = "<java-type>", description = "change java type")
    String javaType;

    @Option(names = { "--at", "--associated-table" }, paramLabel = "<table>", description = "specify associated table")
    String table;

    @Option(names = { "--sif", "--sql-id-field" }, paramLabel = "<id_field>", description = "specify id field in associated table")
    String associatedBeanClass;

    @Parameters(index = "0", paramLabel = "<java-fieldname>", description = "name of java field for referencing the list")
    String javaName;

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
