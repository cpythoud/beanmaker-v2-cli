package org.beanmaker.v2.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "bean",
        description = "Manage current table/bean configuration",
        subcommands = {
                BeanExtraFieldCommand.class,
                BeanFieldCommand.class,
                BeanRelationshipCommand.class,
                BeanShowCommand.class,
                CommandLine.HelpCommand.class
        }
)
class BeanCommand {

    @ParentCommand
    BeanmakerCommand parent;

}
