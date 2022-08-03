package org.beanmaker.v2.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "delete-relationship", aliases = { "dr" }, description = "Delete a relationship with an other table")
class BeanDeleteRelationshipCommand implements Callable<Integer> {

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
